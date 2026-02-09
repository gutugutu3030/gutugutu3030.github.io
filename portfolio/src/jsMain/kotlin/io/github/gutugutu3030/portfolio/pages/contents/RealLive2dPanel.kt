package io.github.gutugutu3030.portfolio.pages.contents

import io.github.gutugutu3030.portfolio.App
import io.github.gutugutu3030.portfolio.config.ContentConfig
import io.github.gutugutu3030.portfolio.config.loadContentConfig
import io.kvision.html.div
import io.kvision.html.h1
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
                +config.title
                config.subTitle?.let{
                    small(it)
                }
            }
        }
        section {
            config.notice?.let{
                div(className = "alert alert-info") {
                    p(it.message)
                    link(
                        it.buttonName, className = "btn btn-info",
                        url = it.buttonUrl, target = "_blank"
                    )
                }
            }
            config.contents.map{
                div{
                    setStyle("margin-bottom", "2em")
                    it.render(this, PATH)
                }
            }
        }
    }
}