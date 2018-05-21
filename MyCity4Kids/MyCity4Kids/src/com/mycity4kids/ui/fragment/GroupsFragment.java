package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.internal.LinkedTreeMap;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsListingActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.adapter.GroupsRecyclerGridAdapter;
import com.mycity4kids.widget.NoScrollGridLayoutManager;
import com.mycity4kids.widget.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsFragment extends BaseFragment implements View.OnClickListener, GroupsRecyclerGridAdapter.RecyclerViewClickListener {

    private boolean isReuqestRunning = false;
    private ArrayList<GroupResult> joinedGroupList, allGroupList;
    private boolean isLastPageReached;
    private int skip = 0;
    private int limit = 10;
    private int totalGroupCount;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private RecyclerView joinedGroupRecyclerGridView, allGroupRecyclerGridView;
    private ProgressBar progressBar;
    private TextView noGroupsTextView, seeAllGpTextView;
    private TextView seeAllJoinedGpTextView;
    private TextView allGroupLabelTextView;
    private GroupsRecyclerGridAdapter getAllGroupAdapter, getJoinedGroupAdapter;

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
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsListingResponse responseModel = response.body();
                    ArrayList<GroupResult> dataList = responseModel.getData().get(0).getResult();
                    if (dataList == null || dataList.isEmpty()) {
                        joinedGroupRecyclerGridView.setVisibility(View.GONE);
                        seeAllJoinedGpTextView.setVisibility(View.GONE);
                    } else {
                        joinedGroupRecyclerGridView.setVisibility(View.VISIBLE);
                        joinedGroupList = dataList;
                        getJoinedGroupAdapter.setNewListData(joinedGroupList);
                        getJoinedGroupAdapter.notifyDataSetChanged();
                        if (joinedGroupList.size() > 4) {
                            seeAllJoinedGpTextView.setVisibility(View.VISIBLE);
                        }
                    }
                    getAllGroupListApi(skip, limit);
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
                    ArrayList<GroupResult> dataList = responseModel.getData().get(0).getResult();
                    if (dataList == null || dataList.isEmpty()) {
                        allGroupRecyclerGridView.setVisibility(View.GONE);
                        seeAllGpTextView.setVisibility(View.GONE);
                        allGroupLabelTextView.setVisibility(View.GONE);
                    } else {
                        allGroupList = dataList;
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
        if (isMember) {
            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
            intent.putExtra("groupItem", joinedGroupList.get(position));
            intent.putExtra("isMember", isMember);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
            intent.putExtra("groupItem", allGroupList.get(position));
            intent.putExtra("questionnaire", (LinkedTreeMap<String, String>) allGroupList.get(position).getQuestionnaire());
            intent.putExtra("isMember", isMember);
            startActivity(intent);
        }

    }
}
