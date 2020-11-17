package com.atomatus.util.serializer.gson;

import com.atomatus.util.DateHelper;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Calendar;

public final class CalendarConverter implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {

    @Override
    public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DateHelper.getInstance().getDate(src.getTime()));
    }

    @Override
    public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return DateHelper.getInstance().parseCalendar(json.getAsString());
    }
}
