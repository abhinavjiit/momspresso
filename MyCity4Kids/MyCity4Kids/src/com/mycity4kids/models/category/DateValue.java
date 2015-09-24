package com.mycity4kids.models.category;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class DateValue extends BaseDataModel{
private String key;
private String value;
private boolean isSelected;

public boolean isSelected() {
	return isSelected;
}
public void setSelected(boolean isSelected) {
	this.isSelected = isSelected;
}
public String getKey() {
	return key;
}
public void setKey(String key) {
	this.key = key;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
}
