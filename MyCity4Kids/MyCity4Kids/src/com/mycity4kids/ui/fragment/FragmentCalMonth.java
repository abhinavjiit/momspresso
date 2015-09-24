package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityShowAppointment;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.AdapterEventMonth;
import com.mycity4kids.ui.adapter.AttendeeListAdapter;
import com.mycity4kids.ui.adapter.CalMonthAdapter;
import com.mycity4kids.widget.CustomGridView;
import com.mycity4kids.widget.CustomListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//
public class FragmentCalMonth extends BaseFragment implements View.OnClickListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    String selectedDateTime;
    SimpleDateFormat sdf;


    LinkedHashMap<String, ArrayList<AppointmentMappingModel>> orignalList;
    ArrayList<String> orignalDaysList;

    ScrollView scrollView;

    TextView backMonth, nextMonth, dateCurrent1;
    Boolean filterFlagNew = false;

    ArrayList<AppointmentMappingModel> listAppointment_;

    public GregorianCalendar month, itemmonth;// calendar instances.
    public CalMonthAdapter adapter;// adapter instance
    CustomGridView gridview;
    public TextView currentDate;
    SimpleDateFormat df;
    SimpleDateFormat df1;
    Calendar temp;
    ArrayList<AppointmentMappingModel> appointmentList;
    TableAppointmentData tableAppointment;
    CustomListView listAppointmentbyMonth;
    AdapterEventMonth adapterEventMonth;
    CustomListView listAttendee;
    FrameLayout attendeeFrame;
    AttendeeListAdapter attendeeAdapter;
    int id_uk;
    String type;
    Calendar tempCal;
    SimpleDateFormat formatter;
    Boolean flag = false;
    Calendar firstDate;
    Calendar lastDate;
    ImageView addAppointment;
    LinkedHashMap<String, ArrayList<AppointmentMappingModel>> AppointmentList;
    GregorianCalendar calendar_;
    private int lastSelectedPosition = 0;
    LinearLayout todayLayout;
    TextView day_;

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_cal__month, container, false);
        currentDate = (TextView) view.findViewById(R.id.currentDate);
        listAppointmentbyMonth = (CustomListView) view.findViewById(R.id.list_month_event);
        attendeeFrame = (FrameLayout) view.findViewById(R.id.filter_attendee1);
        listAttendee = (CustomListView) view.findViewById(R.id.attendee_list1);
        addAppointment = (ImageView) view.findViewById(R.id.add_appointment_month);
        SimpleDateFormat form = new SimpleDateFormat("MMM yyyy", Locale.US);
        backMonth = (TextView) view.findViewById(R.id.back_);
        nextMonth = (TextView) view.findViewById(R.id.next_);
        dateCurrent1 = (TextView) view.findViewById(R.id.date_);
        todayLayout = (LinearLayout) view.findViewById(R.id.curent_date_view);
        day_ = (TextView) view.findViewById(R.id.day_);
        backMonth.setOnClickListener(this);
        nextMonth.setOnClickListener(this);
        scrollView = (ScrollView) view.findViewById(R.id.mainScroll);

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        selectedDateTime = sdf.format(System.currentTimeMillis());

        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());

        calendar_ = (GregorianCalendar) Calendar.getInstance(Locale.US);

        tempCal = Calendar.getInstance();

        ((DashboardActivity) getActivity()).filter = false;
        ((DashboardActivity) getActivity()).refreshMenu();

//        set filter list,........

        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        ArrayList<KidsInfo> kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();

        TableAdult tableAdult = new TableAdult(BaseApplication.getInstance());
        ArrayList<UserInfo> userInfos = (ArrayList<UserInfo>) tableAdult.getAllAdults();

        ArrayList<AttendeeModel> attendeeList = new ArrayList<AttendeeModel>();

        for (int i = 0; i < kidsInformations.size(); i++) {
            attendeeList.add(new AttendeeModel(kidsInformations.get(i).getId(), "kid", kidsInformations.get(i).getName(), kidsInformations.get(i).getColor_code()));
        }
        for (int i = 0; i < userInfos.size(); i++) {
            attendeeList.add(new AttendeeModel(userInfos.get(i).getId(), "user", userInfos.get(i).getFirst_name(), userInfos.get(i).getColor_code()));
        }
        attendeeFrame.setVisibility(View.GONE);

        attendeeAdapter = new AttendeeListAdapter(getActivity(), attendeeList);
        listAttendee.setAdapter(attendeeAdapter);
        listAttendee.setScrollContainer(false);


        formatter = new SimpleDateFormat("yyyy-MM-dd");
        String title = String.valueOf(((DashboardActivity) getActivity()).getTitleText());

        Date dateTitle = null;

        try {
            dateTitle = (Date) form.parse(title);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calTitle = Calendar.getInstance();
        try {

            if (calTitle != null)
                calTitle.setTime(dateTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        df = new SimpleDateFormat("d MMM", Locale.US);
        df1 = new SimpleDateFormat("EEEE", Locale.US);
        temp = Calendar.getInstance();

        month = (GregorianCalendar) GregorianCalendar.getInstance(Locale.US);

        Bundle arguments = getArguments();

        if (arguments != null) {

            month.set(Calendar.YEAR, (Integer) getArguments().get("year"));
            month.set(Calendar.MONTH, (Integer) getArguments().get("month"));
        }

        firstDate = Calendar.getInstance();
        lastDate = Calendar.getInstance();

        firstDate.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        firstDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDate.set(Calendar.MONTH, calTitle.get(Calendar.MONTH));
        firstDate.set(Calendar.YEAR, calTitle.get(Calendar.YEAR));
        firstDate.set(Calendar.HOUR_OF_DAY, 0);
//        firstDate.set(Calendar.HOUR,0);
        firstDate.set(Calendar.MINUTE, 0);

        lastDate.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        lastDate.set(Calendar.DAY_OF_MONTH, calTitle.getActualMaximum(Calendar.DAY_OF_MONTH));
        lastDate.set(Calendar.MONTH, calTitle.get(Calendar.MONTH));
        lastDate.set(Calendar.YEAR, calTitle.get(Calendar.YEAR));
        lastDate.set(Calendar.HOUR_OF_DAY, 23);
        lastDate.set(Calendar.MINUTE, 58);


        if (getArguments() != null) {
            if ((Integer) getArguments().get("month") == 0) {

                month.set(Calendar.MONTH, 0);

                if (temp.get(Calendar.MONTH) == 0 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 0);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(0, month.get(Calendar.YEAR)));

                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 0, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 1) {

                if (temp.get(Calendar.MONTH) == 1 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 1);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 1);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(1, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 1, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 2) {

                if (temp.get(Calendar.MONTH) == 2 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 2);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 2);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(2, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 2, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 3) {

                if (temp.get(Calendar.MONTH) == 3 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 3);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 3);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(3, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 3, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 4) {

                if (temp.get(Calendar.MONTH) == 4 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 4);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 4);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(4, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 4, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 5) {

                if (temp.get(Calendar.MONTH) == 5 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 5);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 5);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(5, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 5, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 6) {

                if (temp.get(Calendar.MONTH) == 6 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 6);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 6);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(6, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 6, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 7) {

                if (temp.get(Calendar.MONTH) == 7 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 7);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 7);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(7, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 7, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 8) {

                if (temp.get(Calendar.MONTH) == 8 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 8);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 8);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(8, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 8, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 9) {

                if (temp.get(Calendar.MONTH) == 9 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 9);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 9);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(9, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 9, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 10) {

                if (temp.get(Calendar.MONTH) == 10 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 10);
                    temp.set(Calendar.MONTH, 2);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 10);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(10, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 10, appointmentList, AppointmentList);
            } else if ((Integer) getArguments().get("month") == 11) {

                if (temp.get(Calendar.MONTH) == 11 && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR)) {

                } else {
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    temp.set(Calendar.MONTH, 11);
                    temp.set(Calendar.YEAR, month.get(Calendar.YEAR));
                }

                month.set(Calendar.MONTH, 11);

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(11, month.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, 11, appointmentList, AppointmentList);
            } else {


                Calendar temp = Calendar.getInstance();

                try {
                    AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(temp.get(Calendar.MONTH), temp.get(Calendar.YEAR)));
                    orignalList = AppointmentList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), month, temp.get(Calendar.MONTH), appointmentList, AppointmentList);
