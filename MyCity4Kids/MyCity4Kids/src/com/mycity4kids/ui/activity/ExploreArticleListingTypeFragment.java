package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
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
public class ExploreArticleListingTypeFragment extends BaseFragment implements View.OnClickListener {

    private final static String MEET_CONTRIBUTOR_ID = "meetContributorId";

    private String[] sectionsKey = {"TRENDING", "TODAY'S BEST", "EDITOR'S PICK", "100WORDSTORY", "FOR YOU", "VIDEOS", "RECENT"};

    private ArrayList<ExploreTopicsModel> mainTopicsList;
    private String fragType = "";
    private String dynamoUserId;

    private TabLayout tabLayout;
    private GridView gridview;

    private ParentTopicsGridAdapter adapter;
    private View view;
    private EditText searchTopicsEditText;
    private TextView exploreCategoriesLabel;
    private TabLayout guideTabLayout;
    private RelativeLayout guideOverLay;
    private TextView guideTopicTextView1;
    private TextView guideTopicTextView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.explore_article_listing_type_activity, container, false);
        if (getArguments() != null) {
            fragType = getArguments().getString("fragType", "");
        }
        String[] sections = {
                getString(R.string.article_listing_type_trending_label), getString(R.string.article_listing_type_todays_best_label), getString(R.string.article_listing_type_editor_label),
                getString(R.string.article_listing_type_short_story_label), getString(R.string.article_listing_type_for_you_label),
                getString(R.string.article_listing_type_videos_label), getString(R.string.article_listing_type_recent_label)
        };

        dynamoUserId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        gridview = (GridView) view.findViewById(R.id.gridview);
        exploreCategoriesLabel = (TextView) view.findViewById(R.id.exploreCategoriesLabel);
        searchTopicsEditText = (EditText) view.findViewById(R.id.searchTopicsEditText);
        guideTabLayout = (TabLayout) view.findViewById(R.id.guide_tab_layout);
        guideOverLay = (RelativeLayout) view.findViewById(R.id.guideOverlay);
        guideTopicTextView1 = (TextView) view.findViewById(R.id.guideTopicTextView1);
        guideTopicTextView2 = (TextView) view.findViewById(R.id.guideTopicTextView2);

        if (fragType.equals("search")) {
            tabLayout.setVisibility(View.GONE);
            searchTopicsEditText.setVisibility(View.VISIBLE);
            exploreCategoriesLabel.setText(getString(R.string.search_topics_title));
            try {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                ExploreTopicsModel[] res = gson.fromJson(fileContent, ExploreTopicsModel[].class);
                createTopicsDataForFollow(res);
                adapter = new ParentTopicsGridAdapter();
                gridview.setAdapter(adapter);
                adapter.setDatalist(mainTopicsList);
                initializeTopicSearch();
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
            setUpTabLayout(sections);
            guideOverLay.setOnClickListener(this);
            try {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                createTopicsData(res);
                adapter = new ParentTopicsGridAdapter();
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
                            adapter = new ParentTopicsGridAdapter();
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
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getAdapter() instanceof ParentTopicsGridAdapter) {
                    ExploreTopicsModel topic = (ExploreTopicsModel) adapterView.getAdapter().getItem(position);
                    if (fragType.equals("search")) {
                        Intent subscribeTopicIntent = new Intent(getActivity(), SubscribeTopicsActivity.class);
                        subscribeTopicIntent.putExtra("tabPos", position);
                        startActivity(subscribeTopicIntent);
                    } else {
                        if (MEET_CONTRIBUTOR_ID.equals(topic.getId())) {
                            Intent intent = new Intent(getActivity(), ContributorListActivity.class);
                            startActivity(intent);
                        } else {
                            TopicsListingFragment fragment1 = new TopicsListingFragment();
                            Bundle mBundle1 = new Bundle();
                            mBundle1.putString("parentTopicId", topic.getId());
                            fragment1.setArguments(mBundle1);
                            ((DashboardActivity) getActivity()).addFragment(fragment1, mBundle1, true);
                        }
                    }
                }
            }
        });

        return view;
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
                            adapter = new ParentTopicsGridAdapter();
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


    private void setUpTabLayout(String[] sections) {
        for (int i = 0; i < sections.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setTag(sectionsKey[i]);
            tabLayout.addTab(tab.setText(sections[i]));
            guideTabLayout.addTab(guideTabLayout.newTab().setText(sections[i]));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        guideTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        AppUtils.changeTabsFont(getActivity(), tabLayout);
        AppUtils.changeTabsFont(getActivity(), guideTabLayout);

        wrapTabIndicatorToTitle(tabLayout, 25, 25);
        wrapTabIndicatorToTitle(guideTabLayout, 25, 25);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent1 = new Intent(getActivity(), ArticleListingActivity.class);
                if (Constants.TAB_FOR_YOU.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.pushOpenScreenEvent(getActivity(), "ForYouScreen", dynamoUserId + "");
                    Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "ForYouScreen");
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_FOR_YOU);
                } else if (Constants.TAB_POPULAR.equalsIgnoreCase(tab.getTag().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_POPULAR);
                } else if (Constants.TAB_EDITOR_PICKS.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.pushOpenScreenEvent(getActivity(), "EditorsPickScreen", dynamoUserId + "");
                    Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "EditorsPickScreen");
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_EDITOR_PICKS);
                } else if (Constants.TAB_TODAYS_BEST.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.pushOpenScreenEvent(getActivity(), "TodaysBestScreen", dynamoUserId + "");
                    Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "TodaysBestScreen");
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_TODAYS_BEST);
                } else if (Constants.TAB_100WORD_STORY.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.pushOpenScreenEvent(getActivity(), "ShortStoryListingScreen", dynamoUserId + "");
                    Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "ShortStoryListingScreen");
                    TopicsShortStoriesContainerFragment fragment1 = new TopicsShortStoriesContainerFragment();
                    Bundle mBundle1 = new Bundle();
                    mBundle1.putString("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                    fragment1.setArguments(mBundle1);
                    ((DashboardActivity) getActivity()).addFragment(fragment1, mBundle1, true);
                    return;
                } else if (Constants.TAB_RECENT.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.pushOpenScreenEvent(getActivity(), "RecentScreen", dynamoUserId + "");
                    Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "RecentScreen");
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_RECENT);
                } else if (Constants.TAB_IN_YOUR_CITY.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.pushOpenScreenEvent(getActivity(), "TopicScreen", dynamoUserId + "");
                    Intent cityIntent = new Intent(getActivity(), ArticleListingActivity.class);
                    cityIntent.putExtra(Constants.SORT_TYPE, Constants.KEY_IN_YOUR_CITY);
                    startActivity(cityIntent);
                    return;
                } else if (Constants.KEY_TRENDING.equalsIgnoreCase(tab.getTag().toString())) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    return;
                } else if (Constants.TAB_LANGUAGE.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.pushOpenScreenEvent(getActivity(), "LanguageScreen", dynamoUserId + "");
                    Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "LanguageScreen");
                    Intent cityIntent = new Intent(getActivity(), LanguageSpecificArticleListingActivity.class);
                    startActivity(cityIntent);
                    return;
                } else if (Constants.TAB_VIDEOS.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.pushOpenScreenEvent(getActivity(), "VideosScreen", dynamoUserId + "");
                    Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "VideosScreen");
                    Intent cityIntent = new Intent(getActivity(), AllVideosListingActivity.class);
                    startActivity(cityIntent);
                    return;
                }
                intent1.putExtra(Constants.FROM_SCREEN, "Topic Articles List");
                startActivity(intent1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Intent intent1 = new Intent(getActivity(), ArticleListingActivity.class);
                if (Constants.TAB_FOR_YOU.equalsIgnoreCase(tab.getTag().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_FOR_YOU);
                } else if (Constants.TAB_POPULAR.equalsIgnoreCase(tab.getTag().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_POPULAR);
                } else if (Constants.TAB_EDITOR_PICKS.equalsIgnoreCase(tab.getTag().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_EDITOR_PICKS);
                } else if (Constants.TAB_TODAYS_BEST.equalsIgnoreCase(tab.getTag().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_TODAYS_BEST);
                } else if (Constants.TAB_100WORD_STORY.equalsIgnoreCase(tab.getTag().toString())) {
                    TopicsShortStoriesContainerFragment fragment1 = new TopicsShortStoriesContainerFragment();
                    Bundle mBundle1 = new Bundle();
                    mBundle1.putString("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                    fragment1.setArguments(mBundle1);
                    ((DashboardActivity) getActivity()).addFragment(fragment1, mBundle1, true);
                    return;
                } else if (Constants.TAB_RECENT.equalsIgnoreCase(tab.getTag().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_RECENT);
                } else if (Constants.TAB_IN_YOUR_CITY.equalsIgnoreCase(tab.getTag().toString())) {
                    Intent cityIntent = new Intent(getActivity(), ArticleListingActivity.class);
                    cityIntent.putExtra(Constants.SORT_TYPE, Constants.KEY_IN_YOUR_CITY);
                    startActivity(cityIntent);
                    return;
                } else if (Constants.KEY_TRENDING.equalsIgnoreCase(tab.getTag().toString())) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    return;
                } else if (Constants.TAB_LANGUAGE.equalsIgnoreCase(tab.getTag().toString())) {
                    Intent cityIntent = new Intent(getActivity(), LanguageSpecificArticleListingActivity.class);
                    startActivity(cityIntent);
                    return;
                } else if (Constants.TAB_VIDEOS.equalsIgnoreCase(tab.getTag().toString())) {
                    Intent cityIntent = new Intent(getActivity(), AllVideosListingActivity.class);
                    startActivity(cityIntent);
                    return;
                }
                intent1.putExtra(Constants.FROM_SCREEN, "Topic Articles List");
                startActivity(intent1);
            }
        });
    }

    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        setMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        setMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        setMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

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
                if ("1".equals(responseData[i].getShowInMenu())) {
                    mainTopicsList.add(responseData[i]);
                }
            }
            if (!fragType.equals("search")) {
                ExploreTopicsModel contributorListModel = new ExploreTopicsModel();
                contributorListModel.setDisplay_name(getString(R.string.explore_listing_explore_categories_meet_contributor));
                contributorListModel.setId(MEET_CONTRIBUTOR_ID);
                mainTopicsList.add(contributorListModel);
            }
        } catch (Exception e) {
//            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
//            showToast(getString(R.string.went_wrong));
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
            if (!fragType.equals("search")) {
                ExploreTopicsModel contributorListModel = new ExploreTopicsModel();
                contributorListModel.setDisplay_name(getString(R.string.explore_listing_explore_categories_meet_contributor));
                contributorListModel.setId(MEET_CONTRIBUTOR_ID);
                mainTopicsList.add(contributorListModel);
            }
        } catch (Exception e) {
//            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
//            showToast(getString(R.string.went_wrong));
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
            case R.id.guideOverlay:
                guideOverLay.setVisibility(View.GONE);
                if (isAdded()) {
                    ((DashboardActivity) getActivity()).hideToolbarAndNavigationLayer();
                    SharedPrefUtils.setCoachmarksShownFlag(getActivity(), "topics", true);
                }
                break;
        }
    }
}
