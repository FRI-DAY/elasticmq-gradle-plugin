package de.friday.gradle.elasticmq

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder

class QueueConfigurationTest: WordSpec({

    "Attributes" should {
        "Be correctly returned by the property if lazily set" {
            val config = queueConfiguration()
            config.lazyAttributes.set(mutableMapOf(
                    "attribute" to "value-1"
            ))

            config.attributes["attribute"] shouldBe "value-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = queueConfiguration()
            config.attributes = mapOf(
                    "attribute" to "value-2"
            )

            config.lazyAttributes.get()["attribute"] shouldBe "value-2"
        }

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

private fun queueConfiguration() = ProjectBuilder.builder().build().let {
    QueueConfiguration(it, "sampleConfiguration")
}
