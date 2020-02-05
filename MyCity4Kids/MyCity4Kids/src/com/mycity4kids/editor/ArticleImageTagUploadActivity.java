package com.mycity4kids.editor;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ArticleTagsImagesResponse;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticlePublishAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.activity.ArticleModerationOrShareActivity;
import com.mycity4kids.ui.activity.BlogSetupActivity;
import com.mycity4kids.ui.adapter.ArticleTagsImagesGridAdapter;
import com.mycity4kids.utils.PermissionUtil;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 3/18/16.
 */
public class ArticleImageTagUploadActivity extends BaseActivity implements View.OnClickListener, ArticleTagsImagesGridAdapter.ITagImageSelect {

    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static final int REQUEST_INIT_PERMISSION = 1;

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final String COMMON_PREF_FILE = "my_city_prefs";
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private Toolbar mToolbar;
    private GridView gridview;
    private RelativeLayout uploadImageCardView;
    private ProgressBar progressBar;
    private ImageView articleImage;
    private Button publish;
    private TextView changePictureTextView, publishTextView;
    private RelativeLayout mLodingView;

    private SharedPreferences pref;

    private boolean blogSetup = false;
    private Uri imageUri;
    private String articleId;
    private String id;
    private String url;
    private String tags;
    private int limit = 10;
    private int pageNumber = 1;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;

    private ArrayList<Map<String, String>> tagsList = new ArrayList<Map<String, String>>();
    private ArrayList<String> tagsKeyList = new ArrayList<String>();
    private ArrayList<ArticleTagsImagesResponse.ArticleTagsImagesData.ArticleTagsImagesResult> tagsImageList;

