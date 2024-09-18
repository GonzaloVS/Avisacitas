// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44")
    }
}

plugins {
    // Define el plugin de Android y otros plugins que uses en los módulos
    id("com.android.application") version "8.1.4" apply false

    // Mantener solo la definición del plugin de Google Services aquí
    id("com.google.gms.google-services") version "4.4.2" apply false
}
