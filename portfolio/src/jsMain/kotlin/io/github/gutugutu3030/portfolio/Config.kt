package io.github.gutugutu3030.portfolio

import kotlinx.serialization.Serializable

/**
 * プロフィール設定情報
 * @property simpleExpression 簡単な自己紹介表現
 * @property oneSentence 一言自己紹介
 * @property career 経歴・受賞歴情報リスト
 * @property award 受賞歴情報リスト
 * @property qualification 資格情報リスト
 * @property publication 論文情報マップ
 * @property hobbyDocument 趣味のドキュメントリスト
 */
@Serializable
data class ProfileConfig(
    val simpleExpression: Map<String, String>,
    val oneSentence: String,
    val career: List<Career>,
    val award: List<Career>,
    val qualification: List<Qualification>,
    val publication: Map<String, List<Publication>>,
    val hobbyDocument: List<Document>
)

/**
 * 経歴・受賞歴情報
 * @property date 日付
 * @property description 説明
 * @property url URLリンク
 */
@Serializable
data class Career(
    val date: String,
    val description: String,
    val url: String? = null
)

/**
 * 資格情報
 * @property description 説明
 * @property url URLリンク
 */
@Serializable
data class Qualification(
    val description: String,
    val url: String? = null
)

/**
 * 論文情報
 * @property description 説明
 * @property pdf PDFリンク
 * @property url URLリンク
 * @property video 動画リンク
 * @property notice 発表資料リンク
 * @property slide スライドリンク
 */
@Serializable
data class Publication(
    val description: String,
    val pdf: String? = null,
    val url: String? = null,
    val video: String? = null,
    val notice: String? = null,
    val slide: String? = null
)

/**
 * ドキュメント情報
 * @property title タイトル
 * @property path パス
 */
@Serializable
data class Document(
    val title: String,
    val path: String
)