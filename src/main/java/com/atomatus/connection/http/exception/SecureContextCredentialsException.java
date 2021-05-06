package com.atomatus.connection.http.exception;

/**
 * Exception for secure context credentials.
 */
public final class SecureContextCredentialsException extends RuntimeException {

    /**
     * Constructs new secure context credentials exception
     * with inner exception.
     * @param e inner exception
     */
    public SecureContextCredentialsException(Throwable e){
        super(e);
    }
}
