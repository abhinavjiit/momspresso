package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.GroupsMembershipResult;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.GroupMembershipActivity;
import com.mycity4kids.ui.adapter.GroupsMembershipRequestRecyclerAdapter;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 26/7/18.
 */

public class GroupMembershipRequestTabFragment extends BaseFragment implements
        GroupsMembershipRequestRecyclerAdapter.RecyclerViewClickListener {

    private int nextPageNumber = 1;
    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private ArrayList<GroupsMembershipResult> membersList;

    private RecyclerView recyclerView;
    private GroupsMembershipRequestRecyclerAdapter adapter;
    private int groupId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_membership_request_tab_fragment, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        groupId = getArguments().getInt("groupId");

        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        membersList = new ArrayList<>();

        adapter = new GroupsMembershipRequestRecyclerAdapter(getActivity(), this);
        adapter.setData(membersList);
        recyclerView.setAdapter(adapter);

        getAllPendingMembersForGroup();

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
                            getAllPendingMembersForGroup();
                        }
                    }
                }
            }
        });
        return view;
    }

    private void getAllPendingMembersForGroup() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupsMembershipResponse> call = groupsAPI
                .getGroupMembersByStatus(groupId, AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION, skip, limit);
        call.enqueue(memberShipReponseCallback);
    }

    private Callback<GroupsMembershipResponse> memberShipReponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call,
                retrofit2.Response<GroupsMembershipResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
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
                FirebaseCrashlytics.getInstance().recordException(e);
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
        ArrayList<GroupsMembershipResult> dataList = (ArrayList<GroupsMembershipResult>) responseModel.getData()
                .getResult();
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
    public void onRecyclerItemClick(View view, int position, boolean isMember) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        switch (view.getId()) {
            case R.id.acceptTextView: {
                showProgressDialog(BaseApplication.getAppContext().getString(R.string.please_wait));
                UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
                updateGroupMembershipRequest.setUserId(membersList.get(position).getUserId());
                updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER);
                Call<GroupsMembershipResponse> call1 = groupsAPI
                        .updateMember(membersList.get(position).getId(), updateGroupMembershipRequest);
                call1.enqueue(updateGroupMembershipResponseCallback);
            }
            break;
            case R.id.rejectTextView: {
                showProgressDialog(BaseApplication.getAppContext().getString(R.string.please_wait));
                UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
                updateGroupMembershipRequest.setUserId(membersList.get(position).getUserId());
                updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_REJECTED);
                Call<GroupsMembershipResponse> call1 = groupsAPI
                        .updateMember(membersList.get(position).getId(), updateGroupMembershipRequest);
                call1.enqueue(updateGroupMembershipResponseCallback);
            }
            break;
        }
    }

    private Callback<GroupsMembershipResponse> updateGroupMembershipResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call,
                retrofit2.Response<GroupsMembershipResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsMembershipResponse groupsMembershipResponse = response.body();
                    skip = 0;
                    limit = 10;
                    membersList.clear();
                    isLastPageReached = false;
                    isReuqestRunning = false;
                    adapter.notifyDataSetChanged();
                    getAllPendingMembersForGroup();
                    ((GroupMembershipActivity) getActivity()).updateExistingMemberList();
                } else {

                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
