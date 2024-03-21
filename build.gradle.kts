plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.gladkowski"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.quickfixj:quickfixj-all:2.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

tasks.jar {
    // Optional: Define manifest attributes directly (overrides Shadow merge)
    manifest {
        attributes(
            "Manifest-Version" to "1.0", // Customize if needed
            "Implementation-Title" to project.name,
            "Main-Class" to "org.gladkowski.MainKt",
        )
    }
}

tasks.withType<Jar>().configureEach {
    // Optional: Exclude unnecessary files from Shadow JAR (adjust paths as needed)
    exclude("**/META-INF/*.RSA", "**/META-INF/*.SF")
}