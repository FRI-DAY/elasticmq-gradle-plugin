package de.friday.gradle.elasticmq

import com.amazonaws.SdkClientException
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class TasksTest: WordSpec({

    "Start ElasticMQ Server Task" should {
        "Start a stopped server" { withProject { project, config ->
            project.tasks.withType(StartElasticMq::class.java).single().doAction()

            config.server.isRunning() shouldBe true
            canConnect(config) shouldBe true
        }}
    }

    "Stop ElasticMQ Server Task" should {
        "Stop a running server" { withProject { project, config ->
            config.server.start()
            project.tasks.withType(StopElasticMq::class.java).single().doAction()

            config.server.isRunning() shouldBe false
            canConnect(config) shouldBe false
        }}
    }

})

private fun withProject(test: (Project, ServerConfiguration) -> Unit) {
    val project = project()
    val config = project.elasticmq.single()
    try {
        test(project, config)
    } finally {
        config.server.ensureIsStopped()
    }
}

private fun project() = ProjectBuilder.builder().build().also {
    it.pluginManager.apply(ElasticMqPlugin::class.java)
    it.elasticmq.create("local")
}

private fun canConnect(config: ServerConfiguration) = try {
    config.createClient().createQueue("queue").queueUrl
    true
} catch (e: SdkClientException) {
    false
}
