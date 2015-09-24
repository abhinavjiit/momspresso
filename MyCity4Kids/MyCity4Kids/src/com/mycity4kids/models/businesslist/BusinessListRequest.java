package com.mycity4kids.models.businesslist;

public class BusinessListRequest {

	private String city_id;
	private String category_id;
	private String sub_category_id;
	private String page;
	private String zone_id;
	private String locality_id;
	private String sort_by;
	private String querySearch;
	private String localitySearch;
	private String latitude;
	private String longitude;
	private String age_group;
	private String date_by;
	private String activities;
	private String more;
	private String totalFilterValues;
	
	
	
	public String getTotalFilterValues() {
		return totalFilterValues;
	}
	public void setTotalFilterValues(String totalFilterValues) {
		this.totalFilterValues = totalFilterValues;
	}
	public String getMore() {
		return more;
	}
	public void setMore(String more) {
		this.more = more;
	}
	public String getActivities() {
		return activities;
	}
	public void setActivities(String activities) {
		this.activities = activities;
	}
	public String getDate_by() {
		return date_by;
	}
	public void setDate_by(String date_by) {
		this.date_by = date_by;
	}
	public String getAge_group() {
		return age_group;
	}
	public void setAge_group(String age_group) {
		this.age_group = age_group;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the city_id
	 */
	public String getCity_id() {
		return city_id;
	}
	/**
	 * @param city_id the city_id to set
	 */
	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}
	/**
	 * @return the category_id
	 */
	public String getCategory_id() {
		return category_id;
	}
	/**
	 * @param category_id the category_id to set
	 */
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	/**
	 * @return the sub_category_id
	 */
	public String getSub_category_id() {
		return sub_category_id;
	}
	/**
	 * @param sub_category_id the sub_category_id to set
	 */
	public void setSub_category_id(String sub_category_id) {
		this.sub_category_id = sub_category_id;
	}
	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}
	/**
	 * @param page the page to set
	 */
	public void setPage(String page) {
		this.page = page;
	}
	/**
	 * @return the zone_id
	 */
	public String getZone_id() {
		return zone_id;
	}
	/**
	 * @param zone_id the zone_id to set
	 */
	public void setZone_id(String zone_id) {
		this.zone_id = zone_id;
	}
	/**
	 * @return the locality_id
	 */
	public String getLocality_id() {
		return locality_id;
	}
	/**
	 * @param locality_id the locality_id to set
	 */
	public void setLocality_id(String locality_id) {
		this.locality_id = locality_id;
	}
	/**
	 * @return the sort_by
	 */
	public String getSort_by() {
		return sort_by;
	}
	/**
	 * @param sort_by the sort_by to set
	 */
	public void setSort_by(String sort_by) {
		this.sort_by = sort_by;
	}
	
	public String getQuerySearch() {
		return querySearch;
	}
	public void setQuerySearch(String querySearch) {
		this.querySearch = querySearch;
	}
	public String getLocalitySearch() {
		return localitySearch;
	}
	public void setLocalitySearch(String localitySearch) {
		this.localitySearch = localitySearch;
	}
}
