package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.newmodels.AttendeeModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 25-06-2015.
 */
public class AdapterKidAdultList extends BaseAdapter {

    Context context;
    ArrayList<KidsInfo> datalist;

    public AdapterKidAdultList(Context context, ArrayList<KidsInfo> datalist) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.aa_kid_user_view, viewGroup, false);

            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.viewColor = (View) view.findViewById(R.id.view_colorCode);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(datalist.get(i).getName());
        try {
//            holder.name.setTextColor(Color.parseColor(datalist.get(i).getColorCode()));
            if (null != datalist.get(i).getColor_code())
                holder.viewColor.setBackgroundColor(Color.parseColor(datalist.get(i).getColor_code()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    public static class ViewHolder {
        TextView name;
        View viewColor;

    }

}
