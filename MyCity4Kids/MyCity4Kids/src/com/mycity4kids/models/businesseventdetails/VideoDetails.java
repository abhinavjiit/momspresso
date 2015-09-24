package com.mycity4kids.models.businesseventdetails;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoDetails implements Parcelable{

	private String url;
	private String thumbnail;

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeString(thumbnail);
	}

	public VideoDetails(Parcel in)
	{
		this.url=	in.readString();
		this.thumbnail=	in.readString();
	}
	public static final Parcelable.Creator<VideoDetails>	CREATOR	= new Parcelable.Creator<VideoDetails>()
			{

		public VideoDetails createFromParcel(Parcel in)
		{
			return new VideoDetails(in);
		}

		public VideoDetails[] newArray(int size)
		{
			return new VideoDetails[size];
		}
			};

}
