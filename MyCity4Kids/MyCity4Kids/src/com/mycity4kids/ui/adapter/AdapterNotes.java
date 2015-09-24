package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.NotesModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 25-06-2015.
 */
public class AdapterNotes extends BaseAdapter {

    Context context;
    ArrayList<NotesModel> datalist;

    public AdapterNotes(Context context, ArrayList<NotesModel> datalist) {
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
            view = inflater.inflate(R.layout.aa_note_item, viewGroup, false);

            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.text);
            holder.addedby = (TextView) view.findViewById(R.id.addedby);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(datalist.get(i).getMsg());
        holder.addedby.setText("Added by "+datalist.get(i).getAddedby());


        return view;
    }

    public static class ViewHolder {
        TextView name;
        TextView addedby;

    }

}
