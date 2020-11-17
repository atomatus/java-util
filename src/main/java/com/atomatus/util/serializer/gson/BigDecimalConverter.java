package com.atomatus.util.serializer.gson;

import com.atomatus.util.DecimalHelper;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public final class BigDecimalConverter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {

    @Override
    public JsonElement serialize(BigDecimal src, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(DecimalHelper.toDecimal(src));
    }

    @Override
    public BigDecimal deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return DecimalHelper.toBigDecimal(json.getAsString());
    }
}
