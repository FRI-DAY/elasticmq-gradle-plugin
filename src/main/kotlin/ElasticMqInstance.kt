package de.friday.gradle.elasticmq

import org.elasticmq.NodeAddress
import org.elasticmq.rest.sqs.SQSRestServer
import org.elasticmq.rest.sqs.SQSRestServerBuilder
import org.gradle.api.Project

internal class ElasticMqInstance(
    private val project: Project,
    val config: ServerInstanceConfiguration
) {
    @Volatile
    private var sqsRestServer: SQSRestServer? = null

    @Synchronized
    fun isRunning() = sqsRestServer != null

    @Synchronized
    fun start() {
        if (isRunning()) return

        project.logger.info("Starting ElasticMQ ${config.name} server instance")
        sqsRestServer = SQSRestServerBuilder
                .withSQSLimits(config.getSqsLimits())
                .withServerAddress(NodeAddress(
                        config.protocol,
                        config.host,
                        config.port,
                        config.contextPath
                ))
                .start()
                .apply { waitUntilStarted() }

        val client = config.createClient()
        config.queues.forEach { queueConfiguration ->
            val queueUrl = client.createQueue(queueConfiguration.name).queueUrl

            @Suppress("UNCHECKED_CAST")
            client.setQueueAttributes(
                    queueUrl,
                    queueConfiguration.attributes as? Map<String, String>)
        }
        client.shutdown()
    }

    @Synchronized
    fun stop() {
        if (!isRunning()) return

        project.logger.info("Stopping ElasticMQ ${config.name} server instance")
        sqsRestServer = sqsRestServer?.let { it.stopAndWait(); null }
    }
}
