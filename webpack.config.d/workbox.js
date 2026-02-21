// webpack.config.d/workbox.js
// Workbox InjectManifest を使い Service Worker を自動生成する
// ビルドモード（production）のときのみ適用

if (!config.devServer) {
    const { InjectManifest } = require('workbox-webpack-plugin');
    const path = require('path');

    config.plugins.push(
        new InjectManifest({
            // カスタム SW テンプレート（src/jsMain/sw-template.mjs）
            // .mjs 拡張子により webpack 5 が ESModule として処理する
            // ※ resources/ の外に置くことで docs/ にコピーされるのを防ぐ
            swSrc: path.resolve(__dirname, '../../../../src/jsMain/sw-template.mjs'),
            swDest: 'sw.js',

            // Precache から除外するファイルパターン
            exclude: [
                // star画像（small/full）はキャッシュしない
                /^star\//,
                // PDFはキャッシュしない（容量大）
                /^pdf\//,
                // ソースマップは不要
                /\.map$/,
                // LICENSEファイル不要
                /\.LICENSE\.txt$/,
            ],
        })
    );
}

