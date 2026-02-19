import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    val kvisionVersion: String by System.getProperties()
    id("io.kvision") version kvisionVersion
}

version = "1.0.0-SNAPSHOT"
group = "io.github.gutugutu3030"

// Versions
val kvisionVersion: String by System.getProperties()
val jacksonVersion: String by System.getProperties()

kotlin {
    js(IR) {
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
        implementation("io.kvision:kvision:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
        implementation("io.kvision:kvision-datetime:$kvisionVersion")
        implementation("io.kvision:kvision-richtext:$kvisionVersion")
        implementation("io.kvision:kvision-tom-select:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-upload:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-icons:$kvisionVersion")
        implementation("io.kvision:kvision-pace:$kvisionVersion")
        implementation("io.kvision:kvision-ktml:$kvisionVersion")
        implementation("io.kvision:kvision-routing-navigo-ng:$kvisionVersion")
        implementation("io.kvision:kvision-state:$kvisionVersion")
        implementation("com.charleskorn.kaml:kaml:0.55.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
    }
    sourceSets["jsTest"].dependencies {
        implementation(kotlin("test-js"))
        implementation("io.kvision:kvision-testutils:$kvisionVersion")
    }
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

        // ホームディレクトリの Gradle キャッシュ (任意 — コメントを外すと実行)
        // val globalCache = file("${System.getProperty("user.home")}/.gradle/caches")
        // if (globalCache.exists()) { delete(globalCache); println("削除: ${globalCache.absolutePath}") }

        println("AllClean 完了。次回ビルドは完全にクリーンな状態で実行されます。")
    }
}
