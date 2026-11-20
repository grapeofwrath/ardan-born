plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "dev.grapeofwrath"
version = "0.1.0-SNAPSHOT"

val paperVersion = "26.3.build.256-stable"
val minecraftVersion = "26.3"
val originsRebornVersion = "2.10.11"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    // Origins-Reborn has no Maven repo of its own; Modrinth serves its releases as Maven artifacts.
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    compileOnly("maven.modrinth:origins-reborn:$originsRebornVersion")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 25
    }

    processResources {
        filteringCharset = "UTF-8"
        val props = mapOf("version" to project.version, "apiVersion" to minecraftVersion)
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    runServer {
        // Downloads Paper and starts a local test server in ./run with the plugin loaded.
        minecraftVersion(minecraftVersion)
        jvmArgs("-Dcom.mojang.eula.agree=true")
        downloadPlugins {
            modrinth("origins-reborn", originsRebornVersion)
        }
    }
}
