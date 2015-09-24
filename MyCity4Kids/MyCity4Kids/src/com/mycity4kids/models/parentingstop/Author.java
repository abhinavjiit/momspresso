package com.mycity4kids.models.parentingstop;

import java.io.Serializable;

public class Author implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = -3670551891184117487L;
private int id;
private String first_name;
private String last_name;
private String about_user;
private String profile_image;


public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getFirst_name() {
	return first_name;
}
public void setFirst_name(String first_name) {
	this.first_name = first_name;
}
public String getLast_name() {
	return last_name;
}
public void setLast_name(String last_name) {
	this.last_name = last_name;
}
public String getAbout_user() {
	return about_user;
}
public void setAbout_user(String about_user) {
	this.about_user = about_user;
}
public String getProfile_image() {
	return profile_image;
}
public void setProfile_image(String profile_image) {
	this.profile_image = profile_image;
}

	
}
