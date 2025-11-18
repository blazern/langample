rootProject.name = "LexiSoup"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")

include(":a-template-kmp-common")

include(":core:ui:strings")
include(":core:ui:theme")
include(":core:ui:components")
include(":core:utils")
include(":core:logging")
include(":core:ktor")
include(":domain:model")
include(":domain:settings")
include(":feature:home")
include(":feature:search-results")
include(":data:kaikki")
include(":data:lexisoup-graphql")
include(":data:lexical-item-details-source:aggregation")
include(":data:lexical-item-details-source:api")
include(":data:lexical-item-details-source:chatgpt")
include(":data:lexical-item-details-source:kaikki")
include(":data:lexical-item-details-source:utils:cache")
include(":data:lexical-item-details-source:utils:examples-tools")
