package com.atomatus.connection.http.exception;

/**
 * Exceptions for url connection error.
 */
public class URLConnectionException extends Exception {

	/**
	 * Default constructor.
	 */
	public URLConnectionException() {
		this("An error occurred while attempt to access URL address.");
	}

	/**
	 * Constructs a new url exception with input message
	 * @param str input error message
	 */
	public URLConnectionException(String str) {
		super(str);
	}

	/**
	 * Constructs a new url exception with inner exception
	 * @param t inner exception
	 */
	public URLConnectionException(Throwable t) {
		super(t);
	}
}