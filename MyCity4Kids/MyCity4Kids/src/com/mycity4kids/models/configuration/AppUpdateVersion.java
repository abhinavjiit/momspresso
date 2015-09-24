package com.mycity4kids.models.configuration;

public class AppUpdateVersion {
	private String version;
	private UpdateVersionData data;
	
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public UpdateVersionData getData() {
		return data;
	}
	public void setData(UpdateVersionData data) {
		this.data = data;
	}

}
