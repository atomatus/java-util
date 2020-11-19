package com.atomatus.connection.http.socket.event;

import com.atomatus.connection.http.socket.Client;

/**
 * Server I/O Actions listener.<br/>
 * <b>Warning:</b>
 * <p>
 * 	<i>
 * 	    When implements this listener never implement both ways (DATA/OBJECT),
 * 	    because after a type receive the data the another will not see it.
 * 	</i>
 * </p>
 * <p>
 * 	<i>
 * 	    Tip: Use adapater ({@link ServerDataAdapter} or {@link ServerObjectAdapter}) to help you.
 * 	</i>
 * </p>
 * @author Carlos Matos
 */
public interface ServerListener {

	/**
	 * Fire action when server receive some data.
	 * @param evt instance of input data event
	 */
	void onInputDataAction(InputDataEvent evt);
	
	/**
	 * Fire action when server receive some data and allow an answear to {@link Client}.
	 * @param evt instance of output data event
	 */
	void onOutputDataAction(OutputDataEvent evt);
	
	/**
	 * Fire action when server receive some serialized object.
	 * @param evt instance of input serialized object event
	 */
	void onInputObjectAction(InputObjectEvent evt);
	
	/**
	 * Fire action when server receive some serialized object and allow an answear to {@link Client}.
	 * @param evt instance of output serialized object event
	 */
	void onOutputObjectAction(OutputObjectEvent evt);
	
}