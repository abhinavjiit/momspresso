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
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.GroupsMembershipResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsListingActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.adapter.GroupsRecyclerGridAdapter;
import com.mycity4kids.widget.NoScrollGridLayoutManager;
import com.mycity4kids.widget.SpacesItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsFragment extends BaseFragment implements View.OnClickListener, GroupsRecyclerGridAdapter.RecyclerViewClickListener, GroupMembershipStatus.IMembershipStatus {

    private ArrayList<GroupResult> joinedGroupList, allGroupList;
    private GroupResult selectedGroup;

    private RecyclerView joinedGroupRecyclerGridView, allGroupRecyclerGridView;
    private ProgressBar progressBar;
    private TextView noGroupsTextView, seeAllGpTextView;
    private TextView seeAllJoinedGpTextView;
    private TextView allGroupLabelTextView;
    private GroupsRecyclerGridAdapter getAllGroupAdapter, getJoinedGroupAdapter;
    private LinkedTreeMap<String, String> selectedQuestionnaire;
    private TextView joinGpLabel;
    private MixpanelAPI mixpanel;
    private int position;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "GroupsListingScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            mixpanel.track("GroupListing", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        joinedGroupRecyclerGridView = (RecyclerView) view.findViewById(R.id.joinedGroupRecyclerGridView);
        allGroupRecyclerGridView = (RecyclerView) view.findViewById(R.id.allGroupRecyclerGridView);
        seeAllGpTextView = (TextView) view.findViewById(R.id.seeAllGpTextView);
        joinGpLabel = (TextView) view.findViewById(R.id.joinGpLabel);
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

        getAllGroupAdapter = new GroupsRecyclerGridAdapter(getActivity(), this, false, true);
        getJoinedGroupAdapter = new GroupsRecyclerGridAdapter(getActivity(), this, true, false);

        joinedGroupRecyclerGridView.setAdapter(getJoinedGroupAdapter);

        joinedGroupList = new ArrayList<>();
        getJoinedGroupAdapter.setNewListData(joinedGroupList);

        getJoinedGroupListApi();
        return view;
    }

    private void getJoinedGroupListApi() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsMembershipResponse> call = groupsAPI.getTop4JoinedGroupList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER);
        call.enqueue(joinedGroupListResponseCallback);
    }

    private void getAllGroupListApi(List<GroupResult> dataList) {
        List<String> groupIdList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            groupIdList.add("" + dataList.get(i).getId());
        }
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupsListingResponse> call;
        switch (dataList.size()) {
            case 1:
                call = groupsAPI.getTop4SuggestedGroupsSingleExclusion(groupIdList.get(0));
                break;
            default:
                call = groupsAPI.getTop4SuggestedGroups(groupIdList);
                break;
        }
        call.enqueue(groupListResponseCallback);
    }

    private Callback<GroupsMembershipResponse> joinedGroupListResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, retrofit2.Response<GroupsMembershipResponse> response) {
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
                    GroupsMembershipResponse responseModel = response.body();
                    List<GroupsMembershipResult> membershipList = responseModel.getData().getResult();
                    List<GroupResult> dataList = new ArrayList<>();
                    for (int i = 0; i < membershipList.size(); i++) {
                        dataList.add(membershipList.get(i).getGroupInfo());
                    }

                    if (dataList.isEmpty()) {
                        joinedGroupRecyclerGridView.setVisibility(View.GONE);
                        seeAllJoinedGpTextView.setVisibility(View.GONE);
                        joinGpLabel.setVisibility(View.GONE);
                    } else {
                        joinedGroupRecyclerGridView.setVisibility(View.VISIBLE);
                        joinedGroupList = (ArrayList<GroupResult>) dataList;
                        getJoinedGroupAdapter.setNewListData(joinedGroupList);
                        getJoinedGroupAdapter.notifyDataSetChanged();
                        if (joinedGroupList.size() > 4) {
                            seeAllJoinedGpTextView.setVisibility(View.VISIBLE);
                        }
                    }
                    getAllGroupListApi(dataList);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<GroupsListingResponse> groupListResponseCallback = new Callback<GroupsListingResponse>() {
        @Override
        public void onResponse(Call<GroupsListingResponse> call, retrofit2.Response<GroupsListingResponse> response) {
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
                        allGroupLabelTextView.setVisibility(View.VISIBLE);
                        if (allGroupList.size() == 10) {
                            seeAllGpTextView.setVisibility(View.VISIBLE);
                        }
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
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seeAllGpTextView: {
                Utils.groupsEvent(getActivity(), "Home Screen", "other groups_View all", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "other group listing", "", "");
                Intent intent = new Intent(getActivity(), GroupsListingActivity.class);
                intent.putExtra("isMember", false);
                intent.putParcelableArrayListExtra("joinedList", joinedGroupList);
                startActivity(intent);
            }
            break;
            case R.id.seeAllJoinedGpTextView: {
                Utils.groupsEvent(getActivity(), "Home Screen", "Groups you are member of_ View all", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Your groups listing", "", "");
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
        } else {
            selectedGroup = allGroupList.get(position);
            selectedQuestionnaire = (LinkedTreeMap<String, String>) allGroupList.get(position).getQuestionnaire();
        }
        groupMembershipStatus.checkMembershipStatus(selectedGroup.getId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
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

        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(userType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender()) ||
                    "m".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                }
                if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                } else {
                    return;
                }
            } else {

            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            if (isAdded())
                Toast.makeText(getActivity(), getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {

            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
            Utils.groupsEvent(getActivity(), "Home Screen", "Groups you are member of_group card", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Discussion Page", "", "");


        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra("pendingMembershipFlag", true);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else {
            Utils.groupsEvent(getActivity(), "Home Screen", "other groups_group card", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "About Page", "", "");
            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }


    public void setHightlight(int position) {
        this.position = position;
    }


}