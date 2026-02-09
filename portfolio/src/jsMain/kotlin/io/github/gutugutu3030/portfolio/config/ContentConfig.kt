package io.github.gutugutu3030.portfolio.config

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.github.gutugutu3030.portfolio.components.col
import io.github.gutugutu3030.portfolio.components.row
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h2
import io.kvision.html.iframe
import io.kvision.html.p
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
data class ContentConfig (
    val title: String,
    val subTitle: String? = null,
    val notice: NoticeContent? = null,
    val contents: List<ContentData>
)

@Serializable
data class NoticeContent(
    val message: String,
    val buttonUrl: String,
    val buttonName: String
)

private val contentModule = SerializersModule {
    polymorphic(ContentData::class) {
        subclass(ColumnContent::class)
        subclass(YoutubeContent::class)
        subclass(ParagraphContent::class)
    }
}

private val yaml by lazy {
    Yaml(
        serializersModule = contentModule,
        configuration = YamlConfiguration(polymorphismStyle = PolymorphismStyle.Property)
    )
}

suspend fun loadContentConfig(path: String): ContentConfig =
    window.fetch(path).await().text().await().let {
        yaml.decodeFromString(ContentConfig.serializer(), it)
    }


@Serializable
sealed interface ContentData{
    fun render(container: Container)
}


/**
 * コンテンツアイテムデータ
 */
@Serializable
@SerialName("col")
data class ColumnContent(
    val column: List<ContentData>
): ContentData {
    override fun render(container: Container) {
        container.apply {
            row{
                val size = 12 / column.size
                column.map{
                    col(size){
                        it.render(this)
                    }
                }
            }
        }
    }
}


/**
 * YouTubeコンテンツデータ
 */
@Serializable
@SerialName("youtube")
data class YoutubeContent(
    val url: String
): ContentData{
    override fun render(container: Container) {
        container.apply {
            div(className = "ratio ratio-16x9") {
                iframe(src= url){
                    setAttribute("allowfullscreen", "true")
                }
            }
        }
    }
}

@Serializable
@SerialName("p")
data class ParagraphContent(
    val title: String? = null,
    val text: List<String>
): ContentData {
    override fun render(container: Container) {
        container.apply {
            title?.let{
                h2(it)
            }
            text.map{
                p(it)
            }
        }
    }
}