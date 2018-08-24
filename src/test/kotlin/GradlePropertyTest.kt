package de.friday.gradle.elasticmq

import io.kotlintest.matchers.beTheSameInstanceAs
import io.kotlintest.should
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder

class GradlePropertyTest : WordSpec({

    "Gradle Property" should {
        "Return the exact same value that was set" {
            val sample = Sample()
            val value = Object()

            sample.field = value
            sample.field should beTheSameInstanceAs(value)
        }

        "Throw an exception if unset" {
            val sample = Sample()
            shouldThrow<IllegalStateException> {
                sample.field
            }
        }

        "Allow to set the default value" {
            val value = Object()
            val sample = object {
                val field by GradleProperty(
                        ProjectBuilder.builder().build(),
                        Object::class.java,
                        value)
            }

            sample.field should beTheSameInstanceAs(value)
        }
    }
})

private class Sample {
    var field by GradleProperty(
            ProjectBuilder.builder().build(),
            Object::class.java)
}
