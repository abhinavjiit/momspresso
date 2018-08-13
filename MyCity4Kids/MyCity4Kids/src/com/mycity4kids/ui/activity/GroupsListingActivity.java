package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.internal.LinkedTreeMap;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.adapter.GroupsRecyclerGridAdapter;
import com.mycity4kids.widget.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 17/5/18.
 */

public class GroupsListingActivity extends BaseActivity implements GroupsRecyclerGridAdapter.RecyclerViewClickListener, GroupMembershipStatus.IMembershipStatus {

    private GroupsRecyclerGridAdapter adapter;

    private boolean isReuqestRunning = false;
    private ArrayList<GroupResult> groupList;
    private boolean isLastPageReached;
    private int skip = 0;
    private int limit = 10;
    private int totalGroupCount;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private Toolbar toolbar;
    private RecyclerView recyclerGridView;
    private TextView noGroupsTextView;
    private ProgressBar progressBar;
    private GroupResult selectedGroup;
    private LinkedTreeMap<String, String> selectedQuestionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_listing_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerGridView = (RecyclerView) findViewById(R.id.recyclerGridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noGroupsTextView = (TextView) findViewById(R.id.noGroupsTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final boolean isMember = getIntent().getBooleanExtra("isMember", false);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerGridView.setLayoutManager(gridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.groups_column_spacing);
        recyclerGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        adapter = new GroupsRecyclerGridAdapter(this, this, isMember, true);
        groupList = new ArrayList<>();
        adapter.setNewListData(groupList);
        recyclerGridView.setAdapter(adapter);

        recyclerGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            if (isMember) {
                                getJoinedGroupListApi(skip, limit);
                            } else {
                                getAllGroupListApi(skip, limit);
                            }

                        }
                    }
                }
            }
        });

        if (isMember) {
            getJoinedGroupListApi(skip, limit);
        } else {
            getAllGroupListApi(skip, limit);
        }
    }

    private void getJoinedGroupListApi(int skip, int limit) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsListingResponse> call = groupsAPI.getJoinedGroupList(skip, limit);
        call.enqueue(groupListResponseCallback);
    }

    private void getAllGroupListApi(int skip, int limit) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsListingResponse> call = groupsAPI.getGroupList(skip, limit);
        call.enqueue(groupListResponseCallback);
    }

    private Callback<GroupsListingResponse> groupListResponseCallback = new Callback<GroupsListingResponse>() {
        @Override
        public void onResponse(Call<GroupsListingResponse> call, retrofit2.Response<GroupsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsListingResponse responseModel = response.body();
                    processGroupListingResponse(responseModel);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsListingResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processGroupListingResponse(GroupsListingResponse responseModel) {
        totalGroupCount = responseModel.getTotal();
        List<GroupResult> dataList = responseModel.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != groupList && !groupList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                noGroupsTextView.setVisibility(View.VISIBLE);
                groupList = (ArrayList<GroupResult>) dataList;
                adapter.setNewListData(groupList);
                adapter.notifyDataSetChanged();
                recyclerGridView.setVisibility(View.GONE);
            }
        } else {
            noGroupsTextView.setVisibility(View.GONE);
            if (skip == 0) {
                groupList = (ArrayList<GroupResult>) dataList;
            } else {
                groupList.addAll(dataList);
            }
            adapter.setNewListData(groupList);
            skip = skip + limit;
            if (skip >= totalGroupCount) {
                isLastPageReached = true;
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position, boolean isMember) {
        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        if (isMember) {
            selectedGroup = groupList.get(position);
//            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
//            intent.putExtra("groupItem", joinedGroupList.get(position));
//            intent.putExtra("isMember", isMember);
//            startActivity(intent);
        } else {
            selectedGroup = groupList.get(position);
            selectedQuestionnaire = (LinkedTreeMap<String, String>) groupList.get(position).getQuestionnaire();
//            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
//            intent.putExtra("groupItem", allGroupList.get(position));
//            intent.putExtra("questionnaire", (LinkedTreeMap<String, String>) allGroupList.get(position).getQuestionnaire());
//            intent.putExtra("isMember", isMember);
//            startActivity(intent);
        }
        groupMembershipStatus.checkMembershipStatus(selectedGroup.getId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

//        if (isMember) {
//            Intent intent = new Intent(this, GroupDetailsActivity.class);
//            intent.putExtra("groupId", groupList.get(position).getId());
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(this, GroupsSummaryActivity.class);
//            intent.putExtra("groupId", groupList.get(position).getId());
//            intent.putExtra("questionnaire", (LinkedTreeMap<String, String>) groupList.get(position).getQuestionnaire());
//            startActivity(intent);
//        }
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

        } else {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
//            intent.putExtra("questionnaire", (LinkedTreeMap<String, String>) selectedGroup.getQuestionnaire());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            Toast.makeText(this, getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(this, GroupDetailsActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
//            if (isAdded())
//                Toast.makeText(getActivity(), "Your membership is still pending", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra("pendingMembershipFlag", true);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

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
