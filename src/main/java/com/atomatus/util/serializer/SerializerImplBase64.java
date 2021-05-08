package com.atomatus.util.serializer;

import com.atomatus.util.Base64;
import com.atomatus.util.StringUtils;
import com.atomatus.util.serializer.wrapper.SerializerBase64;

import java.io.*;
import java.util.Objects;

final class SerializerImplBase64 extends SerializerImpl implements SerializerBase64 {

    @Override
    public <T extends Serializable> String serialize(T t, String rootElement) {
        try{
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baout);
            oos.writeObject(t);
            oos.close();
            return Base64.getEncoder().encodeToString(baout.toByteArray());
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }

    @Override
    public <T extends Serializable> byte[] serializeAsBytes(T t, String rootElement) {
        try{
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baout);
            oos.writeObject(t);
            oos.close();
            return Base64.getEncoder().encode(baout.toByteArray());
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserialize(String serialized, String rootElement, Class<T> type) {
        try{
            byte[] data = Base64.getDecoder().decode(StringUtils.requireNonNullOrEmpty(serialized));
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            ois.close();
            return (T) obj;
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserialize(byte[] serialized, String rootElement, Class<T> type) {
        try{
            byte[] data = Base64.getDecoder().decode(Objects.requireNonNull(serialized));
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            ois.close();
            return (T) obj;
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }
}
