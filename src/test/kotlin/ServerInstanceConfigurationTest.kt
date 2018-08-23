package de.friday.gradle.elasticmq

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.string.shouldBeEmpty
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.elasticmq.rest.sqs.SQSLimits
import org.gradle.testfixtures.ProjectBuilder

class ServerInstanceConfigurationTest: WordSpec({

    "Server Instance Configuration" should {
        "Not accept an empty name" {
            shouldThrow<IllegalArgumentException> {
                serverConfiguration(name = "")
            }
        }

        "Not accept a blank name" {
            shouldThrow<IllegalArgumentException> {
                serverConfiguration(name = " \t \n ")
            }
        }
    }

    "Context Path" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.lazyContextPath.set("context-1")

            config.contextPath shouldBe "context-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.contextPath = "context-2"

            config.lazyContextPath.get() shouldBe "context-2"
        }

        "Be empty by default" {
            serverConfiguration().contextPath.shouldBeEmpty()
        }
    }

    "Protocol" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.lazyProtocol.set("protocol-1")

            config.protocol shouldBe "protocol-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.protocol = "protocol-2"

            config.lazyProtocol.get() shouldBe "protocol-2"
        }

        "Be 'http' by default" {
            serverConfiguration().protocol shouldBe "http"
        }
    }

    "Limits" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.lazyLimits.set("limits-1")

            config.limits shouldBe "limits-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.limits = "limits-2"

            config.lazyLimits.get() shouldBe "limits-2"
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
            config.lazyHost.set("host-1")

            config.host shouldBe "host-1"
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.host = "host-2"

            config.lazyHost.get() shouldBe "host-2"
        }

        "Be 'localhost' by default" {
            serverConfiguration().host shouldBe "localhost"
        }
    }

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    "Port" should {
        "Be correctly returned by the property if lazily set" {
            val config = serverConfiguration()
            config.lazyPort.set(1234 as? Integer)

            config.port shouldBe 1234
        }

        "Be correctly returned lazily if set in the property" {
            val config = serverConfiguration()
            config.port = 4321

            config.lazyPort.get() shouldBe 4321
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

private fun serverConfiguration(name: String = "sample") = ProjectBuilder.builder().build().let {
    ServerInstanceConfiguration(it, name)
}
