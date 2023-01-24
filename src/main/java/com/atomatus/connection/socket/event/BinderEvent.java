package com.atomatus.connection.socket.event;

/**
 * Use BinderEvent to transport data between {@link InputEvent} and {@link OutputEvent}.
 * @author Carlos Matos {@literal @chcmatos}
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
	 * @return true to inform that current event use stop byte.
	 */
	boolean isUseStopByte();
	
	/**
	 * Get stop byte to identify end of transport.
	 * @return current stop byte
	 */
	byte getStopByte();
	
}