package io.github.gutugutu3030.portfolio.pages

import initPanel
import io.github.gutugutu3030.portfolio.App
import io.github.gutugutu3030.portfolio.components.linkMark
import io.github.gutugutu3030.portfolio.pages.app.tripWeatherPanelCreator
import io.github.gutugutu3030.util.parseMarkdown
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.h3
import io.kvision.html.i
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import kotlinx.coroutines.CoroutineScope
import weatherPanelCreator

/**
 * アプリのルーティングパスのベース
 */
private const val PATH = "/app"

/**
 * アプリの内容を表すデータクラス
 * @param title タイトル
 * @param description 説明文のリスト（複数行対応）
 * @param isUnderConstruction 制作途中であるかどうかのフラグ（デフォルトはfalse）
 */
data class AppContentsItem(
    val title: String,
    val description: List<String>,
    val isUnderConstruction: Boolean = false,
) {
    /**
     * このライブラリの内容をHTML要素としてレンダリングする
     * @param container レンダリング先のコンテナ
     */
    fun render(container: Container, url: String) {
        container.apply {
            div(className = "card mb-2") {
                div(className = "card-body") {
                    h3(this@AppContentsItem.title, className = "card-title")
                    description.map {
                        it.parseMarkdown(p())
                    }
                    if (isUnderConstruction) {
                        p(className = "text-warning") {
                            i(className = "bi-cone-striped")
                            +"制作途中です"
                        }
                    }
                    linkMark(url = "/#$url", openInCurrent = true){
                        +"このアプリを開く"
                    }
                }
            }
        }
    }
}

/**
 * アプリのルーティングパスとパネルの生成関数を管理する列挙型
 * @param panelCreator パネルの生成関数
 * @param content アプリの内容を表すデータクラス
 */
enum class AppList(val panelCreator: suspend CoroutineScope.() -> SimplePanel, val content: AppContentsItem) {

   TripWeather(
       tripWeatherPanelCreator,
         AppContentsItem(
              title = "旅行天気予報アプリ",
              description = listOf(
                "旅行先の天気予報を表示するアプリです．",
                "OpenWeatherMapのAPIを使用しています。"
              ),
         )
   ),

    /**
     * 天気予報アプリ
     */
    WEATHER(
        weatherPanelCreator, AppContentsItem(
            title = "天気予報アプリ",
            description = listOf(
                "現在地の詳細な天気予報を表示するアプリです．",
                "OpenWeatherMapのAPIを使用しています。"
            ),
            )
    );

    /**
     * このアプリのルーティングパス
     */
    val path: String = "$PATH/${name.lowercase()}"
}

/**
 * アプリの一覧を表示するパネル
 */
class AppListPanel : SimplePanel() {
    init {
        div {
            h1("アプリ")
            p("現在公開されているアプリの一覧です．")
        }
        AppList.entries.map{
            it.content.render(this@AppListPanel, it.path)
        }
    }
}

/**
 * アプリのルーティングパスとパネルの生成関数を管理する列挙型
 */
fun initApps(app: App) {
    initPanel(app, PATH) {
        AppListPanel()
    }
    AppList.entries.forEach {
        initPanel(app, it.path, it.panelCreator);
    }
}