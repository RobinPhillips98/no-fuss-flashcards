plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "io.github.robinphillips98.nofussflashcards"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "io.github.robinphillips98.nofussflashcards"
        minSdk = 24
        targetSdk = 37
        versionCode = 3
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    val nav_version = "2.9.8"
    val room_version = "2.8.4"
    val coil_version = "2.7.0"

    // Navigation
    implementation("androidx.navigation:navigation-compose:${nav_version}")

    // Material Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Room
    implementation("androidx.room:room-runtime:${room_version}")
    ksp("androidx.room:room-compiler:${room_version}")
    implementation("androidx.room:room-ktx:${room_version}")

    // Coil
    implementation("io.coil-kt:coil-compose:${coil_version}")

    implementation("androidx.datastore:datastore-preferences:1.2.1")
}