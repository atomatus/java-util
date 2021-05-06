package com.atomatus.connection.http.exception;

/**
 * Http Connection Response Exception.
 * @author Carlos Matos
 */
public class ResponseException extends URLConnectionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public ResponseException() {
		super("Was not possible get response from server!");
	}

	/**
	 * Constructs exception with message error.
	 * @param str message error
	 */
	public ResponseException(String str) {
		super(str);
	}

	/**
	 * Constructs exception with inner exception.
	 * @param t inner exception
	 */
	public ResponseException(Throwable t) {
		super(t);
	}
}
