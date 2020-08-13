package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.internal.LinkedTreeMap;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.GroupNotificationToggleRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.request.UpdateUsersGpLevelNotificationSettingRequest;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.MomspressoButtonWidget;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private RelativeLayout expectedDateContainer;
    private TextView expectedDateTextView;
    private MomspressoButtonWidget editExpectedDateWidget;
    private String expectedDate;
    private int currentUserMembershipId;
    private Dialog dialog;
    private String groupQuestionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_settings_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = findViewById(R.id.toolbar);
        disableNotificationSwitch = findViewById(R.id.disableNotificationSwitch);
        leaveGroupContainer = findViewById(R.id.leaveGroupContainer);
        groupImageView = findViewById(R.id.groupImageView);
        editGroupImageView = findViewById(R.id.editGroupImageView);
        reportedContentContainer = findViewById(R.id.reportedContentContainer);
        inviteMemberContainer = findViewById(R.id.inviteMemberContainer);
        memberCountTextView = findViewById(R.id.memberCountTextView);
        groupNameTextView = findViewById(R.id.groupNameTextView);
        expectedDateContainer = findViewById(R.id.expectedDateContainer);
        expectedDateTextView = findViewById(R.id.expectedDateTextView);
        editExpectedDateWidget = findViewById(R.id.editExpectedDateWidget);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupItem = getIntent().getParcelableExtra("groupItem");
        memberType = getIntent().getStringExtra(AppConstants.GROUP_MEMBER_TYPE);

        if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra("expectedDate"))) {
            expectedDateContainer.setVisibility(View.VISIBLE);
            expectedDate = getIntent().getStringExtra("expectedDate");
            currentUserMembershipId = getIntent().getIntExtra("currentUserMembershipId", 0);
            expectedDateTextView.setText(DateTimeUtils.getDOBMilliTimestamp(expectedDate));
        }

        disableNotificationSwitch.setOnClickListener(this);
        leaveGroupContainer.setOnClickListener(this);
        editGroupImageView.setOnClickListener(this);
        reportedContentContainer.setOnClickListener(this);
        inviteMemberContainer.setOnClickListener(this);
        editExpectedDateWidget.setOnClickListener(this);

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
            case R.id.editExpectedDateWidget: {
                showExpectedDateDialog();
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

    private void showExpectedDateDialog() {
        dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_expecting_mom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        MomspressoButtonWidget continueButtonWidget = dialog.findViewById(R.id.continueButtonWidget);
        ImageView cancelDialog = dialog.findViewById(R.id.cancel);
        continueButtonWidget
                .setText(org.apache.commons.lang3.StringUtils
                        .capitalize(getString(R.string.dialog_continue).toLowerCase()));
        MomspressoButtonWidget dateButtonWidget = dialog.findViewById(R.id.dateButtonWidget);
        dateButtonWidget.setGravity(Gravity.START);
        dateButtonWidget.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DatePickerDialog picker = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (view, year1, monthOfYear, dayOfMonth) -> dateButtonWidget
                            .setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1), year, month, day);
            Date referenceDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(referenceDate);
            picker.getDatePicker().setMinDate(c.getTimeInMillis() - 10000);
            c.add(Calendar.MONTH, 9);
            picker.getDatePicker().setMaxDate(c.getTimeInMillis());
            picker.show();
        });

        continueButtonWidget.setOnClickListener(view -> {
            if (getString(R.string.rewards_expected_date).equals(dateButtonWidget.getText().toString())) {
                showToast("Please select a valid date");
            } else {
                String date = dateButtonWidget.getText().toString();
                long timeInMillis = DateTimeUtils.convertStringToMilliTimestamp(date);
                if (timeInMillis == 0) {
                    showToast("Please select a valid date");
                } else {
                    UserDetailResult userDetailResult = new UserDetailResult();
                    userDetailResult.setIsExpected("1");
                    userDetailResult.setExpectedDate("" + timeInMillis);
                    saveExpectedDateOrKidsToUserDashboard(userDetailResult);
                }
            }
        });
        cancelDialog.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void saveExpectedDateOrKidsToUserDashboard(UserDetailResult userDetailResult) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI updateUserDetailApi = retrofit.create(LoginRegistrationAPI.class);
        Call<RewardsPersonalResponse> call = updateUserDetailApi
                .updateUserDetails(SharedPrefUtils.getUserDetailModel(this).getDynamoId(),
                        userDetailResult);
        call.enqueue(new Callback<RewardsPersonalResponse>() {
            @Override
            public void onResponse(Call<RewardsPersonalResponse> call,
                    Response<RewardsPersonalResponse> response) {
                if (response.body() == null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                    return;
                }
                try {
                    if (response.isSuccessful()) {
                        RewardsPersonalResponse updateUserResponse = response.body();
                        if (updateUserResponse.getCode() == 200 && Constants.SUCCESS
                                .equals(updateUserResponse.getStatus())) {
                            saveExpectedDateOrYoungestChildDobToMembership(userDetailResult.getExpectedDate(),
                                    "expectedDate");
                        } else {
                            showToast(updateUserResponse.getReason());
                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<RewardsPersonalResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    private void saveExpectedDateOrYoungestChildDobToMembership(String timeInMillis, String responseQuestionnaireKey) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        UpdateGroupMembershipRequest updateRequest = new UpdateGroupMembershipRequest();
        updateRequest.setUserId(
                SharedPrefUtils.getUserDetailModel(this)
                        .getDynamoId());
        Map<String, String> map = new LinkedTreeMap<>();
        map.put(responseQuestionnaireKey, timeInMillis);
        updateRequest.setQuestionnaireResponse(map);
        Call<GroupsMembershipResponse> groupsCall = groupsApi
                .updateMember(currentUserMembershipId, updateRequest);
        groupsCall.enqueue(updateGroupMembershipResponseCallback);
    }

    private Callback<GroupsMembershipResponse> updateGroupMembershipResponseCallback =
            new Callback<GroupsMembershipResponse>() {
                @Override
                public void onResponse(Call<GroupsMembershipResponse> call,
                        retrofit2.Response<GroupsMembershipResponse> response) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            GroupsMembershipResponse groupsMembershipResponse = response.body();
                            expectedDateTextView.setText(DateTimeUtils
                                    .getDOBMilliTimestamp(groupsMembershipResponse.getData().getResult().get(0)
                                            .getQuestionnaireResponse().get("expectedDate")));
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            };
}
