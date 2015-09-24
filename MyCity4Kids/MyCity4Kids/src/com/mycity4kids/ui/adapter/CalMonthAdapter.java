package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.ui.activity.DashboardActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.MONTH;

/**
 * Created by user on 03-06-2015.
 */
public class CalMonthAdapter extends BaseAdapter {
    private Context mContext;

    private java.util.Calendar month;
    public GregorianCalendar pmonth; // calendar instance for previous month
    /**
     * calendar instance for previous month for getting complete view
     */
    int counter = 1;
    Calendar cal1;
    String gridvalue;
    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    int calMaxP;
    int lastWeekDay;
    int leftDays;
    int mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;
    Boolean flag = false;

    private ArrayList<String> items;
    LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finalAppointmentList;
    public static List<String> dayString;
    private View previousView;
    GregorianCalendar tempdate;
    int monthofYear;
    ArrayList<AppointmentMappingModel> appointmentList;
    private Boolean filterFlag = false;
    Boolean commingFromFilter = false;

    public CalMonthAdapter(Context c, GregorianCalendar monthCalendar, int monthofYear, ArrayList<AppointmentMappingModel> appointmentList, LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finalAppointmentList) {

        this.cal1 = Calendar.getInstance();

        this.tempdate = monthCalendar;
        this.monthofYear = monthofYear;
        this.appointmentList = appointmentList;
        this.finalAppointmentList = finalAppointmentList;

        CalMonthAdapter.dayString = new ArrayList<String>();
        /* Locale.setDefault( Locale.US ); */
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
//        Log.d("Selected date", "" + selectedDate);
        mContext = c;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();

        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();
    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() {
        return dayString.size();
    }

    public ArrayList<AppointmentMappingModel> getAppointmentAtPosition(String date) {
        return finalAppointmentList.get(date);
    }

    public Object getItem(int position) {
        return dayString.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TextView dayView;
        LinearLayout eventLayout;
        ImageView selectIcon;
        TextView monthShow;
        DateFormat format1 = new SimpleDateFormat("MM", Locale.US);
        DateFormat format2 = new SimpleDateFormat("MMM");
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat dfNew = new SimpleDateFormat("dd", Locale.US);

        Date dateNew = new Date();
        try {
            dateNew = df.parse(dayString.get(position));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String currentDate = String.valueOf(dfNew.format(dateNew));

        try {
            if (convertView == null) { // if it's not recycled, initialize some
                // attributes
                LayoutInflater vi = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.aa_calendar_item, null);

            }

            selectIcon = (ImageView) v.findViewById(R.id.select_icon);
            dayView = (TextView) v.findViewById(R.id.date);
            eventLayout = (LinearLayout) v.findViewById(R.id.events_dots);
            monthShow = (TextView) v.findViewById(R.id.month);

            eventLayout.removeAllViews();

            // separates daystring into parts.

            String[] separatedTime = dayString.get(position).split("-");
        /*
         * Log.d("dayString.get(position)",""+dayString.get(position));
		 * Log.d("daystring ",""+dayString);
		 * Log.d("separatedTime ",""+separatedTime[2]);
		 * Log.d("separatedTime :",""+dayString.get(position).split("-"));
		 */
            // taking last part of date. ie; 2 from 2012-12-02
            gridvalue = separatedTime[2].replaceFirst("^0*", "");
//        Log.d("gridvalue ", "" + gridvalue);


            // checking whether the day is in current month or not.
            if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
                // setting offdays to white color.

                dayView.setTextColor(Color.parseColor("#D3D3D3"));
                dayView.setClickable(false);
                dayView.setFocusable(false);
//            dayView.setOnClickListener(null);
                selectIcon.setVisibility(View.GONE);

                v.setOnClickListener(null);

                v.setBackgroundColor(Color.parseColor("#ffffff"));
                monthShow.setVisibility(View.GONE);

            } else {
                if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {

                    Calendar tempDateShow = Calendar.getInstance();

                    selectIcon.setVisibility(View.GONE);
                    dayView.setTextColor(Color.parseColor("#D3D3D3"));
                    dayView.setClickable(false);
                    dayView.setFocusable(false);
//                dayView.setOnClickListener(null);
                    v.setOnClickListener(null);
                    v.setBackgroundColor(Color.parseColor("#ffffff"));

                    if (Integer.parseInt(gridvalue) == 1) {
                        monthShow.setVisibility(View.VISIBLE);
                        tempDateShow.set(MONTH, monthofYear + 2);
                    } else {
                        monthShow.setVisibility(View.GONE);
                    }

                    Date temp = null;
                    try {
                        temp = format1.parse(String.valueOf(tempDateShow.get(MONTH)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println(temp);
                    String month_show = format2.format(temp);

                    monthShow.setText(month_show.toUpperCase());


                } else {
                    // setting curent month's days in blue color.
                    dayView.setTextColor(Color.parseColor("#71747A"));
                    v.setBackgroundColor(Color.parseColor("#E4EBFE"));

                    Calendar tempDateShow = Calendar.getInstance();
                    selectIcon.setVisibility(View.GONE);


//                Log.e("Value", gridvalue.toString());

                    if (Integer.parseInt(gridvalue) == 1) {
                        monthShow.setVisibility(View.VISIBLE);
                        tempDateShow.set(MONTH, monthofYear + 1);
                    } else {
                        monthShow.setVisibility(View.GONE);
                    }

                    Date temp = null;

                    try {
                        temp = format1.parse(String.valueOf(tempDateShow.get(MONTH)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println(temp);
                    String month_show = format2.format(temp);

                    monthShow.setText(month_show.toUpperCase());


                    if (cal.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(gridvalue) && cal.get(MONTH) == month.get(GregorianCalendar.MONTH) && cal.get(Calendar.YEAR) == month.get(GregorianCalendar.YEAR)) {

//                        if (filterFlag) {
//                            selectIcon.setVisibility(View.GONE);
//                        } else {
                        setSelected(v);
                        ((DashboardActivity) mContext).updateDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(MONTH), cal.get(Calendar.YEAR));
                        selectIcon.setVisibility(View.VISIBLE);
//                        }

                    } else {

                        if (commingFromFilter) {

                            if (Integer.parseInt(gridvalue) == 1) {
                                setSelected(v);
                                selectIcon.setVisibility(View.VISIBLE);
                                ((DashboardActivity) mContext).updateDate(1, month.get(GregorianCalendar.MONTH), month.get(GregorianCalendar.YEAR));
                            } else {
                                selectIcon.setVisibility(View.GONE);
                            }
                        }
                    }

                    cal1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(gridvalue));
//                cal1.set(Calendar.DAY_OF_MONTH, counter);
                    cal1.set(Calendar.MONTH, monthofYear);
                    cal1.set(Calendar.YEAR, tempdate.get(Calendar.YEAR));

                    String GridDate = mFormat.format(cal1.getTime());

                    ArrayList<AppointmentMappingModel> itemList = (ArrayList<AppointmentMappingModel>) finalAppointmentList.get(mFormat.format(cal1.getTime()));

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(2, 0, 2, 20);

                    ArrayList<String> colorCodeArrayList = new ArrayList<>();

                    dayView.setText(gridvalue);

                    if (itemList == null) {
                        flag = true;
                    } else {

                        for (int k = 0; k < itemList.size(); k++) {
                            if (itemList.get(k).getAppointment_name() == null) {
                                flag = true;
                            } else {
                                flag = false;
                            }
                        }
                    }


                    if (flag == false) {
                        for (int i = 0; i < itemList.size(); i++) {

                            String tempdate = mFormat.format(cal1.getTime());
                            String targetDate = getDate(itemList.get(i).getStarttime());

                            for (int j = 0; j < itemList.get(i).getAttendee().size(); j++) {
                                colorCodeArrayList.add(itemList.get(i).getAttendee().get(j).getColorCode());
                            }
                        }
                        counter = counter + 1;

                        HashSet<String> uniqueValues = new HashSet<>(colorCodeArrayList);

                        if (uniqueValues.size() > 0) {
                            for (String value : uniqueValues) {

                                ImageView dots = new ImageView(mContext);
                                Drawable res = mContext.getResources().getDrawable(R.drawable.event_dot);
                                PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
                                res.setColorFilter(Color.parseColor(value), mode);
                                dots.setImageDrawable(res);
                                dots.setLayoutParams(layoutParams);
                                eventLayout.addView(dots);

                            }
                        }
                    } else {

                    }
                }
            }

            dayView.setText(gridvalue);
            // create date string for comparison
            String date = dayString.get(position);

            if (date.length() == 1) {
                date = "0" + date;
            }

            String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
            if (monthStr.length() == 1) {
                monthStr = "0" + monthStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }


    public View setSelected(View view) {

        if (previousView != null) {
            // previousView.setBackgroundResource(R.drawable.list_item_background);
            previousView.setBackgroundColor(Color.parseColor("#E4EBFE"));
            previousView.findViewById(R.id.select_icon)
                    .setVisibility(View.GONE);
            ((TextView) previousView.findViewById(R.id.date))
                    .setTextColor(Color.parseColor("#71747A"));
        }
        previousView = view;
        view.setBackgroundColor(Color.parseColor("#E4EBFE"));
        view.findViewById(R.id.select_icon).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.date)).setTextColor(Color.WHITE);
        // ((LinearLayout) view.findViewById(R.id.events_dots))
        // .setVisibility(View.GONE);

        return view;
    }

    public void setSelectedNew() {
        // previousView.setBackgroundResource(R.drawable.list_item_background);
        previousView.setBackgroundColor(Color.parseColor("#E4EBFE"));
        previousView.findViewById(R.id.select_icon)
                .setVisibility(View.GONE);
        ((TextView) previousView.findViewById(R.id.date))
                .setTextColor(Color.parseColor("#71747A"));
//        previousView = view;
//        view.setBackgroundColor(Color.parseColor("#E4EBFE"));
//        view.findViewById(R.id.select_icon).setVisibility(View.VISIBLE);
//
//        ((TextView) view.findViewById(R.id.date)).setTextColor(Color.WHITE);
//        // ((LinearLayout) view.findViewById(R.id.events_dots))
//        // .setVisibility(View.GONE);

    }


    public void refreshDays() {
        // clear items
        // items.clear();
        dayString.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        /**
         * Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.
         */
        pmonthmaxset = (GregorianCalendar) pmonth.clone();
        /**
         * setting the start date as previous month's required date.
         */
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        /**
         * filling calendar gridview.
         */
        for (int n = 0; n < mnthlength; n++) {

            itemvalue = df.format(pmonthmaxset.getTime());
//            Log.d("itemalue", "" + itemvalue);
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
//            Log.d("pmonthmaxset", "" + pmonthmaxset);
            dayString.add(itemvalue);

        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }

    public void updateMonth(java.util.Calendar month) {
        this.month = month;
        notifyDataSetChanged();

    }

    public String getDate(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date netDate = (new Date(timeStampStr));
//            Log.e("TimeStampq", sdf.format(netDate).toString());
            return sdf.format(netDate);

        } catch (Exception ex) {
            return "xx";
        }
    }

    public void notifyDataChange(LinkedHashMap<String, ArrayList<AppointmentMappingModel>> newList) {

        this.finalAppointmentList = newList;
        notifyDataSetChanged();

    }

    public void setFlag(Boolean flag) {
        this.filterFlag = flag;
    }

    public void setCommingFromFilterFlag(Boolean flag) {
        this.commingFromFilter = flag;
    }

}