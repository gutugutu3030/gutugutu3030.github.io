package io.github.gutugutu3030.portfolio.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * プラットフォーム固有の画像表示コンポーネント。
 *
 * @param url 画像のURL
 * @param contentDescription 画像の説明文
 * @param modifier レイアウトに適用する [Modifier]
 */
@Composable
expect fun ContentImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
)
