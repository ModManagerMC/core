pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = java.net.URI.create("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
    }
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("fabric-loom") version "0.7-SNAPSHOT"
    }
}
rootProject.name = "core"

