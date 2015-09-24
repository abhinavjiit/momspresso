package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.interfaces.OnListItemClick;
import com.mycity4kids.newmodels.ExternalAccountInfoModel;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;

/**
 * Created by manish.soni on 30-07-2015.
 */
public class ExternalCalendarAdapter extends BaseAdapter {

    Context context;
    ArrayList<ExternalAccountInfoModel> datalist;
    ExternalCalendarTable externalCalendarTable;
    private OnListItemClick onListItemClick;

    public ExternalCalendarAdapter(Context context, ArrayList<ExternalAccountInfoModel> datalist, OnListItemClick onListItemClick) {
        this.context = context;
        this.datalist = datalist;
        this.onListItemClick = onListItemClick;
    }

    public ArrayList<ExternalAccountInfoModel> getUserList() {
        return datalist;
    }

    public void setUserList(ArrayList<ExternalAccountInfoModel> list) {
        datalist = list;
    }

    public void updateList(ArrayList<ExternalAccountInfoModel> datalist) {
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.aa_external_cal_item, viewGroup, false);

            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.usermail_id);
            holder.color = (ImageView) view.findViewById(R.id.color_icon);
            holder.delete = (ImageView) view.findViewById(R.id.delete);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(datalist.get(i).getUserId());

//        Drawable res = context.getResources().getDrawable(R.drawable.calender_color_circle);
//        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
//        res.setColorFilter(Color.parseColor(datalist.get(i).getColorCode()), mode);
//        holder.color.setImageDrawable(res);

        Drawable res = context.getResources().getDrawable(R.drawable.event_dot);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        res.setColorFilter(Color.parseColor(SharedPrefUtils.getUserDetailModel(context).getColor_code()), mode);
        holder.color.setBackground(res);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onListItemClick != null) {
                    onListItemClick.onItenClicked(view, datalist.get(i).getId());
                }
            }
        });

        return view;
    }

    public static class ViewHolder {
        TextView name;
        ImageView color;
        ImageView delete;

    }
}