package com.atomatus.util.serializer;

import com.atomatus.util.serializer.wrapper.SerializerAction;

import java.io.Serializable;

abstract class SerializerImpl extends Serializer implements SerializerAction {

    @Override
    public <T extends Serializable> String from(T t) {
        return serialize(t);
    }

    @Override
    public <T extends Serializable> T to(String serialized, Class<T> targetClass) {
        return deserialize(serialized, targetClass);
    }
}
