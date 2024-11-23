plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Plugin para serviços do Google

}

android {
    namespace = "com.ifmg.gerenciadordetarefas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ifmg.gerenciadordetarefas"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // Dependências do Android
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Dependências para integração com APIs do Google
    implementation("com.google.android.gms:play-services-auth:20.6.0") // Autenticação Google

    // API Gmail e cliente HTTP
    implementation("com.google.apis:google-api-services-gmail:v1-rev20240520-2.0.0") // Gmail API
    implementation("com.google.api-client:google-api-client:1.33.2") // Cliente Google API
    implementation("com.google.http-client:google-http-client-android:1.42.2") // Cliente HTTP
    implementation("com.google.http-client:google-http-client-gson:1.42.2") // Cliente HTTP com suporte a JSON
    implementation ("com.google.api-client:google-api-client-android:1.35.0")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation ("com.google.code.gson:gson:2.8.9")
    // Biblioteca HTTP para chamadas REST
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")


    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


}
