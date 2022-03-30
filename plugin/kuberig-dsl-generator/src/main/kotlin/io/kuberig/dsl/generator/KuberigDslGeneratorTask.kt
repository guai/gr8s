package io.kuberig.dsl.generator

import io.kuberig.dsl.generator.input.DslMetaProducer
import io.kuberig.dsl.generator.input.swagger.SwaggerDslMetaProducer
import io.kuberig.dsl.generator.output.DslMetaConsumer
import io.kuberig.dsl.generator.output.kotlin.KotlinDslMetaConsumer
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.enterprise.test.FileProperty
import java.io.File

abstract class KuberigDslGeneratorTask : DefaultTask() {

    @get:InputFile
    abstract val swaggerFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {

        // input for dsl meta
        val dslMetaProducer: DslMetaProducer = SwaggerDslMetaProducer(swaggerFile.get().asFile)

        val dslMeta = dslMetaProducer.provide()

        // output from dsl meta

        val kotlinDslMetaConsumer: DslMetaConsumer = KotlinDslMetaConsumer(outputDir.get().asFile)

        kotlinDslMetaConsumer.consume(dslMeta)
    }
}
