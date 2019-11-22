package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.CollectionsModels.FeaturedOnModel;
import com.mycity4kids.models.CollectionsModels.FollowCollectionRequestModel;
import com.mycity4kids.models.CollectionsModels.UserCollectionsModel;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI;
import com.mycity4kids.ui.adapter.FeatureOnRecyclerAdapter;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private Boolean isFollowing = false;
    private String userId;
    private RelativeLayout mLodingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_on);
        backImageView = findViewById(R.id.backImageView);
        featuredonRecyclerview = findViewById(R.id.featuredon_recyclerview);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        finalFeaturedDataList = new ArrayList<>();
        userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        featureOnRecyclerAdapter = new FeatureOnRecyclerAdapter(this, this);
        featuredonRecyclerview.setLayoutManager(linearLayoutManager);
        featuredonRecyclerview.setAdapter(featureOnRecyclerAdapter);
        fetchFeatureList();

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
        Retrofit retrofit = BaseApplication.getInstance().getRetrofitTest();
        CollectionsAPI featureListAPI = retrofit.create(CollectionsAPI.class);
        Call<FeaturedOnModel> call = featureListAPI.getFeatureList("5dc0809611cb4607d6b24667/2", from, from + 9);
        call.enqueue(featuredList);
    }

    private Callback<FeaturedOnModel> featuredList = new Callback<FeaturedOnModel>() {
        @Override
        public void onResponse(Call<FeaturedOnModel> call, retrofit2.Response<FeaturedOnModel> response) {
            isReuqestRunning = false;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            removeProgressDialog();
            if (response == null || response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("New comments API failure");
                Crashlytics.logException(nee);
                return;
            }

            try {
                FeaturedOnModel userCollectionsListModel = response.body();
                showFeatureList(userCollectionsListModel.getData().getResult().getItem_list().get(0).getCollectionList());
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FeaturedOnModel> call, Throwable t) {
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
            if (nextPageNumber == 1) {
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
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view, int position) {

    }

    public void followAPICall(String id, String collectionId, int sortOrder, int pos) {
        authorId = id;
        updateFollowPos = pos;
        changeFollowUnfollowTextPos = pos;
        isFollowing = finalFeaturedDataList.get(pos).isFollowing();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofitTest();
        CollectionsAPI collectionsAPI = retrofit.create(CollectionsAPI.class);
        FollowCollectionRequestModel request = new FollowCollectionRequestModel();
        request.setUserId(userId);
        request.setUserCollectionId(collectionId);
        request.setSortOrder(sortOrder);

        if (isFollowing) {
            isFollowing = false;
            request.setDeleted(true);
            finalFeaturedDataList.get(updateFollowPos).setFollowing(false);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = collectionsAPI.followCollection(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            finalFeaturedDataList.get(updateFollowPos).setFollowing(true);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = collectionsAPI.followCollection(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

    }

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
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
                        finalFeaturedDataList.get(updateFollowPos).setFollowing(false);
                        isFollowing = false;
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
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
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
                        finalFeaturedDataList.get(updateFollowPos).setFollowing(true);
                        isFollowing = true;
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
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
