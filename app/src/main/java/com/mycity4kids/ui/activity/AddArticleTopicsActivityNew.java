package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.MomspressoButtonWidget;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import okhttp3.ResponseBody;
import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AddArticleTopicsActivityNew extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView clearAllTextView;
    private ProgressBar progressBar;

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

    private String userAgent;
    private RelativeLayout root;
    private LinearLayout filterContentContainer;
    private HashMap<String, Topics> selectedTopicsMap;
    private ArrayList<Topics> selectTopic;
    private FlowLayout subTopicsListContainer;
    private FlowLayout selectedTopicsContainer;
    private EditText searchEditText;
    private TextView selectedTagsLabel;
    private TextView maxTopicsLabel;
    private TextView nextTextView;
    private ImageView clearSearchImageView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Utils.pushOpenScreenEvent(this, "AddTagScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        setContentView(R.layout.add_article_topics_activity_new);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Topics");

        progressBar = findViewById(R.id.progressBar);
        clearAllTextView = findViewById(R.id.clearAllTextView);
        selectedTagsLabel = findViewById(R.id.selectedTagsLabel);
        maxTopicsLabel = findViewById(R.id.maxTopicsLabel);
        nextTextView = findViewById(R.id.nextTextView);
        filterContentContainer = findViewById(R.id.filterContentContainer);
        subTopicsListContainer = findViewById(R.id.subTopicsListContainer);
        selectedTopicsContainer = findViewById(R.id.selectedTopicsContainer);
        searchEditText = findViewById(R.id.searchEditText);
        clearSearchImageView = findViewById(R.id.clearSearchImageView);

        clearAllTextView.setOnClickListener(this);
        nextTextView.setOnClickListener(this);
        nextTextView.setEnabled(false);
        clearSearchImageView.setOnClickListener(this);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.isNullOrEmpty(editable.toString())) {
                    filterContentContainer.setVisibility(View.VISIBLE);
                    filterContentContainer.getChildAt(0).callOnClick();
                } else {
                    filterContentContainer.setVisibility(View.GONE);
                    filterTopicsSearchResult(editable.toString());
                }
            }
        });

        userNavigatingFrom = getIntent().getStringExtra("from");
        userAgent = getIntent().getStringExtra("userAgent");
        draftObject = (PublishDraftObject) getIntent().getSerializableExtra("draftItem");
        selectedTopicsMap = new HashMap<>();
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
    }

    private void filterTopicsSearchResult(String searchTerm) {
        selectTopic = new ArrayList<>();
        for (int i = 0; i < topicList.size(); i++) {
            selectTopic.addAll(topicList.get(i).getChild());
        }
        processTopicsDataForList();
        createTopicsToggleButton(searchTerm);
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
                retainItemsFromRemainingList(selectedTopicsIdList);
            }
            createTopicsFilterOptions();
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

    private void retainItemsFromRemainingList(ArrayList<String> list) {
        for (Entry<Topics, List<Topics>> topicsListEntry : topicsMap.entrySet()) {
            ArrayList<Topics> tempList = ((ArrayList) ((Entry) topicsListEntry).getValue());
            for (int j = 0; j < tempList.size(); j++) {

                //subcategories with no child
                if (tempList.get(j).getChild().size() == 0) {
                    tempList.get(j).setIsSelected(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).equals(tempList.get(j).getId())) {
                            tempList.get(j).setIsSelected(true);
                            modifySelectedTopicContainer("add", tempList.get(j));
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
                            modifySelectedTopicContainer("add", tempList.get(j).getChild().get(k));
                        }
                    }
                }
            }
        }
    }

    private void createTopicsFilterOptions() {
        int sides = AppUtils.dpTopx(12);
        int topBottom = AppUtils.dpTopx(8);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(topBottom, topBottom, topBottom, topBottom);
        for (int i = 0; i < topicList.size(); i++) {
            MomspressoButtonWidget momspressoButtonWidget = new MomspressoButtonWidget(this);
            momspressoButtonWidget.setText(topicList.get(i).getDisplay_name());
            momspressoButtonWidget.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color));
            momspressoButtonWidget.setBorderWidth(2);
            momspressoButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_red));
            momspressoButtonWidget.setDisableBorderColor(ContextCompat.getColor(this, R.color.app_grey));
            momspressoButtonWidget.setDisableTextColor(ContextCompat.getColor(this, R.color.app_grey));
            momspressoButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_red));
            momspressoButtonWidget.setTextSize(13);
            momspressoButtonWidget.setRadius(36);
            momspressoButtonWidget.setPadding(sides, topBottom, sides, topBottom);
            momspressoButtonWidget.setLayoutParams(params);
            momspressoButtonWidget.setTag(topicList.get(i).getId());
            filterContentContainer.addView(momspressoButtonWidget);
            int finalI = i;
            momspressoButtonWidget.setOnClickListener(view -> filterTopicTopicWise(view, topicList.get(finalI)));
        }
        filterContentContainer.getChildAt(0).callOnClick();
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

    private void filterTopicTopicWise(View view, Topics topics) {
        for (int i = 0; i < filterContentContainer.getChildCount(); i++) {
            filterContentContainer.getChildAt(i).setSelected(false);
        }
        view.setSelected(true);
        for (int i = 0; i < topicList.size(); i++) {
            if (topicList.get(i).getId().equals(topics.getId())) {
                selectTopic = topics.getChild();
                break;
            }
        }
        processTopicsDataForList();
        createTopicsToggleButton(null);
    }

    private void processTopicsDataForList() {
        for (int i = 0; i < selectTopic.size(); i++) {
            if (selectTopic.get(i).getChild().size() == 0) {
                //terminal Topic. Eligible for tagging
                Topics tempTopic = new Topics();
                tempTopic.setId(selectTopic.get(i).getId());
                tempTopic.setDisplay_name(selectTopic.get(i).getDisplay_name());
                tempTopic.setIsSelected(selectTopic.get(i).isSelected());
                tempTopic.setTitle(selectTopic.get(i).getTitle());
                tempTopic.setPublicVisibility(selectTopic.get(i).getPublicVisibility());
                tempTopic.setShowInMenu(selectTopic.get(i).getShowInMenu());
                ArrayList<Topics> tempList = new ArrayList<>();
                tempList.add(tempTopic);
                selectTopic.get(i).setChild(tempList);
            }
            for (int j = 0; j < selectTopic.get(i).getChild().size(); j++) {
                if (selectTopic.get(i).getChild().get(j).isSelected()) {
                    selectedTopicsMap
                            .put(selectTopic.get(i).getChild().get(j).getId(), selectTopic.get(i).getChild().get(j));
                }
            }

        }
    }

    private void createTopicsToggleButton(String searchTerm) {
        try {
            subTopicsListContainer.removeAllViews();
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int j = 0; j < selectTopic.size(); j++) {
                for (int i = 0; i < selectTopic.get(j).getChild().size(); i++) {
                    if (searchTerm == null || selectTopic.get(j).getChild().get(i).getDisplay_name().toLowerCase()
                            .contains(searchTerm.toLowerCase())) {
                        LinearLayout ll = (LinearLayout) layoutInflater
                                .inflate(R.layout.topic_follow_unfollow_item, null);
                        final TextView tv = ((TextView) ll.getChildAt(0));
                        tv.setText(selectTopic.get(j).getChild().get(i).getDisplay_name().toUpperCase());
                        tv.setTag(selectTopic.get(j).getChild().get(i));
                        if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
                            tv.setSelected(false);
                        } else {
                            tv.setSelected(true);
                        }
                        tv.setOnClickListener(v -> {
                            if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
                                if (selectedTopicsMap.size() == 8) {
                                    showToast(getString(R.string.add_article_topics_max_topics));
                                    return;
                                }
                                selectedTopicsMap.put(((Topics) tv.getTag()).getId(), (Topics) tv.getTag());
                                ((Topics) tv.getTag()).setIsSelected(true);
                                tv.setSelected(true);
                                modifySelectedTopicContainer("add", (Topics) tv.getTag());
                            } else {
                                selectedTopicsMap.remove(((Topics) tv.getTag()).getId());
                                ((Topics) tv.getTag()).setIsSelected(false);
                                tv.setSelected(false);
                                modifySelectedTopicContainer("remove", (Topics) tv.getTag());
                            }
                        });
                        subTopicsListContainer.addView(ll);
                    }
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
    }

    private void getSelectedTopicsFromList() {
        chosenTopicsList.clear();
        for (int i = 0; i < topicList.size(); i++) {
            for (int j = 0; j < topicList.get(i).getChild().size(); j++) {
                if (topicList.get(i).getChild().get(j).getChild().size() == 0) {
                    if (topicList.get(i).getChild().get(j).isSelected()) {
                        chosenTopicsList.add(topicList.get(i).getChild().get(j));
                    }
                }

                for (int k = 0; k < topicList.get(i).getChild().get(j).getChild().size(); k++) {
                    if (topicList.get(i).getChild().get(j).getChild().get(k).isSelected()) {
                        chosenTopicsList.add(topicList.get(i).getChild().get(j).getChild().get(k));
                    }
                }
            }
        }
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

    private void modifySelectedTopicContainer(String action, Topics topics) {
        if ("add".equals(action)) {
            int dimens = AppUtils.dpTopx(6);
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            params.setMargins(dimens, dimens, dimens, dimens);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout ll = (RelativeLayout) layoutInflater.inflate(R.layout.selected_topic_button, null);
            ll.setLayoutParams(params);
            final TextView tv = ((TextView) ll.getChildAt(0));
            final ImageView iv = ((ImageView) ll.getChildAt(1));
            tv.setText(topics.getDisplay_name().toUpperCase());
            iv.setTag(topics);
            iv.setOnClickListener(v -> {
                selectedTopicsMap.remove(((Topics) iv.getTag()).getId());
                ((Topics) iv.getTag()).setIsSelected(false);
                modifySelectedTopicContainer("remove", (Topics) iv.getTag());
                createTopicsToggleButton(null);
            });
            selectedTopicsContainer.addView(ll);
        } else if ("remove".equals(action)) {
            for (int i = 0; i < selectedTopicsContainer.getChildCount(); i++) {
                Topics taggedTopic = (Topics) ((RelativeLayout) selectedTopicsContainer.getChildAt(i)).getChildAt(1)
                        .getTag();
                if (topics.getId().equals(taggedTopic.getId())) {
                    selectedTopicsContainer.removeViewAt(i);
                }
            }
        } else if ("removeAll".equals(action)) {
            for (int i = 0; i < selectedTopicsContainer.getChildCount(); i++) {
                Topics taggedTopic = (Topics) ((RelativeLayout) selectedTopicsContainer.getChildAt(i)).getChildAt(1)
                        .getTag();
                selectedTopicsMap.remove((taggedTopic).getId());
                taggedTopic.setIsSelected(false);
            }
            selectedTopicsContainer.removeAllViews();
            createTopicsToggleButton(null);
        }
        if (selectedTopicsContainer.getChildCount() > 0) {
            nextTextView.setEnabled(true);
            clearAllTextView.setVisibility(View.VISIBLE);
            selectedTagsLabel.setVisibility(View.VISIBLE);
            maxTopicsLabel.setVisibility(View.GONE);
        } else {
            nextTextView.setEnabled(false);
            clearAllTextView.setVisibility(View.GONE);
            selectedTagsLabel.setVisibility(View.GONE);
            maxTopicsLabel.setVisibility(View.VISIBLE);
        }
    }

    private void clearTopicsSelection() {
        modifySelectedTopicContainer("removeAll", null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextTextView:
                try {
                    getSelectedTopicsFromList();
                    createTagObjectFromList();
                    Log.e("dddd", "topics = " + tags);
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
                break;
            case R.id.clearAllTextView:
                clearTopicsSelection();
                break;
            case R.id.clearSearchImageView:
                searchEditText.getText().clear();
                break;
            default:
                break;
        }
    }
}
