package com.mycity4kids.models.locality;

import com.kelltontech.model.BaseModel;

public class Localities extends BaseModel{
	private String name;
	private int id;
	private int zoneId;
	private String zoneCity;
	
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public String getZoneCity() {
		return zoneCity;
	}
	public void setZoneCity(String zoneCity) {
		this.zoneCity = zoneCity;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	

}