    private ArticleTagsImagesGridAdapter adapter;
    private View mLayout;
    private String userAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_image_tag_publish);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        gridview = (GridView) findViewById(R.id.gridview);
        mLayout = findViewById(R.id.rootLayout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        articleImage = (ImageView) findViewById(R.id.articleImage);
        uploadImageCardView = (RelativeLayout) findViewById(R.id.uploadImageContainer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        changePictureTextView = (TextView) findViewById(R.id.changePictureTextView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        publishTextView = (TextView) findViewById(R.id.publishTextView);

        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        tags = getIntent().getStringExtra("tag");
        userAgent = getIntent().getStringExtra("userAgent");

        setTagsList();
        Utils.pushOpenScreenEvent(this, "AddImageScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        if ((getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList"))) {
            String thumbnailUrl = getIntent().getStringExtra("imageUrl");
            uploadImageCardView.setVisibility(View.GONE);
            articleImage.setVisibility(View.VISIBLE);
            changePictureTextView.setVisibility(View.VISIBLE);
            articleId = getIntent().getStringExtra("articleId");
            if (thumbnailUrl != null) {
                Picasso.with(this).load(thumbnailUrl).into(articleImage);
                url = thumbnailUrl;
            }
        } else {
            articleImage.setVisibility(View.GONE);
            uploadImageCardView.setVisibility(View.VISIBLE);
        }

        articleImage.setOnClickListener(this);
        publishTextView.setOnClickListener(this);
        uploadImageCardView.setOnClickListener(this);
        changePictureTextView.setOnClickListener(this);

        tagsImageList = new ArrayList<>();
        adapter = new ArticleTagsImagesGridAdapter(this);
        adapter.setDatalist(tagsImageList);
        gridview.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        getImagesForCategories();

        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    getImagesForCategories();
                    isReuqestRunning = true;
                }
            }
        });
    }

    private void getImagesForCategories() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticlePublishAPI articlePublishAPI = retrofit.create(ArticlePublishAPI.class);
        Call<ArticleTagsImagesResponse> tagImagesCall = articlePublishAPI.getImagesForCategories(TextUtils.join(",", tagsKeyList), limit, pageNumber);
        tagImagesCall.enqueue(tagsImagesResponseCallback);
    }

    private Callback<ArticleTagsImagesResponse> tagsImagesResponseCallback = new Callback<ArticleTagsImagesResponse>() {
        @Override
        public void onResponse(Call<ArticleTagsImagesResponse> call, retrofit2.Response<ArticleTagsImagesResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            ArticleTagsImagesResponse responseModel = response.body();
            if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                if (responseModel.getData() != null && !responseModel.getData().isEmpty() && responseModel.getData().get(0) != null) {
                    processResponse(responseModel.getData());
                } else {
                    showToast(responseModel.getReason().toString());
                }
            }
        }

        @Override
        public void onFailure(Call<ArticleTagsImagesResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processResponse(List<ArticleTagsImagesResponse.ArticleTagsImagesData> data) {
        ArrayList<ArticleTagsImagesResponse.ArticleTagsImagesData.ArticleTagsImagesResult> datalist = data.get(0).getResult();
        if (datalist == null || datalist.size() == 0) {
            isLastPageReached = true;
            if (null != tagsImageList && !tagsImageList.isEmpty()) {
                // empty arraylist in subsequent api calls while pagination
            } else {
                //Empty arraylist result for first api call
                tagsImageList = datalist;
                adapter.setDatalist(datalist);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (pageNumber == 1) {
                tagsImageList = datalist;
            } else {
                tagsImageList.addAll(datalist);
            }
            adapter.setDatalist(tagsImageList);
            pageNumber = pageNumber + 1;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        imageUri = data.getData();
        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.e("inImagePick", "test");
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(ArticleImageTagUploadActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        if (actualHeight < 405 || actualWidth < 720) {
                            showToast(getString(R.string.upload_bigger_image));
                            return;
                        }
                        startCropActivity(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    Log.e("resultUri", resultUri.toString());
                    File file2 = FileUtils.getFile(this, resultUri);
                    sendUploadProfileImageRequest(file2);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void setTagsList() {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(tags);
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                String key = jsonArray.getJSONObject(i).keys().next();
                tagsKeyList.add(key);
                map.put(key, jsonArray.getJSONObject(i).getString(key));
                tagsList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishArticleRequest() {
        PublishDraftObject draftObject = (PublishDraftObject) getIntent().getSerializableExtra("draftItem");
        String cities = getIntent().getStringExtra("cities");
        String from = getIntent().getStringExtra("from");
        showProgressDialog(getResources().getString(R.string.please_wait));
        BaseApplication.getInstance().destroyRetrofitInstance();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticlePublishAPI articlePublishAPI = retrofit.create(ArticlePublishAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        if (draftObject.getId() == null || draftObject.getId().isEmpty()) {
            // Article is published directly and never saved in draft.
            ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
            articleDraftRequest.setTitle(draftObject.getTitle().trim());
            articleDraftRequest.setBody(draftObject.getBody());
            articleDraftRequest.setTags(tagsList);
            articleDraftRequest.setImageUrl(url);
            articleDraftRequest.setArticleType("1");
            if (AppConstants.ANDROID_NEW_EDITOR.equals(userAgent)) {
                articleDraftRequest.setUserAgent1(userAgent);
            }

            Call<ArticleDraftResponse> call = articlePublishAPI.publishArticle(articleDraftRequest);
            Log.e("Publish Request", draftObject.getBody());

            //asynchronous call
            call.enqueue(new Callback<ArticleDraftResponse>() {
                             @Override
                             public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                                 removeProgressDialog();
                                 ArticleDraftResponse responseModel = response.body();
                                 if (response == null || response.body() == null) {
                                     NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                                     Crashlytics.logException(nee);
                                     showToast(getString(R.string.went_wrong));
                                     return;
                                 }
                                 if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                                     if (!StringUtils.isNullOrEmpty(responseModel.getData().get(0).getMsg())) {
                                         Log.i("Retro Publish Message", responseModel.getData().get(0).getMsg());
                                         if (StringUtils.isNullOrEmpty(responseModel.getData().get(0).getResult().getUrl())) {
                                             Utils.pushPublishArticleEvent(ArticleImageTagUploadActivity.this, "AddImageScreen", SharedPrefUtils.getUserDetailModel(ArticleImageTagUploadActivity.this).getDynamoId(), "moderation");
                                         } else {
                                             Utils.pushPublishArticleEvent(ArticleImageTagUploadActivity.this, "AddImageScreen", SharedPrefUtils.getUserDetailModel(ArticleImageTagUploadActivity.this).getDynamoId(), "published");
                                         }
                                         Intent intent = new Intent(ArticleImageTagUploadActivity.this, ArticleModerationOrShareActivity.class);
                                         intent.putExtra("shareUrl", "" + responseModel.getData().get(0).getResult().getUrl());
                                         intent.putExtra("source", "addArticle");
                                         startActivity(intent);
                                     } else {
                                         showToast(responseModel.getReason().toString());
                                     }
                                 } else {
                                     showToast(responseModel.getReason().toString());
                                 }
                             }

                             @Override
                             public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
                                 removeProgressDialog();
                                 Crashlytics.logException(t);
                                 Log.d("MC4kException", Log.getStackTraceString(t));
                             }
                         }
            );
        } else {
            ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
            articleDraftRequest.setTitle(draftObject.getTitle().trim());
            articleDraftRequest.setBody(draftObject.getBody());
            if ("editor".equals(from)) {
                articleDraftRequest.setId(draftObject.getId());
            } else {
                ArrayList<Map<String, String>> cityList = new ArrayList<Map<String, String>>();
                JSONArray cityArray = null;
                try {
                    cityArray = new JSONArray(cities);
                    for (int i = 0; i < cityArray.length(); i++) {
                        HashMap<String, String> map = new HashMap<>();
                        String key = cityArray.getJSONObject(i).keys().next();
                        map.put(key, cityArray.getJSONObject(i).getString(key));
                        cityList.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                articleDraftRequest.setCities(cityList);
            }
            articleDraftRequest.setTags(tagsList);
            articleDraftRequest.setImageUrl(url);
            articleDraftRequest.setArticleType("1");
            if (AppConstants.ANDROID_NEW_EDITOR.equals(userAgent)) {
                articleDraftRequest.setUserAgent1(userAgent);
            }
            Log.e("publish request", new Gson().toJson(articleDraftRequest));
            Call<ArticleDraftResponse> call1 = articlePublishAPI.updateArticle(draftObject.getId(), articleDraftRequest);
            call1.enqueue(new Callback<ArticleDraftResponse>() {
                @Override
                public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                    removeProgressDialog();
                    if (response.body() == null) {
                        showToast(getString(R.string.server_went_wrong));
                        return;
                    }
                    ArticleDraftResponse responseModel = response.body();
                    if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                        id = responseModel.getData().get(0).getResult().getId() + "";
                        if (StringUtils.isNullOrEmpty(responseModel.getData().get(0).getResult().getUrl())) {
                            Utils.pushPublishArticleEvent(ArticleImageTagUploadActivity.this, "AddImageScreen", SharedPrefUtils.getUserDetailModel(ArticleImageTagUploadActivity.this).getDynamoId(), "moderation");
                        } else {
                            Utils.pushPublishArticleEvent(ArticleImageTagUploadActivity.this, "AddImageScreen", SharedPrefUtils.getUserDetailModel(ArticleImageTagUploadActivity.this).getDynamoId(), "published");
                        }
                        Intent intent = new Intent(ArticleImageTagUploadActivity.this, ArticleModerationOrShareActivity.class);
                        intent.putExtra("shareUrl", "" + responseModel.getData().get(0).getResult().getUrl());
                        intent.putExtra("source", "addArticle");
                        startActivity(intent);
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getReason())) {
                            if (responseModel.getReason().equalsIgnoreCase("Can't update article which is under moderation !!")) {
                                Intent intent = new Intent(ArticleImageTagUploadActivity.this, ArticleModerationOrShareActivity.class);
                                intent.putExtra("shareUrl", "" + "");
                                intent.putExtra("source", "addArticle");
                                startActivity(intent);
                            } else {
                                showToast(responseModel.getReason());
                            }
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

    private void getBlogPage() {
        showProgressDialog(getResources().getString(R.string.please_wait));
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
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                launchBlogSetup(responseData);
                if (StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getBlogTitleSlug())) {
                    launchBlogSetup(responseData);
                } else if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getBlogTitleSlug())) {
                    if (StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getEmail())) {
                        launchBlogSetup(responseData);
                    } else if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getEmail())) {
                        publishArticleRequest();
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void launchBlogSetup(UserDetailResponse responseData) {
        Intent intent = new Intent(ArticleImageTagUploadActivity.this, BlogSetupActivity.class);
        intent.putExtra("BlogTitle", responseData.getData().get(0).getResult().getBlogTitle());
        intent.putExtra("email", responseData.getData().get(0).getResult().getEmail());
        intent.putExtra("comingFrom", "ShortStoryAndArticle");
        startActivity(intent);
    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        Log.e("requestBodyFile", requestBodyFile.toString());
        //   RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "" + userModel.getUser().getId());
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "1");
        // prepare call in Retrofit 2.0

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ImageUploadAPI imageUploadAPI = retro.create(ImageUploadAPI.class);
        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,
                //  imageType,
                imageType,
                requestBodyFile);
        //asynchronous call
        call.enqueue(new Callback<ImageUploadResponse>() {
                         @Override
                         public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
                             removeProgressDialog();
                             if (response == null || response.body() == null) {
                                 showToast(getString(R.string.server_went_wrong));
                                 return;
                             }
                             ImageUploadResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                             } else {
                                 url = (responseModel.getData().getResult().getUrl());
                                 articleImage.setVisibility(View.VISIBLE);
                                 uploadImageCardView.setVisibility(View.GONE);
                                 changePictureTextView.setVisibility(View.VISIBLE);
                                 if (null != tagsImageList) {
                                     for (int i = 0; i < tagsImageList.size(); i++) {
                                         tagsImageList.get(i).setSelected(false);
                                     }
                                 }
                                 adapter.notifyDataSetChanged();
                                 Picasso.with(ArticleImageTagUploadActivity.this).load(responseModel.getData().getResult().getUrl()).error(R.drawable.default_article).into(articleImage);
                                 showToast(getString(R.string.image_upload_success));
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             Crashlytics.logException(t);
                             Log.d("MC4KException", Log.getStackTraceString(t));
                             showToast(getString(R.string.went_wrong));
                         }
                     }
        );
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(16, 9);
        uCrop.withMaxResultSize(720, 405);
        uCrop.start(ArticleImageTagUploadActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changePictureTextView:
            case R.id.articleImage:
            case R.id.uploadImageContainer:
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(ArticleImageTagUploadActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(ArticleImageTagUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(ArticleImageTagUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                    } else {
                        startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                    }
                } else {
                    startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                }
                break;
            case R.id.publishTextView:
                if (null == url) {
                    showToast(getString(R.string.publish_article_upload_image_please_upload_or_choose_image));
                    return;
                }
                Utils.pushEvent(ArticleImageTagUploadActivity.this, GTMEventType.PUBLISH_ARTICLE_BUTTON_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(ArticleImageTagUploadActivity.this).getDynamoId() + "", "Article Image Upload");
                getBlogPage();
                break;
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            } else {
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onTagImageSelected(String url) {
        this.url = url;
    }
}
