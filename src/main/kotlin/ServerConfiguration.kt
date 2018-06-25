package de.friday.gradle.elasticmq

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import groovy.lang.Closure
import org.elasticmq.NodeAddress
import org.elasticmq.rest.sqs.SQSRestServer
import org.elasticmq.rest.sqs.SQSRestServerBuilder
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

private const val ELASTICMQ_REGION = "elasticmq"
private const val DEFAULT_CONTEXT_PATH = ""
private const val DEFAULT_PROTOCOL = "http"
private const val DEFAULT_HOST = "localhost"
private const val DEFAULT_PORT = 9324

/**
 * Configuration for an ElasticMQ server instance.
 */
class ServerConfiguration(
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
            project.objects.property(Integer::class.java).also {
                it.set(DEFAULT_PORT as? Integer)
            }

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
        get() = portProperty.get().toInt()

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

    private var server: SQSRestServer? = null

    @Synchronized
    internal fun startServer() {
        if (server != null) {
            project.logger.warn("ElasticMQ $name server is already running")
        } else {
            project.logger.lifecycle("Starting ElasticMQ $name server")
            server = SQSRestServerBuilder
                    .withServerAddress(NodeAddress(
                            protocol, host, port, contextPath
                    ))
                    .start()
            server?.waitUntilStarted()
            createQueues()
        }
    }

    @Synchronized
    internal fun stopServer() {
        if (server == null) {
            project.logger.warn("ElasticMQ $name server is already stopped")
        } else {
            project.logger.lifecycle("Stopping ElasticMQ $name server")
            server?.stopAndWait()
            server = null
        }
    }

    @Synchronized
    internal fun ensureServerIsStopped() {
        if (server != null) {
            stopServer()
        }
    }

    private fun createQueues() {
        val client = createServerClient()

        queues.forEach { queueConfiguration ->
            val queue = queueConfiguration.name
            val queueUrl = client.createQueue(queue).queueUrl
            client.setQueueAttributes(queueUrl, queueConfiguration.attributes)
        }

        client.shutdown()
    }

    internal fun createServerClient() = AmazonSQSClientBuilder
            .standard()
            .withCredentials(
                    AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
            .withEndpointConfiguration(EndpointConfiguration(
                    "$protocol://$host:$port",
                    ELASTICMQ_REGION))
            .build()
}

internal typealias ServerConfigurationContainer =
        NamedDomainObjectContainer<ServerConfiguration>
