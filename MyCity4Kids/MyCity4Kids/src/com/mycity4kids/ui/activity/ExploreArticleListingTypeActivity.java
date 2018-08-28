package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ParentTopicsGridAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class ExploreArticleListingTypeActivity extends BaseActivity implements View.OnClickListener {

    private final static String MEET_CONTRIBUTOR_ID = "meetContributorId";
    private final static String EXPLORE_SECTION_ID = "exploreSectionId";

    private ArrayList<ExploreTopicsModel> mainTopicsList;
    private String fragType = "";
    private String dynamoUserId;

    //    private TabLayout tabLayout;
    private GridView gridview;

    private ParentTopicsGridAdapter adapter;
    //    private View view;
    private EditText searchTopicsEditText;
    private TextView exploreCategoriesLabel;
    //    private TabLayout guideTabLayout;
    private RelativeLayout guideOverLay;
    private TextView guideTopicTextView1;
    private TextView guideTopicTextView2;
    private Toolbar toolbar;
    private HorizontalScrollView quickLinkContainer;
    private TextView todaysBestTextView, editorsPickTextView, shortStoryTextView, forYouTextView, videosTextView, recentTextView;
    private TextView toolbarTitle;
    private TextView continueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_article_listing_type_activity);

        dynamoUserId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        quickLinkContainer = (HorizontalScrollView) findViewById(R.id.quickLinkContainer);
        gridview = (GridView) findViewById(R.id.gridview);
        exploreCategoriesLabel = (TextView) findViewById(R.id.exploreCategoriesLabel);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        searchTopicsEditText = (EditText) findViewById(R.id.searchTopicsEditText);
        guideOverLay = (RelativeLayout) findViewById(R.id.guideOverlay);
        guideTopicTextView1 = (TextView) findViewById(R.id.guideTopicTextView1);
        guideTopicTextView2 = (TextView) findViewById(R.id.guideTopicTextView2);
        todaysBestTextView = (TextView) findViewById(R.id.todaysBestTextView);
        editorsPickTextView = (TextView) findViewById(R.id.editorsPickTextView);
        shortStoryTextView = (TextView) findViewById(R.id.shortStoryTextView);
        forYouTextView = (TextView) findViewById(R.id.forYouTextView);
        videosTextView = (TextView) findViewById(R.id.videosTextView);
        recentTextView = (TextView) findViewById(R.id.recentTextView);
        continueTextView = (TextView) findViewById(R.id.continueTextView);

        fragType = getIntent().getStringExtra("fragType");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        todaysBestTextView.setOnClickListener(this);
        editorsPickTextView.setOnClickListener(this);
        shortStoryTextView.setOnClickListener(this);
        forYouTextView.setOnClickListener(this);
        videosTextView.setOnClickListener(this);
        recentTextView.setOnClickListener(this);

        if ("search".equals(fragType)) {
            toolbarTitle.setText(getString(R.string.search_topics_toolbar_title));
            quickLinkContainer.setVisibility(View.GONE);
            continueTextView.setVisibility(View.VISIBLE);
            continueTextView.setEnabled(false);
            continueTextView.setOnClickListener(this);
//            searchTopicsEditText.setVisibility(View.VISIBLE);
            exploreCategoriesLabel.setText(getString(R.string.search_topics_title));
            exploreCategoriesLabel.setVisibility(View.GONE);
            try {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                ExploreTopicsModel[] res = gson.fromJson(fileContent, ExploreTopicsModel[].class);
                createTopicsDataForFollow(res);
                adapter = new ParentTopicsGridAdapter(fragType);
                gridview.setAdapter(adapter);
                adapter.setDatalist(mainTopicsList);
//                initializeTopicSearch();
            } catch (FileNotFoundException e) {
                Crashlytics.logException(e);
                Log.d("FileNotFoundException", Log.getStackTraceString(e));

                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
                call.enqueue(downloadFollowTopicsJSONCallback);
            }
        } else {
            searchTopicsEditText.setVisibility(View.GONE);
            exploreCategoriesLabel.setText(getString(R.string.explore_listing_explore_categories_title));
//            setUpTabLayout(sections);
            guideOverLay.setOnClickListener(this);
            try {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                createTopicsData(res);
                adapter = new ParentTopicsGridAdapter(fragType);
                gridview.setAdapter(adapter);
                adapter.setDatalist(mainTopicsList);
                guideTopicTextView1.setText(mainTopicsList.get(0).getDisplay_name().toUpperCase());
                guideTopicTextView2.setText(mainTopicsList.get(1).getDisplay_name().toUpperCase());
            } catch (FileNotFoundException e) {
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

                Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());

                        try {
                            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                            createTopicsData(res);
                            adapter = new ParentTopicsGridAdapter(fragType);
                            gridview.setAdapter(adapter);
                            adapter.setDatalist(mainTopicsList);
                            guideTopicTextView1.setText(mainTopicsList.get(0).getDisplay_name().toUpperCase());
                            guideTopicTextView2.setText(mainTopicsList.get(1).getDisplay_name().toUpperCase());
                        } catch (FileNotFoundException e) {
                            Crashlytics.logException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Crashlytics.logException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                    }
                });
            }
            if (!SharedPrefUtils.isCoachmarksShownFlag(this, "topics")) {
                showGuideView();
            }
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);
                if (adapterView.getAdapter() instanceof ParentTopicsGridAdapter) {
                    ExploreTopicsModel topic = (ExploreTopicsModel) adapterView.getAdapter().getItem(position);
                    if ("search".equals(fragType)) {
                        topic.setIsSelected(!topic.isSelected());
                        if (getSelectedTopicCount() > 0) {
                            continueTextView.setEnabled(true);
                        } else {
                            continueTextView.setEnabled(false);
                        }
                        adapter.notifyDataSetChanged();
//                        Intent subscribeTopicIntent = new Intent(ExploreArticleListingTypeActivity.this, SubscribeTopicsActivity.class);
//                        subscribeTopicIntent.putExtra("tabPos", position);
//                        startActivity(subscribeTopicIntent);
                    } else {
                        if (MEET_CONTRIBUTOR_ID.equals(topic.getId())) {
                            Intent intent = new Intent(ExploreArticleListingTypeActivity.this, ContributorListActivity.class);
                            startActivity(intent);
                        } else if (EXPLORE_SECTION_ID.equals(topic.getId())) {
                            Intent intent = new Intent(ExploreArticleListingTypeActivity.this, ExploreEventsResourcesActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ExploreArticleListingTypeActivity.this, TopicsListingActivity.class);
                            intent.putExtra("parentTopicId", topic.getId());
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private int getSelectedTopicCount() {
        int selectedTopic = 0;
        for (int i = 0; i < mainTopicsList.size(); i++) {
            if (mainTopicsList.get(i).isSelected()) {
                selectedTopic++;
            }
        }
        return selectedTopic;
    }

    private void initializeTopicSearch() {
        searchTopicsEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = searchTopicsEditText.getText().toString().toLowerCase();
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });
    }

    public void showGuideView() {
        guideOverLay.setVisibility(View.VISIBLE);
    }

    Callback<ResponseBody> downloadFollowTopicsJSONCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);

                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                String popularURL = jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category").getString("popularLocation");
                Call<ResponseBody> caller = topicsAPI.downloadTopicsListForFollowUnfollow(popularURL);

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                            ExploreTopicsModel[] res = gson.fromJson(fileContent, ExploreTopicsModel[].class);
                            createTopicsDataForFollow(res);
                            adapter = new ParentTopicsGridAdapter(fragType);
                            gridview.setAdapter(adapter);
                            adapter.setDatalist(mainTopicsList);
                            initializeTopicSearch();
                        } catch (FileNotFoundException e) {
                            Crashlytics.logException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Crashlytics.logException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void setMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }

    private void createTopicsDataForFollow(ExploreTopicsModel[] responseData) {
        try {
            mainTopicsList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.length; i++) {
                if ("1".equals(responseData[i].getShowInMenu()) && responseData[i].getChild() != null && !responseData[i].getChild().isEmpty()) {
                    mainTopicsList.add(responseData[i]);
                }
            }
            if (!"search".equals(fragType)) {
                ExploreTopicsModel contributorListModel = new ExploreTopicsModel();
                contributorListModel.setDisplay_name(getString(R.string.explore_listing_explore_categories_meet_contributor));
                contributorListModel.setId(MEET_CONTRIBUTOR_ID);
                mainTopicsList.add(contributorListModel);

                ExploreTopicsModel exploreSectionModel = new ExploreTopicsModel();
                exploreSectionModel.setDisplay_name(getString(R.string.home_screen_explore_title));
                exploreSectionModel.setId(EXPLORE_SECTION_ID);
                mainTopicsList.add(exploreSectionModel);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void createTopicsData(ExploreTopicsResponse responseData) {
        try {
            mainTopicsList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getData().size(); i++) {
                if ("1".equals(responseData.getData().get(i).getShowInMenu()) && !AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    mainTopicsList.add(responseData.getData().get(i));
                }
            }
            if (!"search".equals(fragType)) {
                ExploreTopicsModel contributorListModel = new ExploreTopicsModel();
                contributorListModel.setDisplay_name(getString(R.string.explore_listing_explore_categories_meet_contributor));
                contributorListModel.setId(MEET_CONTRIBUTOR_ID);
                mainTopicsList.add(contributorListModel);

                ExploreTopicsModel exploreSectionModel = new ExploreTopicsModel();
                exploreSectionModel.setDisplay_name(getString(R.string.home_screen_explore_title));
                exploreSectionModel.setId(EXPLORE_SECTION_ID);
                mainTopicsList.add(exploreSectionModel);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("ExploreArticle", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ExploreArticle", "onDestroy");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueTextView: {
//                getSelectedTopicsList();
                Intent intent = new Intent(ExploreArticleListingTypeActivity.this, SubscribeTopicsActivity.class);
                intent.putStringArrayListExtra("selectedTopicList", getSelectedTopicsList());
                startActivity(intent);
            }
            break;
            case R.id.guideOverlay:
                guideOverLay.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(this, "topics", true);
                break;
            case R.id.todaysBestTextView: {
                Utils.pushOpenScreenEvent(ExploreArticleListingTypeActivity.this, "TodaysBestScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(ExploreArticleListingTypeActivity.this, "TopicScreen", dynamoUserId + "", "TodaysBestScreen");
                Intent intent = new Intent(ExploreArticleListingTypeActivity.this, ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_TODAYS_BEST);
                startActivity(intent);
            }
            break;
            case R.id.editorsPickTextView: {
                Utils.pushOpenScreenEvent(ExploreArticleListingTypeActivity.this, "EditorsPickScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(ExploreArticleListingTypeActivity.this, "TopicScreen", dynamoUserId + "", "EditorsPickScreen");
                Intent intent = new Intent(ExploreArticleListingTypeActivity.this, ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_EDITOR_PICKS);
                startActivity(intent);
            }
            break;
            case R.id.shortStoryTextView: {
                Intent intent = new Intent(ExploreArticleListingTypeActivity.this, ShortStoriesListingContainerActivity.class);
                intent.putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                startActivity(intent);
            }
            break;
            case R.id.forYouTextView: {
                Utils.pushOpenScreenEvent(ExploreArticleListingTypeActivity.this, "ForYouScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(ExploreArticleListingTypeActivity.this, "TopicScreen", dynamoUserId + "", "ForYouScreen");
                Intent intent = new Intent(ExploreArticleListingTypeActivity.this, ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_FOR_YOU);
                startActivity(intent);
            }
            break;
            case R.id.videosTextView: {
                Utils.pushOpenScreenEvent(ExploreArticleListingTypeActivity.this, "VideosScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(ExploreArticleListingTypeActivity.this, "TopicScreen", dynamoUserId + "", "VideosScreen");
                Intent cityIntent = new Intent(ExploreArticleListingTypeActivity.this, AllVideosListingActivity.class);
                startActivity(cityIntent);
            }
            break;
            case R.id.recentTextView: {
                Utils.pushOpenScreenEvent(ExploreArticleListingTypeActivity.this, "RecentScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(ExploreArticleListingTypeActivity.this, "TopicScreen", dynamoUserId + "", "RecentScreen");
                Intent intent = new Intent(ExploreArticleListingTypeActivity.this, ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_RECENT);
                startActivity(intent);
            }
            break;

        }
    }

    private ArrayList<String> getSelectedTopicsList() {
        ArrayList<String> categoryIdList = new ArrayList<>();
        int selectedTopic = 0;
        for (int i = 0; i < mainTopicsList.size(); i++) {
            if (mainTopicsList.get(i).isSelected()) {
                categoryIdList.add(mainTopicsList.get(i).getId());
            }
        }
        return categoryIdList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.activity_drawer_slide_in_from_right, R.anim.activity_drawer_slide_out_to_left);
    }
}
