package com.atomatus.connection.http.exception;

@SuppressWarnings("serial")
public class URLConnectionException extends Exception {

	public URLConnectionException() {
		this("An error occurred while attempt to access URL address.");
	}

	public URLConnectionException(String str) {
		super(str);
	}

	public URLConnectionException(Throwable t) {
		super(t);
	}
}