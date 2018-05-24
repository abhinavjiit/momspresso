package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.request.AddGpPostCommentOrReplyRequest;
import com.mycity4kids.models.response.AddGpPostCommentReplyResponse;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.adapter.GroupPostCommentRepliesRecyclerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class AddViewGroupPostCommentsRepliesDialogFragment extends DialogFragment implements OnClickListener, GroupPostCommentRepliesRecyclerAdapter.RecyclerViewClickListener {

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int totalPostCount;
    private int skip = 10;
    private int limit = 10;
    private GroupPostCommentResult data;
    private ArrayList<GroupPostCommentResult> repliesList;

    private Toolbar mToolbar;
    private RecyclerView repliesRecyclerView;
    private TextView toolbarTitleTextView;
    private ProgressDialog mProgressDialog;

    private GroupPostCommentRepliesRecyclerAdapter groupPostCommentRepliesRecyclerAdapter;
    private int childCount;
    private ImageView addCommentImageView;
    private EditText writeCommentEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.group_post_comment_replies_dialog, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        repliesRecyclerView = (RecyclerView) rootView.findViewById(R.id.repliesRecyclerView);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        addCommentImageView = (ImageView) rootView.findViewById(R.id.addCommentImageView);
        writeCommentEditText = (EditText) rootView.findViewById(R.id.writeCommentEditText);

        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle back button naviagtion
                dismiss();
            }
        });

        addCommentImageView.setOnClickListener(this);

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        repliesRecyclerView.setLayoutManager(llm);

        repliesList = new ArrayList<>();

        Bundle extras = getArguments();
        if (extras != null) {
            data = extras.getParcelable("commentReplies");
            childCount = extras.getInt("childCount");
        }


        repliesList.add(data);
        if (data.getChildData() != null) {
            for (int i = 0; i < data.getChildData().size(); i++) {
                repliesList.add(data.getChildData().get(i));
            }
            if (childCount <= data.getChildData().size()) {
                isLastPageReached = true;
            }
        } else {
            isLastPageReached = true;
        }

        groupPostCommentRepliesRecyclerAdapter = new GroupPostCommentRepliesRecyclerAdapter(getActivity(), this);
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

        return rootView;
    }

    private void getCommentReplies() {
        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsAPI.getPostCommentReplies(data.getGroupId(), data.getPostId(), data.getId(), skip, limit);
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
        totalPostCount = response.getTotal();
        ArrayList<GroupPostCommentResult> dataList = (ArrayList<GroupPostCommentResult>) response.getData().get(0).getResult();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_bg_rounded_corners));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCommentImageView:
                if (validateReplyText()) {
                    addCommentReply();
                }
                break;
        }
    }


    private void addCommentReply() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest = new AddGpPostCommentOrReplyRequest();
        addGpPostCommentOrReplyRequest.setGroupId(data.getGroupId());
        addGpPostCommentOrReplyRequest.setPostId(data.getPostId());
        addGpPostCommentOrReplyRequest.setParentId(data.getId());
        addGpPostCommentOrReplyRequest.setUserId(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        addGpPostCommentOrReplyRequest.setContent(writeCommentEditText.getText().toString());
        Call<AddGpPostCommentReplyResponse> call = groupsAPI.addPostCommentOrReply(addGpPostCommentOrReplyRequest);
        call.enqueue(addCommentReplyResponseListener);
//        Call ca
    }

    private Callback<AddGpPostCommentReplyResponse> addCommentReplyResponseListener = new Callback<AddGpPostCommentReplyResponse>() {
        @Override
        public void onResponse(Call<AddGpPostCommentReplyResponse> call, retrofit2.Response<AddGpPostCommentReplyResponse> response) {
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((GroupPostDetailActivity) getActivity()).showToast("Failed to add comment. Please try again");
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
                    groupPostCommentResult.setIsActive(groupPostResponse.getData().getResult().isActive() ? 1 : 0);
                    groupPostCommentResult.setIsAnnon(groupPostResponse.getData().getResult().isAnnon() ? 1 : 0);
                    groupPostCommentResult.setModerationStatus(groupPostResponse.getData().getResult().getModerationStatus());
                    groupPostCommentResult.setModeratedBy(groupPostResponse.getData().getResult().getModeratedBy());
                    groupPostCommentResult.setModeratedOn(groupPostResponse.getData().getResult().getModeratedon());
                    groupPostCommentResult.setLang(groupPostResponse.getData().getResult().getLang());
                    groupPostCommentResult.setCreatedAt(groupPostResponse.getData().getResult().getCreatedAt());
                    groupPostCommentResult.setUpdatedAt(groupPostResponse.getData().getResult().getUpdatedAt());

                    repliesList.add(groupPostCommentResult);
                    groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
                } else {
                    if (isAdded())
                        ((GroupPostDetailActivity) getActivity()).showToast("Failed to add comment. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((GroupPostDetailActivity) getActivity()).showToast("Failed to add comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
            if (isAdded())
                ((GroupPostDetailActivity) getActivity()).showToast("Failed to add comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private boolean validateReplyText() {
        if (StringUtils.isNullOrEmpty(writeCommentEditText.getText().toString())) {
            if (isAdded())
                ((GroupPostDetailActivity) getActivity()).showToast("Invalid reply text");
            return false;
        }
        return true;
    }
}