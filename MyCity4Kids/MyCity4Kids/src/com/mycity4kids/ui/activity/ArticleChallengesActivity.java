package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.retrofitAPIsInterfaces.ArticlePublishAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ArticleChallengesRecyclerAdapter;
import com.mycity4kids.ui.adapter.ChallengeListingRecycleAdapter;
import com.mycity4kids.utils.ArrayAdapterFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ArticleChallengesActivity extends BaseActivity implements ChallengeListingRecycleAdapter.RecyclerViewClickListener {


    //    private ArrayList<String> activeArticleChallengeList;
//    private Topics currentActiveChallenge;
//    private int nextPageNumber = 1;
//    private int limit = 15;
//    private int sortType = 0;
//    private boolean isRequestRunning = false;
//    private boolean isLastPageReached = false;
//    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private ArrayList<Topics> mDatalist;

    private ProgressBar progressBar;
    private RecyclerView challengeArticleRecyclerView;
    private Toolbar mToolbar;
    private ArticleChallengesRecyclerAdapter articleChallengesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_challenges_activity);

        progressBar = findViewById(R.id.progressBar);
        challengeArticleRecyclerView = findViewById(R.id.challengeArticleRecyclerView);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getActiveChallenge(AppConstants.ARTICLE_CHALLENGES_ID);
        progressBar.setVisibility(View.VISIBLE);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        challengeArticleRecyclerView.setLayoutManager(llm);

    }

    private void getActiveChallenge(String categoryId) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticlePublishAPI articlePublishAPI = retrofit.create(ArticlePublishAPI.class);
        Call<ResponseBody> call = articlePublishAPI.getArticleChallenges(categoryId);
        call.enqueue(articleChallengeResponseCallback);

    }

    private Callback<ResponseBody> articleChallengeResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            progressBar.setVisibility(View.GONE);
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            if (response.isSuccessful()) {
                try {
                    String strResponse = new String(response.body().bytes());
                    JSONObject jsonObject = new JSONObject(strResponse);

                    Gson gson1 = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                    ExploreTopicsResponse exploreTopicsResponse = gson1.fromJson(strResponse, ExploreTopicsResponse.class);

                    JSONArray arr = jsonObject.getJSONArray("child");
                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                    mDatalist = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {

                        Topics topic = gson.fromJson(arr.getString(i), Topics.class);
                        if ("true".equals(topic.getPublicVisibility())
                                && topic.getExtraData() != null && !topic.getExtraData().isEmpty()
                                && "1".equals(topic.getExtraData().get(0).getChallenge().getActive())) {
                            Log.d("ARTICLECHALLENGE", "CHALLENGE ------- " + topic.getDisplay_name());
                            mDatalist.add(topic);
                        } else {
                            Log.d("ARTICLECHALLENGE", "GHANTA ------- " + i);
                        }
                    }
                    articleChallengesRecyclerAdapter = new ArticleChallengesRecyclerAdapter(ArticleChallengesActivity.this, mDatalist);
                    challengeArticleRecyclerView.setAdapter(articleChallengesRecyclerAdapter);
                    articleChallengesRecyclerAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Crashlytics.logException(e);
                    Log.d("FileNotFoundException", Log.getStackTraceString(e));
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Log.d("REQUEST", "REQUEST BODY -- " + call.request());
            Crashlytics.logException(t);
            Log.d("FileNotFoundException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view, int position, String activeUrl) {

    }
}