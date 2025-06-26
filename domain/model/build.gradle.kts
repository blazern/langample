plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.domain.model"
}

dependencies {
    implementation(project(":core:strings"))
}
