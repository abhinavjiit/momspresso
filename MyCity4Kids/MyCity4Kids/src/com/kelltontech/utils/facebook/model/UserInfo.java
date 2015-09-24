package com.kelltontech.utils.facebook.model;

import java.util.ArrayList;

/**
 * 
 * @author monish.agarwal
 *
 */
public class UserInfo 
{
	
	String locale;
	String link;
	String updated_time;
	String id;
	String first_name;
	String timezone;
	String username;
	String verified;
	String name;
	String last_name;
	String gender;
	ArrayList<Work> work;
	Base hometown;
	Base location;
	ArrayList<Education> education;
	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * @return the updated_time
	 */
	public String getUpdated_time() {
		return updated_time;
	}
	/**
	 * @param updated_time the updated_time to set
	 */
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the first_name
	 */
	public String getFirst_name() {
		return first_name;
	}
	/**
	 * @param first_name the first_name to set
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}
	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the verified
	 */
	public String getVerified() {
		return verified;
	}
	/**
	 * @param verified the verified to set
	 */
	public void setVerified(String verified) {
		this.verified = verified;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the last_name
	 */
	public String getLast_name() {
		return last_name;
	}
	/**
	 * @param last_name the last_name to set
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the work
	 */
	public ArrayList<Work> getWork() {
		return work;
	}
	/**
	 * @param work the work to set
	 */
	public void setWork(ArrayList<Work> work) {
		this.work = work;
	}
	/**
	 * @return the hometown
	 */
	public Base getHometown() {
		return hometown;
	}
	/**
	 * @param hometown the hometown to set
	 */
	public void setHometown(Base hometown) {
		this.hometown = hometown;
	}
	/**
	 * @return the location
	 */
	public Base getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(Base location) {
		this.location = location;
	}
	/**
	 * @return the education
	 */
	public ArrayList<Education> getEducation() {
		return education;
	}
	/**
	 * @param education the education to set
	 */
	public void setEducation(ArrayList<Education> education) {
		this.education = education;
	}

}
