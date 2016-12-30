package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.interfaces.ITopicSelectionEvent;
import com.mycity4kids.models.FollowTopics;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.SearchTopicsSplashAdapter;
import com.mycity4kids.ui.adapter.TopicsSplashAdapter;

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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 9/11/16.
 */
public class TopicsSplashActivity extends BaseActivity implements ITopicSelectionEvent {

    private Toolbar mToolbar, searchToolbar, categoryToolbar;
    private ListView popularTopicsListView;
    private TextView selectLabelTextView, categoryNameTextView;
    private EditText searchEditText;
    private ImageView categoryBackButton;
    private TextView doneTextView, countTextView;
    private RelativeLayout selectedCategoriesView;
    private LinearLayout selectAll;

    private ArrayList<SelectTopic> selectTopic;
    private ArrayList<String> previouslyFollowedTopics;
    private HashMap<String, Topics> selectedTopicsMap;

    private TopicsSplashAdapter topicsSplashAdapter;
    private SearchTopicsSplashAdapter searchTopicsSplashAdapter;


    private boolean isCategoryMainPage = true;
    private boolean isAddMoreTopic = false;
    private boolean areAllItemsSelected = true;
    private int subCatPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_splash_activity);

        previouslyFollowedTopics = getIntent().getStringArrayListExtra("followedTopics");
        isAddMoreTopic = getIntent().getBooleanExtra(AppConstants.IS_ADD_MORE_TOPIC, false);

        popularTopicsListView = (ListView) findViewById(R.id.popularTopicsListView);
        countTextView = (TextView) findViewById(R.id.countTextView);
        doneTextView = (TextView) findViewById(R.id.doneTextView);
        selectedCategoriesView = (RelativeLayout) findViewById(R.id.selectedCategoriesView);
        selectLabelTextView = (TextView) findViewById(R.id.selectLabelTextView);
        selectAll = (LinearLayout) findViewById(R.id.selectAll);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        searchToolbar = (Toolbar) findViewById(R.id.searchToolbar);
        categoryToolbar = (Toolbar) findViewById(R.id.categoryToolbar);

        ImageView closeSearchImageView = (ImageView) searchToolbar.findViewById(R.id.closeSearchImageView);
        searchEditText = (EditText) searchToolbar.findViewById(R.id.searchEditText);
        categoryNameTextView = (TextView) categoryToolbar.findViewById(R.id.categoryNameTextView);
        categoryBackButton = (ImageView) categoryToolbar.findViewById(R.id.categoryBackTextView);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        selectedTopicsMap = new HashMap<>();

        if (isAddMoreTopic) {
            showProgressDialog("Getting selected topics");
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
            Call<FollowUnfollowCategoriesResponse> call = topicsCategoryAPI.getFollowedCategories(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
            call.enqueue(getFollowedTopicsResponseCallback);
        } else {
            populateTopicsList();
        }

        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == selectTopic) {
                    return;
                }
                searchTopicsSplashAdapter = new SearchTopicsSplashAdapter(TopicsSplashActivity.this, selectedTopicsMap, selectTopic);
                popularTopicsListView.setAdapter(searchTopicsSplashAdapter);
                searchTopicsSplashAdapter.notifyDataSetChanged();
                isCategoryMainPage = false;
                selectAll.setVisibility(View.GONE);
                mToolbar.setVisibility(View.INVISIBLE);
                searchToolbar.setVisibility(View.VISIBLE);
                categoryToolbar.setVisibility(View.INVISIBLE);
                selectLabelTextView.setVisibility(View.GONE);
            }
        });

        popularTopicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!isCategoryMainPage) {
                    return;
                }
                subCatPosition = position;
                ArrayList<SelectTopic> topicCompleteSubSubTopicItemList = new ArrayList<SelectTopic>();
                topicCompleteSubSubTopicItemList.add(selectTopic.get(position));
                //Need to use a new Adapter everytime because it keeps the reference of the data list and shows older list
                // and topicCompleteSubSubTopicItemList is a new list.
                searchTopicsSplashAdapter = new SearchTopicsSplashAdapter(TopicsSplashActivity.this, selectedTopicsMap, topicCompleteSubSubTopicItemList);
                popularTopicsListView.setAdapter(searchTopicsSplashAdapter);
                searchTopicsSplashAdapter.notifyDataSetChanged();
                areAllItemsSelected = true;
                for (int i = 0; i < topicCompleteSubSubTopicItemList.get(0).getChildTopics().size(); i++) {
                    if (selectedTopicsMap.get(topicCompleteSubSubTopicItemList.get(0).getChildTopics().get(i).getId()) == null) {
                        areAllItemsSelected = false;
                    }
                }
                if (areAllItemsSelected) {
                    ((LinearLayout) selectAll.getChildAt(0)).setBackgroundResource(R.drawable.search_topics_filled_bg);
                    ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setTextColor(ContextCompat.getColor(TopicsSplashActivity.this, R.color.white_color));
                    ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setText("DESELECT ALL");
                } else {
                    ((LinearLayout) selectAll.getChildAt(0)).setBackgroundResource(R.drawable.search_topics_transparent_bg);
                    ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setTextColor(ContextCompat.getColor(TopicsSplashActivity.this, R.color.splashtopics_search_topic_item_text));
                    ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setText("SELECT ALL");
                }
                getSupportActionBar().setTitle("");
                isCategoryMainPage = false;
                categoryNameTextView.setText(selectTopic.get(position).getDisplayName().toUpperCase());
                selectAll.setVisibility(View.VISIBLE);
                mToolbar.setVisibility(View.INVISIBLE);
                searchToolbar.setVisibility(View.INVISIBLE);
                categoryToolbar.setVisibility(View.VISIBLE);
                selectLabelTextView.setVisibility(View.GONE);
            }
        });

        closeSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popularTopicsListView.setAdapter(topicsSplashAdapter);
                isCategoryMainPage = true;
                mToolbar.setVisibility(View.VISIBLE);
                selectAll.setVisibility(View.GONE);
                searchToolbar.setVisibility(View.INVISIBLE);
                categoryToolbar.setVisibility(View.INVISIBLE);
                selectLabelTextView.setVisibility(View.VISIBLE);
            }
        });

        categoryBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTopicsSplashAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopicsSplashActivity.this, ConfirmFollowedTopicsActivity.class);
                ArrayList<Topics> topicsList = new ArrayList<Topics>(selectedTopicsMap.values());
                intent.putParcelableArrayListExtra("topicsList", topicsList);
                intent.putStringArrayListExtra("previouslyFollowedTopics", previouslyFollowedTopics);
                startActivity(intent);
            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<SelectTopic> topicCompleteSubSubTopicItemList = new ArrayList<SelectTopic>();
                topicCompleteSubSubTopicItemList.add(selectTopic.get(subCatPosition));
                areAllItemsSelected = true;
                for (int i = 0; i < topicCompleteSubSubTopicItemList.get(0).getChildTopics().size(); i++) {
                    if (selectedTopicsMap.get(topicCompleteSubSubTopicItemList.get(0).getChildTopics().get(i).getId()) == null) {
                        areAllItemsSelected = false;
                    }
                }
                if (areAllItemsSelected) {
                    searchTopicsSplashAdapter.deselectAllItems(popularTopicsListView.getChildAt(0));
                    ((LinearLayout) selectAll.getChildAt(0)).setBackgroundResource(R.drawable.search_topics_transparent_bg);
                    ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setTextColor(ContextCompat.getColor(TopicsSplashActivity.this, R.color.splashtopics_search_topic_item_text));
                    ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setText("SELECT ALL");
                } else {
                    searchTopicsSplashAdapter.selectAllItems(popularTopicsListView.getChildAt(0));
                    ((LinearLayout) selectAll.getChildAt(0)).setBackgroundResource(R.drawable.search_topics_filled_bg);
                    ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setTextColor(ContextCompat.getColor(TopicsSplashActivity.this, R.color.white_color));
                    ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setText("DESELECT ALL");
                }
            }
        });
    }

    private void populateTopicsList() {
        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
            String fileContent = convertStreamToString(fileInputStream);
            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));

            showProgressDialog("Please wait");
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadCategoriesJSONCallback);
        }
    }

    private Callback<FollowUnfollowCategoriesResponse> getFollowedTopicsResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast("Something went wrong from server");
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = (FollowUnfollowCategoriesResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    previouslyFollowedTopics = (ArrayList<String>) responseData.getData();
                    populateTopicsList();
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<ResponseBody> downloadCategoriesJSONCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);

                Retrofit retro = BaseApplication.getInstance().getConfigurableTimeoutRetrofit(3);
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                String popularURL = jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category").getString("popularLocation");
                Call<ResponseBody> caller = topicsAPI.downloadTopicsListForFollowUnfollow(popularURL);

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        removeProgressDialog();
                        boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                        Log.d("TopicsSplashActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = convertStreamToString(fileInputStream);
                            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
                            createTopicsData(res);
                        } catch (FileNotFoundException e) {
                            Crashlytics.logException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        removeProgressDialog();
                        Crashlytics.logException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                        showToast("Something went wrong while downloading topics");

                        Intent intent = new Intent(TopicsSplashActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            removeProgressDialog();
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
                outputStream = openFileOutput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, Context.MODE_PRIVATE);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("TopicsSplashActivity", "file download: " + fileSizeDownloaded + " of " + fileSize);
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

    private void createTopicsData(FollowTopics[] responseData) {
        try {
            selectTopic = new ArrayList<>();
            topicsSplashAdapter = new TopicsSplashAdapter(this, selectedTopicsMap, selectTopic);
            popularTopicsListView.setAdapter(topicsSplashAdapter);
            for (int i = 0; i < responseData.length; i++) {
                //Main Category Parent Details
                SelectTopic st = new SelectTopic();
                st.setId(responseData[i].getId());
                st.setDisplayName(responseData[i].getDisplay_name());
                st.setBackgroundImageUrl(responseData[i].getExtraData().getCategoryBackImage().getApp());
                ArrayList<Topics> topicLL = new ArrayList<>();
                for (int j = 0; j < responseData[i].getChild().size(); j++) {
                    for (int k = 0; k < previouslyFollowedTopics.size(); k++) {
                        if (responseData[i].getChild().get(j).getId().equals(previouslyFollowedTopics.get(k))) {
                            //highlight previously selected topics in the current data.
                            selectedTopicsMap.put(responseData[i].getChild().get(j).getId(), responseData[i].getChild().get(j));
                            responseData[i].getChild().get(j).setIsSelected(true);
                        }
                    }
                    ArrayList<Topics> tempList = new ArrayList<>();
                    topicLL.add(responseData[i].getChild().get(j));
                    responseData[i].getChild().get(j).setChild(tempList);
                }
                if (topicLL.isEmpty()) {
                    //do not add any category with empty childs
                    continue;
                }
                st.setChildTopics(topicLL);
                selectTopic.add(st);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            showToast(getString(R.string.went_wrong));
        }
    }

    @Override
    protected void updateUi(Response response) {

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

    @Override
    public void onTopicSelectionChanged(int mapSize, int action) {
        if (action == 0) {
            ((LinearLayout) selectAll.getChildAt(0)).setBackgroundResource(R.drawable.search_topics_transparent_bg);
            TextView selectAllTV= ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0));
            selectAllTV.setTextColor(ContextCompat.getColor(TopicsSplashActivity.this, R.color.splashtopics_search_topic_item_text));
            selectAllTV.setText("SELECT ALL");
        } else {
            ArrayList<SelectTopic> topicCompleteSubSubTopicItemList = new ArrayList<SelectTopic>();
            topicCompleteSubSubTopicItemList.add(selectTopic.get(subCatPosition));
            areAllItemsSelected = true;
            for (int i = 0; i < topicCompleteSubSubTopicItemList.get(0).getChildTopics().size(); i++) {
                if (selectedTopicsMap.get(topicCompleteSubSubTopicItemList.get(0).getChildTopics().get(i).getId()) == null) {
                    areAllItemsSelected = false;
                }
            }
            if (areAllItemsSelected) {
                ((LinearLayout) selectAll.getChildAt(0)).setBackgroundResource(R.drawable.search_topics_filled_bg);
                ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setTextColor(ContextCompat.getColor(TopicsSplashActivity.this, R.color.white_color));
                ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setText("DESELECT ALL");
            } else {
                ((LinearLayout) selectAll.getChildAt(0)).setBackgroundResource(R.drawable.search_topics_transparent_bg);
                ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setTextColor(ContextCompat.getColor(TopicsSplashActivity.this, R.color.splashtopics_search_topic_item_text));
                ((TextView) ((LinearLayout) selectAll.getChildAt(0)).getChildAt(0)).setText("SELECT ALL");
            }
        }
        if (mapSize >= AppConstants.MINIMUM_TOPICS_FOLLOW_REQUIREMENT) {
            selectedCategoriesView.setVisibility(View.VISIBLE);
            countTextView.setText(mapSize + " topics chosen");
        } else {
            selectedCategoriesView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isCategoryMainPage) {
            super.onBackPressed();
        } else {
            popularTopicsListView.setAdapter(topicsSplashAdapter);
            isCategoryMainPage = true;
            mToolbar.setVisibility(View.VISIBLE);
            selectAll.setVisibility(View.GONE);
            searchToolbar.setVisibility(View.INVISIBLE);
            categoryToolbar.setVisibility(View.INVISIBLE);
            selectLabelTextView.setVisibility(View.VISIBLE);
        }
    }
}
