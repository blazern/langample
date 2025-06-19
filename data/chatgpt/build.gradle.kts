import java.util.Properties

plugins {
    id("blazern.langample.plugin.library")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

val localProperties = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { load(it) }
    }
}

val apiKey: String = localProperties.getProperty("CHATGPT_API_KEY") ?: ""

android {
    namespace = "blazern.langample.data.chatgpt"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "CHATGPT_API_KEY", "\"$apiKey\"")
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.ktor.client.core)

    implementation(project(":core:ktor"))
}
