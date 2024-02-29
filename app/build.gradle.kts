plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.room")
    id("com.mikepenz.aboutlibraries.plugin")

    id ("com.google.protobuf")
}

android {
    namespace = "io.github.mumu12641"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.mumu12641"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

room {
    schemaDirectory("$projectDir/schemas")
}

protobuf {
    protoc {
        // find latest version number here:
        // https://mvnrepository.com/artifact/com.google.protobuf/protoc
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins{
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    //noinspection GradleDependency
    implementation("androidx.compose.material3:material3-android:1.2.0-beta02")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // icons
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

    // navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // permission
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // lottie files
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // Bluetooth toolkit
    implementation("com.zhzc0x.bluetooth:client-android:1.0.1")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // room
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:2.6.1")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    // glide
    implementation("com.github.skydoves:landscapist-glide:2.3.1")

    // about libraries
    implementation("com.mikepenz:aboutlibraries-core:10.10.0")
    implementation("com.mikepenz:aboutlibraries-compose-m3:10.10.0")

    // timeline
    implementation("io.github.pushpalroy:jetlime:2.1.0")

    // datastore
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.protobuf:protobuf-javalite:3.25.1")
}
