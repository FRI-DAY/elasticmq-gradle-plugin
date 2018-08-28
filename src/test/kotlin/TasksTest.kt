package de.friday.gradle.elasticmq

import com.amazonaws.SdkClientException
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class TasksTest : WordSpec({

    "Start ElasticMQ Server Task" should {
        "Start a stopped server" { withProject { project, config ->
            project.tasks.withType(StartElasticMq::class.java).single().doAction()

            config.elasticMqInstance.isRunning() shouldBe true
            canConnect(config) shouldBe true
        } }

        "Belong to the elasticmq group" {
            val task = project().tasks.withType(StartElasticMq::class.java).single()
            task.group shouldBe "elasticmq"
        }

        "Include the instance name in the description" {
            val task = project().tasks.withType(StartElasticMq::class.java).single()
            task.description.shouldContain("instance-name")
        }
    }

    "Stop ElasticMQ Server Task" should {
        "Stop a running server" { withProject { project, config ->
            config.elasticMqInstance.start()
            project.tasks.withType(StopElasticMq::class.java).single().doAction()

            config.elasticMqInstance.isRunning() shouldBe false
            canConnect(config) shouldBe false
        } }

        "Belong to the elasticmq group" {
            val task = project().tasks.withType(StopElasticMq::class.java).single()
            task.group shouldBe "elasticmq"
        }

        "Include the instance name in the description" {
            val task = project().tasks.withType(StopElasticMq::class.java).single()
            task.description.shouldContain("instance-name")
        }
    }
})

private fun withProject(test: (Project, ServerInstanceConfiguration) -> Unit) {
    val project = project()
    val config = project.elasticmq().instances.single()
    try {
        test(project, config)
    } finally {
        config.elasticMqInstance.stop()
    }
}

private fun project() = ProjectBuilder.builder().build().also { project ->
    project.pluginManager.apply(ElasticMqPlugin::class.java)
    project.elasticmq().instances.create("instance-name")
}

private fun canConnect(config: ServerInstanceConfiguration) = try {
    config.elasticMqInstance.createClient().createQueue("queue").queueUrl
    true
} catch (e: SdkClientException) {
    false
}
