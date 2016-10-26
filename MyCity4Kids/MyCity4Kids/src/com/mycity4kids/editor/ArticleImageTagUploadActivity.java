package com.mycity4kids.editor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
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
import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticlePublishAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BlogPageAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anshul on 3/18/16.
 */
public class ArticleImageTagUploadActivity extends BaseActivity {
    Toolbar mToolbar;
    ImageView articleImage;
    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    Uri imageUri;
    String url;
    Button publish;
    SharedPreferences pref;
    boolean blogSetup = false;
    public static final String COMMON_PREF_FILE = "my_city_prefs";
    String articleId;
    BaseApplication baseApplication;
    final int PIC_CROP = 1;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_image_tag_publish);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        publish = (Button) findViewById(R.id.publish);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Publish Blog");
        articleImage = (ImageView) findViewById(R.id.articleImage);

        baseApplication = (BaseApplication) getApplication();
        Utils.pushOpenScreenEvent(ArticleImageTagUploadActivity.this, "Article Image Upload", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        if ((getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList"))) {
            String thumbnailUrl = getIntent().getStringExtra("imageUrl");
            articleId = getIntent().getStringExtra("articleId");
            if (thumbnailUrl != null) {
                Picasso.with(this).load(thumbnailUrl).into(articleImage);
                url = thumbnailUrl;
            }

        }
        articleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.pushEvent(ArticleImageTagUploadActivity.this, GTMEventType.PUBLISH_ARTICLE_BUTTON_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(ArticleImageTagUploadActivity.this).getDynamoId() + "", "Article Image Upload");
                pref = getSharedPreferences(COMMON_PREF_FILE, MODE_PRIVATE);
                blogSetup = pref.getBoolean("blogSetup", false);
                Log.e("blogsetup", blogSetup + "");
                if (blogSetup == false) {
                    getBlogPage();
                } else {
                    publishArticleRequest();
                }
            }
        });
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }
        imageUri = data.getData();

        //   mediaFile.setVideo(imageUri.toString().contains("video"));

        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.e("inImagePick", "test");
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(ArticleImageTagUploadActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        if (actualHeight < 405 || actualWidth < 720) {
                            showToast("Please upload bigger image");
                            return;
                        }
                        startCropActivity(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;
            case UCrop.REQUEST_CROP: {
                {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it

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

    private void publishArticleRequest() {
        PublishDraftObject draftObject = (PublishDraftObject) getIntent().getSerializableExtra("draftItem");
        String tags = getIntent().getStringExtra("tag");
        String cities = getIntent().getStringExtra("cities");
        String from = getIntent().getStringExtra("from");
        showProgressDialog(getResources().getString(R.string.please_wait));
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
            ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(tags);
                for (int i = 0; i < jsonArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<>();
                    String key = (String) jsonArray.getJSONObject(i).keys().next();
                    map.put(key, jsonArray.getJSONObject(i).getString(key));
                    list.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            articleDraftRequest.setTags(list);
            articleDraftRequest.setImageUrl(url);
            articleDraftRequest.setArticleType("1");

            Call<ArticleDraftResponse> call = articlePublishAPI.publishArticle(articleDraftRequest);
            Log.e("Publish Request", draftObject.getBody());

            //asynchronous call
            call.enqueue(new Callback<ArticleDraftResponse>() {
                             @Override
                             public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                                 int statusCode = response.code();

                                 ArticleDraftResponse responseModel = (ArticleDraftResponse) response.body();

                                 removeProgressDialog();
                                 if (response == null || response.body() == null) {
                                     showToast(getString(R.string.went_wrong));
                                     return;
                                 }
                                 if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                                     if (!StringUtils.isNullOrEmpty(responseModel.getData().get(0).getMsg())) {
                                         //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                                         Log.i("Retro Publish Message", responseModel.getData().get(0).getMsg());

                                         alertDialog(responseModel.getData().get(0).getMsg());
                                     } else {
                                         showToast(responseModel.getReason().toString());
                                     }
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
            // Article which is published is  already saved as draft.
            ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
            articleDraftRequest.setTitle(draftObject.getTitle().trim());
            articleDraftRequest.setBody(draftObject.getBody());

            ArrayList<Map<String, String>> tagList = new ArrayList<Map<String, String>>();
            JSONArray tagsArray = null;
            try {
                tagsArray = new JSONArray(tags);
                for (int i = 0; i < tagsArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<>();
                    String key = (String) tagsArray.getJSONObject(i).keys().next();
                    map.put(key, tagsArray.getJSONObject(i).getString(key));
                    tagList.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if ("editor".equals(from)) {
                articleDraftRequest.setId(draftObject.getId());
            } else {
                ArrayList<Map<String, String>> cityList = new ArrayList<Map<String, String>>();
                JSONArray cityArray = null;
                try {
                    cityArray = new JSONArray(cities);
                    for (int i = 0; i < cityArray.length(); i++) {
                        HashMap<String, String> map = new HashMap<>();
                        String key = (String) cityArray.getJSONObject(i).keys().next();
                        map.put(key, cityArray.getJSONObject(i).getString(key));
                        cityList.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                articleDraftRequest.setCities(cityList);

            }

            articleDraftRequest.setTags(tagList);
            articleDraftRequest.setImageUrl(url);
            articleDraftRequest.setArticleType("1");

            Call<ArticleDraftResponse> call1 = articlePublishAPI.updateArticle(draftObject.getId(), articleDraftRequest);
            call1.enqueue(new Callback<ArticleDraftResponse>() {
                @Override
                public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                    int statusCode = response.code();
                    removeProgressDialog();
                    if (response == null || response.body() == null) {
                        showToast("Something went wrong from server");
                        return;
                    }
                    ArticleDraftResponse responseModel = (ArticleDraftResponse) response.body();

                    if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                        id = responseModel.getData().get(0).getResult().getId() + "";
                        alertDialog(responseModel.getData().get(0).getMsg());
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

    private void getBlogPage() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();

        BlogPageAPI getBlogPageAPI = retrofit.create(BlogPageAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        Call<BlogPageResponse> call = getBlogPageAPI.getBlogPage("v1/users/blogPage/" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
        call.enqueue(new Callback<BlogPageResponse>() {
            @Override
            public void onResponse(Call<BlogPageResponse> call, retrofit2.Response<BlogPageResponse> response) {
                BlogPageResponse responseModel = (BlogPageResponse) response
                        .body();
                removeProgressDialog();
                if (responseModel.getCode() != 200) {
                    showToast(getString(R.string.toast_response_error));
                    return;
                } else {
                    if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                        //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                        Log.i("BlogResponse message", responseModel.getData().getMsg());

                    }
                    if (responseModel.getData().getResult().getIsSetup() == 1) {
                        showProgressDialog(getResources().getString(R.string.please_wait));
                        pref = getApplicationContext().getSharedPreferences(COMMON_PREF_FILE, MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("blogSetup", true);
                        Log.e("blog setup in update ui", true + "");
                        editor.commit();
                        publishArticleRequest();

                    } else if (responseModel.getData().getResult().getIsSetup() == 0) {
                        Intent intent = new Intent(ArticleImageTagUploadActivity.this, SetupBlogPageActivity.class);
                        if (responseModel.getData().getResult().getUserBio() != null && !responseModel.getData().getResult().getUserBio().isEmpty())
                            intent.putExtra("userBio", responseModel.getData().getResult().getUserBio());
                        startActivity(intent);
                    }
                    // removeProgressDialog();
                }
                //  removeProgressDialog();
            }

            @Override
            public void onFailure(Call<BlogPageResponse> call, Throwable t) {
                removeProgressDialog();
                Crashlytics.logException(t);
                Log.d("MC4KException", Log.getStackTraceString(t));
            }
        });

    }

    private void alertDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("MyCity4Kids")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        //  dialog.dismiss();
                        Intent intent = new Intent(ArticleImageTagUploadActivity.this, BloggerDashboardActivity.class);
                        intent.putExtra(AppConstants.STACK_CLEAR_REQUIRED, true);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
       /* originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.LIVE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
                             int statusCode = response.code();
                             ImageUploadResponse responseModel = response.body();

                             removeProgressDialog();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                                 }
                                 url = (responseModel.getData().getResult().getUrl());
                                 Picasso.with(ArticleImageTagUploadActivity.this).load(responseModel.getData().getResult().getUrl()).error(R.drawable.default_article).into(articleImage);
                                 showToast("Image successfully uploaded!");
                                 // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
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


}
