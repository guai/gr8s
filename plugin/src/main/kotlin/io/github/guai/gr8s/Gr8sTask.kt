package io.github.guai.gr8s

import io.kuberig.dsl.DslResource
import io.kuberig.dsl.DslResourceSink
import io.kuberig.dsl.model.BasicResource
import kinds.DslKindsRoot
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.nio.charset.Charset
import java.util.function.Supplier


open class Gr8sTask : DefaultTask(), DslResourceSink {

    private val extension: Gr8sPluginExtension = project.extensions.getByType()

    init {
        group = "gr8s"
        if (name != "gr8s")
            project.tasks.getByName("gr8s").dependsOn(this)
        outputs.upToDateWhen { false }
    }


    @get:Internal
    val dsl by lazy { DslKindsRoot(this) }

    fun dsl(block: DslKindsRoot.() -> Unit) = dsl.also { block(it) }

    fun dsl(writerSupplier: Supplier<Writer>, block: DslKindsRoot.() -> Unit = {}) = DslKindsRoot(OutputSink(writerSupplier)).also { block(it) }

    private fun File.appendWriter(charset: Charset = Charsets.UTF_8): Writer = FileWriter(this, charset, true).buffered()

    override fun <T : BasicResource> add(resource: DslResource<T>) {
        val file = extension.outputDir.also { it.mkdirs() }.resolve(resource.alias)
        val writer = if (extension.definitionFiles.add(file)) file.writer() else file.appendWriter()
        writer.use {
            it.write(
                moshi
                    .adapter(resource.dslType.toValue().javaClass)
                    .indent("\t")
                    .toJson(resource.dslType.toValue())
            )
        }
    }
}
