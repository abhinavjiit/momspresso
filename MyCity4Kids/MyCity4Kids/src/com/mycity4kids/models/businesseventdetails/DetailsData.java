package com.mycity4kids.models.businesseventdetails;

import java.util.ArrayList;

import com.mycity4kids.models.businesslist.BusinessDataListing;

/**
 * 
 * @author deepanker.chaudhary
 *
 */
public class DetailsData {
	private BusinessDataListing info;
	private DetailsGallery gallery;
	private ArrayList<DetailsReviews> reviews;
	private DetailMap map;
	
	

	public BusinessDataListing getInfo() {
		return info;
	}
	public void setInfo(BusinessDataListing info) {
		this.info = info;
	}
	public DetailsGallery getGallery() {
		return gallery;
	}
	public void setGallery(DetailsGallery gallery) {
		this.gallery = gallery;
	}
	
	public ArrayList<DetailsReviews> getReviews() {
		return reviews;
	}
	public void setReviews(ArrayList<DetailsReviews> reviews) {
		this.reviews = reviews;
	}
	public DetailMap getMap() {
		return map;
	}
	public void setMap(DetailMap map) {
		this.map = map;
	}

}
