package io.github.gutugutu3030.portfolio

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.gutugutu3030.portfolio.di.AppModule
import io.github.gutugutu3030.portfolio.ui.App
import kotlinx.browser.document

/**
 * Compose for Web (Wasm) のエントリポイント。
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val httpClient = AppModule.createHttpClient()
    val repository = AppModule.createContentListRepository(httpClient)

    ComposeViewport(document.body ?: return) {
        App(repository)
    }
}
