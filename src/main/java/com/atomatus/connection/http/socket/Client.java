package com.atomatus.connection.http.socket;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Socket client to TCP/IP connection.
 * @author Carlos Matos
 */
public class Client extends IOEvent implements Closeable {
	
	public static final int DEFAULT_CONNECTION_TIME_OUT	= 1500;
	public static final int DEFAULT_READ_TIME_OUT		= 5000;
	
    protected Socket socket;
    private final String address;
    private final int port;
    
    private int timeout;
    private int readTimeout;
    
    /**
     * Construtor.<br/>
     * build connection using parameters.
     * @param address target address DNS or TCP/IP.
     * @param port target port openned waiting connection.
     * @param readTimeout read timeout.
	 * @param autoFlush when true, sent data for each request, otherwhise wait for flush() method request or and close connection.
     * @param openConnection open connection when generate instance, otherwhise wait for open() method request.
     * @throws UnknownHostException host is not valid
     * @throws IOException is not possible get input/output stream or open connection.
     */
    public Client(String address, int port, int timeout, int readTimeout, boolean autoFlush, boolean openConnection) throws UnknownHostException, IOException{    	
        this.address		= address;
        this.port       	= port;
        this.timeout		= timeout;
        this.readTimeout	= readTimeout;
        this.setAutoFlush(autoFlush);
        
        if(openConnection){
        	this.open();
        }
    }
    
    /**
     * Constructor to build and open connection.<br/>
     * For this constructor, will set autoFlush and openConnection on constructor how true.
     * @param address target address DNS or TCP/IP.
     * @param port target socket port.
     * @throws UnknownHostException host is not valid
     * @throws IOException is not possible get input/output stream or open connection.
     */
    public Client(String address, int port) throws UnknownHostException, IOException {
    	this(address, port, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_READ_TIME_OUT, true, true);
	}

    /**
     * Constructor to build and open connection.<br/>
     * For this constructor, will set autoFlush and openConnection on constructor how true,
     * and set address how localhost.
     * @param port target socket port.
     * @throws UnknownHostException host is not valid
     * @throws IOException is not possible get input/output stream or open connection.
     */
    public Client(int port) throws UnknownHostException, IOException {
        this("localhost", port, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_READ_TIME_OUT, true, true);
    }

    /**
     * Get print stream.<br/>
     * <i>Use for printers.</i>
     * @return new print stream
     * @throws IOException throws when is not possible open stream.
     */
    public PrintStream getPrintStream() throws IOException{
    	if(isClosed()) throw new IOException("Connection is closed");
    	return new PrintStream(this.getOutputStream(), false, this.getCharset().displayName());   
    }
    
    /**
     * Get socket.
     */
    public Socket getSocket(){
    	return socket;
    }
    
    /**
     * Target socket address.
     */
    public String getAddress(){
    	return address;
    }
    
    /**
     * Target socket port.
     */
    public int getPort(){
    	return port;  
    }

    /**
     * Read timeout in millis.
     */
    public int getReadTimeout() {
		return readTimeout;
	}

    /**
     * Change read timeout.
     * @param readTimeout value in millis
     */
    public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

    /**
     * Get connection timeout in millis.
     * @return value in millis
     */
    public int getTimeout() {
		return timeout;
	}

    /**
     * Change connection timeout.
     * @param timeout value in millis
     */
    public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
    
    /**
     * Check if connection is closed.
     */
    @Override
    public boolean isClosed(){
    	return super.isClosed() || socket == null || socket.isClosed();
    }
    
    /**
     * Open connection.
     * @throws UnknownHostException host is not valid
     * @throws IOException is not possible get input/output stream or open connection.
     */
    public final void open() throws UnknownHostException, IOException {
    	if(!isClosed()){
    		throw new RuntimeException("Conexao ja esta aberta!");
    	}
    	
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(address, port), timeout);
        this.socket.setSoTimeout(readTimeout);
        this.setInput(socket.getInputStream());
        this.setOutput(socket.getOutputStream());
    }
    
    /**
     * Close connection
     * @throws IOException throws when socket already closed or some transmission interrupted.
     */
    @Override
    public final void close() throws IOException {
    	super.close();
    	
    	//envia dados acumulados antes de fechar conexao.
		try {
			this.flush();
		} catch (Exception ignored) { }
				
		if(socket != null){	
			try{
				socket.close();
			}
			finally{
				socket	= null;
			}
		}
		
		System.gc();		
    }

}