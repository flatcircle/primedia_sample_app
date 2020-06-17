package com.primedia.primedia_sample_app.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class StreamItemDataModelTypeDeserializer : JsonDeserializer<StreamItemDataModelType> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): StreamItemDataModelType {
        val layoutType = StreamItemDataModelType.values()
        for (scope in layoutType) {
            if (scope.type == json?.asString) return scope
        }
        return StreamItemDataModelType.UNKNOWN
    }
}
