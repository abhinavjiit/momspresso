package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.NewArticleListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.AuthorDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.FilterTopicsParentExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class TopicsFilterActivity extends BaseActivity {

    ExpandableListView parentExpandableListView;
    TextView filterButtton;
    private FilterTopicsParentExpandableListAdapter filterTopicsParentExpandableListAdapter;
    private ProgressBar progressBar;
    private Toolbar mToolbar;

    int pageNum;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.topics_filter_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose Topics");

        filterButtton = (TextView) findViewById(R.id.filterButtton);
        parentExpandableListView = (ExpandableListView) findViewById(R.id.parentExpandableListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

        progressBar.setVisibility(View.VISIBLE);
        filterButtton.setVisibility(View.GONE);

        Call<TopicsResponse> call = topicsAPI.getTopicsCategory("" + SharedPrefUtils.getUserDetailModel(this).getId());
        call.enqueue(getAllTopicsResponseCallback);

        filterButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedTopics = filterTopicsParentExpandableListAdapter.getAllSelectedElements();
                if (StringUtils.isNullOrEmpty(selectedTopics)) {
                    showToast("Please select atleast one category to continue");
                    return;
                }
                Intent intent = new Intent(TopicsFilterActivity.this, FilteredTopicsArticleListingActivity.class);
                intent.putExtra("selectedTopics", selectedTopics);
                startActivity(intent);
            }
        });

    }

    Callback<TopicsResponse> getAllTopicsResponseCallback = new Callback<TopicsResponse>() {
        @Override
        public void onResponse(Call<TopicsResponse> call, retrofit2.Response<TopicsResponse> response) {
            progressBar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                TopicsResponse responseData = (TopicsResponse) response.body();
                if (responseData.getResponseCode() == 200) {

                    filterButtton.setVisibility(View.VISIBLE);

                    HashMap<Topics, List<Topics>> topicsMap = new HashMap<Topics, List<Topics>>();
                    ArrayList<Topics> topicList = new ArrayList<>();

                    //Prepare structure for multi-expandable listview.
                    for (int i = 0; i < responseData.getResult().getData().size(); i++) {
                        ArrayList<Topics> tempUpList = new ArrayList<>();

                        for (int j = 0; j < responseData.getResult().getData().get(i).getChild().size(); j++) {
                            ArrayList<Topics> tempList = new ArrayList<>();

                            //add All option to select all sub-categories-childrens only if there are more then 0 child in a subcategory.
                            if (responseData.getResult().getData().get(i).getChild().get(j).getChild().size() > 0)
                                tempList.add(new Topics("-1", "all", false, new ArrayList<Topics>(), responseData.getResult().getData().get(i).getId(),
                                        responseData.getResult().getData().get(i).getTitle()));

                            tempList.addAll(responseData.getResult().getData().get(i).getChild().get(j).getChild());
                            responseData.getResult().getData().get(i).getChild().get(j).setChild(tempList);
                        }

                        //add All option to select all sub-categories only if there are more then 0 subcategories.
                        if (responseData.getResult().getData().get(i).getChild().size() > 0)
                            tempUpList.add(new Topics("-1", "all", false, new ArrayList<Topics>(), responseData.getResult().getData().get(i).getId(),
                                    responseData.getResult().getData().get(i).getTitle()));

                        tempUpList.addAll(responseData.getResult().getData().get(i).getChild());
                        topicList.add(responseData.getResult().getData().get(i));
                        topicsMap.put(responseData.getResult().getData().get(i),
                                tempUpList);
                    }
                    filterTopicsParentExpandableListAdapter =
                            new FilterTopicsParentExpandableListAdapter(
                                    TopicsFilterActivity.this,
                                    parentExpandableListView,
                                    topicList, topicsMap
                            );
                    parentExpandableListView.setAdapter(filterTopicsParentExpandableListAdapter);
                } else if (responseData.getResponseCode() == 400) {

                }
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                Crashlytics.logException(e);
                Log.d("Exception", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<TopicsResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("Exception", Log.getStackTraceString(t));
        }
    };

    Callback<CommonParentingResponse> getArticlesForSelectedCategories = new Callback<CommonParentingResponse>() {
        @Override
        public void onResponse(Call<CommonParentingResponse> call, retrofit2.Response<CommonParentingResponse> response) {
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }

            try {
                CommonParentingResponse responseData = (CommonParentingResponse) response.body();

                if (responseData.getResponseCode() == 200) {

                } else if (responseData.getResponseCode() == 400) {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("Exception", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<CommonParentingResponse> call, Throwable t) {
            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("Exception", Log.getStackTraceString(t));
        }
    };

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
