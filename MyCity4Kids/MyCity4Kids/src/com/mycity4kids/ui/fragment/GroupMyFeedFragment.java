package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.adapter.GroupAboutRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupPostDetailsAndCommentsRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupsGenericPostRecyclerAdapter;
import com.mycity4kids.ui.adapter.MyFeedPollGenericRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class GroupMyFeedFragment extends BaseFragment implements MyFeedPollGenericRecyclerAdapter.RecyclerViewClickListener, GroupMembershipStatus.IMembershipStatus {

    private static final int EDIT_POST_REQUEST_CODE = 1010;
    private boolean isRequestRunning = false;
    private boolean isLastPageReached = false;
    private MyFeedPollGenericRecyclerAdapter myFeedPollGenericRecyclerAdapter;
    private GroupPostDetailsAndCommentsRecyclerAdapter groupPostDetailsAndCommentsRecyclerAdapter;
    private int skip = 0;
    private int limit = 10;
    private String postType;
    private ArrayList<GroupPostResult> postList;
    private RecyclerView recyclerView;
    private int totalPostCount;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private GroupResult selectedGroup;
    private String memberType;
    private int groupId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_groupspoll, container, false);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        postList = new ArrayList<>();
        myFeedPollGenericRecyclerAdapter = new MyFeedPollGenericRecyclerAdapter(getContext(), this, selectedGroup, memberType);
        myFeedPollGenericRecyclerAdapter.setData(postList);
        isRequestRunning = false;
        isLastPageReached = false;
        recyclerView.setAdapter(myFeedPollGenericRecyclerAdapter);
        postList.clear();
        skip = 0;
        limit = 10;
        getGroupPosts();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isRequestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isRequestRunning = true;
                            if (recyclerView.getAdapter() instanceof GroupsGenericPostRecyclerAdapter) {
                                getGroupPosts();
                            }
                        }
                    }
                }
            }
        });
        return fragmentView;
    }

    private void getGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupPostResponse> call = groupsAPI.getAllMyFeedPosts(skip,1, limit);
        call.enqueue(groupPostResponseCallback);
    }

    private Callback<GroupPostResponse> groupPostResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            isRequestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    processPostListingResponse(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            isRequestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processPostListingResponse(GroupPostResponse response) {
        totalPostCount = response.getTotal();
        ArrayList<GroupPostResult> dataList = (ArrayList<GroupPostResult>) response.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != postList && !postList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results
            }
        } else {
            formatPostData(dataList);
//            noPostsTextView.setVisibility(View.GONE);
            postList.addAll(dataList);
            myFeedPollGenericRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void processSearchResultListing(GroupPostResponse postSearchResponse) {
        totalPostCount = postSearchResponse.getTotal();
        ArrayList<GroupPostResult> dataList = (ArrayList<GroupPostResult>) postSearchResponse.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != postList && !postList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results
            }
        } else {
            formatPostData(dataList);
//            noPostsTextView.setVisibility(View.GONE);
            postList.addAll(dataList);
            myFeedPollGenericRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void formatPostData(ArrayList<GroupPostResult> dataList) {
        for (int j = 0; j < dataList.size(); j++) {
            if (dataList.get(j).getMediaUrls() != null && !((Map<String, String>) dataList.get(j).getMediaUrls()).isEmpty()) {
                if (((Map<String, String>) dataList.get(j).getMediaUrls()).get("audio") != null) {
                    dataList.get(j).setCommentType(AppConstants.COMMENT_TYPE_AUDIO);
                }
            }
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
                                dataList.get(j).setTotalVotesCount(dataList.get(j).getTotalVotesCount() + dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                switch (dataList.get(j).getCounts().get(i).getCounts().get(k).getName()) {
                                    case "option1":
                                        dataList.get(j).setOption1VoteCount(dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option2":
                                        dataList.get(j).setOption2VoteCount(dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option3":
                                        dataList.get(j).setOption3VoteCount(dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option4":
                                        dataList.get(j).setOption4VoteCount(dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1111) {
                if (data != null && data.getParcelableExtra("postDatas") != null) {
                    GroupPostResult currentPost = data.getParcelableExtra("postDatas");
                    for (int i = 0; i < postList.size(); i++) {
                        if (postList.get(i).getId() == currentPost.getId()) {
                            postList.get(i).setHelpfullCount(currentPost.getHelpfullCount());
                            postList.get(i).setNotHelpfullCount(currentPost.getNotHelpfullCount());
                            myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
                            break;
                        }
                    }

                }
                if (data != null && data.getParcelableArrayListExtra("completeResponseList") != null && data.getIntExtra("postId", -1) != -1) {
                    ArrayList<GroupPostCommentResult> completeCommentResponseList = data.getParcelableArrayListExtra("completeResponseList");
                    int postId = data.getIntExtra("postId", -1);

                    for (int i = 0; i < postList.size(); i++) {

                        if (postList.get(i).getId() == postId) {
                            postList.get(i).setResponseCount(completeCommentResponseList.size() - 1);
                            myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } else {
                    /*MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("groupId", "" + groupId);
                        mixpanel.track("GroupPostCreation", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (addPostContainer.getVisibility() == View.VISIBLE) {
                        addPostContainer.setVisibility(View.GONE);
                    }
                    isLastPageReached = false;
                    skip = 0;
                    limit = 10;
                    postList.clear();
                    myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
                    TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
                    tab.select();*/
                }
            } else if (requestCode == EDIT_POST_REQUEST_CODE) {
                /*if (postSettingsContainerMain.getVisibility() == View.VISIBLE) {
                    postSettingsContainerMain.setVisibility(View.GONE);
                }
                selectedPost.setContent(data.getStringExtra("updatedContent"));
                myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();*/
            } else if (requestCode == 2222) {
                Intent intent = getActivity().getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().finish();
                startActivity(intent);
            }
        }
    }

    /*private void getGroupDetails(int groupId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupDetailResponse> call = groupsAPI.getGroupById(groupId);
        call.enqueue(groupDetailsResponseCallback);
    }

    private Callback<GroupDetailResponse> groupDetailsResponseCallback = new Callback<GroupDetailResponse>() {
        @Override
        public void onResponse(Call<GroupDetailResponse> call, retrofit2.Response<GroupDetailResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupDetailResponse groupPostResponse = response.body();
                    selectedGroup = groupPostResponse.getData().getResult();
                    toolbarTitle.setHint(getString(R.string.groups_search_in));
                    memberCountTextView.setText(selectedGroup.getMemberCount() + " " + getString(R.string.groups_member_label));
                    groupNameTextView.setText(selectedGroup.getTitle());

                    Picasso.with(getActivity()).load(selectedGroup.getHeaderImage())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(groupImageView);

                    groupAboutRecyclerAdapter = new GroupAboutRecyclerAdapter(getContext(), getActivity());
                    groupAboutRecyclerAdapter.setData(selectedGroup);
                    TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
                    tab.select();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };*/

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onGroupPostRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.group_name:
//                getGroupDetails(postList.get(position).getGroupId());
                GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
                groupMembershipStatus.checkMembershipStatus(postList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        }
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
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            if (isAdded())
                Toast.makeText(getActivity(), getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("pendingMembershipFlag", true);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }
}
