package com.atomatus.connection.http.exception;

/**
 * Http Connection Response Exception.
 * @author Carlos Matos
 */
public class ResponseException extends URLConnectionException {

	private static final long serialVersionUID = 1L;

	public ResponseException() {
		super("Was not possible get response from server!");
	}

	public ResponseException(String str) {
		super(str);
	}

	public ResponseException(Throwable t) {
		super(t);
	}
}
