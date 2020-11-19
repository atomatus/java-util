package com.atomatus.connection.socket.event;

import java.io.IOException;

/**
 * Write data to socket (client/server)
 * @author Carlos Matos
 */
public interface OutputDataEvent extends BinderEvent{

	/**
	 * Write a byte array to target socket.<br/>
	 * <i>Warning: check you need autoFlush</i>
	 * @param bytes data will be send
	 * @throws IOException can no write data on socket output stream, no permission or connection is closed.
	 */
	void write(byte[] bytes) throws IOException;
	
	/**
	 * Write a text to target socket.<br/>
	 * <i>Warning: check you need autoFlush</i>
	 * @param str data will be send
	 * @throws IOException can no write data on socket output stream, no permission or connection is closed.
	 */
	void write(String str) throws IOException;
	
	/**
	 * Write a interger number to target socket.<br/>
	 * <i>Warning: check you need autoFlush</i>
	 * @param i data will be send
	 * @throws IOException can no write data on socket output stream, no permission or connection is closed.
	 */
	void write(Integer i) throws IOException;
	
	/**
	 * Write a boolean value to target socket.<br/>
	 * <i>Warning: check you need autoFlush</i>
	 * @param b data will be send
	 * @throws IOException can no write data on socket output stream, no permission or connection is closed.
	 */
	void write(Boolean b) throws IOException;
	
	/**
	 * Write a long number to target socket.<br/>
	 * <i>Warning: check you need autoFlush</i>
	 * @param l data will be send
	 * @throws IOException can no write data on socket output stream, no permission or connection is closed.
	 */
	void write(Long l) throws IOException;

	/**
	 * Write float number to target socket.<br/>
	 * <i>Warning: check you need autoFlush</i>
	 * @param f data will be send
	 * @throws IOException can no write data on socket output stream, no permission or connection is closed.
	 */
	void write(Float f) throws IOException;

	/**
	 * Write double number to target socket.<br/>
	 * <i>Warning: check you need autoFlush</i>
	 * @param d data will be send
	 * @throws IOException can no write data on socket output stream, no permission or connection is closed.
	 */
	void write(Double d) throws IOException;
	
}