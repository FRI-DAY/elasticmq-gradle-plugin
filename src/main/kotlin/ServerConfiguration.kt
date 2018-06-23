package de.friday.gradle.elasticmq

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

private const val DEFAULT_CONTEXT_PATH = ""
private const val DEFAULT_PROTOCOL = "http"
private const val DEFAULT_HOST = "localhost"
private const val DEFAULT_PORT = 9324

/**
 * Configuration for an ElasticMQ server instance.
 */
data class ServerConfiguration(
        private val project: Project,
        val name: String
) {

    internal val contextPathProperty =
            project.objects.property(String::class.java)

    internal val protocolProperty =
            project.objects.property(String::class.java)

    internal val hostProperty =
            project.objects.property(String::class.java)

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    internal val portProperty =
            project.objects.property(Integer::class.java)

    var contextPath: String
        set(value) {
            contextPathProperty.set(value)
        }
        get() = contextPathProperty.getOrElse(DEFAULT_CONTEXT_PATH)

    var protocol: String
        set(value) {
            protocolProperty.set(value)
        }
        get() = protocolProperty.getOrElse(DEFAULT_PROTOCOL)

    var host: String
        set(value) {
            hostProperty.set(value)
        }
        get() = hostProperty.getOrElse(DEFAULT_HOST)

    var port: Int
        set(value) {
            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
            portProperty.set(value as? Integer)
        }
        get() = portProperty.orNull as? Int ?: DEFAULT_PORT
}

internal typealias ServerConfigurationContainer =
        NamedDomainObjectContainer<ServerConfiguration>
