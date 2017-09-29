package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.TutorialAdapter;


public class TutorialActivity extends BaseActivity {

    private ViewPager mViewPager;
    private TutorialAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        Utils.pushOpenScreenEvent(TutorialActivity.this, "OnboardingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mViewPagerAdapter = new TutorialAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);

    }

    @Override
    protected void updateUi(Response response) {

    }

}
