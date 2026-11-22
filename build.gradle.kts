plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kvision)
}

version = "1.0.0-SNAPSHOT"
group = "io.github.gutugutu3030"

kotlin {
    js {
        browser {
            useEsModules()
            commonWebpackConfig {
                outputFileName = "main.bundle.js"
                sourceMaps = false
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
        compilerOptions {
            target.set("es2015")
        }
    }
    sourceSets["jsMain"].dependencies {
        implementation(libs.kvision)
        implementation(libs.kvision.bootstrap)
        implementation(libs.kvision.datetime)
        implementation(libs.kvision.richtext)
        implementation(libs.kvision.tom.select)
        implementation(libs.kvision.bootstrap.upload)
        implementation(libs.kvision.bootstrap.icons)
        implementation(libs.kvision.pace)
        implementation(libs.kvision.ktml)
        implementation(libs.kvision.routing.navigo.ng)
        implementation(libs.kvision.state)
        implementation(libs.kaml)
        implementation(npm("leaflet", libs.versions.leaflet.get()))
        implementation(npm("@types/leaflet", libs.versions.types.leaflet.get()))
    }
    sourceSets["jsTest"].dependencies {
        implementation(kotlin("test-js"))
        implementation(libs.kvision.testutils)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack> {
    outputs.cacheIf { false } // webpack は常に実行（Gradle ビルドキャッシュ対象外）
}
// ========== AllClean タスク ==========
// build ディレクトリ、.gradle キャッシュ、node_modules を一括削除します
tasks.register("AllClean") {
    group = "build"
    description = "ビルド成果物・Gradleキャッシュ・node_modules を全て削除します"

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

        // ホームディレクトリの Gradle キャッシュ (任意 — コメントを外すと実行)
        // val globalCache = file("${System.getProperty("user.home")}/.gradle/caches")
        // if (globalCache.exists()) { delete(globalCache); println("削除: ${globalCache.absolutePath}") }

        println("AllClean 完了。次回ビルドは完全にクリーンな状態で実行されます。")
    }
}

