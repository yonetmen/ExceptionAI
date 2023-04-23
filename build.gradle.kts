plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.ksmgl"
version = "1.0.1"

repositories {
    mavenCentral()
}

intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.9.2")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

tasks.jar {
    manifest {
        attributes(
                "Plugin-Version" to project.version,
                "Plugin-Vendor" to "Kasim Gul",
                "Implementation-Vendor" to "Kasim Gul"
        )
    }
    from("src/main/resources") {
        include("META-INF/plugin.xml")
    }
}


tasks.test {
    useJUnitPlatform()
}
