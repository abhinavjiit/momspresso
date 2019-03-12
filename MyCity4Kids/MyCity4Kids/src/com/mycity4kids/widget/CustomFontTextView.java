package com.mycity4kids.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;

import com.mycity4kids.R;

/**
 * Created by hemant on 17/3/16.
 */
public class CustomFontTextView extends android.support.v7.widget.AppCompatTextView {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public CustomFontTextView(Context context) {
        super(context);
        init(null);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
            String fontName = a.getString(R.styleable.CustomFontTextView_fontName);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }
}
