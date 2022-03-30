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
    maven("https://maven.modmanagermc.net/snapshots") {
        name = "ModManagerMC"
    }
    mavenCentral()
}

val loaderVersion: String by project
val fabricKotlinVersion: String by project
val kotlinVersion: String by project

dependencies {
    implementation("net.fabricmc:fabric-language-kotlin:${fabricKotlinVersion}")
    api("net.fabricmc:fabric-loader:$loaderVersion")
    api("org.apache.httpcomponents:httpclient:4.5.13")
    api("org.apache.logging.log4j:log4j-api:2.17.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
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

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}


publishing {
    repositories {
        maven {
            val releasesRepoUrl = "https://maven.modmanagermc.net/releases"
            val snapshotsRepoUrl = "https://maven.modmanagermc.net/snapshots"
            url = uri(if (project.hasProperty("snapshot")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = System.getenv("MAVEN_NAME")
                password = System.getenv("MAVEN_TOKEN")
            }
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}