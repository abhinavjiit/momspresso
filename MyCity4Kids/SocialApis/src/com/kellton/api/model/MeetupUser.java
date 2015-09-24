package com.kellton.api.model;

public class MeetupUser {
	
	private String token ; 
	private String verifier ;
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the verifier
	 */
	public String getVerifier() {
		return verifier;
	}
	/**
	 * @param verifier the verifier to set
	 */
	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}
}
