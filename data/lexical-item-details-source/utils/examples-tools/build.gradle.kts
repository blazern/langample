plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.data.examples_tools"
}

dependencies {
    api(project(":domain:model"))
    implementation(project(":data:lexical-item-details-source:kaikki"))
}
