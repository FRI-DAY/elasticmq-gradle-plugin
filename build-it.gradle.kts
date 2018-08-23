import org.gradle.internal.os.OperatingSystem
import java.io.OutputStream

val integrationTestGroovy = createIntegrationTestTask("groovy", "build.gradle")
val integrationTestKotlin = createIntegrationTestTask("kotlin", "build.gradle.kts")

tasks.getByName("check") {
    dependsOn(integrationTestGroovy)
    dependsOn(integrationTestKotlin)
}

fun createIntegrationTestTask(dsl: String, script: String): Task {
    val prepareIntegrationTest = task("prepareIntegrationTest${dsl.capitalize()}", Copy::class) {
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

        from(file("integration-test")) {
            rename("_$script", script)
        }

        into("$buildDir/integrationTests/${dsl.toLowerCase()}-dsl")
    }

    return task("integrationTest${dsl.capitalize()}", Exec::class) {
        dependsOn(prepareIntegrationTest)
        workingDir = prepareIntegrationTest.destinationDir
        standardOutput = object : OutputStream() {
            override fun write(ignored: Int) {}
        }

        if (OperatingSystem.current().isWindows()) {
            commandLine("cmd", "/c", "gradlew.bat")
        } else {
            commandLine("./gradlew")
        }

        if (System.getenv("CI") == "true") {
            args("build", "--no-daemon")
        } else {
            args("build")
        }

        mustRunAfter(tasks.getByName("test"))
    }
}
