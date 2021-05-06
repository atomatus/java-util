package com.atomatus.util.serializer;

/**
 * Exception for serializer.
 */
public class SerializerException extends RuntimeException {

    /**
     * Constructs a serializer exception with message error.
     * @param msg message error.
     */
    public SerializerException(String msg) {
        super(msg);
    }

    /**
     * Constructs a serializer exception with inner exception.
     * @param t inner exception.
     */
    public SerializerException(Throwable t) {
        super(t);
    }

    /**
     * Constructs a serializer exception with message error and inner exception.
     * @param msg message error.
     * @param t inner exception.
     */
    public SerializerException(String msg, Throwable t) {
        super(msg, t);
    }
}
