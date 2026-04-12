import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.plugins.JavaPluginExtension

plugins {
    base
}

val jitpackGroup = System.getenv("GROUP")
val jitpackArtifact = System.getenv("ARTIFACT")
val runningOnJitPack = System.getenv("JITPACK") == "true"
val projectGroup = if (runningOnJitPack && !jitpackGroup.isNullOrBlank() && !jitpackArtifact.isNullOrBlank()) {
    "$jitpackGroup.$jitpackArtifact"
} else {
    "me.lukavns"
}

allprojects {
    group = projectGroup
    version = providers.gradleProperty("projectVersion").orElse("0.1.0-SNAPSHOT").get()

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.velocitypowered.com/releases/")
    }
}

subprojects {
    apply(plugin = "java-library")

    if (!project.name.endsWith("-example")) {
        apply(plugin = "maven-publish")
    }

    extensions.configure(JavaPluginExtension::class.java) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType(JavaCompile::class.java).configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters", "-Xlint:all", "-Xlint:-serial", "-Xlint:-classfile", "-Xlint:-processing"))
    }

    tasks.withType(Test::class.java).configureEach {
        useJUnitPlatform()
    }

    if (plugins.hasPlugin("maven-publish")) {
        extensions.configure(PublishingExtension::class.java) {
            publications.create("mavenJava", MavenPublication::class.java) {
                from(components.getByName("java"))
                artifactId = project.name
            }
        }
    }
}
