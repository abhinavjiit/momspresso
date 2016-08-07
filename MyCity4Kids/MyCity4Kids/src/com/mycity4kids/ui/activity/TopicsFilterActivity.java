package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.FilterTopicsParentExpandableListAdapter;

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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class TopicsFilterActivity extends BaseActivity {

    ExpandableListView parentExpandableListView;
    private FilterTopicsParentExpandableListAdapter filterTopicsParentExpandableListAdapter;
    private ProgressBar progressBar;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.topics_filter_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose Topics");

        parentExpandableListView = (ExpandableListView) findViewById(R.id.parentExpandableListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        parentExpandableListView.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(10));

        progressBar.setVisibility(View.VISIBLE);

        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = convertStreamToString(fileInputStream);
            TopicsResponse res = new Gson().fromJson(fileContent, TopicsResponse.class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadCategoriesJSONCallback);
        }
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            progressBar.setVisibility(View.GONE);
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                HashMap<Topics, List<Topics>> topicsMap = new HashMap<Topics, List<Topics>>();
                final ArrayList<Topics> topicList = new ArrayList<>();

                ArrayList<Topics> firstList = new ArrayList<>();
                firstList.add(new Topics("recent", "Recent", false, new ArrayList<Topics>(), "all", "All"));
                firstList.add(new Topics("trending", "Trending", false, new ArrayList<Topics>(), "all", "All"));
                firstList.add(new Topics("popular", "Popular", false, new ArrayList<Topics>(), "all", "All"));

                Topics allTopics = new Topics("all", "All", false, firstList, null, null);
                Topics bestInyoutCity = new Topics("bestInyourCity", "Best of " + SharedPrefUtils.getCurrentCityModel(this).getName(), false, new ArrayList<Topics>(), null, null);

                topicList.add(allTopics);
                topicList.add(bestInyoutCity);

                topicsMap.put(allTopics, firstList);
                topicsMap.put(bestInyoutCity, new ArrayList<Topics>());
                //Prepare structure for multi-expandable listview.
                for (int i = 0; i < responseData.getData().size(); i++) {
                    ArrayList<Topics> tempUpList = new ArrayList<>();

                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        ArrayList<Topics> tempList = new ArrayList<>();

                        //add All option to select all sub-categories-childrens only if there are more then 0 child in a subcategory.
                        if (responseData.getData().get(i).getChild().get(j).getChild().size() > 0)
                            tempList.add(new Topics(responseData.getData().get(i).getChild().get(j).getId(), "All", false, new ArrayList<Topics>(), responseData.getData().get(i).getId(),
                                    responseData.getData().get(i).getTitle()));

                        tempList.addAll(responseData.getData().get(i).getChild().get(j).getChild());
                        responseData.getData().get(i).getChild().get(j).setChild(tempList);
                    }

                    //add All option to select all sub-categories only if there are more then 0 subcategories.
                    if (responseData.getData().get(i).getChild().size() > 0)
                        tempUpList.add(new Topics(responseData.getData().get(i).getId(), "All", false, new ArrayList<Topics>(), responseData.getData().get(i).getId(),
                                responseData.getData().get(i).getTitle()));

                    tempUpList.addAll(responseData.getData().get(i).getChild());
                    topicList.add(responseData.getData().get(i));
                    topicsMap.put(responseData.getData().get(i),
                            tempUpList);
                }

                Topics contributorTopic = new Topics("contributor", "Meet our contributors",
                        false, new ArrayList<Topics>(), null, null);
                topicList.add(contributorTopic);
                topicsMap.put(contributorTopic, new ArrayList<Topics>());

                filterTopicsParentExpandableListAdapter =
                        new FilterTopicsParentExpandableListAdapter(
                                TopicsFilterActivity.this,
                                parentExpandableListView,
                                topicList, topicsMap
                        );
                parentExpandableListView.setAdapter(filterTopicsParentExpandableListAdapter);
                parentExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        Log.d("Group Child", "" + topicList.get(groupPosition).getChild());
                        if (topicList.get(groupPosition).getChild().size() == 0) {
                            switch (topicList.get(groupPosition).getId()) {
                                case "bestInyourCity":
                                    Intent cityIntent = new Intent(TopicsFilterActivity.this, CityBestArticleListingActivity.class);
                                    startActivity(cityIntent);
                                    break;
                                case "contributors":
                                    Intent contributorIntent = new Intent(TopicsFilterActivity.this, CityBestArticleListingActivity.class);
                                    startActivity(contributorIntent);
                                    break;
                                default:
                                    Intent intent = new Intent(TopicsFilterActivity.this, FilteredTopicsArticleListingActivity.class);
                                    intent.putExtra("selectedTopics", topicList.get(groupPosition).getId());
                                    startActivity(intent);
                                    break;
                            }

                        }
                        return false;
                    }
                });
            } else {
                showToast(getString(R.string.server_error));
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

    public int GetDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}
