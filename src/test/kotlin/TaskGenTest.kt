package de.friday.gradle.elasticmq

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder

class TaskGenTest : WordSpec({

    "Task generation" should {
        "Capitalize lower case words" {
            val project = project(name = "example")
            project.tasks.withType(ElasticMqTask::class.java).size shouldBe 2
            project.tasks.getByName("startExampleElasticMq") should beInstanceOf(StartElasticMq::class)
            project.tasks.getByName("stopExampleElasticMq") should beInstanceOf(StopElasticMq::class)
        }

        "Leave first character upper cased and lower case the rest of upper case words" {
            val project = project(name = "EXAMPLE")
            project.tasks.withType(ElasticMqTask::class.java).size shouldBe 2
            project.tasks.getByName("startExampleElasticMq") should beInstanceOf(StartElasticMq::class)
            project.tasks.getByName("stopExampleElasticMq") should beInstanceOf(StopElasticMq::class)
        }

        "Make the first character upper cased and lower case the rest of mixed case words" {
            val project = project(name = "eXaMpLe")
            project.tasks.withType(ElasticMqTask::class.java).size shouldBe 2
            project.tasks.getByName("startExampleElasticMq") should beInstanceOf(StartElasticMq::class)
            project.tasks.getByName("stopExampleElasticMq") should beInstanceOf(StopElasticMq::class)
        }

        "Capitalize each word separated by space and join them" {
            val project = project(name = "another example")
            project.tasks.withType(ElasticMqTask::class.java).size shouldBe 2
            project.tasks.getByName("startAnotherExampleElasticMq") should beInstanceOf(StartElasticMq::class)
            project.tasks.getByName("stopAnotherExampleElasticMq") should beInstanceOf(StopElasticMq::class)
        }

        "Treat multiple whitespaces as one" {
            val project = project(name = "another \t \n example")
            project.tasks.withType(ElasticMqTask::class.java).size shouldBe 2
            project.tasks.getByName("startAnotherExampleElasticMq") should beInstanceOf(StartElasticMq::class)
            project.tasks.getByName("stopAnotherExampleElasticMq") should beInstanceOf(StopElasticMq::class)
        }

        arrayOf(
                '-', '_', '$', 'à', 'á', 'â', 'ã', 'ä', 'ç', 'è', 'é', 'ê', 'ë',
                'ì', 'í', 'î', 'ï', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', 'š', 'ù', 'ú',
                'û', 'ü', 'ý', 'ÿ', 'ž', "\uD83D\uDE01"
        ).forEach { c ->
            "Treat '$c' as space" {
                val project = project(name = "another${c}example")
                project.tasks.withType(ElasticMqTask::class.java).size shouldBe 2
                project.tasks.getByName("startAnotherExampleElasticMq") should beInstanceOf(StartElasticMq::class)
                project.tasks.getByName("stopAnotherExampleElasticMq") should beInstanceOf(StopElasticMq::class)
            }
        }
    }
})

private fun project(name: String) = ProjectBuilder.builder().build().also { project ->
    project.pluginManager.apply(ElasticMqPlugin::class.java)
    project.elasticmq().instances.create(name)
}
