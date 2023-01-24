package com.atomatus.connection.socket;

import com.atomatus.connection.socket.event.InputEvent;
import com.atomatus.connection.socket.event.OutputEvent;
import com.atomatus.connection.socket.event.ServerListener;
import com.atomatus.util.ArrayHelper;
import com.atomatus.util.BufferHelper;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Implements {@link InputEvent} and {@link OutputEvent} for listeners ({@link ServerListener})
 * and is base class to {@link Client}.
 * @author Carlos Matos {@literal @chcmatos}
 */
@SuppressWarnings("unused")
class IOEvent implements InputEvent, OutputEvent, Closeable {

	/**
	 * Default buffer size.
	 */
	public final int BUFFER_SIZE = 1024;

	/**
	 * Instance of IOEvent as input event.
	 * @param input input stream instance.
	 * @param isUseStopByte define whether use stop byte to identify end of stream to write and read.
	 * @param stopByte stop byte.
	 * @return IOEvent instance as InputEvent.
	 */
	public static InputEvent initInputEvent(InputStream input, boolean isUseStopByte, byte stopByte){
		return new IOEvent(input, null, isUseStopByte, stopByte);
	}

	/**
	 * Instance of IOEvent as output event.
	 * @param output output stream instance.
	 * @param isUseStopByte define whether use stop byte to identify end of stream to write and read.
	 * @param stopByte stop byte.
	 * @return IOEvent instance as OutputEvent.
	 */
	public static OutputEvent initOutputEvent(OutputStream output, boolean isUseStopByte, byte stopByte){
		return new IOEvent(null, output, isUseStopByte, stopByte);
	}

	/**
	 * Instance of IOEvent as input event.
	 * @param input input stream instance.
	 * @return IOEvent instance as InputEvent.
	 */
	public static InputEvent initInputEvent(InputStream input){
		return new IOEvent(input, null);
	}

	/**
	 * Instance of IOEvent as output event.
	 * @param output output stream instance.
	 * @return IOEvent instance as OutputEvent.
	 */
	public static OutputEvent initOutputEvent(OutputStream output){
		return new IOEvent(null, output);
	}
	
	private InputStream input;
	private OutputStream output;
	
	private ObjectInputStream objectInput;
	private ObjectOutputStream objectOutput;
		
    private ByteArrayOutputStream bufferStream;
    private boolean autoFlush;
	private boolean isClosed;
	private final boolean isUseStopByte;
    private final byte stopByte;
	private Charset charset;
    private Object bindData;
	
	protected IOEvent(InputStream input, OutputStream output, boolean isUseStopByte, byte stopByte) {	
		this.input 			= input;
		this.output 		= output;
		this.bufferStream	= new ByteArrayOutputStream();
        this.charset		= Charset.defaultCharset();
        this.autoFlush		= true;
        this.stopByte		= stopByte;
        this.isUseStopByte	= isUseStopByte;
	}
	
	protected IOEvent(InputStream input, OutputStream output, boolean isUseStopByte) {
		this(input, output, isUseStopByte, Server.DEFAULT_STOP_BYTE);
	}
	
	protected IOEvent(InputStream input, OutputStream output) {
		this(input, output, false);
	}
	
	protected IOEvent(){
		this(null, null);
	}

	/**
	 * Input stream instance when contains permission to read.
	 * @return input stream instance.
	 * @exception RuntimeException throws when does not contains permission to read.
	 */
	protected InputStream getInputStream(){
		this.requestPermissionToRead();
		return input;
	}

	/**
	 * Output stream instance when contains permission to write.
	 * @return ouput stream instance.
	 * @exception RuntimeException throws when does not contains permission to write.
	 */
	protected OutputStream getOutputStream(){
		this.requestPermissionToWrite();
		return output;
	}

	/**
	 * Define input stream.
	 * @param input input stream.
	 */
	protected void setInput(InputStream input){
		this.input = input;
	}

	/**
	 * Define output stream.
	 * @param output output stream.
	 */
	protected void setOutput(OutputStream output){
		this.output = output;
	}
		
	private void requestPermissionToRead(){
		if(input == null){
			throw new RuntimeException("Is not possible read data from IOEvent instance for OutputEvent (no read permission)!");
		}
	}
	
	private void requestPermissionToWrite(){
		if(output == null){
			throw new RuntimeException("Is not possible read data from IOEvent instance for InputEvent (no write permission)!");
		}
	}
	
	private void requestConnectionOpenned() throws IOException{
		if(isClosed){
			throw new IOException("Connection is closed!");
		}
	}
	
	@Override
	public byte[] readAll() throws IOException{
		this.requestConnectionOpenned();		
		this.requestPermissionToRead();
		
		byte[] buffer	= new byte[BUFFER_SIZE];		
		int offset		= 0;
		int count;
		
		do{
			
			if(offset >= buffer.length){				
				byte[] aux	= buffer;
				buffer		= new byte[aux.length + BUFFER_SIZE];
				System.arraycopy(aux, 0, buffer, 0, aux.length);
			}
						
			count 	 = input.read(buffer, offset, buffer.length - offset);
			offset	+= count;
									
		}while(count > 0 /*se leu algo*/ && (isUseStopByte ? buffer[offset - 1] != stopByte /*use e ainda nao leu stop-byte EOT*/ : (count == buffer.length /*se preencheu o buffer por completo*/ || input.available() > 0 /*se tem mais algo p ler*/)));
		
		boolean hasStopByte;
		
		if((hasStopByte = isUseStopByte && buffer[offset - 1] == stopByte) /*tem stop-byte*/ || offset < buffer.length /*caso o buffer tenha espacos nao preenchidos.*/){
			byte[] aux	= buffer;
			buffer		= new byte[hasStopByte ? offset - 1 : offset];
			System.arraycopy(aux, 0, buffer, 0, hasStopByte ? offset - 1 : offset);
		}
				
		return buffer;		
	}

