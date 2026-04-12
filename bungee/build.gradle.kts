import org.gradle.api.tasks.compile.JavaCompile

dependencies {
    api(project(":core"))
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.release.set(8)
}
