package io.github.gutugutu3030.portfolio.pages.app

import getWeather
import io.github.gutugutu3030.portfolio.pages.AppList
import io.kvision.core.Container
import io.kvision.core.onChange
import io.kvision.core.onEvent
import io.kvision.form.text.textInput
import io.kvision.html.Div
import io.kvision.html.InputType
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.h4
import io.kvision.html.image
import io.kvision.html.p
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableList
import io.kvision.state.ObservableListWrapper
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.table.cell
import io.kvision.table.table
import io.kvision.table.row as tableRow
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Leaflet CSS をインポート（webpack が処理）
@Suppress("UNUSED_VARIABLE")
private val initLeafletCss = leafletCss

// webpack バンドル時のデフォルトマーカーアイコン 404 問題を修正
// leafletCss と同様にモジュール初期化時に一度だけ実行される
@Suppress("UNUSED_VARIABLE")
private val initLeafletIcons = fixLeafletDefaultIcons()

val tripWeatherPanelCreator: suspend CoroutineScope.() -> SimplePanel = {
    TripWeatherPanel()
}

/** Nominatim 検索結果 */
@Serializable
private data class NominatimResult(
    val lat: String,
    val lon: String,
    @SerialName("display_name") val displayName: String
)

/** OSRM ジオメトリ (GeoJSON LineString) */
@Serializable
private data class OsrmGeometry(
    val coordinates: List<List<Double>> = emptyList() // 各要素は [lon, lat]
)

/** OSRM Routing API レスポンス */
@Serializable
private data class OsrmResponse(
    val code: String,
    val routes: List<OsrmRoute> = emptyList()
)

@Serializable
private data class OsrmRoute(
    val duration: Double,  // 秒
    val distance: Double,  // メートル
    val geometry: OsrmGeometry = OsrmGeometry()
)

/** 経路検索結果 */
data class RouteResult(
    val originName: String,
    val destName: String,
    val duration: Double,
    val distance: Double
)

private val jsonParser = Json { ignoreUnknownKeys = true }

private val statusText = ObservableValue<String>("")

data class Message(val text: String = "", val isError: Boolean = false) {
    fun render(container: Container) {
        container.apply {
            val cls = if (isError) "alert alert-danger py-1 px-2" else "alert alert-success py-1 px-2"
            span(text, className = cls)
        }
    }
}

class TripWeatherPanel : SimplePanel() {

    private val scope = MainScope()
    private var leafletMap: LeafletMap? = null
    private var searchMarker: Marker? = null

    /**
     * KVision の [Div] をポップアップ用に DOM マウントするためのオフスクリーン隠しコンテナ。
     * 画面外に配置されているためユーザーには見えない。
     */
    private var popupContainer: Div

    /** 検索状態メッセージ */
    private val searchMessage = ObservableValue<Message?>(null)

    /** 経路検索結果 */
    private val routeResult = ObservableValue<RouteResult?>(null)

    /** 経路検索中メッセージ */
    private val routeMessage = ObservableValue<Message?>(null)

    /** 地図上に描画中の経路ポリライン */
    private var routePolyline: Polyline? = null

    /** 経路の出発地マーカー */
    private var routeOriginMarker: Marker? = null

    /** 経路の目的地マーカー */
    private var routeDestMarker: Marker? = null

    /**
     * 検索状態メッセージを更新する関数
     * @param text メッセージテキスト
     * @param isError エラーメッセージかどうか（デフォルトはfalse）
     */
    fun updateMessage(text: String = "", isError: Boolean = false) {
        searchMessage.value = Message(text, isError)
    }

    init {
        h1("旅行天気")

        // 検索バー
        div(className = "input-group mb-2") {
            val position = ObservableValue("")
            textInput().bind(position) {
                value = it
                placeholder = "場所名 または 緯度,経度 (例: 東京, 35.68,139.76)"
                addCssClass("form-control")
                onChange {
                    position.value = value ?: ""
                    scope.launch { doSearch(position.value) }
                }
            }
            button("検索", className = "btn btn-primary") {
                onClick {
                    scope.launch { doSearch(position.value) }
                }
            }
        }

        // 検索結果メッセージ表示
        div().bind(searchMessage) {
            it?.render(this)
        }

        // ---- 経路検索セクション ----
        h4("経路検索", className = "mt-3 mb-2")
        val origin = ObservableValue("")
        textInput(className = "mb-2").bind(origin) {
            value = it
            placeholder = "出発地"
            addCssClass("form-control")
            onChange { origin.value = value ?: "" }
        }
        val destList = ObservableListWrapper<String>(mutableListOf(""))
        div().bind(destList) { list ->
            list.forEachIndexed { i, it ->
                textInput(className = "mb-2") {
                    value = it
                    placeholder = "目的地 ${i + 1}"
                    onChange {
                        if (value.isNullOrBlank()) {
                            if (i != 0) destList.removeAt(i)
                        } else {
                            if (i == list.size - 1) destList.add("")
                            destList[i] = value ?: ""
                        }
                    }
                }
            }
        }
        button("経路検索", className = "btn btn-success col-auto") {
            onClick {
                scope.launch {
                    doRouteSearch(origin.value, destList.firstOrNull { it.isNotBlank() } ?: "")
                }
            }
        }

        // 経路検索メッセージ
        div().bind(routeMessage) { it?.render(this) }

        // 経路検索結果
        div(className = "card mb-3").bind(routeResult) { result ->
            if (result != null) {
                div(className = "card-body py-2") {
                    p(className = "mb-1") {
                        span("出発地: ", className = "fw-bold")
                        span(result.originName)
                    }
                    p(className = "mb-1") {
                        span("目的地: ", className = "fw-bold")
                        span(result.destName)
                    }
                    p(className = "mb-1") {
                        span("移動時間 (車): ", className = "fw-bold")
                        span(formatDuration(result.duration))
                    }
                    p(className = "mb-0") {
                        span("距離: ", className = "fw-bold")
                        span("${(result.distance / 1000).asDynamic().toFixed(1)} km")
                    }
                }
            }
        }

        add(Map { map -> leafletMap = map })

        p().bind(statusText) {
            +it
        }

        // ポップアップ用 KVision Div のマウント先（オフスクリーン）
        popupContainer = div {
            setStyle("position", "absolute")
            setStyle("left", "-9999px")
            setStyle("visibility", "hidden")
        }
    }

