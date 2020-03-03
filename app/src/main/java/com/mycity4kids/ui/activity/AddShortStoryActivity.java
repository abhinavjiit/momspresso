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
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
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
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.StartSnapHelper;
import com.squareup.picasso.Picasso;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/*
  Created by hemant on 31/5/18.
 */

public class AddShortStoryActivity extends BaseActivity implements View.OnClickListener,
        ShortStoryTopicsRecyclerAdapter.RecyclerViewClickListener {

    private String challengeImageUrl;
    private static final int MAX_WORDS = 100;
    private ArrayList<Map<String, String>> taggedChallenge;
    private TextView publishTextView;
    private ArrayList<Topics> shortStoriesTopicList;
    private ArrayList<ExploreTopicsModel> ssTopicsList;
    private Topics shortStoryChallengeTopic;
    private String draftId = "";
    private ImageView challengeImage;
    private ArrayList<Map<String, String>> tagsList = new ArrayList<>();
    private InputFilter filter;
    private RecyclerView recyclerView;
    private ShortStoryTopicsRecyclerAdapter adapter;
    private EditText storyTitleEditText;
    private EditText storyBodyEditText;
    private String source;
    private String articleId;
    private boolean isMaxLengthToastShown = false;
    private String creationSourceNormalOrChallenge;
    private RelativeLayout challengeHeader, chooseShortStoryTopicPopUp;
    private TextView challengeActiveText, challengeHeaderText, shortstoryheadertext;
    private TextView wordCounterTextView;
    private String taggedCategoryId;
    private String taggedCategoryName;
    private String taggedChallengeId;
    private String taggedChallengeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_short_story_activity);
        RelativeLayout root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);
        ((BaseApplication) getApplication()).setView(root);

        Utils.pushOpenScreenEvent(this, "AddShortStoryScreen",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        challengeHeader = (RelativeLayout) findViewById(R.id.challenge_header_layout);
        challengeImage = (ImageView) findViewById(R.id.image_challenge);
        shortstoryheadertext = (TextView) findViewById(R.id.topicHeading);
        challengeActiveText = (TextView) findViewById(R.id.challenge_topic_text);
        challengeHeaderText = (TextView) findViewById(R.id.challenge_heading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView startWriting = (TextView) findViewById(R.id.start_writing);
        publishTextView = (TextView) toolbar.findViewById(R.id.publishTextView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        storyTitleEditText = (EditText) findViewById(R.id.storyTitleEditText);
        storyBodyEditText = (EditText) findViewById(R.id.storyBodyEditText);
        chooseShortStoryTopicPopUp = (RelativeLayout) findViewById(R.id.choose_layout);
        View overlayLayout = (View) findViewById(R.id.overlayView_choose_story_challenge);
        RadioGroup chooseoptionradioButton = (RadioGroup) findViewById(R.id.reportReasonRadioGroup);
        wordCounterTextView = (TextView) findViewById(R.id.wordCounterTextView);
        publishTextView.setOnClickListener(this);
        overlayLayout.setOnClickListener(this);
        startWriting.setOnClickListener(this);
        chooseoptionradioButton.setOnCheckedChangeListener((radioGroup, i) -> {
            taggedCategoryName = ssTopicsList.get(i).getDisplay_name();
            taggedCategoryId = ssTopicsList.get(i).getId();
            regulatePublishButtonState();
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();

        source = intent.getStringExtra("from");
        ssTopicsList = new ArrayList<>();
        SnapHelper startSnapHelper = new StartSnapHelper();
        startSnapHelper.attachToRecyclerView(recyclerView);
        adapter = new ShortStoryTopicsRecyclerAdapter(this, this);
        final LinearLayoutManager llm1 = new LinearLayoutManager(this);
        llm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm1);
        recyclerView.setAdapter(adapter);

        processCategoryJsonFile();

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
    }

    private void processCategoryJsonFile() {
        try {
            getShortStoryCategoryListAndChallengeData();
            processInfoAccordingToSource();
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE,
                            response.body());
                    try {
                        getShortStoryCategoryListAndChallengeData();
                        processInfoAccordingToSource();
                    } catch (FileNotFoundException e) {
                        Crashlytics.logException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4KException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Crashlytics.logException(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }
    }

    private void getShortStoryCategoryListAndChallengeData() throws FileNotFoundException {
        FileInputStream fileInputStream = BaseApplication.getAppContext()
                .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
        String fileContent = AppUtils.convertStreamToString(fileInputStream);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
        TopicsResponse topicsResponse = gson.fromJson(fileContent, TopicsResponse.class);
        ExploreTopicsResponse exploreTopicsResponse = gson.fromJson(fileContent, ExploreTopicsResponse.class);
        createTopicsData(exploreTopicsResponse);
        shortStoriesTopicList = new ArrayList<>();
        for (int i = 0; i < topicsResponse.getData().size(); i++) {
            if (AppConstants.SHORT_STORY_CATEGORYID.equals(topicsResponse.getData().get(i).getId())) {
                shortStoriesTopicList.addAll(topicsResponse.getData().get(i).getChild());
                break;
            }
        }

        for (int i = 0; i < shortStoriesTopicList.size(); i++) {
            if (AppConstants.SHORT_STORY_CHALLENGE_ID.equals(shortStoriesTopicList.get(i).getId())) {
                shortStoryChallengeTopic = shortStoriesTopicList.get(i);
                break;
            }
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

    private void processInfoAccordingToSource() {
        if ("draftList".equals(source)) {
            DraftListResult draftObject = (DraftListResult) getIntent().getSerializableExtra("draftItem");
            storyTitleEditText.setText(draftObject.getTitle());
            if (!StringUtils.isNullOrEmpty(draftObject.getBody())) {
                storyBodyEditText.setText(draftObject.getBody());
                setBodyWordCount(storyBodyEditText.getText().toString());
            }
            draftId = draftObject.getId();
            taggedChallenge = draftObject.getTags();
            //taggedChallenge should only contains tagged Challenge, tagged category is not to be saved in drafts
            if (taggedChallenge == null || taggedChallenge.isEmpty()) {
                //No tagged challenge, normal draft
                showCategorySelectionRecyclerForNormalStory(true);
            } else {
                processChallengeDataForDraftedStory();
            }
        } else if ("publishedList".equals(source)) {
            if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra("title"))) {
                storyTitleEditText.setText(getIntent().getStringExtra("title"));
            }
            if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra("body"))) {
                storyBodyEditText.setText(getIntent().getStringExtra("body"));
                setBodyWordCount(storyBodyEditText.getText().toString());
            }
            regulatePublishButtonState();
            articleId = getIntent().getStringExtra("articleId");
            String tagsJson = getIntent().getStringExtra("tag");
            getTagListFromJson(tagsJson);
            populateChallengeImageForPublishedStory(tagsJson);
            recyclerView.setVisibility(View.GONE);
        } else {
            creationSourceNormalOrChallenge = getIntent().getStringExtra("selectedrequest");
            if ("challenge".equals(creationSourceNormalOrChallenge)) {
                //New Story - challenge flow
                processChallengeDataForNewStory();
            } else {
                //New story - normal flow(no challenges involved)
                showCategorySelectionRecyclerForNormalStory(false);
            }
        }
    }

    private void processChallengeDataForNewStory() {
        challengeImageUrl = getIntent().getStringExtra("Url");
        taggedChallengeId = getIntent().getStringExtra("challengeId");
        taggedChallengeName = getIntent().getStringExtra("challengeName");
        taggedCategoryName = getIntent().getStringExtra("selectedCategory");
        taggedCategoryId = getIntent().getStringExtra("shortStoryCategoryId");
        if (AppConstants.SHORT_STORY_QUOTES_CATEGORY_ID.equals(taggedCategoryId)) {
            storyTitleEditText.setHint(
                    getString(R.string.short_s_add_title_hint) + "(" + getString(R.string.short_s_add_title_Optional)
                            + ")");
            storyBodyEditText.setHint(getString(R.string.story_text_description_hint));
        }
        if (challengeImageUrl != null) {
            try {
                challengeHeaderText.setVisibility(View.VISIBLE);
                challengeHeader.setVisibility(View.VISIBLE);
                challengeImage.setVisibility(View.VISIBLE);
                challengeActiveText.setVisibility(View.GONE);
                Picasso.get().load(challengeImageUrl).placeholder(R.drawable.default_article)
                        .error(R.drawable.default_article)
                        .fit().into(challengeImage);
            } catch (Exception e) {
                challengeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_article));
            }
        } else {
            challengeImage.setVisibility(View.GONE);
            challengeActiveText.setVisibility(View.VISIBLE);
            challengeActiveText.setText(taggedChallengeName);
        }
    }

    private void showCategorySelectionRecyclerForNormalStory(Boolean showPopUp) {
        recyclerView.setVisibility(View.GONE);
        shortstoryheadertext.setVisibility(View.GONE);
        challengeHeaderText.setVisibility(View.GONE);
        challengeImage.setVisibility(View.GONE);
        challengeActiveText.setVisibility(View.GONE);
        if (showPopUp) {
            RadioGroup chooseOptionRadioButton = findViewById(R.id.reportReasonRadioGroup);
            RadioGroup.LayoutParams radioGroupLayoutParams;
            chooseOptionRadioButton.setOnCheckedChangeListener((radioGroup, i) -> {
                taggedCategoryName = ssTopicsList.get(i).getDisplay_name();
                taggedCategoryId = ssTopicsList.get(i).getId();
                if (AppConstants.SHORT_STORY_QUOTES_CATEGORY_ID.equals(taggedCategoryId)) {
                    storyTitleEditText.setHint(getString(R.string.short_s_add_title_hint) + "(" + getString(
                            R.string.short_s_add_title_Optional) + ")");
                    storyBodyEditText.setHint(getString(R.string.story_text_description_hint));
                }
                regulatePublishButtonState();
            });
            for (int i = 0; i < ssTopicsList.size(); i++) {
                AppCompatRadioButton rbn = new AppCompatRadioButton(this);
                rbn.setId(i);
                rbn.setTextColor(getResources().getColor(R.color.short_story_light_black_color));
                rbn.setText(ssTopicsList.get(i).getDisplay_name());
                radioGroupLayoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                chooseOptionRadioButton.addView(rbn, radioGroupLayoutParams);
                rbn.setPadding(10, 0, 0, 0);
                if (Build.VERSION.SDK_INT >= 21) {
                    ColorStateList colorStateList = new ColorStateList(
                            new int[][] {
                                    new int[] {-android.R.attr.state_enabled},
                                    new int[] {android.R.attr.state_enabled}
                            },
                            new int[] {
                                    getResources().getColor(R.color.app_red),
                                    getResources().getColor(R.color.app_red)
                            }
                    );
                    rbn.setButtonTintList(colorStateList);
                }
            }
            chooseShortStoryTopicPopUp.setVisibility(View.VISIBLE);
        } else {
            if (getIntent().hasExtra("categoryId") && getIntent().hasExtra("categoryName")) {
                taggedCategoryId = getIntent().getStringExtra("categoryId");
                taggedCategoryName = getIntent().getStringExtra("categoryName");
            }
            if (AppConstants.SHORT_STORY_QUOTES_CATEGORY_ID.equals(taggedCategoryId)) {
                storyTitleEditText.setHint(getString(R.string.short_s_add_title_hint) + "(" + getString(
                        R.string.short_s_add_title_Optional) + ")");
                storyBodyEditText.setHint(getString(R.string.story_text_description_hint));
            }
            chooseShortStoryTopicPopUp.setVisibility(View.GONE);
        }
    }

    private void processChallengeDataForDraftedStory() {
        try {
            stripUnwantedTagsFromTaggedChallenge();
        } catch (Exception e) {

        }
        for (Map.Entry<String, String> mapEntry : taggedChallenge.get(0).entrySet()) {
            taggedChallengeId = mapEntry.getKey();
            taggedChallengeName = mapEntry.getValue();
        }

        RadioGroup chooseOptionRadioButton = findViewById(R.id.reportReasonRadioGroup);
        RadioGroup.LayoutParams radioGroupLayoutParams;
        chooseOptionRadioButton.setOnCheckedChangeListener((radioGroup, i) -> {
            taggedCategoryName = ssTopicsList.get(i).getDisplay_name();
            taggedCategoryId = ssTopicsList.get(i).getId();
            regulatePublishButtonState();
        });
        for (int i = 0; i < ssTopicsList.size(); i++) {
            AppCompatRadioButton rbn = new AppCompatRadioButton(this);
            rbn.setId(i);
            rbn.setTextColor(getResources().getColor(R.color.short_story_light_black_color));
            rbn.setText(ssTopicsList.get(i).getDisplay_name());
            radioGroupLayoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            chooseOptionRadioButton.addView(rbn, radioGroupLayoutParams);
            rbn.setPadding(10, 0, 0, 0);
            if (Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][] {
                                new int[] {-android.R.attr.state_enabled},
                                new int[] {android.R.attr.state_enabled}
                        },
                        new int[] {
                                getResources().getColor(R.color.app_red),
                                getResources().getColor(R.color.app_red)
                        }
                );
                rbn.setButtonTintList(colorStateList);
            }
        }
        chooseShortStoryTopicPopUp.setVisibility(View.VISIBLE);
        getChallengeHeaderImageUrlFromChallengeId(taggedChallengeId);
    }

    private void getTagListFromJson(String tagsJson) {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(tagsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (AppConstants.IGNORE_TAG.equals(jsonArray.getJSONObject(i).keys().next())) {
                    continue;
                }
                HashMap<String, String> map = new HashMap<>();
                String topicId = jsonArray.getJSONObject(i).keys().next().toString();
                String topicName = jsonArray.getJSONObject(i).getString(topicId);
                map.put(topicId, topicName);
                tagsList.add(map);
                for (int j = 0; j < shortStoriesTopicList.size(); j++) {
                    if (shortStoriesTopicList.get(j).getId().equals(topicId)) {
                        taggedCategoryId = topicId;
                        taggedCategoryName = topicName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateChallengeImageForPublishedStory(String tagsJson) {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(tagsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (AppConstants.IGNORE_TAG.equals(jsonArray.getJSONObject(i).keys().next())) {
                    continue;
                }
                String topicId = jsonArray.getJSONObject(i).keys().next().toString();
                for (int j = shortStoryChallengeTopic.getChild().size() - 1; j >= 0; j--) {
                    if (populateChallengeHeaderImage(topicId, j)) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean populateChallengeHeaderImage(String topicId, int j) {
        if (topicId.equals(shortStoryChallengeTopic.getChild().get(j).getId())) {
            try {
                if (shortStoryChallengeTopic.getChild().get(j).getExtraData() != null &&
                        shortStoryChallengeTopic.getChild().get(j).getExtraData().size() != 0) {
                    challengeImageUrl = shortStoryChallengeTopic.getChild().get(j).getExtraData().get(0).getChallenge()
                            .getImageUrl();
                }
                challengeHeaderText.setVisibility(View.VISIBLE);
                challengeHeader.setVisibility(View.VISIBLE);
                challengeImage.setVisibility(View.VISIBLE);
                challengeActiveText.setVisibility(View.GONE);
                Picasso.get().load(challengeImageUrl).placeholder(R.drawable.default_article)
                        .error(R.drawable.default_article)
                        .fit().into(challengeImage);
            } catch (Exception e) {
                challengeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_article));
            }
            return true;
        }
        return false;
    }

    private void getChallengeHeaderImageUrlFromChallengeId(String key) {
        if (key != null) {
            for (int j = shortStoryChallengeTopic.getChild().size() - 1; j >= 0; j--) {
                if (populateChallengeHeaderImage(key, j)) {
                    break;
                }
            }
        }
    }

    private void stripUnwantedTagsFromTaggedChallenge() {
        for (int i = 0; i < shortStoryChallengeTopic.getChild().size(); i++) {
            for (int j = 0; j < taggedChallenge.size(); j++) {
                String taggedChallengeId = ((Map.Entry<String, String>) taggedChallenge.get(j).entrySet()).getKey();
                String taggedChallengeName = ((Map.Entry<String, String>) taggedChallenge.get(j).entrySet()).getValue();
                if (shortStoryChallengeTopic.getChild().contains(taggedChallengeId)) {
                    Map<String, String> map = new HashMap<>();
                    map.put(taggedChallengeId, taggedChallengeName);
                    taggedChallenge.clear();
                    taggedChallenge.add(map);
                    break;
                }
            }
        }
    }

    private void setBodyWordCount(String text) {
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
        if (!"publishedList".equals(source) && StringUtils.isNullOrEmpty(taggedCategoryId)) {
            return false;
        }
        if (AppConstants.SHORT_STORY_QUOTES_CATEGORY_ID.equals(taggedCategoryId)) {
            return countWords(storyBodyEditText.getText().toString()) >= 5;
        }
        return countWords(storyBodyEditText.getText().toString()) >= 10
                && storyTitleEditText.getText().toString().trim().length() != 0;
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty()) {
            return 0;
        }
        return trim.split("\\s+").length; // separate string around spaces
    }

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[] {filter});
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
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
                    intent.putExtra("ssTopicsText", taggedCategoryName);
                    intent.putExtra("challengeName", taggedChallengeName);
                    intent.putExtra("challengeId", taggedChallengeId);
                    intent.putExtra("runningrequest", creationSourceNormalOrChallenge);
                    intent.putExtra("draftId", draftId);
                    intent.putExtra("articleId", articleId);
                    intent.putExtra("source", source);
//                    intent.putExtra("isDraftTaggedInActiveChallenge", isDraftTaggedInActiveChallenge);
                    intent.putExtra("taggedChallenge", taggedChallenge);
                    intent.putParcelableArrayListExtra("ssTopicsList", ssTopicsList);
//                    intent.putExtra("currentActiveChallengeId", currentActiveChallengeId);
//                    intent.putExtra("currentActiveChallenge", currentActiveChallenge);
                    intent.putExtra("tagsList", tagsList);
                    intent.putExtra("title", storyTitleEditText.getText().toString().trim());
                    intent.putExtra("story", storyBodyEditText.getText().toString().trim());
                    intent.putExtra("categoryId", taggedCategoryId);
                    startActivity(intent);
                }
                break;
            case R.id.start_writing:
                if (StringUtils.isNullOrEmpty(taggedCategoryId)) {
                    Toast.makeText(this, R.string.select_atleast_one_topic, Toast.LENGTH_SHORT).show();
                } else {
                    chooseShortStoryTopicPopUp.setVisibility(View.INVISIBLE);
                }
        }
    }

    @Override
    public void onClick(View view, int position) {
        for (int i = 0; i < ssTopicsList.size(); i++) {
            ssTopicsList.get(i).setIsSelected(false);
        }
        ssTopicsList.get(position).setIsSelected(true);
        taggedCategoryId = ssTopicsList.get(position).getId();
        taggedCategoryName = ssTopicsList.get(position).getDisplay_name();
        if (ssTopicsList.get(position).getId().equalsIgnoreCase(AppConstants.SHORT_STORY_QUOTES_CATEGORY_ID)) {
            storyTitleEditText.setHint(
                    getString(R.string.short_s_add_title_hint) + "(" + getString(R.string.short_s_add_title_Optional)
                            + ")");
            storyBodyEditText.setHint(getString(R.string.story_text_description_hint));
        } else {
            storyTitleEditText.setHint(R.string.short_s_add_title_hint);
            storyBodyEditText.setHint(R.string.short_s_add_body_hint);
        }
        regulatePublishButtonState();
        adapter.notifyDataSetChanged();
    }

    private void regulatePublishButtonState() {
        if (checkIfStoryIsValid()) {
            publishTextView.setTextColor(getResources().getColor(R.color.app_red));
        } else {
            publishTextView.setTextColor(getResources().getColor(R.color.color_979797));
        }
    }

    private boolean isValid() {
        if (!"publishedList".equals(source) && StringUtils.isNullOrEmpty(taggedCategoryId)) {
            Toast.makeText(this, R.string.select_atleast_one_topic, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (AppConstants.SHORT_STORY_QUOTES_CATEGORY_ID.equals(taggedCategoryId)) {
            if (countWords(storyBodyEditText.getText().toString()) < 5) {
                Toast.makeText(this, getString(R.string.editor_minimum_word_limit, 5), Toast.LENGTH_SHORT).show();
                return false;
            } else if (countWords(storyBodyEditText.getText().toString()) > MAX_WORDS) {
                Toast.makeText(this, getString(R.string.short_s_max_word), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }

        if (storyTitleEditText.getText() == null || StringUtils
                .isNullOrEmpty(storyTitleEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.editor_title_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (countWords(storyBodyEditText.getText().toString()) < 10) {
            Toast.makeText(this, getString(R.string.editor_minimum_word_limit, 10), Toast.LENGTH_SHORT).show();
            return false;
        } else if (countWords(storyBodyEditText.getText().toString()) > MAX_WORDS) {
            Toast.makeText(this, getString(R.string.short_s_max_word), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (((storyTitleEditText.getText() == null || StringUtils
                .isNullOrEmpty(storyTitleEditText.getText().toString()))
                && ((storyBodyEditText.getText() == null) || StringUtils
                .isNullOrEmpty(storyBodyEditText.getText().toString())))
                || "publishedList".equals(getIntent().getStringExtra("from"))) {
            super.onBackPressed();
            finish();
        } else {
            if (storyTitleEditText.getText().toString().trim().isEmpty()) {
                saveDraftRequest(storyTitleEditText.getText().toString(), storyBodyEditText.getText().toString(),
                        draftId);
            } else {
                saveDraftRequest(storyTitleEditText.getText().toString().trim(), storyBodyEditText.getText().toString(),
                        draftId);
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
        ShortStoryDraftOrPublishRequest shortStoryDraftOrPublishRequest = new ShortStoryDraftOrPublishRequest();
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
        if ("challenge".equals(creationSourceNormalOrChallenge)) {
            ArrayList<Map<String, String>> challengeTag = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put(taggedChallengeId, taggedChallengeName);
            challengeTag.add(map);
            shortStoryDraftOrPublishRequest.setTags(challengeTag);
        } else if ("draftList".equals(source)) {
            if (taggedChallenge != null && !taggedChallenge.isEmpty()) {
                shortStoryDraftOrPublishRequest.setTags(taggedChallenge);
            } else {
                shortStoryDraftOrPublishRequest.setTags(null);
            }
        }
        if (draftId1.isEmpty()) {
            Call<ArticleDraftResponse> call = shortStoryAPI.saveOrPublishShortStory(shortStoryDraftOrPublishRequest);
            call.enqueue(saveDraftResponseListener);
        } else {
            Call<ArticleDraftResponse> call = shortStoryAPI
                    .updateOrPublishShortStory(draftId1, shortStoryDraftOrPublishRequest);
            call.enqueue(saveDraftResponseListener);
        }
    }

    private Callback<ArticleDraftResponse> saveDraftResponseListener = new Callback<ArticleDraftResponse>() {
        @Override
        public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
            if (response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                showAlertDialog(getString(R.string.draft_oops), getString(R.string.draft_not_saved),
                        new OnButtonClicked() {
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
