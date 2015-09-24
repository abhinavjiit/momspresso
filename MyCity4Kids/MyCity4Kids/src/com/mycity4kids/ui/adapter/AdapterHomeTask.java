package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by manish.soni on 10-07-2015.
 */
public class AdapterHomeTask extends BaseAdapter {

    Context context;
    ArrayList<TaskMappingModel> datalist;
    TableTaskData tableTaskData;
    private TaskCompletedTable completedTable = new TaskCompletedTable(BaseApplication.getInstance());

    public AdapterHomeTask(Context context, ArrayList<TaskMappingModel> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist == null ? 0 : datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final TaskMappingModel taskItems = (TaskMappingModel) datalist.get(position);
        tableTaskData = new TableTaskData(BaseApplication.getInstance());

        final ViewHolder holder;

        LayoutInflater infalInflater = (LayoutInflater) this.context
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
                    holder.date.setText("Today, " + getDate(taskItems.getTaskDate()));
                } else {
                    holder.date.setText("Today, " + getDate(convertshowDateToTimeStamp(taskItems.getShowDate())));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.listName.setText(taskItems.getTaskListname());
            holder.colorCode.removeAllViews();

            for (int i = 0; i < taskItems.getAttendees().size(); i++) {

                View dummyView = new View(context);
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

            holder.checkBox.setTag("0");

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

//            holder.checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    if (holder.checkBox.getTag().equals("0")) {
//
//                        holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                        tableTaskData.inActiveAppointment(taskItems.getTask_id());
//
//                        holder.checkBox.setImageResource(R.drawable.checkbox_grey_withcheckxxhdpi);
//                        holder.checkBox.setTag("1");
//
//                    } else {
//                        holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
//                        tableTaskData.ActiveAppointment(taskItems.getTask_id());
//
//                        holder.checkBox.setImageResource(R.drawable.checkbox_grey_xxhdpi);
//                        holder.checkBox.setTag("0");
//                    }
//
//                }
//            });

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

    public void notifyTaskList(ArrayList<TaskMappingModel> taskList) {

        this.datalist = taskList;
        notifyDataSetChanged();

    }

    public long convertshowDateToTimeStamp(CharSequence date) {

        if (!StringUtils.isNullOrEmpty(date.toString())) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

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

    private String getDate(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate);
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

}