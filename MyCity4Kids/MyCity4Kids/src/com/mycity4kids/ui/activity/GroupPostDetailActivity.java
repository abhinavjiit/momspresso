package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupPostDetailsAndCommentsRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/4/18.
 */

public class GroupPostDetailActivity extends BaseActivity implements GroupPostDetailsAndCommentsRecyclerAdapter.RecyclerViewClickListener {

    private String commentURL = "https://s3-ap-southeast-1.amazonaws.com/mycity4kids-phoenix/comments-data/article-e32f733cab7e4d9f8c9344b94a089c1d-1.json";
    private boolean isLoading;
    private ArrayList<CommentsData> modList;
    private GroupPostDetailsAndCommentsRecyclerAdapter groupPostDetailsAndCommentsRecyclerAdapter;
    private String postType;

    private RecyclerView recyclerView;
    private HashMap<String, String> mediaUrls;
    private GroupPostResult postData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_post_detail_activity);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        postType = getIntent().getStringExtra("postType");
        postData = (GroupPostResult) getIntent().getParcelableExtra("postData");
        mediaUrls = (HashMap<String, String>) getIntent().getSerializableExtra("mediaUrls");
        postData.setMediaUrls(mediaUrls);

        modList = new ArrayList<>();


        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        groupPostDetailsAndCommentsRecyclerAdapter = new GroupPostDetailsAndCommentsRecyclerAdapter(this, this, postType);
        groupPostDetailsAndCommentsRecyclerAdapter.setData(postData, modList);
        recyclerView.setAdapter(groupPostDetailsAndCommentsRecyclerAdapter);


//        Retrofit retro = BaseApplication.getInstance().getRetrofit();
//        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
//        Call<ResponseBody> call = articleDetailsAPI.getComments(commentURL);
//        call.enqueue(commentsCallback);

        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsAPI.getPostComments(postData.getGroupId(), postData.getId());
        call.enqueue(postCommentCallback);
    }

    private Callback<GroupPostCommentResponse> postCommentCallback = new Callback<GroupPostCommentResponse>() {
        @Override
        public void onResponse(Call<GroupPostCommentResponse> call, retrofit2.Response<GroupPostCommentResponse> response) {
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

        }
    };

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

    }
}
