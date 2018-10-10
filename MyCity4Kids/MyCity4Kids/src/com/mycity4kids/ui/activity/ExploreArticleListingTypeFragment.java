package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
import com.mycity4kids.ui.adapter.TopicsRecyclerGridAdapter;
import com.mycity4kids.ui.fragment.GroupsFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.widget.HeaderGridView;

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
public class ExploreArticleListingTypeFragment extends BaseFragment implements View.OnClickListener, TopicsRecyclerGridAdapter.RecyclerViewClickListener {

    private final static String MEET_CONTRIBUTOR_ID = "meetContributorId";
    private final static String EXPLORE_SECTION_ID = "exploreSectionId";

    private ArrayList<ExploreTopicsModel> mainTopicsList;
    private String fragType = "";
    private String dynamoUserId;

    private TabLayout tabLayout;
    private HeaderGridView gridview;

    private ParentTopicsGridAdapter adapter;
    private View view;
    private EditText searchTopicsEditText;
    private TextView exploreCategoriesLabel;
    //    private TabLayout guideTabLayout;
    private RelativeLayout guideOverLay;
    private TextView guideTopicTextView1;
    private TextView guideTopicTextView2;
    private HorizontalScrollView quickLinkContainer;
    private TextView todaysBestTextView, editorsPickTextView, /*shortStoryTextView,*/
            forYouTextView, /*videosTextView,*/
            recentTextView;
    private TextView continueTextView;
    private RelativeLayout videosContainer, storyContainer, groupsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.explore_article_listing_type_fragment, container, false);

        dynamoUserId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        quickLinkContainer = (HorizontalScrollView) view.findViewById(R.id.quickLinkContainer);
        gridview = (HeaderGridView) view.findViewById(R.id.gridview);
        exploreCategoriesLabel = (TextView) view.findViewById(R.id.exploreCategoriesLabel);
        searchTopicsEditText = (EditText) view.findViewById(R.id.searchTopicsEditText);
        guideOverLay = (RelativeLayout) view.findViewById(R.id.guideOverlay);
        guideTopicTextView1 = (TextView) view.findViewById(R.id.guideTopicTextView1);
        guideTopicTextView2 = (TextView) view.findViewById(R.id.guideTopicTextView2);
        todaysBestTextView = (TextView) view.findViewById(R.id.todaysBestTextView);
        editorsPickTextView = (TextView) view.findViewById(R.id.editorsPickTextView);
//        shortStoryTextView = (TextView) view.findViewById(R.id.shortStoryTextView);
        forYouTextView = (TextView) view.findViewById(R.id.forYouTextView);
//        videosTextView = (TextView) view.findViewById(R.id.videosTextView);
        recentTextView = (TextView) view.findViewById(R.id.recentTextView);
        continueTextView = (TextView) view.findViewById(R.id.continueTextView);

        fragType = getArguments().getString("fragType");

        todaysBestTextView.setOnClickListener(this);
        editorsPickTextView.setOnClickListener(this);
//        shortStoryTextView.setOnClickListener(this);
        forYouTextView.setOnClickListener(this);
//        videosTextView.setOnClickListener(this);
        recentTextView.setOnClickListener(this);

        searchTopicsEditText.setVisibility(View.GONE);
        guideOverLay.setOnClickListener(this);

        View gridViewHeader = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.grid_view_header, gridview, false);
        videosContainer = (RelativeLayout) gridViewHeader.findViewById(R.id.videosContainer);
        storyContainer = (RelativeLayout) gridViewHeader.findViewById(R.id.storyContainer);
        groupsContainer = (RelativeLayout) gridViewHeader.findViewById(R.id.groupsContainer);

        videosContainer.setOnClickListener(this);
        storyContainer.setOnClickListener(this);
        groupsContainer.setOnClickListener(this);
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
            createTopicsData(res);

            adapter = new ParentTopicsGridAdapter(fragType);
            gridview.addHeaderView(gridViewHeader);
            gridview.setAdapter(adapter);
            adapter.setDatalist(mainTopicsList);

//            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//            recyclerView.setHasFixedSize(true);
//            final GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
//            recyclerView.setLayoutManager(manager);
//            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.groups_column_spacing);
//            recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
//            final TopicsRecyclerGridAdapter topicsRecyclerGridAdapter = new TopicsRecyclerGridAdapter(getActivity(), this);
//            topicsRecyclerGridAdapter.setDatalist(mainTopicsList);
//            recyclerView.setAdapter(topicsRecyclerGridAdapter);


//            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    return topicsRecyclerGridAdapter.isHeader(position) ? manager.getSpanCount() : 1;
//                }
//            });
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
        if (!SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "topics")) {
            showGuideView();
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);
                ExploreTopicsModel topic = (ExploreTopicsModel) adapterView.getAdapter().getItem(position);
                if (MEET_CONTRIBUTOR_ID.equals(topic.getId())) {
                    Intent intent = new Intent(getActivity(), ContributorListActivity.class);
                    startActivity(intent);
                } else if (EXPLORE_SECTION_ID.equals(topic.getId())) {
                    Intent intent = new Intent(getActivity(), ExploreEventsResourcesActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), TopicsListingActivity.class);
                    intent.putExtra("parentTopicId", topic.getId());
                    startActivity(intent);
                }
            }
        });

        return view;
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

    private void createTopicsDataForFollow(ExploreTopicsModel[] responseData) {
        try {
            mainTopicsList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.length; i++) {
                if ("1".equals(responseData[i].getShowInMenu())) {
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
            case R.id.todaysBestTextView: {
                Utils.pushOpenScreenEvent(getActivity(), "TodaysBestScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "TodaysBestScreen");
                Intent intent = new Intent(getActivity(), ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_TODAYS_BEST);
                startActivity(intent);
            }
            break;
            case R.id.editorsPickTextView: {
                Utils.pushOpenScreenEvent(getActivity(), "EditorsPickScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "EditorsPickScreen");
                Intent intent = new Intent(getActivity(), ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_EDITOR_PICKS);
                startActivity(intent);
            }
            break;
//            case R.id.shortStoryTextView:
            case R.id.storyContainer: {
                Intent intent = new Intent(getActivity(), ShortStoriesListingContainerActivity.class);
                intent.putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                startActivity(intent);
            }
            break;
            case R.id.forYouTextView: {
                Utils.pushOpenScreenEvent(getActivity(), "ForYouScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "ForYouScreen");
                Intent intent = new Intent(getActivity(), ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_FOR_YOU);
                startActivity(intent);
            }
            break;
//            case R.id.videosTextView:
            case R.id.videosContainer: {
                Utils.pushOpenScreenEvent(getActivity(), "VideosScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "VideosScreen");
                Intent cityIntent = new Intent(getActivity(), AllVideosListingActivity.class);
                startActivity(cityIntent);
            }
            break;
            case R.id.recentTextView: {
                Utils.pushOpenScreenEvent(getActivity(), "RecentScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "RecentScreen");
                Intent intent = new Intent(getActivity(), ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_RECENT);
                startActivity(intent);
            }
            break;
            case R.id.groupsContainer: {
                GroupsFragment fragment0 = new GroupsFragment();
                Bundle mBundle0 = new Bundle();
                fragment0.setArguments(mBundle0);
                ((DashboardActivity) getActivity()).addFragment(fragment0, mBundle0, true);
            }
            break;

        }
    }


    @Override
    public void onClick(View view, int position) {

    }
}
