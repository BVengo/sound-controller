pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie" }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("dev.kikugie.loom-back-compat") version "0.4.3"
}

stonecutter {
    create(rootProject) {
        fun minecraft(version: String) {
            for (loader in listOf("fabric", "neoforge")) {
                version("$version-$loader", version).buildscript("build.$loader.gradle.kts")
            }
        }

        minecraft("26.1")
        minecraft("26.2")
        minecraft("26.3")
        vcsVersion = "26.3-fabric"
    }
}

rootProject.name = "sound-controller"
