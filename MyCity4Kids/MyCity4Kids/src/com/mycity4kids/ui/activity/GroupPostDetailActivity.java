package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.AddGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.DeleteGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.EditGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.GroupActionsPatchRequest;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.request.UpdateGroupPostRequest;
import com.mycity4kids.models.request.UpdatePostSettingsRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.response.AddGpPostCommentReplyResponse;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.ProfilePic;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.adapter.GroupPostDetailsAndCommentsRecyclerAdapter;
import com.mycity4kids.ui.fragment.AddGpPostCommentReplyDialogFragment;
import com.mycity4kids.ui.fragment.GpPostCommentOptionsDialogFragment;
import com.mycity4kids.ui.fragment.GroupPostReportDialogFragment;
import com.mycity4kids.ui.fragment.ViewGroupPostCommentsRepliesDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/4/18.
 */

public class GroupPostDetailActivity extends BaseActivity implements View.OnClickListener, GroupPostDetailsAndCommentsRecyclerAdapter.RecyclerViewClickListener, GroupMembershipStatus.IMembershipStatus {

    private GroupPostDetailsAndCommentsRecyclerAdapter groupPostDetailsAndCommentsRecyclerAdapter;

    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private boolean isLoading;
    private ArrayList<GroupPostCommentResult> completeResponseList;
    private String postType;
    private int groupId;
    private int postId;
    private GroupPostResult selectedPost;
    private UserPostSettingResult currentPostPrefsForUser;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;

    private Animation slideAnim, fadeAnim;

