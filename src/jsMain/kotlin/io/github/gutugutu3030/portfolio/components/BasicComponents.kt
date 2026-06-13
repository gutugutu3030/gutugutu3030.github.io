package io.github.gutugutu3030.portfolio.components
import io.github.gutugutu3030.portfolio.pages.PROFILE_PATH
import io.kvision.core.Container
import io.kvision.dropdown.ddLink
import io.kvision.dropdown.dropDown
import io.kvision.html.ButtonStyle
import io.kvision.html.Link
import io.kvision.html.div
import io.kvision.html.i
import io.kvision.html.link
import io.kvision.navbar.nav
import io.kvision.navbar.navForm
import io.kvision.navbar.navLink
import io.kvision.navbar.navbar
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit

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

private var themeObserverInitialized = false

private fun resolvedDark(): Boolean = when (ThemeManager.theme) {
    Theme.AUTO -> window.matchMedia("(prefers-color-scheme: dark)").matches
    Theme.DARK -> true
    Theme.LIGHT -> false
}

private fun currentIconClass(): String = when (ThemeManager.theme) {
    Theme.AUTO -> "bi-circle-half"
    Theme.LIGHT -> "bi-brightness-high-fill"
    Theme.DARK -> "bi-moon-fill"
}

private fun applyThemeToDom() {
    val dark = resolvedDark()
    val navbarEl = document.querySelector("nav.navbar.fixed-top")
    navbarEl?.let { nav ->
        if (dark) {
            nav.setAttribute("data-bs-theme", "dark")
            nav.classList.add("navbar-dark")
            nav.classList.remove("navbar-light")
        } else {
            nav.removeAttribute("data-bs-theme")
            nav.classList.remove("navbar-dark")
            nav.classList.add("navbar-light")
        }
    }
    val btnEl = document.querySelector(".theme-toggle-icon")
    btnEl?.let { btn ->
        btn.classList.remove("text-white", "text-dark")
        btn.classList.add(if (dark) "text-white" else "text-dark")
    }
    val iconEl = document.querySelector(".theme-toggle-icon > i")
    iconEl?.let { icon ->
        icon.className = "bi ${currentIconClass()} ${if (dark) "text-white" else "text-dark"}"
    }
}

/**
 * Navigation bar
 */
fun Container.bar(){
    navbar(label = "gutugutu3030", className = "fixed-top", link = "/" ) {
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
            dropDown(
                text = "",
                forNavbar = true,
                style = ButtonStyle.OUTLINESECONDARY,
                className = "ms-2 theme-dropdown",
                arrowVisible = false,
                rightAligned = true
            ) {
                button.icon = currentIconClass()
                button.addCssClass("theme-toggle-icon")
                button.addCssClass(if (resolvedDark()) "text-white" else "text-dark")
                ddLink("Auto", "javascript:void(0)") {
                    onClick { ThemeManager.theme = Theme.AUTO }
                }
                ddLink("Light", "javascript:void(0)") {
                    onClick { ThemeManager.theme = Theme.LIGHT }
                }
                ddLink("Dark", "javascript:void(0)") {
                    onClick { ThemeManager.theme = Theme.DARK }
                }
            }
        }
    }

    if (!themeObserverInitialized) {
        themeObserverInitialized = true
        window.setTimeout({ applyThemeToDom() }, 0)
        val observer = MutationObserver { _, _ -> applyThemeToDom() }
        val options = js("({attributes: true, attributeFilter: ['data-bs-theme']})").unsafeCast<MutationObserverInit>()
        observer.observe(document.documentElement!!, options)
        window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", { applyThemeToDom() })
    }
}
