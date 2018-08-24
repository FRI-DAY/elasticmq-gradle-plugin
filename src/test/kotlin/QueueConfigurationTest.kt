package de.friday.gradle.elasticmq

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder

class QueueConfigurationTest : WordSpec({

    "Queue Configuration" should {
        "Not accept an empty name" {
            shouldThrow<IllegalArgumentException> {
                queueConfiguration(name = "")
            }
        }

        "Not accept a blank name" {
            shouldThrow<IllegalArgumentException> {
                queueConfiguration(name = " \t \n ")
            }
        }
    }

    "Attributes" should {
        "Be empty by default" {
            queueConfiguration().attributes.size shouldBe 0
        }

        "Be added by using the 'attribute' function" {
            val config = queueConfiguration()
            config.attribute("attribute-1", "first")
            config.attribute("attribute-2", "second")

            config.attributes["attribute-1"] shouldBe "first"
            config.attributes["attribute-2"] shouldBe "second"
        }
    }
})

private fun queueConfiguration(name: String = "sample") =
        QueueConfiguration(name, ProjectBuilder.builder().build())
