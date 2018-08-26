package de.friday.gradle.elasticmq

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder

class ExtensionTest : WordSpec({

    "Instances" should {
        "Be empty by default" {
            extension().instances.shouldBeEmpty()
        }
    }
})

private fun extension() = ElasticMqExtension(ProjectBuilder.builder().build())
