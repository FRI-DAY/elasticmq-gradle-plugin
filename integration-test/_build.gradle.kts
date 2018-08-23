import de.friday.gradle.elasticmq.elasticmq

buildscript {
    repositories {
        flatDir {
            dirs("libs")
        }
    }
    dependencies {
        classpath(":elasticmq-gradle-plugin:")
    }
}

plugins {
    id("java")
}

apply {
    plugin("de.friday.elasticmq")
}

repositories {
    jcenter()
}

dependencies {
    testImplementation("com.amazonaws:aws-java-sdk-sqs:1.11.391")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    dependsOn("startLocalElasticMq")
    finalizedBy("stopLocalElasticMq")
}

elasticmq {
    instances {
        create("local") {
            protocol = "http"
            host = "localhost"
            port = 9324
            contextPath = "path"

            limits = "relaxed"

            queues {
                create("sample") {
                    attribute("DelaySeconds", "0")
                }
            }
        }
    }
}
