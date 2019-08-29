package com.mycity4kids.utils;

import com.google.android.material.appbar.AppBarLayout;
import androidx.core.view.ViewCompat;
import android.util.Log;
import android.view.View;

public class FadingViewOffsetListener implements AppBarLayout.OnOffsetChangedListener {
    private View mView;

    public FadingViewOffsetListener(View view) {
        mView = view;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        verticalOffset = Math.abs(verticalOffset);
        float halfScrollRange = (int) (appBarLayout.getTotalScrollRange() * 0.5f);
        float ratio = (float) verticalOffset / halfScrollRange;
        ratio = Math.max(0f, Math.min(1f, ratio));
        Log.d("ScrollValues:", "vOffset = " + verticalOffset + " hscrolR = " + halfScrollRange + " ratio = " + ratio);
        ViewCompat.setAlpha(mView, 1 - ratio);

    }
}