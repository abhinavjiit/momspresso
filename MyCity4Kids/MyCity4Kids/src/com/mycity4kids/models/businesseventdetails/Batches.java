package com.mycity4kids.models.businesseventdetails;

import android.os.Parcel;
import android.os.Parcelable;

public class Batches implements Parcelable{
	private String batchname;
	private String startagegroup;
	private String endagegroup;
	private String start_date_time;
	private String end_date_time;
	private String last_date;
	private String activitiesnames;
	private String event_id;
	private String cost;
	private String date;
	private String timings;
	private String agegroup;
	
	

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTimings() {
		return timings;
	}

	public void setTimings(String timings) {
		this.timings = timings;
	}

	public String getAgegroup() {
		return agegroup;
	}

	public void setAgegroup(String agegroup) {
		this.agegroup = agegroup;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getBatchname() {
		return batchname;
	}

	public void setBatchname(String batchname) {
		this.batchname = batchname;
	}

	public String getStartagegroup() {
		return startagegroup;
	}

	public void setStartagegroup(String startagegroup) {
		this.startagegroup = startagegroup;
	}

	public String getEndagegroup() {
		return endagegroup;
	}

	public void setEndagegroup(String endagegroup) {
		this.endagegroup = endagegroup;
	}

	public String getStart_date_time() {
		return start_date_time;
	}

	public void setStart_date_time(String start_date_time) {
		this.start_date_time = start_date_time;
	}

	public String getEnd_date_time() {
		return end_date_time;
	}

	public void setEnd_date_time(String end_date_time) {
		this.end_date_time = end_date_time;
	}

	public String getLast_date() {
		return last_date;
	}

	public void setLast_date(String last_date) {
		this.last_date = last_date;
	}

	public String getActivitiesnames() {
		return activitiesnames;
	}

	public void setActivitiesnames(String activitiesnames) {
		this.activitiesnames = activitiesnames;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(batchname);
	dest.writeString(startagegroup);
	dest.writeString(endagegroup);
	dest.writeString(start_date_time);
	dest.writeString(end_date_time);
	dest.writeString(last_date);
	dest.writeString(activitiesnames);
	dest.writeString(event_id);
	dest.writeString(date);
	dest.writeString(timings);
	dest.writeString(agegroup);
	
	}

	public Batches(Parcel in)
	{
		this.batchname=in.readString();
		this.startagegroup=in.readString();
		this.endagegroup=in.readString();
		this.start_date_time=in.readString();
		this.end_date_time=in.readString();
		this.last_date=in.readString();
		this.activitiesnames=in.readString();
		this.event_id=in.readString();
		this.date=in.readString();
		this.timings=in.readString();
		this.agegroup=in.readString();
		

	}
	public static final Parcelable.Creator<Batches>	CREATOR	= new Parcelable.Creator<Batches>()
	{

		public Batches createFromParcel(Parcel in)
		{
			return new Batches(in);
		}

		public Batches[] newArray(int size)
		{
			return new Batches[size];
		}
	};
}
