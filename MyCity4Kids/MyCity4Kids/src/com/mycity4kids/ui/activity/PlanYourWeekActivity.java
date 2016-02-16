package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by hemant on 30/12/15.
 */
public class PlanYourWeekActivity extends BaseActivity implements View.OnClickListener {

    TextView addAppointmentTextView;
    TextView addTodoTextView;
    ImageView addAppointmentImageView;
    ImageView addTodoImageView;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(PlanYourWeekActivity.this, "Plan Your Week", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        setContentView(R.layout.aa_suggested_task_cal_fragment);
        addAppointmentTextView = (TextView) findViewById(R.id.txtCal);
        addTodoTextView = (TextView) findViewById(R.id.txtTodo);
        addAppointmentImageView = (ImageView) findViewById(R.id.add_appointment);
        addTodoImageView = (ImageView) findViewById(R.id.add_task);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Plan Your Week");

        addAppointmentTextView.setOnClickListener(this);
        addTodoTextView.setOnClickListener(this);
        addAppointmentImageView.setOnClickListener(this);
        addTodoImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.txtCal:
            case R.id.add_appointment:
                intent = new Intent(this, ActivityCreateAppointment.class);
                startActivity(intent);
                break;
            case R.id.txtTodo:
            case R.id.add_task:
                intent = new Intent(this, ActivityCreateTask.class);
                startActivity(intent);
                break;
        }
        finish();
    }

    @Override
    protected void updateUi(Response response) {

    }
}
