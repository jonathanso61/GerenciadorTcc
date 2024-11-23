// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()  // Repositório do Google
        mavenCentral()  // Repositório Maven Central
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.3")
        classpath("com.google.gms:google-services:4.3.15")  // Plugin do Google Services
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    }
}

allprojects {

}
