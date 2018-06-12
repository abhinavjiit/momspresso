package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.TopicsPagerAdapter;
import com.mycity4kids.ui.adapter.TopicsShortStoriesPagerAdapter;
import com.mycity4kids.ui.fragment.TopicsArticlesTabFragment;
import com.mycity4kids.utils.AppUtils;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class TopicsShortStoriesContainerFragment extends BaseFragment {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FrameLayout tablayoutLayer;

    private TopicsShortStoriesPagerAdapter pagerAdapter;

    private ArrayList<Topics> shortStoriesTopicList;
    private String parentTopicId;
    private ArrayList<Topics> subTopicsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.topic_listing_activity, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tablayoutLayer = (FrameLayout) view.findViewById(R.id.tablayoutLayer);

        parentTopicId = getArguments().getString("parentTopicId");
        try {
            shortStoriesTopicList = BaseApplication.getShortStoryTopicList();

            if (shortStoriesTopicList == null) {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                createTopicsData(res);
            }
            getCurrentParentTopicCategoriesAndSubCategories();
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

//            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
//            call.enqueue(downloadCategoriesJSONCallback);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(getActivity(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                    Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                    try {

                        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                        createTopicsData(res);
                        getCurrentParentTopicCategoriesAndSubCategories();
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
        if (subTopicsList.size() == 0) {
            Topics mainTopic = new Topics();
            mainTopic.setId(parentTopicId);
            String allCategoryLabel = "";
            if (isAdded()) {
                allCategoryLabel = getString(R.string.all_categories_label);
            } else {
                allCategoryLabel = "ALL";
            }
            mainTopic.setDisplay_name(allCategoryLabel);
            mainTopic.setTitle(allCategoryLabel);

            Topics childTopic = new Topics();
            childTopic.setId(parentTopicId);
            childTopic.setDisplay_name(allCategoryLabel);
            childTopic.setTitle(allCategoryLabel);

            ArrayList<Topics> aa = new ArrayList<Topics>();
            aa.add(childTopic);

            mainTopic.setChild(aa);
            subTopicsList.add(mainTopic);
        }
        for (int i = 0; i < subTopicsList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(subTopicsList.get(i).getDisplay_name()));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        AppUtils.changeTabsFont(getActivity(), tabLayout);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new TopicsShortStoriesPagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount(), subTopicsList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            shortStoriesTopicList = new ArrayList<>();

            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getData().size(); i++) {
                ArrayList<Topics> tempUpList = new ArrayList<>();

                if (AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    Topics allStoryModel = new Topics();
                    allStoryModel.setId(responseData.getData().get(i).getId());
                    allStoryModel.setChild(new ArrayList<Topics>());
                    allStoryModel.setIsSelected(responseData.getData().get(i).isSelected());
                    allStoryModel.setParentId(responseData.getData().get(i).getId());
                    allStoryModel.setParentName(responseData.getData().get(i).getDisplay_name());
                    allStoryModel.setPublicVisibility(responseData.getData().get(i).getPublicVisibility());
                    allStoryModel.setSlug(responseData.getData().get(i).getSlug());
                    allStoryModel.setTitle(responseData.getData().get(i).getTitle());
                    String allCategoryLabel;
                    if (isAdded()) {
                        allCategoryLabel = getString(R.string.all_categories_label);
                    } else {
                        allCategoryLabel = "ALL";
                    }
                    allStoryModel.setDisplay_name(allCategoryLabel);
                    allStoryModel.setShowInMenu("1");
                    responseData.getData().get(i).getChild().add(0, allStoryModel);


                    for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {

                        //DO NOT REMOVE below commented check -- showInMenu 0 from backend --might be used to show/hide in future
//                        if ("1".equals(responseData.getData().get(i).getChild().get(k).getShowInMenu())) {
                        //Adding All subcategories
                        responseData.getData().get(i).getChild().get(k)
                                .setParentId(responseData.getData().get(i).getId());
                        responseData.getData().get(i).getChild().get(k)
                                .setParentName(responseData.getData().get(i).getTitle());

                        // create duplicate entry for subcategories with no child
                        if (responseData.getData().get(i).getChild().get(k).getChild().isEmpty()) {
                            ArrayList<Topics> duplicateEntry = new ArrayList<Topics>();
                            //adding exact same object adds the object recursively producing stackoverflow exception when writing for Parcel.
                            //So need to create different object with same params
                            Topics dupChildTopic = new Topics();
                            dupChildTopic.setChild(new ArrayList<Topics>());
                            dupChildTopic.setId(responseData.getData().get(i).getChild().get(k).getId());
                            dupChildTopic.setIsSelected(responseData.getData().get(i).getChild().get(k).isSelected());
                            dupChildTopic.setParentId(responseData.getData().get(i).getChild().get(k).getParentId());
                            dupChildTopic.setDisplay_name(responseData.getData().get(i).getChild().get(k).getDisplay_name());
                            dupChildTopic.setParentName(responseData.getData().get(i).getChild().get(k).getParentName());
                            dupChildTopic.setPublicVisibility(responseData.getData().get(i).getChild().get(k).getPublicVisibility());
                            dupChildTopic.setShowInMenu(responseData.getData().get(i).getChild().get(k).getShowInMenu());
                            dupChildTopic.setSlug(responseData.getData().get(i).getChild().get(k).getSlug());
                            dupChildTopic.setTitle(responseData.getData().get(i).getChild().get(k).getTitle());
                            duplicateEntry.add(dupChildTopic);
                            responseData.getData().get(i).getChild().get(k).setChild(duplicateEntry);
                        }
                        tempUpList.add(responseData.getData().get(i).getChild().get(k));
//                        }
                    }
                }
                responseData.getData().get(i).setChild(tempUpList);

                shortStoriesTopicList.add(responseData.getData().get(i));
            }
            BaseApplication.setShortStoryTopicList(shortStoriesTopicList);
        } catch (Exception e) {
//            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
//            showToast(getString(R.string.went_wrong));
        }
    }

    private void getCurrentParentTopicCategoriesAndSubCategories() {
        for (int i = 0; i < shortStoriesTopicList.size(); i++) {
            subTopicsList = new ArrayList<>();
            //Selected topic is Main Category
            if (parentTopicId.equals(shortStoriesTopicList.get(i).getId())) {
                subTopicsList.addAll(shortStoriesTopicList.get(i).getChild());
                ((DashboardActivity) getActivity()).setDynamicToolbarTitle(shortStoriesTopicList.get(i).getDisplay_name());
                Utils.pushViewTopicArticlesEvent(getActivity(), "TopicArticlesListingScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "",
                        shortStoriesTopicList.get(i).getId() + "~" + shortStoriesTopicList.get(i).getDisplay_name());
                return;
            }
        }

    }

    Callback<ResponseBody> downloadCategoriesJSONCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);

                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

                Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(getActivity(), AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {

                            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                            createTopicsData(res);
                            getCurrentParentTopicCategoriesAndSubCategories();
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
//                progressBar.setVisibility(View.GONE);
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
//            progressBar.setVisibility(View.GONE);
//            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TopicListingFragment", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TopicListingFragment", "onDestroy");
    }

    public void showGuideView() {
        TopicsArticlesTabFragment topicsArticlesTabFragment = ((TopicsArticlesTabFragment) pagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem()));
        topicsArticlesTabFragment.showGuideView();
    }

    public void showTabLayer() {
        tablayoutLayer.setVisibility(View.VISIBLE);
    }

    public void hideTabLayer() {
        tablayoutLayer.setVisibility(View.GONE);
    }
}