    private RecyclerView recyclerView;
    private HashMap<String, String> mediaUrls, pollOptions;
    private GroupPostResult postData;
    private Toolbar toolbar;
    private ViewGroupPostCommentsRepliesDialogFragment viewGroupPostCommentsRepliesDialogFragment;
    private LinearLayout postSettingsContainer;
    private RelativeLayout postSettingsContainerMain;
    private View overlayView;
    private TextView savePostTextView, notificationToggleTextView, commentToggleTextView, reportPostTextView, deletePostTextView, blockUserTextView, pinPostTextView;
    private ProgressBar progressBar;
    private FloatingActionButton openAddCommentDialog;
    private int actionItemPosition;
    private String editContent;
    private int editReplyId;
    private int editReplyParentCommentId;
    private int deleteCommentPos;
    private int deleteReplyPos;
    private String memberType;
    private int responseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_post_detail_activity);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        postSettingsContainer = (LinearLayout) findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = (RelativeLayout) findViewById(R.id.postSettingsContainerMain);
        overlayView = findViewById(R.id.overlayView);
        savePostTextView = (TextView) findViewById(R.id.savePostTextView);
        notificationToggleTextView = (TextView) findViewById(R.id.notificationToggleTextView);
        commentToggleTextView = (TextView) findViewById(R.id.commentToggleTextView);
        deletePostTextView = (TextView) findViewById(R.id.deletePostTextView);
        blockUserTextView = (TextView) findViewById(R.id.blockUserTextView);
        pinPostTextView = (TextView) findViewById(R.id.pinPostTextView);

        reportPostTextView = (TextView) findViewById(R.id.reportPostTextView);
        openAddCommentDialog = (FloatingActionButton) findViewById(R.id.openAddCommentDialog);

        groupId = getIntent().getIntExtra("groupId", 0);
        postId = getIntent().getIntExtra("postId", 0);
        responseId = getIntent().getIntExtra("responseId", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        slideAnim = AnimationUtils.loadAnimation(this, R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

        savePostTextView.setOnClickListener(this);
        notificationToggleTextView.setOnClickListener(this);
        commentToggleTextView.setOnClickListener(this);
        reportPostTextView.setOnClickListener(this);
        overlayView.setOnClickListener(this);
        openAddCommentDialog.setOnClickListener(this);
        deletePostTextView.setOnClickListener(this);
        blockUserTextView.setOnClickListener(this);
        pinPostTextView.setOnClickListener(this);

        completeResponseList = new ArrayList<>();
        completeResponseList.add(new GroupPostCommentResult()); // Empty element for Header position

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        groupMembershipStatus.checkMembershipStatus(groupId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

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
                            getPostComments();
                        }
                    }
                }
            }
        });
    }

    private void getPostDetails() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupPostResponse> call = groupsAPI.getSinglePost(postId);
        call.enqueue(postDetailsResponseCallback);
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
                    postData = groupPostResponse.getData().get(0).getResult().get(0);

                    if (postData.getType().equals("0")) {
                        postType = AppConstants.POST_TYPE_TEXT;
                    } else if (postData.getType().equals("1")) {
                        postType = AppConstants.POST_TYPE_MEDIA;
                    } else if (postData.getType().equals("2")) {
                        if (postData.getPollType().equals("1")) {
                            postType = AppConstants.POST_TYPE_IMAGE_POLL;
                        } else {
                            postType = AppConstants.POST_TYPE_TEXT_POLL;
                        }
                    }

                    if (postData.getDisableComments() == 1) {
                        openAddCommentDialog.setVisibility(View.GONE);
                    }

                    if (postType.equals("1")) {
                        mediaUrls = (HashMap<String, String>) getIntent().getSerializableExtra("mediaUrls");
                        postData.setMediaUrls(mediaUrls);
                    } else if (postType.equals("2")) {
                        pollOptions = (HashMap<String, String>) getIntent().getSerializableExtra("pollOptions");
                        postData.setPollOptions(pollOptions);
                    }

                    formatPostData();

                    groupPostDetailsAndCommentsRecyclerAdapter = new GroupPostDetailsAndCommentsRecyclerAdapter(GroupPostDetailActivity.this, GroupPostDetailActivity.this, postType);
                    groupPostDetailsAndCommentsRecyclerAdapter.setData(postData, completeResponseList);
                    recyclerView.setAdapter(groupPostDetailsAndCommentsRecyclerAdapter);
                    if (responseId == 0) {
                        getPostComments();
                    } else {
                        getSinglePostComments();
                    }

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

    private void formatPostData() {
        for (int i = 0; i < postData.getCounts().size(); i++) {
            switch (postData.getCounts().get(i).getName()) {
                case "helpfullCount":
                    postData.setHelpfullCount(postData.getCounts().get(i).getCount());
                    break;
                case "notHelpfullCount":
                    postData.setNotHelpfullCount(postData.getCounts().get(i).getCount());
                    break;
                case "responseCount":
                    postData.setResponseCount(postData.getCounts().get(i).getCount());
                    break;
                case "votesCount": {
                    for (int j = 0; j < postData.getCounts().get(i).getCounts().size(); j++) {
                        postData.setTotalVotesCount(postData.getTotalVotesCount() + postData.getCounts().get(i).getCounts().get(j).getCount());
                        switch (postData.getCounts().get(i).getCounts().get(j).getName()) {
                            case "option1":
                                postData.setOption1VoteCount(postData.getCounts().get(i).getCounts().get(j).getCount());
                                break;
                            case "option2":
                                postData.setOption2VoteCount(postData.getCounts().get(i).getCounts().get(j).getCount());
                                break;
                            case "option3":
                                postData.setOption3VoteCount(postData.getCounts().get(i).getCounts().get(j).getCount());
                                break;
                            case "option4":
                                postData.setOption4VoteCount(postData.getCounts().get(i).getCounts().get(j).getCount());
                                break;
                        }
                    }
                }
                break;
            }
        }
    }

    private void getPostComments() {
        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsAPI.getPostComments(postData.getGroupId(), postData.getId(), skip, limit);
        call.enqueue(postCommentCallback);
    }

    private void getSinglePostComments() {
        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsAPI.getSinglePostComments(postData.getGroupId(), postData.getId(), responseId);
        call.enqueue(postCommentCallback);
    }

    private Callback<GroupPostCommentResponse> postCommentCallback = new Callback<GroupPostCommentResponse>() {
        @Override
        public void onResponse(Call<GroupPostCommentResponse> call, retrofit2.Response<GroupPostCommentResponse> response) {
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
                    GroupPostCommentResponse groupPostResponse = response.body();
                    processRepliesListingResponse(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostCommentResponse> call, Throwable t) {
            isReuqestRunning = false;
        }
    };

    private void processRepliesListingResponse(GroupPostCommentResponse response) {
        totalPostCount = response.getTotal();
        ArrayList<GroupPostCommentResult> dataList = (ArrayList<GroupPostCommentResult>) response.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != completeResponseList && !completeResponseList.isEmpty()) {
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
//            noPostsTextView.setVisibility(View.GONE);
            completeResponseList.addAll(dataList);
//            groupsGenericPostRecyclerAdapter.setHeaderData(selectedGroup);
            groupPostDetailsAndCommentsRecyclerAdapter.setData(postData, completeResponseList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.commentRootView: {
                GpPostCommentOptionsDialogFragment commentOptionsDialogFragment = new GpPostCommentOptionsDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                _args.putInt("position", position);
                _args.putString("authorId", completeResponseList.get(position).getUserId());
                _args.putString(AppConstants.GROUP_MEMBER_TYPE, memberType);
                _args.putString("responseType", "COMMENT");
                commentOptionsDialogFragment.setArguments(_args);
                commentOptionsDialogFragment.setCancelable(true);
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyCommentTextView: {
                openAddCommentReplyDialog(completeResponseList.get(position));
            }
            break;
            case R.id.replyCountTextView:
                viewGroupPostCommentsRepliesDialogFragment = new ViewGroupPostCommentsRepliesDialogFragment();
                Bundle _args = new Bundle();
                _args.putParcelable("commentReplies", completeResponseList.get(position));
                _args.putInt("childCount", completeResponseList.get(position).getChildCount());
                _args.putInt("position", position);
                _args.putString(AppConstants.GROUP_MEMBER_TYPE, memberType);
                viewGroupPostCommentsRepliesDialogFragment.setArguments(_args);
                FragmentManager fm = getSupportFragmentManager();
                viewGroupPostCommentsRepliesDialogFragment.show(fm, "Replies");
                break;
            case R.id.postSettingImageView:
                getCurrentPostSettingsStatus(postData);
                if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)
                        || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
                    getAdminPostSettingsStatus(postData);
                }
                if (postData.getDisableComments() == 1) {
                    commentToggleTextView.setText("ENABLE COMMENTS");
                } else {
                    commentToggleTextView.setText("DISABLE COMMENTS");
                }
                postSettingsContainer.startAnimation(slideAnim);
                overlayView.startAnimation(fadeAnim);
                postSettingsContainerMain.setVisibility(View.VISIBLE);
                postSettingsContainer.setVisibility(View.VISIBLE);
                overlayView.setVisibility(View.VISIBLE);
                break;
            case R.id.upvoteContainer:
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY);
                break;
            case R.id.downvoteContainer:
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY);
                break;
        }
    }

    private void getAdminPostSettingsStatus(GroupPostResult selectedPost) {
//        progressBar.setVisibility(View.VISIBLE);
        pinPostTextView.setVisibility(View.GONE);
        blockUserTextView.setVisibility(View.GONE);
        deletePostTextView.setVisibility(View.GONE);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupPostResponse> call = groupsAPI.getSinglePost(selectedPost.getId());
        call.enqueue(postAdminDetailsResponseCallback);
    }

    private Callback<GroupPostResponse> postAdminDetailsResponseCallback = new Callback<GroupPostResponse>() {
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
            pinPostTextView.setText("UNPIN THIS POST TO TOP");
        } else {
            pinPostTextView.setText("PIN THIS POST TO TOP");
        }
    }

    private void markAsHelpfulOrUnhelpful(String markType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(postData.getGroupId());
        groupActionsRequest.setPostId(postData.getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
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

                            if (dataArray.getJSONObject(0).get("type").equals(dataArray.getJSONObject(1).get("type"))) {
                                //Same Action Event
                                if ("0".equals(dataArray.getJSONObject(0).get("type"))) {
                                    showToast("already marked unhelpful");
                                } else {
                                    showToast("already marked helpful");
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
                        if (postData.getId() == groupsActionResponse.getData().getResult().get(0).getPostId()) {
                            if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                postData.setHelpfullCount(postData.getHelpfullCount() + 1);
                            } else {
                                postData.setNotHelpfullCount(postData.getNotHelpfullCount() + 1);
                            }
                        }
                    }
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
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
                        if (postData.getId() == groupsActionResponse.getData().getResult().get(0).getPostId()) {
                            if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                postData.setHelpfullCount(postData.getHelpfullCount() + 1);
                                postData.setNotHelpfullCount(postData.getNotHelpfullCount() - 1);
                            } else {
                                postData.setNotHelpfullCount(postData.getNotHelpfullCount() + 1);
                                postData.setHelpfullCount(postData.getHelpfullCount() - 1);
                            }
                        }
                    }
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
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

    private void getCurrentPostSettingsStatus(GroupPostResult selectedPost) {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<UserPostSettingResponse> call = groupsAPI.getPostSettingForUser(selectedPost.getId());
        call.enqueue(userPostSettingResponseCallback);
    }

    private Callback<UserPostSettingResponse> userPostSettingResponseCallback = new Callback<UserPostSettingResponse>() {
        @Override
        public void onResponse(Call<UserPostSettingResponse> call, retrofit2.Response<UserPostSettingResponse> response) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void setPostCurrentPreferences(UserPostSettingResponse userPostSettingResponse) {

        if (postData.getUserId().equals(SharedPrefUtils.getUserDetailModel(GroupPostDetailActivity.this).getDynamoId())
                || AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)
                || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
            commentToggleTextView.setVisibility(View.VISIBLE);
        } else {
            commentToggleTextView.setVisibility(View.GONE);
        }

        //No existing settings for this post for this user
        if (userPostSettingResponse.getData().get(0).getResult() == null || userPostSettingResponse.getData().get(0).getResult().size() == 0) {
            savePostTextView.setText("SAVE POST");
            notificationToggleTextView.setText("ENABLE NOTIFICATION");
            currentPostPrefsForUser = null;
            return;
        }
        currentPostPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
        if (currentPostPrefsForUser.getIsBookmarked() == 1) {
            savePostTextView.setText("UNSAVE POST");
        } else {
            savePostTextView.setText("SAVE POST");
        }

        if (currentPostPrefsForUser.getNotificationOff() == 1) {
            notificationToggleTextView.setText("ENABLE NOTIFICATION");
        } else {
            notificationToggleTextView.setText("DISABLE NOTIFICATION");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deletePostTextView:
                Log.d("deletePostTextView", "" + postData.getId());
                updateAdminLevelPostPrefs("markInactive");
                break;
            case R.id.blockUserTextView:
                Log.d("blockUserTextView", "" + postData.getId());
                updateAdminLevelPostPrefs("blockUser");
                break;
            case R.id.pinPostTextView:
                Log.d("pinPostTextView", "" + postData.getId());
                if (pinPostTextView.getText().toString().equals("PIN THIS POST TO TOP")) {
                    updateAdminLevelPostPrefs("pinPost");
                } else {
                    updateAdminLevelPostPrefs("unpinPost");
                }
                break;
            case R.id.savePostTextView:
                Log.d("savePostTextView", "" + postData.getId());
                if (savePostTextView.getText().toString().equals("SAVE POST")) {
                    updateUserPostPreferences("savePost");
                } else {
                    updateUserPostPreferences("deletePost");
                }
                break;
            case R.id.commentToggleTextView:
                if (commentToggleTextView.getText().toString().equals("DISABLE COMMENTS")) {
                    updatePostCommentSettings(1);
                } else {
                    updatePostCommentSettings(0);
                }
                break;
            case R.id.notificationToggleTextView:
                Log.d("notifToggleTextView", "" + postData.getId());
                if (notificationToggleTextView.getText().toString().equals("DISABLE NOTIFICATION")) {
                    updateUserPostPreferences("enableNotif");
                } else {
                    updateUserPostPreferences("disableNotif");
                }
                break;
            case R.id.openAddCommentDialog: {
                AddGpPostCommentReplyDialogFragment addGpPostCommentReplyDialogFragment = new AddGpPostCommentReplyDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                addGpPostCommentReplyDialogFragment.setArguments(_args);
                addGpPostCommentReplyDialogFragment.setCancelable(true);
                addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
            }
            break;
            case R.id.reportPostTextView:
                Log.d("reportPostTextView", "" + postData.getId());
                GroupPostReportDialogFragment groupPostReportDialogFragment = new GroupPostReportDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                _args.putInt("groupId", selectedPost.getGroupId());
                _args.putInt("postId", selectedPost.getId());
                _args.putString("type", AppConstants.GROUP_REPORT_TYPE_POST);
                groupPostReportDialogFragment.setArguments(_args);
                groupPostReportDialogFragment.setCancelable(true);
                groupPostReportDialogFragment.show(fm, "Choose video report option");
                reportPostTextView.setText("Unreport");
                break;
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
        }
    }

    private void updateAdminLevelPostPrefs(String actionType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdatePostSettingsRequest request = new UpdatePostSettingsRequest();
        if ("pinPost".equals(actionType)) {
            request.setIsPinned(1);
        } else if ("unpinPost".equals(actionType)) {
            request.setIsPinned(0);
        } else if ("blockUser".equals(actionType)) {
            getPostingUsersMembershipDetails(postData.getGroupId(), postData.getUserId());
            return;
        } else if ("markInactive".equals(actionType)) {
            request.setIsActive(0);
        }

        Call<GroupPostResponse> call = groupsAPI.updatePost(postData.getId(), request);
        call.enqueue(updateAdminLvlPostSettingResponseCallback);
    }

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
                    if (updatePostResponse.getData().get(0).getResult().get(0).getIsPinned() == 1) {
                        pinPostTextView.setText("UNPIN THIS POST TO TOP");
                    } else {
                        pinPostTextView.setText("PIN THIS POST TO TOP");
                    }
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
                    Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                    GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                    UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
                    updateGroupMembershipRequest.setUserId(postData.getUserId());
                    updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED);
                    Call<GroupsMembershipResponse> call1 = groupsAPI.updateMember(postData.getId(), updateGroupMembershipRequest);
                    call1.enqueue(updateGroupMembershipResponseCallback);
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

    private void updatePostCommentSettings(int status) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdateGroupPostRequest updateGroupPostRequest = new UpdateGroupPostRequest();
        updateGroupPostRequest.setGroupId(postData.getGroupId());
        updateGroupPostRequest.setDisableComments(status);

        Call<GroupPostResponse> call = groupsAPI.disablePostComment(postData.getId(), updateGroupPostRequest);
        call.enqueue(postUpdateResponseListener);
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
                        commentToggleTextView.setText("ENABLE COMMENTS");
                    } else {
                        commentToggleTextView.setText("DISABLE COMMENTS");
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
        request.setPostId(postData.getId());
        request.setIsAnno(postData.getIsAnnon());
        request.setUserId(postData.getUserId());
        Call<UserPostSettingResponse> call;
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
            call = groupsAPI.createNewPostSettingsForUser(request);
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
            call = groupsAPI.updatePostSettingsForUser(currentPostPrefsForUser.getId(), request);
        }
        call.enqueue(updatePostSettingForUserResponseCallback);
    }

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
                    if (userPostSettingResponse.getData().get(0).getResult().get(0).getNotificationOff() == 1) {
                        notificationToggleTextView.setText("ENABLE NOTIFICATION");
                    } else {
                        notificationToggleTextView.setText("DISABLE NOTIFICATION");
                    }
                    if (userPostSettingResponse.getData().get(0).getResult().get(0).getIsBookmarked() == 1) {
                        savePostTextView.setText("UNSAVE POST");
                    } else {
                        savePostTextView.setText("SAVE POST");
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
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void addComment(String content) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest = new AddGpPostCommentOrReplyRequest();
        addGpPostCommentOrReplyRequest.setGroupId(postData.getGroupId());
        addGpPostCommentOrReplyRequest.setPostId(postData.getId());
        addGpPostCommentOrReplyRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        addGpPostCommentOrReplyRequest.setContent(content);
        Call<AddGpPostCommentReplyResponse> call = groupsAPI.addPostCommentOrReply(addGpPostCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);
    }

    private Callback<AddGpPostCommentReplyResponse> addCommentResponseListener = new Callback<AddGpPostCommentReplyResponse>() {
        @Override
        public void onResponse(Call<AddGpPostCommentReplyResponse> call, retrofit2.Response<AddGpPostCommentReplyResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to add comment. Please try again");
                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGpPostCommentReplyResponse groupPostResponse = response.body();
                    GroupPostCommentResult groupPostCommentResult = new GroupPostCommentResult();
                    groupPostCommentResult.setId(groupPostResponse.getData().getResult().getId());
                    groupPostCommentResult.setContent(groupPostResponse.getData().getResult().getContent());
                    groupPostCommentResult.setSentiment(groupPostResponse.getData().getResult().getSentiment());
                    groupPostCommentResult.setParentId(groupPostResponse.getData().getResult().getParentId());
                    groupPostCommentResult.setGroupId(groupPostResponse.getData().getResult().getGroupId());
                    groupPostCommentResult.setPostId(groupPostResponse.getData().getResult().getPostId());
                    groupPostCommentResult.setUserId(groupPostResponse.getData().getResult().getUserId());
                    groupPostCommentResult.setIsActive(groupPostResponse.getData().getResult().isActive());
                    groupPostCommentResult.setIsAnnon(groupPostResponse.getData().getResult().isAnnon());
                    groupPostCommentResult.setModerationStatus(groupPostResponse.getData().getResult().getModerationStatus());
                    groupPostCommentResult.setModeratedBy(groupPostResponse.getData().getResult().getModeratedBy());
                    groupPostCommentResult.setModeratedOn(groupPostResponse.getData().getResult().getModeratedon());
                    groupPostCommentResult.setLang(groupPostResponse.getData().getResult().getLang());
                    groupPostCommentResult.setCreatedAt(groupPostResponse.getData().getResult().getCreatedAt());
                    groupPostCommentResult.setUpdatedAt(groupPostResponse.getData().getResult().getUpdatedAt());

                    UserDetailResult userDetailResult = new UserDetailResult();
                    UserInfo userInfo = SharedPrefUtils.getUserDetailModel(GroupPostDetailActivity.this);
                    userDetailResult.setDynamoId(userInfo.getDynamoId());
                    userDetailResult.setUserType(userInfo.getUserType());
                    userDetailResult.setFirstName(userInfo.getFirst_name());
                    userDetailResult.setLastName(userInfo.getLast_name());
                    ProfilePic profilePic = new ProfilePic();
                    profilePic.setClientApp(SharedPrefUtils.getProfileImgUrl(GroupPostDetailActivity.this));
                    userDetailResult.setProfilePicUrl(profilePic);

                    groupPostCommentResult.setUserInfo(userDetailResult);
                    completeResponseList.add(1, groupPostCommentResult);
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
                } else {
                    showToast("Failed to add comment. Please try again");
                }
            } catch (Exception e) {
                showToast("Failed to add comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
            showToast("Failed to add comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editComment(int id, String updateContent, int position) {
        actionItemPosition = position;
        editContent = updateContent;
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        EditGpPostCommentOrReplyRequest editGpPostCommentOrReplyRequest = new EditGpPostCommentOrReplyRequest();
        editGpPostCommentOrReplyRequest.setContent(updateContent);
        Call<AddGpPostCommentReplyResponse> call = groupsAPI.editPostCommentOrReply(id, editGpPostCommentOrReplyRequest);
        call.enqueue(editCommentResponseListener);
    }

    private Callback<AddGpPostCommentReplyResponse> editCommentResponseListener = new Callback<AddGpPostCommentReplyResponse>() {
        @Override
        public void onResponse(Call<AddGpPostCommentReplyResponse> call, retrofit2.Response<AddGpPostCommentReplyResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to edit comment. Please try again");
                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGpPostCommentReplyResponse groupPostResponse = response.body();
                    completeResponseList.get(actionItemPosition).setContent(editContent);
                    if (viewGroupPostCommentsRepliesDialogFragment != null) {
                        viewGroupPostCommentsRepliesDialogFragment.updateRepliesList(completeResponseList.get(actionItemPosition));
                    }
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
                } else {
                    showToast("Failed to edit comment. Please try again");
                }
            } catch (Exception e) {
                showToast("Failed to edit comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
            showToast("Failed to edit comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void addReply(int parentId, String content) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest = new AddGpPostCommentOrReplyRequest();
        addGpPostCommentOrReplyRequest.setGroupId(postData.getGroupId());
        addGpPostCommentOrReplyRequest.setPostId(postData.getId());
        addGpPostCommentOrReplyRequest.setParentId(parentId);
        addGpPostCommentOrReplyRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        addGpPostCommentOrReplyRequest.setContent(content);
        Call<AddGpPostCommentReplyResponse> call = groupsAPI.addPostCommentOrReply(addGpPostCommentOrReplyRequest);
        call.enqueue(addReplyResponseListener);
    }

    private Callback<AddGpPostCommentReplyResponse> addReplyResponseListener = new Callback<AddGpPostCommentReplyResponse>() {
        @Override
        public void onResponse(Call<AddGpPostCommentReplyResponse> call, retrofit2.Response<AddGpPostCommentReplyResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to add reply. Please try again");

                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGpPostCommentReplyResponse responseData = response.body();

                    GroupPostCommentResult commentListData = new GroupPostCommentResult();
                    commentListData.setId(responseData.getData().getResult().getId());
                    commentListData.setContent(responseData.getData().getResult().getContent());
                    commentListData.setCreatedAt(responseData.getData().getResult().getCreatedAt());
                    commentListData.setPostId(responseData.getData().getResult().getPostId());
                    commentListData.setParentId(responseData.getData().getResult().getParentId());
                    commentListData.setUserId(responseData.getData().getResult().getUserId());

                    UserDetailResult userDetailResult = new UserDetailResult();
                    UserInfo sharedPrefUser = SharedPrefUtils.getUserDetailModel(GroupPostDetailActivity.this);
                    userDetailResult.setDynamoId(sharedPrefUser.getDynamoId());
                    userDetailResult.setUserType(sharedPrefUser.getUserType());
                    userDetailResult.setFirstName(sharedPrefUser.getFirst_name());
                    userDetailResult.setLastName(sharedPrefUser.getLast_name());

                    ProfilePic profilePic = new ProfilePic();
                    profilePic.setClientApp(SharedPrefUtils.getProfileImgUrl(GroupPostDetailActivity.this));
                    userDetailResult.setProfilePicUrl(profilePic);

                    commentListData.setUserInfo(userDetailResult);

                    for (int i = 0; i < completeResponseList.size(); i++) {
                        if (completeResponseList.get(i).getId() == responseData.getData().getResult().getParentId()) {
                            completeResponseList.get(i).getChildData().add(0, commentListData);
                            completeResponseList.get(i).setChildCount(completeResponseList.get(i).getChildCount() + 1);
                            if (viewGroupPostCommentsRepliesDialogFragment != null) {
                                viewGroupPostCommentsRepliesDialogFragment.updateRepliesList(completeResponseList.get(i));
                            }
                            break;
                        }
                    }
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
//                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "add", "reply");
                } else {
                    showToast("Failed to add reply. Please try again");
                }
            } catch (Exception e) {
                showToast("Failed to add reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
            showToast("Failed to add reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editReply(String updatedReply, int parentCommentId, int replyId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        EditGpPostCommentOrReplyRequest editGpPostCommentOrReplyRequest = new EditGpPostCommentOrReplyRequest();
        editGpPostCommentOrReplyRequest.setContent(updatedReply);
        Call<AddGpPostCommentReplyResponse> call = groupsAPI.editPostCommentOrReply(replyId, editGpPostCommentOrReplyRequest);
        call.enqueue(editReplyResponseListener);
        editReplyId = replyId;
        editReplyParentCommentId = parentCommentId;
        editContent = updatedReply;
    }


    private Callback<AddGpPostCommentReplyResponse> editReplyResponseListener = new Callback<AddGpPostCommentReplyResponse>() {
        @Override
        public void onResponse(Call<AddGpPostCommentReplyResponse> call, retrofit2.Response<AddGpPostCommentReplyResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to edit reply. Please try again");

                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGpPostCommentReplyResponse responseData = response.body();
                    boolean isReplyUpdated = false;
                    for (int i = 0; i < completeResponseList.size(); i++) {
                        if (completeResponseList.get(i).getId() == editReplyParentCommentId) {
                            for (int j = 0; j < completeResponseList.get(i).getChildData().size(); j++) {
                                if (completeResponseList.get(i).getChildData().get(j).getId() == editReplyId) {
                                    completeResponseList.get(i).getChildData().get(j).setContent(editContent);
                                    if (viewGroupPostCommentsRepliesDialogFragment != null) {
                                        viewGroupPostCommentsRepliesDialogFragment.updateRepliesList(completeResponseList.get(i));
                                    }
                                    isReplyUpdated = true;
                                    break;
                                }
                            }
                        }
                        if (isReplyUpdated) {
                            break;
                        }
                    }
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
//                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "edit", "reply");
                } else {
                    showToast("Failed to edit reply. Please try again");
                }
            } catch (Exception e) {
                showToast("Failed to edit reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
            showToast("Failed to edit reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private boolean validateCommentText() {
//        if (StringUtils.isNullOrEmpty(writeCommentEditText.getText().toString())) {
//            Toast.makeText(this, "Please add a comment", Toast.LENGTH_LONG).show();
//            return false;
//        }
        return true;
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

    public void onResponseDelete(int commentPos, int replyPos, String responseType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        DeleteGpPostCommentOrReplyRequest deleteGpPostCommentOrReplyRequest = new DeleteGpPostCommentOrReplyRequest();
        deleteGpPostCommentOrReplyRequest.setIsActive(0);
        // position-1 to adjust for the comment added on the top of reply list
        if ("REPLY".equals(responseType)) {
            deleteCommentPos = commentPos;
            deleteReplyPos = replyPos - 1;
            Call<AddGpPostCommentReplyResponse> call = groupsAPI.deleteCommentOrReply(completeResponseList.get(commentPos).getChildData().get(deleteReplyPos).getId(),
                    deleteGpPostCommentOrReplyRequest);
            call.enqueue(deleteReplyResponseListener);
        } else {
            actionItemPosition = replyPos;
            Call<AddGpPostCommentReplyResponse> call = groupsAPI.deleteCommentOrReply(completeResponseList.get(actionItemPosition).getId(), deleteGpPostCommentOrReplyRequest);
            call.enqueue(deleteCommentResponseListener);
        }
    }

    private Callback<AddGpPostCommentReplyResponse> deleteCommentResponseListener = new Callback<AddGpPostCommentReplyResponse>() {
        @Override
        public void onResponse(Call<AddGpPostCommentReplyResponse> call, retrofit2.Response<AddGpPostCommentReplyResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to edit reply. Please try again");

                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGpPostCommentReplyResponse responseData = response.body();
                    completeResponseList.remove(actionItemPosition);
                    if (viewGroupPostCommentsRepliesDialogFragment != null) {
                        viewGroupPostCommentsRepliesDialogFragment.dismiss();
                    }
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
//                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "edit", "reply");
                } else {
                    showToast("Failed to edit reply. Please try again");
                }
            } catch (Exception e) {
                showToast("Failed to edit reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
            showToast("Failed to edit reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
    private Callback<AddGpPostCommentReplyResponse> deleteReplyResponseListener = new Callback<AddGpPostCommentReplyResponse>() {
        @Override
        public void onResponse(Call<AddGpPostCommentReplyResponse> call, retrofit2.Response<AddGpPostCommentReplyResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to edit reply. Please try again");

                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGpPostCommentReplyResponse responseData = response.body();
                    completeResponseList.get(deleteCommentPos).getChildData().remove(deleteReplyPos);
                    completeResponseList.get(deleteCommentPos).setChildCount(completeResponseList.get(deleteCommentPos).getChildCount() - 1);
                    if (viewGroupPostCommentsRepliesDialogFragment != null) {
                        viewGroupPostCommentsRepliesDialogFragment.updateRepliesList(completeResponseList.get(deleteCommentPos));
                        if (completeResponseList.get(deleteCommentPos).getChildCount() == 0) {
                            viewGroupPostCommentsRepliesDialogFragment.dismiss();
                        }
                    }
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
//                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "edit", "reply");
                } else {
                    showToast("Failed to edit reply. Please try again");
                }
            } catch (Exception e) {
                showToast("Failed to edit reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
            showToast("Failed to edit reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void onResponseEdit(int commentPosition, int position, String responseType) {
        AddGpPostCommentReplyDialogFragment addGpPostCommentReplyDialogFragment = new AddGpPostCommentReplyDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        if ("REPLY".equals(responseType)) {
            _args.putString("action", "EDIT_REPLY");
            //position-1 to accommodate the parent comment above.
            _args.putParcelable("parentCommentData", completeResponseList.get(commentPosition).getChildData().get(position - 1));
        } else {
            _args.putString("action", "EDIT_COMMENT");
            _args.putParcelable("parentCommentData", completeResponseList.get(position));
        }
        _args.putInt("position", position);

        addGpPostCommentReplyDialogFragment.setArguments(_args);
        addGpPostCommentReplyDialogFragment.setCancelable(true);
        addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    public void onResponseReport(int commentPosition, int position, String responseType) {
        GroupPostReportDialogFragment groupPostReportDialogFragment = new GroupPostReportDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        _args.putInt("groupId", groupId);
        _args.putInt("postId", postData.getId());
        if ("REPLY".equals(responseType)) {
            //position-1 to accommodate the parent comment above.
            _args.putInt("responseId", completeResponseList.get(commentPosition).getChildData().get(position - 1).getId());
//            _args.putParcelable("parentCommentData", completeResponseList.get(commentPosition).getChildData().get(position - 1));
        } else {
            _args.putInt("responseId", completeResponseList.get(position).getId());
//            _args.putParcelable("parentCommentData", completeResponseList.get(position));
        }
        _args.putString("type", AppConstants.GROUP_REPORT_TYPE_COMMENT);
        groupPostReportDialogFragment.setArguments(_args);
        groupPostReportDialogFragment.setCancelable(true);
        groupPostReportDialogFragment.show(fm, "Choose report option");
        reportPostTextView.setText("UNREPORT");
    }


    public void openAddCommentReplyDialog(GroupPostCommentResult data) {
        AddGpPostCommentReplyDialogFragment addGroupCommentReplyDialogFragment = new AddGpPostCommentReplyDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        _args.putParcelable("parentCommentData", data);
        addGroupCommentReplyDialogFragment.setArguments(_args);
        addGroupCommentReplyDialogFragment.setCancelable(true);
        addGroupCommentReplyDialogFragment.show(fm, "Add Replies");
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

        } else {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                memberType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                memberType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            showToast("You are not a member of this group. Please join first.");
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
            finish();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            showToast("You have been blocked from this group");
            finish();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                memberType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                memberType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
            getPostDetails();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("pendingMembershipFlag", true);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }
}
