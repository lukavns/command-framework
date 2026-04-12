import org.gradle.api.tasks.compile.JavaCompile

dependencies {
    implementation(project(":bukkit"))
    compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.release.set(8)
}
