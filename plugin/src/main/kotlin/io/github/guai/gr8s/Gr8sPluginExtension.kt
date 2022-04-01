package io.github.guai.gr8s

import org.gradle.api.Project
import java.io.File


open class Gr8sPluginExtension(project: Project) {
    var outputDir: File = project.buildDir.resolve("gr8s")

    internal val definitionFiles: MutableSet<File> = hashSetOf()
}
