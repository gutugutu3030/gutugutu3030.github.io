package io.github.gutugutu3030.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import io.github.gutugutu3030.portfolio.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * アプリケーションのテーマ設定を保持する [ViewModel]。
 */
class ThemeViewModel : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.AUTO)

    /**
     * 現在のテーマモード。
     */
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    /**
     * テーマモードを次の状態に切り替える。
     * 切り替え順は AUTO → LIGHT → DARK → AUTO。
     */
    fun toggleThemeMode() {
        _themeMode.update { current ->
            when (current) {
                ThemeMode.AUTO -> ThemeMode.LIGHT
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.AUTO
            }
        }
    }

    /**
     * 指定した [ThemeMode] にテーマを設定する。
     *
     * @param mode 設定するテーマモード
     */
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}
