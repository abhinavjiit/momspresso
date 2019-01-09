package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BlogPageAPI;
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

/**
 * Created by hemant on 31/5/18.
 */

public class AddShortStoryActivity extends BaseActivity implements View.OnClickListener, ShortStoryTopicsRecyclerAdapter.RecyclerViewClickListener {

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean flag = false;
    private String value;
    private String valuee;
    private String keyy;
    private String key;
    private String ImageUrl;
    private TextView startWriting;

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
    private String currentActiveChalleneg;
    private String currentActiveChallengeId;
    private View overlayLayout;
    private String ssTopicsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_short_story_activity);
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
        final AppCompatRadioButton option1RadioButton = (AppCompatRadioButton) findViewById(R.id.reason1RadioButton);
        final AppCompatRadioButton option2RadioButton = (AppCompatRadioButton) findViewById(R.id.reason2RadioButton);
        final AppCompatRadioButton option3RadioButton = (AppCompatRadioButton) findViewById(R.id.reason3RadioButton);
        final AppCompatRadioButton option4RadioButton = (AppCompatRadioButton) findViewById(R.id.reason4RadioButton);
        final AppCompatRadioButton option5RadioButton = (AppCompatRadioButton) findViewById(R.id.reason5RadioButton);
        publishTextView.setOnClickListener(this);
        overlayLayout.setOnClickListener(this);
        startWriting.setOnClickListener(this);


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
                    if (key.equals(currentActiveChallengeId) && value.equals(currentActiveChalleneg)) {
                        flag = true;
                        break;
                    } else {
                        flag = false;
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

            //  chooseLayout.setVisibility(View.VISIBLE);

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
                chooseLayout.setVisibility(View.VISIBLE);
                for (Map<String, String> map : listDraft) {
                    for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                        key = mapEntry.getKey();
                        value = mapEntry.getValue();
                        break;
                    }
                    break;
                }
                getImageUrlShow(key, value);
                // challenegActiveText.setVisibility(View.VISIBLE);
                // challenegActiveText.setText(value);
            }
            shortstoryheadertext.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.GONE);
//            shortstoryheadertext.setVisibility(View.VISIBLE);
            //    recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            challengeHeader.setVisibility(View.VISIBLE);
            //  challenegActiveText.setVisibility(View.VISIBLE);
            challengeheadertext.setVisibility(View.VISIBLE);
//
            chooseoptionradioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (option1RadioButton.isChecked()) {
                        ssTopicsText = option1RadioButton.getText().toString();
                    }
                    if (option2RadioButton.isChecked()) {
                        ssTopicsText = option2RadioButton.getText().toString();
                    }
                    if (option3RadioButton.isChecked()) {
                        ssTopicsText = option3RadioButton.getText().toString();
                    }
                    if (option4RadioButton.isChecked()) {
                        ssTopicsText = option4RadioButton.getText().toString();
                    }
                    if (option5RadioButton.isChecked()) {
                        ssTopicsText = option5RadioButton.getText().toString();
                    }

                }
            });
        }

   /*        OptionSelectionFragment optionSelectionFragment = new OptionSelectionFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            optionSelectionFragment.show(fm, "choose_genre_ShortStory");
            fragmentTransaction.commit();*/
        // else if (runningrequest.equals("ShortStory")) {
