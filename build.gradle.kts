// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    id("com.android.application") version "8.2.1" apply false
//    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
//    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("androidx.room") version "2.6.0" apply false

    id("com.mikepenz.aboutlibraries.plugin") version "10.10.0" apply false

    id("com.chaquo.python") version "15.0.1" apply false
}
buildscript {

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.9.4")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.