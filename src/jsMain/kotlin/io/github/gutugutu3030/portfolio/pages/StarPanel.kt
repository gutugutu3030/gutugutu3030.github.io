package io.github.gutugutu3030.portfolio.pages

import com.charleskorn.kaml.Yaml
import io.github.gutugutu3030.portfolio.App
import io.github.gutugutu3030.portfolio.components.config.StarConfig
import io.github.gutugutu3030.portfolio.components.config.StarPhoto
import io.kvision.core.Display
import io.kvision.core.Style
import io.kvision.core.onClick
import io.kvision.core.style
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

/**
 * スターパネルのルーティングパス
 */
const val STAR_PATH = "/star"

/**
 * スターデータのYAMLファイル名
 */
const val STAR_YAML = "star.yaml"

private suspend fun loadStarConfig(): StarConfig =
    window.fetch(STAR_YAML).await().text().await()
        .let { Yaml.default.decodeFromString(StarConfig.serializer(), it) }

fun initStar(app: App){
    app.apply{
        routing.kvOn(STAR_PATH){
            scope.launch {
                val config = loadStarConfig()
                contentPanel.removeAll()
                contentPanel.add(StarPanel(config))
            }
        }
    }
}

val selectedStarPhoto = ObservableValue<StarPhoto?>(null)

class StarPanel(val config: StarConfig): SimplePanel() {
    init{
        config.render(this)
        div(className = "modal-backdrop fade").bind(selectedStarPhoto){ photo->
            if(photo != null){
                addCssClass("show")
                style {
                    display = Display.BLOCK
                }
            } else {
                removeCssClass("show")
                window.setTimeout({
                    style{
                        display = Display.NONE
                    }
                }, 150)
            }
        }
        div(className="modal fade").bind(selectedStarPhoto){ photo->
            if(photo != null){
                style {
                    display = Display.BLOCK
                }
                window.setTimeout({
                    addCssClass("show")
                }, 10)
                onClick {
                    selectedStarPhoto.value = null
                }
            } else {
                removeCssClass("show")
                style{
                    display = Display.NONE
                }
            }
            div(className="modal-dialog modal-lg"){
                div(className="modal-content"){
                    photo?.modalRender(this)
                    onClick { it.stopPropagation() }
                }
            }
        }
    }
}