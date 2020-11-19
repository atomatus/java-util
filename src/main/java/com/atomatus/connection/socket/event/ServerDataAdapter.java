package com.atomatus.connection.socket.event;

/**
 * {@link ServerListener} Adapter to I/O of simple datas (wrappers and byte array)
 * @author Carlos Matos
 */
public abstract class ServerDataAdapter implements ServerListener{

	@Override
	public final void onInputObjectAction(InputObjectEvent evt) { }

	@Override
	public final void onOutputObjectAction(OutputObjectEvent evt) { }

}