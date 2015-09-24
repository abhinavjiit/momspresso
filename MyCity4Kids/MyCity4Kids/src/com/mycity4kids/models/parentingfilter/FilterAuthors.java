package com.mycity4kids.models.parentingfilter;

import android.os.Parcel;
import android.os.Parcelable;

public class FilterAuthors implements Parcelable{
	private int author_id;
	private String name;
	private String blog_title;
	private String about_author;
	private String author_image;

	public int getAuthor_id() {
		return author_id;
	}
	public void setAuthor_id(int author_id) {
		this.author_id = author_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBlog_title() {
		return blog_title;
	}
	public void setBlog_title(String blog_title) {
		this.blog_title = blog_title;
	}
	public String getAbout_author() {
		return about_author;
	}
	public void setAbout_author(String about_author) {
		this.about_author = about_author;
	}
	public String getAuthor_image() {
		return author_image;
	}
	public void setAuthor_image(String author_image) {
		this.author_image = author_image;
	}

	public FilterAuthors()
	{

	}

	/**
	 * Reconstruct from the Parcel
	 * 
	 * @param source
	 */
	public FilterAuthors(Parcel source)
	{
		this.author_id = source.readInt();
		this.name=source.readString();
		this.blog_title=source.readString();
		this.about_author=source.readString();
		this.author_image=source.readString();

	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeInt(author_id);
		dest.writeString(name);
		dest.writeString(blog_title);
		dest.writeString(about_author);
		dest.writeString(author_image);


	}

	public static Parcelable.Creator<FilterAuthors>	CREATOR	= new Parcelable.Creator<FilterAuthors>() {
		public FilterAuthors createFromParcel(Parcel source) {
			return new FilterAuthors(source);
		}

		public FilterAuthors[] newArray(int size) {
			return new FilterAuthors[size];
		}
	};

}
