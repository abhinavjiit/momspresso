package com.mycity4kids.models.businesseventdetails;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class GalleryListtingData implements Parcelable{
	private String imageUrl="";
	private Bitmap imageBitmap;
	private String playImageUrl="";
	private String imagePath="";
	
	
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getPlayImageUrl() {
		return playImageUrl;
	}
	public void setPlayImageUrl(String playImageUrl) {
		this.playImageUrl = playImageUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Bitmap getImageBitmap() {
		return imageBitmap;
	}
	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}
	/**
	 * Default constructor
	 */
	public GalleryListtingData() {
		// nothing to do here
	}

	/**
	 * @param in
	 *            , to create object from Parcel
	 */
	public GalleryListtingData(Parcel in) {
		this.imageUrl = in.readString();
	//	this.imageBitmap=in.readParcelable(Bitmap.class.getClassLoader());
		this.playImageUrl = in.readString();
		this.imagePath=in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(imageUrl);
//		dest.writeValue(imageBitmap);
		dest.writeString(playImageUrl);
		dest.writeString(imagePath);
	}

	/**
	 * to create list of objects to/from parcel
	 */
	public static final Parcelable.Creator<GalleryListtingData> CREATOR = new Parcelable.Creator<GalleryListtingData>() {

		public GalleryListtingData createFromParcel(Parcel in) {
			return new GalleryListtingData(in);
		}

		public GalleryListtingData[] newArray(int size) {
			return new GalleryListtingData[size];
		}
	};

	@Override
	public int describeContents() {
		// nothing to do here
		return 0;
	}
}
