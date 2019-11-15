package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.models.response.FeaturedOnListResponse;
import com.mycity4kids.models.response.NotificationSettingsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.FeatureListAPI;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.ui.adapter.ArticleCommentsRecyclerAdapter;
import com.mycity4kids.ui.adapter.EmailSubscriptionAdapter;
import com.mycity4kids.ui.adapter.FeatureOnRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class FeaturedOnActivity extends BaseActivity implements View.OnClickListener, FeatureOnRecyclerAdapter.RecyclerViewClickListener {

    private RecyclerView featuredonRecyclerview;
    private FeatureOnRecyclerAdapter featureOnRecyclerAdapter;
    private boolean isReuqestRunning = true;
    private ArrayList<FeaturedOnListResponse.FeaturedListResult> featuredDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_on);
        featuredonRecyclerview = findViewById(R.id.featuredon_recyclerview);
        featuredDataList = new ArrayList<>();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        featureOnRecyclerAdapter = new FeatureOnRecyclerAdapter(this, featuredDataList);
        featuredonRecyclerview.setLayoutManager(linearLayoutManager);
        featuredonRecyclerview.setAdapter(featureOnRecyclerAdapter);
        fetchFeatureList();
    }

    private void fetchFeatureList() {
        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getArticleRetrofit();
        FeatureListAPI featureListAPI = retrofit.create(FeatureListAPI.class);
        Call<FeaturedOnListResponse> call = featureListAPI.getFeatureList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),0,10);
        call.enqueue(featuredList);
    }

    private Callback<FeaturedOnListResponse> featuredList = new Callback<FeaturedOnListResponse>() {
        @Override
        public void onResponse(Call<FeaturedOnListResponse> call, retrofit2.Response<FeaturedOnListResponse> response) {
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("New comments API failure");
                Crashlytics.logException(nee);
                return;
            }

            try {
                FeaturedOnListResponse featuredOnListResponse = response.body();
                showFeatureList(featuredOnListResponse.getData().get(0).getResult());
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FeaturedOnListResponse> call, Throwable t) {
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showFeatureList(List<FeaturedOnListResponse.FeaturedListResult> featuredDataList) {
        if (featuredDataList.size() == 0) {

        } else {
            for (int i = 0; i < featuredDataList.size(); i++) {
                featuredDataList.add(featuredDataList.get(i));
            }
        }
        featureOnRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view, int position) {

    }
}
