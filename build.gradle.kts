version = "1.0.0-SNAPSHOT"
group = "io.github.gutugutu3030"

// ========== AllClean タスク ==========
// build ディレクトリ、.gradle キャッシュを一括削除します
tasks.register("AllClean") {
    group = "build"
    description = "ビルド成果物・Gradleキャッシュを全て削除します"

    doLast {
        // build ディレクトリ
        val buildDir = layout.buildDirectory.asFile.get()
        if (buildDir.exists()) {
            delete(buildDir)
            println("削除: ${buildDir.absolutePath}")
        }

        // プロジェクト直下の .gradle キャッシュ
        val gradleCacheDir = file("${rootDir}/.gradle")
        if (gradleCacheDir.exists()) {
            delete(gradleCacheDir)
            println("削除: ${gradleCacheDir.absolutePath}")
        }

        // .kotlin-js-store（npm パッケージのロック情報）
        val kotlinJsStore = file("${rootDir}/.kotlin-js-store")
        if (kotlinJsStore.exists()) {
            delete(kotlinJsStore)
            println("削除: ${kotlinJsStore.absolutePath}")
        }

        println("AllClean 完了。次回ビルドは完全にクリーンな状態で実行されます。")
    }
}
