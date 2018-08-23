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
class ServerInstanceConfiguration(
        private val project: Project,
        val name: String
) {

    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name must not be blank nor empty")
        }
    }

    var contextPath by GradleProperty(project, String::class.java, DEFAULT_CONTEXT_PATH)

    var protocol by GradleProperty(project, String::class.java, DEFAULT_PROTOCOL)

    var limits by GradleProperty(project, String::class.java, DEFAULT_LIMITS)

    var host by GradleProperty(project, String::class.java, DEFAULT_HOST)

    var port by GradleIntProperty(project, DEFAULT_PORT)

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

    internal var elasticMqInstance = ElasticMqInstance(project, this)

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

internal typealias ServerInstanceConfigurationContainer =
        NamedDomainObjectContainer<ServerInstanceConfiguration>
