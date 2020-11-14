package com.atomatus.util;

public class ReflectionException extends RuntimeException {

    public ReflectionException(String msg) {
        super(msg);
    }

    public ReflectionException(Throwable e) {
        super(e);
    }

}
