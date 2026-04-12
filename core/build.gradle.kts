import org.gradle.api.tasks.compile.JavaCompile

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.release.set(8)
}
