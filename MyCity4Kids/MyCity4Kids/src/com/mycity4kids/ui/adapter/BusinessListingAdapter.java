package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.businesslist.BusinessDataListing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BusinessListingAdapter extends BaseAdapter {

    private ArrayList<BusinessDataListing> mBusinessData;
    private Context mContext;
    private LayoutInflater mInflator;
    private int mBusinessOrEventType;

    public BusinessListingAdapter(Context pContext) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
    }

    public void setListData(ArrayList<BusinessDataListing> pBusinessData, int pBusinessOrEventType) {
        mBusinessData = pBusinessData;
        mBusinessOrEventType = pBusinessOrEventType;
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
            holder.txvrarting = (TextView) view.findViewById(R.id.txvrating);
            //holder.txvDistance = (TextView) view.findViewById(R.id.txvDistance);
            holder.btnBookNow = (TextView) view.findViewById(R.id.txvBookNow);
            if (mBusinessOrEventType == Constants.EVENT_PAGE_TYPE) {
                holder.txvDate = (TextView) view.findViewById(R.id.start_end_date);
                holder.txvMonth = (TextView) view.findViewById(R.id.month_txt);
                holder.txvAgeGroup = (TextView) view.findViewById(R.id.age_group_txt);
                holder.axvAgeGroupRatio = (TextView) view.findViewById(R.id.age_group_value);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //	holder.txvDistance.setVisibility(View.VISIBLE);
        if (mBusinessData.get(position).getRating() == 0.0) {
            holder.txvrarting.setVisibility(View.GONE);
        } else {
            holder.txvrarting.setVisibility(View.VISIBLE);
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            holder.txvrarting.setText("" + oneDForm.format(mBusinessData.get(position).getRating()));
        }

        holder.txvName.setText(mBusinessData.get(position).getName());

        holder.txvActivities.setText(mBusinessData.get(position).getActivities());
        String difference = mBusinessData.get(position).getDistance();
        if (!difference.equals("") && !difference.equals("0")) {
            double diff = Double.parseDouble(difference);
            DecimalFormat df = new DecimalFormat("####0.0");
            difference = df.format(diff);
            holder.txvAddress.setText(mBusinessData.get(position).getLocality() + " (" + difference + "km)");
            //holder.txvDistance.setText(difference + " Km");
        } else {
            holder.txvAddress.setText(mBusinessData.get(position).getLocality());
            //holder.txvDistance.setVisibility(View.GONE);
        }

        if (mBusinessOrEventType == Constants.EVENT_PAGE_TYPE) {
            /*holder.txvDistance.setVisibility(View.GONE);*/
            holder.txvDate.setVisibility(View.VISIBLE);
            holder.txvMonth.setVisibility(View.VISIBLE);
            holder.txvAgeGroup.setVisibility(View.VISIBLE);
            holder.axvAgeGroupRatio.setVisibility(View.VISIBLE);

            String[] startDateValues = getDateValues(mBusinessData.get(position).getStart_date());
            String[] endDateValues = getDateValues(mBusinessData.get(position).getEnd_date());


            Calendar cal = Calendar.getInstance();
            cal.setTime(DateTimeUtils.stringToDate(mBusinessData.get(position).getStart_date()));
            int startmonth = cal.get(Calendar.MONTH);
            int startDay = cal.get(Calendar.DAY_OF_MONTH);
            int startYear = cal.get(Calendar.YEAR);
            cal.setTime(DateTimeUtils.stringToDate(mBusinessData.get(position).getEnd_date()));
            int endMonth = cal.get(Calendar.MONTH);
            int endtDay = cal.get(Calendar.DAY_OF_MONTH);
            int endYear = cal.get(Calendar.YEAR);
            if (startDateValues != null && endDateValues != null) {
                if (startmonth == endMonth && startDay == endtDay) {
                    holder.txvDate.setText(startDateValues[0] + "      ");
                    holder.txvMonth.setText(startDateValues[1] + "      ");
                } else if (startmonth == endMonth && startDay != endtDay) {
                    holder.txvDate.setText(startDateValues[0] + "-" + endDateValues[0]);
                    holder.txvMonth.setText(startDateValues[1]);
                } else {
                    holder.txvDate.setText(startDateValues[0] + "-" + endDateValues[0]);
                    holder.txvMonth.setText(startDateValues[1] + "  " + endDateValues[1]);
                }

            }

			/*if(isAgeGroupCorrect(mBusinessData,position))
            {
				if(mBusinessData.get(position).getEndagegroup().contains("+")){
					holder.axvAgeGroupRatio.setText(Math.round(Float.parseFloat(mBusinessData.get(position).getStartagegroup()))+" "+mBusinessData.get(position).getEndagegroup());
	
				}else if(!mBusinessData.get(position).getStartagegroup().contains("+") && !mBusinessData.get(position).getEndagegroup().contains("+")){
					holder.axvAgeGroupRatio.setText(Math.round(Float.parseFloat(mBusinessData.get(position).getStartagegroup()))+"-"+Math.round(Float.parseFloat(mBusinessData.get(position).getEndagegroup())));

				}
				
			}*/
            /**
             * CR DONE- Deepanker
             */
            if (!StringUtils.isNullOrEmpty(mBusinessData.get(position).getAgegroup_text())) {
                holder.axvAgeGroupRatio.setText(mBusinessData.get(position).getAgegroup_text());
            }

            mBusinessData.get(position).getEnd_date();

        }


        return view;
    }

    /*private String distance(String serverLat,String serverLong){
        GPSTracker getCurrentLocation = new GPSTracker(mContext);
        double _latitude = getCurrentLocation.getLatitude();
        double _longitude = getCurrentLocation.getLongitude();
        if(serverLat.contains("0.0") || serverLong.contains("0.0") || _latitude==0.0 ||_longitude==0.0){
            return "";
        }
        double serverLatitude=Double.parseDouble(serverLat);
        double serverLongitude=Double.parseDouble(serverLong);

        DecimalFormat df = new DecimalFormat("####0.0");
        return df.format(distance(_latitude,_longitude,serverLatitude,serverLongitude));
    }*/
    /*private double distance(double lat1, double lon1, double lat2, double lon2) {

		  double theta = lon1 - lon2;
	      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	      dist = Math.acos(dist);
	      dist = rad2deg(dist);
	      dist = dist * 60 * 1.1515;
	      if (unit == 'K') {
	        dist = dist * 1.609344;
	      } else if (unit == 'N') {
	        dist = dist * 0.8684;
	        }
	      return (dist/0.621);
	    }
	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    ::  This function converts decimal degrees to radians             :
    :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }

    :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    ::  This function converts radians to decimal degrees             :
    :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }*/
    class ViewHolder {
        TextView txvName;
        TextView txvAddress;
        TextView txvActivities;
        //TextView txvDistance;
        TextView txvDate;
        TextView txvMonth;
        TextView txvAgeGroup;
        TextView axvAgeGroupRatio;
        TextView btnBookNow;
        TextView txvrarting;
    }

    private String[] getDateValues(String date) {
        String[] datesValue = null;
        try {
            String formatedDate = DateTimeUtils.changeDate(date);

            datesValue = formatedDate.split(" ");
        /*DateFormat format = new SimpleDateFormat("dd MMM yyyy",Locale.US);
        cal.setTime(format.parse(formatedDate));*/
        } catch (Exception e) {
            return null;
        }

        return datesValue;

    }

    private boolean isAgeGroupCorrect(ArrayList<BusinessDataListing> mBusinessData2, int position) {
        boolean isOK = false;
        try {
            if (!StringUtils.isNullOrEmpty(mBusinessData.get(position).getStartagegroup()) || !StringUtils.isNullOrEmpty(mBusinessData.get(position).getEndagegroup())) {
                isOK = true;
            }


        } catch (Exception e) {
            isOK = false;
        }
        return isOK;
    }

}
