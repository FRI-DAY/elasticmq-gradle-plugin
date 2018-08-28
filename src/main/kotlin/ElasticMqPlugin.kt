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
class ElasticMqPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
                EXTENSION_NAME,
                ElasticMqExtension::class.java,
                project)

        project.gradle.buildFinished {
            extension.instances.forEach { serverConfiguration ->
                serverConfiguration.elasticMqInstance.stop()
            }
        }

        extension.instances.all { serverConfiguration ->
            val name = serverConfiguration.name.toTaskName()

            project.tasks.register(
                    "start${name}ElasticMq",
                    StartElasticMq::class.java,
                    serverConfiguration.name)

            project.tasks.register(
                    "stop${name}ElasticMq",
                    StopElasticMq::class.java,
                    serverConfiguration.name)
        }
    }
}

internal fun Project.elasticmq(): ElasticMqExtension =
    @Suppress("UNCHECKED_CAST")
    extensions.getByName(EXTENSION_NAME) as? ElasticMqExtension
    ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

internal fun String.toTaskName() =
        this.toLowerCase()
            .map(::toValidTaskNameCharacters)
            .joinToString(separator = "")
            .toCamelCase()

private fun toValidTaskNameCharacters(char: Char): Char =
        if (char != '_' && Character.isJavaIdentifierPart(char)) { char } else { ' ' }

private fun String.toCamelCase() = GUtil.toCamelCase(this)
