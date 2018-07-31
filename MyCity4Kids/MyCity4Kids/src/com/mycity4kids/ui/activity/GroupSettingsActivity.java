package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.GroupNotificationToggleRequest;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/4/18.
 */

public class GroupSettingsActivity extends BaseActivity implements View.OnClickListener {

    GroupResult groupItem;

    private RelativeLayout leaveGroupContainer, reportedContentContainer;
    private Switch disableNotificationSwitch;
    private ImageView editGroupImageView;
    private TextView memberCountTextView;
    private Toolbar toolbar;
    private String memberType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_settings_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        disableNotificationSwitch = (Switch) findViewById(R.id.disableNotificationSwitch);
        leaveGroupContainer = (RelativeLayout) findViewById(R.id.leaveGroupContainer);
        editGroupImageView = (ImageView) findViewById(R.id.editGroupImageView);
        reportedContentContainer = (RelativeLayout) findViewById(R.id.reportedContentContainer);
        memberCountTextView = (TextView) findViewById(R.id.memberCountTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupItem = (GroupResult) getIntent().getParcelableExtra("groupItem");
        memberType = getIntent().getStringExtra(AppConstants.GROUP_MEMBER_TYPE);

        disableNotificationSwitch.setOnClickListener(this);
        leaveGroupContainer.setOnClickListener(this);
        editGroupImageView.setOnClickListener(this);
        reportedContentContainer.setOnClickListener(this);

        if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)) {
            reportedContentContainer.setVisibility(View.VISIBLE);
            editGroupImageView.setVisibility(View.VISIBLE);
            memberCountTextView.setOnClickListener(this);
        } else if (AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
            reportedContentContainer.setVisibility(View.VISIBLE);
            editGroupImageView.setVisibility(View.VISIBLE);
        }

        if (groupItem.getNotificationOn() == 1) {
            disableNotificationSwitch.setChecked(false);
        } else {
            disableNotificationSwitch.setChecked(true);
        }

        memberCountTextView.setText("" + groupItem.getMemberCount());
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.memberCountTextView: {
                Intent intent = new Intent(GroupSettingsActivity.this, GroupMembershipRequestActivity.class);
                intent.putExtra("groupId", groupItem.getId());
                startActivity(intent);
            }
            break;
            case R.id.editGroupImageView: {
                Intent intent = new Intent(GroupSettingsActivity.this, EditGroupActivity.class);
                intent.putExtra("groupId", groupItem.getId());
                startActivity(intent);
            }
            break;
            case R.id.disableNotificationSwitch:
                Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                GroupNotificationToggleRequest groupNotificationToggleRequest = new GroupNotificationToggleRequest();
                groupNotificationToggleRequest.setNotificationOn(disableNotificationSwitch.isChecked() ? 0 : 1);
                Call<GroupDetailResponse> call = groupsAPI.updateGroupNotification(groupItem.getId(), groupNotificationToggleRequest);
                call.enqueue(notificationUpdateResponseCallback);
                break;
            case R.id.reportedContentContainer: {
                Intent intent = new Intent(GroupSettingsActivity.this, GroupsReportedContentActivity.class);
                intent.putExtra("groupId", groupItem.getId());
                startActivity(intent);
            }
            break;
            case R.id.leaveGroupContainer: {
                Intent intent = new Intent(GroupSettingsActivity.this, LeaveGroupActivity.class);
                intent.putExtra("groupItem", groupItem);
                startActivity(intent);
            }
            break;
        }
    }

    private Callback<GroupDetailResponse> notificationUpdateResponseCallback = new Callback<GroupDetailResponse>() {
        @Override
        public void onResponse(Call<GroupDetailResponse> call, retrofit2.Response<GroupDetailResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupDetailResponse groupPostResponse = response.body();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
