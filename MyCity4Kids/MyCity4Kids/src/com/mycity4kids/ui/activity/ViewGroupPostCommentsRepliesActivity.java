package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.request.AddGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.DeleteGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.EditGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.GroupActionsPatchRequest;
import com.mycity4kids.models.request.GroupCommentActionsRequest;
import com.mycity4kids.models.response.AddGpPostCommentReplyResponse;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.ProfilePic;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.adapter.GroupPostCommentRepliesRecyclerAdapter;
import com.mycity4kids.ui.fragment.AddGpPostCommentReplyDialogFragment;
import com.mycity4kids.ui.fragment.GpPostCommentOptionsDialogFragment;
import com.mycity4kids.ui.fragment.GroupPostReportDialogFragment;
import com.mycity4kids.ui.fragment.TaskFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class ViewGroupPostCommentsRepliesActivity extends BaseActivity implements OnClickListener, GroupPostCommentRepliesRecyclerAdapter.RecyclerViewClickListener,
        GroupMembershipStatus.IMembershipStatus, TaskFragment.TaskCallbacks {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    //    private ArrayList<GroupPostCommentResult> completeResponseList;
    private ArrayList<GroupPostCommentResult> repliesList;
    private int totalRepliesCount;

    private Toolbar mToolbar;
    private RecyclerView repliesRecyclerView;
    private TextView toolbarTitleTextView;
    private ProgressDialog mProgressDialog;

    private GroupPostCommentRepliesRecyclerAdapter groupPostCommentRepliesRecyclerAdapter;
    //    private int childCount;
    private FloatingActionButton openAddReplyDialog;
    //    private int commentPosition;
    private String memberType;
    private boolean commentDisableFlag;
    private int groupId;
    private int postId;
    private GroupPostResult postData;
    private String postType;
    private int responseId;
    private TextView viewPostTextView;
    private int actionItemPosition;
    private String editContent;
    private int editReplyId;
    private int editReplyParentCommentId;
    private int deleteCommentPos;
    private int deleteReplyPos;
    private TaskFragment mTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_post_comment_replies_dialog);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        repliesRecyclerView = (RecyclerView) findViewById(R.id.repliesRecyclerView);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitle);
        openAddReplyDialog = (FloatingActionButton) findViewById(R.id.openAddReplyDialog);
        viewPostTextView = (TextView) findViewById(R.id.viewPostTextView);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        openAddReplyDialog.setOnClickListener(this);
        viewPostTextView.setOnClickListener(this);
        viewPostTextView.setVisibility(View.VISIBLE);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        repliesRecyclerView.setLayoutManager(llm);

