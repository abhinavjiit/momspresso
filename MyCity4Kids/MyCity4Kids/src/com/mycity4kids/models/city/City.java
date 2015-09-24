package com.mycity4kids.models.city;


/**This model user for calculate current region name according to current location
 *
 * @author Deepanker Chaudhary
 *
 */

public class City {
	private double latitude ;
	private double longitude ;
	private String cityName ;
	private int cityId;
	
	public City() {
		//Created intentionally for setting value from prefs
	}
	
	
	public City(String cityName , double latitude , double longitude,int cityId) {
		this.cityName = cityName  ; 
		this.cityId	 = cityId ; 
		this.longitude = longitude  ; 
		this.latitude=latitude;
	}
	

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

}