//            shortstoryheadertext.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.VISIBLE);
//            challenegActiveText.setVisibility(View.GONE);
//            challengeHeader.setVisibility(View.GONE);
//            challengeheadertext.setVisibility(View.GONE);
        // }
        else {

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
                    // count == 0 means a new word is going to start
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

            }
        });


        dynamoUserId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();

        SnapHelper startSnapHelper = new StartSnapHelper();
        startSnapHelper.attachToRecyclerView(recyclerView);

        adapter = new ShortStoryTopicsRecyclerAdapter(this, this);

        final LinearLayoutManager llm1 = new LinearLayoutManager(this);
        llm1.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(llm1);

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


    private void getImageUrlShow(String key, String value) {
        if (key != null && value != null) {
            selectedTopic = subTopicsList.get(subTopicsList.size() - 1);
            //     String h = selectedTopic.getChild().get(selectedTopic.getChild().size() - 1).getId();
            for (int j = selectedTopic.getChild().size() - 1; j >= 0; j--) {
                if (key.equals(selectedTopic.getChild().get(j).getId())) {
                    if (value.equals(selectedTopic.getChild().get(j).getDisplay_name())) {
                        ImageUrl = selectedTopic.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl();
                        //   if (ImageUrl != null) {
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
                        // }
                        // else {
//                            challengeImage.setVisibility(View.GONE);
//                            challenegActiveText.setVisibility(View.VISIBLE);
//                            challenegActiveText.setText(value);
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

            selectedTopic = subTopicsList.get(subTopicsList.size() - 1);
            for (int j = selectedTopic.getChild().size() - 1; j >= 0; j--) {
                if ("1".equals(selectedTopic.getChild().get(j).getPublicVisibility())) {
                    if ("1".equals(selectedTopic.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                        currentActiveChallengeId = selectedTopic.getChild().get(j).getId();
                        currentActiveChalleneg = selectedTopic.getChild().get(j).getDisplay_name();
                        break;
                    }
                }
            }

            /*    for (int i = 0; i < shortStoriesTopicList.size(); i++) {
                    if (shortStoriesTopicList.get(0).getChild().get(i).getId().equals("category-743892a865774baf9c20cbcc5c01d35f"))

                    {
                        subTopicsList.add(shortStoriesTopicList.get(0).getChild().get(i));

                    }
                    for (int j = subTopicsList.get(0).getChild().size() - 1; j >= 0; j--) {
                        if (subTopicsList.get(0).getChild().get(j).getPublicVisibility().equals("1")) {
                            if (subTopicsList.get(0).getChild().get(j).getExtraData().get(0).getChallenge().getActive().equals("1")) {
                                currentActiveChallengeId = subTopicsList.get(0).getChild().get(j).getId();
                                currentActiveChalleneg = subTopicsList.get(0).getChild().get(j).getDisplay_name();
                            }
                        }
                    }
*/
        /*    for (Map<String, String> map : listDraft) {
                for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                    String key = mapEntry.getKey();
                    String value = mapEntry.getValue();
                    if (key.equals(currentActiveChallengeId) && value.equals(currentActiveChalleneg)) {
                        flag = true;
                        break;
                    } else {
                        flag = false;
                    }

                }
                break;
            }*/


        } catch (
                FileNotFoundException e)

        {
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

                        selectedTopic = subTopicsList.get(subTopicsList.size() - 1);
                        for (int j = selectedTopic.getChild().size() - 1; j >= 0; j--) {
                            if ("1".equals(selectedTopic.getChild().get(j).getPublicVisibility())) {
                                if ("1".equals(selectedTopic.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                                    currentActiveChallengeId = selectedTopic.getChild().get(j).getId();
                                    currentActiveChalleneg = selectedTopic.getChild().get(j).getDisplay_name();
                                }
                            }
                        }


                    /*    for (Map<String, String> map : listDraft) {
                            for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                                String key = mapEntry.getKey();
                                String value = mapEntry.getValue();
                                if (key.equals(currentActiveChallengeId) && value.equals(currentActiveChalleneg)) {
                                    flag = true;
                                    break;
                                } else {
                                    flag = false;
                                }

                            }
                        }*/
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
                    keyy = jsonArray.getJSONObject(i).keys().next();
                    valuee = jsonArray.getJSONObject(i).getString(keyy);
                    map.put(keyy, valuee);
                    if (!"ignore".equals(keyy)) {
                        tagsList1.add(map);
                        count++;
                    }
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    keyy = jsonArray.getJSONObject(i).keys().next();
                    valuee = jsonArray.getJSONObject(i).getString(keyy);
                    map.put(keyy, valuee);
                    if (!"ignore".equals(keyy)) {
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
                keyy = jsonArray.getJSONObject(i).keys().next();
                valuee = jsonArray.getJSONObject(i).getString(keyy);
                map.put(keyy, valuee);
                checkTagIsActive();
                getImageUrlShow(keyy, valuee);
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
                chooseLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view, int position) {
        for (int i = 0; i < ssTopicsList.size(); i++) {
            ssTopicsList.get(i).setIsSelected(false);
        }
        ssTopicsList.get(position).setIsSelected(true);
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
                Toast.makeText(this, "Please choose atleast one topic to continue", Toast.LENGTH_SHORT).show();
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
   /*     if (runningrequest.equals("challenge")) {
            ArrayList<Map<String, String>> list2 = new ArrayList<>();
            Map map1 = new HashMap();

            map1.put(challengeId, challengeName);

            //  Map<String, String> map2 = new HashMap<>();
            // map2.put(challengeId, challengeName);
            //list.add(map2);
            list2.add(map1);

            shortStoryDraftOrPublishRequest.setTags(list2);
        } else if ("draftList".equals(source)) {
            if (!flag) {
                ArrayList<Map<String, String>> list2 = new ArrayList<>();
                if ((!listDraft.isEmpty())) {
                    Map map1 = new HashMap();
                    map1.put(key, value);
                    list2.add(map1);
                }
                shortStoryDraftOrPublishRequest.setTags(list2);
            } else {
                ArrayList<Map<String, String>> list2 = new ArrayList<>();
                Map map1 = new HashMap();
                map1.put(currentActiveChallengeId, currentActiveChalleneg);
                list2.add(map1);
                shortStoryDraftOrPublishRequest.setTags(list2);
            }
        }*/
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
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BlogPageAPI getBlogPageAPI = retrofit.create(BlogPageAPI.class);

        Call<BlogPageResponse> call = getBlogPageAPI.getUserBlogPage(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(blogPageSetUpResponseListener);
    }

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

    private void requestStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
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
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), finalBitmap, "Title", null);
        Log.d("ShortStory", "Path = " + path);
        Uri imageUriTemp = Uri.parse(path);

        File file = FileUtils.getFile(this, imageUriTemp);

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "4");
        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(imageType, requestBodyFile);
        call.enqueue(ssImageUploadCallback);
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

            //  Map<String, String> map2 = new HashMap<>();
            // map2.put(challengeId, challengeName);
            //list.add(map2);
            list2.add(map1);

            shortStoryDraftOrPublishRequest.setTags(list2);
        } else if ("draftList".equals(source)) {
            if (!flag) {
                ArrayList<Map<String, String>> list2 = new ArrayList<>();
                if ((!listDraft.isEmpty())) {
                    Map map1 = new HashMap();
                    map1.put(key, value);
                    list2.add(map1);
                }
                // ArrayList<Map<String, String>> list2 = new ArrayList<>();
                // Map map1 = new HashMap();
                //   map1.put(challengeId, challengeName);
                shortStoryDraftOrPublishRequest.setTags(list2);
            } else {
                ArrayList<Map<String, String>> list2 = new ArrayList<>();
                Map map1 = new HashMap();

                map1.put(currentActiveChallengeId, currentActiveChalleneg);

                //  Map<String, String> map2 = new HashMap<>();
                // map2.put(challengeId, challengeName);
                //list.add(map2);
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
                        // HashMap<String, String> map1 = new HashMap<>();
                        map1.put(challengeId, challengeName);
                        list1.add(map1);
                    } else if ("draftList".equals(source)) {
                        if (!flag) {
                            //ArrayList<Map<String, String>> list = new ArrayList<>();
                            if ((!listDraft.isEmpty())) {
                                // Map map1 = new HashMap();
                                map1.put(key, value);
                                list1.add(map1);
                            } else {
                            }

                        } else {
                            map1.put(currentActiveChallengeId, currentActiveChalleneg);
                            list1.add(map1);
                        }

                    }
                    //  Map<String, String> map2 = new HashMap<>();
                    // map2.put(challengeId, challengeName);
                    //list.add(map2);

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
}
