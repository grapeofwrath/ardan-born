plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "dev.grapeofwrath"
version = "0.1.0-SNAPSHOT"

val paperVersion = "26.3.build.256-stable"
val minecraftVersion = "26.3"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
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
    }
}
