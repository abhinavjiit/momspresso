package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.TutorialAdapter;
import com.shuhart.bubblepagerindicator.BubblePageIndicator;


public class TutorialActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private TutorialAdapter mViewPagerAdapter;
    private BubblePageIndicator dotIndicatorView;
    private TextView signinTextView;
    private TextView getStartedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(TutorialActivity.this, "OnboardingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        mViewPager = (ViewPager) findViewById(R.id.pager);
        dotIndicatorView = (BubblePageIndicator) findViewById(R.id.dotIndicatorView);
        signinTextView = (TextView) findViewById(R.id.signinTextView);
        getStartedTextView = (TextView) findViewById(R.id.getStartedTextView);

        getStartedTextView.setOnClickListener(this);
        signinTextView.setOnClickListener(this);

        mViewPagerAdapter = new TutorialAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);

        dotIndicatorView.setViewPager(mViewPager);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinTextView: {
                Intent intent = new Intent(this, ActivityLogin.class);
                intent.putExtra(AppConstants.LAUNCH_FRAGMENT, AppConstants.FRAGMENT_SIGNIN);
                startActivity(intent);
            }
            break;
            case R.id.getStartedTextView: {
                Intent intent = new Intent(this, ActivityLogin.class);
                intent.putExtra(AppConstants.LAUNCH_FRAGMENT, AppConstants.FRAGMENT_SIGNUP);
                startActivity(intent);
            }
            break;
        }
    }

}
