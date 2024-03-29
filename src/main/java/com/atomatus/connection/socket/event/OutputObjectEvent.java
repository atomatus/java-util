package com.atomatus.connection.socket.event;

import java.io.IOException;
import java.io.Serializable;

/**
 * Write serialized object data to socket (client/server).<br>
 * <i>Warning: Object need implements Serializable</i>
 * @author Carlos Matos {@literal @chcmatos}
 */
public interface OutputObjectEvent extends BinderEvent{

	/**
	 * Send a serialized object to socket (client/server).
	 * @param <T> value type.
	 * @param t target object
	 * @throws IOException can not write serialized data, might no permission or connection closed.
	 */
	<T extends Serializable> void writeObject(T t) throws IOException;
	
}