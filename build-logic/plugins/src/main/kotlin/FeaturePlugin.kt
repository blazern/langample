import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.project

class FeaturePlugin : LibraryPlugin() {
    override fun apply(target: Project) {
        super.apply(target)

        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure(LibraryExtension::class.java) {
                val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

                it.apply {
                    buildFeatures {
                        compose = true
                    }
                }

                dependencies {
                    add("implementation", libs.findLibrary("androidx-appcompat").get())
                    add("implementation", libs.findLibrary("material").get())
                    add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
                    add("implementation", libs.findLibrary("androidx-material3").get())
                    add("implementation", libs.findLibrary("koin-android").get())
                    add("implementation", libs.findLibrary("koin-android-compose-viewmodel").get())

                    add("implementation", project(":core:strings"))

                    val composeBom = libs.findLibrary("androidx-compose-bom").get()
                    add("implementation", platform(composeBom))

                    add("debugImplementation", libs.findLibrary("androidx-ui-tooling").get())
                }
            }
        }
    }
}
