package de.friday.gradle.elasticmq

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Parent of all the tasks regarding ElasticMQ servers.
 */
sealed class ElasticMqTask(
        private val action: ElasticMqInstance.() -> Unit,
        @Input val serverName: String
): DefaultTask() {

    init {
        group = "elasticmq"
    }

    /**
     * Performs the [action] on the [ServerInstanceConfiguration] with the same name as
     * the [serverName] property.
     */
    @TaskAction
    fun doAction() {
        project.elasticmq().instances.getByName(serverName).elasticMqInstance.action()
    }
}

/**
 * Starts a named ElasticMQ Server instance.
 */
open class StartElasticMq
    @Inject constructor(name: String):
        ElasticMqTask(ElasticMqInstance::start, name) {

    init {
        description = "Starts the ${name} ElasticMQ Server Instance, if not running"
    }
}

/**
 * Stops a named ElasticMQ Server instance.
 */
open class StopElasticMq
    @Inject constructor(name: String):
        ElasticMqTask(ElasticMqInstance::stop, name) {

    init {
        description = "Stops the ${name} ElasticMQ Server Instance, if running"
    }
}
