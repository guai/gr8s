package io.github.guai.gr8s

import org.gradle.api.Project
import java.io.File

open class Gr8sPluginExtension(project: Project) {
	var yamlDir: File = project.buildDir.resolve("yaml")

	internal val yamlFiles: MutableMap<String, File> = hashMapOf()

	fun yamlFile(name: String, file: File?): File {
		return (file ?: yamlDir.resolve(name)).also {
			yamlFiles[name] = it
		}
	}

	internal val deleteYamlFiles: Unit by lazy {
		yamlDir.mkdirs()
		yamlFiles.values.forEach {
			if (it.exists()) it.delete()
		}
	}
}
