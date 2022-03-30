package io.kuberig.dsl.generator

import org.gradle.api.Plugin
import org.gradle.api.Project

class KuberigDslGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("generateDsl", KuberigDslGeneratorTask::class.java).apply {
            outputDir.set(project.projectDir.resolve("build/generated-src/main/kotlin"))
        }
    }
}
