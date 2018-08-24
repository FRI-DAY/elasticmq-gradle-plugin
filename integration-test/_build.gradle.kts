
plugins {
    kotlin("jvm") version "1.2.61"
    id("de.friday.elasticmq") version "0.1.0"
}

repositories {
    jcenter()
}

dependencies {
    testImplementation(kotlin("stdlib", "1.2.61"))
    testImplementation(kotlin("test-junit5", "1.2.61"))
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
