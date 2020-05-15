package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.internal.LinkedTreeMap;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.JoinGroupRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupSummaryPostRecyclerAdapter;
import com.mycity4kids.ui.fragment.GroupJoinConfirmationFragment;
import com.mycity4kids.utils.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsSummaryActivity extends BaseActivity implements View.OnClickListener,
        GroupSummaryPostRecyclerAdapter.RecyclerViewClickListener {

    private GroupSummaryPostRecyclerAdapter groupSummaryPostRecyclerAdapter;
    private GroupResult selectedGroup;
    private ArrayList<GroupPostResult> postList;
    private int skip = 0;
    private int groupId;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView joinGroupTextView;
    private ProgressBar progressBar;
    private TextView noPostsTextView;
    private LinearLayout postSettingsContainer;
    private RelativeLayout postSettingsContainerMain;
    private View overlayView;
    private TextView savePostTextView;
    private TextView notificationToggleTextView;
    private TextView commentToggleTextView;
    private TextView reportPostTextView;
    private LinkedTreeMap<String, String> questionMap;
    private boolean pendingMembershipFlag;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_summary_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "GroupSummaryScreen",
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        joinGroupTextView = (TextView) findViewById(R.id.joinGroupTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noPostsTextView = (TextView) findViewById(R.id.noPostsTextView);
        postSettingsContainer = (LinearLayout) findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = (RelativeLayout) findViewById(R.id.postSettingsContainerMain);
        overlayView = findViewById(R.id.overlayView);
        savePostTextView = (TextView) findViewById(R.id.savePostTextView);
        notificationToggleTextView = (TextView) findViewById(R.id.notificationToggleTextView);
        commentToggleTextView = (TextView) findViewById(R.id.commentToggleTextView);
        reportPostTextView = (TextView) findViewById(R.id.reportPostTextView);

        joinGroupTextView.setOnClickListener(this);
        savePostTextView.setOnClickListener(this);
        notificationToggleTextView.setOnClickListener(this);
        commentToggleTextView.setOnClickListener(this);
        reportPostTextView.setOnClickListener(this);
        overlayView.setOnClickListener(this);

        groupId = getIntent().getIntExtra("groupId", 0);
        pendingMembershipFlag = getIntent().getBooleanExtra("pendingMembershipFlag", false);

        if (pendingMembershipFlag) {
            joinGroupTextView.setOnClickListener(null);
            joinGroupTextView.setText(getString(R.string.groups_membership_pending));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setNestedScrollingEnabled(false);

        postList = new ArrayList<>();
        postList.add(new GroupPostResult());

        getGroupDetails();
    }

    private void getGroupDetails() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupDetailResponse> call = groupsApi.getGroupById(groupId);
        call.enqueue(groupDetailsResponseCallback);
    }

    private Callback<GroupDetailResponse> groupDetailsResponseCallback = new Callback<GroupDetailResponse>() {
        @Override
        public void onResponse(Call<GroupDetailResponse> call, retrofit2.Response<GroupDetailResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupDetailResponse groupPostResponse = response.body();
                    selectedGroup = groupPostResponse.getData().getResult();
                    questionMap = (LinkedTreeMap<String, String>) groupPostResponse.getData().getResult()
                            .getQuestionnaire();
                    groupSummaryPostRecyclerAdapter = new GroupSummaryPostRecyclerAdapter(GroupsSummaryActivity.this,
                            GroupsSummaryActivity.this);
                    groupSummaryPostRecyclerAdapter.setData(postList);
                    groupSummaryPostRecyclerAdapter.setHeaderData(selectedGroup);
                    recyclerView.setAdapter(groupSummaryPostRecyclerAdapter);
                    getGroupPosts();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupPostResponse> call = groupsApi.getAllPostsForAGroup(selectedGroup.getId(), 0, 10);
        call.enqueue(groupPostResponseCallback);
    }

    private Callback<GroupPostResponse> groupPostResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    processPostListingResponse(groupPostResponse);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processPostListingResponse(GroupPostResponse response) {
        ArrayList<GroupPostResult> dataList = (ArrayList<GroupPostResult>) response.getData().get(0).getResult();
        if (dataList.size() != 0) {
            formatPostData(dataList);
            noPostsTextView.setVisibility(View.GONE);
            postList.addAll(dataList);
            groupSummaryPostRecyclerAdapter.setHeaderData(selectedGroup);
            groupSummaryPostRecyclerAdapter.setData(postList);
            int limit = 10;
            skip = skip + limit;
            groupSummaryPostRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void formatPostData(ArrayList<GroupPostResult> dataList) {
        for (int j = 0; j < dataList.size(); j++) {
            if (dataList.get(j).getCounts() != null) {
                for (int i = 0; i < dataList.get(j).getCounts().size(); i++) {
                    switch (dataList.get(j).getCounts().get(i).getName()) {
                        case "helpfullCount":
                            dataList.get(j).setHelpfullCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "notHelpfullCount":
                            dataList.get(j).setNotHelpfullCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "responseCount":
                            dataList.get(j).setResponseCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "votesCount": {
                            for (int k = 0; k < dataList.get(j).getCounts().get(i).getCounts().size(); k++) {
                                dataList.get(j).setTotalVotesCount(
                                        dataList.get(j).getTotalVotesCount() + dataList.get(j).getCounts().get(i)
                                                .getCounts().get(k).getCount());
                                switch (dataList.get(j).getCounts().get(i).getCounts().get(k).getName()) {
                                    case "option1":
                                        dataList.get(j).setOption1VoteCount(
                                                dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option2":
                                        dataList.get(j).setOption2VoteCount(
                                                dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option3":
                                        dataList.get(j).setOption3VoteCount(
                                                dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option4":
                                        dataList.get(j).setOption4VoteCount(
                                                dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
            case R.id.joinGroupTextView:
                Utils.groupsEvent(GroupsSummaryActivity.this, "other groups_group card", "Join group", "android",
                        SharedPrefUtils.getAppLocale(GroupsSummaryActivity.this),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Groups Discussion page", "", "");
                joinGroupRequest();
                break;
            default:
                break;
        }
    }

    private void joinGroupRequest() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        if (selectedGroup == null) {
            return;
        }
        if (AppConstants.GROUP_TYPE_OPEN_KEY.equals(selectedGroup.getType())) {
            JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
            joinGroupRequest.setGroupId(selectedGroup.getId());
            joinGroupRequest.setUserId(SharedPrefUtils.getUserDetailModel(GroupsSummaryActivity.this).getDynamoId());
            Call<BaseResponse> call = groupsApi.createMember(joinGroupRequest);
            call.enqueue(groupJoinResponseCallback);
        } else {
            if (questionMap == null || questionMap.isEmpty()) {
                JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
                joinGroupRequest.setGroupId(selectedGroup.getId());
                joinGroupRequest
                        .setUserId(SharedPrefUtils.getUserDetailModel(GroupsSummaryActivity.this).getDynamoId());
                Call<BaseResponse> call = groupsApi.createMember(joinGroupRequest);
                call.enqueue(groupJoinResponseCallback);
            } else {
                Intent intent = new Intent(GroupsSummaryActivity.this, GroupsQuestionnaireActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                intent.putExtra("questionnaire", questionMap);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.memberCountTextView:
                joinAndBecomeAdmin();
                break;
            default:
                break;
        }
    }

    private void joinAndBecomeAdmin() {
    }

    private Callback<BaseResponse> groupJoinResponseCallback = new Callback<BaseResponse>() {
        @Override
        public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
            progressBar.setVisibility(View.GONE);
            if (response.body() == null) {
                if (response.code() == 400) {
                    try {
                        String errorBody = new String(response.errorBody().bytes());
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String reason = jsonObject.getString("reason");
                        if (!StringUtils.isNullOrEmpty(reason) && "already member".equals(reason)) {
                            String status = jsonObject.getJSONObject("data").getJSONArray("data").getJSONObject(0)
                                    .getString("status");
                            switch (status) {
                                case AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED:
                                    showToast(getString(R.string.groups_user_blocked_msg));
                                    break;
                                case AppConstants.GROUP_MEMBERSHIP_STATUS_LEFT:
                                    patchMembershipRequest(
                                            jsonObject.getJSONObject("data").getJSONArray("data").getJSONObject(0)
                                                    .getInt("id"));
                                    break;
                                case AppConstants.GROUP_MEMBERSHIP_STATUS_REJECTED:
                                    showToast(getString(R.string.groups_user_membership_rejected_msg));
                                    break;
                                default:
                                    break;
                            }
                        }
                    } catch (IOException | JSONException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                } else if (response.code() == 409) {
                    try {
                        String errorBody = new String(response.errorBody().bytes());
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String reason = jsonObject.getString("reason");
                        if (!StringUtils.isNullOrEmpty(reason) && "already member".equals(reason)) {
                            Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                            GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

                            UpdateGroupMembershipRequest updateGroupMembershipRequest =
                                    new UpdateGroupMembershipRequest();
                            updateGroupMembershipRequest.setUserId(
                                    jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0)
                                            .getString("userId"));
                            updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER);
                            Call<GroupsMembershipResponse> call1 = groupsApi.updateMember(
                                    jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0)
                                            .getInt("id"),
                                    updateGroupMembershipRequest);
                            call1.enqueue(groupRejoinResponseCallback);
                        }
                    } catch (IOException | JSONException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    MixpanelAPI mixpanel = MixpanelAPI
                            .getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("groupId", "" + groupId);
                        mixpanel.track("JoinGroup", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (AppConstants.GROUP_TYPE_OPEN_KEY.equals(selectedGroup.getType())) {
                        Intent intent = new Intent(GroupsSummaryActivity.this, GroupDetailsActivity.class);
                        intent.putExtra("groupId", selectedGroup.getId());
                        intent.putExtra("justJoined", true);
                        startActivity(intent);
                        finish();
                    } else {
                        showSuccessDialog();
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<GroupsMembershipResponse> groupRejoinResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call,
                retrofit2.Response<GroupsMembershipResponse> response) {
            progressBar.setVisibility(View.GONE);
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    MixpanelAPI mixpanel = MixpanelAPI
                            .getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("groupId", "" + groupId);
                        mixpanel.track("JoinGroup", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (AppConstants.GROUP_TYPE_OPEN_KEY.equals(selectedGroup.getType())) {
                        Intent intent = new Intent(GroupsSummaryActivity.this, GroupDetailsActivity.class);
                        intent.putExtra("groupId", selectedGroup.getId());
                        startActivity(intent);
                        finish();
                    } else {
                        showSuccessDialog();
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void patchMembershipRequest(int membershipId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

        UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
        updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER);
        Call<GroupsMembershipResponse> call1 = groupsApi.updateMember(membershipId, updateGroupMembershipRequest);
        call1.enqueue(updateGroupMemberRoleResponseCallback);
    }

    private Callback<GroupsMembershipResponse> updateGroupMemberRoleResponseCallback =
            new Callback<GroupsMembershipResponse>() {
                @Override
                public void onResponse(Call<GroupsMembershipResponse> call,
                        retrofit2.Response<GroupsMembershipResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        showToast("Failed to update membership");
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            if (AppConstants.GROUP_TYPE_OPEN_KEY.equals(selectedGroup.getType())) {
                                showToast("Group Join success");
                                Intent intent = new Intent(GroupsSummaryActivity.this, GroupDetailsActivity.class);
                                intent.putExtra("groupId", selectedGroup.getId());
                                intent.putExtra("source", "questionnaire");
                                startActivity(intent);
                                finish();
                            } else {
                                showSuccessDialog();
                            }
                        } else {
                            showToast("Group Join Fail");
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
                    showToast("Failed to update membership");
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private void showSuccessDialog() {
        GroupJoinConfirmationFragment groupJoinConfirmationFragment = new GroupJoinConfirmationFragment();
        Bundle args = new Bundle();
        args.putParcelable("groupItem", selectedGroup);
        groupJoinConfirmationFragment.setArguments(args);
        groupJoinConfirmationFragment.setCancelable(true);
        FragmentManager fm = getSupportFragmentManager();
        groupJoinConfirmationFragment.show(fm, "Group Join Request");
    }

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



