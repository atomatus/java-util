package com.atomatus.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

/**
 * SNTP (Simple Network Time Protocol) client for online time requests.
 * @author Carlos Matos {@literal @chcmatos}
 *
 */
public final class SNTPClient {

	/**
	 * Default list of global time servers.
	 * @return addresses of global time servers.
	 */
	public static String[] servers(){
		return new String[]{
				"time.nist.gov",
				"time-a.nist.gov",
				"time-b.nist.gov",
				"time-c.nist.gov",
				"nist1-nj2.ustiming.org",
				"nist1-ny2.ustiming.org",
				"nist1-pa.ustiming.org"
		};
	}

	private static final int NTP_PACKET_SIZE		= 48;
    private static final int ORIGINATE_TIME_OFFSET 	= 24;
    private static final int RECEIVE_TIME_OFFSET 	= 32;
    private static final int TRANSMIT_TIME_OFFSET 	= 40;
	
    private static final int NTP_PORT 				= 123;
    private static final int NTP_MODE_CLIENT 		= 3;
    private static final int NTP_VERSION 			= 3;
	
    //Secunds between Jan 1, 1900 and Jan 1, 1970
    //more 70 years and jump 17 days.
    private static final long OFFSET_1900_TO_1970 	= ((365L * 70L) + 17L) * 24L * 60L * 60L;

    private static final int DEFAULT_TIME_OUT 		= 5000;
        
    private Long ntpTime;
    
    private Long roundTripTime;
    
    /**
     * Get Result of date/time request.
     * @return ntp time.
     */
    public Long getNtpTime() {
		return ntpTime;
	}
    
    /**
     * Get Round Trip Time.
     * @return round trip time.
     */
    public Long getRoundTripTime() {
		return roundTripTime;
	}

	/**
	 * Get Result of date/time request.
	 * @return ntp time.
	 */
    public Date getNtpDate(){
    	return new Date(getNtpTime());
    }

	/**
	 * Get Result of date/time request.
	 * @return ntp time.
	 */
    public Calendar getNtpCalendar(){
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(getNtpDate());
    	return cal;
    }
    
    /**
     * Request global date time on server ({@link SNTPClient#servers()}) available.<br>
     * <i>Each attempt have a default timeout of 5 seconds.</i>
     * @return when true, got global date time successfully, otherwhise failed.
	 * @throws IllegalArgumentException throws exception when host or timeout is invalid.
     */
    public boolean request() {
    	return request(DEFAULT_TIME_OUT);
    }
    
    /**
	 * Request global date time on server ({@link SNTPClient#servers()}) available.<br>
     * @param timeout request timeout in millis.
	 * @return when true, got global date time successfully, otherwhise failed.
	 * @throws IllegalArgumentException throws exception when host or timeout is invalid.
     */
    public boolean request(int timeout) {
    	for(String host : servers()) {
    		if(request(host, timeout)){
    			return true;
    		}
    	}
    	return false;
    }

	/**
	 * Request global date time on server ({@link SNTPClient#servers()}) available.<br>
	 * @param host SNTP global time server address.
	 * @param timeout request timeout in millis.
	 * @return when true, got global date time successfully, otherwhise failed.
	 * @throws IllegalArgumentException throws exception when host or timeout is invalid.
	 */
	private boolean request(String host, int timeout) {
		clear();
		if(host == null || host.isEmpty()){
			throw new IllegalArgumentException("Invalid host name!");
		}
		else if(timeout < 0){
			throw new IllegalArgumentException("Invalid timeout!");
		}
		else if(!hasInternet()){
			return false;
		}
		
		DatagramSocket socket = null;
		DatagramPacket request;//pacote a ser enviado ao servidor.
		DatagramPacket response;//pacote com resposta do servidor.
		InetAddress address;
		byte[] buffer;//buffer com dados de transmissao entre servidor e localhost.
		
		try{
			buffer	= new byte[NTP_PACKET_SIZE];
			address = InetAddress.getByName(host);
			request	= new DatagramPacket(buffer, buffer.length, address, NTP_PORT);
			socket 	= new DatagramSocket();
			socket.setSoTimeout(timeout);
			
			/*
			 * Obtem a data/hora atual e escreve no pacote de requisicao
			 * a se enviado ao ITS (Internet Time Server).
			 */
			buffer[0] = NTP_MODE_CLIENT | (NTP_VERSION << 3);
			
			long currentTime = System.currentTimeMillis();
			writeTimeStamp(buffer, TRANSMIT_TIME_OFFSET, currentTime);
			
			socket.send(request);//envia pacote para o ITS.
			
            //le a reposta do ITS.
            response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            
			//System.out.println(new String(buffer));
			
			/*
			 * Obtem o resultado do servidor.
			 */
			long originateTime	= this.readTimeStamp(buffer, ORIGINATE_TIME_OFFSET);//data do localhost.
			long receiveTime	= this.readTimeStamp(buffer, RECEIVE_TIME_OFFSET);//data recebida do ITS.
			long transmitTime	= this.readTimeStamp(buffer, TRANSMIT_TIME_OFFSET);
			long roundTripTime	= (transmitTime - receiveTime);
			
			//diferenca de data/hora entre o sistem e o servidor global em milissegundos.
			long clockOffset 	= ((receiveTime - originateTime) + (transmitTime - currentTime))/2;
			
			ntpTime				= currentTime + clockOffset;
			this.roundTripTime	= roundTripTime;			
			
		}catch(Exception ex){
			return false;
		}finally{
			if(socket != null){
				socket.close();
			}
		}
		
		return true;
	}

