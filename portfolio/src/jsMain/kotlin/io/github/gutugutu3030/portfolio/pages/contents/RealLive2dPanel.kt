package io.github.gutugutu3030.portfolio.pages.contents

import dev.ktml.Content
import io.github.gutugutu3030.portfolio.App
import io.github.gutugutu3030.portfolio.components.col
import io.github.gutugutu3030.portfolio.components.row
import io.github.gutugutu3030.portfolio.config.ContentConfig
import io.github.gutugutu3030.portfolio.config.loadContentConfig
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.h2
import io.kvision.html.iframe
import io.kvision.html.link
import io.kvision.html.p
import io.kvision.html.section
import io.kvision.html.small
import io.kvision.panel.SimplePanel
import kotlinx.coroutines.launch

private const val PATH = "/contents/reallive2d"

/**
 * Initialize content list page routing
 */
fun initRealLive2d(app: App) {
    app.apply {
        routing.kvOn(PATH) {
            scope.launch{
                val config = loadContentConfig("$PATH/data.yaml")
                contentPanel.removeAll()
                contentPanel.add(RealLive2dPanel(config))
            }
        }
    }
}

class RealLive2dPanel(config: ContentConfig) : SimplePanel() {
    init {
        section {
            h1 {
                +"MultiplaneGirl"
                small("絵に命を吹き込む手法")
            }
        }

        section {
            div(className = "alert alert-info") {
                p("組み立てに使用した3Dモデルデータ、プログラム、設計用システムがダウンロードできます")
                link(
                    "ダウンロード", className = "btn btn-info",
                    url = "https://github.com/gutugutu3030/realLive2d", target = "_blank"
                )
            }

            config.contents.map{
                it.render(this)
            }
        }
    }
}