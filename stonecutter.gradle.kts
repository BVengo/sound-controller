plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.3-fabric"

stonecutter parameters {
    val (minecraft, loader) = current.project.split('-', limit = 2)
    properties { tags(minecraft, loader) }
    constants { match(loader, "fabric", "neoforge") }
}

tasks.register("build") {
    group = "build"
    description = "Builds and collects jars for every Minecraft version and loader."
    dependsOn(subprojects.map { "${it.path}:buildAndCollect" })
}

tasks.register("buildAll") {
    group = "build"
    description = "Builds and collects jars for every Minecraft version and loader."
    dependsOn(tasks.named("build"))
}

tasks.register<Delete>("clean") {
    group = "build"
    description = "Deletes collected artifacts and all variant build outputs."
    delete(layout.buildDirectory)
    dependsOn(subprojects.map { "${it.path}:clean" })
}
