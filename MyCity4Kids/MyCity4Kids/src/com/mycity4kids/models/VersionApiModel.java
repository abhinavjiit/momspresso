package com.mycity4kids.models;

/**
 * this model will be user for save current api versions :- this model saved in shared pref :)
 * @author Deepanker Chaudhary
 *
 */
public class VersionApiModel {
	private float cityVersion;
	private float localityVersion;
	private float categoryVersion;
	public int cityId;
	private String appUpdateVersion;
	
	
	public String getAppUpdateVersion() {
		return appUpdateVersion;
	}
	public void setAppUpdateVersion(String appUpdateVersion) {
		this.appUpdateVersion = appUpdateVersion;
	}
	public float getCityVersion() {
		return cityVersion;
	}
	public void setCityVersion(float cityVersion) {
		this.cityVersion = cityVersion;
	}
	public float getLocalityVersion() {
		return localityVersion;
	}
	public void setLocalityVersion(float localityVersion) {
		this.localityVersion = localityVersion;
	}
	public float getCategoryVersion() {
		return categoryVersion;
	}
	public void setCategoryVersion(float categoryVersion) {
		this.categoryVersion = categoryVersion;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

}
