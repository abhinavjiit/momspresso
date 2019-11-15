package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatSpinner;

public class ReportSpamActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backImageView;
    private AppCompatSpinner spamSpinner;
    private TextView sendTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_spam);
        backImageView = findViewById(R.id.backImageView);
        spamSpinner = (AppCompatSpinner) findViewById(R.id.spamSpinner);
        sendTextView = findViewById(R.id.sendTextView);

        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(this, genderList, "ReportSpam");
        spamSpinner.setAdapter(spinAdapter);
        spamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                String item = adapter.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        backImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.sendTextView:
                break;
        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
