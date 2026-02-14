package io.github.gutugutu3030.portfolio.config

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.github.gutugutu3030.portfolio.components.col
import io.github.gutugutu3030.portfolio.components.linkMark
import io.github.gutugutu3030.portfolio.components.row
import io.kvision.core.Container
import io.kvision.html.Tag
import io.kvision.html.div
import io.kvision.html.h2
import io.kvision.html.iframe
import io.kvision.html.image
import io.kvision.html.li
import io.kvision.html.link
import io.kvision.html.ol
import io.kvision.html.p
import io.kvision.html.strong
import io.kvision.html.table
import io.kvision.html.tbody
import io.kvision.html.td
import io.kvision.html.thead
import io.kvision.html.tr
import io.kvision.html.ul
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.min

    private val linkPattern = """\[([^\]]+)\]\(([^)]+)\)""".toRegex()
fun String.parseMarkdownLinks(tag: Tag): Tag {
    var lastIndex = 0
    linkPattern.findAll(this).forEach{ result ->
        if(result.range.first > lastIndex){
            tag.apply {
                +this@parseMarkdownLinks.substring(lastIndex, result.range.first)
            }
        }
        tag.apply{
            val text = result.groupValues[1]
            val url = result.groupValues[2]
            link(text, url = url)
        }
        lastIndex = result.range.last + 1
    }
    if(lastIndex < this.length) {
        tag.apply {
            +substring(lastIndex)
        }
    }
    return tag
}

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
 * コンテンツ設定用のYAMLデコーダー
 */
private val yaml by lazy {
    Yaml(
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
    val column: List<ContentData>,
    val width: List<Int>? = null
): ContentData {
    override fun render(container: Container, path: String) {
        val sizeList = width?.takeIf{ it.size == column.size} ?: List(column.size){ 12 / column.size }
        container.apply {
            row{
                column.mapIndexed{ index, it->
                    col(sizeList[index]){
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
                it.parseMarkdownLinks(p())
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

@Serializable
enum class DocumentListType(val createList: (Container)-> Container) {
    NUMBER({ it.ol() }),
    BULLET({ it.ul() })
}

@Serializable
@SerialName("documentList")
data class DocumentListContent(
    val title: String,
    val doc: List<DocumentListItem>,
    val listType: DocumentListType = DocumentListType.BULLET,
) : ContentData{
    override fun render(container: Container, path: String) {
        container.apply {
            h2(title)
            listType.createList(this).apply {
                doc.map{ item->
                    li{
                        +item.description
                        item.url?.let { url -> linkMark(url) }
                        item.pdf?.let { url -> linkMark(url, "bi-file-earmark-pdf") }
                        item.video?.let { url -> linkMark(url, "bi-film") }
                        item.notice?.let{
                            strong(" [$it]", className="text-info")
                        }
                    }
                }
            }
        }
    }
}

/**
 * ドキュメントコンテンツデータ
 * @param description 説明文
 * @param urlTitle URLタイトル
 * @param url URL
 * @param video 動画URL
 * @param pdf PDFURL
 */
@Serializable
data class DocumentListItem(
    val description: String,
    val url: String? = null,
    val video: String? = null,
    val pdf: String? = null,
    val notice: String? = null,
)

/**
 * 表コンテンツデータ
 * @param header タイトル行
 * @param rows 行データリスト
 */
@Serializable
@SerialName("table")
data class TableContent(
    val title: String? = null,
    val header: List<String>,
    val rows: List<List<String>>
): ContentData {
    /**
     * 有効列数
     */
    val availableColNum = min(header.size, rows.minOf{it.size})
    override fun render(container: Container, path: String) {
        container.apply{
            title?.let{ h2(it) }
            val titleData = header.take(availableColNum)
            val data = rows.map{ it.take(availableColNum) }
            table(className = "table"){
                thead{
                    tr{
                        titleData.map{ it.parseMarkdownLinks(td())}
                    }
                }
                tbody{
                    data.map{ rowData->
                        tr{
                            rowData.map{ it.parseMarkdownLinks(td()) }
                        }
                    }
                }
            }
        }
    }
}