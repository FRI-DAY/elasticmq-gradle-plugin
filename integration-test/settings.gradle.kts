
pluginManagement {
    repositories {
        flatDir {
            dirs("libs")
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "de.friday.elasticmq") {
                useModule("de.friday:elasticmq-gradle-plugin:${requested.version}")
            }
        }
    }
}
