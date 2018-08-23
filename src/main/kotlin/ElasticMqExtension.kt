package de.friday.gradle.elasticmq

import groovy.lang.Closure

/**
 * Container for the custom ElasticMQ plugin configuration DSL.
 */
open class ElasticMqExtension(
       val instances: ServerConfigurationContainer
) {

    /**
     * Configures the server instances registered in the plugin.
     *
     * @param [config] lambda to configure the server instances.
     */
    fun instances(config: ServerConfigurationContainer.() -> Unit) {
        instances.configure(object: Closure<Unit>(this, this) {
            fun doCall() {
                @Suppress("UNCHECKED_CAST")
                (delegate as? ServerConfigurationContainer)?.let {
                    config(it)
                }
            }
        })
    }

    /**
     * Configures the server instances registered in the plugin.
     *
     * @param [config] Groovy closure to configure the server instances.
     */
    fun instances(config: Closure<Unit>) {
        instances.configure(config)
    }
}
