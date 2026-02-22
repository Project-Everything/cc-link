import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
    id("io.freefair.lombok") version "9.2.0"
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
}

group = "net.cc"
version = "2.0.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.pkg.github.com/project-everything/cc-core") {
            credentials {
                username = System.getenv("REPOSITORY_USER")
                password = System.getenv("REPOSITORY_TOKEN")
            }
        }
    }
}

dependencies {
    implementation("org.spongepowered:configurate-hocon:4.3.0-SNAPSHOT")

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.cc:core:2.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}

paper {
    name = "cc-link"
    main = "net.cc.link.LinkPlugin"
    apiVersion = "1.21.11"
    version = project.version.toString()
    website = "https://creative-central.net"
    authors = listOf("SpektrSoyuz")
    foliaSupported = false

    serverDependencies {
        register("cc-core") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
    }
}