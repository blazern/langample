plugins {
    id("blazern.langample.plugin.library")
    alias(libs.plugins.apollo)
}

android {
    namespace = "blazern.langample.data.langample.graphql"
}

dependencies {
    api(libs.apollo.runtime)
    implementation(libs.ktor.client.core)
    implementation(libs.apollo.engine.ktor)
    implementation(project(":core:ktor"))
}

apollo {
    service("langample") {
        packageName.set("blazern.langample.graphql.model")
        introspection {
            endpointUrl.set("https://blazern.me/langample/graphql")
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}
