package de.friday.gradle.elasticmq

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.string.shouldBeEmpty
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.elasticmq.rest.sqs.SQSLimits
import org.gradle.testfixtures.ProjectBuilder

class ServerConfigurationTest: WordSpec({

    "Context Path" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.contextPathProperty.set("context-1")

            config.contextPath shouldBe "context-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.contextPath = "context-2"

            config.contextPathProperty.get() shouldBe "context-2"
        }

        "Be empty by default" {
            serverConfiguration().contextPath.shouldBeEmpty()
        }
    }

    "Protocol" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.protocolProperty.set("protocol-1")

            config.protocol shouldBe "protocol-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.protocol = "protocol-2"

            config.protocolProperty.get() shouldBe "protocol-2"
        }

        "Be 'http' by default" {
            serverConfiguration().protocol shouldBe "http"
        }
    }

    "Limits" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.limitsProperty.set("limits-1")

            config.limits shouldBe "limits-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.limits = "limits-2"

            config.limitsProperty.get() shouldBe "limits-2"
        }

        "Be 'strict' by default" {
            serverConfiguration().limits shouldBe "strict"
        }

        "Be mapped to an instance on 'relaxed'" {
            val config = serverConfiguration()
            config.limits = "relaxed"

            config.getSqsLimits() shouldBe SQSLimits.Relaxed()
        }

        "Be mapped to an instance on 'strict'" {
            val config = serverConfiguration()
            config.limits = "strict"

            config.getSqsLimits() shouldBe SQSLimits.Strict()
        }

        "Fail mapping on unknown values" {
            val config = serverConfiguration()
            config.limits = "non-existing-limit"

            shouldThrow<IllegalArgumentException> {
                config.getSqsLimits()
            }
        }

        "Be case sensitive" {
            val config = serverConfiguration()
            config.limits = "STRICT"

            shouldThrow<IllegalArgumentException> {
                config.getSqsLimits()
            }
        }
    }

    "Host" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.hostProperty.set("host-1")

            config.host shouldBe "host-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.host = "host-2"

            config.hostProperty.get() shouldBe "host-2"
        }

        "Be 'localhost' by default" {
            serverConfiguration().host shouldBe "localhost"
        }
    }

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    "Port" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.portProperty.set(1234 as? Integer)

            config.port shouldBe 1234
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.port = 4321

            config.portProperty.get() shouldBe 4321
        }

        "Be '9324' by default" {
            serverConfiguration().port shouldBe 9324
        }
    }

    "Queues" should {
        "Be empty by default" {
            serverConfiguration().queues.shouldBeEmpty()
        }
    }

})

private fun serverConfiguration() = ProjectBuilder.builder().build().let {
    ServerConfiguration(it, "sampleConfiguration")
}
