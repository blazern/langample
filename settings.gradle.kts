pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "langample"
include(":app")
include(":core:ui:theme")
include(":feature:home")
include(":core:ui:searchbar")
include(":core:strings")
include(":feature:search-result")
include(":data:tatoeba")
include(":core:ktor")
include(":data:chatgpt")
include(":domain:model")
include(":data:lexical-item-details-source:api")
include(":data:lexical-item-details-source:tatoeba")
include(":data:lexical-item-details-source:chatgpt")
include(":core:utils")
include(":data:kaikki")
include(":data:lexical-item-details-source:kaikki")
include(":domain:settings")
include(":data:panlex")
include(":data:lexical-item-details-source:panlex")
