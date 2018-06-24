package de.friday.gradle.elasticmq

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files

class TaskUsageTest: BehaviorSpec({

    Given("A configured Gradle project (Groovy DSL)") {
        val projectDir = Files.createTempDirectory("")
        val buildScript = projectDir.resolve("build.gradle").toFile()
        buildScript.writeText("""
            import de.friday.gradle.elasticmq.StartElasticMq
            import de.friday.gradle.elasticmq.StopElasticMq

            plugins {
                id 'de.friday.elasticmq'
            }

            elasticmq.create('local')

            tasks.create('startLocalElasticMq', StartElasticMq) {
                serverName = 'local'
            }

            tasks.create('stopLocalElasticMq', StopElasticMq) {
                serverName = 'local'
            }

            tasks.create('testTasks') {
                dependsOn 'startLocalElasticMq'
                finalizedBy 'stopLocalElasticMq'
            }
        """.trimIndent())

        When("The build file is executed") {
            val result = GradleRunner.create()
                    .withProjectDir(projectDir.toFile())
                    .withPluginClasspath()
                    .withArguments("testTasks", "--stacktrace")
                    .build()

            Then("The build should be successful") {
                result.output should {
                    it.contains("BUILD SUCCESSFUL")
                    && it.contains("Starting ElasticMQ local server")
                    && it.contains("Stopping ElasticMQ local server")
                }
                result.task(":startLocalElasticMq")?.outcome shouldBe TaskOutcome.SUCCESS
                result.task(":stopLocalElasticMq")?.outcome shouldBe TaskOutcome.SUCCESS
            }
        }
    }

    Given("A configured Gradle project (Kotlin DSL)") {
        val projectDir = Files.createTempDirectory("")
        val buildScript = projectDir.resolve("build.gradle.kts").toFile()
        buildScript.writeText("""
            import de.friday.gradle.elasticmq.StartElasticMq
            import de.friday.gradle.elasticmq.StopElasticMq

            plugins {
                id("de.friday.elasticmq")
            }

            elasticmq.create("local")

            val startLocalElasticMq =
            tasks.create("startLocalElasticMq", StartElasticMq::class.java) {
                serverName = "local"
            }

            val stopLocalElasticMq =
            tasks.create("stopLocalElasticMq", StopElasticMq::class.java) {
                serverName = "local"
            }

            tasks.create("testTasks") {
                dependsOn(startLocalElasticMq)
                finalizedBy(stopLocalElasticMq)
            }
        """.trimIndent())

        When("The build file is executed") {
            val result = GradleRunner.create()
                    .withProjectDir(projectDir.toFile())
                    .withPluginClasspath()
                    .withArguments("testTasks", "--stacktrace")
                    .build()

            Then("The build should be successful") {
                result.output should {
                    it.contains("BUILD SUCCESSFUL")
                    && it.contains("Starting ElasticMQ local server")
                    && it.contains("Stopping ElasticMQ local server")
                }
                result.task(":startLocalElasticMq")?.outcome shouldBe TaskOutcome.SUCCESS
                result.task(":stopLocalElasticMq")?.outcome shouldBe TaskOutcome.SUCCESS
            }
        }
    }
})
