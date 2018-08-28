package com.mycity4kids.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by hemant on 27/8/18.
 */

public class TopicsSquareView extends LinearLayout {
    public TopicsSquareView(Context context) {
        super(context);
    }

    public TopicsSquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopicsSquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}