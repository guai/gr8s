package io.github.guai.gr8s

import org.gradle.api.Project
import java.io.File

open class Gr8sPluginExtension(project: Project) {
	var yamlDir: File = project.buildDir.resolve("yaml")

	internal val yamlFiles: MutableSet<File> = hashSetOf()
}
