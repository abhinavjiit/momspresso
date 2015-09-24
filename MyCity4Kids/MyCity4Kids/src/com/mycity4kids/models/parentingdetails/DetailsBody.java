package com.mycity4kids.models.parentingdetails;

import java.util.ArrayList;

public class DetailsBody {
	private String text;
	private ArrayList<ImageData> image;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ArrayList<ImageData> getImage() {
		return image;
	}
	public void setImage(ArrayList<ImageData> image) {
		this.image = image;
	}
	

}
