package com.atomatus.connection.http.socket.event;

/**
 * Use BinderEvent to transport data between {@link InputEvent} and {@link OutputEvent}.
 * @author Carlos Matos
 *
 */
public interface BinderEvent {

	/**
	 * Get defined data.
	 * @return current data
	 */
	Object getBind();
	
	/**
	 * Set data to tansport.
	 * @param data new data
	 */
	void setBind(Object data);
	
	/**
	 * Inform if use stop byte to identify end of transport.
	 */
	boolean isUseStopByte();
	
	/**
	 * Get stop byte to identify end of transport.
	 * @return current stop byte
	 */
	byte getStopByte();
	
}