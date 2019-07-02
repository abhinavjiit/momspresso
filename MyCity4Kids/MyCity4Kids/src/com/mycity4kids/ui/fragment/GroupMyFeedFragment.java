package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.GroupActionsPatchRequest;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.request.UpdateGroupPostRequest;
import com.mycity4kids.models.request.UpdatePostSettingsRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.GroupsEditPostActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.activity.PrivateProfileActivity;
import com.mycity4kids.ui.activity.PublicProfileActivity;
import com.mycity4kids.ui.adapter.GroupPostDetailsAndCommentsRecyclerAdapter;
import com.mycity4kids.ui.adapter.MyFeedPollGenericRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class GroupMyFeedFragment extends BaseFragment implements MyFeedPollGenericRecyclerAdapter.RecyclerViewClickListener, GroupMembershipStatus.IMembershipStatus, View.OnClickListener {

    private static final int EDIT_POST_REQUEST_CODE = 1010;
    private boolean isRequestRunning;
    private boolean isLastPageReached;
    private MyFeedPollGenericRecyclerAdapter myFeedPollGenericRecyclerAdapter;
    private GroupPostDetailsAndCommentsRecyclerAdapter groupPostDetailsAndCommentsRecyclerAdapter;
    private int skip = 0;
    private int postId;
    private int limit = 10;
    String content, userDynamoId;
    private String postType;
    private ArrayList<GroupPostResult> postList;
    private RecyclerView recyclerView;
    private int totalPostCount;
    private String action = "";
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private GroupResult selectedGroup;
    private String memberType;
    private int groupId;

    private GroupPostResult selectedPost;
    private LinearLayout postSettingsContainer;
    private RelativeLayout postSettingsContainerMain;
    private TextView savePostTextView, notificationToggleTextView, commentToggleTextView, reportPostTextView, editPostTextView, deletePostTextView, blockUserTextView, pinPostTextView, emptyListTextView;
    private View overlayView;
    Map<String, String> image;
    private Animation slideAnim, fadeAnim;
    private UserPostSettingResult currentPostPrefsForUser;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getUserType().equals("1")) {


            if (getActivity().getWindow() != null) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            }
        }
        View fragmentView = inflater.inflate(R.layout.fragment_groupspoll, container, false);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        postList = new ArrayList<>();
        myFeedPollGenericRecyclerAdapter = new MyFeedPollGenericRecyclerAdapter(getContext(), this, selectedGroup, memberType);
        myFeedPollGenericRecyclerAdapter.setData(postList);
        postSettingsContainer = (LinearLayout) fragmentView.findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = (RelativeLayout) fragmentView.findViewById(R.id.postSettingsContainerMain);
        commentToggleTextView = (TextView) fragmentView.findViewById(R.id.commentToggleTextView);
        overlayView = fragmentView.findViewById(R.id.overlayView);
        slideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_anim);
        savePostTextView = (TextView) fragmentView.findViewById(R.id.savePostTextView);
        deletePostTextView = (TextView) fragmentView.findViewById(R.id.deletePostTextView);
        editPostTextView = (TextView) fragmentView.findViewById(R.id.editPostTextView);
        blockUserTextView = (TextView) fragmentView.findViewById(R.id.blockUserTextView);
        pinPostTextView = (TextView) fragmentView.findViewById(R.id.pinPostTextView);
        notificationToggleTextView = (TextView) fragmentView.findViewById(R.id.notificationToggleTextView);
        reportPostTextView = (TextView) fragmentView.findViewById(R.id.reportPostTextView);
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar);
        emptyListTextView = (TextView) fragmentView.findViewById(R.id.emptyListText);

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            mixpanel.track("MyFeed", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }


        isRequestRunning = false;
        isLastPageReached = false;
        recyclerView.setAdapter(myFeedPollGenericRecyclerAdapter);
        postList.clear();
        skip = 0;
        limit = 10;
        getGroupPosts();

        savePostTextView.setOnClickListener(this);
        notificationToggleTextView.setOnClickListener(this);
        commentToggleTextView.setOnClickListener(this);
        reportPostTextView.setOnClickListener(this);
        overlayView.setOnClickListener(this);
        editPostTextView.setOnClickListener(this);
        deletePostTextView.setOnClickListener(this);
        blockUserTextView.setOnClickListener(this);
        pinPostTextView.setOnClickListener(this);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();


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
                            if (recyclerView.getAdapter() instanceof MyFeedPollGenericRecyclerAdapter) {
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
        Call<GroupPostResponse> call = groupsAPI.getAllMyFeedPosts(skip, 1, limit);
        call.enqueue(groupPostResponseCallback);
    }

    private Callback<GroupPostResponse> groupPostResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            isRequestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    emptyListTextView.setVisibility(View.VISIBLE);
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    emptyListTextView.setVisibility(View.GONE);
                    GroupPostResponse groupPostResponse = response.body();
                    processPostListingResponse(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                emptyListTextView.setVisibility(View.VISIBLE);
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
            emptyListTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            isLastPageReached = false;
            if (null != postList && !postList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results
            }
        } else {
            emptyListTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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
                if (postSettingsContainerMain.getVisibility() == View.VISIBLE) {
                    postSettingsContainerMain.setVisibility(View.GONE);
                }
                selectedPost.setContent(data.getStringExtra("updatedContent"));
                myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
            } else if (requestCode == 2222) {
                if (data != null && data.getParcelableArrayListExtra("completeResponseList") != null && data.getIntExtra("postId", -1) != -1 && data.getIntExtra("replyCount", -1) != -1) {
                    ArrayList<GroupPostCommentResult> completeCommentResponseList = data.getParcelableArrayListExtra("completeResponseList");
                    int postId = data.getIntExtra("postId", -1);
                    int replyCount = data.getIntExtra("replyCount", -1);

                    for (int i = 0; i < postList.size(); i++) {

                        if (postList.get(i).getId() == postId) {
                            postList.get(i).setResponseCount(completeCommentResponseList.size() - 1 + replyCount);
                            myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }


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
            case R.id.postCommentsTextView:
            case R.id.commentLayout:
                Intent intent = new Intent(getActivity(), GroupPostDetailActivity.class);
                intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                intent.putExtra("postData", postList.get(position));
                LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(position).getPollOptions();
                intent.putExtra("pollOptions", linkedTreeMap);
                intent.putExtra("postId", postList.get(position).getId());
                intent.putExtra("groupId", postList.get(position).getGroupId());
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                startActivityForResult(intent, 2222);
                break;
            case R.id.group_name:
//                getGroupDetails(postList.get(position).getGroupId());
                GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
                groupMembershipStatus.checkMembershipStatus(postList.get(position).getGroupId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                break;

            case R.id.postSettingImageView:
                selectedPost = postList.get(position);
                getCurrentUserPostSettingsStatus(selectedPost);
                if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)
                        || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
                    getAdminPostSettingsStatus(selectedPost);
                }
                if (selectedPost.getDisableComments() == 1) {
                    commentToggleTextView.setText(getString(R.string.groups_enable_comment));
                } else {
                    commentToggleTextView.setText(getString(R.string.groups_disable_comment));
                }
                break;
            case R.id.userImageView:
            case R.id.usernameTextView:

                if (postList.get(position).getIsAnnon() == 0) {

                    if (userDynamoId.equals(postList.get(position).getUserId())) {
//                    MyAccountProfileFragment fragment0 = new MyAccountProfileFragment();
//                    Bundle mBundle0 = new Bundle();
//                    fragment0.setArguments(mBundle0);
//                    if (isAdded())
//                        ((ShortStoriesListingContainerActivity) getActivity()).addFragment(fragment0, mBundle0, true);
                        Intent pIntent = new Intent(getActivity(), PrivateProfileActivity.class);
                        startActivity(pIntent);
                    } else {
                        Utils.groupsEvent(getActivity(), "Groups_Discussion", "profile image", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "User Profile", "", "");

                        Intent intentnn = new Intent(getActivity(), PublicProfileActivity.class);
                        intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, postList.get(position).getUserId());
                        intentnn.putExtra(AppConstants.AUTHOR_NAME, postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo().getLastName());
                        intentnn.putExtra(Constants.FROM_SCREEN, "Groups");
                        startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                    }
                }
                break;

            case R.id.shareTextView:
                Utils.groupsEvent(getActivity(), "Groups_Discussion_# comment", "Share", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "sharing options", "", String.valueOf(groupId));

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppConstants.GROUPS_BASE_SHARE_URL + postList.get(position).getUrl();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                break;

            case R.id.upvoteCommentContainer:
            case R.id.upvoteContainer:
                Utils.groupsEvent(getActivity(), "Groups_Discussion", "Helpful", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(groupId));
                if (postList.get(position).getMarkedHelpful() == 0) {


                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);


                }
                if (postList.get(position).getMarkedHelpful() == 1) {

                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                }

                break;
            case R.id.downvoteContainer:
                Utils.groupsEvent(getActivity(), "Groups_Discussion", "not helpful", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postList.get(position).getGroupId()));

                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                break;

            case R.id.whatsappShare:
                String shareUrlWhatsapp = AppConstants.GROUPS_BASE_SHARE_URL + postList.get(position).getUrl();
                AppUtils.shareCampaignWithWhatsApp(getActivity(), shareUrlWhatsapp, "", "", "", "", "");


        }
    }

    private void markAsHelpfulOrUnhelpful(String markType, int position) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(postList.get(position).getGroupId());
        groupActionsRequest.setPostId(postList.get(position).getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        groupActionsRequest.setType(markType);//AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY
        Call<GroupsActionResponse> call = groupsAPI.addAction(groupActionsRequest);
        call.enqueue(groupActionResponseCallback);
    }

    private Callback<GroupsActionResponse> groupActionResponseCallback = new Callback<GroupsActionResponse>() {
        @Override
        public void onResponse(Call<GroupsActionResponse> call, retrofit2.Response<GroupsActionResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    if (response.code() == 400) {
                        try {
                            int patchActionId = 0;
                            String patchActionType = null;

                            String errorBody = new String(response.errorBody().bytes());
                            JSONObject jObject = new JSONObject(errorBody);
                            JSONArray dataArray = jObject.optJSONArray("data");
                            if (dataArray != null) {
                                if (dataArray.getJSONObject(0).get("type").equals(dataArray.getJSONObject(1).get("type"))) {
                                    //Same Action Event
                                    if ("0".equals(dataArray.getJSONObject(0).get("type"))) {
                                        Toast.makeText(getActivity(), "already marked unhelpful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "already marked helpful", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (dataArray.getJSONObject(0).has("id") && !dataArray.getJSONObject(0).isNull("id")) {
                                        patchActionId = dataArray.getJSONObject(0).getInt("id");
                                        patchActionType = dataArray.getJSONObject(1).getString("type");
                                    } else {
                                        patchActionType = dataArray.getJSONObject(0).getString("type");
                                        patchActionId = dataArray.getJSONObject(1).getInt("id");
                                    }
                                    sendUpvoteDownvotePatchRequest(patchActionId, patchActionType);
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
                    GroupsActionResponse groupsActionResponse = response.body();
                    if (groupsActionResponse.getData().getResult().size() == 1) {
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == groupsActionResponse.getData().getResult().get(0).getPostId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() + 1);
                                    postList.get(i).setMarkedHelpful(1);

                                } else {
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() + 1);
                                    postList.get(i).setMarkedHelpful(0);

                                }
                            }
                        }
                    }
                    myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
//                    groupPostResult.setVoted(true);
//                    notifyDataSetChanged();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsActionResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private void sendUpvoteDownvotePatchRequest(int patchActionId, String patchActionType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        GroupActionsPatchRequest groupActionsRequest = new GroupActionsPatchRequest();
        groupActionsRequest.setType(patchActionType);

        Call<GroupsActionResponse> call = groupsAPI.patchAction(patchActionId, groupActionsRequest);
        call.enqueue(patchActionResponseCallback);
    }

    private Callback<GroupsActionResponse> patchActionResponseCallback = new Callback<GroupsActionResponse>() {
        @Override
        public void onResponse(Call<GroupsActionResponse> call, retrofit2.Response<GroupsActionResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionResponse groupsActionResponse = response.body();
                    if (groupsActionResponse.getData().getResult().size() == 1) {
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == groupsActionResponse.getData().getResult().get(0).getPostId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() + 1);
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() - 1);
                                    postList.get(i).setMarkedHelpful(1);

                                } else {
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() + 1);
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() - 1);
                                    postList.get(i).setMarkedHelpful(0);

                                }
                            }
                        }
                    }
                    myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();
