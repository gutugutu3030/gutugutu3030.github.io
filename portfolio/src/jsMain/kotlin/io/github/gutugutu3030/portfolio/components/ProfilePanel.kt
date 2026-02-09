package io.github.gutugutu3030.portfolio.components

import io.github.gutugutu3030.portfolio.ProfileConfig
import io.kvision.html.h1
import io.kvision.html.h2
import io.kvision.html.h3
import io.kvision.html.image
import io.kvision.html.li
import io.kvision.html.link
import io.kvision.html.ol
import io.kvision.html.p
import io.kvision.html.div
import io.kvision.html.small
import io.kvision.html.strong
import io.kvision.html.table
import io.kvision.html.td
import io.kvision.html.tr
import io.kvision.html.ul
import io.kvision.panel.SimplePanel

/**
 * Profile panel
 * @param config Profile configuration
 */
class ProfilePanel(config: ProfileConfig) : SimplePanel(){
    init {
        div(className = "container"){
            h1("Profile")
            div("Hello world")
            row{
                col(4){
                    image("icon.svg", className="img-fluid")
                }
                col(8){
                    h2{
                        +"秋山 耀"
                        small("Yoh Akiyama")
                    }
                    ul{
                        setStyle("list-style-type", "none")
                        config.simpleExpression.map {
                            li("${it.key}：${it.value}")
                        }
                    }
                    p(config.oneSentence)
                }
            }
            div{
                h2("作ったものリスト")
                p{
                    link("こちら", url="index.html")
                    + "をご覧ください。"
                }
            }
            div{
                h2("経歴")
                table{
                    val maxDateLength = config.career.maxOf { it.date.length }
                    config.career.map {
                        tr {
                            td{ +it.date.padEnd(maxDateLength) }
                            td{
                                +it.description
                                it.url?.let{ url-> linkMark(url)}
                            }
                        }
                    }
                }
            }
            div{
                h2("資格")
                ul{
                    config.qualification.map{
                        li{
                            + it.description
                            it.url?.let{ url-> linkMark(url) }
                        }
                    }
                }
            }
            div{
                h2("受賞歴")
                ol{
                    config.award.map{
                        li{
                            + "${it.date} ${it.description}"
                            it.url?.let{ url-> linkMark(url)}
                        }
                    }
                }
            }
            div{
                h2("Publications")
                config.publication.map{ (category, publications)->
                    h3(category)
                    ol{
                        publications.map{
                            li{
                                + it.description
                                it.pdf?.let{ url-> linkMark(url, "bi-file-earmark-pdf") }
                                it.video?.let{ url-> linkMark(url, "bi-film") }
                                it.url?.let{ url-> linkMark(url ) }
                                it.slide?.let{ url -> linkMark(url, "bi-p-square") }
                                it.notice?.let{ notice -> strong("[$notice]", className="text-info") }
                            }
                        }
                    }

                }
            }
            div{
                h2("その他資料")
                ul{
                    config.hobbyDocument.map{
                        li{
                            link(it.title, url=it.path, target="_blank")
                        }
                    }
                }
            }
        }
    }
}
