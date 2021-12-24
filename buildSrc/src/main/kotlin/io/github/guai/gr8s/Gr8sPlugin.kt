package io.github.guai.gr8s

import org.gradle.api.Plugin
import org.gradle.api.Project

class Gr8sPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.extensions.create("gr8s", Gr8sPluginExtension::class.java, project)
		project.tasks.create("generateYaml", GenerateYaml::class.java)
	}
}
