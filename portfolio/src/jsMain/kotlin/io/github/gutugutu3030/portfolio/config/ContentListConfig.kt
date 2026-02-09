package io.github.gutugutu3030.portfolio.config

import kotlinx.serialization.Serializable

/**
 * Content list configuration data class
 *
 * @param list List of content items
 */
@Serializable
data class ContentListConfig(
    val contents: List<ContentItem>
)

/**
 * Content item data class
 *
 * @param directory Directory name
 * @param name Display name
 * @param date Date string
 * @param url Optional URL
 */
@Serializable
data class ContentItem(
    val directory: String,
    val thumbnailExp: String = "png",
    val name: String,
    val date: String,
    val url: String? = null
)
