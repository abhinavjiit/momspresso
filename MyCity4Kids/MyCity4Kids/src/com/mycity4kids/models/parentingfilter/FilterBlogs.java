package com.mycity4kids.models.parentingfilter;

import android.os.Parcel;
import android.os.Parcelable;

public class FilterBlogs implements Parcelable{
	private String author_id;
	private String title;

	public String getAuthor_id() {
		return author_id;
	}
	public void setAuthor_id(String author_id) {
		this.author_id = author_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public FilterBlogs()
	{

	}

	/**
	 * Reconstruct from the Parcel
	 * 
	 * @param source
	 */
	public FilterBlogs(Parcel source)
	{
		this.author_id = source.readString();
		this.title=source.readString();


	}
	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(author_id);
		dest.writeString(title);


	}

	public static Parcelable.Creator<FilterBlogs>	CREATOR	= new Parcelable.Creator<FilterBlogs>() {
		public FilterBlogs createFromParcel(Parcel source) {
			return new FilterBlogs(source);
		}

		public FilterBlogs[] newArray(int size) {
			return new FilterBlogs[size];
		}
	};
}
