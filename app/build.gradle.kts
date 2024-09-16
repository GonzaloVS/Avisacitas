plugins {
    id("com.android.application")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.gvs.avisacitas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gvs.avisacitas"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
//    implementation("androidx.navigation:navigation-fragment:2.8.0")
//    implementation("androidx.navigation:navigation-ui 2.8.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3'")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    implementation("androidx.annotation:annotation:1.8.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Scalable Size Unit (support for different screen sizes)
    implementation("com.intuit.ssp:ssp-android:1.1.1") //solo para textos
    implementation("com.intuit.sdp:sdp-android:1.1.1") //cualquier elemento, excepto textos
    //Rounded ImageView
    implementation("com.makeramen:roundedimageview:2.3.0")
    //Lib phone number google
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.34")

    //Google play services auth
    implementation ("com.google.android.gms:play-services-auth:21.2.0")


}