//        completeResponseList = new ArrayList<>();
        repliesList = new ArrayList<>();

        groupId = getIntent().getIntExtra("groupId", 0);
        postId = getIntent().getIntExtra("postId", 0);
        responseId = getIntent().getIntExtra("responseId", 0);

        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        groupMembershipStatus.checkMembershipStatus(groupId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());


        groupPostCommentRepliesRecyclerAdapter = new GroupPostCommentRepliesRecyclerAdapter(this, this);
        groupPostCommentRepliesRecyclerAdapter.setData(repliesList);
        repliesRecyclerView.setAdapter(groupPostCommentRepliesRecyclerAdapter);

        repliesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            getCommentReplies();
                        }
                    }
                }
            }
        });

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
                    processRepliesListingResponse1(groupPostResponse);
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

    private void processRepliesListingResponse1(GroupPostCommentResponse response) {
        ArrayList<GroupPostCommentResult> dataList = (ArrayList<GroupPostCommentResult>) response.getData().get(0).getResult();
        totalPostCount = dataList.get(0).getChildCount();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != repliesList && !repliesList.isEmpty()) {
                isLastPageReached = true;
            } else {
            }
        } else {
            formatCommentData(dataList);
            repliesList.addAll(dataList);
        }

        if (dataList.get(0).getChildData() != null) {
            for (int i = 0; i < dataList.get(0).getChildData().size(); i++) {
                repliesList.add(dataList.get(0).getChildData().get(i));
            }
        } else {
            isLastPageReached = true;
        }

        groupPostCommentRepliesRecyclerAdapter.setData(repliesList);
        skip = skip + limit;
        if (skip >= totalPostCount) {
            isLastPageReached = true;
        }
        groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
    }

    private void formatCommentData(ArrayList<GroupPostCommentResult> dataList) {
        for (int j = 0; j < dataList.size(); j++) {
            if (dataList.get(j).getCounts() != null) {
                for (int i = 0; i < dataList.get(j).getCounts().size(); i++) {
                    switch (dataList.get(j).getCounts().get(i).getName()) {
                        case "helpfullCount":
                            dataList.get(j).setHelpfullCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "notHelpfullCount":
                            dataList.get(j).setNotHelpfullCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                    }
                }
            }
        }
    }

    private void getCommentReplies() {
        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsAPI.getPostCommentReplies(groupId, postId, responseId, skip, limit);
        call.enqueue(postCommentRepliesCallback);
    }

    private Callback<GroupPostCommentResponse> postCommentRepliesCallback = new Callback<GroupPostCommentResponse>() {
        @Override
        public void onResponse(Call<GroupPostCommentResponse> call, Response<GroupPostCommentResponse> response) {
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
//                    rearrangePostComment(commentsList);
//                    processPostListingResponse(groupPostResponse);
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
        ArrayList<GroupPostCommentResult> dataList = (ArrayList<GroupPostCommentResult>) response.getData().get(0).getResult();
        totalPostCount = dataList.get(0).getChildCount();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != repliesList && !repliesList.isEmpty()) {
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
            repliesList.addAll(dataList);
//            groupsGenericPostRecyclerAdapter.setHeaderData(selectedGroup);
            groupPostCommentRepliesRecyclerAdapter.setData(repliesList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void updateUi(com.kelltontech.network.Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.commentRootView: {
                GpPostCommentOptionsDialogFragment commentOptionsDialogFragment = new GpPostCommentOptionsDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
//                commentOptionsDialogFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
//                _args.putInt("position", commentPosition);
                _args.putString("responseType", "COMMENT");
                _args.putString(AppConstants.GROUP_MEMBER_TYPE, memberType);
                _args.putString("authorId", repliesList.get(position).getUserId());
                commentOptionsDialogFragment.setArguments(_args);
                commentOptionsDialogFragment.setCancelable(true);
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyRootView: {
                GpPostCommentOptionsDialogFragment commentOptionsDialogFragment = new GpPostCommentOptionsDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
//                commentOptionsDialogFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putInt("position", position);
                _args.putString("responseType", "REPLY");
//                _args.putInt("commentPosition", commentPosition);
                _args.putString(AppConstants.GROUP_MEMBER_TYPE, memberType);
                _args.putString("authorId", repliesList.get(position).getUserId());
                commentOptionsDialogFragment.setArguments(_args);
                commentOptionsDialogFragment.setCancelable(true);
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.upvoteCommentContainer:
            case R.id.upvoteReplyContainer:
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);
                break;
            case R.id.downvoteCommentContainer:
            case R.id.downvoteReplyContainer:
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                break;
        }
    }

    private void markAsHelpfulOrUnhelpful(String markType, int position) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        GroupCommentActionsRequest groupActionsRequest = new GroupCommentActionsRequest();
        groupActionsRequest.setGroupId(repliesList.get(position).getGroupId());
        groupActionsRequest.setPostId(repliesList.get(position).getPostId());
        groupActionsRequest.setResponseId(repliesList.get(position).getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        groupActionsRequest.setType(markType);//AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY
        Call<GroupsActionResponse> call = groupsAPI.addCommentAction(groupActionsRequest);
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
                                    Toast.makeText(BaseApplication.getAppContext(), "already marked unhelpful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(BaseApplication.getAppContext(), "already marked helpful", Toast.LENGTH_SHORT).show();
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
                        for (int i = 0; i < repliesList.size(); i++) {
                            if (repliesList.get(i).getId() == groupsActionResponse.getData().getResult().get(0).getResponseId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    repliesList.get(i).setHelpfullCount(repliesList.get(i).getHelpfullCount() + 1);
                                } else {
                                    repliesList.get(i).setNotHelpfullCount(repliesList.get(i).getNotHelpfullCount() + 1);
                                }
                            }
                        }
                    }
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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
                        for (int i = 0; i < repliesList.size(); i++) {
                            if (repliesList.get(i).getId() == groupsActionResponse.getData().getResult().get(0).getResponseId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    repliesList.get(i).setHelpfullCount(repliesList.get(i).getHelpfullCount() + 1);
                                    repliesList.get(i).setNotHelpfullCount(repliesList.get(i).getNotHelpfullCount() - 1);
                                } else {
                                    repliesList.get(i).setNotHelpfullCount(repliesList.get(i).getNotHelpfullCount() + 1);
                                    repliesList.get(i).setHelpfullCount(repliesList.get(i).getHelpfullCount() - 1);
                                }
                            }
                        }
                    }
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openAddReplyDialog:
                openAddCommentReplyDialog(repliesList.get(0));
                break;
            case R.id.viewPostTextView:
                Intent intent = new Intent(ViewGroupPostCommentsRepliesActivity.this, GroupPostDetailActivity.class);
                intent.putExtra("postId", postId);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
                break;
        }
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

        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender()) ||
                    "m".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                Toast.makeText(this, getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                } else {
                    return;
                }
            } else {

            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            showToast("You are not a member of this group. Please join first.");
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
            finish();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            showToast(getString(R.string.groups_user_blocked_msg));
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
                        openAddReplyDialog.setVisibility(View.GONE);
                        commentDisableFlag = true;
                    } else {
                        openAddReplyDialog.setVisibility(View.VISIBLE);
                        commentDisableFlag = false;
                    }

                    getSinglePostComments();

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
    public void onMembershipStatusFetchFail() {

    }

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
                    repliesList.get(actionItemPosition).setContent(editContent);
