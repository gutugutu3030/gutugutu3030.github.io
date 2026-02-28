package io.github.gutugutu3030.portfolio.components
import io.github.gutugutu3030.portfolio.pages.PROFILE_PATH
import io.kvision.core.Container
import io.kvision.dropdown.dropDown
import io.kvision.html.ButtonStyle
import io.kvision.html.Link
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.i
import io.kvision.html.link
import io.kvision.navbar.NavbarColor
import io.kvision.navbar.nav
import io.kvision.navbar.navForm
import io.kvision.navbar.navLink
import io.kvision.navbar.navbar
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager

/**
 * リンクマーク付きリンクコンポーネント
 *
 * @param url リンクURL
 * @param icon アイコンのクラス名（デフォルトはBootstrap Iconsのリンクアイコン）
 */
fun Container.linkMark(url: String, icon: String = "bi-link-45deg",   openInCurrent: Boolean = false,
    init: (Link.() -> Unit)? = null
): Container = link(label = "", url = url, target = if(!openInCurrent) "_blank" else null) {
    i(className = icon)
    init?.invoke(this)
}


/**
 * Bootstrapでグリッドシステムを使うためのrowコンポーネント
 *
 * @param content コンテンツ
 */
fun Container.row(content: Container.() -> Unit): Container {
    return div(className = "row") {
        content()
    }
}


/**
 * Bootstrapでグリッドシステムを使うためのcolコンポーネント
 *
 * @param content コンテンツ
 */
fun Container.col(size: Int, additiveClass: String = "", content: Container.() -> Unit): Container {
    return div(className = "col-sm-$size $additiveClass") {
        content()
    }
}

/**
 * Navigation bar
 */
fun Container.bar(){
    navbar(label = "gutugutu3030", nColor = NavbarColor.DARK, className = "fixed-top", link = "/" ) {
        nav{
            dropDown(
                "Contents",
                listOf("Products" to "/",
                    "Libraries" to "/library",
                    "Apps" to "/app"),
                forNavbar = true,
            )
            navLink("Profile", url= PROFILE_PATH)
            navLink(label = "備忘録",  url ="https://sites.google.com/view/gutugutu3030/home")
        }

        navForm(className = "ms-auto") {
            button(text="Theme ", style=ButtonStyle.OUTLINESECONDARY, className = "ms-2") {
                val icon : (String)->Unit = {
                    removeAll()
                    i(className = it)
                }
                icon("bi-moon-stars")
                onClick {
                    ThemeManager.theme = when(ThemeManager.theme){
                        Theme.DARK -> {
                            icon("bi-sun")
                            Theme.LIGHT
                        }
                        Theme.LIGHT -> {
                            icon("bi-moon-stars")
                            Theme.DARK
                        }
                        else -> ThemeManager.theme
                    }
                }
            }
        }
    }
}
