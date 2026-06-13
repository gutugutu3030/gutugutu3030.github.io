package io.github.gutugutu3030.portfolio.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 未実装ページのプレースホルダー画面。
 *
 * @param title 表示するページタイトル
 * @param modifier レイアウトに適用する [Modifier]
 */
@Composable
fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title は準備中です",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
