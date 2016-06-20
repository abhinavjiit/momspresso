package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.editor.ArticleImageTagUploadActivity;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.widget.TopicView;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/6/16.
 */
public class EditSelectedTopicsActivity extends BaseActivity {

    private Toolbar mToolbar;
    FlowLayout rootView;
    LinearLayout emptyTopicsContainer;
    private Button nextButton;
    private TextView addTopicsTextView;
    private ProgressBar progressBar;

    ArrayList<Topics> selectedTopics;
    ArticleDraftList draftObject;
    ArrayList<Topics> allTopicList;
    HashMap<Topics, List<Topics>> allTopicsMap;
    ArrayList<String> selectedTopicsIdList = new ArrayList<>();

    int initialListSize = 0;
    String imageURL;
    private String articleId;
    private String tags;
    String userNavigatingFrom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_selected_topics_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        rootView = (FlowLayout) findViewById(R.id.rootView);
        emptyTopicsContainer = (LinearLayout) findViewById(R.id.emptyTopicsContainer);
        nextButton = (Button) findViewById(R.id.nextButton);
        addTopicsTextView = (TextView) findViewById(R.id.addTopicsTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topics");

        draftObject = (ArticleDraftList) getIntent().getSerializableExtra("draftItem");
        userNavigatingFrom = getIntent().getStringExtra("from");
        selectedTopics = getIntent().getParcelableArrayListExtra("selectedTopics");

        if ("publishedList".equals(userNavigatingFrom)) {
            // User coming from editing published article.
            imageURL = getIntent().getStringExtra("imageUrl");
            articleId = getIntent().getStringExtra("articleId");
            tags = getIntent().getStringExtra("tag");
            if (null != selectedTopics && !selectedTopics.isEmpty()) {
                //User is coming directly from Editor but from Expandable Listview where he has added or removed topics.
                //selected tags will be null when coming directly from editing a published article
                createSelectedTagsView();
            } else if (null == tags || tags.isEmpty()) {
                //User coming directly from Editor, possible only when editing existing published articles without tags.
                rootView.setVisibility(View.GONE);
                emptyTopicsContainer.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.GONE);
            } else {
                //User coming directly from Editor after editing a published article with tags.
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.GONE);

                    JSONObject jObject = new JSONObject(tags);
                    Iterator<?> keys = jObject.keys();

                    while (keys.hasNext()) {
                        selectedTopicsIdList.add((String) keys.next());
                    }
                    Retrofit retro = BaseApplication.getInstance().getRetrofit();
                    final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                    Call<TopicsResponse> call = topicsAPI.getTopicsCategory("" + SharedPrefUtils.getUserDetailModel(this).getId());
                    call.enqueue(getAllTopicsResponseCallback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // User publishing a new article.
            createSelectedTagsView();
        }

        addTopicsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("publishedList".equals(userNavigatingFrom)) {
                    // User coming from editing published article.
                    Intent _intent = new Intent(EditSelectedTopicsActivity.this, AddArticleTopicsActivity.class);
                    _intent.putExtra("from", userNavigatingFrom);
                    _intent.putExtra("draftItem", draftObject);
                    _intent.putExtra("imageURL", imageURL);
                    _intent.putExtra("articleId", articleId);
                    startActivityForResult(_intent, 1112);
                } else {
                    // User publishing a new article.
                    onBackPressed();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTagObjectFromList();
                Intent intent_1 = new Intent(EditSelectedTopicsActivity.this, ArticleImageTagUploadActivity.class);
                intent_1.putExtra("draftItem", draftObject);
                intent_1.putExtra("imageUrl", imageURL);
                intent_1.putExtra("from", userNavigatingFrom);
                intent_1.putExtra("articleId", articleId);
                intent_1.putExtra("tag", tags);
                startActivity(intent_1);
            }
        });
    }

    /*
    * creates selected tags views from the list of selected topics.
    * */
    private void createSelectedTagsView() {
        for (int i = 0; i < selectedTopics.size(); i++) {
            final TopicView topicView = new TopicView(this);
            topicView.setCategory(selectedTopics.get(i).getParentName());
            topicView.setSubcategory(selectedTopics.get(i).getTitle());
            topicView.setTag(selectedTopics.get(i).getId());

            ((ImageView) topicView.findViewById(R.id.removeTopicImageView)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Update selected list on removing Tags to update the all topics list in the expandable list in previous activity
                    for (int j = 0; j < selectedTopics.size(); j++) {
                        if (selectedTopics.get(j).getId().equals((String) topicView.getTag())) {
                            selectedTopics.remove(j);
                        }
                    }
                    topicView.removeTopic();
                    if (rootView.getChildCount() == 0) {
                        rootView.setVisibility(View.GONE);
                        emptyTopicsContainer.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);
                    }
                }
            });

            rootView.addView(topicView);
        }
    }

    Callback<TopicsResponse> getAllTopicsResponseCallback = new Callback<TopicsResponse>() {
        @Override
        public void onResponse(Call<TopicsResponse> call, retrofit2.Response<TopicsResponse> response) {
            progressBar.setVisibility(View.GONE);

            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }
            if (null == selectedTopics) {
                selectedTopics = new ArrayList<>();
            } else {
                selectedTopics.clear();
            }

            TopicsResponse responseData = (TopicsResponse) response.body();
            if (responseData.getResponseCode() == 200) {
                nextButton.setVisibility(View.VISIBLE);

                allTopicsMap = new HashMap<Topics, List<Topics>>();
                allTopicList = new ArrayList<>();

                //Prepare structure for multi-expandable listview.
                for (int i = 0; i < responseData.getResult().getData().size(); i++) {
                    ArrayList<Topics> tempUpList = new ArrayList<>();

                    //Set Subcategories selected for choosen tags
                    for (int j = 0; j < responseData.getResult().getData().get(i).getChild().size(); j++) {
                        ArrayList<Topics> tempList = new ArrayList<>();
                        if (responseData.getResult().getData().get(i).getChild().get(j).getChild().size() == 0) {
                            for (int l = 0; l < selectedTopicsIdList.size(); l++) {
                                if (selectedTopicsIdList.get(l).equals(responseData.getResult().getData().get(i).getChild().get(j).getId())) {
                                    Log.d("Change SUB to SELECTED ", "" + responseData.getResult().getData().get(i).getChild().get(j).getTitle());
                                    responseData.getResult().getData().get(i).getChild().get(j).setIsSelected(true);
                                    selectedTopics.add(responseData.getResult().getData().get(i).getChild().get(j));
                                }
                            }
                        }

                        //Set Subcategories-children selected for choosen tags
                        for (int k = 0; k < responseData.getResult().getData().get(i).getChild().get(j).getChild().size(); k++) {
                            for (int l = 0; l < selectedTopicsIdList.size(); l++) {
                                if (selectedTopicsIdList.get(l).equals(responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k).getId())) {
                                    Log.d("Change to SELECTED ", "" + responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k).getTitle());
                                    responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k).setIsSelected(true);
                                    selectedTopics.add(responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k));
                                }
                            }
                            responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentId(responseData.getResult().getData().get(i).getId());
                            responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentName(responseData.getResult().getData().get(i).getTitle());
                            tempList.add(responseData.getResult().getData().get(i).getChild().get(j).getChild().get(k));
                        }

                        responseData.getResult().getData().get(i).getChild().get(j).setChild(tempList);
                    }

                    for (int k = 0; k < responseData.getResult().getData().get(i).getChild().size(); k++) {
                        responseData.getResult().getData().get(i).getChild().get(k)
                                .setParentId(responseData.getResult().getData().get(i).getId());
                        responseData.getResult().getData().get(i).getChild().get(k)
                                .setParentName(responseData.getResult().getData().get(i).getTitle());
                        tempUpList.add(responseData.getResult().getData().get(i).getChild().get(k));
                    }

                    allTopicList.add(responseData.getResult().getData().get(i));
                    allTopicsMap.put(responseData.getResult().getData().get(i),
                            tempUpList);
                }
                createSelectedTagsView();

            } else if (responseData.getResponseCode() == 400) {
                showToast("Something went wrong from server");
            }
        }

        @Override
        public void onFailure(Call<TopicsResponse> call, Throwable t) {
            showToast(getString(R.string.went_wrong));
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("Exception", Log.getStackTraceString(t));
        }
    };

    /*
    * Create tags json from list of selected tags Arraylist
    * Sent as tag to server as post param.
    * */
    private void createTagObjectFromList() {
        JSONObject jObject = new JSONObject();
        try {
            for (int i = 0; i < selectedTopics.size(); i++) {
                jObject.put("" + selectedTopics.get(i).getId(), selectedTopics.get(i).getTitle());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tags = jObject.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.menu_topics, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("remainingTopicsList", selectedTopics);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.edit:
                if ("publishedList".equals(userNavigatingFrom)) {
                    // User editing a published article.
                    Intent _intent = new Intent(EditSelectedTopicsActivity.this, AddArticleTopicsActivity.class);
                    _intent.putExtra("from", userNavigatingFrom);
                    _intent.putExtra("draftItem", draftObject);
                    _intent.putExtra("imageURL", imageURL);
                    _intent.putExtra("articleId", articleId);
                    _intent.putParcelableArrayListExtra("remainingTopicsList", selectedTopics);
                    startActivityForResult(_intent, 1112);
                } else {
                    // User publishing a new article.
                    Intent intent_1 = new Intent();
                    intent_1.putParcelableArrayListExtra("remainingTopicsList", selectedTopics);
                    setResult(RESULT_OK, intent_1);
                    finish();
                }
                break;
        }
        return true;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onBackPressed() {
        if ("publishedList".equals(userNavigatingFrom)) {
            // User editing a published article.
        } else {
            // User publishing a new article.
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("remainingTopicsList", selectedTopics);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    /*
    * Called when editing a Published article
    * Update the selected tags view objects after getting updated data from ExpandableListView.
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            selectedTopics = data.getParcelableArrayListExtra("selectedTopics");
            rootView.removeAllViews();
            rootView.setVisibility(View.VISIBLE);
            emptyTopicsContainer.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            createSelectedTagsView();
        }
    }
}
