package io.github.gutugutu3030.portfolio.data

import kotlinx.serialization.Serializable

/**
 * 制作物一覧に表示される個別のコンテンツ情報。
 *
 * @property directory コンテンツのディレクトリパス
 * @property thumbnailExp サムネイル画像の拡張子
 * @property name 表示名
 * @property date 制作日（文字列）
 * @property url 外部リンクの場合のURL。null の場合は内部詳細ページへ遷移する
 */
@Serializable
data class ContentItem(
    val directory: String,
    val thumbnailExp: String = "png",
    val name: String,
    val date: String,
    val url: String? = null
)
