package com.mycity4kids.models.businesseventdetails;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailsGallery implements Parcelable{
	private Photos photo;
	//private Videos video;
	private ArrayList<VideoDetails> video;
	private ArrayList<VideoListingDetails>  listing_videos;

	public ArrayList<VideoListingDetails> getListing_videos() {
		return listing_videos;
	}
	public void setListing_videos(ArrayList<VideoListingDetails> listing_videos) {
		this.listing_videos = listing_videos;
	}
	public Photos getPhoto() {
		return photo;
	}
	public void setPhoto(Photos photo) {
		this.photo = photo;
	}
	/*public Videos getVideo() {
		return video;
	}
	public void setVideo(Videos video) {
		this.video = video;
	}*/
	public ArrayList<VideoDetails> getVideo() {
		return video;
	}
	public void setVideo(ArrayList<VideoDetails> video) {
		this.video = video;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(photo, flags);
		dest.writeTypedList(video);
		dest.writeTypedList(listing_videos);
	}

	private DetailsGallery(){
		video=new ArrayList<VideoDetails>();
		listing_videos=new ArrayList<VideoListingDetails>();
	}
	
	public DetailsGallery(Parcel in)
	{
		this();
		this.photo=in.readParcelable(Photos.class.getClassLoader());
		in.readTypedList(video, VideoDetails.CREATOR);
		in.readTypedList(listing_videos, VideoListingDetails.CREATOR);

	}
	public static final Parcelable.Creator<DetailsGallery>	CREATOR	= new Parcelable.Creator<DetailsGallery>()
			{

		public DetailsGallery createFromParcel(Parcel in)
		{
			return new DetailsGallery(in);
		}

		public DetailsGallery[] newArray(int size)
		{
			return new DetailsGallery[size];
		}
			};
}
