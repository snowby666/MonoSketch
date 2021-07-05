plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":keycommand"))
    implementation(project(":lifecycle"))
    implementation(project(":livedata"))

    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.3")

    testImplementation(kotlin("test-js"))
}

val compilerType: org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType by ext
kotlin {
    js(compilerType) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()
    }
}
