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
import com.facebook.share.Share;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.GroupNotificationToggleRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.request.UpdateUsersGpLevelNotificationSettingRequest;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/4/18.
 */

public class GroupSettingsActivity extends BaseActivity implements View.OnClickListener {

    private UserPostSettingResult currentGpPrefsForUser;
    GroupResult groupItem;

    private RelativeLayout leaveGroupContainer, reportedContentContainer;
    private Switch disableNotificationSwitch;
    private ImageView editGroupImageView;
    private TextView memberCountTextView;
    private Toolbar toolbar;
    private String memberType;
    private ImageView groupImageView;
    private TextView groupNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_settings_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        disableNotificationSwitch = (Switch) findViewById(R.id.disableNotificationSwitch);
        leaveGroupContainer = (RelativeLayout) findViewById(R.id.leaveGroupContainer);
        groupImageView = (ImageView) findViewById(R.id.groupImageView);
        editGroupImageView = (ImageView) findViewById(R.id.editGroupImageView);
        reportedContentContainer = (RelativeLayout) findViewById(R.id.reportedContentContainer);
        memberCountTextView = (TextView) findViewById(R.id.memberCountTextView);
        groupNameTextView = (TextView) findViewById(R.id.groupNameTextView);

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
            leaveGroupContainer.setVisibility(View.GONE);
            if (groupItem.getNotificationOn() == 1) {
                disableNotificationSwitch.setChecked(false);
            } else {
                disableNotificationSwitch.setChecked(true);
            }
        } else if (AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
            reportedContentContainer.setVisibility(View.VISIBLE);
            editGroupImageView.setVisibility(View.VISIBLE);
            memberCountTextView.setOnClickListener(this);
            if (groupItem.getNotificationOn() == 1) {
                disableNotificationSwitch.setChecked(false);
            } else {
                disableNotificationSwitch.setChecked(true);
            }
        } else {
            getNotificationSettingForCurrentUser();
        }


        memberCountTextView.setText("" + groupItem.getMemberCount());

        groupNameTextView.setText(groupItem.getTitle());
        Picasso.with(this).load(groupItem.getHeaderImage())
                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(groupImageView);
    }

    private void getNotificationSettingForCurrentUser() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<UserPostSettingResponse> call = groupsAPI.getGroupNotificationSettingForUser(groupItem.getId(), 0, SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(userGpNotificationSettingResponseCallback);
    }

    private Callback<UserPostSettingResponse> userGpNotificationSettingResponseCallback = new Callback<UserPostSettingResponse>() {
        @Override
        public void onResponse(Call<UserPostSettingResponse> call, retrofit2.Response<UserPostSettingResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    UserPostSettingResponse userPostSettingResponse = response.body();
                    if (userPostSettingResponse.getData().get(0).getResult().isEmpty()) {
                        disableNotificationSwitch.setChecked(true);
                        currentGpPrefsForUser = null;
                    } else if (userPostSettingResponse.getData().get(0).getResult().get(0).getNotificationOff() == 1) {
                        disableNotificationSwitch.setChecked(true);
                        currentGpPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
                    } else {
                        disableNotificationSwitch.setChecked(false);
                        currentGpPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
                    }
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.memberCountTextView: {
                Intent intent = new Intent(GroupSettingsActivity.this, GroupMembershipActivity.class);
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
                if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType) || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
                    GroupNotificationToggleRequest groupNotificationToggleRequest = new GroupNotificationToggleRequest();
                    groupNotificationToggleRequest.setNotificationOn(disableNotificationSwitch.isChecked() ? 0 : 1);
                    Call<GroupDetailResponse> call = groupsAPI.updateGroupNotification(groupItem.getId(), groupNotificationToggleRequest);
                    call.enqueue(notificationUpdateResponseCallback);
                } else {
                    UpdateUsersGpLevelNotificationSettingRequest request = new UpdateUsersGpLevelNotificationSettingRequest();
                    request.setPostId(0);
                    request.setIsAnno(0);
                    request.setIsBookmarked(0);
                    request.setGroupId(groupItem.getId());
                    request.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
                    request.setNotificationOff(disableNotificationSwitch.isChecked() ? 1 : 0);

                    if (currentGpPrefsForUser == null) {
                        Call<ResponseBody> call = groupsAPI.createNewGpSettingsForUser(request);
                        call.enqueue(gpLevelNotificationResponseCallback);
                    } else {
                        Call<UserPostSettingResponse> call = groupsAPI.updateNotificationSettingsOfGpForUser(currentGpPrefsForUser.getId(), request);
                        call.enqueue(patchGpLevelNotificationResponseCallback);
                    }
                }
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

    private Callback<UserPostSettingResponse> patchGpLevelNotificationResponseCallback = new Callback<UserPostSettingResponse>() {
        @Override
        public void onResponse(Call<UserPostSettingResponse> call, retrofit2.Response<UserPostSettingResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    UserPostSettingResponse userPostSettingResponse = response.body();

                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<ResponseBody> gpLevelNotificationResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    String resData = new String(response.body().bytes());
                    JSONObject jObject = new JSONObject(resData);
                    currentGpPrefsForUser = new UserPostSettingResult();
                    currentGpPrefsForUser.setId(jObject.getJSONObject("data").getJSONObject("result").getInt("id"));
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void patchNotificationSettingForGroup(int patchActionId, String patchActionType) {

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
