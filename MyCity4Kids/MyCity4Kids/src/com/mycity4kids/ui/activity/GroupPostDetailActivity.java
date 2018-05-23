package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupPostDetailsAndCommentsRecyclerAdapter;
import com.mycity4kids.ui.fragment.AddViewGroupPostCommentsRepliesDialogFragment;
import com.mycity4kids.ui.fragment.CityListingDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/4/18.
 */

public class GroupPostDetailActivity extends BaseActivity implements GroupPostDetailsAndCommentsRecyclerAdapter.RecyclerViewClickListener {

    private String commentURL = "https://s3-ap-southeast-1.amazonaws.com/mycity4kids-phoenix/comments-data/article-e32f733cab7e4d9f8c9344b94a089c1d-1.json";

    private GroupPostDetailsAndCommentsRecyclerAdapter groupPostDetailsAndCommentsRecyclerAdapter;

    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private boolean isLoading;
    private ArrayList<CommentsData> modList;
    private ArrayList<GroupPostCommentResult> completeResponseList;
    private String postType;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;

    private RecyclerView recyclerView;
    private HashMap<String, String> mediaUrls, pollOptions;
    private GroupPostResult postData;
    private Toolbar toolbar;
    private AddViewGroupPostCommentsRepliesDialogFragment addViewGroupPostCommentsRepliesDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_post_detail_activity);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        postType = getIntent().getStringExtra("postType");
        postData = (GroupPostResult) getIntent().getParcelableExtra("postData");
        if (AppConstants.POST_TYPE_MEDIA.equals(postType)) {
            mediaUrls = (HashMap<String, String>) getIntent().getSerializableExtra("mediaUrls");
            postData.setMediaUrls(mediaUrls);
        } else if (AppConstants.POST_TYPE_IMAGE_POLL.equals(postType) || AppConstants.POST_TYPE_TEXT_POLL.equals(postType)) {
            pollOptions = (HashMap<String, String>) getIntent().getSerializableExtra("pollOptions");
            postData.setPollOptions(pollOptions);
        }

        modList = new ArrayList<>();
        completeResponseList = new ArrayList<>();
        completeResponseList.add(new GroupPostCommentResult()); // Empty element for Header position

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        groupPostDetailsAndCommentsRecyclerAdapter = new GroupPostDetailsAndCommentsRecyclerAdapter(this, this, postType);
        groupPostDetailsAndCommentsRecyclerAdapter.setData(postData, completeResponseList);
        recyclerView.setAdapter(groupPostDetailsAndCommentsRecyclerAdapter);

//        Retrofit retro = BaseApplication.getInstance().getRetrofit();
//        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
//        Call<ResponseBody> call = articleDetailsAPI.getComments(commentURL);
//        call.enqueue(commentsCallback);

        getPostComments();

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

    private void getPostComments() {
        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsAPI.getPostComments(postData.getGroupId(), postData.getId(), skip, limit);
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
//                    List<GroupPostCommentResult> commentsList = groupPostResponse.getData().get(0).getResult();
//                    completeResponseList.addAll(commentsList);
//                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
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
        ArrayList<GroupPostCommentResult> dataList = response.getData().get(0).getResult();
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


    Callback<ResponseBody> commentsCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                isLoading = false;
                commentURL = "http";
                return;
            }
            try {
                isLoading = false;
                String resData = new String(response.body().bytes());
                ArrayList<CommentsData> arrayList = new ArrayList<>();
                JSONArray commentsJson = new JSONArray(resData);
                commentURL = "";
                if (commentsJson.length() > 0) {
//                    commentHeading.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < commentsJson.length(); i++) {
                    if (commentsJson.getJSONObject(i).has("next")) {
                        commentURL = commentsJson.getJSONObject(i).getString("next");
                    } else {
                        CommentsData cData = new Gson().fromJson(commentsJson.get(i).toString(), CommentsData.class);
                        arrayList.add(cData);
                    }
                }

                arrangeComments(arrayList);
                if (StringUtils.isNullOrEmpty(commentURL)) {
//                    commentType = "fb";
                    commentURL = "http";
                }


            } catch (JSONException jsonexception) {
                Crashlytics.logException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
//            mLodingView.setVisibility(View.GONE);
//            handleExceptions(t);
        }
    };

    private void rearrangePostComment(List<GroupPostCommentResult> commentsList) {
        for (int i = 0; i < commentsList.size(); i++) {
//            commentsList.get(i).setCommentLevel(0);
            completeResponseList.add(commentsList.get(i));
            for (int j = 0; j < commentsList.get(i).getChildData().size(); j++) {
//                commentsList.get(i).getChildData().get(j).setCommentLevel(1);
//                completeResponseList.add(commentsList.get(i).getChildData().get(j));
//                for (int k = 0; k < commentsList.get(i).getChildData().get(j).getChildData().size(); k++) {
//                    commentsList.get(i).getChildData().get(j).getChildData().get(k).setCommentLevel(2);
//                    completeResponseList.add(commentsList.get(i).getChildData().get(j).getChildData().get(k));
//                }
            }
            completeResponseList.get(completeResponseList.size() - 1).setIsLastConversation(1);
        }
        groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
    }

    private void arrangeComments(ArrayList<CommentsData> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).setCommentLevel(0);
            modList.add(arrayList.get(i));
            for (int j = 0; j < arrayList.get(i).getReplies().size(); j++) {
                arrayList.get(i).getReplies().get(j).setCommentLevel(1);
                modList.add(arrayList.get(i).getReplies().get(j));
                for (int k = 0; k < arrayList.get(i).getReplies().get(j).getReplies().size(); k++) {
                    arrayList.get(i).getReplies().get(j).getReplies().get(k).setCommentLevel(2);
                    modList.add(arrayList.get(i).getReplies().get(j).getReplies().get(k));
                }
            }
            modList.get(modList.size() - 1).setIsLastConversation(1);
        }
        groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
        Log.d("Mod List", "" + modList);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.replyCommentTextView:
            case R.id.replyCountTextView:
//                GroupPostCommentReplyFragment
                addViewGroupPostCommentsRepliesDialogFragment = new AddViewGroupPostCommentsRepliesDialogFragment();
                Bundle _args = new Bundle();
                _args.putParcelable("commentReplies", completeResponseList.get(position));
                _args.putInt("childCount", completeResponseList.get(position).getChildCount());
                addViewGroupPostCommentsRepliesDialogFragment.setArguments(_args);
                FragmentManager fm = getSupportFragmentManager();
                addViewGroupPostCommentsRepliesDialogFragment.show(fm, "Replies");
                break;
        }
    }
}
