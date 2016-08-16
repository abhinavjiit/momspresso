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
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.AddTopicsParentExpandableListAdapter;

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
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AddArticleTopicsActivity extends BaseActivity {

    ExpandableListView parentExpandableListView;
    int pageNum;
    private AddTopicsParentExpandableListAdapter topicsParentExpandableListAdapter;
    private Toolbar mToolbar;
    PublishDraftObject draftObject;
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
        draftObject = (PublishDraftObject) getIntent().getSerializableExtra("draftItem");
        imageURL = getIntent().getStringExtra("imageUrl");
        articleId = getIntent().getStringExtra("articleId");

        //reminaingTopicsList is available only when Editing A Published Article
        if ("publishedList".equals(userNavigatingFrom)) {
            remainingTopicsList = getIntent().getParcelableArrayListExtra("remainingTopicsList");
        }
        progressBar.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);

        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = convertStreamToString(fileInputStream);
            TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
//            Retrofit retro = BaseApplication.getInstance().getRetrofit();
//            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
//            Call<TopicsResponse> call = topicsAPI.getTopicsCategory("" + SharedPrefUtils.getUserDetailModel(this).getId());
//            call.enqueue(getAllTopicsResponseCallback);

            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadCategoriesJSONCallback);
        }


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

    private void createTopicsData(TopicsResponse responseData) {
        try {
            progressBar.setVisibility(View.GONE);
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                nextButton.setVisibility(View.VISIBLE);

                topicsMap = new HashMap<Topics, List<Topics>>();
                topicList = new ArrayList<>();

                //Prepare structure for multi-expandable listview.
                for (int i = 0; i < responseData.getData().size(); i++) {
                    ArrayList<Topics> tempUpList = new ArrayList<>();


                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        ArrayList<Topics> tempList = new ArrayList<>();

                        for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {

                            if ("1".equals(responseData.getData().get(i).getChild().get(j).getChild().get(k).getPublicVisibility())) {
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

                    for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {
                        if ("1".equals(responseData.getData().get(i).getChild().get(k).getPublicVisibility())) {
                            //Adding All subcategories
                            responseData.getData().get(i).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());
                            tempUpList.add(responseData.getData().get(i).getChild().get(k));
                        }
                    }

                    if ("1".equals(responseData.getData().get(i).getPublicVisibility())) {
                        topicList.add(responseData.getData().get(i));
                        topicsMap.put(responseData.getData().get(i), tempUpList);
                    }

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

            } else {
                showToast(getString(R.string.server_error));
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            showToast(getString(R.string.went_wrong));
        }
    }

//    Callback<TopicsResponse> getAllTopicsResponseCallback = new Callback<TopicsResponse>() {
//        @Override
//        public void onResponse(Call<TopicsResponse> call, retrofit2.Response<TopicsResponse> response) {
//            progressBar.setVisibility(View.GONE);
//
//            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
//                return;
//            }
//            try {
//                TopicsResponse responseData = (TopicsResponse) response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//
//                    nextButton.setVisibility(View.VISIBLE);
//
//                    topicsMap = new HashMap<Topics, List<Topics>>();
//                    topicList = new ArrayList<>();
//
//                    //Prepare structure for multi-expandable listview.
//                    for (int i = 0; i < responseData.getData().size(); i++) {
//                        ArrayList<Topics> tempUpList = new ArrayList<>();
//
//                        for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
//                            ArrayList<Topics> tempList = new ArrayList<>();
//
//                            for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {
//
//                                //Adding All sub-subcategories
//                                responseData.getData().get(i).getChild().get(j).getChild().get(k)
//                                        .setParentId(responseData.getData().get(i).getId());
//                                responseData.getData().get(i).getChild().get(j).getChild().get(k)
//                                        .setParentName(responseData.getData().get(i).getTitle());
//                                tempList.add(responseData.getData().get(i).getChild().get(j).getChild().get(k));
//                            }
//
//                            responseData.getData().get(i).getChild().get(j).setChild(tempList);
//                        }
//
//                        for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {
//                            //Adding All subcategories
//                            responseData.getData().get(i).getChild().get(k)
//                                    .setParentId(responseData.getData().get(i).getId());
//                            responseData.getData().get(i).getChild().get(k)
//                                    .setParentName(responseData.getData().get(i).getTitle());
//                            tempUpList.add(responseData.getData().get(i).getChild().get(k));
//                        }
//
//                        topicList.add(responseData.getData().get(i));
//                        topicsMap.put(responseData.getData().get(i),
//                                tempUpList);
//                    }
//
//                    int totalSelectedItems = 0;
//                    if (null != remainingTopicsList) {
//                        totalSelectedItems = retainItemsFromReminaingList(remainingTopicsList);
//                    }
//
//                    topicsParentExpandableListAdapter =
//                            new AddTopicsParentExpandableListAdapter(
//                                    AddArticleTopicsActivity.this,
//                                    parentExpandableListView,
//                                    topicList, topicsMap
//                            );
//                    parentExpandableListView.setAdapter(topicsParentExpandableListAdapter);
//
//                    if (null != remainingTopicsList) {
//                    /*
//                    Called only when When Editing A Published Article
//                    Expand all categories whose subcategories or sub-subcategories are selected.
//                    */
//                        topicsParentExpandableListAdapter.setTotalSelectedItem(totalSelectedItems);
//                        for (int i = 0; i < topicList.size(); i++) {
//                            for (int j = 0; j < remainingTopicsList.size(); j++) {
//                                if (topicList.get(i).getId().equals(remainingTopicsList.get(j).getParentId())) {
//                                    parentExpandableListView.expandGroup(i);
//                                }
//                            }
//                        }
//                    }
//
//                } else {
//                    showToast(getString(R.string.server_error));
//                }
//            } catch (Exception e) {
//                progressBar.setVisibility(View.GONE);
//                Crashlytics.logException(e);
//                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<TopicsResponse> call, Throwable t) {
//            progressBar.setVisibility(View.GONE);
//            showToast(getString(R.string.went_wrong));
//            Crashlytics.logException(t);
//            Log.d("MC4kException", Log.getStackTraceString(t));
//        }
//    };

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
}
