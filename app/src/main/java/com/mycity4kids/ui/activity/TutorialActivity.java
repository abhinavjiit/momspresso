package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.TutorialAdapter;
import com.mycity4kids.widget.IndefinitePagerIndicator;


public class TutorialActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private TutorialAdapter mViewPagerAdapter;
    private IndefinitePagerIndicator dotIndicatorView;
    private TextView signinTextView;
    private TextView getStartedTextView;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(TutorialActivity.this, "OnboardingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        mViewPager = (ViewPager) findViewById(R.id.pager);
        dotIndicatorView = (IndefinitePagerIndicator) findViewById(R.id.dotIndicatorView);
        signinTextView = (TextView) findViewById(R.id.signinTextView);
        getStartedTextView = (TextView) findViewById(R.id.getStartedTextView);

        getStartedTextView.setOnClickListener(this);
        signinTextView.setOnClickListener(this);

        mViewPagerAdapter = new TutorialAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);

        dotIndicatorView.attachToViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinTextView: {
                Utils.pushGenericEvent(this, "Launch_sign_in_event", "NA", "TutorialActivity");
                Intent intent = new Intent(this, ActivityLogin.class);
                intent.putExtra(AppConstants.LAUNCH_FRAGMENT, AppConstants.FRAGMENT_SIGNIN);
                startActivity(intent);
            }
            break;
            case R.id.getStartedTextView: {
                Utils.pushGenericEvent(this, "Launch_sign_up_event", "NA", "TutorialActivity");
                Intent intent = new Intent(this, ActivityLogin.class);
                intent.putExtra(AppConstants.LAUNCH_FRAGMENT, AppConstants.FRAGMENT_SIGNUP);
                startActivity(intent);
            }
            break;
        }
    }

}