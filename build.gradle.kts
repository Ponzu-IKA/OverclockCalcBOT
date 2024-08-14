plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "ponzu_ika.occalc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("net.dv8tion","JDA","5.0.0-beta.24")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("ponzu_ika.occalc.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(16)
}