//                setTitle(form.format(month.getTime()));
            }
        } else {

            Calendar temp = Calendar.getInstance();
            try {
                AppointmentList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(temp.get(Calendar.MONTH), temp.get(Calendar.YEAR)));
                orignalList = AppointmentList;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            adapter = new CalMonthAdapter(getActivity(), month, temp.get(Calendar.MONTH), appointmentList, AppointmentList);
        }

        gridview = (CustomGridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);
        gridview.setVerticalScrollBarEnabled(false);


        tempCal.set(Calendar.HOUR_OF_DAY, 0);
        tempCal.set(Calendar.MINUTE, 0);

        ArrayList<AppointmentMappingModel> listAppointment = null;


        if (tempCal.get(Calendar.MONTH) == calTitle.get(Calendar.MONTH) && tempCal.get(Calendar.YEAR) == calTitle.get(Calendar.YEAR)) {
            listAppointmentbyMonth.setAdapter(adapterEventMonth);
            listAppointmentbyMonth.setVisibility(View.VISIBLE);
            currentDate.setVisibility(View.VISIBLE);
            todayLayout.setVisibility(View.VISIBLE);

            try {
                listAppointment = (ArrayList<AppointmentMappingModel>) getEventsbyDay_New(tempCal);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            adapterEventMonth = new AdapterEventMonth(getActivity(), listAppointment);
            listAppointmentbyMonth.setAdapter(adapterEventMonth);

        } else {

            adapterEventMonth = new AdapterEventMonth(getActivity(), null);
            listAppointmentbyMonth.setAdapter(adapterEventMonth);

            listAppointmentbyMonth.setVisibility(View.GONE);
            currentDate.setVisibility(View.GONE);
            todayLayout.setVisibility(View.GONE);
        }


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                lastSelectedPosition = position;

                // ((CalMonthAdapter) parent.getAdapter()).setSelected(v);
                String selectedGridDate = CalMonthAdapter.dayString
                        .get(position);
//                Log.d("selectedGridDate", "" + selectedGridDate);
                String[] separatedTime = selectedGridDate.split("-");
//                Log.d("position", "" + position);
//                Log.d("separatedTime0", "" + separatedTime[0]);
//                Log.d("separatedTime1", "" + separatedTime[1]);
//                Log.d("separatedTime2", "" + separatedTime[2]);
                String gridvalueString = separatedTime[2].replaceFirst("^0*",
                        "");// taking last part of date. ie; 2 from 2012-12-02.
                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                ((CalMonthAdapter) parent.getAdapter()).setSelected(v);

                Calendar currentCal = Calendar.getInstance();
                currentCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(separatedTime[2]));
                currentCal.set(Calendar.MONTH, Integer.parseInt(separatedTime[1]) - 1);
                currentCal.set(Calendar.YEAR, Integer.parseInt(separatedTime[0]));

                currentDate.setText(df.format(currentCal.getTime()).toUpperCase());
                day_.setText(df1.format(currentCal.getTime()).toUpperCase());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                ArrayList<AppointmentMappingModel> selectedDatList;

                selectedDatList = adapter.getAppointmentAtPosition(formatter.format(currentCal.getTime()));
                adapterEventMonth.updateEvent(selectedDatList);

                selectedDateTime = sdf.format(currentCal.getTime());

                currentDate.setVisibility(View.VISIBLE);
                todayLayout.setVisibility(View.VISIBLE);
                listAppointmentbyMonth.setVisibility(View.VISIBLE);

            }
        });

        listAppointmentbyMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AppointmentMappingModel appointmentSelected = (AppointmentMappingModel) adapterEventMonth.getItem(i);

                if (appointmentSelected.getAppointment_name() != null) {
                    Intent intent = new Intent(getActivity(), ActivityShowAppointment.class);
                    if (appointmentSelected.getEventId() == 0) {
                        intent.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, 0);
                        intent.putExtra(AppConstants.EXTERNAL_APPOINTMENT_ID, appointmentSelected.getExternalId());
                    } else {
                        intent.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, appointmentSelected.getEventId());
                        intent.putExtra(AppConstants.EXTERNAL_APPOINTMENT_ID, "");
                    }
                    startActivity(intent);
                } else {

                }
            }
        });

        //attendee list filter

        listAttendee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Calendar calendarCurrent = Calendar.getInstance();

                AttendeeModel attendeeModel = (AttendeeModel) attendeeAdapter.getItem(i);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                id_uk = attendeeModel.getId();
                type = attendeeModel.getType();

                if (attendeeModel.getName().equals("All")) {
                    ((DashboardActivity) getActivity()).filter = false;
                    filterFlagNew = false;
                    if (calendarCurrent.get(Calendar.MONTH) == calendar_.get(Calendar.MONTH) && calendarCurrent.get(Calendar.YEAR) == calendar_.get(Calendar.YEAR)) {
                        try {
                            flag = false;
                            adapterEventMonth.updateEvent(getAppointmentByDay(formatter.format(calendarCurrent.getTime())));
                            listAppointmentbyMonth.setVisibility(View.VISIBLE);
                            adapter.setCommingFromFilterFlag(false);
                            adapter.setFlag(false);
                            orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                            adapter.notifyDataChange(orignalList);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Calendar newTempCal = Calendar.getInstance();
                            newTempCal.set(Calendar.DAY_OF_MONTH, 1);
                            newTempCal.set(Calendar.MONTH, calendar_.get(Calendar.MONTH));
                            newTempCal.set(Calendar.YEAR, calendar_.get(Calendar.YEAR));
                            flag = false;
                            adapter.setCommingFromFilterFlag(true);
                            adapter.setFlag(false);
                            orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                            adapter.notifyDataChange(orignalList);

                            adapterEventMonth.updateEvent(adapter.getAppointmentAtPosition(formatter.format(newTempCal.getTime())));
                            listAppointmentbyMonth.setVisibility(View.VISIBLE);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    filterFlagNew = true;

                    flag = true;
                    ((DashboardActivity) getActivity()).filter = true;
                    adapter.setFlag(true);
//                    currentDate.setText("");
                    day_.setText("");

                    // get colorcode
                    String colorcode = "";
                    if (attendeeModel.getType().equalsIgnoreCase("kid")) {
                        colorcode = new TableKids(BaseApplication.getInstance()).getKids(attendeeModel.getId()).getColor_code();
                    } else {
                        colorcode = new TableAdult(BaseApplication.getInstance()).getAdults(attendeeModel.getId()).getColor_code();
                    }

                    ((DashboardActivity) getActivity()).selected_colorcode = colorcode;

                    if (calendarCurrent.get(Calendar.MONTH) == calendar_.get(Calendar.MONTH) && calendarCurrent.get(Calendar.YEAR) == calendar_.get(Calendar.YEAR)) {
                        adapter.setCommingFromFilterFlag(false);
                        try {
                            orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataChange(filterAppointmentListInternal());
                        adapterEventMonth.updateEvent(adapter.getAppointmentAtPosition(formatter.format(calendarCurrent.getTime())));
                        listAppointmentbyMonth.setVisibility(View.VISIBLE);

                    } else {
                        Calendar newTempCal = Calendar.getInstance();
                        newTempCal.set(Calendar.DAY_OF_MONTH, 1);
                        newTempCal.set(Calendar.MONTH, calendar_.get(Calendar.MONTH));
                        newTempCal.set(Calendar.YEAR, calendar_.get(Calendar.YEAR));
                        adapter.setCommingFromFilterFlag(true);
                        try {
                            orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataChange(filterAppointmentListInternal());
                        adapterEventMonth.updateEvent(adapter.getAppointmentAtPosition(formatter.format(newTempCal.getTime())));
                        listAppointmentbyMonth.setVisibility(View.VISIBLE);
                    }
                }
                attendeeFrame.setVisibility(View.GONE);
                ((DashboardActivity) getActivity()).refreshMenu();
            }
        });

        addAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar temp = Calendar.getInstance();
                Date tempDate = null;
                try {
                    tempDate = sdf.parse(selectedDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                temp.setTime(tempDate);

                temp.add(Calendar.HOUR_OF_DAY, 1);

                Intent intent = new Intent(getActivity(), ActivityCreateAppointment.class);
                intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, "");
                intent.putExtra(Constants.EVENT_NAME, "");
                intent.putExtra(Constants.EVENT_LOCATION, "");
                intent.putExtra(Constants.EVENT_START_DATE, selectedDateTime);
                intent.putExtra(Constants.EVENT_END_DATE, sdf.format(temp.getTime()));
                startActivityForResult(intent, 1);

            }
        });

        final GestureDetector gdt = new GestureDetector(new GestureListener());

        gridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });

        gridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gdt.onTouchEvent(event)) {
                    MotionEvent cancelEvent = MotionEvent.obtain(event);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    v.onTouchEvent(cancelEvent);

                    return true;
                }
                return false;
            }
        });

        selectedDateTime = sdf.format(System.currentTimeMillis());

        // Inflate the layout for this fragment
        return view;
    }

    public void setTitle(String title) {

        ((DashboardActivity) getActivity()).setTitle(title);
    }

    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }

        Calendar checkCurrentState = Calendar.getInstance();
        if (month.get(Calendar.MONTH) == checkCurrentState.get(Calendar.MONTH) && month.get(Calendar.YEAR) == checkCurrentState.get(Calendar.YEAR)) {
            selectedDateTime = sdf.format(System.currentTimeMillis());
        } else {
            checkCurrentState = (Calendar) month.clone();
            checkCurrentState.set(Calendar.DAY_OF_MONTH, 1);
            selectedDateTime = sdf.format(checkCurrentState.getTime());
        }

    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }

        Calendar checkCurrentState = Calendar.getInstance();
        if (month.get(Calendar.MONTH) == checkCurrentState.get(Calendar.MONTH) && month.get(Calendar.YEAR) == checkCurrentState.get(Calendar.YEAR)) {
            selectedDateTime = sdf.format(System.currentTimeMillis());
        } else {
            checkCurrentState = (Calendar) month.clone();
            checkCurrentState.set(Calendar.DAY_OF_MONTH, 1);
            selectedDateTime = sdf.format(checkCurrentState.getTime());
        }

    }

    protected void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

    }

    public void refreshCalendar() {

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void updateUi(Response response) {

    }

    public void setCurrentDate(int day, int month, int year) {

        SimpleDateFormat df1 = new SimpleDateFormat("d MMM", Locale.US);
        SimpleDateFormat df2 = new SimpleDateFormat("EEEE", Locale.US);

        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.DAY_OF_MONTH, day);
        currentCal.set(Calendar.MONTH, month);
        currentCal.set(Calendar.YEAR, year);

        currentDate.setText(df1.format(currentCal.getTime()).toUpperCase());
        day_.setText(df2.format(currentCal.getTime()).toUpperCase());
    }


    public ArrayList<AppointmentMappingModel> getAppointmentforMonth(String first, String last, long firstTS, long lastTS) throws ParseException {

        ArrayList<AppointmentMappingModel> appointmentListData;

        String firstDateString;
        String lastDateString;

        firstDateString = first;
        lastDateString = last;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date firstDate = formatter.parse(firstDateString);
        Date lastDate = formatter.parse(lastDateString);

        appointmentListData = (ArrayList<AppointmentMappingModel>) tableAppointment.allDataBTWNdays(firstTS, lastTS);

        return appointmentListData;
    }

    public ArrayList<AppointmentMappingModel> getEventsbyDay(Calendar cal) throws ParseException {

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

        ArrayList<AppointmentMappingModel> appointmentModels = new ArrayList<>();

        Calendar calendar = cal;
//        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);


        for (int j = 0; j < this.AppointmentList.size(); j++) {

            String temp = mFormat.format(calendar.getTime());
            String temp1 = getDate(appointmentList.get(j).getStarttime());

            if (temp.equals(getDate(appointmentList.get(j).getStarttime()))) {
                appointmentModels.add(this.appointmentList.get(j));
            }

        }

        if (appointmentModels.size() == 0) {
            appointmentModels.add(new AppointmentMappingModel(0, null, null));
        }

        return appointmentModels;
    }

    public ArrayList<AppointmentMappingModel> getEventsbyDay_New(Calendar cal) throws ParseException {

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

        ArrayList<AppointmentMappingModel> appointmentModels = new ArrayList<>();

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

            String temp = mFormat.format(calendar.getTime());
            appointmentModels = this.AppointmentList.get(temp);

            if (appointmentModels.size() == 0) {
                appointmentModels.add(new AppointmentMappingModel(0, null, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return appointmentModels;
    }


    public void hideFilter() {
        if (attendeeFrame.getVisibility() == View.VISIBLE)
            attendeeFrame.setVisibility(View.GONE);
    }

    public void showFilter() {

        if (attendeeFrame.getVisibility() == View.VISIBLE) {
            attendeeFrame.setVisibility(View.GONE);
        } else {
            attendeeFrame.setVisibility(View.VISIBLE);
        }
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


    public ArrayList<AppointmentMappingModel> filterList(int id, String type, String first, String last) throws ParseException {

        ArrayList<AppointmentMappingModel> appointmentListData;

        String firstDateString;
        String lastDateString;

        firstDateString = first;
        lastDateString = last;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date firstDate = formatter.parse(firstDateString);

        Calendar f1 = Calendar.getInstance();
        f1.setTime(firstDate);
        f1.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));


        Date lastDate = formatter.parse(lastDateString);

        Calendar l1 = Calendar.getInstance();
        l1.setTime(lastDate);
        l1.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

//        lastDate.setTime(86399999);

        java.sql.Timestamp firsttamp = new java.sql.Timestamp(firstDate.getTime());
        java.sql.Timestamp laststamp = new java.sql.Timestamp(lastDate.getTime());

        appointmentListData = tableAppointment.filterByName(id, type, f1.getTimeInMillis(), l1.getTimeInMillis() + 86280000);

        return appointmentListData;

    }

    public void refreshCalender_afterAdd() throws ParseException {

        Calendar calendar = Calendar.getInstance();

        if (filterFlagNew == true) {
            adapterEventMonth.updateEvent(getFilteredAppointmentByDay(formatter.format(calendar_.getTime()), id_uk, type, false));
            orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
            adapter.notifyDataChange(filterAppointmentListInternal());
        } else {
            adapterEventMonth.updateEvent(getAppointmentByDay(formatter.format(calendar_.getTime())));
            orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
            adapter.notifyDataChange(orignalList);
        }

//        ((CalMonthAdapter) gridview.getAdapter()).setSelected(gridview.getChildAt(lastSelectedPosition));

//        adapter.setSelected(gridview.setSelected(gridview.getChildAt(lastSelectedPosition)));
//        adapter.setLastSelectedView();

//        gridview.setSelection(lastSelectedPosition);
//        adapter.setSelected(gridview.getChildAt(lastSelectedPosition));
    }

    public boolean checkCurrentDateValid(String currentdate, long startdate) {
        boolean result = false;

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
        String taskstartdate = f1.format(startdate);

        Date dateCurrent = new Date();
        Date dateStart = new Date();
        try {

            dateCurrent = (Date) f1.parse(currentdate);
            dateStart = (Date) f1.parse(taskstartdate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendarCurrent.clear();
        calendarCurrent.setTime(dateCurrent);
        calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
        calendarCurrent.set(Calendar.MINUTE, 58);

        Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendarStart.clear();
        calendarStart.setTime(dateStart);
        calendarStart.set(Calendar.HOUR_OF_DAY, 23);
        calendarStart.set(Calendar.MINUTE, 58);

        if (calendarCurrent.getTimeInMillis() >= calendarStart.getTimeInMillis()) {
            return true;
        }

        return result;
    }

    public LinkedHashMap<String, ArrayList<AppointmentMappingModel>> getDatafromDB(List<String> allDays) throws ParseException {

        ArrayList<AppointmentMappingModel> appointmentModels;
        ArrayList<AppointmentMappingModel> tempAppointmentModels;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finaldata = new LinkedHashMap<String, ArrayList<AppointmentMappingModel>>();
        List<String> dateList = allDays;

        String firstDateString;
        String lastDateString;

        firstDateString = dateList.get(0);
        lastDateString = dateList.get(dateList.size() - 1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date firstDate = formatter.parse(firstDateString);
        Date lastDate = formatter.parse(lastDateString);

        java.sql.Timestamp firsttamp = new java.sql.Timestamp(firstDate.getTime());
        java.sql.Timestamp laststamp = new java.sql.Timestamp(lastDate.getTime());

        long first = firsttamp.getTime();
        long last = laststamp.getTime() + 86280000;

        String f11 = firstDateString + " 12:00 AM";
        String f22 = lastDateString + " 11:59 PM";

        long fff = convertTimeStamp_new(f11);
        long lll = convertTimeStamp_new(f22);
        TableAppointmentData tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
        appointmentModels = tableAppointment.allDataBTWNdays(fff, lll);

        ArrayList<AttendeeModel> nullList = new ArrayList<AttendeeModel>();

        for (int i = 0; i < allDays.size(); i++) {

            tempAppointmentModels = new ArrayList<>();

            for (int j = 0; j < appointmentModels.size(); j++) {

                if (checkCurrentDateValid(allDays.get(i), appointmentModels.get(j).getStarttime())) {
                    java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(allDays.get(i)).getTime());

                    String selectDate = allDays.get(i) + " 12:00 AM";
                    long selectDateTS = convertTimeStamp_new(selectDate);
                    String selectDateLast = allDays.get(i) + " 11:59 PM";
                    long selectDateLastTS = convertTimeStamp_new(selectDateLast);


                    if (appointmentModels.get(j).getStarttime() >= selectDateTS && appointmentModels.get(j).getStarttime() <= selectDateLastTS) {

                        tempAppointmentModels.add(appointmentModels.get(j));

                    } else {
                        // condition until check pick date

                        if (!StringUtils.isNullOrEmpty(appointmentModels.get(j).getRepeate_untill())) {
                            if (appointmentModels.get(j).getRepeate_untill().equalsIgnoreCase("forever")) {

                                if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                                    String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                                    for (String day : daysArray) {
                                        boolean result = chkDays(day, allDays.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));
                                    }

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                                    tempAppointmentModels.add(appointmentModels.get(j));
                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                                    // check start date
                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "weekly", allDays.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "monthly", allDays.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "yearly", allDays.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), allDays.get(i), Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));
                                }

                            } else {
                                String pickdate = appointmentModels.get(j).getRepeate_untill();
                                String currentdate = allDays.get(i);

                                SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");

                                Date dateCurrent = new Date();
                                dateCurrent = (Date) f1.parse(currentdate);

                                Date dateUntil = (Date) f2.parse(pickdate);
                                String untilFinal = f1.format(dateUntil);

                                Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                calendarCurrent.clear();
                                calendarCurrent.setTime(dateCurrent);
                                calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
                                calendarCurrent.set(Calendar.MINUTE, 58);

                                Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                calendarUntil.clear();
                                calendarUntil.setTime(dateUntil);
                                calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
                                calendarUntil.set(Calendar.MINUTE, 58);

                                if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {

                                    if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                                        String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                                        for (String day : daysArray) {
                                            boolean result = chkDays(day, allDays.get(i));
                                            if (result)
                                                tempAppointmentModels.add(appointmentModels.get(j));
                                        }

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                                        tempAppointmentModels.add(appointmentModels.get(j));
                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                                        // check start date
                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "weekly", allDays.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "monthly", allDays.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "yearly", allDays.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {


                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), allDays.get(i), Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));
                                    }

                                }
                            }
                        }
                    }
                }
            }
            if (tempAppointmentModels.size() == 0) {
                tempAppointmentModels.add(new AppointmentMappingModel(0, null, null));
            }
            finaldata.put(allDays.get(i), tempAppointmentModels);
        }

        return finaldata;
    }

    public LinkedHashMap<String, ArrayList<AppointmentMappingModel>> getFilteredDatafromDB(List<String> allDays, int id, String type) throws ParseException {

        ArrayList<AppointmentMappingModel> appointmentModels;
        ArrayList<AppointmentMappingModel> tempAppointmentModels;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finaldata = new LinkedHashMap<String, ArrayList<AppointmentMappingModel>>();
        List<String> dateList = allDays;

        String firstDateString;
        String lastDateString;

        firstDateString = dateList.get(0);
        lastDateString = dateList.get(dateList.size() - 1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date firstDate = formatter.parse(firstDateString);
        Date lastDate = formatter.parse(lastDateString);

        java.sql.Timestamp firsttamp = new java.sql.Timestamp(firstDate.getTime());
        java.sql.Timestamp laststamp = new java.sql.Timestamp(lastDate.getTime());

        long first = firsttamp.getTime();
        long last = laststamp.getTime() + 86280000;

        String f11 = firstDateString + " 12:01 AM";
        String f22 = lastDateString + " 11:59 PM";

        long fff = convertTimeStamp_new(f11);
        long lll = convertTimeStamp_new(f22);

        TableAppointmentData tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
//        appointmentModels = tableAppointment.filterByName(id, type, first, last);
        appointmentModels = tableAppointment.filterByName(id, type, fff, lll);

//        commented by manish

//        ArrayList<AttendeeModel> nullList = new ArrayList<AttendeeModel>();
//
//        for (int i = 0; i < allDays.size(); i++) {
//
//            tempAppointmentModels = new ArrayList<>();
//
//            for (int j = 0; j < appointmentModels.size(); j++) {
//                java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(allDays.get(i)).getTime());
//
//
//                String selectDate = allDays.get(i) + " 12:01 AM";
//                long selectDateTS = convertTimeStamp_new(selectDate);
//                String selectDateLast = allDays.get(i) + " 11:59 PM";
//                long selectDateLastTS = convertTimeStamp_new(selectDateLast);
//
//
////                if (appointmentModels.get(j).getStarttime() >= tempTimestamp.getTime() && appointmentModels.get(j).getStarttime() <= (tempTimestamp.getTime() + 86280000)) {
//                if (appointmentModels.get(j).getStarttime() >= selectDateTS && appointmentModels.get(j).getStarttime() <= selectDateLastTS) {
//
//                    tempAppointmentModels.add(appointmentModels.get(j));
//
//                } else {
//                    // condition until check pick date
//
//                    if (!StringUtils.isNullOrEmpty(appointmentModels.get(j).getRepeate_untill())) {
//                        if (appointmentModels.get(j).getRepeate_untill().equalsIgnoreCase("forever")) {
//
//                            if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {
//
//                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {
//
//                                String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
//                                for (String day : daysArray) {
//                                    boolean result = chkDays(day, allDays.get(i));
//                                    if (result)
//                                        tempAppointmentModels.add(appointmentModels.get(j));
//                                }
//
//                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
//                                tempAppointmentModels.add(appointmentModels.get(j));
//                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
//                                // check start date
//                                long starttime = appointmentModels.get(j).getStarttime();
//                                boolean result = getValues(starttime, "weekly", allDays.get(i));
//                                if (result)
//                                    tempAppointmentModels.add(appointmentModels.get(j));
//
//                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {
//
//                                long starttime = appointmentModels.get(j).getStarttime();
//                                boolean result = getValues(starttime, "monthly", allDays.get(i));
//                                if (result)
//                                    tempAppointmentModels.add(appointmentModels.get(j));
//
//                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {
//
//                                long starttime = appointmentModels.get(j).getStarttime();
//                                boolean result = getValues(starttime, "yearly", allDays.get(i));
//                                if (result)
//                                    tempAppointmentModels.add(appointmentModels.get(j));
//
//                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {
//
//                                long starttime = appointmentModels.get(j).getStarttime();
//                                boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), allDays.get(i), Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
//                                if (result)
//                                    tempAppointmentModels.add(appointmentModels.get(j));
//                            }
//
//
//                        } else {
//                            String pickdate = appointmentModels.get(j).getRepeate_untill();
//                            String currentdate = allDays.get(i);
//
//                            SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
//                            SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");
//
//                            Date dateCurrent = new Date();
//                            dateCurrent = (Date) f1.parse(currentdate);
//
//                            Date dateUntil = (Date) f2.parse(pickdate);
//                            String untilFinal = f1.format(dateUntil);
//
//                            Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
//                            calendarCurrent.clear();
//                            calendarCurrent.setTime(dateCurrent);
//                            calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
//                            calendarCurrent.set(Calendar.MINUTE, 58);
//
//                            Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
//                            calendarUntil.clear();
//                            calendarUntil.setTime(dateUntil);
//                            calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
//                            calendarUntil.set(Calendar.MINUTE, 58);
//
//                            if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {
//
//                                if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {
//
//                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {
//
//                                    String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
//                                    for (String day : daysArray) {
//                                        boolean result = chkDays(day, allDays.get(i));
//                                        if (result)
//                                            tempAppointmentModels.add(appointmentModels.get(j));
//                                    }
//
//                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
//                                    tempAppointmentModels.add(appointmentModels.get(j));
//                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
//                                    // check start date
//                                    long starttime = appointmentModels.get(j).getStarttime();
//                                    boolean result = getValues(starttime, "weekly", allDays.get(i));
//                                    if (result)
//                                        tempAppointmentModels.add(appointmentModels.get(j));
//
//                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {
//
//                                    long starttime = appointmentModels.get(j).getStarttime();
//                                    boolean result = getValues(starttime, "monthly", allDays.get(i));
//                                    if (result)
//                                        tempAppointmentModels.add(appointmentModels.get(j));
//
//                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {
//
//                                    long starttime = appointmentModels.get(j).getStarttime();
//                                    boolean result = getValues(starttime, "yearly", allDays.get(i));
//                                    if (result)
//                                        tempAppointmentModels.add(appointmentModels.get(j));
//
//                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {
//
//
//                                    long starttime = appointmentModels.get(j).getStarttime();
//                                    boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), allDays.get(i), Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
//                                    if (result)
//                                        tempAppointmentModels.add(appointmentModels.get(j));
//                                }
//
//                            }
//                        }
//                    }
//
//                }
//            }
//            if (tempAppointmentModels.size() == 0) {
//                tempAppointmentModels.add(new AppointmentMappingModel(0, null, null));
//            }
//            finaldata.put(allDays.get(i), tempAppointmentModels);
//        }

        ArrayList<AttendeeModel> nullList = new ArrayList<AttendeeModel>();

        for (int i = 0; i < allDays.size(); i++) {

            tempAppointmentModels = new ArrayList<>();

            for (int j = 0; j < appointmentModels.size(); j++) {

                if (checkCurrentDateValid(allDays.get(i), appointmentModels.get(j).getStarttime())) {
                    java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(allDays.get(i)).getTime());

                    String selectDate = allDays.get(i) + " 12:01 AM";
                    long selectDateTS = convertTimeStamp_new(selectDate);
                    String selectDateLast = allDays.get(i) + " 11:59 PM";
                    long selectDateLastTS = convertTimeStamp_new(selectDateLast);


//                if (appointmentModels.get(j).getStarttime() >= tempTimestamp.getTime() && appointmentModels.get(j).getStarttime() <= (tempTimestamp.getTime() + 86280000)) {
                    if (appointmentModels.get(j).getStarttime() >= selectDateTS && appointmentModels.get(j).getStarttime() <= selectDateLastTS) {

                        tempAppointmentModels.add(appointmentModels.get(j));

                    } else {
                        // condition until check pick date

                        if (!StringUtils.isNullOrEmpty(appointmentModels.get(j).getRepeate_untill())) {
                            if (appointmentModels.get(j).getRepeate_untill().equalsIgnoreCase("forever")) {

                                if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                                    String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                                    for (String day : daysArray) {
                                        boolean result = chkDays(day, allDays.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));
                                    }


                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                                    tempAppointmentModels.add(appointmentModels.get(j));
                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                                    // check start date
                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "weekly", allDays.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "monthly", allDays.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "yearly", allDays.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), allDays.get(i), Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));
                                }

                            } else {
                                String pickdate = appointmentModels.get(j).getRepeate_untill();
                                String currentdate = allDays.get(i);

                                SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");

                                Date dateCurrent = new Date();
                                dateCurrent = (Date) f1.parse(currentdate);

                                Date dateUntil = (Date) f2.parse(pickdate);
                                String untilFinal = f1.format(dateUntil);

                                Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                calendarCurrent.clear();
                                calendarCurrent.setTime(dateCurrent);
                                calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
                                calendarCurrent.set(Calendar.MINUTE, 58);

                                Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                calendarUntil.clear();
                                calendarUntil.setTime(dateUntil);
                                calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
                                calendarUntil.set(Calendar.MINUTE, 58);

                                if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {

                                    if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                                        String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                                        for (String day : daysArray) {
                                            boolean result = chkDays(day, allDays.get(i));
                                            if (result)
                                                tempAppointmentModels.add(appointmentModels.get(j));
                                        }

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                                        tempAppointmentModels.add(appointmentModels.get(j));
                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                                        // check start date
                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "weekly", allDays.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "monthly", allDays.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "yearly", allDays.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {


                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), allDays.get(i), Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));
                                    }

                                }
                            }
                        }

                    }
                }
            }
            if (tempAppointmentModels.size() == 0) {
                tempAppointmentModels.add(new AppointmentMappingModel(0, null, null));
            }
            finaldata.put(allDays.get(i), tempAppointmentModels);
        }

        return finaldata;
    }


    public ArrayList<AppointmentMappingModel> getAppointmentByDay(String Date) throws ParseException {

        ArrayList<AppointmentMappingModel> appointmentModels;
        ArrayList<AppointmentMappingModel> tempAppointmentModels;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

//        formatter.setTimeZone(TimeZone.getDefault());

        String f11 = Date + " 12:01 AM";
        String f22 = Date + " 11:59 PM";

        long fff = convertTimeStamp_new(f11);
        long lll = convertTimeStamp_new(f22);


        Date firstDate = formatter.parse(Date);
        Date lastDate = formatter.parse(Date);

        java.sql.Timestamp firsttamp = new java.sql.Timestamp(firstDate.getTime());
        java.sql.Timestamp laststamp = new java.sql.Timestamp(lastDate.getTime());

        long first = firsttamp.getTime();
        long last = laststamp.getTime() + 86280000;

        TableAppointmentData tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
//        appointmentModels = tableAppointment.allDataBTWNdays(first, last);

        appointmentModels = tableAppointment.allDataBTWNdays(fff, lll);

        ArrayList<AttendeeModel> nullList = new ArrayList<AttendeeModel>();


        tempAppointmentModels = new ArrayList<>();

        for (int j = 0; j < appointmentModels.size(); j++) {
            java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(Date).getTime());

            if (appointmentModels.get(j).getStarttime() >= fff && appointmentModels.get(j).getStarttime() <= lll) {
                tempAppointmentModels.add(appointmentModels.get(j));

            } else {
                // condition until check pick date

                if (!StringUtils.isNullOrEmpty(appointmentModels.get(j).getRepeate_untill())) {
                    if (appointmentModels.get(j).getRepeate_untill().equalsIgnoreCase("forever")) {

                        if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                            String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                            for (String day : daysArray) {
                                boolean result = chkDays(day, Date);
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));
                            }


                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                            tempAppointmentModels.add(appointmentModels.get(j));
                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                            // check start date
                            long starttime = appointmentModels.get(j).getStarttime();
                            boolean result = getValues(starttime, "weekly", Date);
                            if (result)
                                tempAppointmentModels.add(appointmentModels.get(j));

                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                            long starttime = appointmentModels.get(j).getStarttime();
                            boolean result = getValues(starttime, "monthly", Date);
                            if (result)
                                tempAppointmentModels.add(appointmentModels.get(j));

                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                            long starttime = appointmentModels.get(j).getStarttime();
                            boolean result = getValues(starttime, "yearly", Date);
                            if (result)
                                tempAppointmentModels.add(appointmentModels.get(j));

                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {

                            long starttime = appointmentModels.get(j).getStarttime();
                            boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), Date, Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                            if (result)
                                tempAppointmentModels.add(appointmentModels.get(j));
                        }


                    } else {
                        String pickdate = appointmentModels.get(j).getRepeate_untill();
                        String currentdate = Date;

                        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");

                        Date dateCurrent = new Date();
                        dateCurrent = (Date) f1.parse(currentdate);

                        Date dateUntil = (Date) f2.parse(pickdate);
                        String untilFinal = f1.format(dateUntil);

                        Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                        calendarCurrent.clear();
                        calendarCurrent.setTime(dateCurrent);
                        calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
                        calendarCurrent.set(Calendar.MINUTE, 58);

                        Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                        calendarUntil.clear();
                        calendarUntil.setTime(dateUntil);
                        calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
                        calendarUntil.set(Calendar.MINUTE, 58);

                        if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {

                            if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                                String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                                for (String day : daysArray) {
                                    boolean result = chkDays(day, Date);
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));
                                }

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                                tempAppointmentModels.add(appointmentModels.get(j));
                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                                // check start date
                                long starttime = appointmentModels.get(j).getStarttime();
                                boolean result = getValues(starttime, "weekly", Date);
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                                long starttime = appointmentModels.get(j).getStarttime();
                                boolean result = getValues(starttime, "monthly", Date);
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                                long starttime = appointmentModels.get(j).getStarttime();
                                boolean result = getValues(starttime, "yearly", Date);
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {


                                long starttime = appointmentModels.get(j).getStarttime();
                                boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), Date, Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));
                            }

                        }
                    }
                }

            }
        }
        if (tempAppointmentModels.size() == 0) {
            tempAppointmentModels.add(new AppointmentMappingModel(0, null, null));
        }
