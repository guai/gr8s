package io.kuberig.core.generation

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*


class ByteArrayAdapter {
    @ToJson
    fun toJson(value: ByteArray): String =
        Base64.getEncoder().encodeToString(value)

    @FromJson
    fun fromJson(jsonString: String): ByteArray =
        Base64.getDecoder().decode(jsonString)
}
