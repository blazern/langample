plugins {
    id("blazern.lexisoup.plugin.library")
    alias(libs.plugins.apollo)
}

kotlin {
    androidLibrary {
        namespace = "blazern.lexisoup.data.lexisoup.graphql"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.apollo.runtime)
                implementation(libs.ktor.client.core)
                implementation(libs.apollo.engine.ktor)
                implementation(project(":core:ktor"))
            }
        }
    }
}

apollo {
    service("lexisoup") {
        packageName.set("blazern.lexisoup.graphql.model")
    }
}
