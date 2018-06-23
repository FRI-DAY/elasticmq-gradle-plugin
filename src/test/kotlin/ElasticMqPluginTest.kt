package de.friday.gradle.elasticmq

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec
import org.gradle.testfixtures.ProjectBuilder

class ElasticMqPluginTest: BehaviorSpec({

    Given("A Gradle Project") {
        val project = ProjectBuilder.builder().build()

        When("The Plugin ID is used") {
            project.pluginManager.apply("de.friday.elasticmq")

            Then("The ElasticMQ Plugin should be applied") {
                project.plugins.getPlugin(ElasticMqPlugin::class.java) shouldNotBe null
            }

            Then("The elasticmq extension should be available") {
                project.extensions.getByName("elasticmq") shouldNotBe null
            }
        }
    }
})
