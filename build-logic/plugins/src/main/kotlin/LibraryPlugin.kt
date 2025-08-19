import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.project

open class LibraryPlugin : CorePlugin() {
    override fun apply(target: Project) {
        super.apply(target)

        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            with(pluginManager) {
                apply(libs.findPlugin("android-library").get().get().pluginId)
                apply(libs.findPlugin("kotlin-android").get().get().pluginId)
                apply(libs.findPlugin("ksp").get().get().pluginId)
            }

            extensions.configure(LibraryExtension::class.java) {
                dependencies {
                    add("implementation", libs.findLibrary("androidx-core-ktx").get())
                    add("implementation", libs.findLibrary("koin-core").get())
                    add("implementation", libs.findLibrary("arrow-core").get())
                    add("implementation", project(":core:logging"))
                    add("testImplementation", libs.findLibrary("junit").get())
                    add("testImplementation", libs.findLibrary("ktor-client-mock").get())
                    add("testImplementation", libs.findLibrary("mockk").get())
                    add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
                    add("testImplementation", kotlin("test"))
                    add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
                }
            }
        }
    }
}
