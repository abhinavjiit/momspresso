package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
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
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.StringUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AddArticleTopicsActivityNew extends BaseActivity {

    private Toolbar toolbar;
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
    private String imageUrl;
    private String articleId;
    private String tags;
    private ArrayList<Topics> publishedArticleChallengeTag = new ArrayList<>();
    private String cities;

    private AddArticleTopicsPagerAdapter adapter;
    private String userAgent;
    private RelativeLayout root;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Utils.pushOpenScreenEvent(this, "AddTagScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        setContentView(R.layout.add_article_topics_activity_new);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topics");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        applyTextView = (TextView) findViewById(R.id.applyTextView);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        clearAllTextView = (TextView) findViewById(R.id.clearAllTextView);

        userNavigatingFrom = getIntent().getStringExtra("from");
        userAgent = getIntent().getStringExtra("userAgent");
        draftObject = (PublishDraftObject) getIntent().getSerializableExtra("draftItem");

        if ("publishedList".equals(userNavigatingFrom)) {
            imageUrl = getIntent().getStringExtra("imageUrl");
            articleId = getIntent().getStringExtra("articleId");
            tags = getIntent().getStringExtra("tag");
            cities = getIntent().getStringExtra("cities");
            if (null != tags && !tags.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(tags);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Iterator<?> keys = jsonArray.getJSONObject(i).keys();
                        selectedTopicsIdList.add((String) keys.next());
                    }
                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("JSONException", Log.getStackTraceString(e));
                }
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsApi = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsApi.downloadTopicsJSON();

            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(AddArticleTopicsActivityNew.this,
                            AppConstants.CATEGORIES_JSON_FILE, response.body());

                    try {
                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        TopicsResponse res = gson.fromJson(fileContent, TopicsResponse.class);
                        createTopicsData(res);
                    } catch (FileNotFoundException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4KException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    apiExceptions(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        }

        applyTextView.setOnClickListener(v -> {
            try {
                getSelectedTopicsFromList();
                createTagObjectFromList();

                if (chosenTopicsList.size() == 0) {
                    showToast(getString(R.string.add_article_topics_min_topics));
                    return;
                } else if (chosenTopicsList.size() > 8) {
                    showToast(getString(R.string.add_article_topics_max_topics));
                    return;
                }
                Intent intent = new Intent(AddArticleTopicsActivityNew.this, ArticleImageTagUploadActivity.class);
                intent.putExtra("draftItem", draftObject);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("from", userNavigatingFrom);
                intent.putExtra("articleId", articleId);
                intent.putExtra("tag", tags);
                intent.putExtra("cities", cities);
                if (AppConstants.ANDROID_NEW_EDITOR.equals(userAgent)) {
                    intent.putExtra("userAgent", AppConstants.ANDROID_NEW_EDITOR);
                }
                startActivity(intent);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        });

        clearAllTextView.setOnClickListener(v -> {
            try {
                clearTopicsSelection();
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
    }

    /*
     * Create tags json from list of selected tags Arraylist
     * Sent as tag to server as post param.
     * */
    private void createTagObjectFromList() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < chosenTopicsList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("" + chosenTopicsList.get(i).getId(), chosenTopicsList.get(i).getTitle());
                jsonArray.put(jsonObject);
            }
            if ("publishedList".equals(userNavigatingFrom)) {
                reAddChallengeTagForPublishedArticle(jsonArray);
            }
            if (!"publishedList".equals(userNavigatingFrom) && getIntent().hasExtra("tag")) {
                Log.e("DRAFT", "---------- draft tag == " + getIntent().getStringExtra("tag"));
                if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra("tag"))) {
                    JSONArray jsonArray1 = new JSONArray(getIntent().getStringExtra("tag"));
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        jsonArray.put(jsonArray1.getJSONObject(i));
                    }
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        tags = jsonArray.toString();
        Log.e("DRAFT", "---------- Final tag == " + tags);
    }

    private void reAddChallengeTagForPublishedArticle(JSONArray jsonArray) {
        try {
            for (int i = 0; i < publishedArticleChallengeTag.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("" + publishedArticleChallengeTag.get(i).getId(),
                        publishedArticleChallengeTag.get(i).getDisplay_name());
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
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
            topicsMap = new HashMap<>();
            topicList = new ArrayList<>();

            for (int i = 0; i < responseData.getData().size(); i++) {
                ArrayList<Topics> secondLevelLeafNodeList = new ArrayList<>();
                for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                    ArrayList<Topics> thirdLevelLeafNodeList = new ArrayList<>();
                    for (int k = 0; k < responseData.getData().get(i).getChild().get(j).getChild().size(); k++) {
                        if ("1".equals(responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                .getPublicVisibility())) {
                            //Adding All sub-subcategories
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentId(responseData.getData().get(i).getId());
                            responseData.getData().get(i).getChild().get(j).getChild().get(k)
                                    .setParentName(responseData.getData().get(i).getTitle());
                            thirdLevelLeafNodeList
                                    .add(responseData.getData().get(i).getChild().get(j).getChild().get(k));
                        }
                    }
                    responseData.getData().get(i).getChild().get(j).setChild(thirdLevelLeafNodeList);
                }

                for (int k = 0; k < responseData.getData().get(i).getChild().size(); k++) {
                    if ("1".equals(responseData.getData().get(i).getChild().get(k).getPublicVisibility())) {
                        //Adding All subcategories
                        responseData.getData().get(i).getChild().get(k)
                                .setParentId(responseData.getData().get(i).getId());
                        responseData.getData().get(i).getChild().get(k)
                                .setParentName(responseData.getData().get(i).getTitle());
                        secondLevelLeafNodeList.add(responseData.getData().get(i).getChild().get(k));
                    }
                }

                if ("1".equals(responseData.getData().get(i).getPublicVisibility())
                        && !AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())
                        && !AppConstants.ARTICLE_CHALLENGE_CATEGORY_ID.equals(responseData.getData().get(i).getId())) {
                    responseData.getData().get(i).setChild(secondLevelLeafNodeList);
                    if (!secondLevelLeafNodeList.isEmpty()) {
                        topicList.add(responseData.getData().get(i));
                        topicsMap.put(responseData.getData().get(i), secondLevelLeafNodeList);
                    }
                }
            }

            if (null != selectedTopicsIdList && !selectedTopicsIdList.isEmpty()) {
                retainItemsFromReminaingList(selectedTopicsIdList);
            }
            createTopicsTabPages();
            if ("publishedList".equals(userNavigatingFrom)) {
                extractChallengeTagFromTagLists(responseData);
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            showToast(getString(R.string.went_wrong));
        }
    }

    private void extractChallengeTagFromTagLists(TopicsResponse responseData) {
        try {
            for (int i = 0; i < responseData.getData().size(); i++) {
                if (AppConstants.ARTICLE_CHALLENGE_CATEGORY_ID.equals(responseData.getData().get(i).getId())) {
                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        for (int k = 0; k < selectedTopicsIdList.size(); k++) {
                            if (selectedTopicsIdList.get(k)
                                    .equals(responseData.getData().get(i).getChild().get(j).getId())) {
                                publishedArticleChallengeTag.add(responseData.getData().get(i).getChild().get(j));
                                break;
                            }
                        }
                        if (publishedArticleChallengeTag.size() > 0) {
                            break;
                        }
                    }
                    if (publishedArticleChallengeTag.size() > 0) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void createTopicsTabPages() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        for (int i = 0; i < topicList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(topicList.get(i).getDisplay_name()));
        }
        AppUtils.changeTabsFont(tabLayout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(topicList.size());
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
            default:
                break;
        }
        return true;
    }

    private int retainItemsFromReminaingList(ArrayList<String> list) {
        int totalSelectedItems = 0;
        Iterator it = topicsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Topics> tempList = ((ArrayList) pair.getValue());

            for (int j = 0; j < tempList.size(); j++) {

                //subcategories with no child
                if (tempList.get(j).getChild().size() == 0) {
                    System.out.println(tempList.get(j).getTitle() + " = ");
                    tempList.get(j).setIsSelected(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).equals(tempList.get(j).getId())) {
                            tempList.get(j).setIsSelected(true);
                            totalSelectedItems++;
                        }
                    }
                    continue;
                }

                //subcategories children
                for (int k = 0; k < tempList.get(j).getChild().size(); k++) {
                    System.out.println(tempList.get(j).getChild().get(k).getTitle() + " = ");
                    tempList.get(j).getChild().get(k).setIsSelected(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).equals(tempList.get(j).getChild().get(k).getId())) {
                            tempList.get(j).getChild().get(k).setIsSelected(true);
                            totalSelectedItems++;
                        }
                    }
                }
            }
        }
        return totalSelectedItems;
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
