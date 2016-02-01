package com.mycity4kids.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityShowAppointment;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.AdapterExpandableList;
import com.mycity4kids.ui.adapter.AttendeeListAdapter;
import com.mycity4kids.utils.CalenderUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by manish.soni on 09-06-2015.
 */
public class FragmentCalender extends BaseFragment implements View.OnClickListener {

    private final static long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
    AdapterExpandableList listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    LinkedHashMap<String, ArrayList<AppointmentMappingModel>> listDataItems;
    Boolean noMoreAppointment = false;

    private Boolean flag = false;
    Boolean flagScroll = false;
    ImageView addAppointment;
    int yearTemp, monthTemp;

    private View view;
    Activity mActivity;
    RelativeLayout mainLayout, search_bar;
    private EditText search;

    AttendeeListAdapter attendeeAdapter;
    ArrayList<AttendeeModel> attendeeList;
    ListView listAttendee;
    FrameLayout attendeeFrame;
    private boolean isDateAdditionInProgress = false;
    LinkedHashMap<String, ArrayList<AppointmentMappingModel>> originalList;
    private AttendeeModel mSelectedAttendeeForFilter;
    private RelativeLayout rltBottomProgress;
    private boolean mIsDataFetchInProgress;

    ScrollView baseScroll;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_appointmentmain, container, false);

        mainLayout = (RelativeLayout) view.findViewById(R.id.mainlayout);

        rltBottomProgress = (RelativeLayout) view.findViewById(R.id.rltBottomProgress);
        search = (EditText) view.findViewById(R.id.search);
        expListView = (ExpandableListView) view.findViewById(R.id.expanded_list);
        addAppointment = (ImageView) view.findViewById(R.id.add_appointment);
        attendeeFrame = (FrameLayout) view.findViewById(R.id.filter_attendee);
        listAttendee = (ListView) view.findViewById(R.id.attendee_list);
        search_bar = (RelativeLayout) view.findViewById(R.id.search_bar);
        TextView addad = (TextView) view.findViewById(R.id.txtEmptyView);
        FrameLayout fl = (FrameLayout)view.findViewById(R.id.down_drawer);
        addad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        DatabaseUtil.exportDb();

        ((DashboardActivity) getActivity()).filter = false;

        expListView.setEmptyView(view.findViewById(R.id.txtEmptyView));

        listAdapter = new AdapterExpandableList(getActivity());
        expListView.setAdapter(listAdapter);

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

        ((DashboardActivity) getActivity()).refreshMenu();
        SimpleDateFormat form = new SimpleDateFormat("MMM yyyy", Locale.US);
        form.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

        attendeeFrame.setVisibility(View.GONE);

        attendeeAdapter = new AttendeeListAdapter(getActivity(), attendeeList);
        listAttendee.setAdapter(attendeeAdapter);

        Calendar mCalendar = Calendar.getInstance();
        //       to change the starting date
//        mCalendar.set(2010, 1, 4);

        setTitle(form.format(mCalendar.getTime()));

        //new GetData().execute();

        if (AppointmentManager.getInstance(mActivity).getAppointmentMap() != null && !AppointmentManager.getInstance(mActivity).getAppointmentMap().isEmpty()) {
            refreshAppointmentList();
        } else {
            new GetData().execute();
        }


        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