    /**
     * 指定座標にポップアップ付きマーカーを表示する。
     * @param latlng マーカーの座標
     * @param popupText ポップアップに表示するテキスト（シンプルな文字列用）
     */
    private fun showMarker(latlng: LatLng, popupText: String) {
        showMarker(latlng, Div { +popupText })
    }

    /**
     * 指定座標に KVision [Div] をポップアップとして持つマーカーを表示する。
     *
     * [content] は一旦オフスクリーンコンテナへマウントされ、
     * DOM 挿入後に [org.w3c.dom.HTMLElement.cloneNode] で複製した要素を
     * Leaflet のポップアップへ渡す。
     *
     * 使用例:
     * ```kotlin
     * showMarker(latlng, Div {
     *     h4("東京")
     *     p("緯度: 35.68 / 経度: 139.77")
     * })
     * ```
     *
     * @param latlng マーカーの座標
     * @param content ポップアップに表示する KVision [Div]
     */
    fun showMarker(latlng: LatLng, content: Div) {
        val map = leafletMap ?: return
        searchMarker?.let { map.removeLayer(it) }

        // 前回のポップアップ用 Div をオフスクリーンコンテナから除去
        popupContainer.removeAll()

        popupContainer.add(content)
        content.addAfterInsertHook {
            val el = content.getElement() ?: return@addAfterInsertHook
            // KVision の仮想 DOM ツリーと競合しないよう clone してから Leaflet へ渡す
            val clone = el.cloneNode(true) as org.w3c.dom.HTMLElement
            searchMarker = L.marker(latlng)
                .bindPopup(clone)
                .addTo(map)
                .openPopup()
        }
    }

    private suspend fun doSearch(query: String) {
        if (query.isBlank()) {
            updateMessage("検索バーが空です", isError = true)
            return
        }
        updateMessage("検索中...")

        // 緯度,経度 形式かチェック
        val coordRegex = Regex("""^\s*(-?\d+\.?\d*)\s*,\s*(-?\d+\.?\d*)\s*$""")
        val coordMatch = coordRegex.find(query)
        if (coordMatch != null) {
            val lat = coordMatch.groupValues[1].toDoubleOrNull()
            val lng = coordMatch.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) {
                setMapTarget(lat, lng)
                updateMessage("座標 ($lat, $lng) へ移動しました")
                return
            }
        }

