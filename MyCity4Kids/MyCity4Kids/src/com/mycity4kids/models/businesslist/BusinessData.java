package com.mycity4kids.models.businesslist;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.category.AdvancedSearch;
import com.mycity4kids.models.category.AgeGroup;
import com.mycity4kids.models.category.Filters;
import com.mycity4kids.models.category.SortBy;

public class BusinessData implements Parcelable {
	
	private int total;
	private int page_count;
	
	public int getPage_count() {
		return page_count;
	}
	public void setPage_count(int page_count) {
		this.page_count = page_count;
	}

	private ArrayList<BusinessDataListing> data;
	/**
	 * these there array will come in case of business Search List 
	 */
	private ArrayList<SortBy> SortBy;
	private ArrayList<Filters> Filters;
	private ArrayList<AdvancedSearch> AdvancedSearch;
	private ArrayList<AgeGroup> AgeGroup;
	
	
	
	public ArrayList<AgeGroup> getAgeGroup() {
		return AgeGroup;
	}
	public void setAgeGroup(ArrayList<AgeGroup> ageGroup) {
		AgeGroup = ageGroup;
	}
	public ArrayList<SortBy> getSortBy() {
		return SortBy;
	}
	public void setSortBy(ArrayList<SortBy> sortBy) {
		SortBy = sortBy;
	}
	public ArrayList<Filters> getFilters() {
		return Filters;
	}
	public void setFilters(ArrayList<Filters> filters) {
		Filters = filters;
	}
	public ArrayList<AdvancedSearch> getAdvancedSearch() {
		return AdvancedSearch;
	}
	public void setAdvancedSearch(ArrayList<AdvancedSearch> advancedSearch) {
		AdvancedSearch = advancedSearch;
	}
	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	/**
	 * @return the data
	 */
	public ArrayList<BusinessDataListing> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(ArrayList<BusinessDataListing> data) {
		this.data = data;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		
		dest.writeInt(total);
		dest.writeTypedList(data);
		if(SortBy!=null && !SortBy.isEmpty()){
			dest.writeTypedList(SortBy);
		}
		if(Filters!=null && !Filters.isEmpty()){
			dest.writeTypedList(Filters);
		}
		if(AdvancedSearch!=null && !AdvancedSearch.isEmpty()){
			dest.writeTypedList(AdvancedSearch);
		}
		if(AgeGroup!=null && ! AgeGroup.isEmpty()){
			dest.writeTypedList(AgeGroup);
		}
	}

	public BusinessData(Parcel in)
	{
		this.total = in.readInt();
		in.readTypedList(data, BusinessDataListing.CREATOR);
		if(SortBy!=null && !SortBy.isEmpty()){
			in.readTypedList(SortBy, com.mycity4kids.models.category.SortBy.CREATOR);
		}
		if(Filters!=null && !Filters.isEmpty()){
			in.readTypedList(Filters,com.mycity4kids.models.category.Filters.CREATOR);
		}
		if(AdvancedSearch!=null && !AdvancedSearch.isEmpty()){
			in.readTypedList(AdvancedSearch, com.mycity4kids.models.category.AdvancedSearch.CREATOR);
		}
		if(AgeGroup!=null && !AgeGroup.isEmpty()){
			in.readTypedList(AgeGroup, com.mycity4kids.models.category.AgeGroup.CREATOR);
		}
		
	}

	public static final Parcelable.Creator<BusinessData>	CREATOR	= new Parcelable.Creator<BusinessData>()
			{

		public BusinessData createFromParcel(Parcel in)
		{
			return new BusinessData(in);
		}

		public BusinessData[] newArray(int size)
		{
			return new BusinessData[size];
		}
			};



}
