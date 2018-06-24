package de.friday.gradle.elasticmq

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * Configuration for an ElasticMQ server instance.
 */
class QueueConfiguration(
        project: Project,
        val name: String
) {

    internal val attributesProperty =
            project.objects.property(MutableMap::class.java)

    var attributes: Map<String, String>
        set(value) {
            attributesProperty.set(value.toMutableMap())
        }
        @Suppress("UNCHECKED_CAST")
        get() = attributesProperty.orNull as? Map<String, String> ?: mapOf()

    /**
     * Adds an attribute to the queue configuration.
     *
     * @param [attribute] the name of the attribute to set
     * @param [value] the value to set the attribute to
     */
    fun attribute(attribute: String, value: String) {
        if (attributesProperty.orNull == null) {
            attributesProperty.set(mutableMapOf<String, String>())
        }
        @Suppress("UNCHECKED_CAST")
        val map = attributesProperty.get() as? MutableMap<String, String>
        map?.put(attribute, value)
    }
}

internal typealias QueueConfigurationContainer =
        NamedDomainObjectContainer<QueueConfiguration>