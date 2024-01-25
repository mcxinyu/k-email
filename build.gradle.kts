plugins {
    kotlin("jvm") version "1.9.22"
    `maven-publish`
}

group = "io.github.mcxinyu"
version = "1.0"

repositories {
    mavenCentral()
}

val sourcesJar by tasks.register<Jar>("sourcesJar") {
    from(kotlin.sourceSets.map {
        it.kotlin.sourceDirectories.asFileTree
    })
    archiveClassifier.set("sources")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("k-email") {
                afterEvaluate { from(components["kotlin"]) }
                artifact(sourcesJar)
            }
        }
        repositories {
            maven {
                name = "XXX"
                url = uri("${project.buildDir}/repo")
            }
        }
    }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}