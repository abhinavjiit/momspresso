package com.mycity4kids.models.category;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class Activities extends BaseDataModel{
	
	private int id;
	private String name;
	private boolean isSelected;
	
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Activities that = (Activities) o;

		return id == that.id;

	}

	@Override
	public int hashCode() {
		return id;
	}
}
