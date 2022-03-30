package io.github.guai.gr8s

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
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
//		disable(MapperFeature.AUTO_DETECT_IS_GETTERS) // doesn't help with Volume#csi/Volume#iscsi

		val byteArrayModule = com.fasterxml.jackson.databind.module.SimpleModule().apply {
			addSerializer(ByteArray::class.java, ByteArraySerializer())
			addDeserializer(ByteArray::class.java, ByteArrayDeserializer())
		}
		registerModule(byteArrayModule)

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
