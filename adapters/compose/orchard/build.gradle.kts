plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
}
group = "dev.h516.orchard"
version = "0.4.0"
android {
    namespace = "dev.h516.orchard"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures { compose = true }
    publishing { singleVariant("release") { withSourcesJar() } }
}
dependencies {
    api(platform("androidx.compose:compose-bom:2026.03.01"))
    api("androidx.compose.ui:ui")
    api("androidx.compose.foundation:foundation")
    api("androidx.compose.material3:material3")
    implementation("io.github.kyant0:backdrop:1.0.6")
    implementation("androidx.activity:activity-compose:1.13.0")
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifactId = "orchard"
                pom {
                    name.set("Orchard Android")
                    description.set("Private Apple-inspired Android Compose design system")
                }
            }
        }
    }
}
