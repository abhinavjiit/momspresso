package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class BlogFilterActivity extends BaseActivity implements View.OnClickListener {

    TextView rankName, blogger, expert, editor, aTOz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setContentView(R.layout.blog_filter_activity_layout);
        rankName = (TextView) findViewById(R.id.rank_name);
        blogger = (TextView) findViewById(R.id.bloggers);
        expert = (TextView) findViewById(R.id.experts);
        editor = (TextView) findViewById(R.id.editors);
        aTOz = (TextView) findViewById(R.id.atoz);

        rankName.setOnClickListener(this);
        blogger.setOnClickListener(this);
        expert.setOnClickListener(this);
        editor.setOnClickListener(this);
        aTOz.setOnClickListener(this);


    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {

            case R.id.atoz:

                intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra(Constants.FILTER_BLOG_SORT_TYPE, "atoz");
                setResult(RESULT_OK, intent);
                finish();

                break;

            case R.id.rank_name:

                intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra(Constants.FILTER_BLOG_SORT_TYPE, "ranking");
                setResult(RESULT_OK, intent);
                finish();

                break;
            case R.id.bloggers:

                intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra(Constants.FILTER_BLOG_SORT_TYPE, "bloggers");
                setResult(RESULT_OK, intent);
                finish();

                break;
            case R.id.experts:

                intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra(Constants.FILTER_BLOG_SORT_TYPE, "experts");
                setResult(RESULT_OK, intent);
                finish();

                break;
            case R.id.editors:

                intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra(Constants.FILTER_BLOG_SORT_TYPE, "editors");
                setResult(RESULT_OK, intent);
                finish();

                break;

        }
    }
}
