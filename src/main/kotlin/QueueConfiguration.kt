package de.friday.gradle.elasticmq

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * Configuration for an ElasticMQ server instance.
 */
class QueueConfiguration(
    val name: String,
    project: Project
) {

    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name must not be blank nor empty")
        }
    }

    var attributes by GradleProperty(project, MutableMap::class.java, mutableMapOf<String, String>())

    /**
     * Adds an attribute to the queue configuration.
     *
     * @param [attribute] the name of the attribute to set
     * @param [value] the value to set the attribute to
     */
    fun attribute(attribute: String, value: String) {
        @Suppress("UNCHECKED_CAST")
        val map = attributes as? MutableMap<String, String>
        map?.put(attribute, value)
    }
}

internal typealias QueueConfigurationContainer =
        NamedDomainObjectContainer<QueueConfiguration>
