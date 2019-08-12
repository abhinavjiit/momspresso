package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
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

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.request.ShortStoryDraftOrPublishRequest;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ShortStoryTopicsRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.widget.StartSnapHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/*
  Created by hemant on 31/5/18.
 */

public class AddShortStoryActivity extends BaseActivity implements View.OnClickListener, ShortStoryTopicsRecyclerAdapter.RecyclerViewClickListener {

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean flag = false;
    private String draftChallengeName;
    private String publishedChallengeName;
    private String publishedChallengeId;
    private String draftChallengeId;
    private String ImageUrl;
    private TextView startWriting;
    Uri imageUriTemp;
    private String path;
    File file;
    MediaType MEDIA_TYPE_PNG;
    RequestBody requestBodyFile;
    RequestBody imageType;
    private static final int MAX_WORDS = 100;
    private ArrayList<Topics> subTopicsList = new ArrayList<>();
    private ArrayList<Map<String, String>> listDraft;
    private TopicsResponse res;
    private Toolbar toolbar;
    private TextView publishTextView;
    private ArrayList<Topics> shortStoriesTopicList;
    private String dynamoUserId;
    private ArrayList<ExploreTopicsModel> ssTopicsList;
    private Topics selectedTopic;
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
    private View mLayout;
    private String challengeId = "";
    private String challengeName = "";
    private String runningrequest;
    private RelativeLayout challengeHeader, topicheaderlayout, chooseLayout;
    private TextView challenegActiveText, challengeheadertext, shortstoryheadertext;
    private String currentActiveChallenge = "";
    private String currentActiveChallengeId = "";
    private View overlayLayout;
    private String ssTopicsText;
    private TextView topicHeading;
    private TextView wordCounterTextView;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_short_story_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);
        ((BaseApplication) getApplication()).setView(root);

        Utils.pushOpenScreenEvent(this, "AddShortStoryScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        mLayout = findViewById(R.id.rootLayout);
        challengeHeader = (RelativeLayout) findViewById(R.id.challenge_header_layout);
        challengeImage = (ImageView) findViewById(R.id.image_challenge);
        shortstoryheadertext = (TextView) findViewById(R.id.topicHeading);
        challenegActiveText = (TextView) findViewById(R.id.challenge_topic_text);
        topicheaderlayout = (RelativeLayout) findViewById(R.id.topicHeadinglayout);
        challengeheadertext = (TextView) findViewById(R.id.challenge_heading);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        startWriting = (TextView) findViewById(R.id.start_writing);
        publishTextView = (TextView) toolbar.findViewById(R.id.publishTextView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        storyTitleEditText = (EditText) findViewById(R.id.storyTitleEditText);
        storyBodyEditText = (EditText) findViewById(R.id.storyBodyEditText);
        chooseLayout = (RelativeLayout) findViewById(R.id.choose_layout);
        overlayLayout = (View) findViewById(R.id.overlayView_choose_story_challenge);
        RadioGroup chooseoptionradioButton = (RadioGroup) findViewById(R.id.reportReasonRadioGroup);
        RadioGroup.LayoutParams rprms;
        topicHeading = (TextView) findViewById(R.id.topicHeading);
        wordCounterTextView = (TextView) findViewById(R.id.wordCounterTextView);
        publishTextView.setOnClickListener(this);
        overlayLayout.setOnClickListener(this);
        startWriting.setOnClickListener(this);
        chooseoptionradioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                ssTopicsText = ssTopicsList.get(i).getDisplay_name();
            }
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
            storyBodyEditText.setText(draftObject.getBody());
            draftId = draftObject.getId();
            listDraft = draftObject.getTags();
            checkTagIsActive();
            for (Map<String, String> map : listDraft) {
                for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                    String key = mapEntry.getKey();
                    String value = mapEntry.getValue();
                    if (key.equals(currentActiveChallengeId) && value.equals(currentActiveChallenge)) {
                        flag = true;//draft is Active Challenge
                        break;
                    } else {
                        flag = false;//not a active challenge ,simple draft
                    }

                }
                break;
            }
        } else if ("publishedList".equals(source)) {
            storyTitleEditText.setText(getIntent().getStringExtra("title"));
            storyBodyEditText.setText(getIntent().getStringExtra("body"));
            articleId = getIntent().getStringExtra("articleId");
            tagsJson = getIntent().getStringExtra("tag");
            getTagListFromJason(tagsJson);
            updateTagListFromJson(tagsJson);
            recyclerView.setVisibility(View.GONE);
        }


        runningrequest = intent.getStringExtra("selectedrequest");
        if (runningrequest == null) {
            runningrequest = "ShortStory";
            if ("publishedList".equals(source)) {
                recyclerView.setVisibility(View.GONE);
            }
        }
        if (runningrequest.equals("challenge") || ("draftList".equals(source) && (!listDraft.isEmpty()))) {
            if (runningrequest.equals("challenge")) {
                ImageUrl = intent.getStringExtra("Url");
                challengeId = intent.getStringExtra("challengeId");
                challengeName = intent.getStringExtra("challengeName");
                ssTopicsText = intent.getStringExtra("selectedCategory");
                if (ImageUrl != null) {
                    try {
                        challengeImage.setVisibility(View.VISIBLE);
                        challenegActiveText.setVisibility(View.GONE);
                        Picasso.with(this).load(ImageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article)
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
            if (("draftList".equals(source) && (!listDraft.isEmpty()))) {
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
                chooseLayout.setVisibility(View.VISIBLE);
                for (Map<String, String> map : listDraft) {
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
                if (ImageUrl == null) {
                    challengeHeader.setVisibility(View.GONE);
                    challengeheadertext.setVisibility(View.GONE);
                }


            } else {
                recyclerView.setVisibility(View.VISIBLE);
                shortstoryheadertext.setVisibility(View.VISIBLE);
                // challengeHeader.setVisibility(View.VISIBLE);
                challengeheadertext.setVisibility(View.GONE);
                challengeImage.setVisibility(View.GONE);
            }

            challenegActiveText.setVisibility(View.GONE);
            chooseLayout.setVisibility(View.GONE);

        }
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
                wordCounterTextView.setText(wordsLength + " " + getString(R.string.app_settings_edit_profile_toast_user_bio_words));
            }
        });

        dynamoUserId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();

        SnapHelper startSnapHelper = new StartSnapHelper();
        startSnapHelper.attachToRecyclerView(recyclerView);

        adapter = new ShortStoryTopicsRecyclerAdapter(this, this);

        final LinearLayoutManager llm1 = new LinearLayoutManager(this);
        llm1.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(llm1);

        if (("draftList".equals(source) && (!listDraft.isEmpty()))) {
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
                        // recyclerView.setAdapter(adapter);
                        //   adapter.setListData(ssTopicsList);
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


    private void getImageUrlShow(String key, String value) {
        if (key != null && value != null) {
            for (int i = 0; i < subTopicsList.size() - 1; i++) {
                if ("category-743892a865774baf9c20cbcc5c01d35f".equals(subTopicsList.get(i).getId())) {
                    selectedTopic = subTopicsList.get(i);
                    break;
                }
            }            //     String h = selectedTopic.getChild().get(selectedTopic.getChild().size() - 1).getId();
            for (int j = selectedTopic.getChild().size() - 1; j >= 0; j--) {
                if (key.equals(selectedTopic.getChild().get(j).getId())) {
                    if (value.equals(selectedTopic.getChild().get(j).getDisplay_name())) {
                        ImageUrl = selectedTopic.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl();
                        try {
                            challengeheadertext.setVisibility(View.VISIBLE);
                            challengeHeader.setVisibility(View.VISIBLE);
                            challengeImage.setVisibility(View.VISIBLE);
                            challenegActiveText.setVisibility(View.GONE);
                            Picasso.with(this).load(ImageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article)
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
                if ("category-ce8bdcadbe0548a9982eec4e425a0851".equals(shortStoriesTopicList.get(i).getId())) {
                    subTopicsList.addAll(shortStoriesTopicList.get(i).getChild());
                }
            }
            for (int i = 0; i < subTopicsList.size() - 1; i++) {
                if ("category-743892a865774baf9c20cbcc5c01d35f".equals(subTopicsList.get(i).getId())) {
                    selectedTopic = subTopicsList.get(i);
                    break;
                }
            }

            for (int j = selectedTopic.getChild().size() - 1; j >= 0; j--) {
                if ("1".equals(selectedTopic.getChild().get(j).getPublicVisibility())) {
                    if (selectedTopic != null && selectedTopic.getChild() != null && selectedTopic.getChild().get(j) != null && selectedTopic.getChild().get(j).getExtraData() != null && selectedTopic.getChild().get(j).getExtraData() != null && selectedTopic.getChild().get(j).getExtraData().get(0).getChallenge() != null && selectedTopic.getChild().get(j).getExtraData().get(0).getChallenge().getActive() != null) {
                        if ("1".equals(selectedTopic.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                            currentActiveChallengeId = selectedTopic.getChild().get(j).getId();
                            currentActiveChallenge = selectedTopic.getChild().get(j).getDisplay_name();
                            break;
                        }
                    }
                }
            }

        } catch (
                FileNotFoundException e) {
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
                            if ("category-ce8bdcadbe0548a9982eec4e425a0851".equals(shortStoriesTopicList.get(i).getId())) {
                                subTopicsList.addAll(shortStoriesTopicList.get(i).getChild());
                            }
                        }
                        for (int i = 0; i < subTopicsList.size() - 1; i++) {
                            if ("category-743892a865774baf9c20cbcc5c01d35f".equals(subTopicsList.get(i).getId())) {
                                selectedTopic = subTopicsList.get(i);
                                break;
                            }
                        }
                        for (int j = selectedTopic.getChild().size() - 1; j >= 0; j--) {
                            if ("1".equals(selectedTopic.getChild().get(j).getPublicVisibility())) {
                                if ("1".equals(selectedTopic.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                                    currentActiveChallengeId = selectedTopic.getChild().get(j).getId();
                                    currentActiveChallenge = selectedTopic.getChild().get(j).getDisplay_name();
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
                    publishedChallengeId = jsonArray.getJSONObject(i).keys().next();
                    publishedChallengeName = jsonArray.getJSONObject(i).getString(publishedChallengeId);
                    map.put(publishedChallengeId, publishedChallengeName);
                    if (!"ignore".equals(publishedChallengeId)) {
                        tagsList1.add(map);
                        count++;
                    }
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    publishedChallengeId = jsonArray.getJSONObject(i).keys().next();
                    publishedChallengeName = jsonArray.getJSONObject(i).getString(publishedChallengeId);
                    map.put(publishedChallengeId, publishedChallengeName);
                    if (!"ignore".equals(publishedChallengeId)) {
                        tagsList2.add(map);

                    }

                }
            }

            tagsList.addAll(tagsList1);
            tagsList.addAll(tagsList2);
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
                publishedChallengeId = jsonArray.getJSONObject(i).keys().next();
                publishedChallengeName = jsonArray.getJSONObject(i).getString(publishedChallengeId);
                map.put(publishedChallengeId, publishedChallengeName);
                checkTagIsActive();
                getImageUrlShow(publishedChallengeId, publishedChallengeName);
            /*    if (!"ignore".equals(keyy)) {
                    tagsList.add(map);
                }*/
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
                    if ("publishedList".equals(source)) {
                        shortStoryDraftOrPublishRequest = new ShortStoryDraftOrPublishRequest();
                        shortStoryDraftOrPublishRequest.setTitle(storyTitleEditText.getText().toString().trim());
                        shortStoryDraftOrPublishRequest.setBody(storyBodyEditText.getText().toString());
                        shortStoryDraftOrPublishRequest.setUserAgent("android");
                        shortStoryDraftOrPublishRequest.setType("0");
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
                        } else {
                            shortStoryDraftOrPublishRequest.setLang("0");
                        }
                        getBlogPage();
                    } else {
                        saveDraftBeforePublishRequest(storyTitleEditText.getText().toString().trim(), storyBodyEditText.getText().toString().trim(), draftId);
                    }
                }
                break;
            case R.id.start_writing:
                if (ssTopicsText != null) {
                    chooseLayout.setVisibility(View.INVISIBLE);
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

        if (ssTopicsList.get(position).getId().equalsIgnoreCase(AppConstants.VICHAAR_SAGAR_CATEGORY_ID)) {
            storyTitleEditText.setText(getString(R.string.story_text_title_hindi));
            storyBodyEditText.setHint(getString(R.string.story_text_description_hindi));
        } else {
            storyTitleEditText.setText("");
            storyTitleEditText.setHint(R.string.short_s_add_title_hint);
            storyBodyEditText.setHint(R.string.short_s_add_body_hint);
        }
        adapter.notifyDataSetChanged();
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

        if (storyTitleEditText.getText() == null || StringUtils.isNullOrEmpty(storyTitleEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.editor_title_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (storyBodyEditText.getText() == null || StringUtils.isNullOrEmpty(storyBodyEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.editor_body_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveDraftBeforePublishRequest(String title, String body, String draftId1) {
        showProgressDialog(getResources().getString(R.string.please_wait));

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ShortStoryAPI shortStoryAPI = retrofit.create(ShortStoryAPI.class);

        shortStoryDraftOrPublishRequest = new ShortStoryDraftOrPublishRequest();
        shortStoryDraftOrPublishRequest.setTitle(title);
        shortStoryDraftOrPublishRequest.setBody(body);
        shortStoryDraftOrPublishRequest.setUserAgent("android");
        shortStoryDraftOrPublishRequest.setType("0");
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
        } else {
            shortStoryDraftOrPublishRequest.setLang("0");
        }

        if (draftId1.isEmpty()) {
            Call<ArticleDraftResponse> call = shortStoryAPI.saveOrPublishShortStory(shortStoryDraftOrPublishRequest);
            call.enqueue(saveDraftBeforePublishResponseListener);
        } else {
            Call<ArticleDraftResponse> call = shortStoryAPI.updateOrPublishShortStory(draftId1, shortStoryDraftOrPublishRequest);
            call.enqueue(saveDraftBeforePublishResponseListener);
        }

    }

    private Callback<ArticleDraftResponse> saveDraftBeforePublishResponseListener = new Callback<ArticleDraftResponse>() {
        @Override
        public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
            if (response == null || response.body() == null) {
//                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                ArticleDraftResponse responseModel = response.body();
                if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                    draftId = responseModel.getData().get(0).getResult().getId() + "";
//                    launchSpellCheckDialog();
                    getBlogPage();
                } else {
                    removeProgressDialog();
                    if (StringUtils.isNullOrEmpty(responseModel.getReason())) {
//                        showToast(getString(R.string.toast_response_error));
                    } else {
//                        showToast(responseModel.getReason());
                    }
                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
//            showToast(getString(R.string.went_wrong));
        }
    };

    private void getBlogPage() {
     /*   Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BlogPageAPI getBlogPageAPI = retrofit.create(BlogPageAPI.class);

        Call<BlogPageResponse> call = getBlogPageAPI.getUserBlogPage(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(blogPageSetUpResponseListener);*/
        BaseApplication.getInstance().destroyRetrofitInstance();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);


    }


    Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {

            Log.d("SUCCESS", "" + response);
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }


            UserDetailResponse responseData = response.body();
            if (responseData != null) {
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    if (StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getBlogTitleSlug())) {

                        if (StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getEmail())) {
                            Intent intent = new Intent(AddShortStoryActivity.this, BlogSetupActivity.class);
                            intent.putExtra("BlogTitle", responseData.getData().get(0).getResult().getBlogTitle());
                            intent.putExtra("email", responseData.getData().get(0).getResult().getEmail());
                            intent.putExtra("comingFrom", "ShortStoryAndArticle");
                            startActivity(intent);
                        } else if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getEmail())) {

                            Intent intent = new Intent(AddShortStoryActivity.this, BlogSetupActivity.class);
                            intent.putExtra("BlogTitle", responseData.getData().get(0).getResult().getBlogTitle());
                            intent.putExtra("email", responseData.getData().get(0).getResult().getEmail());
                            intent.putExtra("comingFrom", "ShortStoryAndArticle");
                            startActivity(intent);
                        }


                    } else if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getBlogTitleSlug())) {


                        if (responseData.getData().get(0).getResult().getEmail() == null || responseData.getData().get(0).getResult().getEmail().isEmpty()) {
                            Intent intent = new Intent(AddShortStoryActivity.this, BlogSetupActivity.class);
                            intent.putExtra("BlogTitle", responseData.getData().get(0).getResult().getBlogTitle());
                            intent.putExtra("email", responseData.getData().get(0).getResult().getEmail());
                            intent.putExtra("comingFrom", "ShortStoryAndArticle");
                            startActivity(intent);
                        } else if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getEmail())) {

                            /*Intent intent = new Intent(AddShortStoryActivity.this, BlogSetupActivity.class);
                            intent.putExtra("BlogTitle", responseData.getData().get(0).getResult().getBlogTitle());
                            intent.putExtra("email", responseData.getData().get(0).getResult().getEmail());
                            startActivity(intent);*/

                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ActivityCompat.checkSelfPermission(AddShortStoryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED
                                        || ActivityCompat.checkSelfPermission(AddShortStoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                                    requestStoragePermissions();
                                } else {
                                    createAndUploadShareableImage();
                                }
                            } else {
                                createAndUploadShareableImage();
                            }
                        }
                    }
                }


            } else {
                ToastUtils.showToast(AddShortStoryActivity.this, "something went wrong");
            }

        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));

        }
    };


    private Callback<BlogPageResponse> blogPageSetUpResponseListener = new Callback<BlogPageResponse>() {
        @Override
        public void onResponse(Call<BlogPageResponse> call, retrofit2.Response<BlogPageResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            BlogPageResponse responseModel = response.body();
            if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                if (responseModel.getData().getResult().getIsSetup() == 1) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ActivityCompat.checkSelfPermission(AddShortStoryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(AddShortStoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                            requestStoragePermissions();
                        } else {
                            createAndUploadShareableImage();
                        }
                    } else {
                        createAndUploadShareableImage();
                    }
                } else if (responseModel.getData().getResult().getIsSetup() == 0) {
                    Intent intent = new Intent(AddShortStoryActivity.this, BlogSetupActivity.class);
                    startActivity(intent);
                }
            }
        }

        @Override
        public void onFailure(Call<BlogPageResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

//    private void requestStoragePermissions() {
//        // BEGIN_INCLUDE(contacts_permission_request)
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                || ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//            // Provide an additional rationale to the user if the permission was not granted
//            // and the user would benefit from additional context for the use of the permission.
//            // For example, if the request has been denied previously.
//            Log.i("Permissions",
//                    "Displaying storage permission rationale to provide additional context.");
//
//            // Display a SnackBar with an explanation and a button to trigger the request.
//            Snackbar.make(mLayout, R.string.permission_storage_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            requestUngrantedPermissions();
//             ma           }
//                    })
//                    .show();
//        }
//    }test

    public void requestStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.checkSelfPermission(AddShortStoryActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("Permissions",
                    "Displaying storage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_INIT_PERMISSION);
    }

    private void createAndUploadShareableImage() {
        Bitmap finalBitmap = null;
        try {
            finalBitmap = AppUtils.drawMultilineTextToBitmap(storyTitleEditText.getText().toString(), storyBodyEditText.getText().toString(),
                    SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name());
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ImageUploadAPI imageUploadAPI = retro.create(ImageUploadAPI.class);
        path = MediaStore.Images.Media.insertImage(getContentResolver(), finalBitmap, "Title", null);
        Log.d("ShortStory", "Path = " + path);

        if (path != null) {
            imageUriTemp = Uri.parse(path);
        }
        if (imageUriTemp != null) {
            file = FileUtils.getFile(this, imageUriTemp);
        }

        MEDIA_TYPE_PNG = MediaType.parse("image/png");
        if (file != null && MEDIA_TYPE_PNG != null) {
            requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        }
        imageType = RequestBody.create(MediaType.parse("text/plain"), "4");
        if (imageType != null && requestBodyFile != null) {
            Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(imageType, requestBodyFile);
            call.enqueue(ssImageUploadCallback);
        }
    }

    private Callback<ImageUploadResponse> ssImageUploadCallback = new Callback<ImageUploadResponse>() {
        @Override
        public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
            if (response == null || response.body() == null) {
                removeProgressDialog();
                showToast(getString(R.string.went_wrong));
                return;
            }
            ImageUploadResponse responseModel = response.body();
            if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                publishArticleRequest(responseModel.getData().getResult().getUrl());
            } else {
                removeProgressDialog();
                if (StringUtils.isNullOrEmpty(responseModel.getReason())) {
                    showToast(getString(R.string.toast_response_error));
                } else {
                    showToast(responseModel.getReason());
                }
            }
        }

        @Override
        public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (((storyTitleEditText.getText() == null || StringUtils.isNullOrEmpty(storyTitleEditText.getText().toString()))
                && ((storyBodyEditText.getText() == null) || StringUtils.isNullOrEmpty(storyBodyEditText.getText().toString())))
                || "publishedList".equals(getIntent().getStringExtra("from"))) {
            super.onBackPressed();
            finish();
        } else {
            saveDraftRequest(storyTitleEditText.getText().toString().trim(), storyBodyEditText.getText().toString(), draftId);
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
        } else {
            shortStoryDraftOrPublishRequest.setLang("0");
        }
        if (runningrequest.equals("challenge")) {
            ArrayList<Map<String, String>> list2 = new ArrayList<>();
            Map map1 = new HashMap();
            map1.put(challengeId, challengeName);
            list2.add(map1);
            shortStoryDraftOrPublishRequest.setTags(list2);
        } else if ("draftList".equals(source)) {
            if (!flag) {
                ArrayList<Map<String, String>> list2 = new ArrayList<>();
                if ((!listDraft.isEmpty())) {
                    Map map1 = new HashMap();
                    map1.put(draftChallengeId, draftChallengeName);
                    list2.add(map1);
                }
                shortStoryDraftOrPublishRequest.setTags(list2);
            } else {
                ArrayList<Map<String, String>> list2 = new ArrayList<>();
                Map map1 = new HashMap();
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
            if (response == null || response.body() == null) {
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

    private void publishArticleRequest(String url) {

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ShortStoryAPI shortStoryAPI = retrofit.create(ShortStoryAPI.class);

        shortStoryDraftOrPublishRequest.setType("1");
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
        } else {
            shortStoryDraftOrPublishRequest.setLang("0");
        }
        shortStoryDraftOrPublishRequest.setUserAgent("android");
        shortStoryDraftOrPublishRequest.setStoryImage(url);
        if ("publishedList".equals(source)) {
            draftId = articleId;
            shortStoryDraftOrPublishRequest.setTags(tagsList);
        } else {
            if (ssTopicsText != null) {
                for (int i = 0; i < ssTopicsList.size(); i++) {
                    if (ssTopicsList.get(i).getDisplay_name().equals(ssTopicsText)) {
                        ssTopicsList.get(i).setIsSelected(true);
                    }
                }
            }
            for (int i = 0; i < ssTopicsList.size(); i++) {
                if (ssTopicsList.get(i).isSelected()) {
                    HashMap<String, String> map1 = new HashMap<>();
                    ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    ArrayList<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
                    ArrayList<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<>();
                    map.put(ssTopicsList.get(i).getId(), ssTopicsList.get(i).getDisplay_name());
                    list.add(map);
                    if (runningrequest.equals("challenge")) {
                        map1.put(challengeId, challengeName);
                        list1.add(map1);
                    } else if ("draftList".equals(source)) {
                        if (!flag) {
                            if ((!listDraft.isEmpty())) {
                                map1.put(draftChallengeId, draftChallengeName);
                                list1.add(map1);
                            } else {
                            }
                        } else {
                            map1.put(currentActiveChallengeId, currentActiveChallenge);
                            list1.add(map1);
                        }
                    }
                    list2.addAll(list1);
                    list2.addAll(list);
                    shortStoryDraftOrPublishRequest.setTags(list2);
                    break;
                }
            }

        }

        Call<ArticleDraftResponse> call = shortStoryAPI.updateOrPublishShortStory(draftId, shortStoryDraftOrPublishRequest);
        call.enqueue(new Callback<ArticleDraftResponse>() {
            @Override
            public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                removeProgressDialog();
                if (response == null || response.body() == null) {
                    showToast(getString(R.string.server_went_wrong));
                    return;
                }
                ArticleDraftResponse responseModel = response.body();
                if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                    Utils.pushPublishStoryEvent(AddShortStoryActivity.this, "AddShortStoryScreen", SharedPrefUtils.getUserDetailModel(AddShortStoryActivity.this).getDynamoId(), "published");
                    Intent intent = new Intent(AddShortStoryActivity.this, ArticleModerationOrShareActivity.class);
                    intent.putExtra("shareUrl", "" + responseModel.getData().get(0).getResult().getUrl());
                    intent.putExtra("source", "addStory");
                    intent.putExtra("title", shortStoryDraftOrPublishRequest.getTitle());
                    intent.putExtra("body", shortStoryDraftOrPublishRequest.getBody());
                    startActivity(intent);
                } else {
                    if (!StringUtils.isNullOrEmpty(responseModel.getReason())) {
                        showToast(responseModel.getReason());
                    } else {
                        showToast(getString(R.string.toast_response_error));
                    }
                    return;
                }
            }

            @Override
            public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
                removeProgressDialog();
                Crashlytics.logException(t);
                Log.d("MC4KException", Log.getStackTraceString(t));
                showToast(getString(R.string.went_wrong));
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_INIT_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");
            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                createAndUploadShareableImage();
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeProgressDialog();
    }
}




