import org.gradle.api.tasks.compile.JavaCompile

dependencies {
    implementation(project(":velocity"))
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.release.set(17)
}
