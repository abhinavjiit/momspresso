package com.mycity4kids.models.category;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class CategoryData extends BaseDataModel{
	private MainCategory MainCategory;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MainCategory getMainCategory() {
		return MainCategory;
	}

	public void setMainCategory(MainCategory mainCategory) {
		MainCategory = mainCategory;
	}

}
