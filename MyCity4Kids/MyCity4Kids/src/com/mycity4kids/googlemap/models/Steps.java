package com.mycity4kids.googlemap.models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 
 * @author Deepanker Chaudhary
 *
 */

public class Steps implements Parcelable{
	
	private Location end_location;
	private Location start_location;
	private String travel_mode;
	private String html_instructions;
	
	public String getTravel_mode() {
		return travel_mode;
	}
	public void setTravel_mode(String travel_mode) {
		this.travel_mode = travel_mode;
	}
	public String getHtml_instructions() {
		return html_instructions;
	}
	public void setHtml_instructions(String html_instructions) {
		this.html_instructions = html_instructions;
	}
	/**
	 * @return the end_location
	 */
	public Location getEnd_location() {
		return end_location;
	}
	/**
	 * @param end_location the end_location to set
	 */
	public void setEnd_location(Location end_location) {
		this.end_location = end_location;
	}
	/**
	 * @return the start_location
	 */
	public Location getStart_location() {
		return start_location;
	}
	/**
	 * @param start_location the start_location to set
	 */
	public void setStart_location(Location start_location) {
		this.start_location = start_location;
	}
	
	
	public Steps(Parcel in){
		readFromParcel(in);
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	dest.writeParcelable(start_location, flags);
	dest.writeParcelable(end_location, flags);
		
	}
	private void readFromParcel(Parcel in) {   
		start_location=in.readParcelable(Location.class.getClassLoader());
		end_location=in.readParcelable(Location.class.getClassLoader());
	}
	
	
	public static final Parcelable.Creator<Steps> CREATOR = new Parcelable.Creator<Steps>() { 
		public Steps createFromParcel(Parcel in) { 
			return new Steps(in); 
			}   
		public Steps[] newArray(int size) { 
			return new Steps[size];
			} 
		}; 
	
}
