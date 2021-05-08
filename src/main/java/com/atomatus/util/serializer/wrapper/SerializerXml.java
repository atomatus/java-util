package com.atomatus.util.serializer.wrapper;

import java.io.Serializable;

/**
 * Serializer for xml.
 */
public interface SerializerXml extends SerializerAction {

    /**
     * Serialize target object.
     * @param target target object
     * @param rootElement root tag name.
     * @param <T> target type
     * @return serialized data.
     */
    <T extends Serializable> String from(T target, String rootElement);
}