//                    if (viewGroupPostCommentsRepliesDialogFragment != null) {
//                        viewGroupPostCommentsRepliesDialogFragment.updateRepliesList(completeResponseList.get(actionItemPosition));
//                    }
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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

    public void addReply(int parentId, String content, Map<String, String> image) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest = new AddGpPostCommentOrReplyRequest();
        addGpPostCommentOrReplyRequest.setGroupId(postData.getGroupId());
        addGpPostCommentOrReplyRequest.setPostId(postData.getId());
        addGpPostCommentOrReplyRequest.setParentId(parentId);
        if (SharedPrefUtils.isUserAnonymous(this)) {
            addGpPostCommentOrReplyRequest.setIsAnnon(1);
        }
        addGpPostCommentOrReplyRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        addGpPostCommentOrReplyRequest.setContent(content);
        addGpPostCommentOrReplyRequest.setMediaUrls(image);
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
                    commentListData.setIsAnnon(responseData.getData().getResult().isAnnon());

                    UserDetailResult userDetailResult = new UserDetailResult();
                    if (responseData.getData().getResult().isAnnon() == 1) {
                    } else {
                        commentListData.setUserId(responseData.getData().getResult().getUserId());
                        UserInfo sharedPrefUser = SharedPrefUtils.getUserDetailModel(ViewGroupPostCommentsRepliesActivity.this);
                        userDetailResult.setDynamoId(sharedPrefUser.getDynamoId());
                        userDetailResult.setUserType(sharedPrefUser.getUserType());
                        userDetailResult.setFirstName(sharedPrefUser.getFirst_name());
                        userDetailResult.setLastName(sharedPrefUser.getLast_name());
                        ProfilePic profilePic = new ProfilePic();
                        profilePic.setClientApp(SharedPrefUtils.getProfileImgUrl(ViewGroupPostCommentsRepliesActivity.this));
                        userDetailResult.setProfilePicUrl(profilePic);
                    }

                    commentListData.setUserInfo(userDetailResult);
                    SharedPrefUtils.clearSavedReplyData(ViewGroupPostCommentsRepliesActivity.this, groupId, postId, responseData.getData().getResult().getParentId());
                    repliesList.add(commentListData);
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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
                    for (int i = 0; i < repliesList.size(); i++) {
                        if (repliesList.get(i).getId() == editReplyParentCommentId) {
                            for (int j = 0; j < repliesList.get(i).getChildData().size(); j++) {
                                if (repliesList.get(i).getChildData().get(j).getId() == editReplyId) {
                                    repliesList.get(i).getChildData().get(j).setContent(editContent);
//                                    if (viewGroupPostCommentsRepliesDialogFragment != null) {
//                                        viewGroupPostCommentsRepliesDialogFragment.updateRepliesList(completeResponseList.get(i));
//                                    }
                                    isReplyUpdated = true;
                                    break;
                                }
                            }
                        }
                        if (isReplyUpdated) {
                            break;
                        }
                    }
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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

    public void onResponseDelete(int commentPos, int replyPos, String responseType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        DeleteGpPostCommentOrReplyRequest deleteGpPostCommentOrReplyRequest = new DeleteGpPostCommentOrReplyRequest();
        deleteGpPostCommentOrReplyRequest.setIsActive(0);
        // position-1 to adjust for the comment added on the top of reply list
        if ("REPLY".equals(responseType)) {
            deleteCommentPos = commentPos;
            deleteReplyPos = replyPos;
            Call<AddGpPostCommentReplyResponse> call = groupsAPI.deleteCommentOrReply(repliesList.get(deleteReplyPos).getId(),
                    deleteGpPostCommentOrReplyRequest);
            call.enqueue(deleteReplyResponseListener);
        } else {
            actionItemPosition = replyPos;
            Call<AddGpPostCommentReplyResponse> call = groupsAPI.deleteCommentOrReply(repliesList.get(actionItemPosition).getId(),
                    deleteGpPostCommentOrReplyRequest);
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
                    repliesList.remove(actionItemPosition);
//                    if (viewGroupPostCommentsRepliesDialogFragment != null) {
//                        viewGroupPostCommentsRepliesDialogFragment.dismiss();
//                    }
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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
                    repliesList.get(deleteCommentPos).getChildData().remove(deleteReplyPos);
                    repliesList.get(deleteCommentPos).setChildCount(repliesList.get(deleteCommentPos).getChildCount() - 1);
//                    if (viewGroupPostCommentsRepliesDialogFragment != null) {
//                        viewGroupPostCommentsRepliesDialogFragment.updateRepliesList(completeResponseList.get(deleteCommentPos));
//                        if (completeResponseList.get(deleteCommentPos).getChildCount() == 0) {
//                            viewGroupPostCommentsRepliesDialogFragment.dismiss();
//                        }
//                    }
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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
            _args.putParcelable("parentCommentData", repliesList.get(commentPosition).getChildData().get(position - 1));
        } else {
            _args.putString("action", "EDIT_COMMENT");
            _args.putParcelable("parentCommentData", repliesList.get(position));
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
            _args.putInt("responseId", repliesList.get(commentPosition).getChildData().get(position - 1).getId());
//            _args.putParcelable("parentCommentData", completeResponseList.get(commentPosition).getChildData().get(position - 1));
        } else {
            _args.putInt("responseId", repliesList.get(position).getId());
//            _args.putParcelable("parentCommentData", completeResponseList.get(position));
        }
        _args.putString("type", AppConstants.GROUP_REPORT_TYPE_COMMENT);
        groupPostReportDialogFragment.setArguments(_args);
        groupPostReportDialogFragment.setCancelable(true);
        groupPostReportDialogFragment.show(fm, "Choose report option");
//        reportPostTextView.setText("UNREPORT");
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

    public void processImage(Uri imageUri) {
        android.app.FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("uri", imageUri);
            mTaskFragment.setArguments(bundle);
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        } else {
            showToast("You can add only 1 image in comments");
        }
    }

    @Override
    public void onPreExecute() {
        showProgressDialog(getString(R.string.please_wait));
    }

    @Override
    public void onCancelled() {
        removeProgressDialog();
    }

    @Override
    public void onPostExecute(Bitmap image) {
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("Add Comment");
        if (prev == null) {

        } else {
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);
            Uri imageUriTemp = Uri.parse(path);
            File file2 = FileUtils.getFile(this, imageUriTemp);
            removeProgressDialog();
            ((AddGpPostCommentReplyDialogFragment) prev).sendUploadProfileImageRequest(file2);
        }
    }
}