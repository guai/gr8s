package io.github.guai.gr8s

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.internal.Util
import com.squareup.moshi.rawType
import java.lang.reflect.Type


class MyJsonAdapterFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (annotations.isNotEmpty()) return null

        val rawType = type.rawType
        if (rawType.isInterface) return null
        if (rawType.isEnum) return null
        if (!rawType.isAnnotationPresent(Metadata::class.java)) return null
        if (Util.isPlatformType(rawType)) return null
        if (rawType.name.endsWith("_Adapter")) return null
        try {
            val adapterClass = Class.forName(rawType.canonicalName + "_Adapter", false, rawType.classLoader)
            val generatedAdapter = adapterClass.getDeclaredConstructor().newInstance() as JsonAdapter<*>?
            if (generatedAdapter != null) return generatedAdapter
        } catch (e: Exception) {
            if (e is ClassNotFoundException) return null
            if (e.cause !is ClassNotFoundException) throw e
        }
        return null
    }
}