package de.friday.gradle.elasticmq

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

private const val EXTENSION_NAME = "elasticmq"

/**
 * Plugin to add ElasticMQ support to Gradle builds.
 *
 * Exposes an `elasticmq` extension that allows the user to configure several
 * instances of ElasticMQ servers:
 *
 * ```groovy
 * elasticmq {
 *     // Server for local environments configuration
 *     local {
 *         ...
 *     }
 *
 *     // Server for testing configuration
 *     testing {
 *         ...
 *     }
 * }
 * ```
 */
class ElasticMqPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val serverConfigurationContainer =
                project.container(ServerConfiguration::class.java) { name ->
                    ServerConfiguration(project, name)
                }

        project.extensions.add(EXTENSION_NAME, serverConfigurationContainer)
        project.gradle.buildFinished {
            project.elasticmq.forEach { serverConfiguration ->
                serverConfiguration.ensureServerIsStopped()
            }
        }
    }
}

/**
 * Extension property to easily retrieve the `elasticmq` extension.
 */
val Project.elasticmq: ServerConfigurationContainer
    @Suppress("UNCHECKED_CAST")
    get() = extensions.getByName(EXTENSION_NAME) as? ServerConfigurationContainer
            ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")


/**
 * Extension method to easily configure the `elasticmq` extension.
 */
fun Project.elasticmq(config: Action<ServerConfigurationContainer>.() -> Unit) {
    extensions.configure(EXTENSION_NAME, config)
}
