package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LiveStreamApi;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ParentTopicsGridAdapter;
import com.mycity4kids.ui.adapter.TopicsRecyclerGridAdapter;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.fragment.GroupsViewFragment;
import com.mycity4kids.ui.livestreaming.RecentLiveStreamResponse;
import com.mycity4kids.ui.momspressotv.MomspressoTelevisionActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.widget.HeaderGridView;
import com.mycity4kids.widget.MomspressoButtonWidget;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class ExploreArticleListingTypeFragment extends BaseFragment implements View.OnClickListener,
        TopicsRecyclerGridAdapter.RecyclerViewClickListener {

    private final static String MEET_CONTRIBUTOR_ID = "meetContributorId";
    private final static String EXPLORE_SECTION_ID = "exploreSectionId";

    private ArrayList<ExploreTopicsModel> mainTopicsList;
    private String fragType = "";
    private String dynamoUserId;

    private TabLayout tabLayout;
    private HeaderGridView gridview;

    View gridViewHeader, coachmarkMyMoney;
    Tracker t;
    private ParentTopicsGridAdapter adapter;
    private View view;
    private EditText searchTopicsEditText;
    private TextView exploreCategoriesLabel;
    private RelativeLayout guideOverLay;
    private TextView guideTopicTextView1;
    private TextView guideTopicTextView2;
    private HorizontalScrollView quickLinkContainer;
    private TextView todaysBestTextView, editorsPickTextView, forYouTextView, recentTextView;
    private TextView continueTextView;
    private FrameLayout container;
    private LinearLayout coachmarkMymoneyLinearLayout;
    private RelativeLayout videosContainer, storyContainer, groupsContainer, momsTVContainer, rewardsContainer;
    private RecentLiveStreamResponse liveStreamResponse;
    private MomspressoButtonWidget liveStreamIndicator;
    private LinearLayout sectionContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.explore_article_listing_type_fragment, container, false);

        dynamoUserId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        container = view.findViewById(R.id.container);
        quickLinkContainer = (HorizontalScrollView) view.findViewById(R.id.quickLinkContainer);
        gridview = (HeaderGridView) view.findViewById(R.id.gridview);
        exploreCategoriesLabel = (TextView) view.findViewById(R.id.exploreCategoriesLabel);
        searchTopicsEditText = (EditText) view.findViewById(R.id.searchTopicsEditText);
        guideOverLay = (RelativeLayout) view.findViewById(R.id.guideOverlay);
        guideTopicTextView1 = (TextView) view.findViewById(R.id.guideTopicTextView1);
        guideTopicTextView2 = (TextView) view.findViewById(R.id.guideTopicTextView2);
        todaysBestTextView = (TextView) view.findViewById(R.id.todaysBestTextView);
        editorsPickTextView = (TextView) view.findViewById(R.id.editorsPickTextView);
        forYouTextView = (TextView) view.findViewById(R.id.forYouTextView);
        recentTextView = (TextView) view.findViewById(R.id.recentTextView);
        continueTextView = (TextView) view.findViewById(R.id.continueTextView);
        coachmarkMyMoney = (View) view.findViewById(R.id.coachmarkMyMoney);
        coachmarkMymoneyLinearLayout = (LinearLayout) view.findViewById(R.id.coachmarkMymoneyLinearLayout);
        quickLinkContainer = (HorizontalScrollView) view.findViewById(R.id.quickLinkContainer);

        fragType = getArguments().getString("fragType");

        todaysBestTextView.setOnClickListener(this);
        editorsPickTextView.setOnClickListener(this);
        forYouTextView.setOnClickListener(this);
        recentTextView.setOnClickListener(this);
        coachmarkMyMoney.setOnClickListener(this);
        quickLinkContainer.setOnClickListener(this);

        searchTopicsEditText.setVisibility(View.GONE);
        guideOverLay.setOnClickListener(this);

        gridViewHeader = inflater.inflate(R.layout.grid_view_header, null, false);
        sectionContainer = gridViewHeader.findViewById(R.id.sectionContainer);
        videosContainer = gridViewHeader.findViewById(R.id.videosContainer);
        storyContainer = gridViewHeader.findViewById(R.id.storyContainer);
        groupsContainer = gridViewHeader.findViewById(R.id.groupsContainer);
        momsTVContainer = gridViewHeader.findViewById(R.id.momsTVContainer);
        rewardsContainer = gridViewHeader.findViewById(R.id.rewardsContainer);
        liveStreamIndicator = gridViewHeader.findViewById(R.id.liveStreamIndicator);
        getLiveStream();

        videosContainer.setOnClickListener(this);
        storyContainer.setOnClickListener(this);
        groupsContainer.setOnClickListener(this);
        momsTVContainer.setOnClickListener(view -> {
            Utils.shareEventTracking(getActivity(), "Discover", "Live_Android", "Discover_Live");
            Intent intent = new Intent(getActivity(), MomspressoTelevisionActivity.class);
            startActivity(intent);
        });
        rewardsContainer.setOnClickListener(this);

        if (SharedPrefUtils.getMyMoney(BaseApplication.getAppContext()) == 0) {
            coachmarkMyMoney.setVisibility(View.GONE);
            coachmarkMymoneyLinearLayout.setVisibility(View.GONE);
            quickLinkContainer.setVisibility(View.VISIBLE);
        } else {
            quickLinkContainer.setVisibility(View.VISIBLE);
        }

        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext()
                    .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
            createTopicsData(res);

            adapter = new ParentTopicsGridAdapter(fragType);
            gridview.addHeaderView(gridViewHeader);
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
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE,
                            response.body());

                    try {
                        FileInputStream fileInputStream = BaseApplication.getAppContext()
                                .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                        createTopicsData(res);
                        adapter = new ParentTopicsGridAdapter(fragType);
                        gridview.addHeaderView(gridViewHeader);
                        gridview.setAdapter(adapter);
                        adapter.setDatalist(mainTopicsList);
                        guideTopicTextView1.setText(mainTopicsList.get(0).getDisplay_name().toUpperCase());
                        guideTopicTextView2.setText(mainTopicsList.get(1).getDisplay_name().toUpperCase());
                    } catch (FileNotFoundException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4KException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }

        gridview.setOnItemClickListener((adapterView, view, position, id) -> {
            view.setSelected(true);
            ExploreTopicsModel topic = (ExploreTopicsModel) adapterView.getAdapter().getItem(position);
            if (topic == null) {
                return;
            }
            if (MEET_CONTRIBUTOR_ID.equals(topic.getId())) {
                Intent intent = new Intent(getActivity(), ContributorListActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), TopicsListingActivity.class);
                intent.putExtra("parentTopicId", topic.getId());
                startActivity(intent);
            }
        });

        return view;
    }

    private void getLiveStream() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LiveStreamApi articleDetailsApi = retrofit.create(LiveStreamApi.class);
        Call<RecentLiveStreamResponse> call = articleDetailsApi.getRecentLiveStreams(null, null);
        call.enqueue(new Callback<RecentLiveStreamResponse>() {
            @Override
            public void onResponse(Call<RecentLiveStreamResponse> call, Response<RecentLiveStreamResponse> response) {
                try {
                    liveStreamResponse = response.body();
                    if (liveStreamResponse != null && !liveStreamResponse.getData().getResult().getEvents().isEmpty() &&
                            liveStreamResponse.getData().getResult().getEvents().get(0).getStatus()
                                    == AppConstants.LIVE_STREAM_STATUS_ONGOING) {
                        sectionContainer.removeViewAt(2);
                        sectionContainer.addView(momsTVContainer, 0);
                        liveStreamIndicator.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4KException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<RecentLiveStreamResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4KException", Log.getStackTraceString(t));
            }
        });
    }

    private void createTopicsData(ExploreTopicsResponse responseData) {
        try {
            mainTopicsList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getData().size(); i++) {
                if ("1".equals(responseData.getData().get(i).getShowInMenu()) && !AppConstants.SHORT_STORY_CATEGORYID
                        .equals(responseData.getData().get(i).getId())
                        && !AppConstants.HOME_VIDEOS_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    mainTopicsList.add(responseData.getData().get(i));
                }
            }
            if (!"search".equals(fragType)) {
                ExploreTopicsModel contributorListModel = new ExploreTopicsModel();
                contributorListModel
                        .setDisplay_name(getString(R.string.explore_listing_explore_categories_meet_contributor));
                contributorListModel.setId(MEET_CONTRIBUTOR_ID);
                mainTopicsList.add(contributorListModel);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coachmarkMyMoney:
                coachmarkMyMoney.setVisibility(View.GONE);
                coachmarkMymoneyLinearLayout.setVisibility(View.GONE);
                quickLinkContainer.setVisibility(View.VISIBLE);
                SharedPrefUtils.myMoneyCoachMark(BaseApplication.getAppContext(), 1);
            case R.id.guideOverlay:
                guideOverLay.setVisibility(View.GONE);
                if (isAdded()) {
                    ((DashboardActivity) getActivity()).hideToolbarAndNavigationLayer();
                    SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "topics", true);
                }
                break;
            case R.id.todaysBestTextView: {
                Utils.pushOpenScreenEvent(getActivity(), "TodaysBestScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "",
                        "TodaysBestScreen");
                Intent intent = new Intent(getActivity(), ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_TODAYS_BEST);
                startActivity(intent);
            }
            break;
            case R.id.editorsPickTextView: {
                Utils.pushOpenScreenEvent(getActivity(), "EditorsPickScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "",
                        "EditorsPickScreen");
                Intent intent = new Intent(getActivity(), ArticleListingActivity.class);
                intent.putExtra(Constants.SORT_TYPE, Constants.KEY_EDITOR_PICKS);
                startActivity(intent);
            }
            break;
            case R.id.rewardsContainer: {
                Utils.pushOpenScreenEvent(getActivity(), "RewardsScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "RewardsScreen");
                Intent cityIntent = new Intent(getActivity(), CampaignContainerActivity.class);
                startActivity(cityIntent);
            }
            break;
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
            case R.id.videosContainer: {
                Utils.pushOpenScreenEvent(getActivity(), "VideosScreen", dynamoUserId + "");
                Utils.pushViewQuickLinkArticlesEvent(getActivity(), "TopicScreen", dynamoUserId + "", "VideosScreen");
                Utils.momVlogEvent(getActivity(), "Home Screen", "Discover_vlogs", "", "android",
                        SharedPrefUtils.getAppLocale(getActivity()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");
                Intent cityIntent = new Intent(getActivity(), CategoryVideosListingActivity.class);
                cityIntent.putExtra("parentTopicId", AppConstants.HOME_VIDEOS_CATEGORYID);
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
                GroupsViewFragment fragment0 = new GroupsViewFragment();
                Bundle mBundle0 = new Bundle();
                fragment0.setArguments(mBundle0);
                ((DashboardActivity) getActivity()).addFragment(fragment0, mBundle0);
            }
            break;
        }
    }


    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
