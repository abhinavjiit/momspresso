package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.loading.TaskImageLoader;
import com.mycity4kids.models.businesseventdetails.GalleryListtingData;
import com.mycity4kids.widget.BitmapLruCache;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    ImageLoader.ImageCache imageCache;
    ImageLoader imageLoader;
    private ArrayList<GalleryListtingData> photosList;
    TaskImageLoader loader;
    private String galleryType;
    int width, height;

    public GalleryAdapter(Context context, int textViewResourceId, String galleryType) {

        imageCache = new BitmapLruCache();
        imageLoader = new ImageLoader(Volley.newRequestQueue(context), imageCache);
        mContext = context;
        this.galleryType = galleryType;
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        //loader=new TaskImageLoader(context);
    }

    public void setData(ArrayList<GalleryListtingData> photos) {
        photosList = photos;
        Log.d("check", "photo list size" + photosList.size());
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return photosList == null ? 0 : photosList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return photosList.get(position);
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
        Log.i("listSize", "" + photosList.size());

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_gallery_cell, null);
            holder = new ViewHolder();
            holder.galleryImg = (NetworkImageView) convertView.findViewById(R.id.gallery_img);
            holder.imgView = (ImageView) convertView.findViewById(R.id.gallery_img1);
            holder.gview = (RelativeLayout) convertView.findViewById(R.id.galleryview);
            convertView.setTag(holder);
            Log.d("check", "height" + height);
            holder.gview.getLayoutParams().height = height / 5;
            holder.gview.getLayoutParams().width = height / 5;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position != 0) {
            holder.galleryImg.getLayoutParams().height = height / 5;
            holder.galleryImg.getLayoutParams().width = height / 5;
            holder.imgView.getLayoutParams().height = height / 5;
            holder.imgView.getLayoutParams().width = height / 5;
            holder.galleryImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.galleryImg.setBackgroundResource(R.drawable.no_media);
        }
        if (galleryType.equals(Constants.FIRST_GALLERY)) {
            if (photosList.get(position).getImageBitmap() != null) {
                holder.imgView.setVisibility(View.VISIBLE);
                holder.galleryImg.setVisibility(View.GONE);
                holder.imgView.setImageBitmap(photosList.get(position).getImageBitmap());
            } /*else if (photosList.get(position).getResourceId()!=0) {
                holder.galleryImg.setBackgroundResource(photosList
						.get(position).getResourceId());

				// holder.imgView.setVisibility(View.VISIBLE);
				// holder.galleryImg.setVisibility(View.GONE);
			}*/ else {
                holder.imgView.setVisibility(View.GONE);
                holder.galleryImg.setVisibility(View.VISIBLE);
                holder.galleryImg.setImageUrl(photosList.get(position).getImageUrl(), imageLoader);
            }
        } else if (galleryType.equals(Constants.SECOND_GALLERY)) {

            holder.galleryImg.setBackgroundResource(R.drawable.video_loading1);
            holder.galleryImg.setImageUrl(photosList.get(position).getImageUrl(), imageLoader);

        }

        return convertView;
    }

    private class ViewHolder {
        NetworkImageView galleryImg;
        ImageView imgView;
        RelativeLayout gview;

    }

    public ArrayList<GalleryListtingData> galleryPhotoList() {

        return photosList;
    }


}
