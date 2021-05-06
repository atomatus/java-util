package com.atomatus.util;

/**
 * Exception for reflection error.
 */
public class ReflectionException extends RuntimeException {

    /**
     * Constructs with error message.
     * @param msg error message
     */
    public ReflectionException(String msg) {
        super(msg);
    }

    /**
     * Constructs with inner exception.
     * @param e inner exception
     */
    public ReflectionException(Throwable e) {
        super(e);
    }

}
