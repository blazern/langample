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
        maven {
            // You can find the maven URL for other artifacts (e.g. KMP, METALAVA) on their
            // build pages.
            url = uri("https://androidx.dev/snapshots/builds/13511472/artifacts/repository")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://androidx.dev/snapshots/builds/13511472/artifacts/repository")
        }
    }
}

rootProject.name = "langample"
include(":app")
include(":core:ui:theme")
include(":feature:home")
include(":core:ui:searchbar")
include(":core:strings")
include(":feature:search_result")
include(":data:tatoeba")
include(":core:ktor")
include(":data:chatgpt")
include(":domain:model")
