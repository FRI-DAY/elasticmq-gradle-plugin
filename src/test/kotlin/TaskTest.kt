package de.friday.gradle.elasticmq

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec
import org.gradle.testfixtures.ProjectBuilder

class TaskTest: BehaviorSpec({

    Given("A configured Gradle project") {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(ElasticMqPlugin::class.java)

        val server = project.elasticmq.create("test")
        server.queues.create("sample")

        When("The server is started") {
            project.tasks.getByName("startTestElasticMq") {
                if (it is StartElasticMq) it.doAction()
            }

            val result = try {
                server.createServerClient().getQueueUrl("sample").queueUrl
            } finally {
                project.tasks.getByName("stopTestElasticMq") {
                    if (it is StopElasticMq) it.doAction()
                }
            }

            Then("The queue is configured") {
                result shouldNotBe ""
            }
        }
    }
})

