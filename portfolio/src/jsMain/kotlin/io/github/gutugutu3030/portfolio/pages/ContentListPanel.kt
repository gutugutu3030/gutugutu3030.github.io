package io.github.gutugutu3030.portfolio.pages

import com.charleskorn.kaml.Yaml
import io.github.gutugutu3030.portfolio.App
import io.github.gutugutu3030.portfolio.components.col
import io.github.gutugutu3030.portfolio.components.row
import io.github.gutugutu3030.portfolio.config.ContentItem
import io.github.gutugutu3030.portfolio.config.ContentListConfig
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h5
import io.kvision.html.image
import io.kvision.html.link
import io.kvision.html.p
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
suspend fun loadContentListConfig(): ContentListConfig =
    window.fetch(CONTENT_LIST_YAML).await().text().await()
        .let { Yaml.default.decodeFromString(ContentListConfig.serializer(), it) }

/**
 * Initialize content list page routing
 */
fun initContentList(app: App){
    app.apply {
        routing.kvOn("/"){
            scope.launch {
                val config = loadContentListConfig()
                contentPanel.removeAll()
                contentPanel.add(ContentListPanel(config))
            }
        }
        routing.kvOn(CONTENT_LIST_PATH) {
            scope.launch {
                val config = loadContentListConfig()
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
    init{
        div{
            p{
                +"制作物一覧です。動画一覧は"
                link("こちら", url = "https://www.youtube.com/playlist?list=PLQyFKRbKsHDv9lAgn7kHZCq7V1gATlHlU", target = "_Blank")
                +"。制作したライブラリ一覧は"
                link("こちら", url = "#/library")
                +"。"
            }
            row() {
                config.contents.reversed().map{
                    col(4, additiveClass = "g-2"){ contentCard(it) }
                }
            }
        }
    }
}

/**
 * コンテンツのカードを作成する
 * @param item コンテンツアイテム
 */
fun Container.contentCard(item: ContentItem): Container = div(className="card") {
    item.apply{
        val targetUrl = url ?: "#/$directory"
        link(label = "", url = targetUrl){
            image("$directory/thumbnail.$thumbnailExp", className="card-img-top")
        }
        div(className="card-body"){
            h5(name, className="card-title")
            p(date, className="card-text")
            link(
                "詳細",
                className = "btn btn-outline-secondary",
                url = targetUrl,
                target = if(url != null) "_blank" else "_self"
            )
        }
    }
}



