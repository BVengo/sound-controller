plugins {
    id("dev.bvengo.modkit.target")
    `maven-publish`
}

val minecraftVersion = project.name.substringBeforeLast('-')
val loader = project.name.substringAfterLast('-')
val upperMinecraftVersion = mapOf("26.1" to "26.2", "26.2" to "26.3")[minecraftVersion]
val minecraftRange = if (loader == "fabric") {
    ">=$minecraftVersion" + (upperMinecraftVersion?.let { " <$it" } ?: "")
} else {
    "[$minecraftVersion,${upperMinecraftVersion ?: ""})"
}

modkit {
    licenceFile.set(rootProject.layout.projectDirectory.file("LICENSE.md"))
    metadataProperties.put("${loader}_minecraft_version_range", minecraftRange)
}

// Keep ModMenu in the Maven runtime dependencies after moving it to client sources.
if (loader == "fabric") {
    configurations.runtimeOnly {
        extendsFrom(configurations["clientImplementation"])
    }
}

// Annotations were compile-only on NeoForge in the original build.
if (loader == "neoforge") {
    dependencies {
        compileOnly("org.jetbrains:annotations:26.1.0")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "soundcontroller-$loader"
            from(components["java"])
        }
    }
}
