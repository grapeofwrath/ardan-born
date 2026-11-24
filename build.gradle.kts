import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "dev.grapeofwrath"
version = "0.13.0-SNAPSHOT"

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

fun RunServer.addTestPlugins() = downloadPlugins {
    modrinth("origins-reborn", originsRebornVersion)
    // Optional addons whose powers our origins use; same versions as the live server.
    modrinth("origins-fantasy", "1.1.14")
    modrinth("origins-magic", "1.0.18")
    modrinth("origins-mobs", "1.0.41")
    modrinth("origins-monsters", "1.2.3")
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 25
    }

    processResources {
        filteringCharset = "UTF-8"
        // For the hash of the resource pack; players get it from the docs site.
        from("docs/static/pack") { into("pack") }
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
        addTestPlugins()
    }

    // Server for Claude's own playtests, in ./run-test. Start it with tools/playtest/server.
    register<RunServer>("runTestServer") {
        group = "run paper"
        description = "Runs the loopback-only playtest server in ./run-test."
        minecraftVersion(minecraftVersion)
        runDirectory = layout.projectDirectory.dir("run-test")
        pluginJars(jar.flatMap { it.archiveFile })
        jvmArgs("-Dcom.mojang.eula.agree=true", "-Xmx1536m")
        addTestPlugins()
    }
}
