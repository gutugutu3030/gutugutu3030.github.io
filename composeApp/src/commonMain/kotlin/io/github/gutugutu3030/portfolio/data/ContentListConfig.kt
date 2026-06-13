package io.github.gutugutu3030.portfolio.data

import kotlinx.serialization.Serializable

/**
 * 制作物一覧の全体設定。
 *
 * @property contents 表示対象のコンテンツリスト
 */
@Serializable
data class ContentListConfig(
    val contents: List<ContentItem>
)
