package de.friday.gradle.elasticmq

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import org.gradle.testfixtures.ProjectBuilder

class QueueConfigurationTest: BehaviorSpec({

    Given("A QueueConfiguration instance") {
        val project = ProjectBuilder.builder().build()
        val queueConfiguration = QueueConfiguration(project, "Master Blaster")

        When("The lazy Attributes are set") {
            queueConfiguration.attributesProperty.set(mutableMapOf(
                    "Rednex" to "Cotton Eye Joe"
            ))

            Then("The public Attributes should have the set value") {
                queueConfiguration.attributes["Rednex"] shouldBe "Cotton Eye Joe"
            }
        }

        When("The public Attributes are set") {
            queueConfiguration.attributes = mapOf(
                    "Vanilla Ice" to "Ice Ice Baby"
            )

            Then("The lazy Attributes should have the set value") {
                queueConfiguration.attributesProperty.get().let {
                    it["Vanilla Ice"] shouldBe "Ice Ice Baby"
                }
            }
        }

        When("The attribute function is used") {
            queueConfiguration.attribute("Sir Mix-A-Lot", "Baby Got Back")

            Then("The new attribute should be present") {
                queueConfiguration.attributes["Sir Mix-A-Lot"] shouldBe "Baby Got Back"
            }
        }
    }
})
