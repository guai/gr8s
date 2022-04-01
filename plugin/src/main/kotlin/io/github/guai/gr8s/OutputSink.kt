package io.github.guai.gr8s

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.kuberig.core.generation.ByteArrayAdapter
import io.kuberig.dsl.DslResource
import io.kuberig.dsl.DslResourceSink
import io.kuberig.dsl.model.BasicResource
import java.io.Writer
import java.util.function.Supplier


internal val moshi: Moshi by lazy {
    Moshi.Builder()
        .add(ByteArrayAdapter())
        .addLast(MyJsonAdapterFactory())
        .addLast(KotlinJsonAdapterFactory())
        .build()
}

class OutputSink(private val writerSupplier: Supplier<Writer>) : DslResourceSink {
    override fun <T : BasicResource> add(resource: DslResource<T>) {
        writerSupplier.get().use {
            it.write(
                moshi
                    .adapter(resource.dslType.toValue().javaClass)
                    .indent("\t")
                    .toJson(resource.dslType.toValue())
            )
        }
    }
}
