plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    google()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin.api)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.detekt.gradle.plugin)

    implementation(gradleKotlinDsl())
    testImplementation(libs.junit)
}

gradlePlugin {
    plugins {
        create("corePlugin") {
            id = "blazern.langample.plugin.core"
            implementationClass = "CorePlugin"
        }
        create("libraryPlugin") {
            id = "blazern.langample.plugin.library"
            implementationClass = "LibraryPlugin"
        }
        create("featurePlugin") {
            id = "blazern.langample.plugin.feature"
            implementationClass = "FeaturePlugin"
        }
    }
}
