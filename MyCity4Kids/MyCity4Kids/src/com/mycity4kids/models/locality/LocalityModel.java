package com.mycity4kids.models.locality;
/**
 * 
 * @author deepanker chaudhary
 * Basically I am using this model for containing LocalityName & Id from LocalityTable:
 *
 */
public class LocalityModel {
	private int localityId;
	private String localityName;
	private boolean selected ;
	private int zoneId;
	private String zoneName;
	
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	
	
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public int getLocalityId() {
		return localityId;
	}
	public void setLocalityId(int localityId) {
		this.localityId = localityId;
	}
	public String getLocalityName() {
		return localityName;
	}
	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}
	
	@Override
	public String toString() {
		return localityName;
	}

	

}
