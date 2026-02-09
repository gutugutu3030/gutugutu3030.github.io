package io.github.gutugutu3030.portfolio

import com.charleskorn.kaml.Yaml
import io.github.gutugutu3030.portfolio.components.ProfilePanel
import io.github.gutugutu3030.portfolio.components.bar
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
import io.kvision.startApplication
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

class App : Application() {
    init{
        ThemeManager.init(initialTheme = Theme.DARK)
    }

    /**
     * Load profile config from config.yaml
     */
    private suspend fun LoadConfig(): ProfileConfig =
        window.fetch("config.yaml").await().text().await()
            .let{ Yaml.default.decodeFromString(ProfileConfig.serializer(), it) }

    /**
     * Scope for coroutines
     */
    private val scope = MainScope()

    override fun start() {
        root("kvapp") {
            bar()
            div("Loading...")
        }

        scope.launch {
            val config = LoadConfig()
            root("kvapp") {
                bar()
                add(ProfilePanel(config))
            }
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
