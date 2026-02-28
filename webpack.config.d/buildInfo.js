// ビルド時刻を __BUILD_TIME__ グローバル定数として注入する
// Kotlin 側では `@JsName("__BUILD_TIME__") external val BUILD_TIME: String` で参照できる
const webpack = require("webpack");

const buildTime = new Date().toLocaleString("ja-JP", {
    timeZone: "Asia/Tokyo",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
});

config.plugins = (config.plugins || []).concat(
    new webpack.DefinePlugin({
        __BUILD_TIME__: JSON.stringify(buildTime),
    })
);
