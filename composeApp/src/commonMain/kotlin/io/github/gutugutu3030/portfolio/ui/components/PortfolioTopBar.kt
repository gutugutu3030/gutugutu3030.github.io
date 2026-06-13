package io.github.gutugutu3030.portfolio.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.gutugutu3030.portfolio.ui.theme.ThemeMode
import io.github.gutugutu3030.portfolio.util.openExternalUrl
import io.github.gutugutu3030.portfolio.viewmodel.ThemeViewModel

/**
 * アプリケーション共通のトップバー。
 *
 * @param navController 画面遷移に使用する [NavHostController]
 * @param themeViewModel テーマ設定を保持する [ThemeViewModel]
 * @param modifier レイアウトに適用する [Modifier]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioTopBar(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("gutugutu3030") },
        modifier = modifier,
        navigationIcon = {
            if (currentDestination?.isHome() == false) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "戻る"
                    )
                }
            }
        },
        actions = {
            ThemeToggleButton(
                themeMode = themeMode,
                onToggle = { themeViewModel.toggleThemeMode() }
            )
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "メニュー"
                )
            }
            NavigationMenu(
                expanded = menuExpanded,
                onDismiss = { menuExpanded = false },
                onNavigate = { route ->
                    menuExpanded = false
                    navController.navigate(route)
                }
            )
        }
    )
}

/**
 * 現在の目的地がホーム画面かどうかを判定する。
 *
 * @return ホーム画面の場合は true
 */
private fun NavDestination.isHome(): Boolean {
    return route == "/"
}

/**
 * ナビゲーションメニュー。
 *
 * @param expanded メニューが展開されているかどうか
 * @param onDismiss メニューを閉じる際のコールバック
 * @param onNavigate 項目選択時の遷移コールバック
 */
@Composable
private fun NavigationMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Products") },
            onClick = { onNavigate("/") }
        )
        DropdownMenuItem(
            text = { Text("Libraries") },
            onClick = { onNavigate("/library") }
        )
        DropdownMenuItem(
            text = { Text("Apps") },
            onClick = { onNavigate("/app") }
        )
        DropdownMenuItem(
            text = { Text("Profile") },
            onClick = { onNavigate("/profile") }
        )
        DropdownMenuItem(
            text = { Text("備忘録", color = MaterialTheme.colorScheme.primary) },
            onClick = {
                onDismiss()
                openExternalUrl("https://sites.google.com/view/gutugutu3030/home")
            }
        )
    }
}
