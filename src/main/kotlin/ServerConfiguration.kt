package de.friday.gradle.elasticmq

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import groovy.lang.Closure
import org.elasticmq.rest.sqs.SQSLimits
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

private const val ELASTICMQ_REGION = "elasticmq"
private const val DEFAULT_CONTEXT_PATH = ""
private const val DEFAULT_PROTOCOL = "http"
private const val DEFAULT_LIMITS = "strict"
private const val DEFAULT_HOST = "localhost"
private const val DEFAULT_PORT = 9324

/**
 * Configuration for an ElasticMQ server instance.
 */
class ServerConfiguration(
        private val project: Project,
        val name: String
) {

    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name must not be blank nor empty")
        }
    }

    internal val lazyContextPath =
            project.objects.property(String::class.java)

    internal val lazyProtocol =
            project.objects.property(String::class.java)

    internal val lazyLimits =
            project.objects.property(String::class.java)

    internal val lazyHost =
            project.objects.property(String::class.java)

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    internal val lazyPort =
            project.objects.property(Integer::class.java).also {
                it.set(DEFAULT_PORT as? Integer)
            }

    var contextPath: String
        set(value) {
            lazyContextPath.set(value)
        }
        get() = lazyContextPath.getOrElse(DEFAULT_CONTEXT_PATH)

    var protocol: String
        set(value) {
            lazyProtocol.set(value)
        }
        get() = lazyProtocol.getOrElse(DEFAULT_PROTOCOL)

    var limits: String
        set(value) {
            lazyLimits.set(value)
        }
        get() = lazyLimits.getOrElse(DEFAULT_LIMITS)

    var host: String
        set(value) {
            lazyHost.set(value)
        }
        get() = lazyHost.getOrElse(DEFAULT_HOST)

    var port: Int
        set(value) {
            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
            lazyPort.set(value as? Integer)
        }
        get() = lazyPort.get().toInt()

    val queues = project.container(QueueConfiguration::class.java) { name ->
        QueueConfiguration(project, name)
    }

    /**
     * Configures the queues associated to this ElasticMQ server instance.
     *
     * @param [config] lambda to configure the queues.
     */
    fun queues(config: QueueConfigurationContainer.() -> Unit) {
        queues.configure(object: Closure<Unit>(this, this) {
            fun doCall() {
                @Suppress("UNCHECKED_CAST")
                (delegate as? QueueConfigurationContainer)?.let {
                    config(it)
                }
            }
        })
    }

    /**
     * Configures the queues associated to this ElasticMQ server instance.
     *
     * @param [config] Groovy closure to configure the queues.
     */
    fun queues(config: Closure<Unit>) {
        queues.configure(config)
    }

    internal var server = Server(project, this)

    internal fun createClient() = AmazonSQSClientBuilder
            .standard()
            .withCredentials(
                    AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
            .withEndpointConfiguration(EndpointConfiguration(
                    "$protocol://$host:$port",
                    ELASTICMQ_REGION))
            .build()

    internal fun getSqsLimits() = when (limits) {
        "relaxed" -> SQSLimits.Relaxed()
        "strict" -> SQSLimits.Strict()
        else -> throw IllegalArgumentException(
                "Only 'strict' and 'relaxed' are accepted as limits")
    }
}

internal typealias ServerConfigurationContainer =
        NamedDomainObjectContainer<ServerConfiguration>
