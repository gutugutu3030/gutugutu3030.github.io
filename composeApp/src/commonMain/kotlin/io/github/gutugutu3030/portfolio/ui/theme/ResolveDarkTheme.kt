package io.github.gutugutu3030.portfolio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

/**
 * 現在の [ThemeMode] とシステム設定から、ダークテーマを適用すべきかを解決する。
 *
 * @param themeMode 現在のテーマモード
 * @return ダークテーマを適用する場合は true
 */
@Composable
fun resolveDarkTheme(themeMode: ThemeMode): Boolean {
    return when (themeMode) {
        ThemeMode.AUTO -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
}
