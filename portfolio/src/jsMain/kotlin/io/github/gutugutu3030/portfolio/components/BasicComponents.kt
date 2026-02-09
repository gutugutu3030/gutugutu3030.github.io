package io.github.gutugutu3030.portfolio.components
import io.github.gutugutu3030.portfolio.pages.PROFILE_PATH
import io.kvision.core.Container
import io.kvision.dropdown.dropDown
import io.kvision.html.ButtonStyle
import io.kvision.html.div
import io.kvision.html.i
import io.kvision.html.link
import io.kvision.navbar.NavbarColor
import io.kvision.navbar.nav
import io.kvision.navbar.navForm
import io.kvision.navbar.navLink
import io.kvision.navbar.navbar
import io.kvision.theme.themeSwitcher

/**
 * リンクマーク付きリンクコンポーネント
 *
 * @param url リンクURL
 * @param icon アイコンのクラス名（デフォルトはBootstrap Iconsのリンクアイコン）
 */
fun Container.linkMark(url: String, icon: String = "bi-link-45deg"): Container = link(label = "", url = url, target = "_blank") {
    i(className = icon)
}


/**
 * Bootstrapでグリッドシステムを使うためのrowコンポーネント
 *
 * @param content コンテンツ
 */
fun Container.row( content: Container.() -> Unit): Container {
    return div(className = "row") {
        content()
    }
}


/**
 * Bootstrapでグリッドシステムを使うためのcolコンポーネント
 *
 * @param content コンテンツ
 */
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
                listOf("Products" to "#/index",
                    "Libraries" to "#",
                    "Apps" to "#",
                    "Photos" to "#"),
                forNavbar = true,
            )
            navLink("Profile", url= PROFILE_PATH, dataNavigo = true)
            navLink(label = "備忘録",  url ="https://sites.google.com/view/gutugutu3030/home")
        }
        navForm {
            themeSwitcher(style = ButtonStyle.OUTLINESECONDARY)
        }
    }
}
