import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.androidLibrary
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class CorePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        with(pluginManager) {
            apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("androidKotlinMultiplatformLibrary").get().get().pluginId)
            apply(libs.findPlugin("detekt").get().get().pluginId)
        }

        extensions.configure<DetektExtension> {
            buildUponDefaultConfig = true
            config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        }
        tasks.withType(Detekt::class.java).configureEach {
            reports.html.required.set(true)
            reports.md.required.set(true)
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
        }

        extensions.configure<KotlinMultiplatformExtension> {
            @Suppress("UnstableApiUsage")
            androidLibrary {
                compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
                minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()

                withHostTestBuilder {
                }

                experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
            }

            iosX64()
            iosArm64()
            iosSimulatorArm64()

            @Suppress("OPT_IN_USAGE")
            wasmJs {
                browser()
                binaries.library()
            }

            sourceSets.apply {
                val commonMain = getByName("commonMain")
                val commonTest = getByName("commonTest")
                val androidMain = maybeCreate("androidMain")

                commonMain.dependencies {
                    implementation(libs.findLibrary("kotlin-stdlib").get())

                    // TODO: return
//                    libs.findLibrary("kotlinx-coroutines-core").ifPresent { add("implementation", it) }
//                    libs.findLibrary("koin-core").ifPresent { add("implementation", it) }
//                    libs.findLibrary("arrow-core").ifPresent { add("implementation", it) }
                }
                commonTest.dependencies {
                    implementation(libs.findLibrary("kotlin-test").get())
                    implementation(libs.findLibrary("kotlinx-coroutines-test").get())
                }

                androidMain.dependencies {
                    // lightweight Android-only defaults
                    // TODO: return
//                    libs.findLibrary("koin-android").ifPresent { add("implementation", it) }
                }
            }
        }

        // TODO: remove? probably will need for tests
//        dependencies {
//            libs.findLibrary("junit").ifPresent { add("testImplementation", it) }
//        }
    }
}
