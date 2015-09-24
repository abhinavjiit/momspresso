package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 01-07-2015.
 */
public class ImagesViewPagerAdapter extends PagerAdapter {
    private LayoutInflater infalter;
    private Context mContext;
    private ArrayList<AppoitmentDataModel.Files> mImageNames;
    private ArrayList<TaskDataModel.Files> mTaskImageNames;
    private boolean isFromAppointment;

    public ImagesViewPagerAdapter(Context c, ArrayList<AppoitmentDataModel.Files> pImagesName,ArrayList<TaskDataModel.Files> pTaskImageName,boolean isAppointment) {
        infalter = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mContext = c;
        mImageNames = pImagesName;
        mTaskImageNames = pTaskImageName;
        isFromAppointment = isAppointment;
    }


    @Override
    public int getCount() {

        if(isFromAppointment)
            return mImageNames == null ? 0 : mImageNames.size();
        else
            return mTaskImageNames == null ? 0 : mTaskImageNames.size();

    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub

        View layout = infalter.inflate(R.layout.view_pager_item, null);
        ImageView imgQueue = (ImageView) layout.findViewById(R.id.imgItem);

        ((ViewPager) container).addView(layout);

        if(isFromAppointment){
            try {

                Picasso.with(mContext) //
                        .load(mImageNames.get(position).getUrl()) //
                                // .placeholder(R.drawable.no_media) //
                                // .error(R.drawable.no_media) //
                                // .fit() //
                                //.tag(mContext) //
                        .into(imgQueue);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else{
            try {

                Picasso.with(mContext) //
                        .load(mTaskImageNames.get(position).getUrl()) //
                                // .placeholder(R.drawable.no_media) //
                                // .error(R.drawable.no_media) //
                                // .fit() //
                                //.tag(mContext) //
                        .into(imgQueue);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        //imgQueue.setOnClickListener(onclickLisner);

        return layout;
    }
}
