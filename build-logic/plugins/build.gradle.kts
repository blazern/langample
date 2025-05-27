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
    implementation(gradleKotlinDsl())
    testImplementation(libs.junit)
}

gradlePlugin {
    plugins {
        create("featurePlugin") {
            id = "blazern.langample.plugin.feature"
            implementationClass = "FeaturePlugin"
        }
    }
}
