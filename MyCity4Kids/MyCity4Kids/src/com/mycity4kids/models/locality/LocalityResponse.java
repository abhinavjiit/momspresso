package com.mycity4kids.models.locality;

import java.util.ArrayList;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class LocalityResponse extends BaseDataModel{
	private float version;
	private ArrayList<LocalityData> data;
	
	
	public float getVersion() {
		return version;
	}
	public void setVersion(float version) {
		this.version = version;
	}
	public ArrayList<LocalityData> getData() {
		return data;
	}
	public void setData(ArrayList<LocalityData> data) {
		this.data = data;
	}


}
