import io.github.gutugutu3030.portfolio.pages.AppList
import io.kvision.form.text.textInput
import io.kvision.html.InputType
import io.kvision.html.image
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.table.ResponsiveType
import io.kvision.table.cell
import io.kvision.table.row as tableRow
import io.kvision.table.table
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Date
import kotlin.math.max


suspend fun getCurrentPosition(): Pair<Double, Double> = suspendCancellableCoroutine { cont ->
    val geolocation = js("window.navigator.geolocation")
    geolocation.getCurrentPosition(
        { position: dynamic ->
            val lat = position.coords.latitude as Double
            val lon = position.coords.longitude as Double
            cont.resume(Pair(lat, lon))
        },
        { error: dynamic ->
            cont.resumeWithException(Exception(error.message as? String ?: "Geolocation error"))
        }
    )
}

val json = Json{
    ignoreUnknownKeys = true
}

/**
 * 指定した緯度経度の天気情報を取得する関数
 * @param lat 緯度 * @param lon 経度
 * @return 天気情報（取得に失敗した場合は null）
 */
suspend fun getWeather(lat: Double, lon: Double): Weather? = runCatching {
        window.fetch("https://www.meteosource.com/api/v1/free/point?lat=$lat&lon=$lon&sections=current%2Chourly&language=en&units=metric&key=$weatherApiKey")
            .await().text().await().let{
                json.decodeFromString<Weather>(it)
            }
}.getOrNull()



val weatherPanelCreator : suspend CoroutineScope.() -> SimplePanel= {
    getCurrentPosition().let {
        getWeather(it.first, it.second)
    }
             .let{ WeatherPanel(it?.hourly?.data) }
}

private const val apiKeyName = "meteoSourceApiKey"
var weatherApiKey: String?
    get() = window.localStorage.getItem(apiKeyName) ?: ""
    set(value){
        window.localStorage.setItem(apiKeyName, value?:"")
    }

class WeatherPanel(
    val data: List<HourlyWeatherData>?
): SimplePanel() {
    init{
        if(data != null){
            table(
                headerNames =  listOf("時間","天気", "雲", "温度","降水量","風速", "風向"),
                responsiveType = ResponsiveType.RESPONSIVE
            ){
                data.map{
                    tableRow {
                        cell(it.dateTime)
                        cell{
                            image("${AppList.WEATHER.path}/${it.icon}.png", alt = it.weather)
                        }
                        cell("${it.cloud_cover.total}%"){
                            val color = "rgba(0, 0, 100, ${1 - max(0.0, it.cloud_cover.total / 60.0)})"
                            setAttribute("style", "--bs-table-bg: $color; background-color: $color !important;")
                        }
                        cell("${it.temperature}度")
                        cell("${it.precipitation.total}mm"){
                            if(it.precipitation.total != 0.0){
                                val color = "rgba(100, 0, 0)"
                                setAttribute("style", "--bs-table-bg: $color; background-color: $color !important;")
                            }
                        }
                        cell("${it.wind.speed}m/s")
                        cell{
                            p("→").bind(ObservableValue(it.wind.angle) ){ angle ->
                                setAttribute("style", "transform: rotate(${angle}deg);")

                            }
                        }
                    }
                }
            }
        }
        textInput(type = InputType.PASSWORD, value = weatherApiKey) {
            placeholder = "Enter your MeteoSource API Key"
            subscribe {
                weatherApiKey = value
            }
        }
    }
}

@Serializable
data class Weather(
    val lat: String,
    val lon: String,
    val hourly: HourlyWeather,
)

@Serializable
data class HourlyWeather(
    val data: List<HourlyWeatherData>
)


@Serializable
data class HourlyWeatherData(
    val date: String,
    val weather: String,
    val icon: Int,
    val temperature: Double,
    val wind: Wind,
    val cloud_cover: CloudCover,
    val precipitation: Precipitation
){
    val dateTime: String
        get() = "${Date(date).getHours()}時"
}

@Serializable
data class Wind(
    val speed: Double,
    val angle: Double,
)

@Serializable
data class CloudCover(
    val total: Double
)

@Serializable
data class Precipitation(
    val total: Double,
    val type: String?
)