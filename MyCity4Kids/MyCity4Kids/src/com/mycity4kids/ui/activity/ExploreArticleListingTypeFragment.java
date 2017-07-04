package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ParentTopicsGridAdapter;
import com.mycity4kids.ui.fragment.FragmentMC4KHomeNew;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class ExploreArticleListingTypeFragment extends BaseFragment {

    String[] sections = {"TRENDING", "EDITOR'S PICK", "FOR YOU", "RECENT", "POPULAR", "IN YOUR CITY"};
    private ArrayList<ExploreTopicsModel> mainTopicsList;

    private TabLayout tabLayout;
    private GridView gridview;
    private Toolbar mToolbar;

    private ParentTopicsGridAdapter adapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.explore_article_listing_type_activity, container, false);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.explore_article_listing_type_activity);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("SELECT AN OPTION");
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        gridview = (GridView) view.findViewById(R.id.gridview);

        for (int i = 0; i < sections.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(sections[i]));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        changeTabsFont();
        wrapTabIndicatorToTitle(tabLayout, 25, 25);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent1 = new Intent(getActivity(), ArticleListingActivity.class);

                if (Constants.TAB_FOR_YOU.equalsIgnoreCase(tab.getText().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_FOR_YOU);
                } else if (Constants.TAB_POPULAR.equalsIgnoreCase(tab.getText().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_POPULAR);
                } else if (Constants.TAB_EDITOR_PICKS.equalsIgnoreCase(tab.getText().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_EDITOR_PICKS);
                } else if (Constants.TAB_RECENT.equalsIgnoreCase(tab.getText().toString())) {
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_RECENT);
                } else if (Constants.TAB_IN_YOUR_CITY.equalsIgnoreCase(tab.getText().toString())) {
                    Intent cityIntent = new Intent(getActivity(), ArticleListingActivity.class);
                    cityIntent.putExtra(Constants.SORT_TYPE, Constants.KEY_IN_YOUR_CITY);
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

            }
        });
        try {
            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

        }

        adapter = new ParentTopicsGridAdapter(getActivity());
        gridview.setAdapter(adapter);
        adapter.setDatalist(mainTopicsList);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getAdapter() instanceof ParentTopicsGridAdapter) {
                    Topics topic = (Topics) ((ParentTopicsGridAdapter) adapterView.getAdapter()).getItem(position);
                    TopicsListingFragment fragment1 = new TopicsListingFragment();
                    Bundle mBundle1 = new Bundle();
                    mBundle1.putString("parentTopicId", topic.getId());
                    fragment1.setArguments(mBundle1);
                    ((DashboardActivity) getActivity()).addFragment(fragment1, mBundle1, true);
                }
            }
        });

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

    private void createTopicsData(ExploreTopicsResponse responseData) {
        try {
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                mainTopicsList = new ArrayList<>();

                //Prepare structure for multi-expandable listview.
                for (int i = 0; i < responseData.getData().size(); i++) {
                    if ("1".equals(responseData.getData().get(i).getPublicVisibility())) {
                        mainTopicsList.add(responseData.getData().get(i));
                    }
                }

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
