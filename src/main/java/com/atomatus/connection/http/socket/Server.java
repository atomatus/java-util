package com.atomatus.connection.http.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

import com.atomatus.util.AsciiTable;
import com.atomatus.connection.http.socket.event.ServerDataAdapter;
import com.atomatus.connection.http.socket.event.ServerListener;
import com.atomatus.connection.http.socket.event.ServerObjectAdapter;

/**
 * Socket Server connection.
 * @author Carlos Matos
 */
public class Server implements Runnable, Closeable {

	/**
	 * Default stop byte.
	 * {@link AsciiTable#EOT}
	 */
	public static final byte DEFAULT_STOP_BYTE = AsciiTable.EOT.code();
	
	/**
	 * backlog max count.
	 */
	public static final int DEFAULT_BACKLOG = 100;
	
	private int port;
	private final int backlog;
	protected final Object lock;
		
	private Thread serverThread;
	private ServerSocket server;
	private ServerListener listener;
	private IOType type;
	private boolean isUseStopByte;
	private byte stopByte;

	/**
	 * Input/Output type of data
	 */
	public enum IOType {
		/**
		 * Pure bytes
		 */
		DATA,

		/**
		 * Object serialized
		 */
		OBJECT
	}
	
	/**
	 * Creates a server socket and binds it to the specified local port
	 * number, with the specified backlog.
	 * @param port socket server port to wait for socket client requests, when 0 get first free port automatically.
	 * @param backlog max length of request enqueued.
	 * @see ServerSocket#ServerSocket(int, int)
	 */
	public Server(int port, int backlog) {
		this.port			= port;
		this.backlog		= backlog;
		this.stopByte		= DEFAULT_STOP_BYTE;
		this.lock			= new Object();
	}

	/**
	 * Creates a server socket, bound to the specified port. A port number
	 * of {@code 0} means that the port number is automatically
	 * allocated, typically from an ephemeral port range. This port
	 * @param port socket server port to wait for socket client requests, when 0 get first free port automatically.
	 * @see ServerSocket#ServerSocket(int)
	 */
	public Server(int port) {
		this(port, DEFAULT_BACKLOG);
	}

	/**
	 * reates a server socket, bound to discover port and with a backlog of {@link Server#DEFAULT_BACKLOG}.
	 */
	public Server() {
		this(0);
	}

	/**
	 * Adapter listener for {@link IOType#DATA}.
	 * @param listener instance of adapter.
	 */
	public final void setServerDataAdapter(ServerDataAdapter listener) {
		this.setServerListener(listener, IOType.DATA);
	}

	/**
	 * Adapter listener for {@link IOType#OBJECT}.
	 * @param listener instance of adapter.
	 */
	public final void setServerObjectAdapter(ServerObjectAdapter listener) {
		this.setServerListener(listener, IOType.OBJECT);
	}

	/**
	 * Check if use stop byte to check end of transmission.
	 * @return boolean value.
	 */
	public final boolean isUseStopByte() {
		return this.isUseStopByte;
	}

	/**
	 * Set use stop byte.
	 * @param isUseStopByte boolean value.
	 */
	public final void setUseStopByte(boolean isUseStopByte) {
		this.isUseStopByte = isUseStopByte;	
	}

	/**
	 * Get current stop byte.
	 * @return stop byte.
	 */
	public final byte getStopByte() {
		return this.stopByte;
	}

	/**
	 * Change stop byte.
	 * @param stopByte new stop byte
	 */
	public final void setStopByte(byte stopByte) {
		this.stopByte = stopByte;
	}
	
	/**
	 * Set {@link ServerListener} to Data or Object I/O.
	 * @param listener target listener
	 * @param type data type sent and received.
	 */
	public final void setServerListener(ServerListener listener, IOType type) {
		this.listener	= listener;
		this.type		= type;
	}
	
	@Override
	public final void run() {
		while(true) {
			
			Socket socket = null;
			
			try{//espera ate que algum cliente conecte no servidor.
				
				socket = server.accept();
				
				if(serverThread == null || serverThread.isInterrupted()){
					throw new Exception("Server disconnected!");
				}			
				else if(listener == null){
	               	throw new Exception("[Server] WARN: ServerListener not set, lost solicitation to connect!");
	            }

				new ServerSocketThread(this, socket, this.type, this.listener).start();
								
			} catch(Exception ex) {
				try{
					if(socket != null){
						socket.close();
					}
				}catch (Exception ignored) { }
				
				if(server == null || server.isClosed()){
					System.out.println("[Server] WARN: Connection closed: " + ex.getMessage());
					break;					
				}
				else{
					System.err.println("[Server] ERROR: Occurred an error I/O: " + ex.getMessage());
				}				
			}
		}
	}
	
	/**
	 * Communication port (I/O).
	 * @return current port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Max length of socket queue.
	 * @return current backlog
	 */
	public int getBacklog() {
		return backlog;
	}
	
	/**
	 * Open connection.
	 * @throws IOException throws when can not open connection.
	 */
	public void open() throws IOException{
		this.open(port);
	}
	
	/**
	 * Open connection.
	 * @param port new socket port
	 * @throws IOException throws when can not open connection.
	 */
	public void open(int port) throws IOException{
		if(server != null){
			throw new IOException("Can not open a new connection, server is open and connected in another port.");
		}
			
		try{
			this.port		= port;
			server			= new ServerSocket(port);
			serverThread	= new Thread(this);
			serverThread.start();
			this.port = server.getLocalPort();
			System.out.println("[Server] INFO: New socket connection openned at port: "+this.getPort());
		}catch(Exception ex){
			throw new ConnectException("Could not open a new connection at port "+this.getPort());
		}
	}
	
	/**
	 * Fecha conexao atual.
	 * @throws IOException throws when is not possible do it. Might already closed or another error.
	 */	
	public void close() throws IOException{
		if(this.server == null){
	    	 throw new IOException("Connetion already closed Portas de comunicacao ja estao fechadas!");
	    }
		else if(!this.server.isBound()){
			System.err.println("[Server] WARN: Server busy can not close connection now!");
		}
		else{
			
			try{
				ServerSocketThread.stopAllSocketThreadFromOwner(this);
			}
			catch (Exception e) {
				System.err.println("[Server] WARN: Socket Threads errors and closing: " + e.getMessage());
			}
			
			try{
				
				this.serverThread.interrupt();
			}catch (Exception e) {
				System.err.println("[Server] WARN: Error when attempt interrupt server thread: " + e.getMessage());
			}
			
			try{
				this.server.close();				
		        System.out.println("Communication port (I/O) closed successfully!");
			}finally{
				this.server 		= null;
				this.serverThread 	= null;
				System.gc();
			}
		}
	}
	
}