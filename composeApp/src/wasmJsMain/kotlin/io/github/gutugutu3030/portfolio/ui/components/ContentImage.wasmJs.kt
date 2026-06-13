package io.github.gutugutu3030.portfolio.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.HtmlElementView
import kotlinx.browser.document
import org.w3c.dom.HTMLImageElement

/**
 * Web (Wasm) 向けの画像表示コンポーネント。
 * HTML の `<img>` 要素を使用して画像を表示する。
 *
 * @param url 画像のURL
 * @param contentDescription 画像の説明文
 * @param modifier レイアウトに適用する [Modifier]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun ContentImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier
) {
    HtmlElementView(
        factory = {
            (document.createElement("img") as HTMLImageElement).apply {
                src = url
                alt = contentDescription ?: ""
                style.width = "100%"
                style.height = "100%"
                style.objectFit = "cover"
            }
        },
        modifier = modifier
    )
}
