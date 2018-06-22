group = "de.friday"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.2.50"
    id("java-gradle-plugin")
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib", "1.2.50"))
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.7")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