//            finaldata.put(Date, tempAppointmentModels);


        return tempAppointmentModels;
    }

    public long convertTimeStamp_new(CharSequence date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        Date tempDate = formatter.parse((String) date);
        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());
//        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

    public ArrayList<AppointmentMappingModel> getFilteredAppointmentByDay(String Date, int id, String type, Boolean flag) throws ParseException {

        ArrayList<AppointmentMappingModel> appointmentModels;
        ArrayList<AppointmentMappingModel> tempAppointmentModels;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finaldata = new LinkedHashMap<String, ArrayList<AppointmentMappingModel>>();

        String firstDateString;
        String lastDateString;


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

//        formatter.setTimeZone(TimeZone.getDefault());

        String f11 = Date + " 12:01 AM";
        String f22 = Date + " 11:59 PM";

        long fff = convertTimeStamp_new(f11);
        long lll = convertTimeStamp_new(f22);

        Date firstDate = formatter.parse(Date);
        Date lastDate = formatter.parse(Date);

        java.sql.Timestamp firsttamp = new java.sql.Timestamp(firstDate.getTime());
        java.sql.Timestamp laststamp = new java.sql.Timestamp(lastDate.getTime());

        long first = firsttamp.getTime();
        long last = laststamp.getTime() + 86280000;

        TableAppointmentData tableAppointment = new TableAppointmentData(BaseApplication.getInstance());

        if (flag) {
            appointmentModels = tableAppointment.filterByName_2(id, type);
        } else {
//            appointmentModels = tableAppointment.filterByName_1(id, type, first, last);
            appointmentModels = tableAppointment.filterByName_1(id, type, fff, lll);
        }

        ArrayList<AttendeeModel> nullList = new ArrayList<AttendeeModel>();
        tempAppointmentModels = new ArrayList<>();

        for (int j = 0; j < appointmentModels.size(); j++) {
            java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(Date).getTime());
//            if (appointmentModels.get(j).getStarttime() >= tempTimestamp.getTime() && appointmentModels.get(j).getStarttime() <= (tempTimestamp.getTime() + 86280000)) {
            if (appointmentModels.get(j).getStarttime() >= fff && appointmentModels.get(j).getStarttime() <= lll) {

                tempAppointmentModels.add(appointmentModels.get(j));

            } else {
                // condition until check pick date

                if (!StringUtils.isNullOrEmpty(appointmentModels.get(j).getRepeate_untill())) {
                    if (appointmentModels.get(j).getRepeate_untill().equalsIgnoreCase("forever")) {

                        if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                            String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                            for (String day : daysArray) {
                                boolean result = chkDays(day, Date);
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));
                            }


                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                            tempAppointmentModels.add(appointmentModels.get(j));
                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                            // check start date
                            long starttime = appointmentModels.get(j).getStarttime();
                            boolean result = getValues(starttime, "weekly", Date);
                            if (result)
                                tempAppointmentModels.add(appointmentModels.get(j));

                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                            long starttime = appointmentModels.get(j).getStarttime();
                            boolean result = getValues(starttime, "monthly", Date);
                            if (result)
                                tempAppointmentModels.add(appointmentModels.get(j));

                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                            long starttime = appointmentModels.get(j).getStarttime();
                            boolean result = getValues(starttime, "yearly", Date);
                            if (result)
                                tempAppointmentModels.add(appointmentModels.get(j));

                        } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {

                            long starttime = appointmentModels.get(j).getStarttime();
                            boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), Date, Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                            if (result)
                                tempAppointmentModels.add(appointmentModels.get(j));
                        }


                    } else {
                        String pickdate = appointmentModels.get(j).getRepeate_untill();
                        String currentdate = Date;

                        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");

                        Date dateCurrent = new Date();
                        dateCurrent = (Date) f1.parse(currentdate);

                        Date dateUntil = (Date) f2.parse(pickdate);
                        String untilFinal = f1.format(dateUntil);

                        Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                        calendarCurrent.clear();
                        calendarCurrent.setTime(dateCurrent);
                        calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
                        calendarCurrent.set(Calendar.MINUTE, 58);

                        Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                        calendarUntil.clear();
                        calendarUntil.setTime(dateUntil);
                        calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
                        calendarUntil.set(Calendar.MINUTE, 58);

                        if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {

                            if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                                String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                                for (String day : daysArray) {
                                    boolean result = chkDays(day, Date);
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));
                                }

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                                tempAppointmentModels.add(appointmentModels.get(j));
                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                                // check start date
                                long starttime = appointmentModels.get(j).getStarttime();
                                boolean result = getValues(starttime, "weekly", Date);
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                                long starttime = appointmentModels.get(j).getStarttime();
                                boolean result = getValues(starttime, "monthly", Date);
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                                long starttime = appointmentModels.get(j).getStarttime();
                                boolean result = getValues(starttime, "yearly", Date);
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));

                            } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {

                                long starttime = appointmentModels.get(j).getStarttime();
                                boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), Date, Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                                if (result)
                                    tempAppointmentModels.add(appointmentModels.get(j));
                            }

                        }
                    }

                }
            }
        }
        if (tempAppointmentModels.size() == 0) {
            tempAppointmentModels.add(new AppointmentMappingModel(0, null, null));
        }
