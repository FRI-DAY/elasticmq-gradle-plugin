package de.friday.gradle.elasticmq.integrationtest;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.junit.jupiter.api.Test;

import static com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ElasticMqConnectionTest {

    @Test
    void canConnectToElasticMqInstance() {
        AmazonSQS client = AmazonSQSClientBuilder
                .standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .withEndpointConfiguration(new EndpointConfiguration(
                        "http://localhost:9324", "elasticmq"))
                .build();

        String queueUrl = client.getQueueUrl("sample").getQueueUrl();
        client.sendMessage(queueUrl, "Message Body");

        String messageBody = client
                .receiveMessage(queueUrl)
                .getMessages()
                .get(0)
                .getBody();

        assertEquals("Message Body", messageBody);
    }
}
