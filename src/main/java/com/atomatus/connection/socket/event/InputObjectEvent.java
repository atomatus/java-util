package com.atomatus.connection.socket.event;

import java.io.IOException;
import java.io.Serializable;

/**
 * Read serialized object data on socket.
 * @author Carlos Matos {@literal @chcmatos}
 */
public interface InputObjectEvent extends BinderEvent{

	/**
	 * Get serialized data sent (client/server).
	 * @param <T> value type.
	 * @return deserialized object cast.
	 * @throws IOException is not possible read data from socket, connection is closed.
	 * @throws ClassNotFoundException when target class not found.
	 */
	<T extends Serializable> T readObject() throws IOException, ClassNotFoundException;

}