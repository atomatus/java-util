package com.atomatus.util.serializer.gson;

import com.atomatus.util.DateHelper;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * JsonSerializer implementation to Date.
 */
public final class DateConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DateHelper.getInstance().getDate(src));
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return DateHelper.getInstance().parseDate(json.getAsString());
    }
}
