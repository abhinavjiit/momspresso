package com.mycity4kids.models.category;

import java.util.ArrayList;

import com.kelltontech.model.BaseModel;

public class GroupCategory extends BaseModel {
	
	private String name;
	private ArrayList<CategoryData> MainCategories;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the mainCategories
	 */
	public ArrayList<CategoryData> getMainCategories() {
		return MainCategories;
	}
	/**
	 * @param mainCategories the mainCategories to set
	 */
	public void setMainCategories(ArrayList<CategoryData> mainCategories) {
		MainCategories = mainCategories;
	}
	
	

}
