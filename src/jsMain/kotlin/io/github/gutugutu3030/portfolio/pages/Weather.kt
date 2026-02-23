import io.github.gutugutu3030.portfolio.App
import io.github.gutugutu3030.portfolio.components.row
import io.kvision.form.text.textInput
import io.kvision.html.InputType
import io.kvision.html.header
import io.kvision.html.image
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableListWrapper
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.table.ResponsiveType
import io.kvision.table.cell
import io.kvision.table.headerCell
import io.kvision.table.row as tableRow
import io.kvision.table.table
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Date

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

const val PATH = "/app/weather"

fun initWeather(app: App){
    initPanel(app, PATH){
        val (lat, lon) = getCurrentPosition()
        val weatherDataText =
        window.fetch("https://www.meteosource.com/api/v1/free/point?lat=$lat&lon=$lon&sections=current%2Chourly&language=en&units=metric&key=$apiKey").await().text().await()
        val weatherData = json.decodeFromString<Weather>(weatherDataText)

        console.log(weatherData)
        WeatherPanel(weatherData.hourly.data)
    }
}

private const val apiKeyName = "meteoSourceApiKey"
private var apiKey: String?
    get() = window.localStorage.getItem(apiKeyName) ?: ""
    set(value){
        window.localStorage.setItem(apiKeyName, value?:"")
    }

class WeatherPanel(
    val data: List<HourlyWeatherData>
): SimplePanel() {
    init{
        val d = ObservableListWrapper(data.toMutableList())
        table(
           headerNames =  listOf("時間","天気", "雲", "温度","風速", "風向"),
            responsiveType = ResponsiveType.RESPONSIVE
        ){
            data.map{
                tableRow {
                    cell(it.dateTime)
                    cell{
                        image("$PATH/${it.icon}.png", alt = it.weather)
                    }
                    cell("${it.cloud_cover.total}%"){
                        val color = "rgba(0, 0, 100, ${1 - it.cloud_cover.total / 100.0})"
                        setAttribute("style", "--bs-table-bg: $color; background-color: $color !important;")
                    }
                    cell("${it.temperature}度")
                    cell("${it.wind.speed}m/s")
                    cell{
                        p("→").bind(ObservableValue(it.wind.angle) ){ angle ->
                            setAttribute("style", "transform: rotate(${angle}deg);")

                        }
                    }
                }
            }
        }
        textInput(type = InputType.PASSWORD, value = apiKey) {
            placeholder = "Enter your MeteoSource API Key"
            subscribe {
                apiKey = value
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
    val cloud_cover: CloudCover
){
    val dateTime: String
        get() = "${Date(date).getHours()}時"
}

@Serializable
data class Wind(
    val speed: Double,
    val angle: Int,
)

@Serializable
data class CloudCover(
    val total: Int
)