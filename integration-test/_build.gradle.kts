
plugins {
    kotlin("jvm") version "1.3.21"
    id("de.friday.elasticmq") version "1.0.0-SNAPSHOT"
}

repositories {
    jcenter()
}

dependencies {
    testImplementation(kotlin("stdlib", "1.3.21"))
    testImplementation(kotlin("test-junit5", "1.3.21"))
    testImplementation("com.amazonaws:aws-java-sdk-sqs:1.11.519")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.3.1")
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
