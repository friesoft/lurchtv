plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "org.friesoft.lurchtv"
    // Needed for latest androidx snapshot build
    compileSdk = 36

    defaultConfig {
        applicationId = "org.friesoft.lurchtv"
        minSdk = 28
        targetSdk = 36

        // Parse semantic version for robust versionCode calculation
        val versionStr = project.version.toString()
        val versionParts = versionStr.split(".")
        val major = versionParts.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = versionParts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = versionParts.getOrNull(2)?.split("-")?.get(0)?.toIntOrNull() ?: 0
        
        val envVersionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        val isRelease = System.getenv("GITHUB_REF")?.startsWith("refs/tags/v") == true || System.getenv("IS_RELEASE_BUILD") == "true"
        
        // Robust Versioning:
        // Formula: major(7) + minor(5) + patch(3) + build_number(2)
        versionCode = (major * 1000000) + (minor * 10000) + (patch * 100) + (envVersionCode % 100)
        
        // Dynamic version name logic
        var version = versionStr
        
        if (!isRelease) {
             val gitCommit = providers.exec {
                commandLine("git", "rev-parse", "--short", "HEAD")
            }.standardOutput.asText.orNull?.trim() ?: ""
            
             if (gitCommit.isNotEmpty()) {
                version = "$version-$gitCommit"
            }
        }
        
        versionName = version

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val storePath = System.getenv("SIGNING_KEY_STORE_PATH")
            if (!storePath.isNullOrEmpty() && file(storePath).exists()) {
                storeFile = file(storePath)
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null) {
                signingConfig = releaseSigning
            }
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.tooling.preview)

    // extra material icons
    implementation(libs.androidx.material.icons.extended)

    // Material components optimized for TV apps
    implementation(libs.androidx.tv.material)

    // General Material3
    implementation(libs.androidx.material3)

    // ViewModel in Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Coil
    implementation(libs.coil.compose)

    // JSON parser
    implementation(libs.kotlinx.serialization)

    // Media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.hls)

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.paging.common)
    ksp(libs.hilt.compiler)

    // Retrofit for Networking
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    // Compose Previews
    debugImplementation(libs.androidx.compose.ui.tooling)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.register("printVersionName") {
    doLast {
        println(project.the<com.android.build.api.dsl.ApplicationExtension>().defaultConfig.versionName)
    }
}
