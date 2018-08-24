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
        "Be empty by default" {
            serverConfiguration().contextPath.shouldBeEmpty()
        }
    }

    "Protocol" should {
        "Be 'http' by default" {
            serverConfiguration().protocol shouldBe "http"
        }
    }

    "Limits" should {
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
        "Be 'localhost' by default" {
            serverConfiguration().host shouldBe "localhost"
        }
    }

    "Port" should {
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

private fun serverConfiguration(name: String = "sample") =
        ServerInstanceConfiguration(name, ProjectBuilder.builder().build())
