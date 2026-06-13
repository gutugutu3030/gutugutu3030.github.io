# Portfolio KMP 移行計画メモ

## 1. 現在の状態サマリー

KVision（Kotlin/JS）から **Kotlin/Wasm + Compose Multiplatform** への移行を開始し、**Phase 1（トップページ移行）が完了**している。

- ビルド成功：`./gradlew :composeApp:wasmJsBrowserDistribution`
- 出力先：`composeApp/build/dist/wasmJs/productionExecutable/`
- デプロイ先：GitHub Pages（`gh-pages` ブランチ）

## 2. 技術スタック

| 項目 | 採用技術 | バージョン |
|---|---|---|
| 言語 | Kotlin | 2.3.20 |
| UI | Compose Multiplatform | 1.11.1 |
| Material3 | org.jetbrains.compose.material3 | 1.11.0-alpha07 |
| Material Icons | org.jetbrains.compose.material | 1.7.3 |
| HTTP | Ktor Client | 3.5.0（JS エンジン） |
| YAML | kaml | 0.104.0 |
| ナビゲーション | Jetpack Navigation KMP | 2.9.2 |
| ViewModel | lifecycle-viewmodel-compose | 2.11.0-beta01 |
| ビルド | Gradle | - |

## 3. Phase 1 で実装済み

### 3.1 プロジェクト構成

- `composeApp` モジュール新設
- ルート `build.gradle.kts` を最小構成に整理
- 旧 `src/jsMain/`、`src/jsTest/`、`webpack.config.d/`、`test.json` を削除

### 3.2 画面・機能

| 画面 | パス | 状態 |
|---|---|---|
| トップページ（制作物一覧） | `/` | 実装済み |
| プロフィール | `/profile` | プレースホルダー |
| ライブラリ | `/library` | プレースホルダー |
| アプリ一覧 | `/app` | プレースホルダー |
| 制作物詳細 | `/contents/{directory}` | プレースホルダー |

### 3.3 実装ファイル一覧

```
composeApp/src/commonMain/kotlin/io/github/gutugutu3030/portfolio/
├── data/
│   ├── ContentItem.kt                    # 制作物データモデル
│   ├── ContentListConfig.kt              # 制作物一覧設定
│   ├── ContentListRepository.kt          # Repository インターフェース
│   └── ContentListRepositoryImpl.kt      # Ktor Client 実装
├── di/
│   └── AppModule.kt                      # HttpClient / Repository 生成
├── ui/
│   ├── App.kt                            # ナビゲーション構成
│   ├── PlaceholderScreen.kt              # 未実装ページ用画面
│   ├── components/
│   │   ├── ContentCard.kt                # 制作物カード
│   │   ├── ContentImage.kt               # 画像表示（expect）
│   │   ├── PortfolioTopBar.kt            # ナビゲーションバー
│   │   └── ThemeToggleButton.kt          # テーマ切り替えボタン
│   ├── home/
│   │   ├── HomeScreen.kt                 # トップページ画面
│   │   └── HomeUiState.kt                # トップページ状態
│   └── theme/
│       ├── Theme.kt                      # Material3 テーマ
│       ├── ThemeMode.kt                  # Auto/Light/Dark 定義
│       ├── ResolveDarkTheme.kt           # テーマ解決
│       └── Type.kt                       # タイポグラフィ
├── util/
│   ├── ContentItemExtensions.kt          # ContentItem の拡張プロパティ
│   └── Platform.kt                       # openExternalUrl（expect）
└── viewmodel/
    ├── HomeViewModel.kt                  # トップページ ViewModel
    └── ThemeViewModel.kt                 # テーマ ViewModel

composeApp/src/wasmJsMain/kotlin/io/github/gutugutu3030/portfolio/
├── main.kt                               # Web エントリポイント
├── ui/components/ContentImage.wasmJs.kt  # HTML img 実装
└── util/Platform.wasmJs.kt               # window.open 実装
```

### 3.4 PWA 対応

- `index.html`：Wasm 出力 `portfolio.js` を読み込み、SPA リダイレクト復元
- `404.html`：パスを `sessionStorage` に保存して `/` へリダイレクト
- `sw.js`： precache / runtime cache を Wasm 出力に合わせ更新
- `manifest.json`：維持

## 4. 未実装・既知の課題

### 4.1 未実装画面

- `/profile`：プロフィール表示（`profile.yaml` 読み込み）
- `/library`：ライブラリ一覧（`library.yaml` 読み込み）
- `/app`：アプリ一覧＋天気予報アプリ＋旅行天気アプリ
- `/contents/{directory}`：制作物詳細（各 `contents/{dir}/data.yaml` 読み込み）

### 4.2 未実装機能

- **Markdown パーサー**：旧 `Markdown.kt` に相当。詳細ページ実装時に Compose 対応で再実装が必要。
- **Leaflet 地図**：旅行天気アプリで使用。Wasm-JS interop で再実装が必要。
- **テーマ永続化**：旧アプリでは `localStorage` に保存。現在はメモリ保持のみ。
- **検索・フィルタ**：旧アプリでは未実装だったが、必要なら追加検討。

