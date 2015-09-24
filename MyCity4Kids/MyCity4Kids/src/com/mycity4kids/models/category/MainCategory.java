package com.mycity4kids.models.category;

import java.util.ArrayList;

import com.mycity4kids.models.basemodel.BaseDataModel;


public class MainCategory extends BaseDataModel{
	private int id;
	private String name;
	private ArrayList<SubCategories> SubCategories;
	private ArrayList<MainFilters> Filters;
	private ArrayList<SortBy> SortBy;
	private ArrayList<AdvancedSearch> AdvancedSearch;
	private ArrayList<AgeGroup> AgeGroup;
	private ArrayList<Activities>  Activities;
	private ArrayList<DateValue> DateValue;
	
	public ArrayList<Activities> getActivities() {
		return Activities;
	}
	public void setActivities(ArrayList<Activities> activities) {
		Activities = activities;
	}
	public ArrayList<DateValue> getDateValue() {
		return DateValue;
	}
	public void setDateValue(ArrayList<DateValue> dateValue) {
		DateValue = dateValue;
	}
	public ArrayList<AgeGroup> getAgeGroup() {
		return AgeGroup;
	}
	public void setAgeGroup(ArrayList<AgeGroup> ageGroup) {
		AgeGroup = ageGroup;
	}
	public ArrayList<AdvancedSearch> getAdvancedSearch() {
		return AdvancedSearch;
	}
	public void setAdvancedSearch(ArrayList<AdvancedSearch> advancedSearch) {
		AdvancedSearch = advancedSearch;
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
	public ArrayList<SubCategories> getSubCategories() {
		return SubCategories;
	}
	public void setSubCategories(ArrayList<SubCategories> subCategories) {
		SubCategories = subCategories;
	}
	public ArrayList<MainFilters> getFilters() {
		return Filters;
	}
	public void setFilters(ArrayList<MainFilters> filters) {
		Filters = filters;
	}
	public ArrayList<SortBy> getSortBy() {
		return SortBy;
	}
	public void setSortBy(ArrayList<SortBy> sortBy) {
		SortBy = sortBy;
	}


}
