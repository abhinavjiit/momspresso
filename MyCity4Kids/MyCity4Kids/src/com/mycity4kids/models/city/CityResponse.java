package com.mycity4kids.models.city;

import java.util.ArrayList;

public class CityResponse {
	private float version;
	private ArrayList<CityData> data;
	
	public float getVersion() {
		return version;
	}
	public void setVersion(float version) {
		this.version = version;
	}
	public ArrayList<CityData> getData() {
		return data;
	}
	public void setData(ArrayList<CityData> data) {
		this.data = data;
	}

}
