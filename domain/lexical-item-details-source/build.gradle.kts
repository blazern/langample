plugins {
    id("blazern.langample.plugin.library")
}

android {
    namespace = "blazern.langample.domain.lexical_item_details_source"
}

dependencies {
    api(project(":domain:model"))
}
