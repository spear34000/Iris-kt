plugins {
    kotlin("plugin.serialization") version "1.9.24"
    id("com.android.application") version "8.1.1"
    id("org.jetbrains.kotlin.android") version "1.9.24"
}

android {
    namespace = "com.spear.iriskt"
    compileSdk = 34

    repositories {
        google()
        mavenCentral()
    }

    defaultConfig {
        applicationId = "com.spear.iriskt"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.9.24")
    // Ktor Client
    implementation("io.ktor:ktor-client-core:2.3.9")
    implementation("io.ktor:ktor-client-cio:2.3.9")
    implementation("io.ktor:ktor-client-websockets:2.3.9")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.9")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
    
    // Ktor Server (for HTTP/Webhook mode)
    implementation("io.ktor:ktor-server-core:2.3.9")
    implementation("io.ktor:ktor-server-cio:2.3.9")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.9")
    
    // Logging
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    // Android dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.preference:preference-ktx:1.2.1")
}

kotlin {
    jvmToolchain(17)
}

group = "com.spear.iriskt"
version = "0.1.0"
