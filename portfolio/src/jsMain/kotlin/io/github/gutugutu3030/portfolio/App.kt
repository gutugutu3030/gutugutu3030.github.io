package io.github.gutugutu3030.portfolio

import com.charleskorn.kaml.Yaml
import io.kvision.Application
import io.kvision.CoreModule
import io.kvision.BootstrapModule
import io.kvision.BootstrapCssModule
import io.kvision.DatetimeModule
import io.kvision.RichTextModule
import io.kvision.TomSelectModule
import io.kvision.BootstrapUploadModule
import io.kvision.BootstrapIconsModule
import io.kvision.Hot
import io.kvision.core.Container
import io.kvision.core.style
import io.kvision.dropdown.dropDown
import io.kvision.html.ButtonStyle
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.h2
import io.kvision.html.h3
import io.kvision.html.i
import io.kvision.html.image
import io.kvision.html.li
import io.kvision.html.link
import io.kvision.html.ol
import io.kvision.html.p
import io.kvision.html.small
import io.kvision.html.strong
import io.kvision.html.table
import io.kvision.html.td
import io.kvision.html.tr
import io.kvision.html.ul
import io.kvision.navbar.NavbarColor
import io.kvision.navbar.nav
import io.kvision.navbar.navForm
import io.kvision.navbar.navLink
import io.kvision.navbar.navbar
import io.kvision.panel.SimplePanel
import io.kvision.panel.root
import io.kvision.startApplication
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import io.kvision.theme.themeSwitcher
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

class App : Application() {
    init{
        ThemeManager.init(initialTheme = Theme.DARK)
    }

    /**
     * Load profile config from config.yaml
     */
    private suspend fun LoadConfig(): ProfileConfig =
        window.fetch("config.yaml").await().text().await()
            .let{ Yaml.default.decodeFromString(ProfileConfig.serializer(), it) }

    /**
     * Scope for coroutines
     */
    private val scope = MainScope()

    override fun start() {
        root("kvapp") {
            bar()
            div("Loading...")
        }

        scope.launch {
            val config = LoadConfig()
            root("kvapp") {
                bar()
                add(ProfilePanel(config))
            }
        }
    }
}

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

fun Container.linkMark(url: String, icon: String = "bi-link-45deg"): Container = link(label = "", url = url, target = "_blank") {
    i(className = icon)
}



fun Container.row( content: Container.() -> Unit): Container {
    return div(className = "row") {
        content()
    }
}

fun Container.col(size: Int, content: Container.() -> Unit): Container {
    return div(className = "col-sm-$size") {
        content()
    }
}

/**
 * Navigation bar
 */
fun Container.bar(){
    navbar(label = "gutugutu3030", nColor = NavbarColor.DARK) {
        nav{
            dropDown(
                "Contents",
                listOf("Products" to "#",
                    "Libraries" to "#",
                    "Apps" to "#",
                    "Photos" to "#"),
                forNavbar = true
            )
            navLink("Profile")
            navLink(label = "備忘録",  url ="https://sites.google.com/view/gutugutu3030/home")
        }
        navForm {
            themeSwitcher(style = ButtonStyle.OUTLINESECONDARY)
        }
    }
}

fun main() {
    startApplication(
        ::App,
        js("import.meta.webpackHot").unsafeCast<Hot?>(),
        BootstrapModule,
        BootstrapCssModule,
        DatetimeModule,
        RichTextModule,
        TomSelectModule,
        BootstrapUploadModule,
        BootstrapIconsModule,
        CoreModule
    )
}
