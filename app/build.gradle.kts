plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.fooddiary"
    compileSdk = 34  // ← ИЗМЕНИЛИ с 33 на 34

    defaultConfig {
        applicationId = "com.example.fooddiary"
        minSdk = 21
        targetSdk = 34  // ← ИЗМЕНИЛИ с 33 на 34
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.code.gson:gson:2.8.9")

    val room_version = "2.4.2"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // Gson для сериализации
    implementation("com.google.code.gson:gson:2.8.9")

    // Библиотека для графиков
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


}