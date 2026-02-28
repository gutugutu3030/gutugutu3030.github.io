package io.github.gutugutu3030.portfolio.pages.app

import CloudCover
import HourlyWeatherData
import Precipitation
import Wind
import getWeather
import io.github.gutugutu3030.portfolio.pages.AppList
import io.kvision.core.Container
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

private val jsonParser = Json { ignoreUnknownKeys = true }

private val statusText = ObservableValue<String>("")

data class Message(val text: String = "", val isError: Boolean = false){
    fun render(container: Container){
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
    private lateinit var popupContainer: Div

    /** 検索状態メッセージ */
    private val searchMessage = ObservableValue<Message?>(null)

    /**
     * 検索状態メッセージを更新する関数
     * @param text メッセージテキスト
     * @param isError エラーメッセージかどうか（デフォルトはfalse）
      */
    fun updateMessage(text: String = "", isError: Boolean = false){
        searchMessage.value = Message(text, isError)
    }

    init {
        h1("旅行天気")

        // 検索バー
        div(className = "input-group mb-2") {
            val input = textInput(InputType.TEXT) {
                placeholder = "場所名 または 緯度,経度 (例: 東京, 35.68,139.76)"
                addCssClass("form-control")

                // Enter キーで検索
                onEvent {
                    keydown = { e ->
                        if (e.asDynamic().key == "Enter") {
                            scope.launch { doSearch(value ?: "") }
                        }
                    }
                }
            }
            button("検索", className = "btn btn-primary") {
                onClick {
                    scope.launch { doSearch(input.value ?: "") }
                }
            }
        }

        // 検索結果メッセージ表示
        div().bind(searchMessage) {
            it?.render(this)
        }

        add(Map { map -> leafletMap = map })

        p().bind(statusText){
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
        if (query.isBlank()){
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

    private suspend fun setMapTarget(lat: Double, lng: Double){
        val latlng = L.latLng(lat, lng)

        getWeather(lat, lng)?.hourly?.data?.let{
            console.log(it)
            showMarker(latlng, Div{
                div{
                    setStyle("max-height", "100px")
                    setStyle("overflow-y", "auto")

                    h4("天気")
                    table(headerNames = listOf("時間","天気", "温度")){
                        it.map{
                            tableRow {
                                cell(it.dateTime)
                                cell{
                                    image("${AppList.WEATHER.path}/${it.icon}.png", alt = it.weather)
                                }
                                cell("${it.temperature}度")
                            }
                        }
                    }
                }
            })

            leafletMap?.flyTo(latlng, 13)
        } ?: run{
            console.warn("天気情報の取得に失敗")
            leafletMap?.flyTo(latlng, 13)
        }
    }
}

class Map(val onMapReady: (LeafletMap) -> Unit) : Div() {

    init {
        id = "trip-weather-map"
        setStyle("height", "${window.innerHeight /2}px")
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

