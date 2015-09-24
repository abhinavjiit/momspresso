package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.TaskDataModel;

import java.util.ArrayList;

public class AddDocumentAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AppoitmentDataModel.Files> photosList;
    private ArrayList<TaskDataModel.Files> taskPhotosList;
    private View.OnClickListener onclickLisner;
    private boolean isFromAppointment;
    private boolean showDelete;


    public AddDocumentAdapter(Context context, ArrayList<AppoitmentDataModel.Files> urls, ArrayList<TaskDataModel.Files> taskurls, View.OnClickListener mclickListner, boolean isAppointment, boolean delete) {

        mContext = context;
        photosList = urls;
        taskPhotosList = taskurls;
        onclickLisner = mclickListner;
        isFromAppointment = isAppointment;
        showDelete = delete;

    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (isFromAppointment)
            return photosList == null ? 0 : photosList.size();
        else
            return taskPhotosList == null ? 0 : taskPhotosList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub

        if (isFromAppointment)
            return photosList.get(position);
        else
            return taskPhotosList.get(position);

    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /*@Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return photosList == null ? 0 : photosList.size();
    }*/
    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;


        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.aa_show_attachments, null);
            holder = new ViewHolder();
            holder.txt = (TextView) convertView.findViewById(R.id.txtfile);
            holder.deleteimage = (ImageView) convertView.findViewById(R.id.delete_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (showDelete) {
            holder.deleteimage.setVisibility(View.VISIBLE);
        } else {
            holder.deleteimage.setVisibility(View.GONE);
        }


        holder.deleteimage.setOnClickListener(onclickLisner);
        holder.deleteimage.setTag(position);

        holder.txt.setVisibility(View.VISIBLE);

        if (isFromAppointment) {
            holder.txt.setText(photosList.get(position).getFile_name());
        } else {
            holder.txt.setText(taskPhotosList.get(position).getFile_name());
        }

        holder.txt.setPaintFlags(holder.txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        holder.txt.setOnClickListener(onclickLisner);

        holder.txt.setTag(position);

        return convertView;
    }


    private class ViewHolder {

        ImageView deleteimage;
        TextView txt;
    }


}
