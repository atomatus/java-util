package com.atomatus.util.serializer;

import java.io.Serializable;

public abstract class Serializer {

    public enum Type {
        BSON,
        JSON,
        XML
    }

    public static Serializer getInstance(Type type){
        return null;
    }

    public abstract <T extends Serializable> String serialize(T t, String rootElement);

    public abstract <T extends Serializable> T deserialize(String serialized, String rootElement);

    public final <T extends Serializable> String serialize(T t) {
        return serialize(t, null);
    }

    public final <T extends Serializable> T deserialize(String serialized) {
        return deserialize(serialized, null);
    }
}
