package io.github.gutugutu3030.portfolio.components.config

import io.github.gutugutu3030.portfolio.components.col
import io.github.gutugutu3030.portfolio.components.row
import io.github.gutugutu3030.portfolio.pages.STAR_PATH
import io.github.gutugutu3030.portfolio.pages.selectedStarPhoto
import io.kvision.core.Container
import io.kvision.core.onClick
import io.kvision.html.div
import io.kvision.html.iframe
import io.kvision.html.image
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable

/**
 * スター写真の設定データクラス
 * @param contents スター写真のリスト
 */
@Serializable
data class StarConfig(
    val contents: List<StarPhoto>
) {
    fun render(container: Container){
        container.apply {
            row{
                contents.map {
                    col(3, additiveClass = "g-2"){
                        it.render(this)
                    }
                }
            }
        }
    }
}

/**
 * スター写真のデータクラス
 * @param file 写真ファイルのパス
 * @param youtube YouTube動画のURL（任意）
 */
@Serializable
data class StarPhoto(
    val file: String,
    val youtube: String? = null,
) {
    fun render(container: Container){
        container.apply {
            div(className="card overflow-hidden"){
                image(src="$STAR_PATH/small/$file", className="thumbnail img-fluid card-img-top"){
                    mapOf(
                        "cursor" to "pointer",
                        "width" to "100%",
                        "aspectRatio" to "1/1",
                        "objectFit" to "cover"
                    ).forEach { (k, v) -> setStyle(k, v) }
                    onClick {
                        selectedStarPhoto.value = this@StarPhoto
                    }
                }
            }
        }
    }
    fun modalRender(container: Container){
        container.apply {
            if(youtube == null){
                image(src="$STAR_PATH/full/$file", className = "img-fluid")
            }else{
                iframe(youtube){
                    mapOf(
                        "width" to "100%",
                        "height" to "500px",
                        "frameBorder" to "0",
                        "allow" to "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture",
                        "allowFullScreen" to "true"
                    ).forEach {
                        setAttribute(it.key, it.value)
                    }
                }
            }
        }
    }
}