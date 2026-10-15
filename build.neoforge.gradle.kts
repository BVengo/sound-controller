plugins {
    id("net.neoforged.moddev") version "2.0.147"
}

apply(from = rootProject.file("build.common.gradle.kts"))

neoForge {
    version = property("deps.neoforge").toString()

    runs {
        register("client") {
            client()
            gameDirectory = rootProject.file("run/neoforge")
        }
    }

    mods {
        register("soundcontroller") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:${property("deps.annotations")}")
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "neoforge_minecraft_version_range" to project.property("mod.mc_compat"),
        "neoforge_version" to project.property("deps.neoforge"),
    )
    inputs.properties(props)
    filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    exclude("fabric.mod.json")
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}
