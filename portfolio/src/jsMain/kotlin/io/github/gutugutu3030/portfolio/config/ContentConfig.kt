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
import io.kvision.html.image
import io.kvision.html.p
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * コンテンツ設定データ
 * @param title タイトル
 * @param subTitle サブタイトル
 * @param notice お知らせコンテンツ
 * @param contents コンテンツデータリスト
 */
@Serializable
data class ContentConfig (
    val title: String,
    val subTitle: String? = null,
    val notice: NoticeContent? = null,
    val contents: List<ContentData>
)

/**
 * お知らせコンテンツデータ
 * @param message お知らせメッセージ
 * @param buttonUrl ボタンURL
 * @param buttonName ボタン名
 */
@Serializable
data class NoticeContent(
    val message: String,
    val buttonUrl: String,
    val buttonName: String
)

/**
 * コンテンツデータのシリアライザーモジュール
 */
private val contentModule = SerializersModule {
    polymorphic(ContentData::class) {
        subclass(ColumnContent::class)
        subclass(YoutubeContent::class)
        subclass(ParagraphContent::class)
        subclass(ImageContent::class)
    }
}

/**
 * コンテンツ設定用のYAMLデコーダー
 */
private val yaml by lazy {
    Yaml(
        serializersModule = contentModule,
        configuration = YamlConfiguration(polymorphismStyle = PolymorphismStyle.Property)
    )
}

/**
 * コンテンツ設定データをロードする
 */
suspend fun loadContentConfig(path: String): ContentConfig =
    window.fetch(path).await().text().await().let {
        yaml.decodeFromString(ContentConfig.serializer(), it)
    }


/**
 * コンテンツデータの基底クラス
 */
@Serializable
sealed interface ContentData{
    /**
     * コンテンツをレンダリングする
     * @param container コンテナ
     */
    fun render(container: Container, path: String)
}


/**
 * n段組コンテンツデータ
 * @param column コンテンツデータリスト
 */
@Serializable
@SerialName("col")
data class ColumnContent(
    val column: List<ContentData>
): ContentData {
    override fun render(container: Container, path: String) {
        container.apply {
            row{
                val size = 12 / column.size
                column.map{
                    col(size){
                        it.render(this, path)
                    }
                }
            }
        }
    }
}


/**
 * YouTubeコンテンツデータ
 * @param url YouTube動画URL
 */
@Serializable
@SerialName("youtube")
data class YoutubeContent(
    val url: String,
    val title: String? = null
): ContentData{
    override fun render(container: Container, path: String) {
        container.apply {
            title?.let{
                h2(it)
            }
            div(className = "ratio ratio-16x9") {
                iframe(src= url){
                    setAttribute("allowfullscreen", "true")
                }
            }
        }
    }
}

/**
 * 段落コンテンツデータ
 * @param title タイトル
 * @param text テキストリスト
 */
@Serializable
@SerialName("p")
data class ParagraphContent(
    val title: String? = null,
    val text: List<String>
): ContentData {
    override fun render(container: Container, path: String) {
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

/**
 * 画像コンテンツデータ
 * @param src 画像URL
 * @param caption キャプション
 */
@Serializable
@SerialName("image")
data class ImageContent(
    val src: String,
    val caption: String? = null
): ContentData{
    override fun render(container: Container, path: String) {
        container.apply{
            div(className="thumbnail") {
                image("$path/$src", className="img-fluid")
                caption?.let{
                    div(className="caption") {
                        p(it)
                    }
                }
            }
        }
    }
}
