package com.mycity4kids.models.parentingstop;

import java.io.Serializable;

public class Articles implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1168127905949437843L;
	private int id;
	private String title;
	private String body;
	private String created;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}

}
