package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.TaskListModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 09-07-2015.
 */
public class UserTaskListAdapter extends BaseAdapter {

    Context context;
    ArrayList<TaskListModel> datalist;
    Boolean flag = false;

    public UserTaskListAdapter(Context context, ArrayList<TaskListModel> datalist, Boolean flag) {
        this.context = context;
        this.datalist = datalist;
        this.flag = flag;
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            if (flag) {
                view = inflater.inflate(R.layout.aa_dialog_tasklist_item, viewGroup, false);
            } else {
                view = inflater.inflate(R.layout.aa_task_list_item1, viewGroup, false);
            }

            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.task_list_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (flag) {
            holder.name.setText(datalist.get(i).getList_name());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(datalist.get(i).getList_name() + " (" + String.valueOf(datalist.get(i).getSize()) + ")");
            holder.name.setText(stringBuilder);
        }

        return view;
    }

    public static class ViewHolder {
        TextView name;

    }

    public void notifyList(ArrayList<TaskListModel> list, Boolean flag) {

        datalist = list;
        this.flag = flag;
        notifyDataSetChanged();

    }

}
