plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "net.modmanagermc"
version = "1.0.0"

repositories {
    maven("https://maven.fabricmc.net") {
        name = "Fabric"
    }
    mavenCentral()
}

val loaderVersion: String by project
val fabricKotlinVersion: String by project

dependencies {
    implementation("net.fabricmc:fabric-language-kotlin:${fabricKotlinVersion}")
    api("net.fabricmc:fabric-loader:$loaderVersion")
    api("org.apache.httpcomponents:httpclient:4.5.13")
    api("org.apache.logging.log4j:log4j-api:2.17.0")
}

val releaseTarget: String by project
tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "version" to version,
                "fabricKotlinVersion" to fabricKotlinVersion,
            )
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
