package io.github.gutugutu3030.portfolio.util

import io.github.gutugutu3030.portfolio.data.ContentItem

/**
 * このコンテンツのサムネイル画像URLを取得する。
 */
val ContentItem.thumbnailUrl: String
    get() = "$directory/thumbnail.$thumbnailExp"

/**
 * このコンテンツカードの遷移先URLを取得する。
 * [ContentItem.url] が指定されていれば外部リンク、そうでなければ内部詳細ページ用のパスを返す。
 */
val ContentItem.targetUrl: String
    get() = url ?: "/contents/${directory.removePrefix("contents/")}"

/**
 * このコンテンツが外部リンクかどうかを取得する。
 */
val ContentItem.isExternal: Boolean
    get() = url != null
