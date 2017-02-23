package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.HorizontalScrollCustomView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 5/1/17.
 */
public class AllVideoSectionActivity extends BaseActivity {

    private Toolbar mToolbar;
    private HorizontalScrollCustomView momspressoSection, funnyVideosSection;

    private ArrayList<ArticleListingResult> mMomspressoArticleListing;
    private ArrayList<VlogsListingAndDetailResult> funnyVideosListing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_video_section_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        momspressoSection = (HorizontalScrollCustomView) findViewById(R.id.momspressoSection);
        funnyVideosSection = (HorizontalScrollCustomView) findViewById(R.id.funnyVideosSection);
        momspressoSection.setSectionTitle(getString(R.string.home_sections_title_momspresso));
        funnyVideosSection.setSectionTitle(getString(R.string.home_sections_title_funny_videos));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("VIDEOS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMomspressoArticleListing = new ArrayList<>();
        funnyVideosListing = new ArrayList<>();

//        String momspressoCategoryId = getMomspressoCategory();
        hitMomspressoListingApi(AppConstants.MOMSPRESSO_CATEGORYID);
        hitFunnyVideosListingApi();
    }

    private void hitMomspressoListingApi(String momspressoCategoryId) {
//        momspressoProgressbar.setVisibility(View.VISIBLE);
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(momspressoCategoryId, 0, 1, 10);
        filterCall.enqueue(momspressoListingResponseCallback);
    }

    private void hitFunnyVideosListingApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(0, 9, 0, 3);
        callRecentVideoArticles.enqueue(funnyVideosResponseCallback);
    }

    private Callback<ArticleListingResponse> momspressoListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
//            momspressoProgressbar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processMomspressoListingResponse(responseData);
                } else {
                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private Callback<VlogsListingResponse> funnyVideosResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }

            try {
                VlogsListingResponse responseData = (VlogsListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processFunnyVideosResponse(responseData);
                } else {
                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private void processMomspressoListingResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            momspressoSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            mMomspressoArticleListing.clear();
            mMomspressoArticleListing.addAll(responseData.getData().get(0).getResult());
            momspressoSection.setmDatalist(mMomspressoArticleListing, Constants.KEY_MOMSPRESSO);
        }
    }

    private void processFunnyVideosResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            funnyVideosSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            funnyVideosListing.clear();
            funnyVideosListing.addAll(responseData.getData().get(0).getResult());
            funnyVideosSection.setVlogslist(funnyVideosListing);
        }
    }

    private String getMomspressoCategory() {
        if (StringUtils.isNullOrEmpty(SharedPrefUtils.getMomspressoCategory(this).getId())) {
            try {
                FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                TopicsResponse responseData = new Gson().fromJson(fileContent, TopicsResponse.class);

                for (int i = 0; i < responseData.getData().size(); i++) {
                    if (AppConstants.MOMSPRESSO_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                        SharedPrefUtils.setMomspressoCategory(this, responseData.getData().get(i));
                        return responseData.getData().get(i).getId();
                    }
                }
            } catch (FileNotFoundException fnfe) {

            }
        } else {
            return SharedPrefUtils.getMomspressoCategory(this).getId();
        }
        return null;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }
}
