package com.mycity4kids.models.parentingfilter;

public class ParentingSearchRequest {
private String query;
private String parentingType;
private String filerType;
private int cityId;
private boolean isCommingFromSearch;
private String page;
private String sortBy;

public String getSortBy() {
	return sortBy;
}
public void setSortBy(String sortBy) {
	this.sortBy = sortBy;
}
public String getPage() {
	return page;
}
public void setPage(String page) {
	this.page = page;
}
public boolean isCommingFromSearch() {
	return isCommingFromSearch;
}
public void setCommingFromSearch(boolean isCommingFromSearch) {
	this.isCommingFromSearch = isCommingFromSearch;
}
public String getQuery() {
	return query;
}
public void setQuery(String query) {
	this.query = query;
}
public String getParentingType() {
	return parentingType;
}
public void setParentingType(String parentingType) {
	this.parentingType = parentingType;
}
public String getFilerType() {
	return filerType;
}
public void setFilerType(String filerType) {
	this.filerType = filerType;
}
public int getCityId() {
	return cityId;
}
public void setCityId(int cityId) {
	this.cityId = cityId;
}
}
