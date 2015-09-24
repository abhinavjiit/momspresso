/**
 * 
 */
package com.mycity4kids.models.businesseventdetails;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author sachin.gupta
 */
public class AdditionalInfo implements Parcelable {

	private String info_key;
	private String info_value;

	/**
	 * @return the info_key
	 */
	public String getInfo_key() {
		return info_key;
	}

	/**
	 * @param info_key
	 *            the info_key to set
	 */
	public void setInfo_key(String info_key) {
		this.info_key = info_key;
	}

	/**
	 * @return the info_value
	 */
	public String getInfo_value() {
		return info_value;
	}

	/**
	 * @param info_value
	 *            the info_value to set
	 */
	public void setInfo_value(String info_value) {
		this.info_value = info_value;
	}

	// code for Parcelable

	/**
	 * Default constructor
	 */
	public AdditionalInfo() {
		// nothing to do here
	}

	/**
	 * @param in
	 *            , to create object from Parcel
	 */
	public AdditionalInfo(Parcel in) {
		this.info_key = in.readString();
		this.info_value = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(info_key);
		dest.writeString(info_value);
	}

	/**
	 * to create list of objects to/from parcel
	 */
	public static final Parcelable.Creator<AdditionalInfo> CREATOR = new Parcelable.Creator<AdditionalInfo>() {

		public AdditionalInfo createFromParcel(Parcel in) {
			return new AdditionalInfo(in);
		}

		public AdditionalInfo[] newArray(int size) {
			return new AdditionalInfo[size];
		}
	};

	@Override
	public int describeContents() {
		// nothing to do here
		return 0;
	}
}
