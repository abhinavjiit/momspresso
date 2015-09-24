package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.ui.adapter.TutorialAdapter;

public class TutorialActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private TutorialAdapter mViewPagerAdapter;
    private TextView btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pager);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        btnSkip = (TextView) findViewById(R.id.txvSkip);
        mViewPagerAdapter = new TutorialAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);

        ((TextView) findViewById(R.id.txvSkip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTologin();
            }


        });

    }


    public void hideLogos(boolean flag) {
        if (flag) {
            findViewById(R.id.logo).setVisibility(View.VISIBLE);
            findViewById(R.id.txvSkip).setVisibility(View.VISIBLE);
            btnSkip.setText("Skip");
        } else {
            findViewById(R.id.logo).setVisibility(View.GONE);
            findViewById(R.id.txvSkip).setVisibility(View.GONE);
            // btnSkip.setText("Get Started");
        }


    }

    private void navigateTologin() {

//        Intent intent = new Intent(TutorialActivity.this, LandingLoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
        mViewPager.setCurrentItem(5);


    }


    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public int lastPosition = -1;
            public boolean isLastPageReached = false;

            @Override
            public void onPageSelected(int i) {
//                setCurrentIndicator(i);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (lastPosition != position) {
                    // DB query

                }
                lastPosition = position;
                hideLogos(position != 5);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

//    @Override
//    protected void updateUi(Response response) {
//
//    }

    private void setCurrentIndicator(int pos) {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        for (int j = 0; j < linearLayout.getChildCount(); j++) {
            if (j == pos) {
                ((ImageView) linearLayout.getChildAt(j)).setImageResource(R.drawable.white_filled_bg);
            } else {
                ((ImageView) linearLayout.getChildAt(j)).setImageResource(R.drawable.grey_filled_bg);
            }
        }

    }

}