//            finaldata.put(allDays.get(i), tempAppointmentModels);


        return tempAppointmentModels;
    }


    public boolean chkDays(String appointmentday, String cureentdate) throws ParseException {
        boolean result = false;

        appointmentday = appointmentday.replace(" ", "");

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(date);

        if (dayOfTheWeek.equalsIgnoreCase(appointmentday))
            result = true;

        return result;
    }


    public boolean getValues(long apointmenttime, String repeat, String cureentdate) throws ParseException {

        boolean result = false;

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        String appointmentDate = f1.format(apointmenttime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(apointmenttime);

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);

        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendar1.clear();
        calendar1.setTime(date);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 58);


        if (repeat.equalsIgnoreCase("monthly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;

                }
                calendar.add(Calendar.MONTH, 1);
            }

        } else if (repeat.equalsIgnoreCase("yearly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;

                }
                calendar.add(Calendar.YEAR, 1);
            }

        } else if (repeat.equalsIgnoreCase("weekly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;

                }
                calendar.add(Calendar.DAY_OF_MONTH, 7);
            }

        }

        return result;
    }

    public boolean getOtherValues(long apointmenttime, String repeat, String cureentdate, int count) throws ParseException {

        boolean result = false;

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        String appointmentDate = f1.format(apointmenttime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(apointmenttime);

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);

        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendar1.clear();
        calendar1.setTime(date);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 58);


        if (repeat.equalsIgnoreCase("months")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.MONTH, count);
            }

        } else if (repeat.equalsIgnoreCase("weeks")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.WEEK_OF_YEAR, count);
            }

        } else if (repeat.equalsIgnoreCase("days")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.DAY_OF_YEAR, count);
            }

        }

        return result;
    }

    public List<String> getDaysbyMonth(int month, int year) {

        List<String> allDays = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

        for (int i = 0; i < daysInMonth; i++) {
            // Add day to list
            allDays.add(i, mFormat.format(calendar.getTime()));
            // Move next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        orignalDaysList = (ArrayList<String>) allDays;

        return allDays;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.back_:
                switchToBack();

                break;

            case R.id.next_:
                switchToNext();
                break;

        }

    }

    public void switchToBack() {

        String title_new = String.valueOf(((DashboardActivity) getActivity()).getTitleText());
        SimpleDateFormat form_new = new SimpleDateFormat("MMM yyyy", Locale.US);
        Date dateTitle = null;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> AppointmentList_new = null;
        try {
            dateTitle = (Date) form_new.parse(title_new);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar_.setTime(dateTitle);

        if (calendar_.get(Calendar.MONTH) == 0) {
            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {

                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 11, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(11, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter.setFlag(false);
                adapter = new CalMonthAdapter(getActivity(), calendar_, 11, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 11) {

            calendar_.add(Calendar.MONTH, -1);
            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 10, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(10, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 10, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 1) {

            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 0, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {

                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(0, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 0, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 2) {
            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter = new CalMonthAdapter(getActivity(), calendar_, 1, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(1, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 1, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 3) {

            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 2, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(2, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 2, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 4) {

            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter = new CalMonthAdapter(getActivity(), calendar_, 3, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(3, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 3, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 5) {

            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 4, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 4, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 6) {

            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter = new CalMonthAdapter(getActivity(), calendar_, 5, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(5, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 5, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        } else if (calendar_.get(Calendar.MONTH) == 7) {

            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter = new CalMonthAdapter(getActivity(), calendar_, 6, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(6, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 6, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 8) {

            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 7, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(7, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 7, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 9) {

            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 8, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(8, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 8, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 10) {
            calendar_.add(Calendar.MONTH, -1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 9, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(9, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 9, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        }

        Calendar tempCal_new = Calendar.getInstance();
        setTitle(form_new.format(calendar_.getTime()));

        if (tempCal_new.get(Calendar.MONTH) == calendar_.get(Calendar.MONTH) && tempCal_new.get(Calendar.YEAR) == calendar_.get(Calendar.YEAR)) {

            if (filterFlagNew) {
                try {
                    adapterEventMonth.updateEvent(getFilteredAppointmentByDay(formatter.format(tempCal_new.getTime()), id_uk, type, true));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
//                need to change
                try {
                    adapterEventMonth = new AdapterEventMonth(getActivity(), getAppointmentByDay(formatter.format(tempCal_new.getTime())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                listAppointmentbyMonth.setAdapter(adapterEventMonth);
                adapterEventMonth.notifyDataSetChanged();
            }

            listAppointmentbyMonth.setVisibility(View.VISIBLE);
            currentDate.setVisibility(View.VISIBLE);
            todayLayout.setVisibility(View.VISIBLE);

        } else {
            listAppointmentbyMonth.setVisibility(View.GONE);
            currentDate.setVisibility(View.VISIBLE);
            todayLayout.setVisibility(View.VISIBLE);
            day_.setText("");
            currentDate.setText("");
        }

        Calendar checkCurrentState = Calendar.getInstance();
        if (calendar_.get(Calendar.MONTH) == checkCurrentState.get(Calendar.MONTH) && calendar_.get(Calendar.YEAR) == checkCurrentState.get(Calendar.YEAR)) {
            selectedDateTime = sdf.format(System.currentTimeMillis());
        } else {
            checkCurrentState = (Calendar) calendar_.clone();
            checkCurrentState.set(Calendar.DAY_OF_MONTH, 1);
            selectedDateTime = sdf.format(checkCurrentState.getTime());
        }

    }

    public void switchToNext() {

        String title_new = String.valueOf(((DashboardActivity) getActivity()).getTitleText());
        SimpleDateFormat form_new = new SimpleDateFormat("MMM yyyy", Locale.US);
        Date dateTitle = null;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> AppointmentList_new = null;
        try {
            dateTitle = (Date) form_new.parse(title_new);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar_.setTime(dateTitle);

        if (calendar_.get(Calendar.MONTH) == 0) {
            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 1, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(1, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 1, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 11) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 0, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(0, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 0, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 1) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 2, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(2, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 2, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 2) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter = new CalMonthAdapter(getActivity(), calendar_, 3, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(3, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 3, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        } else if (calendar_.get(Calendar.MONTH) == 3) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 4, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(4, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 4, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        } else if (calendar_.get(Calendar.MONTH) == 4) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 5, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(5, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 5, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 5) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 6, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(6, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 6, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }


        } else if (calendar_.get(Calendar.MONTH) == 6) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 7, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {

                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(7, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 7, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        } else if (calendar_.get(Calendar.MONTH) == 7) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 8, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {

                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(8, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 8, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        } else if (calendar_.get(Calendar.MONTH) == 8) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter = new CalMonthAdapter(getActivity(), calendar_, 9, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {

                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(9, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 9, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        } else if (calendar_.get(Calendar.MONTH) == 9) {

            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 10, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {

                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(10, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 10, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        } else if (calendar_.get(Calendar.MONTH) == 10) {
            calendar_.add(Calendar.MONTH, 1);

            if (filterFlagNew) {
                try {
                    orignalList = getDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 11, appointmentList, filterAppointmentListInternal());
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {

                try {
                    AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(11, calendar_.get(Calendar.YEAR)));
                    orignalList = AppointmentList_new;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter = new CalMonthAdapter(getActivity(), calendar_, 11, appointmentList, AppointmentList_new);
                gridview.setAdapter(adapter);
                adapter.setFlag(false);
                adapter.notifyDataSetChanged();
            }

        }

        setTitle(form_new.format(calendar_.getTime()));
        Calendar tempCal_new = Calendar.getInstance();

        if (tempCal_new.get(Calendar.MONTH) == calendar_.get(Calendar.MONTH) && tempCal_new.get(Calendar.YEAR) == calendar_.get(Calendar.YEAR)) {

            if (filterFlagNew) {
                try {
                    adapterEventMonth.updateEvent(getFilteredAppointmentByDay(formatter.format(tempCal_new.getTime()), id_uk, type, true));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    adapterEventMonth = new AdapterEventMonth(getActivity(), getAppointmentByDay(formatter.format(tempCal_new.getTime())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                listAppointmentbyMonth.setAdapter(adapterEventMonth);
                adapterEventMonth.notifyDataSetChanged();
            }

            listAppointmentbyMonth.setVisibility(View.VISIBLE);
            currentDate.setVisibility(View.VISIBLE);
            todayLayout.setVisibility(View.VISIBLE);

        } else {
            listAppointmentbyMonth.setVisibility(View.GONE);
            currentDate.setVisibility(View.VISIBLE);
            todayLayout.setVisibility(View.VISIBLE);
            day_.setText("");
            currentDate.setText("");
        }


        Calendar checkCurrentState = Calendar.getInstance();
        if (calendar_.get(Calendar.MONTH) == checkCurrentState.get(Calendar.MONTH) && calendar_.get(Calendar.YEAR) == checkCurrentState.get(Calendar.YEAR)) {
            selectedDateTime = sdf.format(System.currentTimeMillis());
        } else {
            checkCurrentState = (Calendar) calendar_.clone();
            checkCurrentState.set(Calendar.DAY_OF_MONTH, 1);
            selectedDateTime = sdf.format(checkCurrentState.getTime());
        }

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        //        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//
//            Log.d("---onFling---", e1.toString() + e2.toString() + "");
//
//            try {
//                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
//                    return false;
//                // right to left swipe
//                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    //do your code
//                    switchToNext();
//
//                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    //left to right flip
//                    switchToBack();
//
//                }
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//
//        }

        //        private final int SWIPE_MIN_DISTANCE1 = 120;
        private final int SWIPE_MIN_DISTANCE1 = 60;
        private final int SWIPE_THRESHOLD_VELOCITY1 = 200;

//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
////            super.onSingleTapUp(e);
//            MotionEvent cancelEvent = MotionEvent.obtain(e);
//            cancelEvent.setAction(MotionEvent.ACTION_UP);
//            gridview.onTouchEvent(cancelEvent);
//            Log.e("", "Single");
//
//            return false;
//        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE1 && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY1) {
                // Right to left, your code here
                switchToNext();

                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE1 && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY1) {
                // Left to right, your code here

                switchToBack();
                return true;
            }
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE1 && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY1) {
                // Bottom to top, your code here

                scrollView.setScrollX(0);
//                scrollView.setTouchEnabled();

                return false;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE1 && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY1) {
                // Top to bottom, your code here
                scrollView.setScrollX(0);

                return false;
            } else if (e2.getY() - e1.getY() <= 5) {
                return false;
            }
            return false;
        }

    }


    public void updateAppointmentList_New(GregorianCalendar Date) {

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar1 = (Calendar) Date.clone();
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar2 = (Calendar) calendar1.clone();
        calendar2.set(Calendar.DAY_OF_MONTH, calendar1.getActualMaximum(Calendar.DAY_OF_MONTH));

        String date1, date2;

        date1 = formatter.format(calendar1.getTime());
        date2 = formatter.format(calendar2.getTime());

        long fff = 0;
        long lll = 0;

        try {
            fff = convertTimeStamp_new((date1 + " 12:01 AM"));
            lll = convertTimeStamp_new((date2 + " 11:59 PM"));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
//            this.appointmentList = getAppointmentforMonth(formatter.format(firstDate.getTime()), formatter.format(lastDate.getTime()), firstDate.getTimeInMillis(), lastDate.getTimeInMillis());
            this.appointmentList = getAppointmentforMonth(formatter.format(firstDate.getTime()), formatter.format(lastDate.getTime()), fff, lll);

            listAppointment_ = (ArrayList<AppointmentMappingModel>) getEventsbyDay(calendar_);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapterEventMonth = new AdapterEventMonth(getActivity(), listAppointment_);
        listAppointmentbyMonth.setAdapter(adapterEventMonth);
        adapterEventMonth.notifyDataSetChanged();

    }

    public void setTodayCalenderView() {

        Calendar tempC = Calendar.getInstance();

        calendar_.set(Calendar.MONTH, tempC.get(Calendar.MONTH));
        calendar_.set(Calendar.YEAR, tempC.get(Calendar.YEAR));
        calendar_.set(Calendar.DAY_OF_MONTH, 5);

        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> AppointmentList_new = null;
        try {
            AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(tempC.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (filterFlagNew) {

            adapter.setFlag(filterFlagNew);
            try {
//                adapter.notifyDataChange(getFilteredDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)), id_uk, type));

                adapter = new CalMonthAdapter(getActivity(), calendar_, tempC.get(Calendar.MONTH), appointmentList, getFilteredDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)), id_uk, type));
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {

            adapter = new CalMonthAdapter(getActivity(), calendar_, tempC.get(Calendar.MONTH), appointmentList, AppointmentList_new);
            gridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

//        adapter = new CalMonthAdapter(getActivity(), calendar_, tempC.get(Calendar.MONTH), appointmentList, AppointmentList_new);
//        gridview.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

//        updateAppointmentList_New(calendar_);
        SimpleDateFormat form_new = new SimpleDateFormat("MMM yyyy", Locale.US);

        setTitle(form_new.format(tempC.getTime()));
//        dateCurrent.setText(form_new.format(calendar_.getTime()));
        Calendar tempCal_new = Calendar.getInstance();

        if (tempCal_new.get(Calendar.MONTH) == calendar_.get(Calendar.MONTH) && tempCal_new.get(Calendar.YEAR) == calendar_.get(Calendar.YEAR))

        {

            if (filterFlagNew) {
                try {
                    adapterEventMonth.updateEvent(getFilteredAppointmentByDay(formatter.format(tempCal_new.getTime()), id_uk, type, true));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    adapterEventMonth = new AdapterEventMonth(getActivity(), getAppointmentByDay(formatter.format(tempCal_new.getTime())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                listAppointmentbyMonth.setAdapter(adapterEventMonth);
                adapterEventMonth.notifyDataSetChanged();
            }

            listAppointmentbyMonth.setVisibility(View.VISIBLE);
            currentDate.setVisibility(View.VISIBLE);
            todayLayout.setVisibility(View.VISIBLE);
            currentDate.setText(form_new.format(calendar_.getTime()));

        }

        Calendar checkCurrentState = Calendar.getInstance();
        if (calendar_.get(Calendar.MONTH) == checkCurrentState.get(Calendar.MONTH) && calendar_.get(Calendar.YEAR) == checkCurrentState.get(Calendar.YEAR)) {
            selectedDateTime = sdf.format(System.currentTimeMillis());
        } else {
            checkCurrentState = (Calendar) calendar_.clone();
            checkCurrentState.set(Calendar.DAY_OF_MONTH, 1);
            selectedDateTime = sdf.format(checkCurrentState.getTime());
        }

    }

    public void setMonthByPopUp(int month, int year) {

        calendar_.set(Calendar.MONTH, month);
        calendar_.set(Calendar.YEAR, year);
        calendar_.set(Calendar.DAY_OF_MONTH, 5);


        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> AppointmentList_new = null;
        try {
            AppointmentList_new = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) getDatafromDB(getDaysbyMonth(month, calendar_.get(Calendar.YEAR)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (filterFlagNew) {

            adapter.setFlag(filterFlagNew);
            try {

                adapter = new CalMonthAdapter(getActivity(), calendar_, month, appointmentList, getFilteredDatafromDB(getDaysbyMonth(calendar_.get(Calendar.MONTH), calendar_.get(Calendar.YEAR)), id_uk, type));
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {

            adapter = new CalMonthAdapter(getActivity(), calendar_, month, appointmentList, AppointmentList_new);
            gridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

//        updateAppointmentList_New(calendar_);
        SimpleDateFormat form_new = new SimpleDateFormat("MMM yyyy", Locale.US);

        String tt = form_new.format(calendar_.getTime());

        setTitle(form_new.format(calendar_.getTime()));

        Calendar tempCal_new = Calendar.getInstance();

        if (tempCal_new.get(Calendar.MONTH) == calendar_.get(Calendar.MONTH) && tempCal_new.get(Calendar.YEAR) == calendar_.get(Calendar.YEAR))

        {
            if (filterFlagNew) {
                try {
                    adapterEventMonth.updateEvent(getFilteredAppointmentByDay(formatter.format(tempCal_new.getTime()), id_uk, type, true));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    adapterEventMonth = new AdapterEventMonth(getActivity(), getAppointmentByDay(formatter.format(tempCal_new.getTime())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                listAppointmentbyMonth.setAdapter(adapterEventMonth);
                adapterEventMonth.notifyDataSetChanged();
            }

            listAppointmentbyMonth.setVisibility(View.VISIBLE);
            currentDate.setVisibility(View.VISIBLE);
            currentDate.setText(form_new.format(calendar_.getTime()));

        } else

        {
            listAppointmentbyMonth.setVisibility(View.GONE);
            todayLayout.setVisibility(View.VISIBLE);
            currentDate.setVisibility(View.VISIBLE);
            currentDate.setText("");
            day_.setText("");
        }

        Calendar checkCurrentState = Calendar.getInstance();
        if (calendar_.get(Calendar.MONTH) == checkCurrentState.get(Calendar.MONTH) && calendar_.get(Calendar.YEAR) == checkCurrentState.get(Calendar.YEAR)) {
            selectedDateTime = sdf.format(System.currentTimeMillis());
        } else {
            checkCurrentState = (Calendar) calendar_.clone();
            checkCurrentState.set(Calendar.DAY_OF_MONTH, 1);
            selectedDateTime = sdf.format(checkCurrentState.getTime());
        }

    }

    public LinkedHashMap<String, ArrayList<AppointmentMappingModel>> filterAppointmentListInternal() {

        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> tempList = orignalList;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> filteredList = new LinkedHashMap<>();
        ArrayList<AppointmentMappingModel> appointmentList = null;

        ArrayList<String> listdays = orignalDaysList;
        for (int i = 0; i < listdays.size(); i++) {

            appointmentList = new ArrayList<>();

            for (int j = 0; j < tempList.get(listdays.get(i)).size(); j++) {
                if (tempList.get(listdays.get(i)).get(j).getAttendee() != null) {

                    for (int k = 0; k < tempList.get(listdays.get(i)).get(j).getAttendee().size(); k++) {

                        if (tempList.get(listdays.get(i)).get(j).getAttendee().get(k).getId() == id_uk) {
                            appointmentList.add(tempList.get(listdays.get(i)).get(j));
                        }
                    }
                }
            }
            filteredList.put(listdays.get(i), appointmentList);
        }

        return filteredList;

    }

}

