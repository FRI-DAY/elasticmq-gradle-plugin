package de.friday.gradle.elasticmq

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import org.gradle.testfixtures.ProjectBuilder

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
})
