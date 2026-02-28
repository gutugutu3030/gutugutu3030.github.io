# webpack + Leaflet でデフォルトマーカーアイコンが 404 になる問題

## 症状

- 地図上にマーカーを配置すると、ピン画像が表示されない
- ブラウザの DevTools で以下のような 404 リクエストが発生している

```
GET http://localhost:3000/marker-icon-2x.png 404 (Not Found)
GET http://localhost:3000/marker-icon.png    404 (Not Found)
GET http://localhost:3000/marker-shadow.png  404 (Not Found)
```

## 原因

Leaflet の `L.Icon.Default.prototype._getIconUrl()` は、自身がロードされた
`<script>` タグの URL を基点としてアイコン画像のパスを動的に解決する。

しかし **webpack でバンドルすると** このスクリプトタグ検出が失敗し、
サーバーのルートパス (`/marker-icon-2x.png` 等) を参照してしまう。
実際の PNG は `node_modules/leaflet/dist/images/` に存在するが、
バンドルに含まれないためリクエストが 404 になる。

## 解決策

PNG を `@JsModule` で明示的に import して webpack に `asset/resource`
として処理させ、`L.Icon.Default.mergeOptions()` で正しい URL を注入する。

### 1. `LeafletExternals.kt` に PNG import と修正関数を追加

```kotlin
// webpack の asset/resource として処理させることで、
// バンドル後のハッシュ付き URL 文字列が得られる
@JsModule("leaflet/dist/images/marker-icon.png")
@JsNonModule
external val markerIconUrl: dynamic

@JsModule("leaflet/dist/images/marker-icon-2x.png")
@JsNonModule
external val markerIconRetinaUrl: dynamic

@JsModule("leaflet/dist/images/marker-shadow.png")
@JsNonModule
external val markerShadowUrl: dynamic

fun fixLeafletDefaultIcons() {
    // 自動パス検出を無効化する
    val l: dynamic = L
    js("delete l.Icon.Default.prototype._getIconUrl")
    // webpack が解決した正しい URL を設定する
    val opts = js("({})")
    opts["iconUrl"] = markerIconUrl
    opts["iconRetinaUrl"] = markerIconRetinaUrl
    opts["shadowUrl"] = markerShadowUrl
    L.asDynamic().Icon.Default.mergeOptions(opts)
}
```

### 2. モジュール初期化時に呼び出す (`TripWeather.kt` 等)

```kotlin
// CSS インポートと同じ要領でモジュール初期化時に一度だけ実行する
@Suppress("UNUSED_VARIABLE")
private val initLeafletIcons = fixLeafletDefaultIcons()
```

## 確認方法

ビルド後の出力ディレクトリ (`build/kotlin-webpack/js/developmentExecutable/`)
に `marker-icon-2x.png` 相当のハッシュ付き PNG が新規生成されていれば OK。

```
680f69f3c2e6b90c1812.png  ← marker-icon-2x.png (2.4K)
a0c6cc1401c107b501ef.png  ← marker-shadow.png  (618B)
```

## 参考

- [Leaflet 公式 issue #4968](https://github.com/Leaflet/leaflet/issues/4968)
- 同様に webpack を使う React / Vue プロジェクトでも頻出する問題で、
  解決策のパターンは共通。
