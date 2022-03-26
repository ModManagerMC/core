plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("fabric-loom")
}

group = "net.modmanagermc"
version = "1.0.0"

repositories {
    maven("https://maven.fabricmc.net") {
        name = "Fabric"
    }
    mavenCentral()
}

val minecraftVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val fabricKotlinVersion: String by project

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")

    modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")
    includeMod("net.fabricmc:fabric-language-kotlin:${fabricKotlinVersion}")
}


fun DependencyHandler.includeMod(dep: Any) {
    modImplementation(dep)
    include(dep)
}

fun DependencyHandler.includeApi(dep: Any) {
    api(dep)
    include(dep)
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
    filesMatching("buildInfo.json") {
        expand(
            mutableMapOf(
                "releaseTarget" to minecraftVersion,
            )
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
