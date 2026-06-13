package io.github.gutugutu3030.portfolio.util

import kotlinx.browser.window

/**
 * ブラウザの [window.open] を使用して、指定されたURLを新しいタブで開く。
 *
 * @param url 開くURL
 */
actual fun openExternalUrl(url: String) {
    window.open(url, "_blank")
}
