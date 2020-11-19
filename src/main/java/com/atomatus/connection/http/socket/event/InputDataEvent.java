package com.atomatus.connection.http.socket.event;

import java.io.IOException;

/**
 * Read socket data
 * @author Carlos Matos
 */
public interface InputDataEvent extends BinderEvent{
	
	/**
	 * Get all bytes array sent (client/server).
	 * @return sent data in byte array.
	 * @throws IOException is not possible read data, connection was closed.
	 */
	byte[] readAll() throws IOException;
	
	/**
	 * Get data sent as string.
	 * @return sent data converted to string
	 * @throws IOException is not possible read data, connection was closed.
	 */
	String readString() throws IOException;

	/**
	 * Get data sent as integer.
	 * @return send data converted to integer.
	 * @throws IOException is not possible read data, connection was closed.
	 */
	Integer readInteger() throws IOException;

	/**
	 * Get data sent as boolean.
	 * @return send data converted to boolean.
	 * @throws IOException is not possible read data, connection was closed.
	 */
	Boolean readBoolean() throws IOException;

	/**
	 * Get data sent as long.
	 * @return send data converted to long.
	 * @throws IOException is not possible read data, connection was closed.
	 */
	Long readLong() throws IOException;

	/**
	 * Get data sent as float.
	 * @return send data converted to float.
	 * @throws IOException is not possible read data, connection was closed.
	 */
	Float readFloat() throws IOException;

	/**
	 * Get data sent as double.
	 * @return send data converted to double.
	 * @throws IOException is not possible read data, connection was closed.
	 */
	Double readDouble() throws IOException;
}