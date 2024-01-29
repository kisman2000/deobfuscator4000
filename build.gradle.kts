plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "ksmn"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.bitbucket.mstrobel:procyon-compilertools:0.6.0")
    implementation("io.github.spair:imgui-java-app:1.86.11")
    implementation("tv.wunderbox:nativefiledialog:1.0.3")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.jar {
    manifest.attributes(
        "Main-Class" to "MainKt"
    )

    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    configurations["compileClasspath"].forEach { file : File ->
        from(zipTree(file.absoluteFile))
    }
}