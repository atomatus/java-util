package com.atomatus.util.serializer;

import com.atomatus.util.StringUtils;
import com.atomatus.util.serializer.xstream.XStreamHelper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SerializerImplXML extends Serializer {

    private String getRootElementFromSerialized(String serialized) {
        if(serialized != null) {
            Pattern p = Pattern.compile("(?<=<)([\\w|\\-]+)(?=[\\s|>])",
                    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(serialized);
            if(m.find()) {
                return m.group();
            }
        }
        return null;
    }

    private String getRootElement(String serialized, String rootElement, Class<?> type) {
        if(StringUtils.isNullOrWhitespace(rootElement)) {
            if(type == null) {
                return getRootElementFromSerialized(serialized);
            } else {
                for (Annotation annotation : type.getDeclaredAnnotations()) {
                    if(annotation.annotationType().equals(XStreamAlias.class)) {
                        XStreamAlias alias = (XStreamAlias)annotation;
                        return alias.value();
                    }
                }
                rootElement = getRootElementFromSerialized(serialized);
                return rootElement != null ? rootElement : type.getSimpleName();
            }
        } else {
            return rootElement;
        }
    }

    private void setRootElementAlias(XStream xs, String serialized, String rootElement, Class<?> type){
        if(type != null) {
            rootElement = getRootElement(serialized, rootElement, type);
            if (rootElement != null) {
                xs.alias(rootElement, type);
            }
        }
    }

    @Override
    public <T extends Serializable> String serialize(T t, String rootElement) {
        try {
            XStream xs = XStreamHelper.getInstance(Objects.requireNonNull(t).getClass());
            setRootElementAlias(xs, null, rootElement, t.getClass());
            return xs.toXML(t);
        } catch (Throwable e){
            throw new SerializerException(e);
        }
    }

    @Override
    public <T extends Serializable> byte[] serializeAsBytes(T t, String rootElement) {
        try {
            XStream xs = XStreamHelper.getInstance(Objects.requireNonNull(t).getClass());
            setRootElementAlias(xs, null, rootElement, t.getClass());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xs.toXML(t, out);
            return out.toByteArray();
        } catch (Throwable e){
            throw new SerializerException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserialize(String serialized, String rootElement, Class<T> type) {
        try {
            StringUtils.requireNonNullOrEmpty(serialized);
            XStream xs = XStreamHelper.getInstance(type);
            setRootElementAlias(xs, serialized, rootElement, type);
            return (T) xs.fromXML(serialized);
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserialize(byte[] serialized, String rootElement, Class<T> type) {
        try {
            Objects.requireNonNull(serialized);
            XStream xs = XStreamHelper.getInstance(type);
            setRootElementAlias(xs, null, rootElement, type);
            ByteArrayInputStream in = new ByteArrayInputStream(serialized);
            return (T) xs.fromXML(in);
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }
}
