package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.GroupNotificationToggleRequest;
import com.mycity4kids.models.request.UpdateUsersGpLevelNotificationSettingRequest;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.squareup.picasso.Picasso;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/4/18.
 */

public class GroupSettingsActivity extends BaseActivity implements View.OnClickListener {

    private UserPostSettingResult currentGpPrefsForUser;
    private GroupResult groupItem;
    private RelativeLayout leaveGroupContainer;
    private RelativeLayout reportedContentContainer;
    private SwitchCompat disableNotificationSwitch;
    private ImageView editGroupImageView;
    private TextView memberCountTextView;
    private Toolbar toolbar;
    private String memberType;
    private ImageView groupImageView;
    private TextView groupNameTextView;
    private RelativeLayout inviteMemberContainer;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_settings_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        disableNotificationSwitch = (SwitchCompat) findViewById(R.id.disableNotificationSwitch);
        leaveGroupContainer = (RelativeLayout) findViewById(R.id.leaveGroupContainer);
        groupImageView = (ImageView) findViewById(R.id.groupImageView);
        editGroupImageView = (ImageView) findViewById(R.id.editGroupImageView);
        reportedContentContainer = (RelativeLayout) findViewById(R.id.reportedContentContainer);
        inviteMemberContainer = (RelativeLayout) findViewById(R.id.inviteMemberContainer);
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
        inviteMemberContainer.setOnClickListener(this);

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
        Picasso.get().load(groupItem.getHeaderImage())
                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(groupImageView);
    }

    private void getNotificationSettingForCurrentUser() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

        Call<UserPostSettingResponse> call = groupsApi.getGroupNotificationSettingForUser(groupItem.getId(), 0,
                SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(userGpNotificationSettingResponseCallback);
    }

    private Callback<UserPostSettingResponse> userGpNotificationSettingResponseCallback =
            new Callback<UserPostSettingResponse>() {
                @Override
                public void onResponse(Call<UserPostSettingResponse> call,
                        retrofit2.Response<UserPostSettingResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            UserPostSettingResponse userPostSettingResponse = response.body();
                            if (userPostSettingResponse.getData().get(0).getResult().isEmpty()) {
                                disableNotificationSwitch.setChecked(true);
                                currentGpPrefsForUser = null;
                            } else if (userPostSettingResponse.getData().get(0).getResult().get(0).getNotificationOff()
                                    == 1) {
                                disableNotificationSwitch.setChecked(true);
                                currentGpPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
                            } else {
                                disableNotificationSwitch.setChecked(false);
                                currentGpPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
                            }
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.inviteMemberContainer: {
                MixpanelAPI mixpanel = MixpanelAPI
                        .getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    jsonObject.put("groupId", "" + groupItem.getId());
                    mixpanel.track("GroupInvite", jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppConstants.WEB_URL + groupItem.getUrl();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        groupItem.getDescription() + "\n\n" + "Join " + groupItem.getTitle() + " support group\n"
                                + shareUrl);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
            }
            break;
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
                GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
                if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType) || AppConstants.GROUP_MEMBER_TYPE_MODERATOR
                        .equals(memberType)) {
                    GroupNotificationToggleRequest groupNotificationToggleRequest =
                            new GroupNotificationToggleRequest();
                    groupNotificationToggleRequest.setNotificationOn(disableNotificationSwitch.isChecked() ? 0 : 1);
                    Call<GroupDetailResponse> call = groupsApi
                            .updateGroupNotification(groupItem.getId(), groupNotificationToggleRequest);
                    call.enqueue(notificationUpdateResponseCallback);
                } else {
                    UpdateUsersGpLevelNotificationSettingRequest request =
                            new UpdateUsersGpLevelNotificationSettingRequest();
                    request.setPostId(0);
                    request.setIsAnno(0);
                    request.setIsBookmarked(0);
                    request.setGroupId(groupItem.getId());
                    request.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
                    request.setNotificationOff(disableNotificationSwitch.isChecked() ? 1 : 0);

                    if (currentGpPrefsForUser == null) {
                        Call<ResponseBody> call = groupsApi.createNewGpSettingsForUser(request);
                        call.enqueue(gpLevelNotificationResponseCallback);
                    } else {
                        Call<UserPostSettingResponse> call = groupsApi
                                .updateNotificationSettingsOfGpForUser(currentGpPrefsForUser.getId(), request);
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
                MixpanelAPI mixpanel = MixpanelAPI
                        .getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    jsonObject.put("groupId", "" + groupItem.getId());
                    mixpanel.track("LeaveGroup", jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(GroupSettingsActivity.this, LeaveGroupActivity.class);
                intent.putExtra("groupItem", groupItem);
                startActivity(intent);
            }
            break;
            default:
                break;
        }
    }

    private Callback<UserPostSettingResponse> patchGpLevelNotificationResponseCallback =
            new Callback<UserPostSettingResponse>() {
                @Override
                public void onResponse(Call<UserPostSettingResponse> call,
                        retrofit2.Response<UserPostSettingResponse> response) {
                }

                @Override
                public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private Callback<ResponseBody> gpLevelNotificationResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    String resData = new String(response.body().bytes());
                    JSONObject jsonObject = new JSONObject(resData);
                    currentGpPrefsForUser = new UserPostSettingResult();
                    currentGpPrefsForUser.setId(jsonObject.getJSONObject("data").getJSONObject("result").getInt("id"));
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<GroupDetailResponse> notificationUpdateResponseCallback = new Callback<GroupDetailResponse>() {
        @Override
        public void onResponse(Call<GroupDetailResponse> call, retrofit2.Response<GroupDetailResponse> response) {
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
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
