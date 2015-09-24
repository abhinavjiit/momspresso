package com.mycity4kids.models.profile;

import com.kelltontech.model.BaseModel;

public class ProfileModel extends BaseModel {
	private String userId;
	private String MobileNumber;
	private String emailId;
	private String name;
	private String cityId;
	private String localityId;
	private String profileId;
	private String cityName;
	private String localityName;
	private String parentType;
	private ProfileKidsInfo KidsInformation;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMobileNumber() {
		return MobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getLocalityId() {
		return localityId;
	}
	public void setLocalityId(String localityId) {
		this.localityId = localityId;
	}
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getLocalityName() {
		return localityName;
	}
	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}
	public String getParentType() {
		return parentType;
	}
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}
	public ProfileKidsInfo getKidsInformation() {
		return KidsInformation;
	}
	public void setKidsInformation(ProfileKidsInfo KidsInformation) {
		this.KidsInformation = KidsInformation;
	}
}
