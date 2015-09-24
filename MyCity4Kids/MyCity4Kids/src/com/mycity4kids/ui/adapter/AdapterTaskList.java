package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.newmodels.TaskMappingModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by manish.soni on 08-07-2015.
 */
public class AdapterTaskList extends BaseExpandableListAdapter {

    TableTaskData tableTaskData;
    Calendar calendar;
    private Context mContext;
    private List<String> headerList; // header titles
    // child data in format of header title, child title
    private LinkedHashMap<String, ArrayList<TaskMappingModel>> childList;
    private TaskCompletedTable completedTable = new TaskCompletedTable(BaseApplication.getInstance());

    public AdapterTaskList(Context context, List<String> listDataHeader,
                           LinkedHashMap<String, ArrayList<TaskMappingModel>> listChildData) {
        this.mContext = context;
        this.headerList = listDataHeader;
        this.childList = listChildData;
    }


    public LinkedHashMap<String, ArrayList<TaskMappingModel>> getChildListData() {
        return childList;
    }

    public void setChildListData(LinkedHashMap<String, ArrayList<TaskMappingModel>> mList) {
        this.childList = mList;
    }

    @Override
    public int getGroupCount() {
        return this.headerList == null ? 0 : this.headerList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this.childList.get(this.headerList.get(groupPosition)) == null ? 0 : this.childList.get(this.headerList.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childList.get(this.headerList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        final GroupViewHolder groupHolder;

        LayoutInflater infalInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.aa_task_header, null);
            groupHolder = new GroupViewHolder();

            groupHolder.header = (TextView) convertView.findViewById(R.id.task_header);

            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupViewHolder) convertView.getTag();
        }

        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);

        if (headerList.get(groupPosition).equalsIgnoreCase("OVERDUE ITEMS")) {

            groupHolder.header.setText(headerList.get(groupPosition));
            groupHolder.header.setTextColor(Color.parseColor("#FD3159"));

        } else if (headerList.get(groupPosition).equalsIgnoreCase("DUE THIS WEEK")) {

            groupHolder.header.setText(headerList.get(groupPosition));
            groupHolder.header.setTextColor(Color.BLACK);

        } else if (headerList.get(groupPosition).equalsIgnoreCase("DUE IN 30 DAYS")) {

            groupHolder.header.setText(headerList.get(groupPosition));
            groupHolder.header.setTextColor(Color.BLACK);

        }
        convertView.setBackgroundColor(Color.parseColor("#F0F4FF"));

        return convertView;
    }

    class GroupViewHolder {
        TextView header;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final TaskMappingModel taskItems = (TaskMappingModel) getChild(groupPosition, childPosition);
        tableTaskData = new TableTaskData(BaseApplication.getInstance());

        final ViewHolder holder;

        LayoutInflater infalInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.aa_task_item, null);
            holder = new ViewHolder();

            holder.date = (TextView) convertView.findViewById(R.id.task_date);
            holder.title = (TextView) convertView.findViewById(R.id.task_name);
            holder.listName = (TextView) convertView.findViewById(R.id.list_name);
            holder.colorCode = (LinearLayout) convertView.findViewById(R.id.colorCode);
            holder.notesImage = (ImageView) convertView.findViewById(R.id.task_notes);
            holder.checkBox = (ImageView) convertView.findViewById(R.id.task_check);
            holder.notask = (TextView) convertView.findViewById(R.id.no_task);
            holder.hasTask = (LinearLayout) convertView.findViewById(R.id.hasTask);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (taskItems.getTaskName() == null) {
            holder.hasTask.setVisibility(View.GONE);
            holder.notask.setVisibility(View.VISIBLE);
        } else {

            holder.hasTask.setVisibility(View.VISIBLE);
            holder.notask.setVisibility(View.GONE);

            LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

            holder.title.setText(taskItems.getTaskName());

            try {
                if (StringUtils.isNullOrEmpty(taskItems.getShowDate())) {
                    holder.date.setText(check_Today_yesterday_tomorrow(taskItems.getTaskDate()));
                } else {
                    holder.date.setText(check_Today_yesterday_tomorrow(convertshowDateToTimeStamp(taskItems.getShowDate())));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            holder.listName.setText(taskItems.getTaskListname());


            holder.colorCode.removeAllViews();

            for (int i = 0; i < ((TaskMappingModel) getChild(groupPosition, childPosition)).getAttendees().size(); i++) {

                View dummyView = new View(mContext);
                dummyView.setLayoutParams(dummyParams);
                try {
                    dummyView.setBackgroundColor(Color.parseColor(taskItems.getAttendees().get(i).getColorCode()));
                    dummyParams.weight = 1f;
                    holder.colorCode.addView(dummyView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (taskItems.getNumberNotes() > 0) {
                holder.notesImage.setVisibility(View.VISIBLE);
            } else {
                holder.notesImage.setVisibility(View.GONE);
            }

            // holder.checkBox.setTag("0");


            if (taskItems.isCompleted()) {
                holder.checkBox.setImageResource(R.drawable.checkbox_grey_withcheckxxhdpi);
                holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.checkBox.setImageResource(R.drawable.checkbox_grey_xxhdpi);
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // completed tasks

                    if (taskItems.isCompleted()) {   //  now completed false

                        holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        if (taskItems.getIs_recurring().equalsIgnoreCase("no"))
                            tableTaskData.CompleteTaskFlag(taskItems.getTask_id(), 1);

                        else {
                            // update in completed tasks table

                            if (StringUtils.isNullOrEmpty(taskItems.getShowDate())) {
                                completedTable.deleteTask(getTaskDate(taskItems.getTaskDate()), taskItems.getTask_id());
                            } else {
                                completedTable.deleteTask(taskItems.getShowDate(), taskItems.getTask_id());
                            }

                        }
                        taskItems.setIsCompleted(false);
                        holder.checkBox.setImageResource(R.drawable.checkbox_grey_xxhdpi);

                    } else {
                        holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        if (taskItems.getIs_recurring().equalsIgnoreCase("no"))
                            tableTaskData.CompleteTaskFlag(taskItems.getTask_id(), 0);

                        else {

                            try {
                                if (StringUtils.isNullOrEmpty(taskItems.getShowDate())) {
                                    completedTable.AddTasks(getTaskDate(taskItems.getTaskDate()), taskItems.getTask_id());
                                } else {
                                    completedTable.AddTasks(taskItems.getShowDate(), taskItems.getTask_id());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        taskItems.setIsCompleted(true);
                        holder.checkBox.setImageResource(R.drawable.checkbox_grey_withcheckxxhdpi);

                    }


                }
            });

        }


        return convertView;
    }

    class ViewHolder {
        TextView date;
        TextView title;
        TextView listName;
        LinearLayout colorCode;
        ImageView notesImage;
        ImageView checkBox;
        TextView notask;
        LinearLayout hasTask;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void notifyDataChange(ArrayList<String> headerData, LinkedHashMap<String, ArrayList<TaskMappingModel>> childData) {

        this.headerList = headerData;
        this.childList = childData;

        notifyDataSetChanged();

    }

    private String getDate(long timeStampStr, Boolean flag) {

        try {
            if (flag) {
                DateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy");
                Date netDate = (new Date(timeStampStr));
                return sdf.format(netDate);
            } else {
                DateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                Date netDate = (new Date(timeStampStr));
                return sdf.format(netDate);
            }
        } catch (Exception ex) {
            return "xx";
        }
    }


    private String getTaskDate(long timeStampStr) {

        try {

            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate);

        } catch (Exception ex) {
            return "";
        }
    }

    public long convertTimeStamp(CharSequence date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy h:mm a");

        Date tempDate = formatter.parse((String) date);
        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

    public long convertshowDateToTimeStamp(CharSequence date) {

        if (!StringUtils.isNullOrEmpty(date.toString())) {

            date = date + " 9:00 AM";

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm a");

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

    public String check_Today_yesterday_tomorrow(long timestamp) {

        String date = "";

        Calendar today = Calendar.getInstance();
        Calendar tomorrow = (Calendar) today.clone();
        Calendar yesterday = (Calendar) today.clone();

        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        long todayStart = 0, todayEnd = 0, yesterdayStart = 0, yesterdayEnd = 0, tomorrowStart = 0, tomorrowEnd = 0;

        try {
            todayStart = (convertTimeStamp(String.valueOf(String.valueOf(today.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((today.get(Calendar.MONTH) + 1)) + " " + String.valueOf(today.get(Calendar.YEAR)) + " 12:01 AM")));

            todayEnd = (convertTimeStamp(String.valueOf(today.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((today.get(Calendar.MONTH) + 1)) + " " + String.valueOf(today.get(Calendar.YEAR)) + " 11:59 PM"));

            yesterdayStart = (convertTimeStamp(String.valueOf(yesterday.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((yesterday.get(Calendar.MONTH) + 1)) + " " + String.valueOf(yesterday.get(Calendar.YEAR)) + " 12:01 AM"));

            yesterdayEnd = (convertTimeStamp(String.valueOf(yesterday.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((yesterday.get(Calendar.MONTH)) + 1) + " " + String.valueOf(yesterday.get(Calendar.YEAR)) + " 11:59 PM"));

            tomorrowStart = (convertTimeStamp(String.valueOf(tomorrow.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((tomorrow.get(Calendar.MONTH) + 1)) + " " + String.valueOf(tomorrow.get(Calendar.YEAR)) + " 12:01 AM"));

            tomorrowEnd = (convertTimeStamp(String.valueOf(tomorrow.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((tomorrow.get(Calendar.MONTH) + 1)) + " " + String.valueOf(tomorrow.get(Calendar.YEAR)) + " 11:59 PM"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (timestamp >= todayStart && timestamp <= todayEnd) {
            date = "Today, " + getDate(timestamp, false);
        } else if (timestamp >= yesterdayStart && timestamp <= yesterdayEnd) {
            date = "Yesterday, " + getDate(timestamp, false);
        } else if (timestamp >= tomorrowStart && timestamp <= tomorrowEnd) {
            date = "Tomorrow, " + getDate(timestamp, false);
        } else {
            date = getDate(timestamp, true);
        }

        return date;

    }


}
