plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("fabric-loom")
}

group = "xyz.deathsgun.modmanager"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

val minecraftVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project


dependencies {
    // Only for some specific code like identifiers
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("core-build.info") {
        expand(
            mutableMapOf(
                "version" to version
            )
        )
    }
}


//region Set target to Java 1.8
tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
//endregion

tasks.test {
    useJUnitPlatform()
    workingDir = File("run")
    workingDir.mkdirs()
}