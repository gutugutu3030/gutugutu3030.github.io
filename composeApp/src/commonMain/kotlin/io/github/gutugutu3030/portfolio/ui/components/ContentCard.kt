package io.github.gutugutu3030.portfolio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.gutugutu3030.portfolio.data.ContentItem
import io.github.gutugutu3030.portfolio.util.isExternal
import io.github.gutugutu3030.portfolio.util.openExternalUrl
import io.github.gutugutu3030.portfolio.util.targetUrl
import io.github.gutugutu3030.portfolio.util.thumbnailUrl

/**
 * 制作物一覧に表示するカードコンポーネント。
 *
 * @param item 表示するコンテンツ情報
 * @param onInternalClick 内部詳細ページへの遷移要求時のコールバック
 * @param modifier レイアウトに適用する [Modifier]
 */
@Composable
fun ContentCard(
    item: ContentItem,
    onInternalClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (item.isExternal) {
                    item.url?.let { openExternalUrl(it) }
                } else {
                    onInternalClick(item.targetUrl)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            ContentImage(
                url = item.thumbnailUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
