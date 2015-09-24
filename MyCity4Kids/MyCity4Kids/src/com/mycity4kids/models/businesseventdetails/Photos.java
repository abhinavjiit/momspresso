package com.mycity4kids.models.businesseventdetails;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Photos implements Parcelable{
	private ArrayList<String> business_pics;
	private ArrayList<String> user_uploaded;
	private ArrayList<String> event_pics;
	
	public ArrayList<String> getBusiness_pics() {
		return business_pics;
	}
	public ArrayList<String> getEvent_pics() {
		return event_pics;
	}
	public void setEvent_pics(ArrayList<String> event_pics) {
		this.event_pics = event_pics;
	}
	public void setBusiness_pics(ArrayList<String> business_pics) {
		this.business_pics = business_pics;
	}
	public ArrayList<String> getUser_uploaded() {
		return user_uploaded;
	}
	public void setUser_uploaded(ArrayList<String> user_uploaded) {
		this.user_uploaded = user_uploaded;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if(business_pics!=null && !business_pics.isEmpty())
		{
			dest.writeStringList(business_pics);
		}
		if(user_uploaded!=null && !user_uploaded.isEmpty())
		{
			dest.writeStringList(user_uploaded);
		}
		if(event_pics!=null && !event_pics.isEmpty())
		{
			dest.writeStringList(event_pics);
		}
		
		

	}

	public Photos(Parcel in)
	{
		if(business_pics!=null && !business_pics.isEmpty())
		{
			in.readStringList(business_pics);
		}
		if(user_uploaded!=null && !user_uploaded.isEmpty())
		{
			in.readStringList(user_uploaded);
		}
		if(event_pics!=null && !event_pics.isEmpty())
		{
			in.readStringList(event_pics);
		}
		
		
		
		
	}
	public static final Parcelable.Creator<Photos>	CREATOR	= new Parcelable.Creator<Photos>()
			{

		public Photos createFromParcel(Parcel in)
		{
			return new Photos(in);
		}

		public Photos[] newArray(int size)
		{
			return new Photos[size];
		}
			};
	

}
