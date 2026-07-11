plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

android {
    namespace = "mx.camaronpirata.campaigns"
    compileSdk = 36
    defaultConfig {
        applicationId = "mx.camaronpirata.campaigns"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.2.0"
    }
    buildFeatures { buildConfig = true }
}
