package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.request.ReportSpamRequest;
import com.mycity4kids.retrofitAPIsInterfaces.ReportSpamAPI;
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ReportSpamActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backImageView;
    private AppCompatSpinner spamSpinner;
    private TextView sendTextView;
    private EditText spamEdtTxt;
    private String item;
    private int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_spam);
        backImageView = findViewById(R.id.backImageView);
        spamSpinner = (AppCompatSpinner) findViewById(R.id.spamSpinner);
        sendTextView = findViewById(R.id.sendTextView);
        spamEdtTxt = findViewById(R.id.repost_spam_text_edittext);

        ArrayList<String> spamTopicList = new ArrayList<>();
        spamTopicList.add("Select your topic…");
        spamTopicList.add("Male");
        spamTopicList.add("Female");

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(this, spamTopicList);
        spamSpinner.setAdapter(spinAdapter);
        spamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                item = adapter.getItemAtPosition(position).toString();
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                item = null;
            }
        });
        backImageView.setOnClickListener(this);
        sendTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.sendTextView:
                if (isValid()) {
                    postSpam();
                }
                break;
        }
    }

    private boolean isValid() {
        boolean isValid = true;
        if (pos == 0) {
            isValid = false;
            Toast.makeText(this, R.string.please_select, Toast.LENGTH_SHORT).show();
        }else if (StringUtils.isNullOrEmpty(spamEdtTxt.getText().toString())) {
            isValid = false;
            Toast.makeText(this, R.string.editor_body_empty, Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    private void postSpam() {
        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ReportSpamAPI reportSpamAPI = retrofit.create(ReportSpamAPI.class);
        ReportSpamRequest reportSpamRequest = new ReportSpamRequest();
        reportSpamRequest.setSubject(item);
        reportSpamRequest.setBody(spamEdtTxt.getText().toString());
        Call<ResponseBody> call = reportSpamAPI.reportSpam(reportSpamRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                removeProgressDialog();
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                removeProgressDialog();
            }
        });
    }

    @Override
    protected void updateUi(Response response) {

    }
}
