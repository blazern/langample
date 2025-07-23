plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.domain.settings"
}

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.prefs)
    implementation(project(":domain:model"))
}
