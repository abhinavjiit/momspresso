package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
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
import com.mycity4kids.ui.fragment.ProcessBitmapTaskFragment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class ViewGroupPostCommentsRepliesActivity extends BaseActivity implements OnClickListener,
        GroupPostCommentRepliesRecyclerAdapter.RecyclerViewClickListener,
        GroupMembershipStatus.IMembershipStatus, ProcessBitmapTaskFragment.TaskCallbacks {

    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private ArrayList<GroupPostCommentResult> repliesList;

    private Toolbar toolbar;
    private RecyclerView repliesRecyclerView;
    private GroupPostCommentRepliesRecyclerAdapter groupPostCommentRepliesRecyclerAdapter;
    private FloatingActionButton openAddReplyDialog;
    private String memberType;
    private int groupId;
    private int postId;
    private GroupPostResult postData;
    private int responseId;
    private TextView viewPostTextView;
    private int actionItemPosition;
    private String editContent;
    private int editReplyId;
    private int editReplyParentCommentId;
    private int deleteCommentPos;
    private int deleteReplyPos;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_post_comment_replies_dialog);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        repliesRecyclerView = (RecyclerView) findViewById(R.id.repliesRecyclerView);
        openAddReplyDialog = (FloatingActionButton) findViewById(R.id.openAddReplyDialog);
        viewPostTextView = (TextView) findViewById(R.id.viewPostTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        openAddReplyDialog.setOnClickListener(this);
        viewPostTextView.setOnClickListener(this);
        viewPostTextView.setVisibility(View.VISIBLE);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        repliesRecyclerView.setLayoutManager(llm);
        repliesList = new ArrayList<>();

        groupId = getIntent().getIntExtra("groupId", 0);
        postId = getIntent().getIntExtra("postId", 0);
        responseId = getIntent().getIntExtra("responseId", 0);

        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        groupMembershipStatus.checkMembershipStatus(groupId,
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

        groupPostCommentRepliesRecyclerAdapter = new GroupPostCommentRepliesRecyclerAdapter(this, this);
        groupPostCommentRepliesRecyclerAdapter.setData(repliesList);
        repliesRecyclerView.setAdapter(groupPostCommentRepliesRecyclerAdapter);

        repliesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        groupPostCommentRepliesRecyclerAdapter.releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        groupPostCommentRepliesRecyclerAdapter.releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        groupPostCommentRepliesRecyclerAdapter.releasePlayer();
    }

    private void getSinglePostComments() {
        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsApi
                .getSinglePostComments(postData.getGroupId(), postData.getId(), responseId);
        call.enqueue(postCommentCallback);
    }

    private Callback<GroupPostCommentResponse> postCommentCallback = new Callback<GroupPostCommentResponse>() {
        @Override
        public void onResponse(Call<GroupPostCommentResponse> call,
                retrofit2.Response<GroupPostCommentResponse> response) {
            isReuqestRunning = false;
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostCommentResponse groupPostResponse = response.body();
                    processRepliesListingResponse1(groupPostResponse);
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
        ArrayList<GroupPostCommentResult> dataList = (ArrayList<GroupPostCommentResult>) response.getData().get(0)
                .getResult();
        totalPostCount = dataList.get(0).getChildCount();
        if (dataList.size() == 0) {
            isLastPageReached = null != repliesList && !repliesList.isEmpty();
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
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void getCommentReplies() {
        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsApi.getPostCommentReplies(groupId, postId, responseId, skip, limit);
        call.enqueue(postCommentRepliesCallback);
    }

    private Callback<GroupPostCommentResponse> postCommentRepliesCallback = new Callback<GroupPostCommentResponse>() {
        @Override
        public void onResponse(Call<GroupPostCommentResponse> call, Response<GroupPostCommentResponse> response) {
            isReuqestRunning = false;
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostCommentResponse groupPostResponse = response.body();
                    processRepliesListingResponse(groupPostResponse);
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
        ArrayList<GroupPostCommentResult> dataList = (ArrayList<GroupPostCommentResult>) response.getData().get(0)
                .getResult();
        totalPostCount = dataList.get(0).getChildCount();
        if (dataList.size() == 0) {
            //No more next results for search from pagination
            isLastPageReached = null != repliesList && !repliesList.isEmpty();
        } else {
            repliesList.addAll(dataList);
            groupPostCommentRepliesRecyclerAdapter.setData(repliesList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.commentRootView: {
                Bundle args = new Bundle();
                args.putString("responseType", "COMMENT");
                args.putInt("commentType", repliesList.get(position).getCommentType());
                args.putString(AppConstants.GROUP_MEMBER_TYPE, memberType);
                args.putString("authorId", repliesList.get(position).getUserId());
                GpPostCommentOptionsDialogFragment commentOptionsDialogFragment =
                        new GpPostCommentOptionsDialogFragment();
                commentOptionsDialogFragment.setArguments(args);
                commentOptionsDialogFragment.setCancelable(true);
                FragmentManager fm = getSupportFragmentManager();
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyRootView: {
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putInt("commentType", repliesList.get(position).getCommentType());
                args.putString("responseType", "REPLY");
                args.putString(AppConstants.GROUP_MEMBER_TYPE, memberType);
                args.putString("authorId", repliesList.get(position).getUserId());
                GpPostCommentOptionsDialogFragment commentOptionsDialogFragment =
                        new GpPostCommentOptionsDialogFragment();
                commentOptionsDialogFragment.setArguments(args);
                commentOptionsDialogFragment.setCancelable(true);
                FragmentManager fm = getSupportFragmentManager();
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.upvoteCommentContainer:
            case R.id.upvoteReplyContainer:
                if (repliesList.get(position).getMarkedHelpful() == 0) {
                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);
                }
                if (repliesList.get(position).getMarkedHelpful() == 1) {
                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                }
                break;
            case R.id.downvoteCommentContainer:
            case R.id.downvoteReplyContainer:
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                break;
            default:
                break;
        }
    }

    private void markAsHelpfulOrUnhelpful(String markType, int position) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        GroupCommentActionsRequest groupActionsRequest = new GroupCommentActionsRequest();
        groupActionsRequest.setGroupId(repliesList.get(position).getGroupId());
        groupActionsRequest.setPostId(repliesList.get(position).getPostId());
        groupActionsRequest.setResponseId(repliesList.get(position).getId());
        groupActionsRequest
                .setUserId(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        groupActionsRequest.setType(markType);//AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY
        Call<GroupsActionResponse> call = groupsApi.addCommentAction(groupActionsRequest);
        call.enqueue(groupActionResponseCallback);
    }

    private Callback<GroupsActionResponse> groupActionResponseCallback = new Callback<GroupsActionResponse>() {
        @Override
        public void onResponse(Call<GroupsActionResponse> call, retrofit2.Response<GroupsActionResponse> response) {
            if (response.body() == null) {
                if (response.code() == 400) {
                    try {
                        int patchActionId = 0;
                        String patchActionType = null;

                        String errorBody = new String(response.errorBody().bytes());
                        JSONObject jsonObject = new JSONObject(errorBody);
                        JSONArray dataArray = jsonObject.optJSONArray("data");
                        if (dataArray.getJSONObject(0).get("type").equals(dataArray.getJSONObject(1).get("type"))) {
                            //Same Action Event
                            if ("0".equals(dataArray.getJSONObject(0).get("type"))) {
                                Toast.makeText(BaseApplication.getAppContext(), "already marked unhelpful",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BaseApplication.getAppContext(), "already marked helpful",
                                        Toast.LENGTH_SHORT).show();
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
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionResponse groupsActionResponse = response.body();
                    if (groupsActionResponse.getData().getResult().size() == 1) {
                        for (int i = 0; i < repliesList.size(); i++) {
                            if (repliesList.get(i).getId() == groupsActionResponse.getData().getResult().get(0)
                                    .getResponseId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    repliesList.get(i).setHelpfullCount(repliesList.get(i).getHelpfullCount() + 1);
                                    repliesList.get(i).setMarkedHelpful(1);
                                } else {
                                    repliesList.get(i)
                                            .setNotHelpfullCount(repliesList.get(i).getNotHelpfullCount() + 1);
                                    repliesList.get(i).setMarkedHelpful(0);

                                }
                            }
                        }
                    }
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

        GroupActionsPatchRequest groupActionsRequest = new GroupActionsPatchRequest();
        groupActionsRequest.setType(patchActionType);

        Call<GroupsActionResponse> call = groupsApi.patchAction(patchActionId, groupActionsRequest);
        call.enqueue(patchActionResponseCallback);
    }

    private Callback<GroupsActionResponse> patchActionResponseCallback = new Callback<GroupsActionResponse>() {
        @Override
        public void onResponse(Call<GroupsActionResponse> call, retrofit2.Response<GroupsActionResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionResponse groupsActionResponse = response.body();
                    if (groupsActionResponse.getData().getResult().size() == 1) {
                        for (int i = 0; i < repliesList.size(); i++) {
                            if (repliesList.get(i).getId() == groupsActionResponse.getData().getResult().get(0)
                                    .getResponseId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    repliesList.get(i).setHelpfullCount(repliesList.get(i).getHelpfullCount() + 1);
                                    repliesList.get(i)
                                            .setNotHelpfullCount(repliesList.get(i).getNotHelpfullCount() - 1);
                                    repliesList.get(i).setMarkedHelpful(1);
                                } else {
                                    repliesList.get(i)
                                            .setNotHelpfullCount(repliesList.get(i).getNotHelpfullCount() + 1);
                                    repliesList.get(i).setHelpfullCount(repliesList.get(i).getHelpfullCount() - 1);
                                    repliesList.get(i).setMarkedHelpful(0);
                                }
                            }
                        }
                    }
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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
            default:
                break;
        }
    }

    public void openAddCommentReplyDialog(GroupPostCommentResult data) {
        AddGpPostCommentReplyDialogFragment addGroupCommentReplyDialogFragment =
                new AddGpPostCommentReplyDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("parentCommentData", data);
        addGroupCommentReplyDialogFragment.setArguments(args);
        addGroupCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getSupportFragmentManager();
        addGroupCommentReplyDialogFragment.show(fm, "Add Replies");
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        if (body.getData().getResult() != null && !body.getData().getResult().isEmpty()) {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                memberType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                memberType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }

        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN
                .equals(memberType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())
                    ||
                    "m".equalsIgnoreCase(
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                Toast.makeText(this, getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (!BuildConfig.DEBUG && !AppConstants.DEBUGGING_USER_ID
                        .contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {
                    return;
                }
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
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION
                .equals(body.getData().getResult().get(0).getStatus())) {
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
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupPostResponse> call = groupsApi.getSinglePost(postId);
        call.enqueue(postDetailsResponseCallback);
    }

    private Callback<GroupPostResponse> postDetailsResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    postData = groupPostResponse.getData().get(0).getResult().get(0);
                    if (postData.getDisableComments() == 1) {
                        openAddReplyDialog.setVisibility(View.GONE);
                    } else {
                        openAddReplyDialog.setVisibility(View.VISIBLE);
                    }
                    getSinglePostComments();
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
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        EditGpPostCommentOrReplyRequest editGpPostCommentOrReplyRequest = new EditGpPostCommentOrReplyRequest();
        editGpPostCommentOrReplyRequest.setContent(updateContent);
        Call<AddGpPostCommentReplyResponse> call = groupsApi
                .editPostCommentOrReply(id, editGpPostCommentOrReplyRequest);
        call.enqueue(editCommentResponseListener);
    }

    private Callback<AddGpPostCommentReplyResponse> editCommentResponseListener =
            new Callback<AddGpPostCommentReplyResponse>() {
                @Override
                public void onResponse(Call<AddGpPostCommentReplyResponse> call,
                        retrofit2.Response<AddGpPostCommentReplyResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        Crashlytics.logException(nee);
                        showToast("Failed to edit comment. Please try again");
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            AddGpPostCommentReplyResponse groupPostResponse = response.body();
                            repliesList.get(actionItemPosition).setContent(editContent);
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
        AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest = new AddGpPostCommentOrReplyRequest();
        addGpPostCommentOrReplyRequest.setGroupId(postData.getGroupId());
        addGpPostCommentOrReplyRequest.setPostId(postData.getId());
        addGpPostCommentOrReplyRequest.setParentId(parentId);
        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            addGpPostCommentOrReplyRequest.setIsAnnon(1);
        }
        addGpPostCommentOrReplyRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        addGpPostCommentOrReplyRequest.setContent(content);
        addGpPostCommentOrReplyRequest.setMediaUrls(image);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<AddGpPostCommentReplyResponse> call = groupsApi.addPostCommentOrReply(addGpPostCommentOrReplyRequest);
        call.enqueue(addReplyResponseListener);
    }

    private Callback<AddGpPostCommentReplyResponse> addReplyResponseListener =
            new Callback<AddGpPostCommentReplyResponse>() {
                @Override
                public void onResponse(Call<AddGpPostCommentReplyResponse> call,
                        retrofit2.Response<AddGpPostCommentReplyResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        Crashlytics.logException(nee);
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
                            if (responseData.getData().getResult().isAnnon() != 1) {
                                commentListData.setUserId(responseData.getData().getResult().getUserId());
                                UserInfo sharedPrefUser = SharedPrefUtils
                                        .getUserDetailModel(BaseApplication.getAppContext());
                                userDetailResult.setDynamoId(sharedPrefUser.getDynamoId());
                                userDetailResult.setUserType(sharedPrefUser.getUserType());
                                userDetailResult.setFirstName(sharedPrefUser.getFirst_name());
                                userDetailResult.setLastName(sharedPrefUser.getLast_name());
                                ProfilePic profilePic = new ProfilePic();
                                profilePic.setClientApp(
                                        SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()));
                                userDetailResult.setProfilePicUrl(profilePic);
                            }

                            commentListData.setUserInfo(userDetailResult);
                            SharedPrefUtils.clearSavedReplyData(BaseApplication.getAppContext(), groupId, postId,
                                    responseData.getData().getResult().getParentId());
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
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        EditGpPostCommentOrReplyRequest editGpPostCommentOrReplyRequest = new EditGpPostCommentOrReplyRequest();
        editGpPostCommentOrReplyRequest.setContent(updatedReply);
        Call<AddGpPostCommentReplyResponse> call = groupsApi
                .editPostCommentOrReply(replyId, editGpPostCommentOrReplyRequest);
        call.enqueue(editReplyResponseListener);
        editReplyId = replyId;
        editReplyParentCommentId = parentCommentId;
        editContent = updatedReply;
    }


    private Callback<AddGpPostCommentReplyResponse> editReplyResponseListener =
            new Callback<AddGpPostCommentReplyResponse>() {
                @Override
                public void onResponse(Call<AddGpPostCommentReplyResponse> call,
                        retrofit2.Response<AddGpPostCommentReplyResponse> response) {
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

    public void blockUserWithResponseId(int commentPos, int replyPos, String responseType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        if ("REPLY".equals(responseType)) {
            deleteCommentPos = commentPos;
            deleteReplyPos = replyPos;
            Call<GroupsMembershipResponse> call = groupsApi
                    .blockUserWithResponseId(repliesList.get(deleteReplyPos).getId());
            call.enqueue(blockUserResponseCallback);
        } else {
            actionItemPosition = replyPos;
            Call<GroupsMembershipResponse> call = groupsApi
                    .blockUserWithResponseId(repliesList.get(actionItemPosition).getId());
            call.enqueue(blockUserResponseCallback);
        }

    }

    private Callback<GroupsMembershipResponse> blockUserResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, Response<GroupsMembershipResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    showToast(getString(R.string.groups_user_block_success));
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

    public void onResponseDelete(int commentPos, int replyPos, String responseType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        DeleteGpPostCommentOrReplyRequest deleteGpPostCommentOrReplyRequest = new DeleteGpPostCommentOrReplyRequest();
        deleteGpPostCommentOrReplyRequest.setIsActive(0);
        if ("REPLY".equals(responseType)) {
            deleteCommentPos = commentPos;
            deleteReplyPos = replyPos;
            Call<AddGpPostCommentReplyResponse> call = groupsApi
                    .deleteCommentOrReply(repliesList.get(deleteReplyPos).getId(),
                            deleteGpPostCommentOrReplyRequest);
            call.enqueue(deleteReplyResponseListener);
        } else {
            actionItemPosition = replyPos;
            Call<AddGpPostCommentReplyResponse> call = groupsApi
                    .deleteCommentOrReply(repliesList.get(actionItemPosition).getId(),
                            deleteGpPostCommentOrReplyRequest);
            call.enqueue(deleteCommentResponseListener);
        }
    }

    private Callback<AddGpPostCommentReplyResponse> deleteCommentResponseListener =
            new Callback<AddGpPostCommentReplyResponse>() {
                @Override
                public void onResponse(Call<AddGpPostCommentReplyResponse> call,
                        retrofit2.Response<AddGpPostCommentReplyResponse> response) {
                    removeProgressDialog();
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        Crashlytics.logException(nee);
                        showToast("Failed to edit reply. Please try again");

                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            AddGpPostCommentReplyResponse responseData = response.body();
                            repliesList.remove(actionItemPosition);
                            groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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
    private Callback<AddGpPostCommentReplyResponse> deleteReplyResponseListener =
            new Callback<AddGpPostCommentReplyResponse>() {
                @Override
                public void onResponse(Call<AddGpPostCommentReplyResponse> call,
                        retrofit2.Response<AddGpPostCommentReplyResponse> response) {
                    removeProgressDialog();
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        Crashlytics.logException(nee);
                        showToast("Failed to edit reply. Please try again");

                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            AddGpPostCommentReplyResponse responseData = response.body();
                            repliesList.get(deleteCommentPos).getChildData().remove(deleteReplyPos);
                            repliesList.get(deleteCommentPos)
                                    .setChildCount(repliesList.get(deleteCommentPos).getChildCount() - 1);
                            groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
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
        Bundle args = new Bundle();
        if ("REPLY".equals(responseType)) {
            args.putString("action", "EDIT_REPLY");
            //position-1 to accommodate the parent comment above.
            args.putParcelable("parentCommentData", repliesList.get(commentPosition).getChildData().get(position - 1));
        } else {
            args.putString("action", "EDIT_COMMENT");
            args.putParcelable("parentCommentData", repliesList.get(position));
        }
        args.putInt("position", position);
        AddGpPostCommentReplyDialogFragment addGpPostCommentReplyDialogFragment =
                new AddGpPostCommentReplyDialogFragment();
        addGpPostCommentReplyDialogFragment.setArguments(args);
        addGpPostCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getSupportFragmentManager();
        addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    public void onResponseReport(int commentPosition, int position, String responseType) {
        Bundle args = new Bundle();
        args.putInt("groupId", groupId);
        args.putInt("postId", postData.getId());
        if ("REPLY".equals(responseType)) {
            //position-1 to accommodate the parent comment above.
            args.putInt("responseId", repliesList.get(commentPosition).getChildData().get(position - 1).getId());
        } else {
            args.putInt("responseId", repliesList.get(position).getId());
        }
        args.putString("type", AppConstants.GROUP_REPORT_TYPE_COMMENT);
        GroupPostReportDialogFragment groupPostReportDialogFragment = new GroupPostReportDialogFragment();
        groupPostReportDialogFragment.setArguments(args);
        groupPostReportDialogFragment.setCancelable(true);
        FragmentManager fm = getSupportFragmentManager();
        groupPostReportDialogFragment.show(fm, "Choose report option");
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
        Fragment prev = getSupportFragmentManager().findFragmentByTag("Add Comment");
        if (prev != null) {
            String path = MediaStore.Images.Media
                    .insertImage(getContentResolver(), image, "Title" + System.currentTimeMillis(), null);
            Uri imageUriTemp = Uri.parse(path);
            File file2 = FileUtils.getFile(this, imageUriTemp);
            removeProgressDialog();
            ((AddGpPostCommentReplyDialogFragment) prev).sendUploadProfileImageRequest(file2);
        }
    }
}
