package de.friday.gradle.elasticmq

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GUtil

private const val EXTENSION_NAME = "elasticmq"

/**
 * Plugin to add ElasticMQ support to Gradle builds.
 *
 * Exposes an `elasticmq` extension that allows the user to configure several
 * ElasticMQ server instances:
 *
 * ```groovy
 * elasticmq {
 *     instances {
 *         // Server for local environments configuration
 *         local {
 *             ...
 *         }
 *
 *         // Server for testing configuration
 *         testing {
 *             ...
 *         }
 *     }
 * }
 * ```
 *
 * Tasks for starting and stopping each ElasticMQ server are automatically added
 * to the build. For the previous example this means that `startLocalElasticMq`
 * and `stopLocalElasticMq` tasks are automatically created. New tasks can be
 * added by leveraging the [StartElasticMq] and [StopElasticMq] task types.
 */
class ElasticMqPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
                EXTENSION_NAME,
                ElasticMqExtension::class.java,
                project.container(ServerInstanceConfiguration::class.java) { name ->
                    ServerInstanceConfiguration(project, name)
                })

        project.gradle.buildFinished {
            extension.instances.forEach { serverConfiguration ->
                serverConfiguration.elasticMqInstance.ensureIsStopped()
            }
        }

        extension.instances.all { serverConfiguration ->
            val name = convertToTaskName(serverConfiguration.name)
            val startName = "start${name}ElasticMq"
            val stopName = "stop${name}ElasticMq"

            project.tasks.create(startName, StartElasticMq::class.java) {
                it.serverName = serverConfiguration.name
            }

            project.tasks.create(stopName, StopElasticMq::class.java) {
                it.serverName = serverConfiguration.name
            }
        }
    }
}

internal fun Project.elasticmq(): ElasticMqExtension =
    @Suppress("UNCHECKED_CAST")
    extensions.getByName(EXTENSION_NAME) as? ElasticMqExtension
    ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

internal fun convertToTaskName(name: String) =
        name.toLowerCase()
            .map { if (isValidTaskNameCharacter(it)) it else ' ' }
            .joinToString(separator = "")
            .toCamelCase()

private fun isValidTaskNameCharacter(char: Char) =
        char != '_' && Character.isJavaIdentifierPart(char)

private fun String.toCamelCase() = GUtil.toCamelCase(this)
