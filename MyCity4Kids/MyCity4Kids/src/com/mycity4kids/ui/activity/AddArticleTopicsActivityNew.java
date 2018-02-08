package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.editor.ArticleImageTagUploadActivity;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.AddArticleTopicsPagerAdapter;
import com.mycity4kids.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AddArticleTopicsActivityNew extends BaseActivity {

    private Toolbar mToolbar;
    private TextView clearAllTextView;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private TextView applyTextView;

    private PublishDraftObject draftObject;
    private ArrayList<Topics> topicList;
    private ArrayList<Topics> chosenTopicsList = new ArrayList<>();
    private ArrayList<String> selectedTopicsIdList = new ArrayList<>();
    private HashMap<Topics, List<Topics>> topicsMap;
    private String userNavigatingFrom;
    private String imageURL;
    private String articleId;
    private String tags, cities;

    private AddArticleTopicsPagerAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Utils.pushOpenScreenEvent(this, "AddTagScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        setContentView(R.layout.add_article_topics_activity_new);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topics");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        applyTextView = (TextView) findViewById(R.id.applyTextView);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        clearAllTextView = (TextView) findViewById(R.id.clearAllTextView);

        userNavigatingFrom = getIntent().getStringExtra("from");
        draftObject = (PublishDraftObject) getIntent().getSerializableExtra("draftItem");


        //reminaingTopicsList is available only when Editing A Published Article
        if ("publishedList".equals(userNavigatingFrom)) {
            imageURL = getIntent().getStringExtra("imageUrl");
            articleId = getIntent().getStringExtra("articleId");
            tags = getIntent().getStringExtra("tag");
            cities = getIntent().getStringExtra("cities");
            if (null == tags || tags.isEmpty()) {

            } else {
                try {
                    JSONArray jsonArray = new JSONArray(tags);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Iterator<?> keys = jsonArray.getJSONObject(i).keys();
                        selectedTopicsIdList.add((String) keys.next());
                    }

                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    Log.d("JSONException", Log.getStackTraceString(e));
                }
            }
        }
        progressBar.setVisibility(View.VISIBLE);
//        nextButton.setVisibility(View.GONE);

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

//            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
//            call.enqueue(downloadCategoriesJSONCallback);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();

            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(AddArticleTopicsActivityNew.this, AppConstants.CATEGORIES_JSON_FILE, response.body());
                    Log.d("AddArticleTopicsActivityNew", "file download was a success? " + writtenToDisk);

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
        }


        applyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectedTopicsFromList();
                createTagObjectFromList();

                if (chosenTopicsList.size() == 0) {
                    showToast(getString(R.string.add_article_topics_min_topics));
                    return;
                } else if (chosenTopicsList.size() > 8) {
                    showToast(getString(R.string.add_article_topics_max_topics));
                    return;
                }
                Intent _intent = new Intent(AddArticleTopicsActivityNew.this, ArticleImageTagUploadActivity.class);
                _intent.putExtra("draftItem", draftObject);
                _intent.putExtra("imageUrl", imageURL);
                _intent.putExtra("from", userNavigatingFrom);
                _intent.putExtra("articleId", articleId);
                _intent.putExtra("tag", tags);
                _intent.putExtra("cities", cities);
                startActivity(_intent);

            }
        });

        clearAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTopicsSelection();
            }
        });
    }

    /*
     * Create tags json from list of selected tags Arraylist
     * Sent as tag to server as post param.
     * */
    private void createTagObjectFromList() {
//        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        try {
            for (int i = 0; i < chosenTopicsList.size(); i++) {
                JSONObject jObject = new JSONObject();
                jObject.put("" + chosenTopicsList.get(i).getId(), chosenTopicsList.get(i).getTitle());
                jArray.put(jObject);
            }
        } catch (JSONException e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }
        tags = jArray.toString();
    }

    private void getSelectedTopicsFromList() {
        chosenTopicsList.clear();
        for (int i = 0; i < topicList.size(); i++) {
            for (int j = 0; j < topicList.get(i).getChild().size(); j++) {
                for (int k = 0; k < topicList.get(i).getChild().get(j).getChild().size(); k++) {
                    if (topicList.get(i).getChild().get(j).getChild().get(k).isSelected()) {
                        chosenTopicsList.add(topicList.get(i).getChild().get(j).getChild().get(k));
                    }
                }
            }
        }
    }

    private void createTopicsData(TopicsResponse responseData) {
        try {
            progressBar.setVisibility(View.GONE);
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
                    responseData.getData().get(i).setChild(tempUpList);
                    topicList.add(responseData.getData().get(i));
                    topicsMap.put(responseData.getData().get(i), tempUpList);
                }

            }

            createTopicsTabPages();
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            showToast(getString(R.string.went_wrong));
        }
    }

    private void createTopicsTabPages() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        for (int i = 0; i < topicList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(topicList.get(i).getDisplay_name()));
        }
        AppUtils.changeTabsFont(this, tabLayout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new AddArticleTopicsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), topicList);
        viewPager.setAdapter(adapter);
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
     *Update dataset for Expandable Listview remove topics removed from EditSelectedActivity from topics Map also
     * Also updates the total selected items.
     */
    private int retainItemsFromReminaingList(ArrayList<String> list) {
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
                        if (list.get(i).equals(tList.get(j).getId())) {
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
                        if (list.get(i).equals(tList.get(j).getChild().get(k).getId())) {
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

    private void clearTopicsSelection() {
        for (int i = 0; i < topicList.size(); i++) {
            for (int j = 0; j < topicList.get(i).getChild().size(); j++) {
                for (int k = 0; k < topicList.get(i).getChild().get(j).getChild().size(); k++) {
                    topicList.get(i).getChild().get(j).getChild().get(k).setIsSelected(false);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
