package com.atomatus.util.serializer;

public class SerializerException extends RuntimeException {

    public SerializerException(String msg) {
        super(msg);
    }

    public SerializerException(Throwable t) {
        super(t);
    }

    public SerializerException(String msg, Throwable t) {
        super(msg, t);
    }
}
