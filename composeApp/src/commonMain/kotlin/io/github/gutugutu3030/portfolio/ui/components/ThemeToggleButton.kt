package io.github.gutugutu3030.portfolio.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.gutugutu3030.portfolio.ui.theme.ThemeMode

/**
 * テーマモードを切り替えるボタン。
 *
 * @param themeMode 現在のテーマモード
 * @param onToggle タップ時のコールバック
 * @param modifier レイアウトに適用する [Modifier]
 */
@Composable
fun ThemeToggleButton(
    themeMode: ThemeMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = themeMode.icon(),
            contentDescription = "テーマ切り替え: ${themeMode.name}"
        )
    }
}

/**
 * テーマモードに対応するアイコンを取得する。
 *
 * @return テーマモードを表す [androidx.compose.ui.graphics.vector.ImageVector]
 */
@Composable
private fun ThemeMode.icon() = when (this) {
    ThemeMode.AUTO -> Icons.Default.SettingsSuggest
    ThemeMode.LIGHT -> Icons.Default.LightMode
    ThemeMode.DARK -> Icons.Default.DarkMode
}
