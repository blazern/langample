plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.model.lexical_item_details_source.tatoeba"
}

dependencies {
    api(project(":domain:lexical-item-details-source"))
    implementation(project(":data:tatoeba"))
}
