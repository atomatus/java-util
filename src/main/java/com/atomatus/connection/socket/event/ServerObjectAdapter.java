package com.atomatus.connection.socket.event;

import java.io.Serializable;

/**
 * Server Object Adapter for {@link ServerListener} I/O of serialized objects data (that implements {@link Serializable}).
 * @author Carlos Matos
 */
public abstract class ServerObjectAdapter implements ServerListener{

	@Override
	public final void onInputDataAction(InputDataEvent evt) { }
	
	@Override
	public final void onOutputDataAction(OutputDataEvent evt) { }
	
}