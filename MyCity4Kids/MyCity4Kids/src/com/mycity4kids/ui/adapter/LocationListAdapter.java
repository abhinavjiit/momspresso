package com.mycity4kids.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.city.MetroCity;

public class LocationListAdapter extends BaseAdapter {

	private ArrayList<MetroCity> mLocationList;
	private LayoutInflater mInflator;
	private Context mContext;
	private float density;

	public LocationListAdapter(Context pContext){
		mContext = pContext;
		mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		density = mContext.getResources().getDisplayMetrics().density;
	}

	public void setData(ArrayList<MetroCity> pLocationList) {
		mLocationList = pLocationList;
	}

	@Override
	public int getCount() {
		return mLocationList.size();
	}

	@Override
	public Object getItem(int position) {
		return mLocationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = mInflator.inflate(R.layout.list_item_location, null);
			holder = new ViewHolder();
			holder.txvLocation = (TextView) view.findViewById(R.id.txvLocation);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (position == 0 || position == 2) {
		//	holder.txvLocation.setTextSize(12*density);
			holder.txvLocation.setTextColor(mContext.getResources().getColor(R.color.btn_bg_blue));
			holder.txvLocation.setBackgroundColor(mContext.getResources().getColor(R.color.white_color));
			holder.txvLocation.setPadding((int) (5*density), 2, 0, 2);
			holder.txvLocation.setTypeface(null, Typeface.BOLD);
		} else {
		//	holder.txvLocation.setTextSize(12*density);
			holder.txvLocation.setBackgroundColor(mContext.getResources().getColor(R.color.white_color));
			holder.txvLocation.setPadding((int) (10*density), 10, 0, 10);
			holder.txvLocation.setTypeface(null, Typeface.NORMAL);
		}
		if (position == 1) {
		//	holder.txvLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_launcher, 0, 0, 0);
		} else {
			holder.txvLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		
		holder.txvLocation.setText(mLocationList.get(position).getName());
		
		return view;
	}

	class ViewHolder {
		TextView txvLocation;
	}

}
