package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.JoinGroupRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsJoinResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.GroupsSettingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupsQuestionnaireRecyclerAdapter;
import com.mycity4kids.ui.fragment.GroupJoinConfirmationFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsQuestionnaireActivity extends BaseActivity implements View.OnClickListener, GroupsQuestionnaireRecyclerAdapter.RecyclerViewClickListener {

    private ArrayList<String> questionList;
    private GroupResult selectedGroup;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private GroupsQuestionnaireRecyclerAdapter groupsQuestionnaireRecyclerAdapter;
    private TextView joinGroupTextView;
    private ProgressBar progressBar;
    private HashMap<String, String> qMap;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_questioannaire_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        joinGroupTextView = (TextView) findViewById(R.id.joinGroupTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        selectedGroup = getIntent().getParcelableExtra("groupItem");
        qMap = (HashMap<String, String>) getIntent().getSerializableExtra("questionnaire");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        questionList = new ArrayList<>();

        for (Map.Entry entry : qMap.entrySet()) {
            questionList.add(entry.getValue().toString());
        }
//        arrayList.add("one");
//        arrayList.add("two");
//        arrayList.add("three");
//        arrayList.add("four");
//        arrayList.add("five");
//        arrayList.add("six");
//        arrayList.add("12one");
//        arrayList.add("12two");
//        arrayList.add("12three");
//        arrayList.add("12four");
//        arrayList.add("12five");
//        arrayList.add("12six");
//        arrayList.add("124one");
//        arrayList.add("124two");
//        arrayList.add("124three");
//        arrayList.add("124four");
//        arrayList.add("124five");
//        arrayList.add("124six");

        groupsQuestionnaireRecyclerAdapter = new GroupsQuestionnaireRecyclerAdapter(this, this);
        groupsQuestionnaireRecyclerAdapter.setData(questionList);
        recyclerView.setAdapter(groupsQuestionnaireRecyclerAdapter);

        joinGroupTextView.setOnClickListener(this);

//        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
//        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
//        Call<GroupsSettingResponse> call = groupsAPI.getGroupsAllSetting(selectedGroup.getId());
//        call.enqueue(groupSettingsResponseCallback);
    }

    private Callback<GroupsSettingResponse> groupSettingsResponseCallback = new Callback<GroupsSettingResponse>() {
        @Override
        public void onResponse(Call<GroupsSettingResponse> call, retrofit2.Response<GroupsSettingResponse> response) {
            progressBar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsSettingResponse responseModel = response.body();
                    processGroupsQuestionnaire(responseModel);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsSettingResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processGroupsQuestionnaire(GroupsSettingResponse responseModel) {
        for (Map.Entry<String, String> entry : responseModel.getData().get(0).getResult().get(0).getQuestionnaire().entrySet()) {
            questionList.add(entry.getValue());
        }
        groupsQuestionnaireRecyclerAdapter.setData(questionList);
        groupsQuestionnaireRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.joinGroupTextView:
                Log.d("JoinGp", "joinGp");
                if (validateAnswers()) {
                    showToast("Please enter answers to all questions");
                    return;
                }
                updateUsersMembership();
                break;
        }


//        GroupJoinConfirmationFragment groupJoinConfirmationFragment = new GroupJoinConfirmationFragment();
//        FragmentManager fm = getSupportFragmentManager();
//        Bundle _args = new Bundle();
//        _args.putString("activity", "dashboard");
//        groupJoinConfirmationFragment.setArguments(_args);
//        groupJoinConfirmationFragment.setCancelable(true);
//        groupJoinConfirmationFragment.show(fm, "Choose video option");
    }

    private void updateUsersMembership() {
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
        joinGroupRequest.setGroupId(selectedGroup.getId());
        joinGroupRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        Map<String, String> answersMap = new HashMap<>();
        String[] answerArray = groupsQuestionnaireRecyclerAdapter.getAnswersList();
        for (int i = 0; i < answerArray.length; i++) {
            answersMap.put("" + (i + 1), answerArray[i]);
        }
        joinGroupRequest.setQuestionnaireResponse(answersMap);
        Log.d("updateUsersMembership", "updateUsersMembership");

        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupsJoinResponse> call = groupsAPI.createMember(joinGroupRequest);
        call.enqueue(groupJoinResponseCallback);
    }

    private Callback<GroupsJoinResponse> groupJoinResponseCallback = new Callback<GroupsJoinResponse>() {
        @Override
        public void onResponse(Call<GroupsJoinResponse> call, retrofit2.Response<GroupsJoinResponse> response) {
            progressBar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    if (response.code() == 400) {
                        try {
                            String errorBody = new String(response.errorBody().bytes());
                            JSONObject jObject = new JSONObject(errorBody);
                            String reason = jObject.getString("reason");
                            if (!StringUtils.isNullOrEmpty(reason) && "already member".equals(reason)) {
                                String status = jObject.getJSONObject("data").getJSONArray("data").getJSONObject(0).getString("status");
                                if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(status)) {
                                    showToast(getString(R.string.groups_user_blocked_msg));
                                } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_LEFT.equals(status)) {
                                    patchMembershipRequest(jObject.getJSONObject("data").getJSONArray("data").getJSONObject(0).getInt("id"));
                                } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_REJECTED.equals(status)) {
                                    showToast(getString(R.string.groups_user_membership_rejected_msg));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    if (AppConstants.GROUP_TYPE_OPEN_KEY.equals(selectedGroup.getType())) {
                        showToast("Group Join success");
                        Intent intent = new Intent(GroupsQuestionnaireActivity.this, GroupDetailsActivity.class);
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
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsJoinResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            showToast("Group Join Request wrong");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void patchMembershipRequest(int membershipId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
        updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED);
        Call<GroupsMembershipResponse> call1 = groupsAPI.updateMember(membershipId, updateGroupMembershipRequest);
        call1.enqueue(updateGroupMemberRoleResponseCallback);
    }

    private Callback<GroupsMembershipResponse> updateGroupMemberRoleResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, retrofit2.Response<GroupsMembershipResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to update membership");
                return;
            }
            try {
                if (response.isSuccessful()) {
                    if (AppConstants.GROUP_TYPE_OPEN_KEY.equals(selectedGroup.getType())) {
                        showToast("Group Join success");
                        Intent intent = new Intent(GroupsQuestionnaireActivity.this, GroupDetailsActivity.class);
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
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            showToast("Failed to update membership");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showSuccessDialog() {
        GroupJoinConfirmationFragment groupJoinConfirmationFragment = new GroupJoinConfirmationFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        _args.putParcelable("groupItem", selectedGroup);
        groupJoinConfirmationFragment.setArguments(_args);
        groupJoinConfirmationFragment.setCancelable(true);
        groupJoinConfirmationFragment.show(fm, "Choose video option");
    }

    private boolean validateAnswers() {
        String[] answersArray = groupsQuestionnaireRecyclerAdapter.getAnswersList();
        for (int i = 0; i < answersArray.length; i++) {
            if (StringUtils.isNullOrEmpty(answersArray[i])) {
                Log.d("validate", "empty");
                return true;
            }
        }
        Log.d("validate", "Filled");
        return false;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {

    }
}
