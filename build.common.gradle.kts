import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.MavenPublication

apply(plugin = "maven-publish")

val minecraftVersion = project.name.substringBeforeLast('-')
val loader = project.name.substringAfterLast('-')
val modId = property("mod.id").toString()
val requiredJava = property("mod.java").toString().toInt()

version = "${property("mod.version")}-mc$minecraftVersion"
group = property("mod.group").toString()

configure<BasePluginExtension> {
    archivesName.set("$modId-$loader")
}

repositories {
    mavenCentral()
}

configure<JavaPluginExtension> {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(requiredJava))
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(requiredJava)
}

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE.md")) {
        rename { "${it}_${modId}-$loader" }
    }
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "$modId-$loader"
            from(components["java"])
        }
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Builds this variant and collects its jars in the root build/libs directory."
    dependsOn(tasks.named("build"))
    from(tasks.named<Jar>("jar").flatMap { it.archiveFile })
    from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs"))
}
