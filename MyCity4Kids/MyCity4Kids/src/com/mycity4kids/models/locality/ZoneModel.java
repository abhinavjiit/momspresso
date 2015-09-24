package com.mycity4kids.models.locality;
/**
 * we put zone id & name from locality table
 * @author deepanker Chaudhary
 *
 */
public class ZoneModel {
private int zoneId;
private String zoneCity;
private boolean isSelected;


public boolean isSelected() {
	return isSelected;
}
public void setSelected(boolean isSelected) {
	this.isSelected = isSelected;
}
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
	
}
