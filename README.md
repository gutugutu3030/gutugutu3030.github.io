# gutugutu3030.github.io

gutugutu3030 のポートフォリオサイト。Kotlin/Wasm + Compose Multiplatform で構築した PWA 対応の SPA です。

## 技術スタック

- **言語**: Kotlin (Kotlin/Wasm)
- **UIフレームワーク**: [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **ビルドツール**: Gradle
- **ホスティング**: GitHub Pages
- **PWA**: 手書き Service Worker

## プロジェクト構成

```
portfolio/
├── composeApp/              # Compose Multiplatform アプリ
│   ├── src/
│   │   ├── commonMain/      # 共有コード（UI、ViewModel、Repository）
│   │   └── wasmJsMain/      # Web エントリポイント、静的リソース
│   └── build.gradle.kts
├── build.gradle.kts         # ルート（共通タスクのみ）
├── settings.gradle.kts
└── gradle.properties
```

## ローカル開発

### 開発サーバー起動

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

webpack dev server が起動します。コード変更時にホットリロードされます。

### プロダクションビルド

```bash
./gradlew :composeApp:wasmJsBrowserDistribution
```

ビルド成果物は `composeApp/build/dist/wasmJs/productionExecutable/` に出力されます。

## デプロイ

`master` ブランチへ push すると GitHub Actions が自動でビルド・デプロイします。

**ワークフロー**: [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml)

1. `master` ブランチへ push
2. GitHub Actions が `./gradlew :composeApp:wasmJsBrowserDistribution` を実行
3. `composeApp/build/dist/wasmJs/productionExecutable/` の内容を `gh-pages` ブランチへ自動公開

> **注意**: `README.md` の変更のみの push ではワークフローはトリガーされません。

## Gradle タスク

| タスク | 説明 |
|---|---|
| `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` | 開発サーバーを起動（ホットリロード付き） |
| `./gradlew :composeApp:wasmJsBrowserDistribution` | プロダクションビルド（`composeApp/build/dist/wasmJs/productionExecutable/` へ出力） |
| `./gradlew AllClean` | `build/`・`.gradle/` キャッシュを一括削除 |

## PWA 対応

- **Service Worker**: 手書き `sw.js`
- **オフラインキャッシュ**: `contents/` 以下・YAML・アプリ本体をキャッシュ
- **キャッシュ除外**: `star/full/`（フル画像）・`pdf/` はストレージ節約のためキャッシュしない
- **iOS 対応**: `apple-touch-icon.png`・`manifest.json`・各種 Apple メタタグを設定済み

## アイコン

`composeApp/src/wasmJsMain/resources/apple-touch-icon.png` を差し替えることで PWA アイコンを変更できます。
