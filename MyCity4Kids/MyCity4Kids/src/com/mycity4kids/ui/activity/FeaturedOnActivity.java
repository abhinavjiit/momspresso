package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.CollectionsModels.CollectionFeaturedListModel;
import com.mycity4kids.models.CollectionsModels.FollowCollectionRequestModel;
import com.mycity4kids.models.CollectionsModels.UserCollectiosModel;
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
    private boolean isReuqestRunning = true;
    private ArrayList<UserCollectiosModel> featuredDataList;
    private String authorId;
    private int updateFollowPos, changeFollowUnfollowTextPos;
    private Boolean isFollowing = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_on);
        backImageView = findViewById(R.id.backImageView);
        featuredonRecyclerview = findViewById(R.id.featuredon_recyclerview);
        featuredDataList = new ArrayList<>();
        userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        featureOnRecyclerAdapter = new FeatureOnRecyclerAdapter(this, this);
        featuredonRecyclerview.setLayoutManager(linearLayoutManager);
        featuredonRecyclerview.setAdapter(featureOnRecyclerAdapter);
        fetchFeatureList();
        backImageView.setOnClickListener(this);
    }

    private void fetchFeatureList() {
        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofitTest();
        CollectionsAPI featureListAPI = retrofit.create(CollectionsAPI.class);
        Call<CollectionFeaturedListModel> call = featureListAPI.getFeatureList("5dc0809611cb4607d6b24667/2", 0, 10);
        call.enqueue(featuredList);
    }

    private Callback<CollectionFeaturedListModel> featuredList = new Callback<CollectionFeaturedListModel>() {
        @Override
        public void onResponse(Call<CollectionFeaturedListModel> call, retrofit2.Response<CollectionFeaturedListModel> response) {
            isReuqestRunning = false;
            removeProgressDialog();
            if (response == null || response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("New comments API failure");
                Crashlytics.logException(nee);
                return;
            }

            try {
                CollectionFeaturedListModel userCollectionsListModel = response.body();
                showFeatureList(userCollectionsListModel);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CollectionFeaturedListModel> call, Throwable t) {
            isReuqestRunning = false;
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showFeatureList(CollectionFeaturedListModel userCollectionsListModel) {
        featuredDataList = userCollectionsListModel.getData().get(0).getResult().get(0).getCollections_list().get(0).getCollectionList();
        featureOnRecyclerAdapter.setData(featuredDataList);
        featureOnRecyclerAdapter.notifyDataSetChanged();
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
        isFollowing = featuredDataList.get(pos).isFollowing();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofitTest();
        CollectionsAPI collectionsAPI = retrofit.create(CollectionsAPI.class);
        FollowCollectionRequestModel request = new FollowCollectionRequestModel();
        request.setUserId(userId);
        request.setUserCollectionId(collectionId);
        request.setSortOrder(sortOrder);

        if (isFollowing) {
            isFollowing = false;
            request.setDeleted(true);
            featuredDataList.get(updateFollowPos).setFollowing(false);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = collectionsAPI.followCollection(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            featuredDataList.get(updateFollowPos).setFollowing(true);
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
                        featuredDataList.get(updateFollowPos).setFollowing(false);
                        isFollowing = false;
                    }
                }
                featureOnRecyclerAdapter.setListUpdate(updateFollowPos, featuredDataList);
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
                        featuredDataList.get(updateFollowPos).setFollowing(true);
                        isFollowing = true;
                    }
                }
                featureOnRecyclerAdapter.setListUpdate(updateFollowPos, featuredDataList);
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
