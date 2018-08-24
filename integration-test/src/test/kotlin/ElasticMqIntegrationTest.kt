package de.friday.gradle.elasticmq.integrationtest

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class ElasticMqIntegrationTest {

    @Test
    fun `Can Connect Successfully to the ElasticMQ Instance`() {
        val client = AmazonSQSClientBuilder
                .standard()
                .withCredentials(
                        AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
                .withEndpointConfiguration(EndpointConfiguration(
                        "http://localhost:9324", "elasticmq"))
                .build()

        val queueUrl = client.getQueueUrl("sample").queueUrl
        client.sendMessage(queueUrl, "Message Body")

        val messageBody = client
                .receiveMessage(queueUrl)
                .messages
                .single()
                .body

        assertEquals("Message Body", messageBody)
    }
}
