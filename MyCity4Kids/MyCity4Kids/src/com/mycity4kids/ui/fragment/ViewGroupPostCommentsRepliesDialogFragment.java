package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.GroupActionsPatchRequest;
import com.mycity4kids.models.request.GroupCommentActionsRequest;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.adapter.GroupPostCommentRepliesRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class ViewGroupPostCommentsRepliesDialogFragment extends DialogFragment implements OnClickListener, GroupPostCommentRepliesRecyclerAdapter.RecyclerViewClickListener {

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int totalPostCount;
    private int skip = 10;
    private int limit = 10;
    replyUpdate replyUpdate;
    private GroupPostCommentResult data;
    private ArrayList<GroupPostCommentResult> repliesList;
    private int totalRepliesCount;

    private Toolbar mToolbar;
    private RecyclerView repliesRecyclerView;
    private TextView toolbarTitleTextView;
    private ProgressDialog mProgressDialog;
    private RelativeLayout commentLayout;
    private GroupPostCommentRepliesRecyclerAdapter groupPostCommentRepliesRecyclerAdapter;
    private int childCount;
    private FloatingActionButton openAddReplyDialog;
    private int commentPosition;
    private String memberType;
    private boolean commentDisableFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.group_post_comment_replies_dialog, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        repliesRecyclerView = (RecyclerView) rootView.findViewById(R.id.repliesRecyclerView);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        openAddReplyDialog = (FloatingActionButton) rootView.findViewById(R.id.openAddReplyDialog);
        commentLayout = (RelativeLayout) rootView.findViewById(R.id.commentLayout);


        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle back button naviagtion

                //        getActivity().setResult(Activity.RESULT_OK);
                //replyUpdate.replyDataUpdate(repliesList,commentPosition);
                if (getActivity() instanceof GroupPostDetailActivity) {
                    ((GroupPostDetailActivity) getActivity()).update(repliesList, commentPosition);
                }
                dismiss();
            }
        });
        commentLayout.setOnClickListener(this);
        openAddReplyDialog.setOnClickListener(this);

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        repliesRecyclerView.setLayoutManager(llm);

        repliesList = new ArrayList<>();

        Bundle extras = getArguments();
        if (extras != null) {
            data = extras.getParcelable("commentReplies");
            childCount = extras.getInt("childCount");
            commentPosition = extras.getInt("position");
            memberType = extras.getString(AppConstants.GROUP_MEMBER_TYPE);
            commentDisableFlag = extras.getBoolean("commentDisableFlag", false);
        }

        if (commentDisableFlag) {
            commentLayout.setVisibility(View.GONE);
            openAddReplyDialog.setVisibility(View.GONE);
        } else {
            commentLayout.setVisibility(View.VISIBLE);
            openAddReplyDialog.setVisibility(View.GONE);
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
        formatCommentData(repliesList);
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
            formatCommentData(dataList);
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

    private void formatCommentData(ArrayList<GroupPostCommentResult> dataList) {
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
                    }
                }
            }
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
        switch (view.getId()) {
            case R.id.commentRootView: {
                GpPostCommentOptionsDialogFragment commentOptionsDialogFragment = new GpPostCommentOptionsDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                //commentOptionsDialogFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putInt("position", position);
                _args.putInt("commentPosition", commentPosition);
                _args.putInt("commentType", repliesList.get(position).getCommentType());
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
                FragmentManager fm = getChildFragmentManager();
                //commentOptionsDialogFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putInt("position", position);
                _args.putString("responseType", "REPLY");
                _args.putInt("commentType", repliesList.get(position).getCommentType());
                _args.putInt("commentPosition", commentPosition);
                _args.putString(AppConstants.GROUP_MEMBER_TYPE, memberType);
                _args.putString("authorId", repliesList.get(position).getUserId());
                commentOptionsDialogFragment.setArguments(_args);
                commentOptionsDialogFragment.setCancelable(true);
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.upvoteCommentContainer:

                if (repliesList.get(position).getMarkedHelpful() == 0) {


                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, 0);


                }
                if (repliesList.get(position).getMarkedHelpful() == 1) {

                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, 0);


                }

                // markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);
                break;
            case R.id.upvoteReplyContainer:
                if (repliesList.get(position).getMarkedHelpful() == 0) {


                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);


                }
                if (repliesList.get(position).getMarkedHelpful() == 1) {

                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);


                }

                //  markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);
                break;
            case R.id.downvoteCommentContainer:
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                break;
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
                                    repliesList.get(i).setMarkedHelpful(1);
                                } else {
                                    repliesList.get(i).setNotHelpfullCount(repliesList.get(i).getNotHelpfullCount() + 1);
                                    repliesList.get(i).setMarkedHelpful(0);

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
                                    repliesList.get(i).setMarkedHelpful(1);
                                } else {
                                    repliesList.get(i).setNotHelpfullCount(repliesList.get(i).getNotHelpfullCount() + 1);
                                    repliesList.get(i).setHelpfullCount(repliesList.get(i).getHelpfullCount() - 1);
                                    repliesList.get(i).setMarkedHelpful(0);

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
            case R.id.commentLayout:
            case R.id.openAddReplyDialog:
                ((GroupPostDetailActivity) getActivity()).openAddCommentReplyDialog(data);
                break;
        }
    }

    public void updateRepliesList(GroupPostCommentResult ssComment) {
        if (repliesList != null) {
            repliesList.clear();
            repliesList.add(ssComment);
            if (data.getChildData() != null) {
                for (int i = 0; i < ssComment.getChildData().size(); i++) {
                    repliesList.add(ssComment.getChildData().get(i));
                }
                if (totalRepliesCount <= ssComment.getChildData().size()) {
                    isLastPageReached = true;
                }
            } else {
                isLastPageReached = true;
            }
        }
        groupPostCommentRepliesRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        groupPostCommentRepliesRecyclerAdapter.releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        groupPostCommentRepliesRecyclerAdapter.releasePlayer();
    }

    public interface replyUpdate {
        public void replyDataUpdate(ArrayList<GroupPostCommentResult> repliesList, int position);
    }

}