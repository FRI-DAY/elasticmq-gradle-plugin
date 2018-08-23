package de.friday.gradle.elasticmq

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder

class GradleIntPropertyTest: WordSpec({

    "Gradle Int Property" should {
        "Return the same value that was set" {
            val sample = IntSample()

            sample.field = 1234
            sample.field shouldBe 1234
        }

        "Throw an exception if unset" {
            val sample = IntSample()
            shouldThrow<IllegalStateException> {
                sample.field
            }
        }

        "Allow to set the default value" {
            val sample = object {
                val field by GradleIntProperty(
                        ProjectBuilder.builder().build(),
                        1234)
            }

            sample.field shouldBe 1234
        }
    }

})

private class IntSample {
    var field by GradleIntProperty(ProjectBuilder.builder().build())
}
