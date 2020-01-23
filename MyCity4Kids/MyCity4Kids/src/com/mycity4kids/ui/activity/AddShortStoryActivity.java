package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.request.ShortStoryDraftOrPublishRequest;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ShortStoryTopicsRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.widget.StartSnapHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/*
  Created by hemant on 31/5/18.
 */

public class AddShortStoryActivity extends BaseActivity implements View.OnClickListener, ShortStoryTopicsRecyclerAdapter.RecyclerViewClickListener {

    private boolean isDraftTaggedInActiveChallenge = false;
    private String draftChallengeName;
    private String publishedChallengeName;
    private String publishedChallengeId;
    private String draftChallengeId;
    private String challengeImageUrl;
    private TextView startWriting;
    File file;
    private static final int MAX_WORDS = 100;
    private ArrayList<Topics> subTopicsList = new ArrayList<>();
    private ArrayList<Map<String, String>> taggedTopicList;
    private TopicsResponse res;
    private Toolbar toolbar;
    private TextView publishTextView;
    private ArrayList<Topics> shortStoriesTopicList;
    private ArrayList<ExploreTopicsModel> ssTopicsList;
    private Topics shortStoryChallengeTopic;
    private String draftId = "";
    private ImageView challengeImage;
    private ShortStoryDraftOrPublishRequest shortStoryDraftOrPublishRequest;
    private DraftListResult draftObject;
    private ArrayList<Map<String, String>> tagsList = new ArrayList<Map<String, String>>();
    private InputFilter filter;
    private float singleContentHeight = 1100f;
    private RecyclerView recyclerView;
    private ShortStoryTopicsRecyclerAdapter adapter;
    private boolean isTopicSelected = false;
    private EditText storyTitleEditText;
    private EditText storyBodyEditText;
    private String source;
    private String articleId;
    private String tagsJson;
    private boolean isMaxLengthToastShown = false;
    private String challengeId = "";
    private String challengeName = "";
    private String creationSourceNormalOrChallenge;
    private RelativeLayout challengeHeader, chooseShortStoryTopicPopUp;
    private TextView challenegActiveText, challengeheadertext, shortstoryheadertext;
    private String currentActiveChallenge = "";
    private String currentActiveChallengeId = "";
    private View overlayLayout;
    private String ssTopicsText;
    private TextView wordCounterTextView;
    private RelativeLayout root;
    private String categoryId;
    private boolean hasQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_short_story_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);
        ((BaseApplication) getApplication()).setView(root);

        Utils.pushOpenScreenEvent(this, "AddShortStoryScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        challengeHeader = (RelativeLayout) findViewById(R.id.challenge_header_layout);
        challengeImage = (ImageView) findViewById(R.id.image_challenge);
        shortstoryheadertext = (TextView) findViewById(R.id.topicHeading);
        challenegActiveText = (TextView) findViewById(R.id.challenge_topic_text);
        challengeheadertext = (TextView) findViewById(R.id.challenge_heading);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        startWriting = (TextView) findViewById(R.id.start_writing);
        publishTextView = (TextView) toolbar.findViewById(R.id.publishTextView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        storyTitleEditText = (EditText) findViewById(R.id.storyTitleEditText);
        storyBodyEditText = (EditText) findViewById(R.id.storyBodyEditText);
        chooseShortStoryTopicPopUp = (RelativeLayout) findViewById(R.id.choose_layout);
        overlayLayout = (View) findViewById(R.id.overlayView_choose_story_challenge);
        RadioGroup chooseoptionradioButton = (RadioGroup) findViewById(R.id.reportReasonRadioGroup);
        RadioGroup.LayoutParams rprms;
        wordCounterTextView = (TextView) findViewById(R.id.wordCounterTextView);
        publishTextView.setOnClickListener(this);
        overlayLayout.setOnClickListener(this);
        startWriting.setOnClickListener(this);
        chooseoptionradioButton.setOnCheckedChangeListener((radioGroup, i) -> {
            ssTopicsText = ssTopicsList.get(i).getDisplay_name();
            categoryId = ssTopicsList.get(i).getId();
            regulatePublishButtonState();
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();

        source = getIntent().getStringExtra("from");
        ssTopicsList = new ArrayList<>();

        if ("draftList".equals(source)) {
            draftObject = (DraftListResult) getIntent().getSerializableExtra("draftItem");
            storyTitleEditText.setText(draftObject.getTitle());
            if (!StringUtils.isNullOrEmpty(draftObject.getBody())) {
                storyBodyEditText.setText(draftObject.getBody());
                countWord(storyBodyEditText.getText().toString());
            }
            draftId = draftObject.getId();
            taggedTopicList = draftObject.getTags();
            //taggedTopicList should only contains tagged Challenge, tagged category is not to be saved in drafts
            checkTagIsActive();
            for (Map<String, String> map : taggedTopicList) {
                for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                    String key = mapEntry.getKey();
                    String value = mapEntry.getValue();
                    if (key.equals(currentActiveChallengeId) && value.equals(currentActiveChallenge)) {
                        isDraftTaggedInActiveChallenge = true;//draft is Active Challenge
                        break;
                    } else {
                        isDraftTaggedInActiveChallenge = false;//not a active challenge ,simple draft
                    }
                }
                break;
            }
        } else if ("publishedList".equals(source)) {
            if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra("title"))) {
                storyTitleEditText.setText(getIntent().getStringExtra("title"));
            }
            if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra("body"))) {
                storyBodyEditText.setText(getIntent().getStringExtra("body"));
                countWord(storyBodyEditText.getText().toString());
            }
            regulatePublishButtonState();
            articleId = getIntent().getStringExtra("articleId");
            tagsJson = getIntent().getStringExtra("tag");
            getTagListFromJason(tagsJson);
            updateTagListFromJson(tagsJson);
            recyclerView.setVisibility(View.GONE);
        }

        creationSourceNormalOrChallenge = intent.getStringExtra("selectedrequest");
        if (creationSourceNormalOrChallenge == null) {
            creationSourceNormalOrChallenge = "ShortStory";
            if ("publishedList".equals(source)) {
                recyclerView.setVisibility(View.GONE);
            }
        }
        if (creationSourceNormalOrChallenge.equals("challenge") || ("draftList".equals(source) && (!taggedTopicList.isEmpty()))) {
            //Challenge Only FLOW
            if (creationSourceNormalOrChallenge.equals("challenge")) {
                //New Story Coming through Challenge Flow
                challengeImageUrl = intent.getStringExtra("Url");
                challengeId = intent.getStringExtra("challengeId");
                challengeName = intent.getStringExtra("challengeName");
                ssTopicsText = intent.getStringExtra("selectedCategory");
                categoryId = intent.getStringExtra("shortStoryCategoryId");
                if (AppConstants.VICHAAR_SAGAR_CATEGORY_ID.equals(categoryId)) {
                    hasQuote = true;
                    storyTitleEditText.setHint(getString(R.string.short_s_add_title_hint) + "(" + getString(R.string.short_s_add_title_Optional) + ")");
                    storyBodyEditText.setHint(getString(R.string.story_text_description_hint));
                }
                if (challengeImageUrl != null) {
                    try {
                        challengeImage.setVisibility(View.VISIBLE);
                        challenegActiveText.setVisibility(View.GONE);
                        Picasso.with(this).load(challengeImageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                .fit().into(challengeImage);
                    } catch (Exception e) {
                        challengeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_article));
                    }
                } else {
                    challengeImage.setVisibility(View.GONE);
                    challenegActiveText.setVisibility(View.VISIBLE);
                    challenegActiveText.setText(challengeName);
                }
            }
            if (("draftList".equals(source) && (!taggedTopicList.isEmpty()))) {
                //Draft Story contains tagged Challenges
                getCategoryTopicsList();
                for (int i = 0; i < ssTopicsList.size(); i++) {
                    AppCompatRadioButton rbn = new AppCompatRadioButton(this);
                    rbn.setId(i);
                    rbn.setTextColor(getResources().getColor(R.color.short_story_light_black_color));
                    rbn.setText(ssTopicsList.get(i).getDisplay_name());
                    rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    chooseoptionradioButton.addView(rbn, rprms);
                    rbn.setPadding(10, 0, 0, 0);
                    if (Build.VERSION.SDK_INT >= 21) {
                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{

                                        new int[]{-android.R.attr.state_enabled}, //disabled
                                        new int[]{android.R.attr.state_enabled} //enabled
                                },
                                new int[]{

                                        getResources().getColor(R.color.app_red)//// disabled
                                        , getResources().getColor(R.color.app_red) //enabled
                                }
                        );
                        rbn.setButtonTintList(colorStateList);//set the color tint list
                        // radio.invalidate(); //could not be necessary
                    }
                }
                chooseShortStoryTopicPopUp.setVisibility(View.VISIBLE);
                for (Map<String, String> map : taggedTopicList) {
                    for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                        draftChallengeId = mapEntry.getKey();
                        draftChallengeName = mapEntry.getValue();
                        break;
                    }
                    break;
                }
                getImageUrlShow(draftChallengeId, draftChallengeName);
            }
            shortstoryheadertext.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            challengeHeader.setVisibility(View.VISIBLE);
            challengeheadertext.setVisibility(View.VISIBLE);
        } else {
            if ("publishedList".equals(source)) {
                recyclerView.setVisibility(View.GONE);
                shortstoryheadertext.setVisibility(View.GONE);
                if (challengeImageUrl == null) {
                    challengeHeader.setVisibility(View.GONE);
                    challengeheadertext.setVisibility(View.GONE);
                }
            } else {
                //New story normal flow(no challenges involved) and normal drafts
                recyclerView.setVisibility(View.VISIBLE);
                shortstoryheadertext.setVisibility(View.VISIBLE);
                challengeheadertext.setVisibility(View.GONE);
                challengeImage.setVisibility(View.GONE);
            }
            challenegActiveText.setVisibility(View.GONE);
            chooseShortStoryTopicPopUp.setVisibility(View.GONE);
        }

        storyTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                regulatePublishButtonState();
            }
        });

        storyBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try {
                    int wordsLength = countWords(s.toString());// words.length;
                    if (count == 0 && wordsLength >= MAX_WORDS) {
                        setCharLimit(storyBodyEditText, storyBodyEditText.getText().length());
                        if (!isMaxLengthToastShown) {
                            showToast(getString(R.string.short_s_max_word));
                            isMaxLengthToastShown = true;
                        }
                    } else {
                        removeFilter(storyBodyEditText);
                        isMaxLengthToastShown = false;
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int wordsLength = countWords(s.toString());
                wordCounterTextView.setVisibility(View.VISIBLE);
                if (wordsLength <= MAX_WORDS) {
                    wordCounterTextView.setText("" + (100 - wordsLength));
                    wordCounterTextView.setBackground(getResources().getDrawable(R.drawable.short_story_word_count_bg));
                } else {
                    wordCounterTextView.setText("-" + (wordsLength - 100));
                    wordCounterTextView.setBackground(getResources().getDrawable(R.drawable.campaign_detail_red_bg));
                }
                regulatePublishButtonState();
            }
        });

        SnapHelper startSnapHelper = new StartSnapHelper();
        startSnapHelper.attachToRecyclerView(recyclerView);
        adapter = new ShortStoryTopicsRecyclerAdapter(this, this);
        final LinearLayoutManager llm1 = new LinearLayoutManager(this);
        llm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm1);

        if (("draftList".equals(source) && (!taggedTopicList.isEmpty()))) {
        } else {
            try {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                createTopicsData(res);
                recyclerView.setAdapter(adapter);
                adapter.setListData(ssTopicsList);
            } catch (FileNotFoundException e) {
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                        try {
                            FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                            createTopicsData(res);
                            recyclerView.setAdapter(adapter);
                            adapter.setListData(ssTopicsList);
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
        }
    }

    private void getCategoryTopicsList() {
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                    try {
                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
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
    }

    private void countWord(String text) {
        int wordsLength = countWords(text);
        wordCounterTextView.setVisibility(View.VISIBLE);
        if (wordsLength <= MAX_WORDS) {
            wordCounterTextView.setText("" + (100 - wordsLength));
            wordCounterTextView.setBackground(getResources().getDrawable(R.drawable.short_story_word_count_bg));
        } else {
            wordCounterTextView.setText("-" + (wordsLength - 100));
            wordCounterTextView.setBackground(getResources().getDrawable(R.drawable.campaign_detail_red_bg));
        }
    }

    private boolean checkIfStoryIsValid() {
        if (StringUtils.isNullOrEmpty(categoryId)) {
            return false;
        }
        if (AppConstants.VICHAAR_SAGAR_CATEGORY_ID.equals(categoryId)) {
            return countWords(storyBodyEditText.getText().toString()) >= 5;
        }
        return countWords(storyBodyEditText.getText().toString()) >= 10 && storyTitleEditText.getText().toString().trim().length() != 0;
    }

    private void getImageUrlShow(String key, String value) {
        if (key != null && value != null) {
            for (int i = 0; i < subTopicsList.size() - 1; i++) {
                if (AppConstants.SHORT_STORY_CHALLENGE_ID.equals(subTopicsList.get(i).getId())) {
                    shortStoryChallengeTopic = subTopicsList.get(i);
                    break;
                }
            }
            for (int j = shortStoryChallengeTopic.getChild().size() - 1; j >= 0; j--) {
                if (key.equals(shortStoryChallengeTopic.getChild().get(j).getId())) {
                    if (value.equals(shortStoryChallengeTopic.getChild().get(j).getDisplay_name())) {
                        if (shortStoryChallengeTopic.getChild().get(j).getExtraData() != null && shortStoryChallengeTopic.getChild().get(j).getExtraData().size() != 0) {
                            challengeImageUrl = shortStoryChallengeTopic.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl();
                        }
                        try {
                            challengeheadertext.setVisibility(View.VISIBLE);
                            challengeHeader.setVisibility(View.VISIBLE);
                            challengeImage.setVisibility(View.VISIBLE);
                            challenegActiveText.setVisibility(View.GONE);
                            Picasso.with(this).load(challengeImageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                    .fit().into(challengeImage);
                        } catch (Exception e) {
                            challengeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_article));
                        }
                        break;
                    }
                    break;
                }
            }
        }
    }

    private void checkTagIsActive() {
        try {
            shortStoriesTopicList = BaseApplication.getShortStoryTopicList();
            if (shortStoriesTopicList == null) {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                res = gson.fromJson(fileContent, TopicsResponse.class);
                shortStoriesTopicList = new ArrayList<Topics>();
                for (int i = 0; i < res.getData().size(); i++) {
                    if (AppConstants.SHORT_STORY_CATEGORYID.equals(res.getData().get(i).getId())) {
                        shortStoriesTopicList.add(res.getData().get(i));
                    }
                }
            }
            for (int i = 0; i < shortStoriesTopicList.size(); i++) {
                if (AppConstants.SHORT_STORY_CATEGORYID.equals(shortStoriesTopicList.get(i).getId())) {
                    subTopicsList.addAll(shortStoriesTopicList.get(i).getChild());
                }
            }
            for (int i = 0; i < subTopicsList.size() - 1; i++) {
                if (AppConstants.SHORT_STORY_CHALLENGE_ID.equals(subTopicsList.get(i).getId())) {
                    shortStoryChallengeTopic = subTopicsList.get(i);
                    break;
                }
            }
            for (int j = shortStoryChallengeTopic.getChild().size() - 1; j >= 0; j--) {
                if (shortStoryChallengeTopic != null && shortStoryChallengeTopic.getChild() != null &&
                        shortStoryChallengeTopic.getChild().get(j) != null
                        && "1".equals(shortStoryChallengeTopic.getChild().get(j).getPublicVisibility())) {
                    if (shortStoryChallengeTopic.getChild().get(j).getExtraData() != null &&
                            shortStoryChallengeTopic.getChild().get(j).getExtraData().size() != 0 &&
                            shortStoryChallengeTopic.getChild().get(j).getExtraData().get(0).getChallenge() != null &&
                            shortStoryChallengeTopic.getChild().get(j).getExtraData().get(0).getChallenge().getActive() != null) {
                        if ("1".equals(shortStoryChallengeTopic.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                            currentActiveChallengeId = shortStoryChallengeTopic.getChild().get(j).getId();
                            currentActiveChallenge = shortStoryChallengeTopic.getChild().get(j).getDisplay_name();
                            break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                    Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);

                    try {
                        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        res = gson.fromJson(fileContent, TopicsResponse.class);
                        shortStoriesTopicList = new ArrayList<Topics>();
                        for (int i = 0; i < res.getData().size(); i++) {
                            if (AppConstants.SHORT_STORY_CATEGORYID.equals(res.getData().get(i).getId())) {
                                shortStoriesTopicList.add(res.getData().get(i));
                            }
                        }
                        for (int i = 0; i < shortStoriesTopicList.size(); i++) {
                            if (AppConstants.SHORT_STORY_CATEGORYID.equals(shortStoriesTopicList.get(i).getId())) {
                                subTopicsList.addAll(shortStoriesTopicList.get(i).getChild());
                            }
                        }
                        for (int i = 0; i < subTopicsList.size() - 1; i++) {
                            if (AppConstants.SHORT_STORY_CHALLENGE_ID.equals(subTopicsList.get(i).getId())) {
                                shortStoryChallengeTopic = subTopicsList.get(i);
                                break;
                            }
                        }
                        for (int j = shortStoryChallengeTopic.getChild().size() - 1; j >= 0; j--) {
                            if ("1".equals(shortStoryChallengeTopic.getChild().get(j).getPublicVisibility())) {
                                if (shortStoryChallengeTopic.getChild().get(j).getExtraData() != null && shortStoryChallengeTopic.getChild().get(j).getExtraData().size() != 0) {
                                    if ("1".equals(shortStoryChallengeTopic.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                                        currentActiveChallengeId = shortStoryChallengeTopic.getChild().get(j).getId();
                                        currentActiveChallenge = shortStoryChallengeTopic.getChild().get(j).getDisplay_name();
                                        break;
                                    }
                                }
                            }
                        }
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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[]{filter});
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

    private void getTagListFromJason(String tagsJson) {
        int count = 0;
        ArrayList<Map<String, String>> tagsList1 = new ArrayList<>();
        ArrayList<Map<String, String>> tagsList2 = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(tagsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (count == 0) {
                    HashMap<String, String> map = new HashMap<>();
                    publishedChallengeId = jsonArray.getJSONObject(i).keys().next().toString();
                    publishedChallengeName = jsonArray.getJSONObject(i).getString(publishedChallengeId);
                    map.put(publishedChallengeId, publishedChallengeName);
                    if (!"ignore".equals(publishedChallengeId)) {
                        tagsList1.add(map);
                        count++;
                    }
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    publishedChallengeId = jsonArray.getJSONObject(i).keys().next().toString();
                    publishedChallengeName = jsonArray.getJSONObject(i).getString(publishedChallengeId);
                    map.put(publishedChallengeId, publishedChallengeName);
                    if (!"ignore".equals(publishedChallengeId)) {
                        tagsList2.add(map);
                    }
                }
            }
            tagsList.addAll(tagsList1);
            tagsList.addAll(tagsList2);
            for (Map<String, String> listItem : tagsList) {
                for (Map.Entry<String, String> stringStringEntry : listItem.entrySet()) {
                    if (((Map.Entry) stringStringEntry).getKey().equals(AppConstants.VICHAAR_SAGAR_CATEGORY_ID)) {
                        hasQuote = true;
                        categoryId = AppConstants.VICHAAR_SAGAR_CATEGORY_ID;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTagListFromJson(String tagsJson) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(tagsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                publishedChallengeId = jsonArray.getJSONObject(i).keys().next().toString();
                publishedChallengeName = jsonArray.getJSONObject(i).getString(publishedChallengeId);
                map.put(publishedChallengeId, publishedChallengeName);
                checkTagIsActive();
                getImageUrlShow(publishedChallengeId, publishedChallengeName);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTopicsData(ExploreTopicsResponse responseData) {
        try {
            for (int i = 0; i < responseData.getData().size(); i++) {
                if (AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        //DO NOT REMOVE below commented check -- showInMenu 0 from backend --might be used to show/hide in future
                        if ("1".equals(responseData.getData().get(i).getChild().get(j).getPublicVisibility())) {
                            ssTopicsList.add(responseData.getData().get(i).getChild().get(j));
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publishTextView:
                if (isValid()) {
                    Intent intent = new Intent(this, ShortStoriesCardActivity.class);
                    intent.putExtra("ssTopicsText", ssTopicsText);
                    intent.putExtra("challengeName", challengeName);
                    intent.putExtra("challengeId", challengeId);
                    intent.putExtra("runningrequest", creationSourceNormalOrChallenge);
                    intent.putExtra("draftId", draftId);
                    intent.putExtra("articleId", articleId);
                    intent.putExtra("source", source);
                    intent.putExtra("isDraftTaggedInActiveChallenge", isDraftTaggedInActiveChallenge);
                    intent.putExtra("listDraft", taggedTopicList);
                    intent.putParcelableArrayListExtra("ssTopicsList", ssTopicsList);
                    intent.putExtra("currentActiveChallengeId", currentActiveChallengeId);
                    intent.putExtra("currentActiveChallenge", currentActiveChallenge);
                    intent.putExtra("tagsList", tagsList);
                    intent.putExtra("title", storyTitleEditText.getText().toString().trim());
                    intent.putExtra("story", storyBodyEditText.getText().toString().trim());
                    intent.putExtra("categoryId", categoryId);
                    startActivity(intent);
                }
                break;
            case R.id.start_writing:
                if (ssTopicsText != null) {
                    chooseShortStoryTopicPopUp.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(this, R.string.select_atleast_one_topic, Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onClick(View view, int position) {
        for (int i = 0; i < ssTopicsList.size(); i++) {
            ssTopicsList.get(i).setIsSelected(false);
        }
        ssTopicsList.get(position).setIsSelected(true);
        categoryId = ssTopicsList.get(position).getId();

        if (ssTopicsList.get(position).getId().equalsIgnoreCase(AppConstants.VICHAAR_SAGAR_CATEGORY_ID)) {
            hasQuote = true;
            storyTitleEditText.setHint(getString(R.string.short_s_add_title_hint) + "(" + getString(R.string.short_s_add_title_Optional) + ")");
            storyBodyEditText.setHint(getString(R.string.story_text_description_hint));
        } else {
            hasQuote = false;
            storyTitleEditText.setHint(R.string.short_s_add_title_hint);
            storyBodyEditText.setHint(R.string.short_s_add_body_hint);
        }
        regulatePublishButtonState();
        adapter.notifyDataSetChanged();
    }

    private void regulatePublishButtonState() {
        if (checkIfStoryIsValid()) {
            publishTextView.setEnabled(true);
            publishTextView.setTextColor(getResources().getColor(R.color.app_red));
        } else {
            publishTextView.setEnabled(false);
            publishTextView.setTextColor(getResources().getColor(R.color.color_979797));
        }
    }

    private boolean isValid() {
        if (!"publishedList".equals(source)) {
            for (int i = 0; i < ssTopicsList.size(); i++) {
                if (ssTopicsList.get(i).isSelected()) {
                    isTopicSelected = true;
                }
            }
            if (ssTopicsText != null) {
                for (int i = 0; i < ssTopicsList.size(); i++) {
                    if (ssTopicsList.get(i).getDisplay_name().equals(ssTopicsText)) {
                        ssTopicsList.get(i).setIsSelected(true);
                        isTopicSelected = true;
                    }
                }
            }
            if (!isTopicSelected) {
                Toast.makeText(this, R.string.select_atleast_one_topic, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (!hasQuote && (storyTitleEditText.getText() == null || StringUtils.isNullOrEmpty(storyTitleEditText.getText().toString()))) {
            Toast.makeText(this, getString(R.string.editor_title_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (storyBodyEditText.getText() == null || StringUtils.isNullOrEmpty(storyBodyEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.editor_body_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (countWords(storyBodyEditText.getText().toString()) > MAX_WORDS) {
            Toast.makeText(this, getString(R.string.short_s_max_word), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (((storyTitleEditText.getText() == null || StringUtils.isNullOrEmpty(storyTitleEditText.getText().toString()))
                && ((storyBodyEditText.getText() == null) || StringUtils.isNullOrEmpty(storyBodyEditText.getText().toString())))
                || "publishedList".equals(getIntent().getStringExtra("from"))) {
            super.onBackPressed();
            finish();
        } else {
            if (storyTitleEditText.getText().toString().trim().isEmpty()) {
                saveDraftRequest(storyTitleEditText.getText().toString(), storyBodyEditText.getText().toString(), draftId);
            } else {
                saveDraftRequest(storyTitleEditText.getText().toString().trim(), storyBodyEditText.getText().toString(), draftId);
            }
        }
    }

    public void saveDraftRequest(String title, String body, String draftId1) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ShortStoryAPI shortStoryAPI = retrofit.create(ShortStoryAPI.class);
        if (StringUtils.isNullOrEmpty(body)) {
            //dynamoDB can't handle empty spaces
            body = " ";
        }
        shortStoryDraftOrPublishRequest = new ShortStoryDraftOrPublishRequest();
        shortStoryDraftOrPublishRequest.setTitle(title);
        shortStoryDraftOrPublishRequest.setBody(body);
        shortStoryDraftOrPublishRequest.setType("0");
        shortStoryDraftOrPublishRequest.setUserAgent("android");
        if (AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("0");
        } else if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("1");
        } else if (AppConstants.LOCALE_MARATHI.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("2");
        } else if (AppConstants.LOCALE_BENGALI.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("3");
        } else if (AppConstants.LOCALE_TAMIL.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("4");
        } else if (AppConstants.LOCALE_TELUGU.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("5");
        } else if (AppConstants.LOCALE_KANNADA.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("6");
        } else if (AppConstants.LOCALE_MALAYALAM.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("7");
        } else if (AppConstants.LOCALE_GUJARATI.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("8");
        } else if (AppConstants.LOCALE_PUNJABI.equals(SharedPrefUtils.getAppLocale(this))) {
            shortStoryDraftOrPublishRequest.setLang("9");
        } else {
            shortStoryDraftOrPublishRequest.setLang("0");
        }
        if (creationSourceNormalOrChallenge.equals("challenge")) {
            ArrayList<Map<String, String>> list2 = new ArrayList<>();
            Map<String, String> map1 = new HashMap<>();
            map1.put(challengeId, challengeName);
            list2.add(map1);
            shortStoryDraftOrPublishRequest.setTags(list2);
        } else if ("draftList".equals(source)) {
            if (!isDraftTaggedInActiveChallenge) {
                ArrayList<Map<String, String>> list2 = new ArrayList<>();
                if ((!taggedTopicList.isEmpty())) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put(draftChallengeId, draftChallengeName);
                    list2.add(map1);
                }
                shortStoryDraftOrPublishRequest.setTags(list2);
            } else {
                ArrayList<Map<String, String>> list2 = new ArrayList<>();
                Map<String, String> map1 = new HashMap<>();
                map1.put(currentActiveChallengeId, currentActiveChallenge);
                list2.add(map1);
                shortStoryDraftOrPublishRequest.setTags(list2);
            }
        }
        if (draftId1.isEmpty()) {
            Call<ArticleDraftResponse> call = shortStoryAPI.saveOrPublishShortStory(shortStoryDraftOrPublishRequest);
            call.enqueue(saveDraftResponseListener);
        } else {
            Call<ArticleDraftResponse> call = shortStoryAPI.updateOrPublishShortStory(draftId1, shortStoryDraftOrPublishRequest);
            call.enqueue(saveDraftResponseListener);
        }
    }

    private Callback<ArticleDraftResponse> saveDraftResponseListener = new Callback<ArticleDraftResponse>() {
        @Override
        public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
            if (response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                showAlertDialog(getString(R.string.draft_oops), getString(R.string.draft_not_saved), new OnButtonClicked() {
                    @Override
                    public void onButtonCLick(int buttonId) {
                        finish();
                    }
                });
                return;
            }
            try {
                ArticleDraftResponse responseModel = response.body();
                if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                    draftId = responseModel.getData().get(0).getResult().getId() + "";
                    removeProgressDialog();
                    showToast(getString(R.string.draft_save_success));
                    finish();
                } else {
                    removeProgressDialog();
                    if (StringUtils.isNullOrEmpty(responseModel.getReason())) {
                        showToast(getString(R.string.toast_response_error));
                    } else {
                        showToast(responseModel.getReason());
                    }
                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeProgressDialog();
    }
}