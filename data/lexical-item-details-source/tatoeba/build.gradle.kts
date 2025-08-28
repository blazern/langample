plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.model.lexical_item_details_source.tatoeba"
}

dependencies {
    api(project(":data:lexical-item-details-source:api"))
    implementation(project(":data:lexical-item-details-source:cache"))
    implementation(project(":data:tatoeba"))
    implementation(project(":core:utils"))
}
