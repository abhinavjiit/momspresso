package com.mycity4kids.models.businesseventdetails;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactDetail implements Parcelable{
	private String contact_no;
	private String contact_person;
	private String email;
	private String website;
	
	
	
	public String getContact_no() {
		return contact_no;
	}

	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}

	public String getContact_person() {
		return contact_person;
	}

	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * Default constructor
	 */
	public ContactDetail() {
		// nothing to do here
	}

	/**
	 * @param in
	 *            , to create object from Parcel
	 */
	public ContactDetail(Parcel in) {
		this.contact_no = in.readString();
		this.contact_person = in.readString();
		this.email = in.readString();
		this.website = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(contact_no);
		dest.writeString(contact_person);
		dest.writeString(email);
		dest.writeString(website);
	}

	/**
	 * to create list of objects to/from parcel
	 */
	public static final Parcelable.Creator<ContactDetail> CREATOR = new Parcelable.Creator<ContactDetail>() {

		public ContactDetail createFromParcel(Parcel in) {
			return new ContactDetail(in);
		}

		public ContactDetail[] newArray(int size) {
			return new ContactDetail[size];
		}
	};

	@Override
	public int describeContents() {
		// nothing to do here
		return 0;
	}
}
