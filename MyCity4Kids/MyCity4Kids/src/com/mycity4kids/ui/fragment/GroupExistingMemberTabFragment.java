package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.GroupsMembershipResult;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.GroupMembershipRequestActivity;
import com.mycity4kids.ui.adapter.GroupsMembersRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupsMembershipRequestRecyclerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 26/7/18.
 */

public class GroupExistingMemberTabFragment extends BaseFragment implements GroupsMembersRecyclerAdapter.RecyclerViewClickListener {

    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private ArrayList<GroupsMembershipResult> membersList;

    private RecyclerView recyclerView;
    private GroupsMembersRecyclerAdapter adapter;
    private int groupId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_existing_member_tab_fragment, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        groupId = getArguments().getInt("groupId");

        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        membersList = new ArrayList<>();

        adapter = new GroupsMembersRecyclerAdapter(getActivity(), this);
        adapter.setData(membersList);
        recyclerView.setAdapter(adapter);

        getAllMembers();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            getAllMembers();
                        }
                    }
                }
            }
        });
        return view;
    }

    public void refreshList() {
        isReuqestRunning = false;
        isLastPageReached = false;
        skip = 0;
        limit = 10;
        membersList.clear();
        adapter.notifyDataSetChanged();
        getAllMembers();
    }

    private void getAllMembers() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupsMembershipResponse> call = groupsAPI.getGroupMembersByStatus(groupId, AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER, skip, limit);
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
            }
        } else {
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
    public void onRecyclerItemClick(View view, int position) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        switch (view.getId()) {
            case R.id.memberOptionImageView: {
                ((GroupMembershipRequestActivity) getActivity()).showMembersOption(membersList.get(position));
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
