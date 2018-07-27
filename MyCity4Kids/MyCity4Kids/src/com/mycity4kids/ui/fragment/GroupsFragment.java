package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.internal.LinkedTreeMap;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsListingActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.adapter.GroupsRecyclerGridAdapter;
import com.mycity4kids.widget.NoScrollGridLayoutManager;
import com.mycity4kids.widget.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsFragment extends BaseFragment implements View.OnClickListener, GroupsRecyclerGridAdapter.RecyclerViewClickListener, GroupMembershipStatus.IMembershipStatus {

    private boolean isReuqestRunning = false;
    private ArrayList<GroupResult> joinedGroupList, allGroupList;
    private boolean isLastPageReached;
    private int skip = 0;
    private int limit = 10;
    private GroupResult selectedGroup;
    private int totalGroupCount;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private RecyclerView joinedGroupRecyclerGridView, allGroupRecyclerGridView;
    private ProgressBar progressBar;
    private TextView noGroupsTextView, seeAllGpTextView;
    private TextView seeAllJoinedGpTextView;
    private TextView allGroupLabelTextView;
    private GroupsRecyclerGridAdapter getAllGroupAdapter, getJoinedGroupAdapter;
    private LinkedTreeMap<String, String> selectedQuestionnaire;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_fragment, container, false);
        joinedGroupRecyclerGridView = (RecyclerView) view.findViewById(R.id.joinedGroupRecyclerGridView);
        allGroupRecyclerGridView = (RecyclerView) view.findViewById(R.id.allGroupRecyclerGridView);
        seeAllGpTextView = (TextView) view.findViewById(R.id.seeAllGpTextView);
        seeAllJoinedGpTextView = (TextView) view.findViewById(R.id.seeAllJoinedGpTextView);
        allGroupLabelTextView = (TextView) view.findViewById(R.id.allGroupLabelTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noGroupsTextView = (TextView) view.findViewById(R.id.noGroupsTextView);

        seeAllGpTextView.setOnClickListener(this);
        seeAllJoinedGpTextView.setOnClickListener(this);

        final NoScrollGridLayoutManager joinedGpGridLayoutManager = new NoScrollGridLayoutManager(getActivity(), 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        joinedGroupRecyclerGridView.setLayoutManager(joinedGpGridLayoutManager);
        final NoScrollGridLayoutManager allGpGridLayoutManager = new NoScrollGridLayoutManager(getActivity(), 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        allGroupRecyclerGridView.setLayoutManager(allGpGridLayoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.groups_column_spacing);
        joinedGroupRecyclerGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        allGroupRecyclerGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        getAllGroupAdapter = new GroupsRecyclerGridAdapter(getActivity(), this, false, false);
        getJoinedGroupAdapter = new GroupsRecyclerGridAdapter(getActivity(), this, true, false);

        joinedGroupRecyclerGridView.setAdapter(getJoinedGroupAdapter);

        joinedGroupList = new ArrayList<>();
        getJoinedGroupAdapter.setNewListData(joinedGroupList);

        getJoinedGroupListApi(skip, limit);
        return view;
    }

    private void getJoinedGroupListApi(int skip, int limit) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsListingResponse> call = groupsAPI.getJoinedGroupList(skip, limit);
        call.enqueue(joinedGroupListResponseCallback);
    }

    private void getAllGroupListApi(int skip, int limit) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsListingResponse> call = groupsAPI.getGroupList(skip, limit);
        call.enqueue(groupListResponseCallback);
    }

    private Callback<GroupsListingResponse> joinedGroupListResponseCallback = new Callback<GroupsListingResponse>() {
        @Override
        public void onResponse(Call<GroupsListingResponse> call, retrofit2.Response<GroupsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);

//                    Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
//                    intent.putExtra("groupId", 1);
//                    intent.putExtra("isMember", true);
//                    startActivity(intent);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsListingResponse responseModel = response.body();
                    List<GroupResult> dataList = responseModel.getData().get(0).getResult();
                    if (dataList == null || dataList.isEmpty()) {
                        joinedGroupRecyclerGridView.setVisibility(View.GONE);
                        seeAllJoinedGpTextView.setVisibility(View.GONE);
                    } else {
                        joinedGroupRecyclerGridView.setVisibility(View.VISIBLE);
                        joinedGroupList = (ArrayList<GroupResult>) dataList;
                        getJoinedGroupAdapter.setNewListData(joinedGroupList);
                        getJoinedGroupAdapter.notifyDataSetChanged();
                        if (joinedGroupList.size() > 4) {
                            seeAllJoinedGpTextView.setVisibility(View.VISIBLE);
                        }
                    }
                    if (responseModel.isMember()) {
                        getAllGroupListApi(skip, limit);
                    }
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

//            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
//            intent.putExtra("groupId", 1);
//            intent.putExtra("isMember", true);
//            startActivity(intent);

        }
    };

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
                    List<GroupResult> dataList = responseModel.getData().get(0).getResult();
                    if (dataList == null || dataList.isEmpty()) {
                        allGroupRecyclerGridView.setVisibility(View.GONE);
                        seeAllGpTextView.setVisibility(View.GONE);
                        allGroupLabelTextView.setVisibility(View.GONE);
                    } else {
                        allGroupList = (ArrayList<GroupResult>) dataList;
                        allGroupRecyclerGridView.setAdapter(getAllGroupAdapter);
                        getAllGroupAdapter.setNewListData(allGroupList);
                        getAllGroupAdapter.notifyDataSetChanged();
                        allGroupRecyclerGridView.setVisibility(View.VISIBLE);
                        seeAllGpTextView.setVisibility(View.VISIBLE);
                        allGroupLabelTextView.setVisibility(View.VISIBLE);
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
        public void onFailure(Call<GroupsListingResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seeAllGpTextView: {
                Intent intent = new Intent(getActivity(), GroupsListingActivity.class);
                intent.putExtra("isMember", false);
                startActivity(intent);
            }
            break;
            case R.id.seeAllJoinedGpTextView: {
                Intent intent = new Intent(getActivity(), GroupsListingActivity.class);
                intent.putExtra("isMember", true);
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position, boolean isMember) {
        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        if (isMember) {
            selectedGroup = joinedGroupList.get(position);
//            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
//            intent.putExtra("groupItem", joinedGroupList.get(position));
//            intent.putExtra("isMember", isMember);
//            startActivity(intent);
        } else {
            selectedGroup = allGroupList.get(position);
            selectedQuestionnaire = (LinkedTreeMap<String, String>) allGroupList.get(position).getQuestionnaire();
//            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
//            intent.putExtra("groupItem", allGroupList.get(position));
//            intent.putExtra("questionnaire", (LinkedTreeMap<String, String>) allGroupList.get(position).getQuestionnaire());
//            intent.putExtra("isMember", isMember);
//            startActivity(intent);
        }
        groupMembershipStatus.checkMembershipStatus(selectedGroup.getId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body) {
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
            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
            intent.putExtra("groupItem", selectedGroup);
            intent.putExtra("questionnaire", (LinkedTreeMap<String, String>) selectedGroup.getQuestionnaire());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            if (isAdded())
                Toast.makeText(getActivity(), "You have been blocked from this group", Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
            intent.putExtra("groupItem", selectedGroup);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            if (isAdded())
                Toast.makeText(getActivity(), "Your membership is still pending", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
            intent.putExtra("groupItem", selectedGroup);
            intent.putExtra("questionnaire", (LinkedTreeMap<String, String>) selectedGroup.getQuestionnaire());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }
}
