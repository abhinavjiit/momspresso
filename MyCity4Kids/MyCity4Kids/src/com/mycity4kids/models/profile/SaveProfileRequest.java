package com.mycity4kids.models.profile;

import java.util.ArrayList;

import com.kelltontech.model.BaseModel;

public class SaveProfileRequest extends BaseModel {
	private String userId;
	private String MobileNumber;
	private String emailId;
	private String name;
	private String cityId;
	private String localityId;
	private String profileId;
//	private String parentType;
	private ArrayList<KidsInformation> KidsInfo;
	private String sessionId;

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
/*	public String getParentType() {
		return parentType;
	}
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}*/
	public ArrayList<KidsInformation> getKidsInformation() {
		return KidsInfo;
	}
	public void setKidsInformation(ArrayList<KidsInformation> KidsInfo) {
		this.KidsInfo = KidsInfo;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
