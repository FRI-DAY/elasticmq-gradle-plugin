package de.friday.gradle.elasticmq

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder

class PluginTest: WordSpec({

    "Using the Plugin ID" should {
        "Apply the Plugin" {
            val project = ProjectBuilder.builder().build()
            project.pluginManager.apply("de.friday.elasticmq")

            project.plugins.getPlugin(ElasticMqPlugin::class.java) shouldNotBe null
        }
    }

    "Applying the Plugin" should {
        "Register the 'elasticmq' extension" {
            val project = ProjectBuilder.builder().build()
            project.pluginManager.apply(ElasticMqPlugin::class.java)

            project.elasticmq shouldNotBe null
        }
    }

})
