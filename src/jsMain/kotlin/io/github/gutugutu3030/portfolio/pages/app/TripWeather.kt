package io.github.gutugutu3030.portfolio.pages.app

import io.kvision.core.Container
import io.kvision.core.onEvent
import io.kvision.form.text.textInput
import io.kvision.html.Div
import io.kvision.html.InputType
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.p
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
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
                leafletMap?.flyTo(L.latLng(lat, lng), 13)
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
            leafletMap?.flyTo(L.latLng(lat, lng), 13)
            updateMessage("「${result.displayName.take(60)}」へ移動しました")
        } else {
            updateMessage("「$query」が見つかりませんでした", isError = true)
        }
    }
}

class Map(val onMapReady: (LeafletMap) -> Unit) : Div() {

    init {
        id = "trip-weather-map"
        setStyle("height", "500px")
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

