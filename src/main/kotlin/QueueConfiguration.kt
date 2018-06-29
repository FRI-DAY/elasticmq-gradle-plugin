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

    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name must not be blank nor empty")
        }
    }

    internal val lazyAttributes =
            project.objects.property(MutableMap::class.java)

    var attributes: Map<String, String>
        set(value) {
            lazyAttributes.set(value.toMutableMap())
        }
        @Suppress("UNCHECKED_CAST")
        get() = lazyAttributes.orNull as? Map<String, String> ?: mapOf()

    /**
     * Adds an attribute to the queue configuration.
     *
     * @param [attribute] the name of the attribute to set
     * @param [value] the value to set the attribute to
     */
    fun attribute(attribute: String, value: String) {
        if (lazyAttributes.orNull == null) {
            lazyAttributes.set(mutableMapOf<String, String>())
        }
        @Suppress("UNCHECKED_CAST")
        val map = lazyAttributes.get() as? MutableMap<String, String>
        map?.put(attribute, value)
    }
}

internal typealias QueueConfigurationContainer =
        NamedDomainObjectContainer<QueueConfiguration>
