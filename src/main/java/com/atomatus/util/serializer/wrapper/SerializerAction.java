package com.atomatus.util.serializer.wrapper;

import java.io.Serializable;

/**
 * Serializer actions
 * @author Carlos  Matos
 */
public interface SerializerAction {

    /**
     * Serialize target object.
     * @param target target object
     * @param <T> target type
     * @return serialized data.
     */
    <T extends Serializable> String from(T target);

    /**
     * Deserialize data to object.
     * @param serialized serialized data.
     * @param targetClass target type class.
     * @param <T> target type.
     * @return deserialized object.
     */
    <T extends Serializable> T to(String serialized, Class<T> targetClass);
}
