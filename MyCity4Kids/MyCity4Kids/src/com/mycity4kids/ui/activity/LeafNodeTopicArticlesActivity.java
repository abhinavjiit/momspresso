package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.LeafTopicsPagerAdapter;
import com.mycity4kids.ui.fragment.LeafTopicArticlesTabFragment;
import com.mycity4kids.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class LeafNodeTopicArticlesActivity extends BaseActivity {

    //    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FrameLayout topLayerGuideLayout;

    private LeafTopicsPagerAdapter pagerAdapter;
    private ImageView imageSortBy;
    private HashMap<Topics, List<Topics>> allTopicsMap;
    private ArrayList<Topics> allTopicsList;
    //    private ArrayList<Topics> subTopicsList;
    private Toolbar toolbar;
    private TextView toolbarTitleTextView;
    private Topics leafTopic, leafTopicParent;
    private int tabPosition;
    private String jsonMyObject, jasonMyObject1;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaf_topic_articles_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);
        imageSortBy = findViewById(R.id.imageSortBy);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        topLayerGuideLayout = (FrameLayout) findViewById(R.id.topLayerGuideLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("leafTopicParent");
            jasonMyObject1 = extras.getString("leafTopic");
        }
        leafTopic = new Gson().fromJson(jasonMyObject1, Topics.class);
        leafTopicParent = new Gson().fromJson(jsonMyObject, Topics.class);


        toolbarTitleTextView.setText(leafTopicParent.getDisplay_name());

        Utils.pushOpenScreenEvent(this, "LeafTopicArticlesScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

//        if (subTopicsList.size() == 0) {
        Topics mainTopic = new Topics();
        mainTopic.setId(leafTopicParent.getId());
        String allCategoryLabel = "";
        allCategoryLabel = getString(R.string.all_categories_label);
        mainTopic.setDisplay_name(allCategoryLabel);
        mainTopic.setTitle(allCategoryLabel);

        leafTopicParent.getChild().add(0, mainTopic);

        for (int i = 0; i < leafTopicParent.getChild().size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(leafTopicParent.getChild().get(i).getDisplay_name()));
            if (leafTopicParent.getChild().get(i).getId().equals(leafTopic.getId())) {
                tabPosition = i;
            }

        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        AppUtils.changeTabsFont(this, tabLayout);

        pagerAdapter = new LeafTopicsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), leafTopicParent.getChild());
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
        viewPager.setCurrentItem(tabPosition);
        if (!SharedPrefUtils.isCoachmarksShownFlag(this, "topics_article")) {
//            showGuideView();
        }
        imageSortBy = (ImageView) findViewById(R.id.imageSortBy);

        imageSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = pagerAdapter.getCurrentFragment();//.get(viewPager.getCurrentItem());
                if (fragment != null && fragment instanceof LeafTopicArticlesTabFragment) {
                    ((LeafTopicArticlesTabFragment) fragment).showSortedByDialog();
                }
            }
        });
    }


    private void createTopicsData(TopicsResponse responseData) {
        try {
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

                if ("1".equals(responseData.getData().get(i).getShowInMenu()) && !AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
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
                        }
                    }
                }
                responseData.getData().get(i).setChild(tempUpList);

                allTopicsList.add(responseData.getData().get(i));
                allTopicsMap.put(responseData.getData().get(i), tempUpList);
            }
            BaseApplication.setTopicList(allTopicsList);
            BaseApplication.setTopicsMap(allTopicsMap);
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
        Log.d("TopicListingFragment", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TopicListingFragment", "onDestroy");
    }

//    public void showGuideView() {
//        TopicsArticlesTabFragment topicsArticlesTabFragment = ((TopicsArticlesTabFragment) pagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem()));
//        topicsArticlesTabFragment.showGuideView();
//    }

    public void showGuideTopLayer() {
        topLayerGuideLayout.setVisibility(View.VISIBLE);
    }

    public void hideGuideTopLayer() {
        topLayerGuideLayout.setVisibility(View.GONE);
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
}
