package com.mycity4kids.ui.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.recentlyviewed.RecentlyViewedModel;

public class RecentlyViewedAdapter extends BaseAdapter {

	private List<RecentlyViewedModel> mBusinessData;
	private LayoutInflater mInflator;
	private int mBusinessOrEventType;

	public RecentlyViewedAdapter(Context pContext){
		mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setListData(List<RecentlyViewedModel> listData, int pBusinessOrEventType){
		mBusinessData = listData;
		mBusinessOrEventType=pBusinessOrEventType;
	}

	@Override
	public int getCount() {
		return mBusinessData == null ? 0 : mBusinessData.size();
	}

	@Override
	public Object getItem(int position) {
		return mBusinessData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder holder;
		if (view == null) {
			view = mInflator.inflate(R.layout.list_item_business, null);
			holder = new ViewHolder();
			holder.txvName = (TextView) view.findViewById(R.id.txvName);
			holder.txvAddress = (TextView) view.findViewById(R.id.txvAddress);
			holder.txvActivities = (TextView) view.findViewById(R.id.txvDescription);
			holder.txvDistance = (TextView) view.findViewById(R.id.txvDistance);
			holder.btnBookNow = (TextView) view.findViewById(R.id.txvBookNow);
			if(mBusinessOrEventType==Constants.EVENT_PAGE_TYPE)
			{
			holder.txvDate = (TextView) view.findViewById(R.id.start_end_date);
			holder.txvMonth = (TextView) view.findViewById(R.id.month_txt);
			holder.txvAgeGroup = (TextView) view.findViewById(R.id.age_group_txt);
			holder.axvAgeGroupRatio = (TextView) view.findViewById(R.id.age_group_value);
			}
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.txvDistance.setVisibility(View.VISIBLE);
		holder.txvName.setText(mBusinessData.get(position).getName());
		holder.txvAddress.setText(mBusinessData.get(position).getLocality());
		holder.txvActivities.setText(mBusinessData.get(position).getActivities());
		String difference = mBusinessData.get(position).getDistance();
		if(!difference.equals("") && !difference.equals("0")){
		double diff=Double.parseDouble(difference);
		DecimalFormat df = new DecimalFormat("####0.0");
		difference=df.format(diff);
		holder.txvDistance.setText(difference + " Km");
		}else{
			holder.txvDistance.setVisibility(View.GONE);
		}
		
		/*if(mBusinessOrEventType==Constants.EVENT_PAGE_TYPE){
			holder.txvDistance.setVisibility(View.GONE);
			holder.txvDate.setVisibility(View.VISIBLE);
			holder.txvMonth.setVisibility(View.VISIBLE);
			holder.txvAgeGroup.setVisibility(View.VISIBLE);
			holder.axvAgeGroupRatio.setVisibility(View.VISIBLE);
			
		//	String[] startDateValues=getDateValues(mBusinessData.get(position).getStart_date());
		//	String[] endDateValues=getDateValues(mBusinessData.get(position).getEnd_date());
			if(startDateValues!=null && endDateValues!=null){
				holder.txvDate.setText(startDateValues[0]+"-"+endDateValues[0]);
				holder.txvMonth.setText(endDateValues[1]);
			}
			holder.axvAgeGroupRatio.setText(Math.round(Float.parseFloat(mBusinessData.get(position).getStartagegroup()))+"-"+Math.round(Float.parseFloat(mBusinessData.get(position).getEndagegroup())));
			
			mBusinessData.get(position).getEnd_date();
			
		}*/
		
		
		return view;
	}
	
	
	class ViewHolder{
		TextView txvName;
		TextView txvAddress;
		TextView txvActivities;
		TextView txvDistance;
		TextView txvDate;
		TextView txvMonth;
		TextView txvAgeGroup;
		TextView axvAgeGroupRatio;
		TextView btnBookNow;
	}
private String[] getDateValues(String date){
	String[] datesValue=null;
	try {
		String formatedDate=DateTimeUtils.changeDate(date);
		
		datesValue=formatedDate.split(" ");
		/*DateFormat format = new SimpleDateFormat("dd MMM yyyy",Locale.US);
		cal.setTime(format.parse(formatedDate));*/
	} catch (Exception e) {
		return null;
	}
	
	return datesValue;
	
}
}
