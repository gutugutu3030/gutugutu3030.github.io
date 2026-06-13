package io.github.gutugutu3030.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.gutugutu3030.portfolio.data.ContentListRepository
import io.github.gutugutu3030.portfolio.ui.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 制作物一覧画面の状態を管理する [ViewModel]。
 *
 * @property repository 制作物一覧の取得に使用する [ContentListRepository]
 */
class HomeViewModel(
    private val repository: ContentListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    /**
     * 画面の表示状態。
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    /**
     * 制作物一覧を非同期で読み込む。
     */
    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            _uiState.value = try {
                HomeUiState.Success(repository.load())
            } catch (e: Exception) {
                HomeUiState.Error(e.message ?: "不明なエラーが発生しました")
            }
        }
    }
}
