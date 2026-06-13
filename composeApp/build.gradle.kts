import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    val composeVersion: String by System.getProperties()
    id("org.jetbrains.compose") version composeVersion
    id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "portfolio"
        browser {
            commonWebpackConfig {
                outputFileName = "portfolio.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                val composeVersion: String by System.getProperties()
                implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
                implementation("org.jetbrains.compose.foundation:foundation:$composeVersion")
                implementation("org.jetbrains.compose.ui:ui:$composeVersion")
                implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")
                implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
                implementation("org.jetbrains.compose.material3:material3:1.11.0-alpha07")

                val ktorVersion: String by System.getProperties()
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-js:$ktorVersion")

                val kamlVersion: String by System.getProperties()
                implementation("com.charleskorn.kaml:kaml:$kamlVersion")

                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0-beta01")
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.2")
            }
        }
    }
}
