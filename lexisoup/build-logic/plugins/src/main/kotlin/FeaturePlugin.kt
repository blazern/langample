import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FeaturePlugin : LibraryPlugin() {
    override fun apply(target: Project) = with(target) {
        super.apply(target)

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                val commonMain = getByName("commonMain")
                commonMain.dependencies {
                    implementation(project(":core:ui:strings"))
                }
            }
        }
    }
}
