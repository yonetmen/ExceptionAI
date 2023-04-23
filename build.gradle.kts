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
    type.set("IC")

    plugins.set(listOf(/* Plugin Dependencies */))
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks {
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

    from({configurations.runtimeClasspath.get().filter {it.name.endsWith("jar") }.map {zipTree(it)} })

    exclude("META-INF/*.kotlin_module")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.RSA")
    exclude("META-INF/*.MF")
    exclude("META-INF/versions/9/module-info.class")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}