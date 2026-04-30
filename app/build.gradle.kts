import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

val supportEmail = (project.findProperty("FUTURE_SELF_SUPPORT_EMAIL") as String?)
    ?: localProperties.getProperty("FUTURE_SELF_SUPPORT_EMAIL")
    ?: ""
val githubPagesBaseUrl = (project.findProperty("FUTURE_SELF_GITHUB_PAGES_BASE_URL") as String?)
    ?: localProperties.getProperty("FUTURE_SELF_GITHUB_PAGES_BASE_URL")
    ?: ""
val normalizedGithubPagesBaseUrl = githubPagesBaseUrl.trim().trimEnd('/')
val privacyPolicyUrl = (project.findProperty("FUTURE_SELF_PRIVACY_URL") as String?)
    ?: localProperties.getProperty("FUTURE_SELF_PRIVACY_URL")
    ?: if (normalizedGithubPagesBaseUrl.isNotBlank()) {
        "$normalizedGithubPagesBaseUrl/privacy-policy.html"
    } else {
        ""
    }
val termsUrl = (project.findProperty("FUTURE_SELF_TERMS_URL") as String?)
    ?: localProperties.getProperty("FUTURE_SELF_TERMS_URL")
    ?: if (normalizedGithubPagesBaseUrl.isNotBlank()) {
        "$normalizedGithubPagesBaseUrl/terms-of-service.html"
    } else {
        ""
    }
val admobAppId = (project.findProperty("ADMOB_APP_ID") as String?)
    ?: localProperties.getProperty("ADMOB_APP_ID")
    ?: "ca-app-pub-3940256099942544~3347511713"
val admobRewardedUnitId = (project.findProperty("ADMOB_REWARDED_AD_UNIT_ID") as String?)
    ?: localProperties.getProperty("ADMOB_REWARDED_AD_UNIT_ID")
    ?: "ca-app-pub-3940256099942544/5224354917"

val releaseRequested = gradle.startParameter.taskNames.any { taskName ->
    taskName.contains("release", ignoreCase = true) ||
        taskName.contains("bundle", ignoreCase = true) ||
        taskName.contains("publish", ignoreCase = true)
}

fun String.isPlaceholderUrl(): Boolean =
    isBlank() || contains("example.com", ignoreCase = true)

fun String.isPlaceholderEmail(): Boolean =
    isBlank() || contains("futureself.app", ignoreCase = true) || contains("example.com", ignoreCase = true)

fun String.isTestAdId(): Boolean =
    this == "ca-app-pub-3940256099942544~3347511713" || this == "ca-app-pub-3940256099942544/5224354917"

if (releaseRequested) {
    require(!privacyPolicyUrl.isPlaceholderUrl()) {
        "Release build blocked: set FUTURE_SELF_PRIVACY_URL to a real public privacy policy URL."
    }
    require(!termsUrl.isPlaceholderUrl()) {
        "Release build blocked: set FUTURE_SELF_TERMS_URL to a real public terms URL."
    }
    require(!supportEmail.isPlaceholderEmail()) {
        "Release build blocked: set FUTURE_SELF_SUPPORT_EMAIL to a real support email."
    }
    require(!admobAppId.isTestAdId()) {
        "Release build blocked: replace test AdMob App ID with the production App ID."
    }
    require(!admobRewardedUnitId.isTestAdId()) {
        "Release build blocked: replace test rewarded ad unit ID with the production ad unit ID."
    }
}
android {
    namespace = "com.already.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.already.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"

        buildConfigField("String", "FUTURE_SELF_SUPPORT_EMAIL", "\"$supportEmail\"")
        buildConfigField("String", "FUTURE_SELF_PRIVACY_URL", "\"$privacyPolicyUrl\"")
        buildConfigField("String", "FUTURE_SELF_TERMS_URL", "\"$termsUrl\"")
        buildConfigField("String", "FUTURE_SELF_ADMOB_APP_ID", "\"$admobAppId\"")
        buildConfigField("String", "FUTURE_SELF_REWARDED_AD_UNIT_ID", "\"$admobRewardedUnitId\"")
        buildConfigField("String", "FUTURE_SELF_PLUS_MONTHLY_ID", "\"future_self_plus_monthly\"")
        buildConfigField("String", "FUTURE_SELF_PLUS_YEARLY_ID", "\"future_self_plus_yearly\"")
        buildConfigField("String", "FUTURE_SELF_LIFETIME_ID", "\"future_self_lifetime\"")
        resValue("string", "admob_app_id", admobAppId)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            java.setSrcDirs(listOf("src/main/java/com/futureself"))
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = if (keystorePropertiesFile.exists()) file(keystoreProperties["storeFile"] as String) else null
            storePassword = keystoreProperties["storePassword"] as String?
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEV_PREMIUM_UNLOCK", "false")
        }
        release {
            buildConfigField("boolean", "DEV_PREMIUM_UNLOCK", "false")
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
    val roomVersion = "2.6.1"

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("com.android.billingclient:billing-ktx:8.0.0")
    implementation("com.google.android.gms:play-services-ads:24.7.0")
    implementation("com.google.android.ump:user-messaging-platform:3.2.0")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.material:material:1.12.0")
    ksp("androidx.room:room-compiler:$roomVersion")

    testImplementation("junit:junit:4.13.2")
    debugImplementation(composeBom)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