//                    groupPostResult.setVoted(true);
//                    notifyDataSetChanged();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsActionResponse> call, Throwable t) {

        }
    };


    private Callback<UserPostSettingResponse> userPostSettingResponseCallback = new Callback<UserPostSettingResponse>() {
        @Override
        public void onResponse(Call<UserPostSettingResponse> call, retrofit2.Response<UserPostSettingResponse> response) {
            progressBar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    UserPostSettingResponse userPostSettingResponse = response.body();
                    setPostCurrentPreferences(userPostSettingResponse);

                    postSettingsContainer.startAnimation(slideAnim);
                    overlayView.startAnimation(fadeAnim);
                    postSettingsContainerMain.setVisibility(View.VISIBLE);
                    postSettingsContainer.setVisibility(View.VISIBLE);
                    overlayView.setVisibility(View.VISIBLE);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private void getCurrentUserPostSettingsStatus(GroupPostResult selectedPost) {
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<UserPostSettingResponse> call = groupsAPI.getPostSettingForUser(selectedPost.getId());
        call.enqueue(userPostSettingResponseCallback);
    }

    private void setPostCurrentPreferences(UserPostSettingResponse userPostSettingResponse) {

        if (selectedPost.getUserId().equals(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId())
                || AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)
                || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
            commentToggleTextView.setVisibility(View.VISIBLE);
        } else {
            commentToggleTextView.setVisibility(View.GONE);
        }

        if (selectedPost.getUserId().equals(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId())) {
            editPostTextView.setVisibility(View.VISIBLE);
            deletePostTextView.setVisibility(View.VISIBLE);
        } else {
            editPostTextView.setVisibility(View.GONE);
            deletePostTextView.setVisibility(View.GONE);
        }

        //No existing settings for this post for this user
        if (userPostSettingResponse.getData().get(0).getResult() == null || userPostSettingResponse.getData().get(0).getResult().size() == 0) {
            savePostTextView.setText(getString(R.string.groups_save_post));
            notificationToggleTextView.setText(getString(R.string.groups_enable_notification));
            currentPostPrefsForUser = null;
            return;
        }
        currentPostPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
        if (currentPostPrefsForUser.getIsBookmarked() == 1) {
            savePostTextView.setText(getString(R.string.groups_remove_post));
        } else {
            savePostTextView.setText(getString(R.string.groups_save_post));
        }

        if (currentPostPrefsForUser.getNotificationOff() == 1) {
            notificationToggleTextView.setText(getString(R.string.groups_enable_notification));
        } else {
            notificationToggleTextView.setText("DISABLE NOTIFICATION");
        }
    }

    private void getAdminPostSettingsStatus(GroupPostResult selectedPost) {
        progressBar.setVisibility(View.VISIBLE);
        pinPostTextView.setVisibility(View.GONE);
        blockUserTextView.setVisibility(View.GONE);
        deletePostTextView.setVisibility(View.GONE);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupPostResponse> call = groupsAPI.getSinglePost(selectedPost.getId());
        call.enqueue(postDetailsResponseCallback);
    }

    private void updatePostCommentSettings(int status) {

        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdateGroupPostRequest updateGroupPostRequest = new UpdateGroupPostRequest();
        updateGroupPostRequest.setGroupId(selectedPost.getId());
        updateGroupPostRequest.setDisableComments(status);

        Call<GroupPostResponse> call = groupsAPI.disablePostComment(selectedPost.getId(), updateGroupPostRequest);
        call.enqueue(postUpdateResponseListener);
    }

    private Callback<GroupPostResponse> postDetailsResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
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
                    setAdminPostPreferences(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void setAdminPostPreferences(GroupPostResponse groupPostResponse) {
        pinPostTextView.setVisibility(View.VISIBLE);
        blockUserTextView.setVisibility(View.VISIBLE);
        deletePostTextView.setVisibility(View.VISIBLE);
        if (groupPostResponse.getData().get(0).getResult().get(0).getIsPinned() == 1) {
            pinPostTextView.setText(getString(R.string.groups_unpin_post));
        } else {
            pinPostTextView.setText(getString(R.string.groups_pin_post));
        }
    }

    private Callback<GroupPostResponse> postUpdateResponseListener = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
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
                    if (groupPostResponse.getData().get(0).getResult().get(0).getDisableComments() == 1) {
                        commentToggleTextView.setText(getString(R.string.groups_enable_comment));
                        selectedPost.setDisableComments(1);
                    } else {
                        commentToggleTextView.setText(getString(R.string.groups_disable_comment));
                        selectedPost.setDisableComments(0);
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
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private void updateUserPostPreferences(String action) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdateUserPostSettingsRequest request = new UpdateUserPostSettingsRequest();
        request.setPostId(selectedPost.getId());
        request.setIsAnno(selectedPost.getIsAnnon());
        request.setUserId(SharedPrefUtils.getUserDetailModel(getContext()).getDynamoId());

        if (currentPostPrefsForUser == null) {
            if ("savePost".equals(action)) {
                request.setIsBookmarked(1);
                request.setNotificationOff(1);
            } else if ("deletePost".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(1);
            } else if ("enableNotif".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(1);
            } else if ("disableNotif".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(0);
            }
            Call<ResponseBody> call = groupsAPI.createNewPostSettingsForUser(request);
            call.enqueue(createPostSettingForUserResponseCallback);
        } else {
            if ("savePost".equals(action)) {
                request.setIsBookmarked(1);
                request.setNotificationOff(currentPostPrefsForUser.getNotificationOff());
            } else if ("deletePost".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(currentPostPrefsForUser.getNotificationOff());
            } else if ("enableNotif".equals(action)) {
                request.setIsBookmarked(currentPostPrefsForUser.getIsBookmarked());
                request.setNotificationOff(1);
            } else if ("disableNotif".equals(action)) {
                request.setIsBookmarked(currentPostPrefsForUser.getIsBookmarked());
                request.setNotificationOff(0);
            }
            Call<UserPostSettingResponse> call = groupsAPI.updatePostSettingsForUser(currentPostPrefsForUser.getId(), request);
            call.enqueue(updatePostSettingForUserResponseCallback);
        }

    }


    private Callback<ResponseBody> createPostSettingForUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    String resData = new String(response.body().bytes());
                    JSONObject jObject = new JSONObject(resData);
                    currentPostPrefsForUser = new UserPostSettingResult();
                    currentPostPrefsForUser.setId(jObject.getJSONObject("data").getJSONObject("result").getInt("id"));
                    if (jObject.getJSONObject("data").getJSONObject("result").getBoolean("notificationOff")) {
                        notificationToggleTextView.setText(getString(R.string.groups_enable_notification));
                    } else {
                        notificationToggleTextView.setText("DISABLE NOTIFICATION");
                    }
                    if (jObject.getJSONObject("data").getJSONObject("result").getBoolean("isBookmarked")) {
                        savePostTextView.setText(getString(R.string.groups_remove_post));
                    } else {
                        savePostTextView.setText(getString(R.string.groups_save_post));
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
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };

    private Callback<UserPostSettingResponse> updatePostSettingForUserResponseCallback = new Callback<UserPostSettingResponse>() {
        @Override
        public void onResponse(Call<UserPostSettingResponse> call, retrofit2.Response<UserPostSettingResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    UserPostSettingResponse userPostSettingResponse = response.body();
                    currentPostPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
                    if (userPostSettingResponse.getData().get(0).getResult().get(0).getNotificationOff() == 1) {
                        notificationToggleTextView.setText(getString(R.string.groups_enable_notification));
                    } else {
                        notificationToggleTextView.setText("DISABLE NOTIFICATION");
                    }
                    if (userPostSettingResponse.getData().get(0).getResult().get(0).getIsBookmarked() == 1) {
                        savePostTextView.setText(getString(R.string.groups_remove_post));
                    } else {
                        savePostTextView.setText(getString(R.string.groups_save_post));
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
        public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {

        }
    };


    private void openEditPostOption() {
        Intent intent = new Intent(getActivity(), GroupsEditPostActivity.class);
        intent.putExtra("postData", selectedPost);
        startActivityForResult(intent, EDIT_POST_REQUEST_CODE);
    }


    private void updateAdminLevelPostPrefs(String actionType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdatePostSettingsRequest request = new UpdatePostSettingsRequest();
        if ("pinPost".equals(actionType)) {
            request.setIsPinned(1);
            request.setIsActive(1);
            request.setPinnedBy(SharedPrefUtils.getUserDetailModel(getContext()).getDynamoId());
        } else if ("unpinPost".equals(actionType)) {
            request.setIsPinned(0);
            request.setIsActive(1);
            request.setPinnedBy(SharedPrefUtils.getUserDetailModel(getContext()).getDynamoId());
        } else if ("blockUser".equals(actionType)) {
            getPostingUsersMembershipDetails(selectedPost.getGroupId(), selectedPost.getUserId());
            return;
        } else if ("markInactive".equals(actionType)) {
            request.setIsActive(0);
            request.setIsPinned(0);
            action = actionType;
        }

        Call<GroupPostResponse> call = groupsAPI.updatePost(selectedPost.getId(), request);
        call.enqueue(updateAdminLvlPostSettingResponseCallback);
    }

    private void getPostingUsersMembershipDetails(int groupId, String postsUserId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsMembershipResponse> call = groupsAPI.getUsersMembershipDetailsForGroup(groupId, postsUserId);
        call.enqueue(getMembershipDetailsReponseCallback);
    }

    private Callback<GroupsMembershipResponse> getMembershipDetailsReponseCallback = new Callback<GroupsMembershipResponse>() {
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
                    GroupsMembershipResponse membershipResponse = response.body();
                    Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                    GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                    UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
                    updateGroupMembershipRequest.setUserId(selectedPost.getUserId());
                    updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED);
                    Call<GroupsMembershipResponse> call1 = groupsAPI.updateMember(membershipResponse.getData().getResult().get(0).getId(), updateGroupMembershipRequest);
                    call1.enqueue(updateGroupMembershipResponseCallback);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
                    postSettingsContainerMain.setVisibility(View.GONE);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<GroupPostResponse> updateAdminLvlPostSettingResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse updatePostResponse = response.body();
                    postSettingsContainerMain.setVisibility(View.GONE);
                    overlayView.setVisibility(View.GONE);
                    postSettingsContainer.setVisibility(View.GONE);
                    skip = 0;
                    limit = 10;
                    isRequestRunning = false;
                    isLastPageReached = false;
                    if ("markInactive".equals(action)) {
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == selectedPost.getId()) {
                                postList.remove(i);
                                break;
                            }
                        }
                    }

                    myFeedPollGenericRecyclerAdapter.notifyDataSetChanged();

                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.savePostTextView:
                Log.d("savePostTextView", "" + selectedPost.getId());
                if (savePostTextView.getText().toString().equals(getString(R.string.groups_save_post))) {
                    updateUserPostPreferences("savePost");
                } else {
                    updateUserPostPreferences("deletePost");
                }
                break;
            case R.id.commentToggleTextView:
                if (commentToggleTextView.getText().toString().equals(getString(R.string.groups_disable_comment))) {
                    updatePostCommentSettings(1);
                } else {
                    updatePostCommentSettings(0);
                }
                break;
            case R.id.notificationToggleTextView:
                Log.d("notifToggleTextView", "" + selectedPost.getId());
                if (notificationToggleTextView.getText().toString().equals("DISABLE NOTIFICATION")) {
                    updateUserPostPreferences("enableNotif");
                } else {
                    updateUserPostPreferences("disableNotif");
                }
                break;
            case R.id.editPostTextView:
                Log.d("editPostTextView", "" + selectedPost.getId());
                openEditPostOption();
                break;
            case R.id.deletePostTextView:
                Log.d("deletePostTextView", "" + selectedPost.getId());
                updateAdminLevelPostPrefs("markInactive");
                break;
            case R.id.blockUserTextView:
                Log.d("blockUserTextView", "" + selectedPost.getId());
                updateAdminLevelPostPrefs("blockUser");
                break;
            case R.id.pinPostTextView:
                Log.d("pinPostTextView", "" + selectedPost.getId());
                if (pinPostTextView.getText().toString().equals(getString(R.string.groups_pin_post))) {
                    updateAdminLevelPostPrefs("pinPost");
                } else {
                    updateAdminLevelPostPrefs("unpinPost");
                }
                break;
            case R.id.reportPostTextView:
                Log.d("reportPostTextView", "" + selectedPost.getId());
                GroupPostReportDialogFragment groupPostReportDialogFragment = new GroupPostReportDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putInt("groupId", selectedPost.getGroupId());
                _args.putInt("postId", selectedPost.getId());
                _args.putString("type", AppConstants.GROUP_REPORT_TYPE_POST);
                groupPostReportDialogFragment.setArguments(_args);
                groupPostReportDialogFragment.setCancelable(true);
                groupPostReportDialogFragment.show(fm, "Choose video report option");
//                reportPostTextView.setText("UNREPORT");
                break;
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
        }
    }


}
