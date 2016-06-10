package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.editor.ArticleImageTagUploadActivity;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.AddTopicsParentExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AddArticleTopicsActivity extends BaseActivity {

    ExpandableListView parentExpandableListView;
    int pageNum;
    private AddTopicsParentExpandableListAdapter topicsParentExpandableListAdapter;
    private Toolbar mToolbar;
    ArticleDraftList draftObject;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Utils.pushOpenScreenEvent(AddArticleTopicsActivity.this, "Add Topics", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        setContentView(R.layout.add_article_topics_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topics");

        draftObject = (ArticleDraftList) getIntent().getSerializableExtra("draftItem");

        TextView empty = (TextView) findViewById(R.id.done);
        parentExpandableListView = (ExpandableListView) findViewById(R.id.parentExpandableListView);

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

        Call<TopicsResponse> call = topicsAPI.getTopicsCategory("" + SharedPrefUtils.getUserDetailModel(this).getId());
        call.enqueue(getAllTopicsResponseCallback);

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Call<CommonParentingResponse> filterCall = topicsAPI.filterCategories(topicsParentExpandableListAdapter.getAllSelectedElements(), "category", pageNum);
//                filterCall.enqueue(getArticlesForSelectedCategories);
                Intent intent_1 = new Intent(AddArticleTopicsActivity.this, EditSelectedTopicsActivity.class);
                intent_1.putExtra("draftItem", draftObject);
                intent_1.putExtra("from", "editor");
                startActivity(intent_1);
            }
        });


    }

    Callback<TopicsResponse> getAllTopicsResponseCallback = new Callback<TopicsResponse>() {
        @Override
        public void onResponse(Call<TopicsResponse> call, retrofit2.Response<TopicsResponse> response) {
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }
            TopicsResponse responseData = (TopicsResponse) response.body();
            HashMap<Topics, List<Topics>> topicsMap = new HashMap<Topics, List<Topics>>();
            ArrayList<Topics> topicList = new ArrayList<>();


            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getResult().getData().size(); i++) {
                ArrayList<Topics> tempUpList = new ArrayList<>();

                for (int j = 0; j < responseData.getResult().getData().get(i).getChild().size(); j++) {
                    ArrayList<Topics> tempList = new ArrayList<>();

                    //add All option to select all sub-categories-childrens only if there are more then 0 child in a subcategory.
//                    if (responseData.getResult().getData().get(i).getChild().get(j).getChild().size() > 0)
//                        tempList.add(new Topics(-1, "all", false, new ArrayList<Topics>(), responseData.getResult().getData().get(i).getId(),
//                                responseData.getResult().getData().get(i).getTitle()));

                    //add All option to select all sub-categories only if there are more then 0 subcategories.
                    tempList.addAll(responseData.getResult().getData().get(i).getChild().get(j).getChild());
                    responseData.getResult().getData().get(i).getChild().get(j).setChild(tempList);
                }

//                if (responseData.getResult().getData().get(i).getChild().size() > 0)
//                    tempUpList.add(new Topics(-1, "all", false, new ArrayList<Topics>(), responseData.getResult().getData().get(i).getId(),
//                            responseData.getResult().getData().get(i).getTitle()));

                tempUpList.addAll(responseData.getResult().getData().get(i).getChild());
                topicList.add(responseData.getResult().getData().get(i));
                topicsMap.put(responseData.getResult().getData().get(i),
                        tempUpList);
            }
            topicsParentExpandableListAdapter =
                    new AddTopicsParentExpandableListAdapter(
                            AddArticleTopicsActivity.this,
                            parentExpandableListView,
                            topicList, topicsMap
                    );
            parentExpandableListView.setAdapter(topicsParentExpandableListAdapter);

            if (responseData.getResponseCode() == 200) {

            } else if (responseData.getResponseCode() == 400) {

            }
        }

        @Override
        public void onFailure(Call<TopicsResponse> call, Throwable t) {
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
            String commentMessage = "";
            CommonParentingResponse responseData = (CommonParentingResponse) response.body();

            if (responseData.getResponseCode() == 200) {

            } else if (responseData.getResponseCode() == 400) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it

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

    @Override
    protected void updateUi(Response response) {

    }

}
