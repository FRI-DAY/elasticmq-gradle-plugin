import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "de.friday"
version = "1.0.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.2.70"
    id("java-gradle-plugin")
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC14"
    id("com.github.ben-manes.versions") version "0.21.0"
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

repositories {
    jcenter()
}

dependencies {
    detekt("io.gitlab.arturbosch.detekt:detekt-formatting:1.0.0-RC14")
    detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.0.0-RC14")
    implementation(kotlin("stdlib", "1.2.70"))
    implementation("com.amazonaws:aws-java-sdk-sqs:1.11.519")
    implementation("org.elasticmq:elasticmq-rest-sqs_2.12:0.14.5")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")
}

gradlePlugin {
    plugins {
        create("elasticmq") {
            id = "de.friday.elasticmq"
            implementationClass = "de.friday.gradle.elasticmq.ElasticMqPlugin"
        }
    }
}

detekt {
    failFast = true
    input = files("$projectDir/src")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.allWarningsAsErrors = true
}

tasks.getByName("check") {
    dependsOn(tasks.withType<Detekt>())
}

tasks.withType<ShadowJar> {
    mergeServiceFiles("*.conf")
}

apply {
    from("build-it.gradle.kts")
}
