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

stonecutter {
    replacements.string(current.parsed < "1.21.11") {
        replace(".getIdentifier()", ".getLocation()")
        replace(".identifier()", ".location()")
        replace("Identifier", "ResourceLocation")
    }
    replacements.string(current.parsed < "26.1") {
        replace("GuiGraphicsExtractor", "GuiGraphics")
    }
    replacements.string(current.parsed < "1.21.4") {
        replace("scrollBarX()", "getScrollbarPosition()")
    }
    replacements.string(current.parsed < "1.21.8") {
        replace("setScreenAndShow", "setScreen")
    }
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "neoforge_minecraft_version_range" to project.property("mod.mc_compat"),
        "neoforge_version" to project.property("deps.neoforge"),
        "java_version" to project.property("mod.java"),
    )
    inputs.properties(props)
    filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    filesMatching("soundcontroller.mixins.json") { expand(props) }
    exclude("fabric.mod.json")
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}
