package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

/**
 * Created by hemant on 25/7/17.
 */
public class BlogSetupActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout introLinearLayout;
    private RelativeLayout detailsRelativeLayout;
    private TextView okayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_setup_activity);

        introLinearLayout = (LinearLayout) findViewById(R.id.introLinearLayout);
        detailsRelativeLayout = (RelativeLayout) findViewById(R.id.detailsRelativeLayout);
        okayTextView = (TextView) findViewById(R.id.okayTextView);

//        introLinearLayout.setOnClickListener(this);
//        detailsRelativeLayout.setOnClickListener(this);
        okayTextView.setOnClickListener(this);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okayTextView:
                introLinearLayout.setVisibility(View.GONE);
                detailsRelativeLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
}