//                if (!cs.toString().trim().equals(search.getText().toString().trim())) {
//                    searchfilter(cs.toString());
//                }
                searchfilter(cs.toString());
                if (cs.toString().trim().equalsIgnoreCase("")) {
                    search_bar.setVisibility(View.GONE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                Calendar calendar = Calendar.getInstance();
                String headerAtPos = (String) listAdapter.getGroup(getFirstVisibleGroup());

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
                DateFormat format1 = new SimpleDateFormat("MMM yyyy", Locale.US);
                format1.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
                Date date = null;
                try {
                    date = format.parse(headerAtPos);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.setTime(date);
                ((DashboardActivity) getActivity()).setTitle(format1.format(calendar.getTime()));

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int mPrevTotalItemCount = 0;
                String searchText = search.getText().toString().trim();
                if (searchText.equals("")) {
                    if (firstVisibleItem == 0) {
                        search_bar.setVisibility(View.GONE);
                        flagScroll = false;
                        //animateExpandableListView(false);
                    } else {

                        search_bar.setVisibility(View.VISIBLE);
                        search.setVisibility(View.VISIBLE);
                        if (flagScroll == false) {
                            //animateexpandablelistview(true);
                        }
                        flagScroll = true;
                    }
                }

//                if (totalItemCount != 0) {
//                    if (view.getAdapter().getCount() < 10) {
//
//                        final Handler handler = new Handler();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e) {
//                                }
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        new AddNewDatesAtBottomOnScroll().execute();
//                                    }
//                                });
//                            }
//                        }).start();
//
//                    }
//                }
                if (view.getAdapter() != null && ((firstVisibleItem + visibleItemCount) >= totalItemCount) && !isDateAdditionInProgress && searchText.equals("") && visibleItemCount != totalItemCount && !noMoreAppointment) {
                    new AddNewDatesAtBottomOnScroll().execute();
                }
            }
        });

        expListView.setGroupIndicator(null);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                AppointmentMappingModel appointmentSelected = (AppointmentMappingModel) listAdapter.getChild(i, i1);

                if (appointmentSelected.getAppointment_name() != null) {
                    Intent intent = new Intent(getActivity(), ActivityShowAppointment.class);
                    if (appointmentSelected.getEventId() == 0) {
                        intent.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, 0);
                        intent.putExtra(AppConstants.EXTERNAL_APPOINTMENT_ID, appointmentSelected.getExternalId());
                    } else {
                        intent.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, appointmentSelected.getEventId());
                        intent.putExtra(AppConstants.EXTERNAL_APPOINTMENT_ID, "");
                    }
                    startActivityForResult(intent, 1);
//                    startActivityForResult(intent, 1);
                    //startActivity(intent);

                }
                return false;
            }
        });

        addAppointment.setOnClickListener(this);

        listAttendee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AttendeeModel attendeeModel = (AttendeeModel) attendeeAdapter.getItem(i);

                if (attendeeModel.getName().equals("All")) {
                    mSelectedAttendeeForFilter = null;
                    ((DashboardActivity) getActivity()).filter = false;
                    try {
                        ArrayList<String> listHeaders = new ArrayList<String>();
                        listHeaders.addAll(originalList.keySet());
                        listAdapter.notifyDataChange((ArrayList<String>) listHeaders, originalList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    search.setText("");

                } else {

                    ((DashboardActivity) getActivity()).filter = true;
                    try {
                        mSelectedAttendeeForFilter = attendeeModel;
                        filterAttendee(attendeeModel);
//                        filterList(attendeeModel.getId(), attendeeModel.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // get colorcode
                    String colorcode = "";

                    if (attendeeModel.getType().equalsIgnoreCase("kid")) {
                        colorcode = new TableKids(BaseApplication.getInstance()).getKids(attendeeModel.getId()).getColor_code();
                    } else {
                        colorcode = new TableAdult(BaseApplication.getInstance()).getAdults(attendeeModel.getId()).getColor_code();
                    }

                    ((DashboardActivity) getActivity()).selected_colorcode = colorcode;
                }
                ((DashboardActivity) getActivity()).refreshMenu();
                attendeeFrame.setVisibility(View.GONE);
            }
        });