### 4.3 技術的負債・警告

- **ClickableText の非推奨警告**：`HomeScreen.kt` の紹介テキストリンクで使用。Compose 1.11 では `LinkAnnotation` への移行が推奨されているが、現状は警告のみで動作する。
- **バンドルサイズ**：`portfolio.js` 523 KiB、`portfolio.wasm` 3.24 MiB、`skiko.wasm` 8.25 MiB。webpack のサイズ警告が出るが、これは Wasm アプリの初期コストとして許容。
- **@js-joda/core**：Ktor/kaml の依存で含まれる（392 KiB）。将来削減できれば検討。

### 4.4 動作未検証項目

- ブラウザでの実際のレンダリング
- `/profile` などの直接 URL アクセス（GitHub Pages + 404.html 経由）
- Service Worker のキャッシュ動作
- 画像読み込み（`HtmlElementView` 経由）
- YAML 読み込み（Ktor + kaml）

## 5. アーキテクチャ指針

### 5.1 レイヤー構成

```
Presentation (Compose UI + ViewModel)
    ↓
Domain (Repository interface, data class)
    ↓
Data (RepositoryImpl, Ktor Client, YAML decode)
    ↓
Platform (wasmJsMain: JS interop)
```

### 5.2 重要な設計判断

- **HTTP 層**：Ktor Client（JS エンジン）を `commonMain` で使用。将来 JVM/Android 追加時も `HttpClient` インターフェースは共有可能。
- **YAML**：`kaml` を使用。YAML → JSON 変換は**禁止**（ユーザー要件）。
- **画像表示**：Web では `HtmlElementView` で `<img>` を表示。他プラットフォーム追加時は `ContentImage` の actual 実装を追加。
- **ルーティング**：Compose Navigation を使用。URL パスは通常の `/profile` 形式（ハッシュなし）。
- **外部リンク**：`expect fun openExternalUrl(url: String)` で抽象化。

## 6. 次の Phase 計画

### Phase 2: 制作物詳細ページ

1. `ContentData` sealed class とそのサブクラスを `commonMain` に移植（旧 `ContentConfig.kt` 参照）
2. `ContentDetailScreen.kt` / `ContentDetailViewModel` を作成
3. `/contents/{directory}` ルートを実装
4. Markdown パーサーを Compose 対応で再実装
5. 各 `ContentData` の `render()` を Compose Composable に置き換え

### Phase 3: プロフィール・ライブラリページ

1. `ProfileConfig` / `LibraryConfig` データモデルを `commonMain` に移植
2. `ProfileScreen` / `LibraryScreen` を作成
3. `profile.yaml` / `library.yaml` を読み込む Repository/ViewModel を追加
4. 旧レイアウトを Compose + Material3 で再現

### Phase 4: アプリページ

1. `AppListScreen` を作成
2. 天気予報アプリを Compose 化
3. 旅行天気アプリを Compose 化
4. Leaflet 地図を Wasm-JS interop で再実装

### Phase 5: 追加プラットフォーム（オプション）

1. `androidMain` / `iosMain` / `desktopMain` を追加
2. UI は `commonMain` の Compose を共有
3. プラットフォーム固有の `ContentImage` actual 実装を追加

## 7. 開発コマンド

```bash
# 開発サーバー起動
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# プロダクションビルド
./gradlew :composeApp:wasmJsBrowserDistribution

# クリーン
./gradlew AllClean
```

## 8. コーディング規約（ユーザー要件）

- **トップレベル関数**：責務を考え、問題なければ拡張関数・拡張プロパティとすること
- **副作用のある動作**：（拡張）プロパティとしては使わないこと
- **KDoc**：関数単位で日本語コメントを記載。`@param` `@return` `@property` も含めること
- YAML は YAML のまま維持すること（JSON リファクタ禁止）
- URL パスは通常の `/profile` 形式を維持
- テーマは Auto / Light / Dark の 3 状態を維持

## 9. 参考：旧実装の在り処

旧 KVision コードは Git 履歴に残っている。主な参考ファイル：

- `src/jsMain/kotlin/io/github/gutugutu3030/portfolio/pages/ProfilePanel.kt`
- `src/jsMain/kotlin/io/github/gutugutu3030/portfolio/pages/LibraryPanel.kt`
- `src/jsMain/kotlin/io/github/gutugutu3030/portfolio/pages/AppListPanel.kt`
- `src/jsMain/kotlin/io/github/gutugutu3030/portfolio/components/config/ContentConfig.kt`
- `src/jsMain/kotlin/io/github/gutugutu3030/portfolio/pages/app/TripWeather.kt`
- `src/jsMain/kotlin/io/github/gutugutu3030/util/Markdown.kt`

これらは現在の `HEAD` から削除されているが、`git show <commit>:<path>` で参照可能。
