
import java.util.Properties

plugins {
    id("com.android.application")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun secret(name: String): String {
    val value = localProperties.getProperty(name).orEmpty()
    return value.replace("\\", "\\\\").replace("\"", "\\\"")
}

android {
    namespace = "bd.naim.smsrelay"
    compileSdk = 37

    defaultConfig {
        applicationId = "bd.naim.smsrelay"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "TELEGRAM_BOT_TOKEN", "\"${secret("TELEGRAM_BOT_TOKEN")}\"")
        buildConfigField("String", "TELEGRAM_CHAT_ID", "\"${secret("TELEGRAM_CHAT_ID")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
}
