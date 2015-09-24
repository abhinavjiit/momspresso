package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.newmodels.TaskMappingModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityShowTask;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.AdapterTaskList;

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
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by manish.soni on 08-07-2015.
 */
public class FragmentTaskHome extends BaseFragment implements View.OnClickListener {

    private View view;
    EditText searchName;
    //private ImageView searchBtn;
    ExpandableListView tastMainList;
    AdapterTaskList adapterTaskList;
    List<String> headerList;
    private LinkedHashMap<String, ArrayList<TaskMappingModel>> childList;
    ArrayList<TaskMappingModel> tempData1;
    ArrayList<TaskMappingModel> tempData2;
    ArrayList<TaskMappingModel> tempData3;
    ArrayList<TaskMappingModel> new_tempData1;
    ArrayList<TaskMappingModel> new_tempData2;
    ArrayList<TaskMappingModel> new_tempData3;
    TableTaskData tableTaskData;
    Boolean flag_id_all = false;
    int listId = 0;
    ArrayList<TaskMappingModel> finalTaskDataModelList;
    private LinkedHashMap<String, ArrayList<TaskMappingModel>> originalList;
    EditText search_bar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.aa_task_home, container, false);

        tastMainList = (ExpandableListView) view.findViewById(R.id.task_main_list);
        //searchBtn = (ImageView) view.findViewById(R.id.search_btn);
        search_bar = (EditText) view.findViewById(R.id.search);
        searchName = (EditText) view.findViewById(R.id.search);
        // searchBtn.setOnClickListener(this);
        view.findViewById(R.id.add_task).setOnClickListener(this);

        headerList = new ArrayList<>();

        tastMainList.setEmptyView(view.findViewById(R.id.txtEmptyView));

        // update list in dashboard activity
        ((DashboardActivity) getActivity()).notiftTaskList();


        new FetchTaskFromDBAsync().execute();

        tastMainList.setGroupIndicator(null);
        tastMainList.setOnGroupClickListener(null);

        tastMainList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                if (((TaskMappingModel) adapterTaskList.getChild(i, i1)).getTaskName() == null) {

                } else {

                    ((DashboardActivity) getActivity()).UploadCompleteTasks();
                    Intent intent = new Intent(getActivity(), ActivityShowTask.class);
                    intent.putExtra(AppConstants.EXTRA_TASK_ID, ((TaskMappingModel) adapterTaskList.getChild(i, i1)).getTask_id());
                    startActivity(intent);
                }

                return false;
            }
        });


        tastMainList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                String searchText = searchName.getText().toString().trim();
                if (searchText.equals("")) {
                    if (firstVisibleItem == 0) {
                        search_bar.setVisibility(View.GONE);
                        //searchName.setVisibility(View.GONE);
                    } else {
                        search_bar.setVisibility(View.VISIBLE);
                        //searchName.setVisibility(View.VISIBLE);

                    }
                }
            }

        });

        searchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text

                filter(cs.toString());
                if (cs.toString().trim().equalsIgnoreCase("")) {
                    search_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // call service here for completed tasks
        ((DashboardActivity) getActivity()).UploadCompleteTasks();

    }

    public void filter(String query) {

        query = query.toLowerCase();

        if (query.isEmpty()) {
            //childList.clear();
            childList.putAll(originalList);
        } else {

            Iterator myVeryOwnIterator = originalList.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {

                ArrayList<TaskMappingModel> newList = new ArrayList<>();
                String key = (String) myVeryOwnIterator.next();
                ArrayList<TaskMappingModel> tasklist = originalList.get(key);


                for (TaskMappingModel modelTasks : tasklist) {

                    if (!StringUtils.isNullOrEmpty(modelTasks.getTaskName())) {
                        if (modelTasks.getTaskName().toLowerCase().contains(query))

                            newList.add(modelTasks);
                    }
                }
                childList.put(key, newList);
            }
        }

        adapterTaskList = new AdapterTaskList(getActivity(), headerList, childList);
        tastMainList.setAdapter(adapterTaskList);
        adapterTaskList.notifyDataSetChanged();
        //mContext.notifyList();

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.add_task:

                Intent intent = new Intent(getActivity(), ActivityCreateTask.class);
                getActivity().startActivityForResult(intent, Constants.CREATE_TASK);

                break;

            case R.id.search_btn:

//                if (searchName.getText().equals("")) {
//                    ToastUtils.showToast(getActivity(), "Enter name to search");
//                } else {
//                    NotifyTaskByListId_Search(String.valueOf(searchName.getText()));
//                }

                break;
        }


    }


    private String getDate(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }


    public boolean chkIfExists(TaskMappingModel daylist, ArrayList<TaskMappingModel> overdue) {
        boolean result = false;

        for (int i = 0; i < overdue.size(); i++) {
            if (overdue.get(i).getTask_id() == daylist.getTask_id()) {
                if (overdue.get(i).getShowDate().equalsIgnoreCase(daylist.getShowDate())) {
                    result = true;
                }
            }

        }


        return result;

    }

    public ArrayList<TaskMappingModel> getOverdueList(ArrayList<TaskMappingModel> todaylist, ArrayList<TaskMappingModel> overdueList) {

        // ArrayList<TaskMappingModel> newList = new ArrayList<>();
        ArrayList<TaskMappingModel> newTodayList = new ArrayList<>();

        //   TaskCompletedTable completedTable = new TaskCompletedTable(BaseApplication.getInstance());

//        for (int i = 0; i < overdueList.size(); i++) {
//            // check completed dates
//
//            ArrayList<String> completedDatesList = completedTable.getCompletedDatesById(overdueList.get(i).getTask_id());
//
//            if (!completedDatesList.contains(getDate(overdueList.get(i).getTaskDate()))) {
//                newList.add(overdueList.get(i));
//            }
//
//
//        }
        ///  today date logic
        for (int i = 0; i < todaylist.size(); i++) {

            if (checkTodayDate(todaylist.get(i).getShowDate())) // day is today
            {
                /// now check time
                if (checkTime(todaylist.get(i).getTaskDate())) {
                    // add to overdue
                    if (!chkIfExists(todaylist.get(i), overdueList))
                        overdueList.add(todaylist.get(i));
                } else {
                    newTodayList.add(todaylist.get(i));
                }
            } else {
                newTodayList.add(todaylist.get(i));
            }
        }

        new_tempData2 = newTodayList;
        new_tempData1 = overdueList;
        return new_tempData1;
    }


    public long convertToTimeStamp(CharSequence date) {

        if (!StringUtils.isNullOrEmpty(date.toString())) {
            try {
                final Calendar c = Calendar.getInstance();
                int curent_year = c.get(Calendar.YEAR);
                int current_month = c.get(Calendar.MONTH);
                int current_day = c.get(Calendar.DAY_OF_MONTH);


                date = curent_year + "-" + (current_month + 1) + "-" + current_day + " " + date;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

                Date tempDate = formatter.parse((String) date);
                java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());
                Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

                long time_stamp = timestamp.getTime();
                return time_stamp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }


    public boolean checkTime(long timeStampStr) {
        boolean result = false;
        try {
            DateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date netDate = (new Date(timeStampStr));
            String time = sdf.format(netDate);

            long currenttimeSatmp = System.currentTimeMillis();
            long showtimestamp = convertToTimeStamp(time);

            if (showtimestamp > currenttimeSatmp) {
                //time has not elapsed
                result = false;
            } else {
                // time elapsed
                // add in overdue
                result = true;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public boolean checkTodayDate(String showDate) {

        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = df.format(c.getTime());

            Date dateobj = (Date) df.parse(showDate);
            String startDate = df.format(dateobj);

            if (startDate.equalsIgnoreCase(currentDate))
                return true;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return false;
    }


    public LinkedHashMap<String, ArrayList<TaskMappingModel>> getAllTASK(Boolean isListSelected, int id) {

        tableTaskData = new TableTaskData(BaseApplication.getInstance());

        Calendar baseCalendar = Calendar.getInstance();

        Calendar dueThisWeek = Calendar.getInstance();
        Calendar backDate = (Calendar) baseCalendar.clone();
        Calendar dueIn30 = (Calendar) baseCalendar.clone();
        Calendar start8 = (Calendar) baseCalendar.clone();

        Calendar dueThisSunday = Calendar.getInstance();
        // add only those days before calender week
        int count = 0;
        while (dueThisSunday.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            count++;
            dueThisSunday.add(Calendar.DAY_OF_WEEK, 1);
        }


        long minimumTime = tableTaskData.getMininumTimeStamp();

        System.out.println("time " + minimumTime);


        dueThisWeek.add(Calendar.DAY_OF_MONTH, count);
        backDate.add(Calendar.DAY_OF_MONTH, -1);
        start8.add(Calendar.DAY_OF_MONTH, count + 1);
//        dueIn30.add(Calendar.DAY_OF_MONTH, 30);
        dueIn30.add(Calendar.DAY_OF_MONTH, start8.getActualMaximum(Calendar.DAY_OF_MONTH));


        long currentTS = 0, backDateTS = 0, due7TS = 0, due8TS = 0, due30TS = 0;

        try {
//            currentTS = (convertTimeStamp(String.valueOf(String.valueOf(baseCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((baseCalendar.get(Calendar.MONTH) + 1)) + " " + String.valueOf(baseCalendar.get(Calendar.YEAR)) + " 12:01 AM")));
//            backDateTS = (convertTimeStamp(String.valueOf(backDate.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((backDate.get(Calendar.MONTH) + 1)) + " " + String.valueOf(backDate.get(Calendar.YEAR)) + " 11:59 PM"));

            currentTS = System.currentTimeMillis() + 1000;
            backDateTS = System.currentTimeMillis() - 1000;

            due7TS = (convertTimeStamp(String.valueOf(dueThisWeek.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((dueThisWeek.get(Calendar.MONTH) + 1)) + " " + String.valueOf(dueThisWeek.get(Calendar.YEAR)) + " 11:59 PM"));

            due8TS = (convertTimeStamp(String.valueOf(start8.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((start8.get(Calendar.MONTH)) + 1) + " " + String.valueOf(start8.get(Calendar.YEAR)) + " 12:01 AM"));

            due30TS = (convertTimeStamp(String.valueOf(dueIn30.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((dueIn30.get(Calendar.MONTH) + 1)) + " " + String.valueOf(dueIn30.get(Calendar.YEAR)) + " 11:59 PM"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        childList = new LinkedHashMap<>();
        tempData1 = new ArrayList<>();
        tempData2 = new ArrayList<>();
        tempData3 = new ArrayList<>();

        tempData1 = tableTaskData.getBackDaysData(backDateTS, isListSelected, id, SharedPrefUtils.getUserDetailModel(getActivity()).getId());
        tempData2 = tableTaskData.allDataBTWNdays(currentTS, due7TS, isListSelected, id, SharedPrefUtils.getUserDetailModel(getActivity()).getId());
        tempData3 = tableTaskData.allDataBTWNdays(due8TS, due30TS, isListSelected, id, SharedPrefUtils.getUserDetailModel(getActivity()).getId());


        new_tempData1 = new ArrayList<>();
        new_tempData2 = new ArrayList<>();
        new_tempData3 = new ArrayList<>();
        headerList = new ArrayList<>();

// getoverdue recurring list

        try {

            Calendar minTime = Calendar.getInstance();
            Date netDate = (new Date(minimumTime));
            minTime.setTime(netDate);

            Calendar basecal = Calendar.getInstance();
            basecal.add(Calendar.DAY_OF_MONTH, -1);

            if (tempData1.size() > 0) {
                new_tempData1 = (ArrayList<TaskMappingModel>) getDaysRecurring(minTime, basecal, tempData1);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        new_tempData2 = (ArrayList<TaskMappingModel>) getDaysRecurring(baseCalendar, dueThisWeek, tempData2);
        new_tempData3 = (ArrayList<TaskMappingModel>) getDaysRecurring(baseCalendar, dueIn30, tempData3);

        new_tempData1 = getOverdueList(new_tempData2, new_tempData1);

        if (new_tempData1.size() > 0) {
            headerList.add("OVERDUE ITEMS");
            childList.put("OVERDUE ITEMS", new_tempData1);
        }


        if (new_tempData2.size() > 0) {
            headerList.add("DUE THIS WEEK");
            childList.put("DUE THIS WEEK", new_tempData2);
        }


        if (new_tempData3.size() > 0) {
            headerList.add("DUE IN 30 DAYS");
            childList.put("DUE IN 30 DAYS", new_tempData3);
        }


        originalList = (LinkedHashMap<String, ArrayList<TaskMappingModel>>) childList.clone();

        return childList;
    }

    public ArrayList<TaskMappingModel> getDaysRecurring(Calendar baseCalendar, Calendar dueCalender, ArrayList<TaskMappingModel> datalist) {

        ArrayList<TaskMappingModel> recurringList = null;
        // set values according to recurring
        ArrayList<String> allDays = new ArrayList<>();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        int countNew = (int) calculateBTWNDays(baseCalendar, dueCalender);

        // if (countNew != 1)
        countNew++;

        for (int i = 0; i < countNew; i++) {
            // Add day to list
            allDays.add(i, mFormat.format(baseCalendar.getTime()));
            // Move next day
            baseCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        try {
            recurringList = getDatafromDB(allDays, datalist);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return recurringList;

    }


//    public LinkedHashMap<String, ArrayList<TaskMappingModel>> getAllTASK_Search(Boolean flag, int id, String name) {
//
//        tableTaskData = new TableTaskData(BaseApplication.getInstance());
//
//        Calendar baseCalendar = Calendar.getInstance();
//
//        Calendar dueThisWeek = Calendar.getInstance();
//        Calendar backDate = (Calendar) baseCalendar.clone();
//        Calendar dueIn30 = (Calendar) baseCalendar.clone();
//        Calendar start8 = (Calendar) baseCalendar.clone();
//
//        Calendar dueThisSunday = Calendar.getInstance();
//        // add only those days before calender week
//        int count = 0;
//        while (dueThisSunday.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
//            count++;
//            dueThisSunday.add(Calendar.DAY_OF_WEEK, 1);
//        }
//
//        dueThisWeek.add(Calendar.DAY_OF_MONTH, count);
//        backDate.add(Calendar.DAY_OF_MONTH, -1);
//        start8.add(Calendar.DAY_OF_MONTH, count + 1);
////        dueIn30.add(Calendar.DAY_OF_MONTH, 30);
//        dueIn30.add(Calendar.DAY_OF_MONTH, start8.getActualMaximum(Calendar.DAY_OF_MONTH));
//
//
////        dueThisWeek.add(Calendar.DAY_OF_MONTH, 7);
////        backDate.add(Calendar.DAY_OF_MONTH, -1);
////        start8.add(Calendar.DAY_OF_MONTH, 8);
////        dueIn30.add(Calendar.DAY_OF_MONTH, 30);
//
//        long currentTS = 0, backDateTS = 0, due7TS = 0, due8TS = 0, due30TS = 0;
//
//        try {
//            currentTS = (convertTimeStamp(String.valueOf(String.valueOf(baseCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((baseCalendar.get(Calendar.MONTH) + 1)) + " " + String.valueOf(baseCalendar.get(Calendar.YEAR)) + " 12:01 AM")));
//
//            backDateTS = (convertTimeStamp(String.valueOf(backDate.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((backDate.get(Calendar.MONTH) + 1)) + " " + String.valueOf(backDate.get(Calendar.YEAR)) + " 11:59 PM"));
//
//            due7TS = (convertTimeStamp(String.valueOf(dueThisWeek.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((dueThisWeek.get(Calendar.MONTH) + 1)) + " " + String.valueOf(dueThisWeek.get(Calendar.YEAR)) + " 11:59 PM"));
//
//            due8TS = (convertTimeStamp(String.valueOf(start8.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((start8.get(Calendar.MONTH)) + 1) + " " + String.valueOf(start8.get(Calendar.YEAR)) + " 12:01 AM"));
//
//            due30TS = (convertTimeStamp(String.valueOf(dueIn30.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((dueIn30.get(Calendar.MONTH) + 1)) + " " + String.valueOf(dueIn30.get(Calendar.YEAR)) + " 11:59 PM"));
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        childList = new LinkedHashMap<>();
//        tempData1 = new ArrayList<>();
//        tempData2 = new ArrayList<>();
//        tempData3 = new ArrayList<>();
//
//        if (flag) {
//
//            tempData1 = tableTaskData.getBackDaysData_Search(backDateTS, true, id, name);
//            tempData2 = tableTaskData.allDataBTWNdays_Search(currentTS, due7TS, true, id, name);
//            tempData3 = tableTaskData.allDataBTWNdays_Search(due8TS, due30TS, true, id, name);
//
//        } else {
//
//            tempData1 = tableTaskData.getBackDaysData_Search(backDateTS, false, 0, name);
//            tempData2 = tableTaskData.allDataBTWNdays_Search(currentTS, due7TS, false, 0, name);
//            tempData3 = tableTaskData.allDataBTWNdays_Search(due8TS, due30TS, false, 0, name);
//        }
//
//        if (tempData1.size() == 0) {
//            tempData1.add(new TaskMappingModel(null, 0, null));
//        }
//        if (tempData2.size() == 0) {
//            tempData2.add(new TaskMappingModel(null, 0, null));
//        }
//        if (tempData3.size() == 0) {
//            tempData3.add(new TaskMappingModel(null, 0, null));
//        }
//
//        childList.put("OVERDUE ITEMS", tempData1);
//        childList.put("DUE THIS WEEK", tempData2);
//        childList.put("DUE IN 30 DAYS", tempData3);
//
//        return childList;
//    }

    public long convertTimeStamp(CharSequence date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy hh:mm a");


        Date tempDate = formatter.parse((String) date);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

//        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

    public void refreshTaskList() {

        headerList = new ArrayList<>();
        headerList.add("OVERDUE ITEMS");
        headerList.add("DUE THIS WEEK");
        headerList.add("DUE IN 30 DAYS");

        adapterTaskList.notifyDataChange((ArrayList<String>) headerList, getAllTASK(false, 0));

    }

//    public void NotifyTaskByListId_Search(String name) {
//
//        headerList = new ArrayList<>();
//        headerList.add("OVERDUE ITEMS");
//        headerList.add("DUE THIS WEEK");
//        headerList.add("DUE IN 30 DAYS");
//
//        if (flag_id_all) {
//            adapterTaskList.notifyDataChange((ArrayList<String>) headerList, getAllTASK_Search(flag_id_all, listId, name));
//        } else {
//            adapterTaskList.notifyDataChange((ArrayList<String>) headerList, getAllTASK_Search(flag_id_all, listId, name));
//        }
//
//    }

    public void NotifyTaskByListId(Boolean flag, int id) {
        if (adapterTaskList == null) {
            return;
        }
        flag_id_all = flag;
        listId = id;


        if (flag) {
            adapterTaskList.notifyDataChange((ArrayList<String>) headerList, getAllTASK(true, id));
        } else {
            adapterTaskList.notifyDataChange((ArrayList<String>) headerList, getAllTASK(false, 0));
        }

        searchName.setText("");
    }


    // recurring methods

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
                calendar.add(Calendar.DAY_OF_MONTH, count);
            }

        }

        return result;
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

    public ArrayList<TaskMappingModel> getDatafromDB(List<String> allDays, ArrayList<TaskMappingModel> taskModels) throws ParseException {

        ArrayList<TaskMappingModel> finalTaskDataModel;

        finalTaskDataModelList = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        TaskCompletedTable completedTable = new TaskCompletedTable(BaseApplication.getInstance());

        for (int i = 0; i < allDays.size(); i++) {

            try {

                finalTaskDataModel = new ArrayList<>();

                for (int j = 0; j < taskModels.size(); j++) {
                    java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(allDays.get(i)).getTime());
                    TaskMappingModel model = (TaskMappingModel) taskModels.get(j).clone();
                    // validation of start date

                    if (checkCurrentDateValid(allDays.get(i), model.getTaskDate())) {


                        // check completed dates
                        ArrayList<String> completedDatesList = completedTable.getCompletedDatesById(taskModels.get(j).getTask_id());

                        if (!completedDatesList.contains(allDays.get(i))) {

                            if (model.getTaskDate() >= tempTimestamp.getTime() && model.getTaskDate() <= (tempTimestamp.getTime() + 86280000)) {
                                model.setShowDate(allDays.get(i));
                                finalTaskDataModel.add(model);

                            } else {
                                // condition until check pick date

                                if (!StringUtils.isNullOrEmpty(model.getRepeate_untill())) {
                                    if (model.getRepeate_untill().equalsIgnoreCase("forever")) {
                                        if (model.getRepeat().equalsIgnoreCase("No Repeat")) {

                                        } else if (model.getRepeat().equalsIgnoreCase("Days")) {

                                            String[] daysArray = model.getRepeate_frequency().split(",");
                                            for (String day : daysArray) {
                                                boolean result = chkDays(day, allDays.get(i));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }

                                            }


                                        } else if (model.getRepeat().equalsIgnoreCase("Daily")) {
                                            model.setShowDate(allDays.get(i));
                                            finalTaskDataModel.add(model);
                                        } else if (model.getRepeat().equalsIgnoreCase("Weekly")) {
                                            // check start date
                                            long starttime = model.getTaskDate();
                                            boolean result = getValues(starttime, "weekly", allDays.get(i));
                                            if (result) {

                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            }
                                        } else if (model.getRepeat().equalsIgnoreCase("Monthly")) {

                                            long starttime = model.getTaskDate();
                                            boolean result = getValues(starttime, "monthly", allDays.get(i));
                                            if (result) {
                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            }
                                        } else if (model.getRepeat().equalsIgnoreCase("Yearly")) {

                                            long starttime = model.getTaskDate();
                                            boolean result = getValues(starttime, "yearly", allDays.get(i));
                                            if (result) {
                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            }
                                        } else if (model.getRepeat().equalsIgnoreCase("Other")) {


                                            long starttime = model.getTaskDate();
                                            boolean result = getOtherValues(starttime, model.getRepeate_frequency(), allDays.get(i), Integer.parseInt(model.getRepeate_num()));
                                            if (result) {
                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            }
                                        }

                                    } else {
                                        String pickdate = model.getRepeate_untill();
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

                                            if (model.getRepeat().equalsIgnoreCase("No Repeat")) {

                                            } else if (model.getRepeat().equalsIgnoreCase("Days")) {

                                                String[] daysArray = model.getRepeate_frequency().split(",");
                                                for (String day : daysArray) {
                                                    boolean result = chkDays(day, allDays.get(i));
                                                    if (result) {
                                                        model.setShowDate(allDays.get(i));
                                                        finalTaskDataModel.add(model);
                                                    }
                                                }

                                            } else if (model.getRepeat().equalsIgnoreCase("Daily")) {
                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            } else if (model.getRepeat().equalsIgnoreCase("Weekly")) {
                                                // check start date
                                                long starttime = model.getTaskDate();
                                                boolean result = getValues(starttime, "weekly", allDays.get(i));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }
                                            } else if (model.getRepeat().equalsIgnoreCase("Monthly")) {

                                                long starttime = model.getTaskDate();
                                                boolean result = getValues(starttime, "monthly", allDays.get(i));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }
                                            } else if (model.getRepeat().equalsIgnoreCase("Yearly")) {

                                                long starttime = model.getTaskDate();
                                                boolean result = getValues(starttime, "yearly", allDays.get(i));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }
                                            } else if (model.getRepeat().equalsIgnoreCase("Other")) {


                                                long starttime = model.getTaskDate();
                                                boolean result = getOtherValues(starttime, model.getRepeate_frequency(), allDays.get(i), Integer.parseInt(model.getRepeate_num()));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }
                                            }

                                        }
                                    }
                                }

                            }

                        }
                    }


                }
                if (finalTaskDataModel.size() == 0) {
                } else {
                    this.finalTaskDataModelList.addAll(finalTaskDataModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        return finalTaskDataModelList;
    }

    public long calculateBTWNDays(Calendar a, Calendar b) {

        long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
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

    private class FetchTaskFromDBAsync extends AsyncTask<Void, Void, LinkedHashMap<String, ArrayList<TaskMappingModel>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Please wait..");
        }

        @Override
        protected LinkedHashMap<String, ArrayList<TaskMappingModel>> doInBackground(Void... voids) {
            LinkedHashMap<String, ArrayList<TaskMappingModel>> taskList = getAllTASK(false, 0);

            return taskList;
        }

        @Override
        protected void onPostExecute(LinkedHashMap<String, ArrayList<TaskMappingModel>> taskList) {
            adapterTaskList = new AdapterTaskList(getActivity(), headerList, taskList);
            tastMainList.setAdapter(adapterTaskList);
            adapterTaskList.notifyDataSetChanged();

            removeProgressDialog();
            // ((DashboardActivity) getActivity()).notiftTaskinFragment();
            super.onPostExecute(taskList);
        }
    }

    public void refreshList(String oldName, String newName, int id) {

        LinkedHashMap<String, ArrayList<TaskMappingModel>> tempChildList = adapterTaskList.getChildListData();
        LinkedHashMap<String, ArrayList<TaskMappingModel>> newChildList = new LinkedHashMap<>();
        TaskMappingModel taskMappingModel;
        for (Map.Entry<String, ArrayList<TaskMappingModel>> entry : tempChildList.entrySet()) {
            ArrayList<TaskMappingModel> newTaskList = new ArrayList<>();

            if (entry.getValue() != null) {

                for (int i = 0; i < entry.getValue().size(); i++) {

                    if (entry.getValue().get(i).getTaskListname().equalsIgnoreCase(oldName)) {
                        taskMappingModel = (TaskMappingModel) entry.getValue().get(i);
                        taskMappingModel.setTaskListname(newName);
                        newTaskList.add(taskMappingModel);
                    } else {
                        newTaskList.add(entry.getValue().get(i));
                    }
                }
                newChildList.put(entry.getKey(), newTaskList);
            }
        }
        adapterTaskList.setChildListData(newChildList);
        adapterTaskList.notifyDataSetChanged();

    }

}
