package com.primedia.primedia_sample_app.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class StreamLayoutTypeDeserializer : JsonDeserializer<StreamLayoutType> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): StreamLayoutType {
        val layoutType = StreamLayoutType.values()
        for (scope in layoutType) {
            if (scope.type == json?.asInt) return scope
        }
        return StreamLayoutType.UNKNOWN
    }
}