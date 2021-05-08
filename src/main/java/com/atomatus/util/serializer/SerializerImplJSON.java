package com.atomatus.util.serializer;

import com.atomatus.util.StringUtils;
import com.atomatus.util.serializer.gson.GsonHelper;
import com.atomatus.util.serializer.wrapper.SerializerJson;
import com.google.gson.FieldNamingStrategy;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

class SerializerImplJSON extends SerializerImpl implements SerializerJson {

    //region Container
    private static class Container<T> {
        T container_data;

        Container(T t) {
            container_data = t;
        }

        public static ParameterizedType getParameterizedType(Class<?> dataType){
            return new ParameterizedType() {
                @Override
                public java.lang.reflect.Type[] getActualTypeArguments() {
                    return new java.lang.reflect.Type[] { dataType };
                }

                @Override
                public java.lang.reflect.Type getRawType() {
                    return Container.class;
                }

                @Override
                public java.lang.reflect.Type getOwnerType() {
                    return null;
                }
            };
        }
    }

    private static class ContainerFieldNamingStrategy implements FieldNamingStrategy {

        private final String rootElement;

        public ContainerFieldNamingStrategy(String rootElement){
            this.rootElement = rootElement;
        }

        @Override
        public String translateName(Field f) {
            String name = f.getName();
            return name.equals("container_data") ? rootElement : name;
        }
    }
    //endregion

    @Override
    public <T extends Serializable> String serialize(T t, String rootElement) {
        try {
            if(StringUtils.isNullOrWhitespace(rootElement)) {
                return GsonHelper.getInstance(Objects.requireNonNull(t).getClass())
                        .toJson(t);
            } else{
                return GsonHelper.getInstance(Objects.requireNonNull(t).getClass(),
                        builder -> builder.setFieldNamingStrategy(
                                new ContainerFieldNamingStrategy(rootElement)))
                        .toJson(new Container<>(t));
            }
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }

    @Override
    public <T extends Serializable> byte[] serializeAsBytes(T t, String rootElement) {
        return serialize(t, rootElement).getBytes();
    }

    @Override
    public <T extends Serializable> T deserialize(String serialized, String rootElement, Class<T> type) {
        try{
            if(StringUtils.isNullOrWhitespace(rootElement)) {
                return GsonHelper.getInstance(type).fromJson(serialized, type);
            } else{
                Container<T> container = GsonHelper.getInstance(type,
                        builder -> builder.setFieldNamingStrategy(
                                new ContainerFieldNamingStrategy(rootElement)))
                        .fromJson(serialized, Container.getParameterizedType(type));
                return container.container_data;
            }
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }

    @Override
    public <T extends Serializable> T deserialize(byte[] serialized, String rootElement, Class<T> type) {
        return deserialize(new String(serialized), rootElement, type);
    }
}
