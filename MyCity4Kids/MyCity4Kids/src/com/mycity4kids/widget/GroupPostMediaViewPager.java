package com.mycity4kids.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

/**
 * Created by hemant on 11/5/18.
 */

public class GroupPostMediaViewPager extends ViewPager {
    public GroupPostMediaViewPager(Context context) {
        super(context);
    }

    public GroupPostMediaViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
