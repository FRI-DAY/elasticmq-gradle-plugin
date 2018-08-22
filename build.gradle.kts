import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.gitlab.arturbosch.detekt.DetektCheckTask
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.OutputStream

group = "de.friday"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.2.61"
    id("java-gradle-plugin")
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC8"
    id("com.github.ben-manes.versions") version "0.20.0"
    id("com.github.johnrengelman.shadow") version "2.0.4"
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

tasks.withType<ShadowJar>() {
    mergeServiceFiles("*.conf")
}

val copyIntegrationTestGroovy = task("copyIntegrationTestGroovy", Copy::class) {
    val shadowJar = tasks.getByName("shadowJar")
    dependsOn(shadowJar)
    from(shadowJar.outputs) {
        rename("elasticmq-gradle-plugin.*.jar", "libs/elasticmq-gradle-plugin.jar")
    }

    from(projectDir) {
        include("gradle/**")
        include("gradlew")
        include("gradlew.bat")
    }

    from(file("integration-test"))
    into("$buildDir/integrationTests/groovy-dsl")
}

val integrationTestGroovy = task("integrationTestGroovy", Exec::class) {
    dependsOn(copyIntegrationTestGroovy)
    workingDir = copyIntegrationTestGroovy.destinationDir
    standardOutput = object: OutputStream() {
        override fun write(ignored: Int) { }
    }

    args("build")
    if (OperatingSystem.current().isWindows()) {
        commandLine("cmd", "/c", "gradlew.bat")
    } else {
        commandLine("./gradlew")
    }
}

tasks.getByName("check") {
    dependsOn(tasks.withType<DetektCheckTask>())
    dependsOn(integrationTestGroovy)
}
