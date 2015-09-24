package com.mycity4kids.enums;

/**
 * 
 * @author deepanker.chaudhary
 *
 */

public enum ParentingFilterType {
	BLOGS("blogs"),ARTICLES("articles"),TOP_PICS("top_pics");

	private String mParentingType;

	private ParentingFilterType(String pParentingType){
		mParentingType=pParentingType;
	}

	public String getParentingType(){
		return mParentingType;
	}


}
