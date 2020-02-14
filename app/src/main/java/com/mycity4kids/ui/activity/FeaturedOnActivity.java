package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.collectionsModels.FollowCollectionRequestModel;
import com.mycity4kids.models.collectionsModels.UserCollectionsModel;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.MixFeedResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI;
import com.mycity4kids.ui.adapter.FeatureOnRecyclerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class FeaturedOnActivity extends BaseActivity implements View.OnClickListener, FeatureOnRecyclerAdapter.RecyclerViewClickListener {

    private ImageView backImageView;
    private RecyclerView featuredonRecyclerview;
    private FeatureOnRecyclerAdapter featureOnRecyclerAdapter;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int nextPageNumber = 0;
    private boolean isReuqestRunning = true;
    private boolean isLastPageReached = false;
    private ArrayList<UserCollectionsModel> finalFeaturedDataList;
    private String authorId;
    private int updateFollowPos, changeFollowUnfollowTextPos;
    private String isFollowing;
    private String userId;
    private RelativeLayout mLodingView;
    private String contentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_on);
        backImageView = findViewById(R.id.backImageView);
        featuredonRecyclerview = findViewById(R.id.featuredon_recyclerview);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        finalFeaturedDataList = new ArrayList<>();
        userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        contentId = getIntent().getStringExtra(AppConstants.CONTENT_ID);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        featureOnRecyclerAdapter = new FeatureOnRecyclerAdapter(this);
        featuredonRecyclerview.setLayoutManager(linearLayoutManager);
        featuredonRecyclerview.setAdapter(featureOnRecyclerAdapter);

        if (!StringUtils.isNullOrEmpty(contentId)) {
            fetchFeatureList();
        }

        featuredonRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            fetchFeatureList();
                        }
                    }
                }
            }
        });
        backImageView.setOnClickListener(this);
    }

    private void fetchFeatureList() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        int from = 10 * nextPageNumber;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        CollectionsAPI featureListAPI = retrofit.create(CollectionsAPI.class);
        Call<MixFeedResponse> call = featureListAPI.getFeatureList(contentId, "0", from, from + 10);
        call.enqueue(featuredList);
    }

    private Callback<MixFeedResponse> featuredList = new Callback<MixFeedResponse>() {
        @Override
        public void onResponse(Call<MixFeedResponse> call, retrofit2.Response<MixFeedResponse> response) {
            isReuqestRunning = false;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }

            try {
                MixFeedResponse userCollectionsListModel = response.body();
                showFeatureList(userCollectionsListModel.getData().getResult().get(0).getCollectionList());
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<MixFeedResponse> call, Throwable t) {
            isReuqestRunning = false;
            removeProgressDialog();
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showFeatureList(ArrayList<UserCollectionsModel> userCollectionsListModel) {
        if (userCollectionsListModel.size() == 0) {
            isLastPageReached = false;
            if (null != finalFeaturedDataList && !finalFeaturedDataList.isEmpty()) {
                isLastPageReached = true;
            } else {
                finalFeaturedDataList = userCollectionsListModel;
                featureOnRecyclerAdapter.setData(finalFeaturedDataList);
                featureOnRecyclerAdapter.notifyDataSetChanged();
            }
        } else {
            if (nextPageNumber == 0) {
                finalFeaturedDataList = userCollectionsListModel;
            } else {
                finalFeaturedDataList.addAll(userCollectionsListModel);
            }
            featureOnRecyclerAdapter.setData(finalFeaturedDataList);
            nextPageNumber = nextPageNumber + 1;
            featureOnRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onClick(View view, int position) {

    }

    public void followAPICall(String id, String collectionId, int sortOrder, int pos) {
        authorId = id;
        updateFollowPos = pos;
        changeFollowUnfollowTextPos = pos;
        isFollowing = finalFeaturedDataList.get(pos).getIsFollowed();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        CollectionsAPI collectionsAPI = retrofit.create(CollectionsAPI.class);
        FollowCollectionRequestModel request = new FollowCollectionRequestModel();
        request.setUserId(userId);
        request.setUserCollectionId(collectionId);
        request.setSortOrder(sortOrder);
        showProgressDialog(getResources().getString(R.string.please_wait));

        if (isFollowing.equalsIgnoreCase("1")) {
            request.setDeleted(true);
            finalFeaturedDataList.get(updateFollowPos).setIsFollowed("0");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = collectionsAPI.followCollection(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            finalFeaturedDataList.get(updateFollowPos).setIsFollowed("1");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = collectionsAPI.followCollection(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

    }

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData().getResult().getId() == null) {
                        ToastUtils.showToast(FeaturedOnActivity.this, responseData.getData().getMsg());
                        return;
                    }
                } else {
                    if (responseData.getData().getResult().getId() == null) {
                        ToastUtils.showToast(FeaturedOnActivity.this, responseData.getData().getMsg());
                        return;
                    } else {
                        finalFeaturedDataList.get(updateFollowPos).setIsFollowed("0");
                        isFollowing = "0";
                    }
                }
                featureOnRecyclerAdapter.setListUpdate(updateFollowPos, finalFeaturedDataList);
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            showToast(getString(R.string.server_went_wrong));
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData().getResult().getId() == null) {
                        ToastUtils.showToast(FeaturedOnActivity.this, responseData.getData().getMsg());
                        return;
                    }
                } else {
                    if (responseData.getData().getResult().getId() == null) {
                        ToastUtils.showToast(FeaturedOnActivity.this, responseData.getData().getMsg());
                        return;
                    } else {
                        finalFeaturedDataList.get(updateFollowPos).setIsFollowed("1");
                        isFollowing = "1";
                    }
                }
                featureOnRecyclerAdapter.setListUpdate(updateFollowPos, finalFeaturedDataList);
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            showToast(getString(R.string.server_went_wrong));
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
