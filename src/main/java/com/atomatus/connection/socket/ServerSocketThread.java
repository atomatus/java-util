package com.atomatus.connection.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.atomatus.connection.socket.Server.IOType;
import com.atomatus.connection.socket.event.InputDataEvent;
import com.atomatus.connection.socket.event.InputEvent;
import com.atomatus.connection.socket.event.InputObjectEvent;
import com.atomatus.connection.socket.event.OutputDataEvent;
import com.atomatus.connection.socket.event.OutputEvent;
import com.atomatus.connection.socket.event.OutputObjectEvent;
import com.atomatus.connection.socket.event.ServerListener;

/**
 * Thread instanciada pelo {@link Server} para comunicacao com socket client.
 * @author Carlos Matos
 *
 */
final class ServerSocketThread extends Thread {
	
	private static final Map<Server, Set<ServerSocketThread>> socketThreadsMap;
	
	private Server owner;
	private Socket socket;
	private IOType type;
	private ServerListener listener;
	private boolean isDisposed;
	
	static {
		socketThreadsMap = new ConcurrentHashMap<>();
	}

	private static Set<ServerSocketThread> getSocketThreadSetByOwner(Server owner) {
		synchronized (Objects.requireNonNull(owner).lock) {
			Set<ServerSocketThread> set = socketThreadsMap.get(owner);
			
			if(set == null){
				set = Collections.newSetFromMap(new ConcurrentHashMap<>());
				socketThreadsMap.put(owner, set);
			}
			
			return set;
		}
	}
		
	private static void addSocketThread(Server owner, ServerSocketThread socketThread) {
		Socket socket 	= socketThread.socket;
		boolean isAdded	= getSocketThreadSetByOwner(owner).add(socketThread);
		assert isAdded : "Novo Socket Thread ("+socket.hashCode()+") ja existe na lista de gerenciamento, nao manipule a colecao socketThreads fora dessa classe!";
	}
	
	private static void removeSocketThread(Server owner, ServerSocketThread socketThread){

		Socket socket 		= socketThread.socket;
		boolean isRemoved 	= getSocketThreadSetByOwner(owner).remove(socketThread);
		assert isRemoved : "Novo Socket Thread ("+socket.hashCode()+") ja foi removido da lista de gerenciamento, nao manipule a colecao socketThreads fora dessa classe!";
	}

	/**
	 * Finalize all sockets and dispose server threads.
	 * @param owner target server
	 */
	static void stopAllSocketThreadFromOwner(Server owner){
		synchronized (Objects.requireNonNull(owner).lock) {
			Set<ServerSocketThread> set = socketThreadsMap.get(owner);
			
			if(set != null){
				for(ServerSocketThread sst : set){					
					sst.dispose(false);
				}
				
				set.clear();				
				socketThreadsMap.remove(owner);
			}			
		}
	}
		
	public ServerSocketThread(Server owner, Socket socket, IOType type, ServerListener listener){
		this.owner		= owner;
		this.socket		= socket;
		this.type		= type;	
		this.listener	= listener;
		addSocketThread(owner, this);
	}
	
	private void FireOnInputDataAction(InputDataEvent inEvent){
		synchronized (Objects.requireNonNull(owner).lock) {
			if(listener != null) {
				listener.onInputDataAction(inEvent);	
			}
		}
	}

	private void FireOnOutputDataAction(OutputDataEvent outEvent){			
		synchronized (Objects.requireNonNull(owner).lock) {
			if(listener != null) {
				listener.onOutputDataAction(outEvent);	
			}
		}
	}

	private void FireOnInputObjectAction(InputObjectEvent inEvent){
		synchronized (Objects.requireNonNull(owner).lock) {
			if(listener != null) {
				listener.onInputObjectAction(inEvent);
			}
		}
	}

	private void FireOnOutputObjectAction(OutputObjectEvent outEvent){			
		synchronized (Objects.requireNonNull(owner).lock) {
			if(listener != null) {
				listener.onOutputObjectAction(outEvent);
			}
		}
	}
	
	@Override
	public void run() {
		try{//espera ate que algum cliente conecte no servidor.
			
			InputEvent inEvent 		= IOEvent.initInputEvent(socket.getInputStream(), owner.isUseStopByte(), owner.getStopByte());
			OutputEvent outEvent 	= IOEvent.initOutputEvent(socket.getOutputStream(), owner.isUseStopByte(), owner.getStopByte());
			
			switch(type){
				case DATA:						
					this.FireOnInputDataAction(inEvent);//dispara evento para leitura de dados.						
					outEvent.setBind(inEvent.getBind());//envia objeto para ser manipulado no outEvent.
					this.FireOnOutputDataAction(outEvent);//dispara evento para escrita de dados.
					break;
				case OBJECT:
					this.FireOnInputObjectAction(inEvent);//dispara evento para leitura de bytes e converte para objeto.
					outEvent.setBind(inEvent.getBind());//envia objeto para ser manipulado no outEvent.
					this.FireOnOutputObjectAction(outEvent);//dispara evento para escrever objeto e converte para bytes.
					break;
				default:
					throw new Exception("Tipo ("+type+") nao implementado!");
			}				
		}catch(Exception ex){
			System.err.println("[Server] ERROR: Erro durante I/O de dados:\n"+
				ex.getMessage());
			ex.printStackTrace();
		}
		finally {
			this.dispose();
		}
	}
	
	private void dispose(){
		this.dispose(true);
	}
	
	private void dispose(boolean isRemoveSocketThread){

		if(isDisposed){
			return;
		}
		
		isDisposed = true;
		
		if(isRemoveSocketThread){
			removeSocketThread(owner, this);
		}
		
		try{
			this.interrupt();
		}
		catch (Exception ignored) { }
		
		try{				
			socket.shutdownInput();
		}
		catch(IOException ignored) { }
					
		try{
			socket.shutdownOutput();
		}
		catch(IOException ignored) { }
		
		try{				
			socket.close();
		}
		catch(Exception ignored) { }
		
		this.owner		= null;
		this.socket		= null;
		this.type 		= null;
		this.listener	= null;
	}
	
}