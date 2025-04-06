import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecraftforge.gradle") version "[6.0.16,6.2)"
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
}

version = "1.17.0"
group = "thedarkcolour.kotlinforforge"
base.archivesName.set("kotlinforforge")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

repositories {
    mavenCentral()
}

val minecraft by configurations
val kotlinVersion = "2.0.21"
val coroutinesVersion = "1.10.1"
val annotationsVersion = "26.0.2"
val serializationVersion = "1.8.1"

dependencies {
    minecraft("net.minecraftforge:forge:1.16.5-36.2.42")

    api(group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version = kotlinVersion)
    api(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk7", version = kotlinVersion)
    api(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = kotlinVersion)
    api(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
    api(group = "org.jetbrains", name = "annotations", version = annotationsVersion)
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutinesVersion)
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core-jvm", version = coroutinesVersion)
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = coroutinesVersion)
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = serializationVersion)
}

// Extension function to configure minecraft
fun net.minecraftforge.gradle.common.util.RunConfig.properties(vararg properties: Pair<String, String>) {
    properties.forEach { (key, value) ->
        property(key, value)
    }
}

// Minecraft configuration
configure<net.minecraftforge.gradle.userdev.UserDevExtension> {
    mappings("official", "1.16.5")

    runs {
        create("client") {
            workingDirectory(project.file("run"))
            properties("forge.logging.console.level" to "debug")
        }

        create("server") {
            workingDirectory(project.file("run/server"))
            properties("forge.logging.console.level" to "debug")
        }
    }
}

tasks {
    build {
        dependsOn("kotlinSourcesJar")
        dependsOn("shadowJar")
    }

    jar {
        manifest {
            attributes(mapOf(
                "FMLModType" to "LANGPROVIDER"
            ))
            attributes(mapOf(
                "Specification-Title" to "Mod Language Provider",
                "Specification-Vendor" to "Forge",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "thedarkcolour",
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
            ), "thedarkcolour/kotlinforforge/")
        }
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("obf")
        dependencies {
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${kotlinVersion}"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}"))
            include(dependency("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}"))
            include(dependency("org.jetbrains:annotations:${annotationsVersion}"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${coroutinesVersion}"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${coroutinesVersion}"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${serializationVersion}"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${serializationVersion}"))
        }
    }

    compileKotlin {
        compilerOptions.configureKotlinOptions()
    }

    compileTestKotlin {
        compilerOptions.configureKotlinOptions()
    }
}

// Kotlin compiler options
fun org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions.configureKotlinOptions() {
    freeCompilerArgs = listOf("-Xexplicit-api=warning", "-Xjvm-default=all")
}