//        setCurrentDateTitle();
//
//        for (int i = 0; i < listAdapter.getHeaderList().size(); i++) {
//            expListView.expandGroup(i);
//        }
//        expListView.setSelectedGroup(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mAppointmentUpdatedListener,
                new IntentFilter(AppointmentManager.LOCAL_BROADCAST_APPOINTMENT_UPDATED));
    }

    private BroadcastReceiver mAppointmentUpdatedListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshAppointmentList();
        }
    };

    private void refreshAppointmentList() {

        search_bar.setVisibility(View.GONE);
        try {
            originalList = AppointmentManager.getInstance(mActivity).getAppointmentMap();
            if (originalList == null) {
                new GetData().execute();
            } else {
                listDataItems = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) originalList.clone();
                List<String> listHeaders = new ArrayList<String>();
                listHeaders.addAll(listDataItems.keySet());
                listAdapter.notifyDataChange((ArrayList<String>) listHeaders, listDataItems);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mAppointmentUpdatedListener);
    }

    private void animateExpandableListView(final boolean isExpand) {
        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newTopMargin = 0;
                if (isExpand) {
                    newTopMargin = (int) (40 * getResources().getDisplayMetrics().density);
                }

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) expListView.getLayoutParams();
                params.topMargin = (int) (newTopMargin * interpolatedTime);
                expListView.setLayoutParams(params);
            }
        };
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isExpand) {
                    search_bar.setVisibility(View.VISIBLE);
                    search.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.SlideInRight).duration(400).playOn(search);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        a.setDuration(500); // in ms
        expListView.startAnimation(a);
    }

    public void searchfilter(String query) {
        if (listDataItems == null) {
            return;
        }
        query = query.toLowerCase();
        listDataItems.clear();
        if (query.isEmpty()) {
            if (originalList == null) {
                return;
            }
            listDataItems = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) originalList.clone();
        } else {

            Iterator myVeryOwnIterator = originalList.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {

                ArrayList<AppointmentMappingModel> newList = new ArrayList<>();
                String key = (String) myVeryOwnIterator.next();
                ArrayList<AppointmentMappingModel> tasklist = originalList.get(key);


                for (AppointmentMappingModel modelTasks : tasklist) {
                    if (modelTasks.getAppointment_name().toLowerCase().contains(query))
                        newList.add(modelTasks);

                }
                if (newList != null && newList.size() > 0)
                    listDataItems.put(key, newList);
            }
        }
        ArrayList<String> listHeaders = new ArrayList<String>();
        listHeaders.addAll(listDataItems.keySet());
        listAdapter.notifyDataChange(listHeaders, listDataItems);
//        listAdapter.notifyDataChange((ArrayList<String>) listAdapter.getHeaderList(), listDataItems);
        //listAdapter.notifyDataSetChanged();

    }

    public class AddNewDatesAtBottomOnScroll extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rltBottomProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                isDateAdditionInProgress = true;
                List<String> stringslist = getNextSixMonthDates(listAdapter.getHeaderList(), true);
                LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finalNextData = getDatafromDB(stringslist);
                if (finalNextData != null && finalNextData.size() > 0) {
                    AppointmentManager.getInstance(mActivity).addDatesDate(stringslist);
                    AppointmentManager.getInstance(mActivity).addAppointmentdata(finalNextData);
                    originalList.putAll(finalNextData);
                    listDataItems.putAll(finalNextData);
                    noMoreAppointment = false;
                } else {
                    noMoreAppointment = true;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ArrayList<String> listHeaders = new ArrayList<String>();
            listHeaders.addAll(listDataItems.keySet());
//            originalList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) listDataItems.clone();

            if (mSelectedAttendeeForFilter != null) {
                filterAttendee(mSelectedAttendeeForFilter);
            } else {
                listAdapter.notifyDataChange(listHeaders, listDataItems);
            }
            isDateAdditionInProgress = false;

            rltBottomProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.add_appointment:

                Intent intent = new Intent(getActivity(), ActivityCreateAppointment.class);
                startActivityForResult(intent, 1);
                break;

//            case R.id.search_btn:
//                try {
//                    searchList(String.valueOf(search.getText()));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                break;
        }
    }


    @Override
    protected void updateUi(Response response) {

    }

    public List<String> getDaysForMonths(Calendar mCalendar, Boolean flag) {


        List<String> allDays = new ArrayList<String>();
        Calendar calendar = (Calendar) mCalendar.clone();
//        if (flag == true) {
//            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
//        }
//
//        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2);

        if (flag == true) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 6);

        int daysInMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        daysInMonth = daysInMonth - (mCalendar.get(Calendar.DAY_OF_MONTH) - 1);

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

        for (int i = 0; i < daysInMonth; i++) {
            // Add day to list
            allDays.add(i, mFormat.format(mCalendar.getTime()));
            // Move next day
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return allDays;
    }

