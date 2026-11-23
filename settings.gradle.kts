pluginManagement {
    providers.gradleProperty("modkit.includeBuild").orNull?.let { path ->
        includeBuild(path)
    }
    repositories {
        providers.gradleProperty("modkit.repository").orNull?.let { path ->
            maven { url = uri(path) }
        }
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.bvengo.modkit.settings") version "0.1.0"
}

rootProject.name = "sound-controller"
