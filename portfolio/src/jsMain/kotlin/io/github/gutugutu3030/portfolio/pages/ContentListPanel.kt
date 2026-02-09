package io.github.gutugutu3030.portfolio.pages

import com.charleskorn.kaml.Yaml
import io.github.gutugutu3030.portfolio.App
import io.github.gutugutu3030.portfolio.config.ContentListConfig
import io.kvision.panel.SimplePanel
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

/**
 * コンテンツ一覧のルーティングパス
 */
const val CONTENT_LIST_PATH = "/contents"

/**
 * コンテンツ一覧データのYAMLファイル名
 */
const val CONTENT_LIST_YAML = "content_list.yaml"


/**
 * コンテンツ一覧の設定を読み込む
 */
private suspend fun loadConfig(): ContentListConfig =
    window.fetch(CONTENT_LIST_YAML).await().text().await()
        .let { Yaml.default.decodeFromString(ContentListConfig.serializer(), it) }

/**
 * Initialize content list page routing
 */
fun initContentList(app: App){
    app.apply {
        routing.kvOn("/"){
            scope.launch {
                val config = loadConfig()
                contentPanel.removeAll()
                contentPanel.add(ContentListPanel(config))
            }
        }
        routing.kvOn(CONTENT_LIST_PATH) {
            scope.launch {
                val config = loadConfig()
                contentPanel.removeAll()
                contentPanel.add(ContentListPanel(config))
            }
        }
    }
}

/**
 * Content list panel
 * @param config Content list configuration
 */
class ContentListPanel(config: ContentListConfig) : SimplePanel() {
}
