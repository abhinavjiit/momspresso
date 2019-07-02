package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.request.UpdatePostContentRequest;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 14/8/18.
 */

public class GroupsEditPostActivity extends BaseActivity implements View.OnClickListener {

    private EditText postContentEditText;
    private ImageView closeEditorImageView;
    private GroupPostResult postData;
    private TextView publishTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_edit_post_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        postContentEditText = (EditText) findViewById(R.id.postContentEditText);
        closeEditorImageView = (ImageView) findViewById(R.id.closeEditorImageView);
        publishTextView = (TextView) findViewById(R.id.publishTextView);

        closeEditorImageView.setOnClickListener(this);
        publishTextView.setOnClickListener(this);

        postData = getIntent().getParcelableExtra("postData");

        postContentEditText.setText(postData.getContent());

        postContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    publishTextView.setEnabled(false);
                } else {
                    publishTextView.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeEditorImageView:
                onBackPressed();
                break;
            case R.id.publishTextView:
                Log.d("publish", "publish");

                Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                UpdatePostContentRequest updatePostRequest = new UpdatePostContentRequest();
                updatePostRequest.setContent(postContentEditText.getText().toString());

                Call<GroupPostResponse> call = groupsAPI.updatePostContent(postData.getId(), updatePostRequest);
                call.enqueue(updatePostContentResponseCallback);
                break;
        }
    }

    private Callback<GroupPostResponse> updatePostContentResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    Intent intent = getIntent();
                    intent.putExtra("updatedContent", groupPostResponse.getData().get(0).getResult().get(0).getContent());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
