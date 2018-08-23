package de.friday.gradle.elasticmq

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.should
import io.kotlintest.specs.WordSpec
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files

class BuildScriptTest: WordSpec({

    "A configured Groovy DSL builscript" should {
        "Lead to a successful build" {
            val projectDir = Files.createTempDirectory("")
            val buildScript = projectDir.resolve("build.gradle").toFile()
            buildScript.writeText("""
                plugins {
                    id 'de.friday.elasticmq'
                }

                elasticmq {
                    instances {
                        local {
                            protocol = 'http'
                            host = 'localhost'
                            port = 9324
                            contextPath = 'path'

                            limits = 'relaxed'

                            queues {
                                sample {
                                    attributes = [ DelaySeconds: '5' ]
                                    attribute 'VisibilityTimeout', '6000'
                                }
                            }
                        }
                    }
                }

                tasks.create('example') {
                    dependsOn 'startLocalElasticMq'
                    finalizedBy 'stopLocalElasticMq'
                }
            """.trimIndent())

            val result = GradleRunner.create()
                    .withProjectDir(projectDir.toFile())
                    .withPluginClasspath()
                    .withArguments("example", "--stacktrace")
                    .build()

            result.task(":startLocalElasticMq") should {
                it != null && it.outcome == TaskOutcome.SUCCESS
            }
            result.task(":stopLocalElasticMq") should {
                it != null && it.outcome == TaskOutcome.SUCCESS
            }
            result.output.shouldContain("Starting ElasticMQ local server instance")
            result.output.shouldContain("Stopping ElasticMQ local server instance")
        }
    }

    "A configured Kotlin DSL builscript" should {
        "Lead to a successful build" {
            val projectDir = Files.createTempDirectory("")
            val buildScript = projectDir.resolve("build.gradle.kts").toFile()
            buildScript.writeText("""
                plugins {
                    id("de.friday.elasticmq")
                }

                elasticmq {
                    instances {
                        create("local") {
                            protocol = "http"
                            host = "localhost"
                            port = 9324
                            contextPath = "path"

                            limits = "relaxed"

                            queues {
                                create("sample") {
                                    attributes = mapOf("DelaySeconds" to "5")
                                    attribute("VisibilityTimeout", "6000")
                                }
                            }
                        }
                    }
                }

                tasks.create("example") {
                    dependsOn("startLocalElasticMq")
                    finalizedBy("stopLocalElasticMq")
                }
            """.trimIndent())

            val result = GradleRunner.create()
                    .withProjectDir(projectDir.toFile())
                    .withPluginClasspath()
                    .withArguments("example", "--stacktrace")
                    .build()

            result.task(":startLocalElasticMq") should {
                it != null && it.outcome == TaskOutcome.SUCCESS
            }
            result.task(":stopLocalElasticMq") should {
                it != null && it.outcome == TaskOutcome.SUCCESS
            }
            result.output.shouldContain("Starting ElasticMQ local server instance")
            result.output.shouldContain("Stopping ElasticMQ local server instance")
        }
    }

    "A builscript that doesn't stop the server explicitly" should {
        "Stop the server implicitly after the build" {
            val projectDir = Files.createTempDirectory("")
            val buildScript = projectDir.resolve("build.gradle").toFile()
            buildScript.writeText("""
                plugins {
                    id 'de.friday.elasticmq'
                }

                elasticmq {
                    instances {
                        local {
                            queues {
                                sample { }
                            }
                        }
                    }
                }
            """.trimIndent())

            val result = GradleRunner.create()
                    .withProjectDir(projectDir.toFile())
                    .withPluginClasspath()
                    .withArguments("startLocalElasticMq", "--stacktrace")
                    .build()

            result.task(":startLocalElasticMq") should {
                it != null && it.outcome == TaskOutcome.SUCCESS
            }
            result.task(":stopLocalElasticMq") should {
                it != null && it.outcome == TaskOutcome.SKIPPED
            }
            result.output.shouldContain("Starting ElasticMQ local server instance")
            result.output.shouldContain("Stopping ElasticMQ local server instance")
        }
    }
})
