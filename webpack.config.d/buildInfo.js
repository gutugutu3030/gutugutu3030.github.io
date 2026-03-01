// ビルドバージョンを __SW_VERSION__ グローバル定数として注入する
// sw.js の CACHE_VERSION と同じ値を共有する（CI では sed で sw.js にも注入）
// Kotlin 側では `@JsName("__SW_VERSION__") external val SW_VERSION: String` で参照できる
const webpack = require("webpack");

const now = new Date();
const pad = (n) => String(n).padStart(2, "0");
// タイムゾーンを Asia/Tokyo に合わせた yyyyMMdd-HHmmss 形式
const jst = new Date(now.toLocaleString("en-US", { timeZone: "Asia/Tokyo" }));
const swVersion = [
    jst.getFullYear(),
    pad(jst.getMonth() + 1),
    pad(jst.getDate()),
    "-",
    pad(jst.getHours()),
    pad(jst.getMinutes()),
    pad(jst.getSeconds()),
].join("");

config.plugins = (config.plugins || []).concat(
    new webpack.DefinePlugin({
        __SW_VERSION__: JSON.stringify(swVersion),
    })
);
