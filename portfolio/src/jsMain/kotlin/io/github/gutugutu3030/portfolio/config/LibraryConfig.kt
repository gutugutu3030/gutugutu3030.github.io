package io.github.gutugutu3030.portfolio.config

import io.github.gutugutu3030.portfolio.components.col
import io.github.gutugutu3030.portfolio.components.linkMark
import io.github.gutugutu3030.portfolio.components.row
import io.kvision.core.Container
import io.kvision.core.ListStyle
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.html.h4
import io.kvision.html.i
import io.kvision.html.li
import io.kvision.html.p
import io.kvision.html.small
import io.kvision.html.ul
import io.kvision.modal.Alert
import kotlinx.serialization.Serializable


/**
 * ライブラリの設定データ
 * @param contents 公開ライブラリのリスト
 */
@Serializable
data class LibraryConfig(
    val contents: List<LibraryContentsItem>
) {
    fun render(container: Container) {
        contents.forEach { it.render(container) }
    }
}

/**
 * ダウンロード可能なコンテンツのデータ
 * @param url ダウンロードURL
 * @param subTitle サブタイトル（任意）
 */
@Serializable
data class LibraryDownloadData(
    val url: String,
    val subTitle: String? = null,
)

/**
 * 公開ライブラリの設定データ
 * @param title タイトル
 * @param description 説明文のリスト
 * @param date 公開日
 * @param download ダウンロードデータのリスト
 * @param moreInfoUrl 詳細情報URL（任意）
 * @param isUnderConstruction 制作途中フラグ（任意）
 */
@Serializable
data class LibraryContentsItem(
    val title: String,
    val description: List<String>,
    val date: String,
    val download: List<LibraryDownloadData>,
    val moreInfoUrl: String? = null,
    val isUnderConstruction: Boolean = false,
) {

    /**
     * このライブラリの内容をHTML要素としてレンダリングする
      * @param container レンダリング先のコンテナ
     */
    fun render(container: Container) {
        container.apply{
            div(className="card mb-2"){
                div(className="card-body") {
                    h3(this@LibraryContentsItem.title, className = "card-title")
                    row {
                        col(6){
                            description.map {
                                it.parseMarkdownLinks(p())
                            }
                            if(isUnderConstruction){
                               p(className="text-warning"){
                                   i(className="bi-cone-striped")
                                   +"制作途中です"
                               }
                            }
                            moreInfoUrl?.let{
                                linkMark(url = it, icon="bi-info-circle"){
                                    +"もっと詳しく"
                                }
                            }
                        }
                        col(6){
                            div(className="alert alert-info"){
                                h4("ダウンロード")
                                ul{
                                    download.map{
                                        li{
                                            linkMark(url=it.url, icon="bi-download"){
                                                + " ${it.subTitle ?: "ダウンロード"}"
                                            }
                                        }
                                    }
                                }
                                p{
                                    small(date)
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}