	/**
	 * Clear result.
	 */
	private void clear(){
		roundTripTime	= null;
		ntpTime			= null;
	}

    private boolean hasInternet(){
    	try{
    		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    		while(interfaces.hasMoreElements()){
    			NetworkInterface i;
    			if((i = interfaces.nextElement()).isUp() && !i.isLoopback()){
    				return true;
    			}
    		}
    	}catch(Exception ex){
    		return false;
    	}
    	
    	return false;
	}
    
    /**
     * Le um numero de 32 bits "nao assinado" em 
     * formato big endian entre o offset (posicao) dada do buffer.
     */
    private long read32(byte[] buffer, int offset) {
        byte b0 = buffer[offset];
        byte b1 = buffer[offset+1];
        byte b2 = buffer[offset+2];
        byte b3 = buffer[offset+3];

        // convert signed bytes to unsigned values
        int i0 = ((b0 & 0x80) == 0x80 ? (b0 & 0x7F) + 0x80 : b0);
        int i1 = ((b1 & 0x80) == 0x80 ? (b1 & 0x7F) + 0x80 : b1);
        int i2 = ((b2 & 0x80) == 0x80 ? (b2 & 0x7F) + 0x80 : b2);
        int i3 = ((b3 & 0x80) == 0x80 ? (b3 & 0x7F) + 0x80 : b3);

        return ((long)i0 << 24) + ((long)i1 << 16) + ((long)i2 << 8) + (long)i3;
    }
    
    /**
     * Le o NTP time stamp da posicao dada (offset) no buffer e retorna-o como 
     * data/hora do sistema (millisegundos desde 1 de Janeiro de 1970).
     */    
    private long readTimeStamp(byte[] buffer, int offset) {
        long seconds = read32(buffer, offset);
        long fraction = read32(buffer, offset + 4);
        return ((seconds - OFFSET_1900_TO_1970) * 1000) + ((fraction * 1000L) / 0x100000000L);        
    }

	/**
     * Writes system time (milliseconds since January 1, 1970) as an NTP time stamp 
     * at the given offset in the buffer.
     */    
    private void writeTimeStamp(byte[] buffer, int offset, long time) {
        long seconds = time / 1000L;
        long milliseconds = time - seconds * 1000L;
        seconds += OFFSET_1900_TO_1970;

        //escreve segundos no formato big endian.
        buffer[offset++] = (byte)(seconds >> 24);
        buffer[offset++] = (byte)(seconds >> 16);
        buffer[offset++] = (byte)(seconds >> 8);
        buffer[offset++] = (byte)(seconds);

        long fraction = milliseconds * 0x100000000L / 1000L;

        //escreve a fracao no formato big endian.
        buffer[offset++] = (byte)(fraction >> 24);
        buffer[offset++] = (byte)(fraction >> 16);
        buffer[offset++] = (byte)(fraction >> 8);
        
        //bits de baixa ordem deve ser dados aleatorios
        buffer[offset] = (byte)(Math.random() * 255.0);
    }
}