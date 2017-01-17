package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.adapter.UploadVideoFLAdapter;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;

/**
 * Created by hemant on 16/1/17.
 */
public class UploadVideoFLActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ImageView tutorialImageView;
    private TextView learnMoreTextView;
    private LinearLayout introLayout;

    private UploadVideoFLAdapter mViewPagerAdapter;
    ParticleSystem p1, p2, p3, p4;
    Handler handler = new Handler();
    Handler changImageHandler = new Handler();
    private ArrayList<Integer> pagerImagesList;
    private int position = 0;
    private RelativeLayout tutorialLayout;
    Runnable runnable;
    private TextView goToFunnyVideosTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_video_fl_activity);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        tutorialImageView = (ImageView) findViewById(R.id.uploadTutorialImageView);
        learnMoreTextView = (TextView) findViewById(R.id.learnMoreTextView);
        introLayout = (LinearLayout) findViewById(R.id.introLayout);
        tutorialLayout = (RelativeLayout) findViewById(R.id.tutorialLayout);
        goToFunnyVideosTextView = (TextView) findViewById(R.id.goToFunnyVideosTextView);

        pagerImagesList = new ArrayList<Integer>();
        pagerImagesList.add(R.drawable.video_intro1);
        pagerImagesList.add(R.drawable.video_intro2);
        pagerImagesList.add(R.drawable.video_intro3);
        pagerImagesList.add(R.drawable.video_intro4);
        pagerImagesList.add(R.drawable.video_intro5);

        p1 = new ParticleSystem(UploadVideoFLActivity.this, 60, R.drawable.red_confetti, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.2f, 120, 60)
                .setRotationSpeed(144)
                .setAcceleration(0.000020f, 90);
        p2 = new ParticleSystem(UploadVideoFLActivity.this, 60, R.drawable.yellow_confetti, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.2f, 120, 60)
                .setRotationSpeed(144)
                .setAcceleration(0.000020f, 90);
        p3 = new ParticleSystem(UploadVideoFLActivity.this, 60, R.drawable.blue_confetti, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.2f, 120, 60)
                .setRotationSpeed(144)
                .setAcceleration(0.000020f, 90);
        p4 = new ParticleSystem(UploadVideoFLActivity.this, 60, R.drawable.violet_confetti, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.2f, 120, 60)
                .setRotationSpeed(144)
                .setAcceleration(0.000020f, 90);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                p1.emit(findViewById(R.id.emiter_top_left), 8);
                p2.emit(findViewById(R.id.emiter_top_right), 8);
                p3.emit(findViewById(R.id.emitter_top_center), 8);
                p4.emit(findViewById(R.id.emitter_top_center), 8);
            }
        }, 1600);

        runnable = new Runnable() {
            @Override
            public void run() {
                tutorialImageView.setImageResource(pagerImagesList.get(position));
                position++;
                if (position >= pagerImagesList.size()) {
                    position = 0;
                }
                changImageHandler.postDelayed(runnable, 1000);
            }
        };
        learnMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introLayout.setVisibility(View.GONE);
                tutorialLayout.setVisibility(View.VISIBLE);
                changImageHandler.postDelayed(runnable, 1000);
            }
        });
        goToFunnyVideosTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadVideoFLActivity.this, VlogsListingActivity.class);
                intent.putExtra(AppConstants.STACK_CLEAR_REQUIRED, true);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void updateUi(Response response) {

    }
}
