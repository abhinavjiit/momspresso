package com.mycity4kids.ui.activity;

import android.content.Context;
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
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.ArticleImageTagUploadActivity;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.widget.TopicView;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
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
    PublishDraftObject draftObject;
    ArrayList<Topics> allTopicList;
    HashMap<Topics, List<Topics>> allTopicsMap;
    ArrayList<String> selectedTopicsIdList = new ArrayList<>();

    int initialListSize = 0;
    String imageURL;
    private String articleId;
    private String tags, cities;
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

        draftObject = (PublishDraftObject) getIntent().getSerializableExtra("draftItem");
        userNavigatingFrom = getIntent().getStringExtra("from");
        selectedTopics = getIntent().getParcelableArrayListExtra("selectedTopics");

        if ("publishedList".equals(userNavigatingFrom)) {
            // User coming from editing published article.
            imageURL = getIntent().getStringExtra("imageUrl");
            articleId = getIntent().getStringExtra("articleId");
            tags = getIntent().getStringExtra("tag");
            cities = getIntent().getStringExtra("cities");
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

                    JSONArray jsonArray = new JSONArray(tags);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Iterator<?> keys = jsonArray.getJSONObject(i).keys();
                        selectedTopicsIdList.add((String) keys.next());
                    }

                    try {
                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = convertStreamToString(fileInputStream);
                        TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                        selectedTopics = new ArrayList<>();
                        createTopicsData(res);
                    } catch (FileNotFoundException e) {
                        Crashlytics.logException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        Retrofit retro = BaseApplication.getInstance().getRetrofit();
                        final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

                        Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
                        call.enqueue(downloadCategoriesJSONCallback);
                    }


//                    Retrofit retro = BaseApplication.getInstance().getRetrofit();
//                    final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
//                    Call<TopicsResponse> call = topicsAPI.getTopicsCategory("" + SharedPrefUtils.getUserDetailModel(this).getId());
//                    call.enqueue(getAllTopicsResponseCallback);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    Log.d("JSONException", Log.getStackTraceString(e));
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
                intent_1.putExtra("cities", cities);
                startActivity(intent_1);
            }
        });
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            progressBar.setVisibility(View.GONE);
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                nextButton.setVisibility(View.VISIBLE);

                allTopicsMap = new HashMap<Topics, List<Topics>>();
                allTopicList = new ArrayList<>();

                //Prepare structure for multi-expandable listview.
                for (int i = 0; i < responseData.getData().size(); i++) {
                    ArrayList<Topics> tempUpList = new ArrayList<>();

                    //Set Subcategories selected for choosen tags
                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        ArrayList<Topics> tempList = new ArrayList<>();
                        if (responseData.getData().get(i).getChild().get(j).getChild().size() == 0) {
                            for (int l = 0; l < selectedTopicsIdList.size(); l++) {
                                if (selectedTopicsIdList.get(l).equals(responseData.getData().get(i).getChild().get(j).getId())) {
                                    Log.d("Change SUB to SELECTED ", "" + responseData.getData().get(i).getChild().get(j).getTitle());
                                    responseData.getData().get(i).getChild().get(j).setIsSelected(true);
                                    selectedTopics.add(responseData.getData().get(i).getChild().get(j));
                                }
                            }
                        }

                        //Set Subcategories-children selected for choosen tags
                        for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {
                            for (int l = 0; l < selectedTopicsIdList.size(); l++) {
                                if (selectedTopicsIdList.get(l).equals(responseData.getData().get(i).getChild().get(j).getChild().get(k).getId())) {
                                    Log.d("Change to SELECTED ", "" + responseData.getData().get(i).getChild().get(j).getChild().get(k).getTitle());
                                    responseData.getData().get(i).getChild().get(j).getChild().get(k).setIsSelected(true);
                                    selectedTopics.add(responseData.getData().get(i).getChild().get(j).getChild().get(k));
                                }
                            }
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());
                            tempList.add(responseData.getData().get(i).getChild().get(j).getChild().get(k));
                        }

                        responseData.getData().get(i).getChild().get(j).setChild(tempList);
                    }

                    for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {
                        responseData.getData().get(i).getChild().get(k)
                                .setParentId(responseData.getData().get(i).getId());
                        responseData.getData().get(i).getChild().get(k)
                                .setParentName(responseData.getData().get(i).getTitle());
                        tempUpList.add(responseData.getData().get(i).getChild().get(k));
                    }

                    allTopicList.add(responseData.getData().get(i));
                    allTopicsMap.put(responseData.getData().get(i),
                            tempUpList);
                }
                createSelectedTagsView();

            } else {
                showToast("Something went wrong from server");
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
            showToast(getString(R.string.went_wrong));
        }
    }

    Callback<ResponseBody> downloadCategoriesJSONCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            progressBar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
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
                        boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                        Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = convertStreamToString(fileInputStream);
                            TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
                            createTopicsData(res);
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
                progressBar.setVisibility(View.GONE);
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            Crashlytics.logException(e);
            Log.d("IOException", Log.getStackTraceString(e));
        }
        return sb.toString();
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = openFileOutput(AppConstants.CATEGORIES_JSON_FILE, Context.MODE_PRIVATE);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("TopicsFilterActivity", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
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

    /*
    * Create tags json from list of selected tags Arraylist
    * Sent as tag to server as post param.
    * */
    private void createTagObjectFromList() {
//        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        try {
            for (int i = 0; i < selectedTopics.size(); i++) {
                JSONObject jObject = new JSONObject();
                jObject.put("" + selectedTopics.get(i).getId(), selectedTopics.get(i).getTitle());
                jArray.put(jObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tags = jArray.toString();
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
