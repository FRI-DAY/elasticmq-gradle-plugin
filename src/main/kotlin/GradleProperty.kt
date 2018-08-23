package de.friday.gradle.elasticmq

import org.gradle.api.Project
import kotlin.reflect.KProperty

internal class GradleProperty<T>(
        project: Project,
        type: Class<T>,
        default: T? = null
) {
    val property = project.objects.property(type).apply {
        set(default)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        this.property.get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        this.property.set(value)
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
internal class GradleIntProperty(
        project: Project,
        default: Int? = null
) {
    val property = project.objects.property(Integer::class.java).apply {
        set(default as? Integer)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int =
            this.property.get().toInt()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) =
            this.property.set(value as? Integer)
}
