package com.mycity4kids.models.profile;

import java.util.ArrayList;

public class ProfileKidsInfo {

	private int total;
	private ArrayList<KidsInformation> KidsInformation;

	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public ArrayList<KidsInformation> getKidsInformation() {
		return KidsInformation;
	}
	public void setKidsInformation(ArrayList<KidsInformation> kidsInformation) {
		KidsInformation = kidsInformation;
	} 

}