        // Nominatim で場所名を検索
        val url = "https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&limit=1"
        val response = window.fetch(url).await()
        val text = response.text().await()
        val results = jsonParser.decodeFromString<List<NominatimResult>>(text)
        if (results.isNotEmpty()) {
            val result = results.first()
            val lat = result.lat.toDouble()
            val lng = result.lon.toDouble()
            setMapTarget(lat, lng)
            updateMessage("「${result.displayName.take(60)}」へ移動しました")
        } else {
            updateMessage("「$query」が見つかりませんでした", isError = true)
        }
    }

    /**
     * 場所名 (または 緯度,経度 文字列) をジオコードして座標を返す。
     * 失敗時は null。
     */
    private suspend fun geocode(query: String): Pair<Pair<Double, Double>, String>? {
        val coordRegex = Regex("""^\s*(-?\d+\.?\d*)\s*,\s*(-?\d+\.?\d*)\s*$""")
        val coordMatch = coordRegex.find(query)
        if (coordMatch != null) {
            val lat = coordMatch.groupValues[1].toDoubleOrNull()
            val lng = coordMatch.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng) to "($lat, $lng)"
        }
        val url = "https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&limit=1"
        val response = window.fetch(url).await()
        val text = response.text().await()
        val results = jsonParser.decodeFromString<List<NominatimResult>>(text)
        if (results.isEmpty()) return null
        val r = results.first()
        return Pair(r.lat.toDouble(), r.lon.toDouble()) to r.displayName.take(40)
    }

    /**
     * 出発地・目的地を OSRM でルート検索し移動時間・距離を表示する。
     */
    private suspend fun doRouteSearch(originQuery: String, destQuery: String) {
        if (originQuery.isBlank() || destQuery.isBlank()) {
            routeMessage.value = Message("出発地と目的地を両方入力してください", isError = true)
            return
        }
        routeMessage.value = Message("検索中...")
        routeResult.value = null

        val originGeo = geocode(originQuery)
        if (originGeo == null) {
            routeMessage.value = Message("出発地「$originQuery」が見つかりませんでした", isError = true)
            return
        }
        val destGeo = geocode(destQuery)
        if (destGeo == null) {
            routeMessage.value = Message("目的地「$destQuery」が見つかりませんでした", isError = true)
            return
        }

        val (originCoord, originName) = originGeo
        val (destCoord, destName) = destGeo

        val (oLat, oLng) = originCoord
        val (dLat, dLng) = destCoord

        val url =
            "https://router.project-osrm.org/route/v1/driving/$oLng,$oLat;$dLng,$dLat?overview=full&geometries=geojson"
        try {
            val response = window.fetch(url).await()
            val text = response.text().await()
            val osrm = jsonParser.decodeFromString<OsrmResponse>(text)
            if (osrm.code != "Ok" || osrm.routes.isEmpty()) {
                routeMessage.value = Message("ルートが見つかりませんでした (${osrm.code})", isError = true)
                return
            }
            val route = osrm.routes.first()
            routeResult.value = RouteResult(
                originName = originName,
                destName = destName,
                duration = route.duration,
                distance = route.distance
            )
            routeMessage.value = Message("")

            // 経路を地図に描画
            val map = leafletMap ?: return

            // 古い経路レイヤーを削除
            routePolyline?.let { map.removeLayer(it) }
            routeOriginMarker?.let { map.removeLayer(it) }
            routeDestMarker?.let { map.removeLayer(it) }

            // GeoJSON の座標は [lon, lat] 順なので入れ替える
            val latlngs = route.geometry.coordinates
                .map { L.latLng(it[1], it[0]) }
                .toTypedArray()

            // ポリライン描画
            val opts = js("({})")
            opts["color"] = "#0d6efd"
            opts["weight"] = 4
            opts["opacity"] = 0.85
            val polyline = L.polyline(latlngs, opts).addTo(map)
            routePolyline = polyline

            // 出発地・目的地マーカー
            routeOriginMarker = L.marker(L.latLng(oLat, oLng))
                .bindPopup("出発地: $originName")
                .addTo(map)
            routeDestMarker = L.marker(L.latLng(dLat, dLng))
                .bindPopup("目的地: $destName")
                .addTo(map)
                .openPopup()

            // 経路全体が見えるように地図をフィット
            map.fitBounds(polyline.getBounds())
        } catch (e: Exception) {
            routeMessage.value = Message("通信エラー: ${e.message}", isError = true)
        }
    }

    /** 秒数を「X時間Y分」形式の文字列に変換する */
    private fun formatDuration(seconds: Double): String {
        val totalMin = (seconds / 60).toInt()
        val hours = totalMin / 60
        val mins = totalMin % 60
        return if (hours > 0) "${hours}時間${mins}分" else "${mins}分"
    }

    private suspend fun setMapTarget(lat: Double, lng: Double) {
        val latlng = L.latLng(lat, lng)

        getWeather(lat, lng)?.hourly?.data?.let {
            console.log(it)
            showMarker(latlng, Div {
                div {
                    setStyle("max-height", "100px")
                    setStyle("overflow-y", "auto")

                    table(headerNames = listOf("時間", "天気", "温度")) {
                        it.map {
                            tableRow {
                                cell(it.dateTime)
                                cell {
                                    image("${AppList.WEATHER.path}/${it.icon}.png", alt = it.weather)
                                }
                                cell("${it.temperature}度")
                            }
                        }
                    }
                }
            })

            leafletMap?.flyTo(latlng, 13)
        } ?: run {
            console.warn("天気情報の取得に失敗")
            leafletMap?.flyTo(latlng, 13)
        }
    }
}

class Map(val onMapReady: (LeafletMap) -> Unit) : Div() {

    init {
        id = "trip-weather-map"
        setStyle("height", "${window.innerHeight / 2}px")
        setStyle("width", "100%")

        addAfterInsertHook {
            val element = getElement() ?: return@addAfterInsertHook

            val map = L.map(element)
                .setView(L.latLng(35.6812, 139.7671), 10) // 初期表示: 東京

            L.tileLayer(
                "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
                TileLayerOptions(maxZoom = 19)
            ).let { map.addLayer(it) }

            onMapReady(map)

            // コンポーネント破棄時に Leaflet マップも解放
            addBeforeDisposeHook {
                map.remove()
            }
        }
    }
}

