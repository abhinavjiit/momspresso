package com.mycity4kids.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ParentTopicsGridAdapter;
import com.mycity4kids.ui.adapter.TopicsPagerAdapter;
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
public class TopicsListingFragment extends BaseFragment {

    private View view;
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private GridView gridview;
    private ViewPager viewPager;

    private TopicsPagerAdapter pagerAdapter;
    private ParentTopicsGridAdapter adapter;

    private HashMap<Topics, List<Topics>> allTopicsMap;
    private ArrayList<Topics> allTopicsList;
    private String parentTopicId;
    private ArrayList<Topics> subTopicsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.topic_listing_activity, container, false);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.explore_article_listing_type_activity);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("SELECT AN OPTION");

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

//        try {
//            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
//            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
//            createTopicsData(res);
//        } catch (FileNotFoundException e) {
//            Crashlytics.logException(e);
//            Log.d("FileNotFoundException", Log.getStackTraceString(e));
//            Retrofit retro = BaseApplication.getInstance().getRetrofit();
//            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
//
//        }
        parentTopicId = getArguments().getString("parentTopicId");
        try {
            allTopicsList = BaseApplication.getTopicList();
            allTopicsMap = BaseApplication.getTopicsMap();

            if (allTopicsList == null || allTopicsMap == null) {
                FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
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

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadCategoriesJSONCallback);
        }
        for (int i = 0; i < subTopicsList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(subTopicsList.get(i).getDisplay_name()));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        changeTabsFont();

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new TopicsPagerAdapter
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
//        adapter = new ParentTopicsGridAdapter(getActivity());
//        gridview.setAdapter(adapter);
//        adapter.setDatalist(subTopicsList);
        return view;
    }

    private void changeTabsFont() {
        //Typeface font = Typeface.createFromAsset(getAssets(), "fonts/androidnation.ttf");
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + "oswald_regular.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(myTypeface, Typeface.NORMAL);
                }
            }
        }
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                allTopicsMap = new HashMap<Topics, List<Topics>>();
                allTopicsList = new ArrayList<>();

                //Prepare structure for multi-expandable listview.
                for (int i = 0; i < responseData.getData().size(); i++) {
                    ArrayList<Topics> tempUpList = new ArrayList<>();

                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        ArrayList<Topics> tempList = new ArrayList<>();
                        for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {
                            if ("1".equals(responseData.getData().get(i).getChild().get(j).getChild().get(k).getShowInMenu())) {
                                //Adding All sub-subcategories
                                responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                        .setParentId(responseData.getData().get(i).getId());
                                responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                        .setParentName(responseData.getData().get(i).getTitle());
                                tempList.add(responseData.getData().get(i).getChild().get(j).getChild().get(k));
                            }
                        }
                        responseData.getData().get(i).getChild().get(j).setChild(tempList);
                    }

                    if ("1".equals(responseData.getData().get(i).getShowInMenu())) {
                        for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {
                            if ("1".equals(responseData.getData().get(i).getChild().get(k).getShowInMenu())) {
                                //Adding All subcategories
                                responseData.getData().get(i).getChild().get(k)
                                        .setParentId(responseData.getData().get(i).getId());
                                responseData.getData().get(i).getChild().get(k)
                                        .setParentName(responseData.getData().get(i).getTitle());

                                // create duplicate entry for subcategories with no child
                                if (responseData.getData().get(i).getChild().get(k).getChild().isEmpty()) {
                                    ArrayList<Topics> duplicateEntry = new ArrayList<Topics>();
                                    duplicateEntry.add(responseData.getData().get(i).getChild().get(k));
                                    responseData.getData().get(i).getChild().get(k).setChild(duplicateEntry);
                                }
                                tempUpList.add(responseData.getData().get(i).getChild().get(k));
                            }
                        }
                    }
                    responseData.getData().get(i).setChild(tempUpList);

//                    if ("1".equals(responseData.getData().get(i).getPublicVisibility())) {
                    allTopicsList.add(responseData.getData().get(i));
                    allTopicsMap.put(responseData.getData().get(i), tempUpList);
//                    }
                }
                BaseApplication.setTopicList(allTopicsList);
                BaseApplication.setTopicsMap(allTopicsMap);
            } else {
//                showToast(getString(R.string.server_error));
            }
        } catch (Exception e) {
//            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
//            showToast(getString(R.string.went_wrong));
        }
    }

    private void getCurrentParentTopicCategoriesAndSubCategories() {
        for (int i = 0; i < allTopicsList.size(); i++) {
            subTopicsList = new ArrayList<>();
            //Selected topic is Main Category
            if (parentTopicId.equals(allTopicsList.get(i).getId())) {
                subTopicsList.addAll(allTopicsList.get(i).getChild());
                ((DashboardActivity) getActivity()).setDynamicToolbarTitle(allTopicsList.get(i).getDisplay_name());
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

                Call<ResponseBody> caller = topicsAPI.downloadFileWithDynamicUrlSync(jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category").getString("location"));

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(getActivity(), AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
//                            Topics t = new Topics();
//                            t.setId("");
//                            t.setDisplay_name("");
//                            SharedPrefUtils.setMomspressoCategory(FilteredTopicsArticleListingActivity.this, t);

                            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                            createTopicsData(res);
                            getCurrentParentTopicCategoriesAndSubCategories();
//                            if (AppConstants.TOPIC_LEVEL_SUB_SUB_CATEGORY.equals(topicLevel)) {
//                                sortBgLayout.setVisibility(View.GONE);
//                                bottomOptionMenu.setVisibility(View.GONE);
//                            } else {
//                                frameLayout.setVisibility(View.GONE);
//                                fabMenu.setVisibility(View.GONE);
//                                fabSort.setVisibility(View.GONE);
//                                popularSortFAB.setVisibility(View.GONE);
//                                recentSortFAB.setVisibility(View.GONE);
//                            }
//                            openFilterDialog();
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
        Log.d("ExploreArticle", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ExploreArticle", "onDestroy");
    }
}
