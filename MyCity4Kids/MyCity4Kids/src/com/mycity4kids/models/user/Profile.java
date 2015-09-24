package com.mycity4kids.models.user;

public class Profile {
	private String locality_id;
	private String id;
	private String profile_image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocality_id() {
		return locality_id;
	}

	public void setLocality_id(String locality_id) {
		this.locality_id = locality_id;
	}

	public String getProfile_image() {
		return profile_image;
	}

	public void setProfile_image(String profile_image) {
		this.profile_image = profile_image;
	}

}
