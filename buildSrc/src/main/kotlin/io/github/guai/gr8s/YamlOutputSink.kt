package io.github.guai.gr8s

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.k8s.api.core.v1.Volume
import io.kuberig.core.generation.yaml.ByteArrayDeserializer
import io.kuberig.core.generation.yaml.ByteArraySerializer
import io.kuberig.dsl.DslResource
import io.kuberig.dsl.DslResourceSink
import io.kuberig.dsl.model.BasicResource
import java.io.Writer
import java.util.function.Supplier

internal val objectMapper by lazy {
	ObjectMapper(YAMLFactory()).apply {
		findAndRegisterModules()

		val byteArrayModule = com.fasterxml.jackson.databind.module.SimpleModule().apply {
			addSerializer(ByteArray::class.java, ByteArraySerializer())
			addDeserializer(ByteArray::class.java, ByteArrayDeserializer())
		}
		registerModule(byteArrayModule)

		addMixIn(Volume::class.java, VolumeMixin::class.java)

		setSerializationInclusion(JsonInclude.Include.NON_DEFAULT) // todo seems to affect nothing
	}
}

class YamlOutputSink(private val writerSupplier: Supplier<Writer>) : DslResourceSink {
	override fun <T : BasicResource> add(resource: DslResource<T>) {
		writerSupplier.get().use {
			it.write(objectMapper.writeValueAsString(resource.dslType.toValue()))
		}
	}
}
