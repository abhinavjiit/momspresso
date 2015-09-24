package com.mycity4kids.models.locality;

import java.util.ArrayList;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class LocalityData extends BaseDataModel{
	private int id;
	private String name;
	
	private ArrayList<Localities> Localities;
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
	public ArrayList<Localities> getLocalities() {
		return Localities;
	}
	public void setLocalities(ArrayList<Localities> localities) {
		Localities = localities;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
