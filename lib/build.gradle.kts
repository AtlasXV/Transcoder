plugins {
    id("com.android.library")
    id("kotlin-android")
    id("io.deepmedia.tools.publisher")
}

apply(plugin = "com.atlasv.android.publishlib")

android {
    setCompileSdkVersion(property("compileSdkVersion") as Int)
    defaultConfig {
        setMinSdkVersion(property("minSdkVersion") as Int)
        setTargetSdkVersion(property("targetSdkVersion") as Int)
        versionCode = 1
        versionName = "0.10.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes["release"].isMinifyEnabled = false
}


dependencies {
    api("com.otaliastudios.opengl:egloo:0.6.0")
    api("androidx.annotation:annotation:1.2.0")

    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("org.mockito:mockito-android:2.28.2")
    testImplementation("junit:junit:4.13.2")
}
