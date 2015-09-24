package com.mycity4kids.enums;
/**
 * 
 * @author deepanker.chaudhary
 * these string are the perameters for parenting search filter type which will use in parentingFilterSearchController.
 *
 */
public enum SearchListType {
Authors("authors"),Blogs("blogs"),Bloggers("authors"),Tags("tags"),Topics("topics"),None("none");

private String mSearchListType;

private SearchListType(String pSearchListType){
	mSearchListType=pSearchListType;
}

public String getSearchListType(){
	return mSearchListType;
}
}
