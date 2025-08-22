plugins {
    id("blazern.langample.plugin.core")
}

android {
    namespace = "blazern.langample.test_utils"
}

dependencies {
    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.test)
}
