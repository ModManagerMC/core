@file:Suppress("GradlePackageVersionRange")

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("maven-publish")
}

val libVersion: String by project

group = "net.modmanagermc"
version = if(project.hasProperty("snapshot")) "$libVersion-SNAPSHOT" else libVersion

repositories {
    maven("https://maven.fabricmc.net") {
        name = "Fabric"
    }
    mavenCentral()
}

val loaderVersion: String by project
val fabricKotlinVersion: String by project
val kotlinVersion: String by project

dependencies {
    local("net.fabricmc:fabric-language-kotlin:${fabricKotlinVersion}")
    local("net.fabricmc:fabric-loader:$loaderVersion")
    local("org.apache.httpcomponents:httpclient:4.5.13")
    local("org.apache.logging.log4j:log4j-api:2.17.1")

    testImplementation("org.apache.logging.log4j:log4j-core:2.17.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

fun DependencyHandler.local(dependencyNotation: Any) {
    compileOnly(dependencyNotation)
    testImplementation(dependencyNotation)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks {
    test {
        File("run").mkdirs()
        workingDir("run")
    }
    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version,
                    "fabricKotlinVersion" to fabricKotlinVersion,
                )
            )
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ModManagerMC/core")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}