package com.atomatus.connection.http;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Proxy Connection Authenticator.
 * @author Carlos Matos {@literal @chcmatos}
 */
class ProxyAuthenticator extends Authenticator {

	private final String username;
	private final String password;
	
	/**
	 * Constructor.
	 * @param username user name.
	 * @param password user password.
	 */
	public ProxyAuthenticator(String username, String password) {
		super();
		this.username	= username;
		this.password	= password;
	}
		
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}
}