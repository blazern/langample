plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.data.lexical_item_details_source.api"
}

dependencies {
    api(project(":domain:model"))
}
