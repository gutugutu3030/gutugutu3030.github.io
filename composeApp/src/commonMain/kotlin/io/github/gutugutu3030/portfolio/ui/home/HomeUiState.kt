package io.github.gutugutu3030.portfolio.ui.home

import io.github.gutugutu3030.portfolio.data.ContentListConfig

/**
 * 制作物一覧画面の表示状態。
 */
sealed interface HomeUiState {

    /**
     * 読み込み中。
     */
    data object Loading : HomeUiState

    /**
     * 取得成功。
     *
     * @property config 表示対象の [ContentListConfig]
     */
    data class Success(val config: ContentListConfig) : HomeUiState

    /**
     * 取得失敗。
     *
     * @property message エラーメッセージ
     */
    data class Error(val message: String) : HomeUiState
}
