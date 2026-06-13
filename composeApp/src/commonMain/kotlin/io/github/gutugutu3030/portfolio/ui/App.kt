package io.github.gutugutu3030.portfolio.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.gutugutu3030.portfolio.data.ContentListRepository
import io.github.gutugutu3030.portfolio.ui.components.PortfolioTopBar
import io.github.gutugutu3030.portfolio.ui.home.HomeScreen
import io.github.gutugutu3030.portfolio.ui.theme.PortfolioTheme
import io.github.gutugutu3030.portfolio.ui.theme.resolveDarkTheme
import io.github.gutugutu3030.portfolio.viewmodel.ThemeViewModel

/**
 * アプリケーションのルートComposable。
 *
 * @param repository 制作物一覧の取得に使用する [ContentListRepository]
 */
@Composable
fun App(repository: ContentListRepository) {
    val navController = rememberNavController()
    val themeViewModel: ThemeViewModel = viewModel { ThemeViewModel() }
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

    val isDark = resolveDarkTheme(themeMode)

    PortfolioTheme(darkTheme = isDark) {
        Scaffold(
            topBar = {
                PortfolioTopBar(
                    navController = navController,
                    themeViewModel = themeViewModel
                )
            }
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                repository = repository,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

/**
 * ナビゲーション構成。
 *
 * @param navController 画面遷移に使用する [NavHostController]
 * @param repository 制作物一覧の取得に使用する [ContentListRepository]
 * @param modifier レイアウトに適用する [Modifier]
 */
@Composable
private fun AppNavigation(
    navController: NavHostController,
    repository: ContentListRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "/",
        modifier = modifier
    ) {
        composable("/") {
            HomeScreen(repository = repository)
        }
        composable("/profile") {
            PlaceholderScreen(title = "Profile")
        }
        composable("/library") {
            PlaceholderScreen(title = "Library")
        }
        composable("/app") {
            PlaceholderScreen(title = "Apps")
        }
        composable("/contents/{directory}") {
            PlaceholderScreen(title = "Contents")
        }
    }
}
