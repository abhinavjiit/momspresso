package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddImagesAdapter extends BaseAdapter {
    private final float density;
    private Context mContext;
    private ArrayList<AppoitmentDataModel.Files> photosList;
    private ArrayList<TaskDataModel.Files> taskPhotosList;
    private View.OnClickListener onclickLisner;
    private boolean isFromAppointment;
    private boolean showDelete;


    public AddImagesAdapter(Context context, ArrayList<AppoitmentDataModel.Files> urls,ArrayList<TaskDataModel.Files> taskurls, View.OnClickListener mclickListner,boolean isAppointment,boolean delete) {

        mContext = context;
        photosList = urls;
        taskPhotosList = taskurls;
        onclickLisner = mclickListner;
        isFromAppointment = isAppointment;
        showDelete = delete;
        density = mContext.getResources().getDisplayMetrics().density;
    }

//    public void setData(ArrayList<AppoitmentDataModel.Files> photos) {
//        photosList = photos;
//    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(isFromAppointment)
        return photosList == null ? 0 : photosList.size();
        else
            return taskPhotosList == null ? 0 : taskPhotosList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub

        if(isFromAppointment)
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
            convertView = mInflater.inflate(R.layout.aa_image_show, null);
            holder = new ViewHolder();
            holder.imgView = (ImageView) convertView.findViewById(R.id.image);
            holder.deleteimage = (ImageView) convertView.findViewById(R.id.delete_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imgView.setOnClickListener(onclickLisner);
        holder.deleteimage.setOnClickListener(onclickLisner);
        holder.imgView.setTag(position);
        holder.deleteimage.setTag(position);

        if(showDelete) {
            holder.deleteimage.setVisibility(View.VISIBLE);
        }else {
            holder.deleteimage.setVisibility(View.GONE);
        }


        if(isFromAppointment){
            if (!StringUtils.isNullOrEmpty(photosList.get(position).getUrl()))
                Picasso.with(mContext).load(photosList.get(position).getUrl()).
                        placeholder(R.drawable.no_media).resize((int)(100* density), (int)(100* density)).centerCrop().
                        error(R.drawable.no_media).into(holder.imgView);
        }
        else
        {
            if (!StringUtils.isNullOrEmpty(taskPhotosList.get(position).getUrl()))
                Picasso.with(mContext).load(taskPhotosList.get(position).getUrl()).
                        placeholder(R.drawable.no_media).resize((int)(100* density), (int)(100* density)).centerCrop().
                        error(R.drawable.no_media).into(holder.imgView);
        }



        return convertView;
    }


    private class ViewHolder {

        ImageView imgView, deleteimage;
    }

//    public ArrayList<AppoitmentDataModel.Files> galleryPhotoList() {
//
//        return photosList;
//    }


}
