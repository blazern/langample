plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.data.lexical_item_details_source.aggregation"
}

dependencies {
    api(project(":data:lexical-item-details-source:api"))
    implementation(project(":domain:settings"))
    implementation(project(":data:tatoeba"))
    implementation(project(":data:langample-graphql"))
    implementation(project(":data:kaikki"))
    implementation(project(":core:ktor"))
    implementation(project(":data:lexical-item-details-source:utils:examples-tools"))
    implementation(project(":data:lexical-item-details-source:utils:cache"))
    implementation(project(":data:lexical-item-details-source:tatoeba"))
    implementation(project(":data:lexical-item-details-source:chatgpt"))
    implementation(project(":data:lexical-item-details-source:kaikki"))
    implementation(project(":data:lexical-item-details-source:panlex"))
    implementation(project(":data:lexical-item-details-source:wortschatz-leipzig"))
}
