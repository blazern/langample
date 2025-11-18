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
    service("langample") {
        packageName.set("blazern.langample.graphql.model")
        introspection {
            endpointUrl.set("https://blazern.me/langample/graphql")
            schemaFile.set(file("src/commonMain/graphql/schema.graphqls"))
        }
    }
}
