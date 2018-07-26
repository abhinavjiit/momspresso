package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.GroupsMembershipResult;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupMembersPagerAdapter;
import com.mycity4kids.ui.adapter.GroupsMembershipRequestRecyclerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 9/7/18.
 */

public class GroupMembershipRequestActivity extends BaseActivity implements GroupsMembershipRequestRecyclerAdapter.RecyclerViewClickListener {

    private int nextPageNumber = 1;
    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private ArrayList<GroupsMembershipResult> membersList;

    private GroupsMembershipRequestRecyclerAdapter adapter;
    //    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private int groupId;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_membership_request_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        groupId = getIntent().getIntExtra("groupId", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabLayout.addTab(tabLayout.newTab().setText("Requests"));
        tabLayout.addTab(tabLayout.newTab().setText("Existing Members"));
        membersList = new ArrayList<>();

        GroupMembersPagerAdapter groupMembersPagerAdapter = new GroupMembersPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), groupId);
        viewPager.setAdapter(groupMembersPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//
//        final LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(llm);
//
//        adapter = new GroupsMembershipRequestRecyclerAdapter(this, this);
//        adapter.setData(membersList);
//        recyclerView.setAdapter(adapter);
//
//        getAllPendingMembersForGroup();
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) //check for scroll down
//                {
//                    visibleItemCount = llm.getChildCount();
//                    totalItemCount = llm.getItemCount();
//                    pastVisiblesItems = llm.findFirstVisibleItemPosition();
//
//                    if (!isReuqestRunning && !isLastPageReached) {
//                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                            isReuqestRunning = true;
//                            getAllPendingMembersForGroup();
//                        }
//                    }
//                }
//            }
//        });
    }

    private void getAllPendingMembersForGroup() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupsMembershipResponse> call = groupsAPI.getGroupMembersByStatus(groupId, AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION, skip, limit);
        call.enqueue(memberShipReponseCallback);
    }

    private Callback<GroupsMembershipResponse> memberShipReponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, retrofit2.Response<GroupsMembershipResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsMembershipResponse responseModel = response.body();
                    processGroupsPendingMembers(responseModel);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {

        }
    };

    private void processGroupsPendingMembers(GroupsMembershipResponse responseModel) {
        totalPostCount = responseModel.getTotal();
        ArrayList<GroupsMembershipResult> dataList = (ArrayList<GroupsMembershipResult>) responseModel.getData().getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != membersList && !membersList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results
//                noPostsTextView.setVisibility(View.VISIBLE);
//                postList = dataList;
//                groupSummaryPostRecyclerAdapter.setHeaderData(selectedGroup);
//                groupSummaryPostRecyclerAdapter.setData(postList);
//                groupSummaryPostRecyclerAdapter.notifyDataSetChanged();
            }
        } else {
//            formatPostData(dataList);
//            noPostsTextView.setVisibility(View.GONE);
            membersList.addAll(dataList);
            adapter.setData(membersList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
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
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        switch (view.getId()) {
            case R.id.acceptTextView: {
                UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
                updateGroupMembershipRequest.setUserId(membersList.get(position).getUserId());
                updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER);
                Call<GroupsMembershipResponse> call1 = groupsAPI.updateMember(membersList.get(position).getId(), updateGroupMembershipRequest);
                call1.enqueue(updateGroupMembershipResponseCallback);
            }
            break;
            case R.id.rejectTextView: {
                UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
                updateGroupMembershipRequest.setUserId(membersList.get(position).getUserId());
                updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED);
                Call<GroupsMembershipResponse> call1 = groupsAPI.updateMember(membersList.get(position).getId(), updateGroupMembershipRequest);
                call1.enqueue(updateGroupMembershipResponseCallback);
            }
            break;
        }
    }

    private Callback<GroupsMembershipResponse> updateGroupMembershipResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, retrofit2.Response<GroupsMembershipResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsMembershipResponse groupsMembershipResponse = response.body();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
