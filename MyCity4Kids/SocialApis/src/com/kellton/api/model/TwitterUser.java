/**
 * 
 */
package com.kellton.api.model;

import twitter4j.User;

/**
 * @author Shashank.agarwal
 *
 */
public class TwitterUser {
	User user ;
	long userID ; 
	String token ; 
	String secret ; 
	
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
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	public User getUser() {
		return user;
	}

	/**
	 * @return the userID
	 */
	public long getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(long userID) {
		this.userID = userID;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
