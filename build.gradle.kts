import io.gitlab.arturbosch.detekt.DetektCheckTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "de.friday"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.2.61"
    id("java-gradle-plugin")
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC8"
    id("com.github.ben-manes.versions") version "0.20.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib", "1.2.61"))
    implementation("com.amazonaws:aws-java-sdk-sqs:1.11.391")
    implementation("org.elasticmq:elasticmq-rest-sqs_2.12:0.14.5")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.9")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.allWarningsAsErrors = true
}

tasks.getByName("check").dependsOn(tasks.withType<DetektCheckTask>())

gradlePlugin {
    plugins {
        create("elasticmq") {
            id = "de.friday.elasticmq"
            implementationClass = "de.friday.gradle.elasticmq.ElasticMqPlugin"
        }
    }
}

detekt {
    defaultProfile(Action {
        config = file("detekt.yml")
        input = "src"
    })
}
