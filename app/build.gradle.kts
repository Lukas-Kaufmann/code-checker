import java.net.URL

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") apply true
    application
}

tasks.dokkaHtml {
    moduleName = "app"
    dokkaSourceSets.named("main").configure {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl.set(URL("https://github.com/Lukas-Kaufmann/code-checker/tree/main/app/src"))
            remoteLineSuffix.set("#L")
        }
    }

    suppressInheritedMembers.set(true)
}

dependencies {
    implementation("com.github.javaparser:javaparser-core:3.26.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("org.reflections:reflections:0.10.2")

    testImplementation(libs.junit.jupiter.engine)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-framework-api:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(kotlin("reflect"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "at.fhv.lka2.checker.AppKt"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
