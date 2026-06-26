package de.gimik.apps.gpstracker.backend.web;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class UsernamePasswordAppAuthenticationToken extends UsernamePasswordAuthenticationToken {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appCode;
	public UsernamePasswordAppAuthenticationToken(Object principal, Object credentials,String appCode) {
		super(principal, credentials);
		this.appCode = appCode;
		// TODO Auto-generated constructor stub
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

}
