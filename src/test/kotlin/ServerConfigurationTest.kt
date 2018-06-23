package de.friday.gradle.elasticmq

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files

class ServerConfigurationTest: BehaviorSpec({

    Given("A ServerConfiguration instance") {
        val project = ProjectBuilder.builder().build()
        val serverConfiguration = ServerConfiguration(project, "Culture Beat")

        When("The lazy Context Path is set") {
            serverConfiguration.contextPathProperty.set("Darude")

            Then("The public Context Path should have the set value") {
                serverConfiguration.contextPath shouldBe "Darude"
            }
        }

        When("The public Context Path is set") {
            serverConfiguration.contextPath = "E-Rotic"

            Then("The lazy Context Path should have the set value") {
                serverConfiguration.contextPathProperty.get() shouldBe "E-Rotic"
            }
        }

        When("The lazy Protocol is set") {
            serverConfiguration.protocolProperty.set("Corona")

            Then("The public Protocol should have the set value") {
                serverConfiguration.protocol shouldBe "Corona"
            }
        }

        When("The public Protocol is set") {
            serverConfiguration.protocol = "Mr. Scruff"

            Then("The lazy Protocol should have the set value") {
                serverConfiguration.protocolProperty.get() shouldBe "Mr. Scruff"
            }
        }

        When("The lazy Host is set") {
            serverConfiguration.hostProperty.set("Sin With Sebastian")

            Then("The public Host should have the set value") {
                serverConfiguration.host shouldBe "Sin With Sebastian"
            }
        }

        When("The public Host is set") {
            serverConfiguration.host = "Haddaway"

            Then("The lazy Host should have the set value") {
                serverConfiguration.hostProperty.get() shouldBe "Haddaway"
            }
        }

        When("The lazy Port is set") {
            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
            serverConfiguration.portProperty.set(42 as? Integer)

            Then("The public Port should have the set value") {
                serverConfiguration.port shouldBe 42
            }
        }

        When("The public Port is set") {
            serverConfiguration.port = 101010

            Then("The lazy Port should have the set value") {
                serverConfiguration.portProperty.get() shouldBe 101010
            }
        }
    }

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
