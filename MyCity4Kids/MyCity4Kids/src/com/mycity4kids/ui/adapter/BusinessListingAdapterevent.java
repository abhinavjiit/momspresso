package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.ColorModel;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BusinessListingAdapterevent extends BaseAdapter implements Filterable {

    private final ArrayList<KidsInfo> kidsInformations;
    private HashMap<String, ColorModel> map;
    private ArrayList<BusinessDataListing> mBusinessData;
    private Context mContext;
    private LayoutInflater mInflator;
    private int mBusinessOrEventType;
    int width, height;
    private ItemFilter mFilter = new ItemFilter();
    private ArrayList<BusinessDataListing> filteredData;
    private ArrayList<String> eventIdList;

    public BusinessListingAdapterevent(Context pContext) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();
        refreshEventIdList();
        createColorMap();
    }

    public void refreshEventIdList() {
        TableApiEvents table = new TableApiEvents(BaseApplication.getInstance());
        eventIdList = table.getApiEventIdList();
    }

    private void createColorMap() {
        map = new HashMap<String, ColorModel>();
        float c1[] = {0, 2};
        float c2[] = {2, 4};
        float c3[] = {4, 6};
        float c4[] = {6, 10};
        float c5[] = {10, 14};
        ColorModel cd1 = new ColorModel("#f6157f", c1);
        ColorModel cd2 = new ColorModel("#ff9900", c2);
        ColorModel cd3 = new ColorModel("#4ecfe2", c3);
        ColorModel cd4 = new ColorModel("#e040fb", c4);
        ColorModel cd5 = new ColorModel("#64de17", c5);
        map.put("Infants", cd1);
        map.put("toddlers", cd2);
        map.put("kindergarten", cd3);
        map.put("junior school", cd4);
        map.put("middle school", cd5);
    }

    public void setListData(ArrayList<BusinessDataListing> pBusinessData, int pBusinessOrEventType) {
        mBusinessData = pBusinessData;
        filteredData = pBusinessData;
        mBusinessOrEventType = pBusinessOrEventType;
        //  Log.d("check", "in setListData size " + filteredData.size());
    }

    @Override
    public int getCount() {
        return filteredData == null ? 0 : filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        //LinearLayout.LayoutParams dummyParams=null;
        LinearLayout.LayoutParams dummyParams;
        float startage = 0, endagegroup = 0;
        HashMap<Float, String> kidslist;
        ArrayList<String> colorsvalue;
        if (view == null) {
            view = mInflator.inflate(R.layout.aa_new_listitem_business, null);
            holder = new ViewHolder();
            holder.colorcode = (LinearLayout) view.findViewById(R.id.colorcodeevent);
            holder.txvName = (TextView) view.findViewById(R.id.name);
            holder.txvAddress = (TextView) view.findViewById(R.id.addresstxt);
            holder.thumb = (ImageView) view.findViewById(R.id.thumbnail);
            holder.txvDate = (TextView) view.findViewById(R.id.textdate);
            holder.thumbview = (LinearLayout) view.findViewById(R.id.leftpane);
            holder.txvtime = (TextView) view.findViewById(R.id.durationtxt);
            holder.statusimg = (ImageView) view.findViewById(R.id.statusimg);
            holder.txvslg = (TextView) view.findViewById(R.id.slg);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
//        holder.thumbview.getLayoutParams().height = height / 6;
//        holder.thumbview.getLayoutParams().width = height / 6;
//        holder.thumb.getLayoutParams().height = height / 6;
//        holder.thumb.getLayoutParams().width = height / 6;

        dummyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);


        try {
            startage = Float.parseFloat(filteredData.get(position).getStartagegroup());
            if (filteredData.get(position).getEndagegroup() != null) {
                endagegroup = Float.parseFloat(filteredData.get(position).getEndagegroup());
            } else {
                endagegroup = startage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        kidslist = new HashMap<Float, String>();
        colorsvalue = new ArrayList<>();

        ArrayList<Float> ageArray = new ArrayList<>();
        ArrayList<String> colorArray = new ArrayList<>();

        for (int i = 0; i < kidsInformations.size(); i++) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate;
            try {
                startDate = df.parse(kidsInformations.get(i).getDate_of_birth());
                float age = getAge(startDate);
                kidslist.put(age, kidsInformations.get(i).getColor_code());

                ageArray.add(age);
                colorArray.add("" + kidsInformations.get(i).getColor_code());


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!kidslist.isEmpty()) {
//            Iterator<Map.Entry<Float, String>> iterator = kidslist.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry pairs = (Map.Entry) iterator.next();
//                float a = (float) pairs.getKey();
//                String c = (String) pairs.getValue();
//                if ((a >= startage) && (a <= endagegroup)) {
//                    colorsvalue.add(c);
//                }
//
//            }

            for (int i = 0; i < colorArray.size(); i++) {
                if ((ageArray.get(i) >= startage) && (ageArray.get(i) <= endagegroup)) {
                    colorsvalue.add(colorArray.get(i).toString());
                }
            }

        }

        if (colorsvalue.isEmpty()) {
            if (!map.isEmpty()) {
                Iterator<Map.Entry<String, ColorModel>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry pairs = (Map.Entry) iterator.next();
                    float arr[] = new float[2];
                    String color = "";
                    ColorModel c = (ColorModel) pairs.getValue();
                    arr = c.agegroup;
                    color = c.colorname;
                    // if ((startage >= arr[0]) && (arr[1] <= endagegroup)) {
                    if ((startage <= arr[0]) && (arr[0] <= endagegroup) || (startage < arr[1]) && (arr[1] < endagegroup)) {
                        colorsvalue.add(color);
                    }
                }
            }
        }


        holder.colorcode.removeAllViews();
        for (int i = 0; i < colorsvalue.size(); i++) {
            View dummyView = new View(mContext);
            dummyView.setLayoutParams(dummyParams);
            dummyView.setBackgroundColor(Color.parseColor(colorsvalue.get(i)));
            dummyParams.weight = 1f;
            holder.colorcode.addView(dummyView);
        }

        try {
            DecimalFormat df = new DecimalFormat("####0.0");
            String distance = df.format(Double.parseDouble(filteredData.get(position).getDistance()));

            //holder.txvDistance.setVisibility(View.VISIBLE);
            holder.txvName.setText(filteredData.get(position).getName());

            holder.txvAddress.setText(filteredData.get(position).getLocality() + " (" + distance + "km)");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //holder.txvDate.setText(mBusinessData.get(position).getStart_date());

        if (StringUtils.isNullOrEmpty(filteredData.get(position).getActivities())) {
            holder.txvslg.setVisibility(View.GONE);
        } else {
            holder.txvslg.setVisibility(View.VISIBLE);
            holder.txvslg.setText(filteredData.get(position).getActivities());
        }

        try {

            Calendar cal = Calendar.getInstance();
            cal.setTime(DateTimeUtils.stringToDate(filteredData.get(position).getStart_date()));

            int startmonth = cal.get(Calendar.MONTH);
            int startDay = cal.get(Calendar.DAY_OF_MONTH);
            String orgmnth = getMonth(startmonth);
            String startDaystr = String.valueOf(startDay);
            String orgmnthstr = String.valueOf(orgmnth);

            Calendar cal1 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                cal1.setTime(sdf.parse(filteredData.get(position).getStart_date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int hour = cal1.get(Calendar.HOUR_OF_DAY);
            //int ampm=cal1.get(Calendar.AM_PM);
            holder.txvDate.setText(startDaystr + "\n" + orgmnthstr);

            String orgweekday = getWeek(cal.get(Calendar.DAY_OF_WEEK));
            String timeString = "";

            // Log.d("check", "hour " + hour + "am pm" + cal.get(Calendar.AM_PM));

            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(DateTimeUtils.stringToDate(filteredData.get(position).getStart_date()));
            calendar2.setTime(DateTimeUtils.stringToDate(filteredData.get(position).getEnd_date()));
            long milsecs1 = calendar1.getTimeInMillis();
            long milsecs2 = calendar2.getTimeInMillis();
            long diff1 = milsecs2 - milsecs1;
            long dsecs = diff1 / 1000;
            long dminutes = diff1 / (60 * 1000);
            long dhours = diff1 / (60 * 60 * 1000);
            long ddays = diff1 / (24 * 60 * 60 * 1000);

            String orgduration;
            if (ddays == 0) {
                if (dhours == 0) {
                    orgduration = String.valueOf(dminutes) + " Minutes";
                } else {
                    orgduration = String.valueOf(dhours) + " Hour";
                }
            } else {
                if (ddays > 30) {
                    long mn = ddays / 30;
                    orgduration = String.valueOf(mn) + " Month";
                } else {
                    orgduration = String.valueOf(ddays) + " Day";
                }
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss ", Locale.getDefault());


            //correct duration
            //HH converts hour in 24 hours format (0-23), day calculation
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            DateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = null;
            Date d2 = null;
            String orgduration1 = "";
            long difftmp = 0;
            try {
                d1 = format.parse(filteredData.get(position).getStart_date());

                d2 = format.parse(filteredData.get(position).getEnd_date());
                difftmp = d2.getTime() - d1.getTime();
                if (d2 != null) {
                    String formattedDate = writeFormat.format(d2);
                    // Log.d("check", "formattedDate 24 hr frmt " + formattedDate);
                }
                Calendar cal11 = Calendar.getInstance();
                cal11.setTime(DateTimeUtils.stringToDate(filteredData.get(position).getStart_date()));
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(DateTimeUtils.stringToDate(filteredData.get(position).getEnd_date()));
                long diff = cal2.getTimeInMillis() - cal.getTimeInMillis();

                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffDays = diff / (24 * 60 * 60 * 1000);
                timeString = getTime(String.valueOf(d1.getTime()));

                if (diffDays == 0) {
                    if (diffHours == 0) {
                        if (diffMinutes > 1)
                            orgduration1 = String.valueOf(diffMinutes) + " Mins";
                        else
                            orgduration1 = String.valueOf(diffMinutes) + " Min";
                    } else {
                        if (diffHours > 1)
                            orgduration1 = String.valueOf(diffHours) + " Hours";
                        else
                            orgduration1 = String.valueOf(diffHours) + " Hours";
                    }
                } else {
                    if (diffDays >= 30) {
                        long mn = diffDays / 30;
                        if (mn > 1)
                            orgduration1 = String.valueOf(mn) + " Months";
                        else
                            orgduration1 = String.valueOf(mn) + " Month";
                    } else {
                        if (diffDays > 1)
                            orgduration1 = String.valueOf(diffDays) + " Days";
                        else
                            orgduration1 = String.valueOf(diffDays) + " Day";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            StringBuilder distanceTime = new StringBuilder();

            if (!StringUtils.isNullOrEmpty(filteredData.get(position).getDuration())) {
                distanceTime.append(orgweekday);
                distanceTime.append(" at ").append(timeString.toLowerCase());
                distanceTime.append(" (").append(filteredData.get(position).getDuration()).append(")");
            } else {
                distanceTime.append(orgweekday);
                distanceTime.append(" at ").append(timeString.toLowerCase());
            }
            holder.txvtime.setText(distanceTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // add here


        String difference = filteredData.get(position).getDistance();
        String thumbnail = filteredData.get(position).getThumbnail();
        if (!StringUtils.isNullOrEmpty(thumbnail)) {
            Picasso.with(mContext).load(thumbnail).placeholder(R.drawable.thumbnail_eventsxxhdpi).error(R.drawable.thumbnail_eventsxxhdpi).into(holder.thumb);
        } else {
            holder.thumb.setImageResource(R.drawable.thumbnail_eventsxxhdpi);
        }
        if (!difference.equals("") && !difference.equals("0")) {
            double diff = Double.parseDouble(difference);
            DecimalFormat df1 = new DecimalFormat("####0.0");
            difference = df1.format(diff);
            //holder.txvDistance.setText(difference + " Km");
        } else {
            //holder.txvDistance.setVisibility(View.GONE);
        }
        if (mBusinessOrEventType == Constants.EVENT_PAGE_TYPE) {
            //	holder.txvDistance.setVisibility(View.GONE);
            //holder.txvDate.setVisibility(View.VISIBLE);
            /*holder.txvMonth.setVisibility(View.VISIBLE);
            holder.txvAgeGroup.setVisibility(View.VISIBLE);
			holder.axvAgeGroupRatio.setVisibility(View.VISIBLE);
			
			String[] startDateValues=getDateValues(mBusinessData.get(position).getStart_date());
			String[]endDateValues=getDateValues(mBusinessData.get(position).getEnd_date());*/


			/*Calendar cal = Calendar.getInstance();
            cal.setTime(DateTimeUtils.stringToDate(mBusinessData.get(position).getStart_date()));
			int startmonth = cal.get(Calendar.MONTH);
			int startDay=cal.get(Calendar.DAY_OF_MONTH);
			int startYear=cal.get(Calendar.YEAR);
			cal.setTime(DateTimeUtils.stringToDate(mBusinessData.get(position).getEnd_date()));
			int endMonth = cal.get(Calendar.MONTH);
			int endtDay=cal.get(Calendar.DAY_OF_MONTH);
			int endYear=cal.get(Calendar.YEAR);*/

			/*if(startDateValues!=null && endDateValues!=null){
                if(startmonth==endMonth && startDay==endtDay ){
				    holder.txvDate.setText(startDateValues[0]+"      ");
					holder.txvMonth.setText(startDateValues[1]+"      ");
				}else if(startmonth==endMonth && startDay!=endtDay){
					holder.txvDate.setText(startDateValues[0]+"-"+endDateValues[0]);
					holder.txvMonth.setText(startDateValues[1]);
				}
				else{
					holder.txvDate.setText(startDateValues[0]+"-"+endDateValues[0]);
					holder.txvMonth.setText(startDateValues[1]+"  "+endDateValues[1]);
				}
				
			}*/

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
            /*if(!StringUtils.isNullOrEmpty(mBusinessData.get(position).getAgegroup_text())){
                holder.axvAgeGroupRatio.setText(mBusinessData.get(position).getAgegroup_text());
			}*/

            filteredData.get(position).getEnd_date();

        }


//        if (mBusinessData.get(position).isEventAdded()) {
//            holder.statusimg.setBackgroundResource(R.drawable.checkbox_withcheckxxhdpi);
//        } else {
//            holder.statusimg.setBackgroundResource(R.drawable.add_red);
//        }

        if (eventIdList.contains(mBusinessData.get(position).getId())) {
            holder.statusimg.setImageResource(R.drawable.checkmark_xxhdpi);
            mBusinessData.get(position).setIsEventAdded(true);
        } else {
            holder.statusimg.setImageResource(R.drawable.add_red);
            mBusinessData.get(position).setIsEventAdded(false);
        }


        holder.statusimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBusinessData.get(position).isEventAdded()) {

                    ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.event_added));
                } else {
                    Intent i = new Intent(mContext, ActivityCreateAppointment.class);
                    i.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessData.get(position).getId());
                    i.putExtra(Constants.EVENT_NAME, mBusinessData.get(position).getName());
                    i.putExtra(Constants.EVENT_DES, mBusinessData.get(position).getDescription());
                    i.putExtra(Constants.EVENT_LOCATION, mBusinessData.get(position).getLocality());
                    i.putExtra(Constants.EVENT_START_DATE, mBusinessData.get(position).getStart_date());
                    i.putExtra(Constants.EVENT_END_DATE, mBusinessData.get(position).getEnd_date());
                    mContext.startActivity(i);
                }

            }
        });


        return view;
    }

    String getTimeAmPm(String milliseconds) {

        String time = "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(milliseconds));

        time = cal.get(Calendar.HOUR) + " : " + cal.get(Calendar.MINUTE);

        if (cal.get(Calendar.AM_PM) == 0)
            time = "AM";
        else
            time = "PM";

        return time;

    }

    String getTime(String milliseconds) {

        SimpleDateFormat format = new SimpleDateFormat("hh:mma");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliseconds));

        String time = "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(milliseconds));

        time = cal.get(Calendar.HOUR) + " : " + cal.get(Calendar.MINUTE);

        if (cal.get(Calendar.AM_PM) == 0)
            time = time + " AM";
        else
            time = time + " PM";

        return format.format(calendar.getTime());
//        return time;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            //String filterString = constraint.toString().toLowerCase();
            String filterString = constraint.toString();
            FilterResults results = new FilterResults();

            final List<BusinessDataListing> list = mBusinessData;

            int count = list.size();
            final ArrayList<BusinessDataListing> nlist = new ArrayList<BusinessDataListing>(count);

            if (!StringUtils.isNullOrEmpty(filterString)) {
                for (BusinessDataListing activities : mBusinessData) {
                    if (activities.getName().toLowerCase().startsWith(filterString.toLowerCase())) {
                        nlist.add(activities);
                    }
                }
            } else {
                nlist.addAll(mBusinessData);
            }

//			String filterableString ;
//         Log.d("check","filterString "+filterString);
//			for (int i = 0; i < count; i++) {
//				filterableString = list.get(i).getName();
//				if (filterString.equalsIgnoreCase((String)filterableString.subSequence(0,filterString.length()))){
//					Activities activityModel=new Activities();
//					activityModel.setId(list.get(i).getId());
//					activityModel.setName(list.get(i).getName());
//					nlist.add(activityModel);
//				}
//			}

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<BusinessDataListing>) results.values;
            //  Log.d("check", "constraint " + constraint);
            // Log.d("check", "results.values " + results.values);
            notifyDataSetChanged();
        }

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
        ImageView thumb;
        TextView txvName;
        TextView txvslg;
        TextView txvAddress;
        TextView txvtime;
        TextView txvDate;
        LinearLayout thumbview;
        LinearLayout colorcode;
        ImageView statusimg;
        ImageView plusBtn;
        /*TextView txvActivities;
        TextView txvDistance;
		TextView txvDate;
		TextView txvMonth;
		TextView txvAgeGroup;
		TextView axvAgeGroupRatio;
		TextView btnBookNow;*/
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
            if (!StringUtils.isNullOrEmpty(filteredData.get(position).getStartagegroup()) || !StringUtils.isNullOrEmpty(filteredData.get(position).getEndagegroup())) {
                isOK = true;
            }

        } catch (Exception e) {
            isOK = false;
        }
        return isOK;
    }

    public String getMonth(int month) {
        SimpleDateFormat sdf_n = new SimpleDateFormat("MMM");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
//        return new DateFormatSymbols().getMonths()[month];
        return (sdf_n.format(calendar.getTime())).toUpperCase();
    }

    public String getWeek(int weekday) {
        return new DateFormatSymbols().getWeekdays()[weekday];
    }

//    public float getAge(Date dateOfBirth) {
//        float age = 0;
//        Calendar born = Calendar.getInstance();
//        Calendar now = Calendar.getInstance();
//        if (dateOfBirth != null) {
//            now.setTime(new Date());
//            born.setTime(dateOfBirth);
//            if (born.after(now)) {
//                throw new IllegalArgumentException("Can't be born in the future");
//            }
//            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
//            if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
//                age -= 1;
//            }
//        }
//
//        return age;
//    }

    public float getAge(Date dateOfBirth) {
        float age = 0;

        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (now.get(Calendar.YEAR) == born.get(Calendar.YEAR)) {
                age = 0;
            } else if (now.get(Calendar.YEAR) > born.get(Calendar.YEAR)) {
                if (born.get(Calendar.MONTH) <= now.get(Calendar.MONTH) && born.get(Calendar.DAY_OF_MONTH) <= now.get(Calendar.DAY_OF_MONTH)) {
                    age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                } else {
                    age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                    age = age - 1;
                }
            }
        }

        return age;
    }

}

