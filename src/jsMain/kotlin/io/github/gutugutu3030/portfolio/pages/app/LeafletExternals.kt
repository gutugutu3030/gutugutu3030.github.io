package io.github.gutugutu3030.portfolio.pages.app

// Leaflet CSS を webpack 経由で読み込む
// このプロパティを参照することで CSS がバンドルに含まれる
@JsModule("leaflet/dist/leaflet.css")
@JsNonModule
external val leafletCss: dynamic

/**
 * Leaflet.js のエントリポイントとなるグローバルオブジェクト `L` の外部宣言。
 * npm パッケージ "leaflet" からインポートされる。
 */
@JsModule("leaflet")
@JsNonModule
external object L {
    /**
     * 指定した DOM 要素 ID でLeafletマップを生成する。
     * @param id マップをマウントする HTML 要素の id 属性値
     * @param options Leaflet の MapOptions に相当する動的オブジェクト
     * @return 生成された [LeafletMap] インスタンス
     */
    fun map(id: String, options: dynamic = definedExternally): LeafletMap

    /**
     * 指定した DOM 要素で Leaflet マップを生成する。
     * @param element マップをマウントする [HTMLElement]
     * @param options Leaflet の MapOptions に相当する動的オブジェクト
     * @return 生成された [LeafletMap] インスタンス
     */
    fun map(element: org.w3c.dom.HTMLElement, options: dynamic = definedExternally): LeafletMap

    /**
     * タイルレイヤーを生成する。
     * @param urlTemplate タイルの URL テンプレート (例: `https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png`)
     * @param options Leaflet の TileLayerOptions に相当する動的オブジェクト
     * @return 生成された [TileLayer] インスタンス
     */
    fun tileLayer(urlTemplate: String, options: dynamic = definedExternally): TileLayer

    /**
     * 緯度・経度を表す [LatLng] オブジェクトを生成する。
     * @param lat 緯度 (度)
     * @param lng 経度 (度)
     * @return 生成された [LatLng] インスタンス
     */
    fun latLng(lat: Double, lng: Double): LatLng

    /**
     * 画像ファイルを使ったマーカーアイコンを生成する。
     * @param options Leaflet の IconOptions に相当する動的オブジェクト
     * @return 生成された [LeafletIcon] インスタンス
     */
    fun icon(options: dynamic): LeafletIcon

    /**
     * 指定した緯度・経度にマーカーを生成する。
     * @param latLng マーカーを配置する座標
     * @param options Leaflet の MarkerOptions に相当する動的オブジェクト
     * @return 生成された [Marker] インスタンス
     */
    fun marker(latLng: LatLng, options: dynamic = definedExternally): Marker

    /**
     * HTML 文字列を使った div ベースのマーカーアイコンを生成する。
     * @param options Leaflet の DivIconOptions に相当する動的オブジェクト
     * @return 生成された [LeafletIcon] インスタンス
     */
    fun divIcon(options: dynamic): LeafletIcon
}

/**
 * Leaflet の緯度・経度座標を表すインターフェース。
 * [L.latLng] で生成する。
 */
external interface LatLng

/**
 * Leaflet のマップインスタンスを表すクラス。
 * [L.map] で生成する。
 */
external class LeafletMap {
    /**
     * マップの表示位置とズームレベルを即座に設定する。
     * @param latLng 中心座標
     * @param zoom ズームレベル
     * @return メソッドチェーン用に自身を返す
     */
    fun setView(latLng: LatLng, zoom: Int): LeafletMap

    /**
     * 指定した座標へアニメーションしながら移動する。
     * @param latLng 移動先の座標
     * @param zoom 移動後のズームレベル
     * @return メソッドチェーン用に自身を返す
     */
    fun flyTo(latLng: LatLng, zoom: Int): LeafletMap

    /**
     * マップにレイヤーを追加する。
     * @param layer 追加する [Layer]
     * @return メソッドチェーン用に自身を返す
     */
    fun addLayer(layer: Layer): LeafletMap

    /**
     * マップからレイヤーを削除する。
     * @param layer 削除する [Layer]
     * @return メソッドチェーン用に自身を返す
     */
    fun removeLayer(layer: Layer): LeafletMap

    /**
     * マップを破棄してリソースを解放する。
     * コンポーネントのアンマウント時に呼び出すこと。
     */
    fun remove()

    /**
     * マップのサイズを再計算して描画を更新する。
     * コンテナのサイズが動的に変わった場合に呼び出す。
     */
    fun invalidateSize()
}

/**
 * JavaScript のグローバル関数 `encodeURIComponent` の外部宣言。
 * URL に埋め込む文字列をパーセントエンコードする。
 * @param str エンコードする文字列
 * @return パーセントエンコードされた文字列
 */
external fun encodeURIComponent(str: String): String

/**
 * Leaflet のレイヤー基底インターフェース。
 * [TileLayer] や [Marker] などの親となる。
 */
external interface Layer

/**
 * OpenStreetMap などのタイル画像を表示するレイヤー。
 * [L.tileLayer] で生成し、[LeafletMap.addLayer] でマップに追加する。
 */
external class TileLayer : Layer

/**
 * [LeafletMap.addLayer] に渡すタイルレイヤーのオプションを表すデータクラス。
 * Kotlin 側で型安全にオプションを組み立てるために使用する。
 *
 * @property attribution 地図右下に表示する著作権表示 HTML 文字列
 * @property maxZoom 最大ズームレベル (Leaflet デフォルトは 18)
 */
data class TileLayerOptions(
    val attribution: String? = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
    val maxZoom: Int = 19
)

/**
 * Leaflet のマーカーアイコンを表すクラス。
 * [L.icon] または [L.divIcon] で生成し、MarkerOptions の `icon` プロパティに渡す。
 */
external class LeafletIcon

/**
 * 地図上の特定座標に配置されるマーカーを表すクラス。
 * [L.marker] で生成する。
 */
external class Marker : Layer {
    /**
     * マーカーを指定のマップに追加する。
     * @param map 追加先の [LeafletMap]
     * @return メソッドチェーン用に自身を返す
     */
    fun addTo(map: LeafletMap): Marker

    /**
     * マーカーにポップアップを紐付ける。
     * マーカークリック時にポップアップが表示される。
     * @param content ポップアップに表示する HTML 文字列またはテキスト
     * @return メソッドチェーン用に自身を返す
     */
    fun bindPopup(content: String): Marker

    /**
     * マーカーに DOM 要素をポップアップコンテンツとして紐付ける。
     * @param element ポップアップに表示する [HTMLElement]
     * @return メソッドチェーン用に自身を返す
     */
    fun bindPopup(element: org.w3c.dom.HTMLElement): Marker

    /**
     * 紐付けられたポップアップを開く。
     * @return メソッドチェーン用に自身を返す
     */
    fun openPopup(): Marker
}
