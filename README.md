# gutugutu3030.github.io

gutugutu3030 のポートフォリオサイト。Kotlin/JS + KVision で構築した PWA 対応の SPA です。

## 技術スタック

- **言語**: Kotlin/JS (IR)
- **UIフレームワーク**: [KVision](https://kvision.io/)
- **ビルドツール**: Gradle + Webpack
- **ホスティング**: GitHub Pages
- **PWA**: Workbox (workbox-webpack-plugin)

## ローカル開発

### 開発サーバー起動

```bash
./gradlew -t run
```

webpack dev server が port 3000 で起動します。コード変更時にホットリロードされます。

### プロダクションビルド

```bash
./gradlew jsBrowserDistribution
```

ビルド成果物は `build/dist/js/productionExecutable/` に出力されます。

## デプロイ

`master` ブランチへ push すると GitHub Actions が自動でビルド・デプロイします。

**ワークフロー**: [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml)

1. `master` ブランチへ push
2. GitHub Actions が `./gradlew jsBrowserDistribution` を実行
3. `build/dist/js/productionExecutable/` の内容を `gh-pages` ブランチへ自動公開

> **注意**: `README.md` の変更のみの push ではワークフローはトリガーされません。

## Gradle タスク

| タスク | 説明 |
|---|---|
| `./gradlew -t run` | webpack dev server を port 3000 で起動（ホットリロード付き） |
| `./gradlew jsBrowserDistribution` | プロダクションビルド（`build/dist/js/productionExecutable/` へ出力） |
| `./gradlew AllClean` | `build/`・`.gradle/` キャッシュを一括削除 |

## PWA 対応

- **Service Worker**: Workbox (`InjectManifest` モード) で自動生成
- **オフラインキャッシュ**: `contents/` 以下・YAML・アプリ本体をキャッシュ
- **キャッシュ除外**: `star/full/`（フル画像）・`pdf/` はストレージ節約のためキャッシュしない
- **iOS 対応**: `apple-touch-icon.png`・`manifest.json`・各種 Apple メタタグを設定済み

## アイコン

`src/jsMain/resources/apple-touch-icon.png` を差し替えることで PWA アイコンを変更できます。
