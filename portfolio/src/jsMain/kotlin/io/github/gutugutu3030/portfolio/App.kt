package io.github.gutugutu3030.portfolio

import io.github.gutugutu3030.portfolio.components.bar
import io.github.gutugutu3030.portfolio.config.loadContentConfig
import io.github.gutugutu3030.portfolio.pages.contents.ContentPanel
import io.github.gutugutu3030.portfolio.pages.initContentList
import io.github.gutugutu3030.portfolio.pages.initProfile
import io.github.gutugutu3030.portfolio.pages.loadContentListConfig
import io.kvision.Application
import io.kvision.CoreModule
import io.kvision.BootstrapModule
import io.kvision.BootstrapCssModule
import io.kvision.DatetimeModule
import io.kvision.RichTextModule
import io.kvision.TomSelectModule
import io.kvision.BootstrapUploadModule
import io.kvision.BootstrapIconsModule
import io.kvision.Hot
import io.kvision.html.div
import io.kvision.panel.root
import io.kvision.panel.SimplePanel
import io.kvision.routing.Routing
import io.kvision.startApplication
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class App : Application() {
    init {
        ThemeManager.init(initialTheme = Theme.DARK)
    }

    /**
     * Scope for coroutines
     */
    val scope = MainScope()

    /**
     * Content panel for routing
     */
    lateinit var contentPanel: SimplePanel

    lateinit var routing: Routing

    override fun start() {
        root("kvapp") {
            setStyle("margin-top", "70px")
            bar()
            contentPanel = div(className="container") {}
            contentPanel.div("Loading...")
        }
        scope.launch {
           val config = loadContentListConfig()
            routing = Routing.init()
            initProfile(this@App)
            initContentList(this@App)

                config.contents
//                    .filter{ it.url == null }
                    .forEach {
                    it.apply {
                        routing.kvOn("$directory"){
                            console.log("call route /$directory")
                            scope.launch{
                                loadContentConfig("$directory/data.yaml").let{
                                    contentPanel.removeAll()
                                    contentPanel.add(ContentPanel(it, directory))
                                }
                            }
                        }
                    }
                }
            routing.kvOn(".*") {
                contentPanel.removeAll()
                contentPanel.div("not found")
            }

            routing.kvResolve()
        }
    }
}

fun main() {
    startApplication(
        ::App,
        js("import.meta.webpackHot").unsafeCast<Hot?>(),
        BootstrapModule,
        BootstrapCssModule,
        DatetimeModule,
        RichTextModule,
        TomSelectModule,
        BootstrapUploadModule,
        BootstrapIconsModule,
        CoreModule
    )
}
