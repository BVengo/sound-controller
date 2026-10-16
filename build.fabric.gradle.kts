plugins {
    id("dev.kikugie.loom-back-compat")
}

apply(from = rootProject.file("build.common.gradle.kts"))

dependencies {
    minecraft("com.mojang:minecraft:${stonecutter.current.version}")
    loomx.applyMojangMappings()
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    modImplementation("com.terraformersmc:modmenu:${property("deps.modmenu")}") {
        exclude(module = "fabric-api")
    }
}

repositories {
    maven("https://maven.terraformersmc.com/")
}

loom {
    runConfigs.configureEach {
        runDirectory.set(rootProject.file("run/fabric"))
    }
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
        "fabric_minecraft_version_range" to project.property("mod.mc_compat"),
        "java_version" to project.property("mod.java"),
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") { expand(props) }
    filesMatching("soundcontroller.mixins.json") { expand(props) }
    exclude("META-INF/neoforge.mods.toml")
}
