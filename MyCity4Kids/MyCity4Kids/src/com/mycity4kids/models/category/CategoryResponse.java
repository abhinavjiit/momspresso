package com.mycity4kids.models.category;

import java.util.ArrayList;

import com.mycity4kids.models.basemodel.BaseDataModel;


public class CategoryResponse extends BaseDataModel{
	private float  version;
	private ArrayList<GroupCategoryData> data;
	
	/**
	 * @return the data
	 */
	public ArrayList<GroupCategoryData> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(ArrayList<GroupCategoryData> data) {
		this.data = data;
	}
	public float getVersion() {
		return version;
	}
	public void setVersion(float version) {
		this.version = version;
	}
}