	private <T> T parse(ArrayHelper.Function<byte[], T> mount) throws IOException {
		byte[] arr = this.readAll();
		return arr.length != 0 ? mount.apply(arr) : null;
	}

	private <T extends Number> T parseNumber(ArrayHelper.Function<byte[], T> mount) throws IOException {
		byte[] arr = this.readAll();
		return arr.length != 0 ? mount.apply(arr) : null;
	}

	@Override
	public String readString() throws IOException {
		return this.parse(String::new);
	}

	@Override
	public Integer readInteger() throws IOException {
		return this.parseNumber(BufferHelper::toInt);
	}

	@Override
	public Boolean readBoolean() throws IOException {
		return this.parse(BufferHelper::toBoolean);
	}

	@Override
	public Long readLong() throws IOException {
		return this.parseNumber(BufferHelper::toLong);
	}

	@Override
	public Float readFloat() throws IOException {
		return this.parseNumber(BufferHelper::toFloat);
	}

	@Override
	public Double readDouble() throws IOException {
		return this.parseNumber(BufferHelper::toDouble);
	}
	
	@Override
	public void write(byte[] bytes) throws IOException {	
		this.requestConnectionOpenned();
		this.requestPermissionToWrite();
		
		try{
			
			int oldLength = bytes.length;			
			if(isUseStopByte && bytes[oldLength - 1] != stopByte) {				
				bytes = Arrays.copyOf(bytes, oldLength + 1);
				bytes[oldLength] = stopByte;
			}
			
	    	bufferStream.write(bytes);
	    	
    	}finally{
    	   if(autoFlush){
    		   flush();
    	   }
        }
	}
	
	@Override
	public void write(String str) throws IOException {
		this.write(BufferHelper.fromString(str, charset));
	}
	
	@Override
	public void write(Integer i) throws IOException {
		this.write(BufferHelper.fromInt(i));
	}
	
	@Override
	public void write(Boolean b) throws IOException {
		this.write(BufferHelper.fromBoolean(b));
	}
	
	@Override
	public void write(Long l) throws IOException {
		this.write(BufferHelper.fromLong(l));
	}

	@Override
	public void write(Float f) throws IOException {
		this.write(BufferHelper.fromFloat(f));
	}

	@Override
	public void write(Double d) throws IOException {
		this.write(BufferHelper.fromDouble(d));
	}

	@Override
	public <T extends Serializable> void writeObject(T t) throws IOException {
		objectOutput = objectOutput == null ? new ObjectOutputStream(output) : objectOutput;
		objectOutput.writeObject(t);
		objectOutput.flush();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T readObject() throws IOException, ClassNotFoundException {		
		objectInput = objectInput == null ? new ObjectInputStream(input) : objectInput;		
		return (T) objectInput.readObject();
	}
	
	/**
	 * Set if when write something have to be send automatically,
	 * othewhise will wait for method flush or close connection.
	 * Obs.: By default autoflush = true.
	 * @param autoFlush enable auto flush
	 */
	public final void setAutoFlush(boolean autoFlush){
		this.autoFlush = autoFlush;
	}

	@Override
	public Object getBind() {
		Object aux 	= bindData;
		bindData 	= null;
		return aux;
	}
	
	@Override
	public void setBind(Object data) {
		bindData = data;
	}


	@Override
	public boolean isUseStopByte() {
		return this.isUseStopByte;
	}

	@Override
	public byte getStopByte() {
		return stopByte;
	}
	
	/**
	 * Send queued data to destiny socket connection.
	 * @throws IOException throws when is not possible write data.
	 */
	public void flush() throws IOException {
		this.requestConnectionOpenned();
		
		if(bufferStream.size() > 0){
			try{
		    	output.write(bufferStream.toByteArray());
		    	output.flush();
		    }finally{
		    	bufferStream.reset();
		    }
		}
	}

	/**
	 * I/O charset for encode/decode
	 * @return current charset
	 */
    public final Charset getCharset() {
		return charset;
	}

	/**
	 * I/O charset for encode/decode
	 * @param charset charset value.
	 */
	public final void setCharset(Charset charset) {
		this.charset = charset;
	}

    /**
     * Check if connection is closed.
     * @return connection is closed or not.
     */
    public boolean isClosed(){
    	return isClosed;
    }
    
	@Override
	public void close() throws IOException {
		
		try{
			if(this.bufferStream != null){
				this.bufferStream.reset();
				this.bufferStream.close();
			}			
		}
		finally{
			try{
				
				if(this.input != null){
					this.input.close();
				}
				
				if(this.output != null){
					this.output.close();
				}
			}
			finally{
				this.bufferStream	= null;
				this.bindData 		= null;
				this.objectInput	= null;
				this.objectOutput 	= null;
				this.input			= null;
				this.output			= null;
				this.charset		= null;
				this.isClosed		= true;
			}		
		}
	}
}