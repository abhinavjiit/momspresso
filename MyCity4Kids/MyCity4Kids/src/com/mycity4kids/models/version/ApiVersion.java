package com.mycity4kids.models.version;

import java.util.ArrayList;

public class ApiVersion {
	private float versionNumber;
	private ArrayList<ModifiedApiList> ModifiedApiList;
	
	public float getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(float versionNumber) {
		this.versionNumber = versionNumber;
	}
	public ArrayList<ModifiedApiList> getModifiedApiList() {
		return ModifiedApiList;
	}
	public void setModifiedApiList(ArrayList<ModifiedApiList> modifiedApiList) {
		ModifiedApiList = modifiedApiList;
	}

}
