package util
import org.gradle.api.Project

import java.util.Properties

fun getLocalProperty(propertyName: String, project: Project): String {
    val localProperties = Properties().apply {
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            load(localPropertiesFile.inputStream())
        }
    }

    return localProperties.getProperty(propertyName) ?: run {
        throw NoSuchFieldException("Not defined property: $propertyName")
    }
}

fun localPropertyExists(propertyName: String, project: Project): Boolean {
    val localProperties = Properties().apply {
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            load(localPropertiesFile.inputStream())
        }
    }
    return localProperties.getProperty(propertyName) != null
}

fun Project.getLocalProperty(propertyName: String): String {
    return getLocalProperty(propertyName, this)
}

fun Project.localPropertyExists(propertyName: String): Boolean {
    return localPropertyExists(propertyName, this)
}
