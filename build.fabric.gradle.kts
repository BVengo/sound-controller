plugins {
    id("dev.kikugie.loom-back-compat")
}

apply(from = rootProject.file("build.common.gradle.kts"))

dependencies {
    minecraft("com.mojang:minecraft:${stonecutter.current.version}")
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    implementation("com.terraformersmc:modmenu:${property("deps.modmenu")}") {
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

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "fabric_minecraft_version_range" to project.property("mod.mc_compat"),
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") { expand(props) }
    exclude("META-INF/neoforge.mods.toml")
}
