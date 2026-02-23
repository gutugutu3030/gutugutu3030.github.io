import com.charleskorn.kaml.Yaml
import io.github.gutugutu3030.portfolio.App
import io.kvision.panel.SimplePanel
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

import kotlinx.serialization.serializer

suspend inline fun <reified T> loadYaml(url: String): T =
    window.fetch(url).await().text().await()
        .let { Yaml.default.decodeFromString(serializer(), it) }

fun initPanel(app: App, path: String, panelCreater: suspend CoroutineScope.() -> SimplePanel){
    app.apply{
        routing.kvOn(path){
            scope.launch {
                val panel = panelCreater()
                contentPanel.removeAll()
                contentPanel.add(panel)
            }
        }
    }
}