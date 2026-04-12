import org.gradle.api.tasks.compile.JavaCompile

dependencies {
    api(project(":core"))
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.release.set(17)
}
