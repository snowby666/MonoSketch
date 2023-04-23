/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.storeManager)
    implementation(projects.graphicsgeo)

    implementation(libs.kotlin.stdlib.js)
    testImplementation(libs.kotlin.test.js)
}

val compilerType: org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType by ext
kotlin {
    js(compilerType) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
}
