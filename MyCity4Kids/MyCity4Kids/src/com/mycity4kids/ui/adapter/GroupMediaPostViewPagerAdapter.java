package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mycity4kids.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

/**
 * View Pager Adapter to be used in search result
 *
 * @author kapil.vij
 */
public class GroupMediaPostViewPagerAdapter extends PagerAdapter implements View.OnClickListener {

    private ArrayList<String> mediaList;
    Context context;

    public GroupMediaPostViewPagerAdapter(Context context) {
        this.context = context;
    }

    public void setDataList(ArrayList<String> mediaList) {
        this.mediaList = mediaList;
    }

    @Override
    public int getCount() {
        return mediaList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.group_media_pager_item, container, false);
        ImageView iv = (ImageView) v.findViewById(R.id.mediaImageView);
        Picasso.with(context).load(mediaList.get(position)).error(R.drawable.default_article).into(iv);
        iv.setTag(position);
        iv.setOnClickListener(this);
        container.addView(v);
        return v;
    }

    private Matrix getBottomCropMatrix(Context context, int intrinsicWidth, int intrinsicHeight) {
        Matrix matrix = new Matrix();

// Get screen size
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

// Get scale to match parent
        float scaleWidthRatio = screenWidth / intrinsicWidth;
        float scaleHeightRatio = screenHeight / intrinsicHeight;

// screenHeight multi by width scale to get scaled image height
        float scaledImageHeight = intrinsicHeight * scaleWidthRatio;

// If scaledImageHeight < screenHeight, set scale to scaleHeightRatio to fit screen
// If scaledImageHeight >= screenHeight, use width scale as height scale
        if (scaledImageHeight >= screenHeight) {
            scaleHeightRatio = scaleWidthRatio;
        }

        matrix.setScale(scaleWidthRatio, scaleHeightRatio);
        return matrix;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void onClick(View v) {
        new ImageViewer.Builder(context, mediaList)
                .setStartPosition((int) v.getTag())
                .show();
    }
}
