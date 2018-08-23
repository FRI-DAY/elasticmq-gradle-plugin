package de.friday.gradle.elasticmq

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Parent of all the tasks regarding ElasticMQ servers.
 */
sealed class ElasticMqTask(
        private val action: ElasticMqInstance.() -> Unit
): DefaultTask() {

    internal val serverNameProperty =
            project.objects.property(String::class.java)
        @Internal get

    var serverName: String
        set(value) = serverNameProperty.set(value)
        @Input get() = serverNameProperty.get()

    /**
     * Performs the [action] on the [ServerInstanceConfiguration] with the same name as
     * the [serverName] property.
     */
    @TaskAction
    fun doAction() {
        project.elasticmq.instances.getByName(serverName).elasticMqInstance.action()
    }
}

/**
 * Starts a named ElasticMQ Server instance.
 */
open class StartElasticMq: ElasticMqTask(ElasticMqInstance::start)

/**
 * Stops a named ElasticMQ Server instance.
 */
open class StopElasticMq: ElasticMqTask(ElasticMqInstance::stop)
