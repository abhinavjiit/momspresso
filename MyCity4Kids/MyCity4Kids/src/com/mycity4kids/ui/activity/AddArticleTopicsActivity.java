package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.AddTopicsParentExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AddArticleTopicsActivity extends BaseActivity {

    ExpandableListView parentExpandableListView;
    int pageNum;
    private AddTopicsParentExpandableListAdapter topicsParentExpandableListAdapter;
    private Toolbar mToolbar;
    ArticleDraftList draftObject;
    private ProgressBar progressBar;
    Button nextButton;
    ArrayList<Topics> topicList;
    HashMap<Topics, List<Topics>> topicsMap;
    private String userNavigatingFrom;
    private String imageURL;
    private String articleId;
    ArrayList<Topics> remainingTopicsList;

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        nextButton = (Button) findViewById(R.id.nextButton);
        parentExpandableListView = (ExpandableListView) findViewById(R.id.parentExpandableListView);

        userNavigatingFrom = getIntent().getStringExtra("from");
        draftObject = (ArticleDraftList) getIntent().getSerializableExtra("draftItem");
        imageURL = getIntent().getStringExtra("imageUrl");
        articleId = getIntent().getStringExtra("articleId");

        //reminaingTopicsList is available only when Editing A Published Article
        if ("publishedList".equals(userNavigatingFrom)) {
            remainingTopicsList = getIntent().getParcelableArrayListExtra("remainingTopicsList");
        }
        progressBar.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
        Call<TopicsResponse> call = topicsAPI.getTopicsCategory("" + SharedPrefUtils.getUserDetailModel(this).getId());
        call.enqueue(getAllTopicsResponseCallback);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("publishedList".equals(userNavigatingFrom)) {
                    //Called when Editing A Published Article
                    if (topicsParentExpandableListAdapter.getTotalSelectedItem() == 0) {
                        showToast("Please select atleast one topics to continue");
                        return;
                    }
                    Intent _intent = new Intent(AddArticleTopicsActivity.this, EditSelectedTopicsActivity.class);
                    _intent.putExtra("from", userNavigatingFrom);
                    _intent.putExtra("draftItem", draftObject);
                    _intent.putExtra("imageURL", imageURL);
                    _intent.putExtra("articleId", articleId);
                    _intent.putParcelableArrayListExtra("selectedTopics", topicsParentExpandableListAdapter.counttotalSelected());
                    setResult(RESULT_OK, _intent);
                    finish();
                } else {
                    //Called when Publishing a new Article
                    if (topicsParentExpandableListAdapter.getTotalSelectedItem() == 0) {
                        showToast("Please select atleast one topics to continue");
                        return;
                    }
                    Intent intent_1 = new Intent(AddArticleTopicsActivity.this, EditSelectedTopicsActivity.class);
                    intent_1.putExtra("draftItem", draftObject);
                    intent_1.putExtra("from", "editor");
                    intent_1.putParcelableArrayListExtra("selectedTopics", topicsParentExpandableListAdapter.getAllSelectedElements());
                    startActivityForResult(intent_1, 1111);
                }
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

                    nextButton.setVisibility(View.VISIBLE);

                    topicsMap = new HashMap<Topics, List<Topics>>();
                    topicList = new ArrayList<>();

                    //Prepare structure for multi-expandable listview.
                    for (int i = 0; i < responseData.getResult().getData().size(); i++) {
                        ArrayList<Topics> tempUpList = new ArrayList<>();

                        for (int j = 0; j < responseData.getResult().getData().get(i).getChild().size(); j++) {
                            ArrayList<Topics> tempList = new ArrayList<>();

                            for (int k = 0; k < responseData.getResult().getData().get(i).getChild().get(j).getChild().size(); k++) {

                                //Adding All sub-subcategories
                                responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k)
                                        .setParentId(responseData.getResult().getData().get(i).getId());
                                responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k)
                                        .setParentName(responseData.getResult().getData().get(i).getTitle());
                                tempList.add(responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k));
                            }

                            responseData.getResult().getData().get(i).getChild().get(j).setChild(tempList);
                        }

                        for (int k = 0; k < responseData.getResult().getData().get(i).getChild().size(); k++) {
                            //Adding All subcategories
                            responseData.getResult().getData().get(i).getChild().get(k)
                                    .setParentId(responseData.getResult().getData().get(i).getId());
                            responseData.getResult().getData().get(i).getChild().get(k)
                                    .setParentName(responseData.getResult().getData().get(i).getTitle());
                            tempUpList.add(responseData.getResult().getData().get(i).getChild().get(k));
                        }

                        topicList.add(responseData.getResult().getData().get(i));
                        topicsMap.put(responseData.getResult().getData().get(i),
                                tempUpList);
                    }

                    int totalSelectedItems = 0;
                    if (null != remainingTopicsList) {
                        totalSelectedItems = retainItemsFromReminaingList(remainingTopicsList);
                    }

                    topicsParentExpandableListAdapter =
                            new AddTopicsParentExpandableListAdapter(
                                    AddArticleTopicsActivity.this,
                                    parentExpandableListView,
                                    topicList, topicsMap
                            );
                    parentExpandableListView.setAdapter(topicsParentExpandableListAdapter);

                    if (null != remainingTopicsList) {
                    /*
                    Called only when When Editing A Published Article
                    Expand all categories whose subcategories or sub-subcategories are selected.
                    */
                        topicsParentExpandableListAdapter.setTotalSelectedItem(totalSelectedItems);
                        for (int i = 0; i < topicList.size(); i++) {
                            for (int j = 0; j < remainingTopicsList.size(); j++) {
                                if (topicList.get(i).getId().equals(remainingTopicsList.get(j).getParentId())) {
                                    parentExpandableListView.expandGroup(i);
                                }
                            }
                        }
                    }

                } else if (responseData.getResponseCode() == 400) {
                    showToast("Something went wrong from server");
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

    @Override
    protected void updateUi(Response response) {

    }

    /*
    * Called when Publishing new article
    * Update the Expandable listview with remaining topics left after removal from EditSelectedActivity
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Topics> list = data.getParcelableArrayListExtra("remainingTopicsList");
        int totalSelectedItems = retainItemsFromReminaingList(list);
        topicsParentExpandableListAdapter.notifyDataSetChanged();
        topicsParentExpandableListAdapter.setTotalSelectedItem(totalSelectedItems);
        topicsParentExpandableListAdapter.updateChildAdapter();

    }

    /*
     *Update dataset for Expandable Listview remove topics removed from EditSelectedActivity from topics Map also
     * Also updates the total selected items.
     */
    private int retainItemsFromReminaingList(ArrayList<Topics> list) {
        int totalSelectedItems = 0;
        Iterator it = topicsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Topics> tList = ((ArrayList) pair.getValue());

            for (int j = 0; j < tList.size(); j++) {

                //subcategories with no child
                if (tList.get(j).getChild().size() == 0) {
                    System.out.println(tList.get(j).getTitle() + " = ");
                    tList.get(j).setIsSelected(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId().equals(tList.get(j).getId())) {
                            tList.get(j).setIsSelected(true);
                            totalSelectedItems++;
                        }
                    }
                    continue;
                }

                //subcategories children
                for (int k = 0; k < tList.get(j).getChild().size(); k++) {
                    System.out.println(tList.get(j).getChild().get(k).getTitle() + " = ");
                    tList.get(j).getChild().get(k).setIsSelected(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId().equals(tList.get(j).getChild().get(k).getId())) {
                            tList.get(j).getChild().get(k).setIsSelected(true);
                            totalSelectedItems++;
                        }
                    }
                }
            }
        }
        return totalSelectedItems;

    }
}
