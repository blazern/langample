import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

open class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure(LibraryExtension::class.java) {
                val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

                it.apply {
                    compileSdk = 36

                    defaultConfig {
                        minSdk = 24
                        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                        consumerProguardFiles("consumer-rules.pro")
                    }
                    buildTypes {
                        release {
                            isMinifyEnabled = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                    }
                    compileOptions {
                        sourceCompatibility = JavaVersion.VERSION_11
                        targetCompatibility = JavaVersion.VERSION_11
                    }
                }

                tasks.withType<KotlinCompile>().configureEach { compileTask ->
                    compileTask.apply {
                        compilerOptions {
                            jvmTarget.set(JvmTarget.JVM_11)
                        }
                    }
                }

                dependencies {
                    add("implementation", libs.findLibrary("androidx-core-ktx").get())
                    add("testImplementation", libs.findLibrary("junit").get())
                    add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
                }
            }
        }
    }
}
