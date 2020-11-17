package com.atomatus.util.serializer;

import java.io.Serializable;

/**
 * <p>
 * Serialize and deserialize objects to {@link Serializer.Type#BSON},
 * {@link Serializer.Type#JSON} or {@link Serializer.Type#XML}.
 * </p>
 * <pre>
 *     Example ex = new Example();
 *     Serializer xml = Serializer.getInstance(Serializer.Type.XML);
 *     String xmlEx = xml.serialize(ex);
 *     ex = xml.deserialize(xmlEx);
 * </pre>
 */
public abstract class Serializer {

    /**
     * Serializer type.
     */
    public enum Type {
        BASE64,
        BSON,
        JSON,
        XML
    }

    /**
     * Instance of serializer from type.
     * @param type serializer type
     * @return instance of serializer type.
     */
    public static Serializer getInstance(Type type) {
        switch (type) {
            case BASE64:
                return new SerializerImplBase64();
            case BSON:
                return new SerializerImplBSON();
            case JSON:
                return new SerializerImplJSON();
            case XML:
                return new SerializerImplXML();
            default:
                throw new UnsupportedOperationException("Serializer type not implemented: " + type);
        }
    }

    /**
     * Serialize target object.
     * @param t target object
     * @param rootElement root element of serialize, example, for xml, root tag. When null or empty, ignored.
     * @param <T> object type
     * @return serialize result.
     */
    public abstract <T extends Serializable> String serialize(T t, String rootElement);

    /**
     * Serialize target object as byte array.
     * @param t target object
     * @param rootElement root element of serialize, example, for xml, root tag. When null or empty, ignored.
     * @param <T> object type
     * @return serialize result as byte array.
     */
    public abstract <T extends Serializable> byte[] serializeAsBytes(T t, String rootElement);

    /**
     * Deserialize data to target object.
     * @param serialized data serialized
     * @param rootElement root element of serialize, example, for xml, root tag. When null or empty, ignored.
     * @param type target class type.
     * @param <T> object type.
     * @return deserialized data to object again.
     */
    public abstract <T extends Serializable> T deserialize(String serialized, String rootElement, Class<T> type);

    /**
     * Deserialize data from bytes array to target object.
     * @param serialized byte array data serialized
     * @param rootElement root element of serialize, example, for xml, root tag. When null or empty, ignored.
     * @param type target class type.
     * @param <T> object type.
     * @return deserialized data to object again.
     */
    public abstract <T extends Serializable> T deserialize(byte[] serialized, String rootElement, Class<T> type);

    /**
     * Serialize target object.
     * @param t target object
     * @param <T> object type
     * @return serialize result.
     */
    public final <T extends Serializable> String serialize(T t) {
        return serialize(t, null);
    }

    /**
     * Serialize target object as byte array.
     * @param t target object
     * @param <T> object type
     * @return serialize result as byte array.
     */
    public final <T extends Serializable> byte[] serializeAsBytes(T t) {
        return serializeAsBytes(t, null);
    }

    /**
     * Deserialize data to target object.
     * @param serialized data serialized
     * @param type target class type.
     * @param <T> object type.
     * @return deserialized data to object again.
     */
    public final <T extends Serializable> T deserialize(String serialized, Class<T> type) {
        return deserialize(serialized, null, type);
    }

    /**
     * Deserialize data from bytes array to target object.
     * @param serialized data serialized
     * @param type target class type.
     * @param <T> object type.
     * @return deserialized data to object again.
     */
    public final <T extends Serializable> T deserialize(byte[] serialized, Class<T> type) {
        return deserialize(serialized, null, type);
    }
}