//    public List<String> getNewDaysForMonths(Calendar mCalendar, Boolean flag) {
//
//        List<String> allDays = new ArrayList<String>();
//
//        Calendar calendar = (Calendar) mCalendar.clone();
////        if (flag == true) {
////            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
////        }
////        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2);
//
//        if (flag == true) {
//            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
//        }
//
//        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 6);
//
//        int daysInMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        daysInMonth = daysInMonth - (mCalendar.get(Calendar.DAY_OF_MONTH) - 1);
//
//        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
//
//        for (int i = 0; i < daysInMonth; i++) {
//            // Add day to list
//            allDays.add(i, mFormat.format(mCalendar.getTime()));
//            // Move next day
//            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
//        }
//        return allDays;
//    }


    public void setTitle(String title) {

        ((DashboardActivity) getActivity()).setTitle(title);
    }

    public List<String> getNextSixMonthDates(List<String> headerList, boolean isFromNextMonth) throws ParseException {

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        form.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        Date d1 = null;
        Calendar tdy1;

        tdy1 = Calendar.getInstance();
        if (headerList != null && headerList.size() > 0) {
            String lastdate = headerList.get(headerList.size() - 1);
            tdy1.setTime(form.parse(lastdate));
        } else {
            tdy1.setTimeInMillis(System.currentTimeMillis());
        }


        List<String> newDates = CalenderUtils.getSixMonthDatesFromCalender(tdy1, isFromNextMonth);
//        List<String> newDates = getNewDaysForMonths(tdy1, true);


//        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finalNextData = getDatafromDB(newDates);
//        listAdapter.updateDates(newDates, finalNextData);

        return newDates;
    }

    public List<String> getNewDataForUpdate(List<String> headerList) throws ParseException {

        String lastdate;

        lastdate = headerList.get(headerList.size() - 1);

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        form.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        Date d1 = null;
        Calendar tdy1;

        try {
            d1 = (Date) form.parse(lastdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tdy1 = Calendar.getInstance();
        tdy1.setTime(d1);

        List<String> newDates = getDaysForMonths(tdy1, true);


//        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finalNextData = getDatafromDB(newDates);
//
//        listAdapter.updateDates(newDates, finalNextData);

        return newDates;
    }


    public void showTodayIcon(Context ctx) {

//        Boolean flagx = false;
//
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
//
//        String tempdate = mFormat.format(cal.getTime());
//
//        Boolean ifTodayDateExists = false;
//        int datePosition = 0;


//        for (int i = 0; i < listAdapter.getHeaderList().size(); i++) {
//
//            if (tempdate.equals(listAdapter.getHeaderList().get(i))) {
//
//                expListView.setSelectedGroup(i);
//                flagx = true;
//                break;
//            }
//        }
//
//
//        if (flagx == false) {
//
//            new GetData().execute();
//        }

//        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> orignalList2 = AppointmentManager.getInstance(mActivity).getAppointmentMap();
//        ArrayList<String> headerList = new ArrayList<>();
//        for (Map.Entry<String, ArrayList<AppointmentMappingModel>> entry : orignalList2.entrySet()) {
//            headerList.add(entry.getKey());
//        }
//
//        for (int i = 0; i < headerList.size(); i++) {
//            if (headerList.get(i).equalsIgnoreCase(tempdate)) {
//                ifTodayDateExists = true;
//                datePosition = i;
//                break;
//            }
//        }


//        if (ifTodayDateExists) {
//
//            expListView.setSelected(true);
//            expListView.setSelectedGroup(datePosition);
//            search_bar.setVisibility(View.GONE);
//            search.setVisibility(View.GONE);
//            removeProgressDialog();
//
//        } else {
//            originalList = AppointmentManager.getInstance(mActivity).getAppointmentMap();
//            new GetData().execute();
//        }

        originalList = AppointmentManager.getInstance(mActivity).getAppointmentMap();

        try {
            // expListView.setSelection(0);
            expListView.setSelection(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //new GetData().execute();

        // search.setText("");
        Calendar calendar = Calendar.getInstance();

        DateFormat format1 = new SimpleDateFormat("MMM yyyy", Locale.US);
        format1.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        ((DashboardActivity) getActivity()).setTitle(format1.format(calendar.getTime()));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                search_bar.setVisibility(View.GONE);
                search.setVisibility(View.GONE);
            }
        }, 100);

    }

    public void showCalender(Context ctx) {

        ((DashboardActivity) ctx).refreshMenu();

        String title = String.valueOf(((DashboardActivity) getActivity()).getTitleText());
        SimpleDateFormat form = new SimpleDateFormat("MMM yyyy", Locale.US);
//        form.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        Date dateTitle = null;

        try {
            dateTitle = (Date) form.parse(title);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calTitle = Calendar.getInstance();
        calTitle.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        calTitle.setTime(dateTitle);

        Bundle args = new Bundle();
//        args.putInt("month", monthTemp);
//        args.putInt("year", yearTemp);

        args.putInt("month", calTitle.get(Calendar.MONTH));
        args.putInt("year", calTitle.get(Calendar.YEAR));


        if (flag == false) {
            ((DashboardActivity) ctx).replaceFragment(new FragmentCalMonth(), args, true);
        } else {
            ((DashboardActivity) ctx).replaceFragment(new FragmentCalMonth(), args, true);
        }
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

//    public void hidePOPup(Context context) {
//
//        if (popupWindow != null)
//            popupWindow.dismiss();
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }


    public void updateListbyDay(int month, int year) throws ParseException {

        Calendar cloneCal = Calendar.getInstance();

        this.flag = true;
        this.yearTemp = year;
        this.monthTemp = month;

//        for future dates

        if (cloneCal.get(Calendar.YEAR) >= year && cloneCal.get(Calendar.MONTH) < month) {
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);

            int pos;
            pos = mapSelection(mFormat.format(cal.getTime()));
            if (pos == -1) {

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                populateCalendarData(mFormat.format(cal.getTime()));
                cal.set(year, month, 1);
                expListView.setSelectedGroup(mapSelection(mFormat.format(cal.getTime())));

            } else {
                expListView.setSelectedGroup(pos);
            }
        } else {

            //            for back dates

            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            List<String> NewDays = new ArrayList<String>();

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.set(year, month, 1);

            List<String> headers = listAdapter.getHeaderList();
            try {
                if (headers != null && headers.size() > 0) {
                    cal2.setTime(mFormat.parse(listAdapter.getHeaderList().get(0)));
                } else {
                    cal2.setTimeInMillis(System.currentTimeMillis());
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            long limit = calculateBTWNDays(cal1, cal2);
            NewDays = getListBTWDays(cal1, (int) limit);

            int Counter = 0;
            Boolean tempFlag = false;

            Calendar calTemp = Calendar.getInstance();
            calTemp.set(Calendar.MONTH, month);
            calTemp.set(Calendar.YEAR, year);
            calTemp.set(Calendar.DAY_OF_MONTH, 1);

            String tempdate = mFormat.format(calTemp.getTime());
            String[] tempDateArray = tempdate.split("-");
            String tempdateYearMonthString = tempDateArray[0] + "-" + tempDateArray[1];

            if (headers != null && headers.size() > 0) {
                for (int i = 0; i < headers.size(); i++) {
                    if (listAdapter.getHeaderList().get(i).contains(tempdateYearMonthString)) {
                        expListView.setSelectedGroup(Counter);
                        tempFlag = true;
                        break;
                    }
                    Counter = Counter + 1;
                }
            }
            if (!tempFlag) {

                new JumpToMonth().execute(NewDays);

//                listAdapter.updateDatesPrevious(NewDays, getDatafromDB(NewDays));
                expListView.setSelectedGroup(0);
            }
        }
    }

    public int mapSelection(String date) {
        String[] dateFormt = date.split("-");
        List<String> dateList = listAdapter.getHeaderList();
        int count = 0;
        Boolean flag = false;
        for (int i = 0; i < dateList.size(); i++) {
//            if (date.equals(dateList.get(i))) {
            if (dateList.get(i).startsWith(dateFormt[0] + "-" + dateFormt[1])) {
                flag = true;
                break;
            }
            count = count + 1;
        }
        if (flag == false) {
            count = -1;
        }
        return count;
    }

    public void populateCalendarData(String date) throws ParseException {

        List<String> dateData = new ArrayList<>();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String requiredLastDate = date;

        Calendar calFirst = Calendar.getInstance();
        Calendar calLast = Calendar.getInstance();

        List<String> headerList = listAdapter.getHeaderList();
        if (headerList != null && headerList.size() > 0) {
            int position = listAdapter.getHeaderList().size() - 1;
            String existingCalLastDate = listAdapter.getHeaderList().get(position);
            calFirst.setTime(mFormat.parse(existingCalLastDate));
        } else {
            calFirst.setTimeInMillis(System.currentTimeMillis());
        }

        try {

            calLast.setTime(mFormat.parse(requiredLastDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calFirst.add(Calendar.DAY_OF_MONTH, +1);

        int tempcount = (int) calculateBTWNDays(calFirst, calLast);

        int diff = (int) (calLast.getTimeInMillis() - calFirst.getTimeInMillis());
        int dateCount = diff / (24 * 60 * 60 * 1000);

        for (int i = 0; i < tempcount; i++) {

            dateData.add(i, mFormat.format(calFirst.getTime()));
            calFirst.add(Calendar.DAY_OF_MONTH, 1);

        }

        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finalNextData = getDatafromDB(dateData);

        listDataItems.putAll(finalNextData);
        ArrayList<String> listHeaders = new ArrayList<String>();
        listHeaders.addAll(listDataItems.keySet());
        originalList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) listDataItems.clone();
        listAdapter.notifyDataChange(listHeaders, listDataItems);
//        listAdapter.updateDates(dateData, getDatafromDB(dateData));

    }


    public long calculateBTWNDays(Calendar a, Calendar b) {

        // Optional: avoid cloning objects if it is the same day
        if (a.get(Calendar.ERA) == b.get(Calendar.ERA)
                && a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)) {
            return 0;
        }
        Calendar a2 = (Calendar) a.clone();
        Calendar b2 = (Calendar) b.clone();
        a2.set(Calendar.HOUR_OF_DAY, 0);
        a2.set(Calendar.MINUTE, 0);
        a2.set(Calendar.SECOND, 0);
        a2.set(Calendar.MILLISECOND, 0);
        b2.set(Calendar.HOUR_OF_DAY, 0);
        b2.set(Calendar.MINUTE, 0);
        b2.set(Calendar.SECOND, 0);
        b2.set(Calendar.MILLISECOND, 0);
        long diff = a2.getTimeInMillis() - b2.getTimeInMillis();
        long days = diff / MILLISECS_PER_DAY;
        return Math.abs(days);
    }

    public List<String> getListBTWDays(Calendar a, int limit) {

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String temp;
        List<String> allDays = new ArrayList<String>();

        for (int i = 0; i <= limit - 1; i++) {

            temp = mFormat.format(a.getTime());
            a.add(Calendar.DAY_OF_MONTH, 1);
            allDays.add(i, temp);
        }
        return allDays;
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

        mIsDataFetchInProgress = true;

        ArrayList<AppointmentMappingModel> appointmentModels;
        ArrayList<AppointmentMappingModel> tempAppointmentModels;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finaldata = new LinkedHashMap<String, ArrayList<AppointmentMappingModel>>();
        List<String> dateList = allDays;

        String firstDateString;
        String lastDateString;

        firstDateString = dateList.get(0);
        lastDateString = dateList.get(dateList.size() - 1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

//        formatter.setTimeZone(TimeZone.getDefault());

        Date firstDate = formatter.parse(firstDateString);
        Date lastDate = formatter.parse(lastDateString);

        java.sql.Timestamp firsttamp = new java.sql.Timestamp(firstDate.getTime());
        java.sql.Timestamp laststamp = new java.sql.Timestamp(lastDate.getTime());

        long first = firsttamp.getTime();
        long last = laststamp.getTime() + 86280000;

        TableAppointmentData tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
        appointmentModels = tableAppointment.allDataBTWNdays(first, last);

        for (int i = 0; i < allDays.size(); i++) {

            try {
                tempAppointmentModels = new ArrayList<>();

                for (int j = 0; j < appointmentModels.size(); j++) {
                    String isRecurring = appointmentModels.get(j).getIs_recurring();

                    if (checkCurrentDateValid(allDays.get(i), appointmentModels.get(j).getStarttime())) {

                        java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(allDays.get(i)).getTime());
                        if (appointmentModels.get(j).getStarttime() >= tempTimestamp.getTime() && appointmentModels.get(j).getStarttime() <= (tempTimestamp.getTime() + 86280000)) {
                            tempAppointmentModels.add(appointmentModels.get(j));

                        } else {
                            // condition until check pick date

                            if (!StringUtils.isNullOrEmpty(appointmentModels.get(j).getRepeate_untill())) {
                                if (appointmentModels.get(j).getRepeate_untill().equalsIgnoreCase("forever")) {
                                    if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {
                                        // no repeat means its non recurring, break loop
//                                        break;
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
                                            // no repeat means its non recurring, break loop
//                                            break;
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
                                    if (calendarCurrent.getTimeInMillis() == calendarUntil.getTimeInMillis()) {
                                        // if we reached the end date of recurring event, then break the loop
//                                        break;
                                    }
                                }
                            } else {
                                // if its non recurring, break the loop
//                                break;
                            }

                        }
                    }
                }
                if (tempAppointmentModels.size() > 0) {
                    // do sortting here
                    if (tempAppointmentModels.size() > 1)
                        tempAppointmentModels = getSorted(tempAppointmentModels);
                    finaldata.put(allDays.get(i), tempAppointmentModels);
                }
//                finaldata.put(allDays.get(i), tempAppointmentModels);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        mIsDataFetchInProgress = false;
        return finaldata;
    }

    public long convertTimeStamp(CharSequence date, CharSequence time) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mma");

        String temp = date + " " + time;
        Date tempDate = formatter.parse(temp);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("h:mma");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toUpperCase();
        } catch (Exception ex) {
            return "xx";
        }
    }

    public ArrayList<AppointmentMappingModel> getSorted(ArrayList<AppointmentMappingModel> dataList) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(calendar.getTime());
        for (int i = 0; i < dataList.size(); i++) {
            try {
                dataList.get(i).setTemptime(convertTimeStamp(date, getTime(dataList.get(i).getStarttime())));
            } catch (Exception e) {
                e.getMessage();
            }

        }

        // now sorted by timeastamp
        AppointmentMappingModel swapModel;
        for (int i = 0; i < dataList.size(); i++) {
            for (int j = i + 1; j < dataList.size(); j++) {
                if (dataList.get(i).getTemptime() > dataList.get(j).getTemptime()) {
                    swapModel = dataList.get(i);
                    dataList.set(i, dataList.get(j));
                    dataList.set(j, swapModel);
                }

            }
        }

        return dataList;

    }

    public boolean chkDays(String appointmentday, String cureentdate) throws ParseException {
        boolean result = false;

        appointmentday = appointmentday.replace(" ", "");

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(date);

        if (dayOfTheWeek.trim().equalsIgnoreCase(appointmentday))
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
                calendar.add(Calendar.DAY_OF_MONTH, count);
            }

        }


        return result;
    }

    public void refreshView() {
        refreshAppointmentList();
//        try {
//            listDataItems = getDatafromDB(listDataHeader);
//            List<String> listHeaders = new ArrayList<String>();
//            listHeaders.addAll(listDataItems.keySet());
////            originalList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) listDataItems.clone();
//            listAdapter.notifyDataChange((ArrayList<String>) listHeaders, listDataItems);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private void filterAttendee(AttendeeModel attendeeModel) {
//        query = query.toLowerCase();
        listDataItems.clear();
//        if (query.isEmpty()) {
//            listDataItems.putAll(originalList);
//        } else {

        Iterator myVeryOwnIterator = originalList.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {

            ArrayList<AppointmentMappingModel> newList = new ArrayList<>();
            String key = (String) myVeryOwnIterator.next();
            ArrayList<AppointmentMappingModel> tasklist = originalList.get(key);


            for (AppointmentMappingModel modelTasks : tasklist) {
                if (modelTasks.getAttendee().contains(attendeeModel))
                    newList.add(modelTasks);
            }
            if (newList != null && newList.size() > 0)
                listDataItems.put(key, newList);
        }
//        }
        ArrayList<String> listHeaders = new ArrayList<String>();
        listHeaders.addAll(listDataItems.keySet());
        listAdapter.notifyDataChange(listHeaders, listDataItems);
    }

    public int getFirstVisibleGroup() {
        int firstVis = expListView.getFirstVisiblePosition();
        long packedPosition = expListView.getExpandableListPosition(firstVis);
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        return groupPosition;
    }


    public void setTodayInstance() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String tempdate = mFormat.format(cal.getTime());

        for (int i = 0; i < listAdapter.getHeaderList().size(); i++) {

            if (tempdate.equals(listAdapter.getHeaderList().get(i))) {
                expListView.setSelectedGroup(i);
                break;
            }
        }
    }


    public void setCurrentDateTitle() {

        Calendar calendar = Calendar.getInstance();
        DateFormat format1 = new SimpleDateFormat("MMM yyyy", Locale.US);
        if (listAdapter.getHeaderList().isEmpty()) {
            if ((DashboardActivity) getActivity() != null)
                ((DashboardActivity) getActivity()).setTitle(format1.format(calendar.getTime()));
        } else {
            String headerAtPos = (String) listAdapter.getHeaderList().get(0);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            Date date = null;
            try {
                date = format.parse(headerAtPos);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(date);
            calendar.setTime(date);
            if ((DashboardActivity) getActivity() != null)
                ((DashboardActivity) getActivity()).setTitle(format1.format(calendar.getTime()));

        }


    }

    // background tasks from db

    // background tasks


    public class GetData extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Please wait..");
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                Calendar mCalendar = Calendar.getInstance();
//                listDataHeader = getDaysForMonths(mCalendar, false);
//                listDataHeader = getNextSixMonthDates(null, false);
                List<String> allDays = getNextSixMonthDates(null, false);
                AppointmentManager.getInstance(mActivity).addDatesDate(allDays);
                try {
                    listDataItems = getDatafromDB(allDays);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);


            originalList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) listDataItems.clone();
            AppointmentManager.getInstance(mActivity).setAppointmentMap(originalList);
            List<String> listHeaders = new ArrayList<String>();
            listHeaders.addAll(listDataItems.keySet());
            listAdapter.notifyDataChange((ArrayList<String>) listHeaders, listDataItems);
            setCurrentDateTitle();

            for (int i = 0; i < listAdapter.getHeaderList().size(); i++) {
                expListView.expandGroup(i);
            }

            expListView.setSelectedGroup(0);
            search_bar.setVisibility(View.GONE);
            search.setVisibility(View.GONE);

            removeProgressDialog();

            // add more dates
            //   expListView.scrollTo(20,10);
            // expListView.scrollTo(0,0);

            // expListView.smoothScrollToPosition(3);

        }
    }

    // get scrollview background

    public class JumpToMonth extends AsyncTask<List<String>, String, String> {

        List<String> NewDays;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> dataList;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            showProgressDialog("Please wait..");
        }

        @Override
        protected String doInBackground(List<String>... params) {
            // TODO Auto-generated method stub

            NewDays = params[0];
            try {
                dataList = getDatafromDB(NewDays);
                dataList.putAll(listDataItems);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            removeProgressDialog();

            listDataItems = dataList;
            ArrayList<String> listHeaders = new ArrayList<String>();
            listHeaders.addAll(listDataItems.keySet());
            originalList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) listDataItems.clone();
            listAdapter.notifyDataChange(listHeaders, listDataItems);

//            listAdapter.updateDatesPrevious(NewDays, dataList);

        }

    }

    private void sendScroll() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        search_bar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }
}
//    public void filterList(int id, String type ) throws ParseException {
//
//        List<String> dateHeadet = (ArrayList<String>) listAdapter.getHeaderList();
//        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finaldata = new LinkedHashMap<String, ArrayList<AppointmentMappingModel>>();
//
//        TableAppointmentData tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
//
//        ArrayList<AppointmentMappingModel> appointmentModels;
//        ArrayList<AppointmentMappingModel> tempAppointmentModels;
//
//        String firstDateString;
//        String lastDateString;
//
//        firstDateString = dateHeadet.get(0);
//        lastDateString = dateHeadet.get(dateHeadet.size() - 1);
//
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        Date firstDate = formatter.parse(firstDateString);
//        Date lastDate = formatter.parse(lastDateString);
//
//        java.sql.Timestamp firsttamp = new java.sql.Timestamp(firstDate.getTime());
//        java.sql.Timestamp laststamp = new java.sql.Timestamp(lastDate.getTime());
//
//        appointmentModels = tableAppointment.filterByName(id,type, firsttamp.getTime(), laststamp.getTime() + 86399999);
//
//        if (appointmentModels.size() == 0) {
//
//            ToastUtils.showToast(getActivity(), "Invalid text");
//
//        } else {
//            ArrayList<AttendeeModel> nullList = new ArrayList<AttendeeModel>();
//
//            for (int i = 0; i < dateHeadet.size(); i++) {
//
//                tempAppointmentModels = new ArrayList<>();
//
//                for (int j = 0; j < appointmentModels.size(); j++) {
//                    java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(dateHeadet.get(i)).getTime());
//                    if (appointmentModels.get(j).getStarttime() >= tempTimestamp.getTime() && appointmentModels.get(j).getStarttime() <= (tempTimestamp.getTime() + 86399999)) {
//                        tempAppointmentModels.add(appointmentModels.get(j));
//                    }
//                }
//                if (tempAppointmentModels.size() == 0) {
//                    tempAppointmentModels.add(new AppointmentMappingModel(0, null, null));
//                }
//                finaldata.put(dateHeadet.get(i), tempAppointmentModels);
//            }
//
//            listAdapter.notifyDataChange((ArrayList<String>) listAdapter.getHeaderList(), finaldata);
//        }
//