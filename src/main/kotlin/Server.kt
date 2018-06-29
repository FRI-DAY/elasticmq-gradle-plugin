package de.friday.gradle.elasticmq

import org.elasticmq.NodeAddress
import org.elasticmq.rest.sqs.SQSRestServer
import org.elasticmq.rest.sqs.SQSRestServerBuilder
import org.gradle.api.Project

internal class Server(
        private val project: Project,
        val config: ServerConfiguration
) {
    private var sqsRestServer: SQSRestServer? = null

    @Synchronized
    fun isRunning() = sqsRestServer != null

    @Synchronized
    fun start() {
        if (isRunning()) return

        project.logger.lifecycle("Starting ElasticMQ ${config.name} server")
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
            val queue = queueConfiguration.name
            val queueUrl = client.createQueue(queue).queueUrl
            client.setQueueAttributes(queueUrl, queueConfiguration.attributes)
        }
        client.shutdown()
    }

    @Synchronized
    fun stop() {
        if (!isRunning()) return

        project.logger.lifecycle("Stopping ElasticMQ ${config.name} server")
        sqsRestServer = sqsRestServer?.let { it.stopAndWait(); null }
    }

    @Synchronized
    fun ensureIsStopped() {
        if (isRunning()) {
            stop()
        }
    }
}
