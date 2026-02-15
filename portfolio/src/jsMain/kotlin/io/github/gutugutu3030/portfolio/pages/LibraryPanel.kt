package io.github.gutugutu3030.portfolio.pages

import com.charleskorn.kaml.Yaml
import io.github.gutugutu3030.portfolio.App
import io.github.gutugutu3030.portfolio.components.config.LibraryConfig
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

/**
 * ライブラリのルーティングパス
 */
const val LIBRARY_PATH = "/library"

/**
 * ライブラリデータのYAMLファイル名
 */
const val LIBRARY_YAML = "library.yaml"

private suspend fun loadProfileConfig(): LibraryConfig =
    window.fetch(LIBRARY_YAML).await().text().await()
        .let { Yaml.default.decodeFromString(LibraryConfig.serializer(), it) }

fun initLibrary(app: App){
    app.apply{
        routing.kvOn(LIBRARY_PATH){
            scope.launch {
                val config = loadProfileConfig()
                contentPanel.removeAll()
                contentPanel.add(LibraryPanel(config))
            }
        }
    }
}

class LibraryPanel(val config: LibraryConfig): SimplePanel() {
    init{
        div{
            h1("ライブラリ")
            p("作成したライブラリやダウンロード可能コンテンツを公開しております．")
            p("バグなどを発見された場合は連絡をお願いします．")
        }
        config.render(this)
    }
}