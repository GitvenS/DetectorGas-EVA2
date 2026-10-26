plugins {
    alias(libs.plugins.android.application)
}

// El proyecto compila sin Firebase hasta que se agregue google-services.json.
// Al conectar Firebase se aplica el plugin de Google automaticamente.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    namespace = "cl.inacap.app1_firebase"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }
    defaultConfig {
        applicationId = "cl.inacap.detectorgas"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "2.0-demo"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
}
