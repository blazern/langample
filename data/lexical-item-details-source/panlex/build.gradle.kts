plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.data.lexical_item_details_source.panlex"
}

dependencies {
    api(project(":data:lexical-item-details-source:api"))
    implementation(project(":data:panlex"))
    implementation(project(":core:utils"))
}
