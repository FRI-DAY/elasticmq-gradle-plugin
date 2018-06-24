package de.friday.gradle.elasticmq

import io.kotlintest.should
import io.kotlintest.specs.BehaviorSpec
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files

class PluginUsageTest: BehaviorSpec({

    Given("A configured Gradle project (Groovy DSL)") {
        val projectDir = Files.createTempDirectory("")
        val buildScript = projectDir.resolve("build.gradle").toFile()
        buildScript.writeText("""
            plugins {
                id 'de.friday.elasticmq'
            }

            elasticmq {
                local {
                    contextPath = "Mr. President"
                    protocol = "Vengaboys"
                    host = "Eiffel 65"
                    port = -1

                    queues {
                        sample {
                            attributes = [ Technotronic: 'Pump Up The Jam' ]
                            attribute 'MC Hammer', "U Can't Touch This"
                        }
                    }
                }
            }
        """.trimIndent())


        When("The build file is executed") {
            val result = GradleRunner.create()
                    .withProjectDir(projectDir.toFile())
                    .withPluginClasspath()
                    .withArguments("--stacktrace")
                    .build()

            Then("The build should be successful") {
                result.output should {
                    it.contains("BUILD SUCCESSFUL")
                }
            }
        }
    }

    Given("A configured Gradle project (Kotlin DSL)") {
        val projectDir = Files.createTempDirectory("")
        val buildScript = projectDir.resolve("build.gradle.kts").toFile()
        buildScript.writeText("""
            plugins {
                id("de.friday.elasticmq")
            }

            elasticmq {
                create("local") {
                    contextPath = "Gigi D'Agostino"
                    protocol = "Dr. Alban"
                    host = "DJ Bobo"
                    port = -1

                    queues {
                        create("sample") {
                            attributes = mapOf("Los Del Rio" to "Macarena")
                            attribute("Lou Bega", "Mambo No. 5 (a Little Bit of...)")
                        }
                    }
                }
            }
        """.trimIndent())


        When("The build file is executed") {
            val result = GradleRunner.create()
                    .withProjectDir(projectDir.toFile())
                    .withPluginClasspath()
                    .withArguments("--stacktrace")
                    .build()

            Then("The build should be successful") {
                result.output should {
                    it.contains("BUILD SUCCESSFUL")
                }
            }
        }
    }
})
