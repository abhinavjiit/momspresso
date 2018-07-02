package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.mycity4kids.editor.ArticleImageTagUploadActivity;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
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
import com.mycity4kids.widget.StartSnapHelper;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    private static final int MAX_WORDS = 100;
    private Toolbar toolbar;
    private TextView publishTextView;
    private String dynamoUserId;
    private ArrayList<ExploreTopicsModel> ssTopicsList;
    private String draftId = "";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_short_story_activity);
        Utils.pushOpenScreenEvent(this, "AddShortStoryScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        publishTextView = (TextView) toolbar.findViewById(R.id.publishTextView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        storyTitleEditText = (EditText) findViewById(R.id.storyTitleEditText);
        storyBodyEditText = (EditText) findViewById(R.id.storyBodyEditText);

        publishTextView.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        source = getIntent().getStringExtra("from");
        ssTopicsList = new ArrayList<>();

        if ("draftList".equals(source)) {
            draftObject = (DraftListResult) getIntent().getSerializableExtra("draftItem");
            storyTitleEditText.setText(draftObject.getTitle());
            storyBodyEditText.setText(draftObject.getBody());
            draftId = draftObject.getId();
        } else if ("publishedList".equals(source)) {
            storyTitleEditText.setText(getIntent().getStringExtra("title"));
            storyBodyEditText.setText(getIntent().getStringExtra("body"));
            articleId = getIntent().getStringExtra("articleId");
            tagsJson = getIntent().getStringExtra("tag");
            updateTagListFromJson(tagsJson);
            recyclerView.setVisibility(View.GONE);
        }

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

    private void updateTagListFromJson(String tagsJson) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(tagsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                String key = jsonArray.getJSONObject(i).keys().next();
                map.put(key, jsonArray.getJSONObject(i).getString(key));
                if (!"ignore".equals(key)) {
                    tagsList.add(map);
                }
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
                        saveDraftBeforePublishRequest(storyTitleEditText.getText().toString(), storyBodyEditText.getText().toString(), draftId);
                    }
                }
                break;
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
                    createAndUploadShareableImage();
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
            for (int i = 0; i < ssTopicsList.size(); i++) {
                if (ssTopicsList.get(i).isSelected()) {
                    ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<>();
                    map.put(ssTopicsList.get(i).getId(), ssTopicsList.get(i).getDisplay_name());
                    list.add(map);
                    shortStoryDraftOrPublishRequest.setTags(list);
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
}
