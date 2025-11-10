plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.24")
    
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
    
    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.spear.iriskt"
            artifactId = "iris-kt"
            version = "0.1.0"
            
            from(components["java"])
            
            pom {
                name.set("Iris-kt")
                description.set("Kotlin implementation of irispy-client for KakaoTalk bot development")
                url.set("https://github.com/spear34000/Iris-kt")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("spear34000")
                        name.set("Spear")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/spear34000/Iris-kt.git")
                    developerConnection.set("scm:git:ssh://github.com/spear34000/Iris-kt.git")
                    url.set("https://github.com/spear34000/Iris-kt")
                }
            }
        }
    }
}

group = "com.spear.iriskt"
version = "0.1.0"
