package io.github.gutugutu3030.portfolio.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.gutugutu3030.portfolio.data.ContentListConfig
import io.github.gutugutu3030.portfolio.data.ContentListRepository
import io.github.gutugutu3030.portfolio.ui.components.ContentCard
import io.github.gutugutu3030.portfolio.util.openExternalUrl
import io.github.gutugutu3030.portfolio.viewmodel.HomeViewModel

/**
 * 制作物一覧（トップページ）画面。
 *
 * @param repository 制作物一覧の取得に使用する [ContentListRepository]
 * @param navController 詳細ページへの遷移に使用する [NavController]
 * @param modifier レイアウトに適用する [Modifier]
 */
@Composable
fun HomeScreen(
    repository: ContentListRepository,
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = viewModel { HomeViewModel(repository) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        onRetry = { viewModel.load() },
        onContentClick = { route -> navController.navigate(route) },
        modifier = modifier
    )
}

/**
 * [HomeUiState] に応じた画面内容を表示する。
 *
 * @param uiState 現在の表示状態
 * @param onRetry 再読み込み要求時のコールバック
 * @param onContentClick コンテンツカード選択時の遷移コールバック
 * @param modifier レイアウトに適用する [Modifier]
 */
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onRetry: () -> Unit,
    onContentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        IntroductionText(
            onNavigateToLibrary = { onContentClick("/library") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        when (uiState) {
            is HomeUiState.Loading -> LoadingIndicator()
            is HomeUiState.Error -> ErrorMessage(
                message = uiState.message,
                onRetry = onRetry
            )
        is HomeUiState.Success -> ContentGrid(
                config = uiState.config,
                onContentClick = onContentClick
            )
        }
    }
}

/**
 * トップページの紹介テキスト。
 * YouTube リンクは外部ブラウザ、Libraries リンクは内部ナビゲーションで開く。
 *
 * @param onNavigateToLibrary Libraries ページへの遷移要求時のコールバック
 * @param modifier レイアウトに適用する [Modifier]
 */
@Composable
private fun IntroductionText(
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val linkColor = MaterialTheme.colorScheme.primary
    val annotatedString = buildAnnotatedString {
        append("制作物一覧です。動画一覧は ")
        pushStringAnnotation(tag = "youtube", annotation = "https://www.youtube.com/playlist?list=PLQyFKRbKsHDv9lAgn7kHZCq7V1gATlHlU")
        withStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)) {
            append("YouTube")
        }
        pop()
        append(" 、制作したライブラリ一覧は ")
        pushStringAnnotation(tag = "library", annotation = "/library")
        withStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)) {
            append("Libraries")
        }
        pop()
        append(" をご覧ください。")
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "youtube", start = offset, end = offset)
                .firstOrNull()?.let { openExternalUrl(it.item) }
            annotatedString.getStringAnnotations(tag = "library", start = offset, end = offset)
                .firstOrNull()?.let { onNavigateToLibrary() }
        },
        modifier = modifier
    )
}

/**
 * 読み込み中表示。
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * エラー表示。
 *
 * @param message エラーメッセージ
 * @param onRetry 再読み込み要求時のコールバック
 */
@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "エラー: $message",
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = onRetry) {
            Text("再読み込み")
        }
    }
}

/**
 * 制作物カードのグリッド表示。
 *
 * @param config 表示する [ContentListConfig]
 * @param onContentClick カード選択時の遷移コールバック
 */
@Composable
private fun ContentGrid(
    config: ContentListConfig,
    onContentClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = config.contents.reversed(),
            key = { it.directory }
        ) { item ->
            ContentCard(
                item = item,
                onInternalClick = onContentClick
            )
        }
    }
}
