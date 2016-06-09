package com.mycity4kids.editor;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import com.mycity4kids.controller.ArticlePublishController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.models.editor.ArticlePublishRequest;
import com.mycity4kids.models.editor.BlogDataResponse;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticlePublishAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BlogPageAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;

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
    String imageString;
    File file;
    String response;
    Bitmap finalBitmap;
    String url = "default-article-listing.jpg";
    Button publish;
    private UserModel userModel;
    SharedPreferences pref;
    boolean blogSetup = false;
    public static final String COMMON_PREF_FILE = "my_city_prefs";
    String articleId;
    BaseApplication baseApplication;

    @Override
    protected void updateUi(Response response) {
        switch (response.getDataType()) {

               /* case AppConstants.BLOG_SETUP_REQUEST: {
                    if (response.getResponseObject() instanceof CommonResponse) {
                        CommonResponse responseModel = (CommonResponse) response
                                .getResponseObject();
                        if (responseModel.getResponseCode() != 200) {
                            showToast(getString(R.string.toast_response_error));
                            return;
                        } else {
                            if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                                SharedPrefUtils.setProfileImgUrl(SetupBlogPageActivity.this, responseModel.getResult().getMessage());
                                Log.i("Uploaded Image URL", responseModel.getResult().getMessage());
                            }

                            //setProfileImage(originalImage);
                            //   showToast("You have successfully uploaded image.");
                        }
                        removeProgressDialog();
                    }
                    break;
                }*/
            case AppConstants.ARTICLE_PUBLISH_REQUEST: {
                if (response.getResponseObject() instanceof ParentingDetailResponse) {
                    ParentingDetailResponse responseModel = (ParentingDetailResponse) response
                            .getResponseObject();
                    removeProgressDialog();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Draft message", responseModel.getResult().getMessage());
                        }
                        if (responseModel.getResponse().toString().equals("success")) {


                            // draftId=responseModel.getResult().getData().getId()+"";

                            //setProfileImage(originalImage);
                            alertDialog(responseModel.getResult().getMessage());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ArticleImageTagUploadActivity.this, BloggerDashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2000);
                        } else {
                            showToast(responseModel.getResult().getMessage().toString());
                        }
                        // showToast(responseModel.getResult().getMessage());
                           /* if (fromBackpress) {
                                super.onBackPressed();
                            }*/
                        //  finish();
                    }
                }
                break;
            }
            case AppConstants.BLOG_DATA_REQUEST: {
                if (response.getResponseObject() instanceof BlogDataResponse) {
                    BlogDataResponse responseModel = (BlogDataResponse) response
                            .getResponseObject();
                    removeProgressDialog();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("BlogResponse message", responseModel.getResult().getMessage());

                        }
                        if (responseModel.getResponse().equals("success")) {
                            ArticleDraftList draftObject = (ArticleDraftList) getIntent().getSerializableExtra("draftItem");
                            showProgressDialog(getResources().getString(R.string.please_wait));
                            pref = getApplicationContext().getSharedPreferences(COMMON_PREF_FILE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("blogSetup", true);
                            Log.e("blog setup in update ui", true + "");
                            editor.commit();

                            ArticlePublishRequest articlePublishRequestRequest = new ArticlePublishRequest();
                              /*
                              * this case will case in pagination case: for sorting
                                  */
                            articlePublishRequestRequest.setUser_id("" + userModel.getUser().getId());

                            articlePublishRequestRequest.setImageUrl(url);
                            articlePublishRequestRequest.setBody(draftObject.getBody());
                            articlePublishRequestRequest.setTitle(draftObject.getTitle());
                            articlePublishRequestRequest.setDraftId(draftObject.getId());
                            articlePublishRequestRequest.setSourceId("" + 2);
                              /*
                              articleDraftRequest.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
                              _parentingModel.setPage("" + pPageCount);*/
                            ArticlePublishController _controller = new ArticlePublishController(ArticleImageTagUploadActivity.this, ArticleImageTagUploadActivity.this);

                            _controller.getData(AppConstants.ARTICLE_PUBLISH_REQUEST, articlePublishRequestRequest);

                        } else {
                            Intent intent = new Intent(ArticleImageTagUploadActivity.this, SetupBlogPageActivity.class);
                            startActivity(intent);
                        }
                        // removeProgressDialog();
                    }
                    //  removeProgressDialog();
                }
                break;
            }
            case AppConstants.IMAGE_EDITOR_UPLOAD_REQUEST:
                removeProgressDialog();
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        ((BaseActivity) this).showSnackbar(findViewById(R.id.root), getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getResult().getMessage());
                        }
                        setProfileImage(responseModel.getResult().getMessage());
                        Picasso.with(this).load(responseModel.getResult().getMessage()).error(R.drawable.default_article).into(articleImage);
                        showToast("Image successfully uploaded!");
                        // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                    }
                }
                break;

        }
    }

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
        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        userModel = userTable.getAllUserData();
        baseApplication = (BaseApplication) getApplication();
        Utils.pushOpenScreenEvent(ArticleImageTagUploadActivity.this, "Article Image Upload", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        if ((getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList")) || (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("draftList"))) {
            String thumbnailUrl = getIntent().getStringExtra("imageUrl");
            articleId = getIntent().getStringExtra("articleId");
            if (thumbnailUrl != null) {
                Picasso.with(this).load(thumbnailUrl).into(articleImage);
                String[] seperated = thumbnailUrl.split("/");
                if (seperated.length != 0) {
                    url = seperated[seperated.length - 1];
                    Log.e("url", url);
                }
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
                Utils.pushEvent(ArticleImageTagUploadActivity.this, GTMEventType.PUBLISH_ARTICLE_BUTTON_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(ArticleImageTagUploadActivity.this).getId() + "", "Article Image Upload");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        //   mediaFile.setVideo(imageUri.toString().contains("video"));

        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
                imageUri = data.getData();

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = this.getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.e("File", "filePath: " + filePath);
                        filePath = filePath.replaceAll("[^a-zA-Z0-9.-/_]", "_");
                        file = new File(new URI("file://"
                                + filePath.replaceAll(" ", "%20")));

                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(ArticleImageTagUploadActivity.this.getContentResolver(), imageUri);
                        //  sendUploadProfileImageRequest(imageBitmap);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 243;
                        float maxWidth = 423;
                       /* float maxHeight = 1300;
                        float maxWidth = 700;*/
                        float imgRatio = actualWidth / actualHeight;
                        float maxRatio = maxWidth / maxHeight;

                        if (actualHeight > maxHeight || actualWidth > maxWidth) {
                            if (imgRatio < maxRatio) {
                                //adjust width according to maxHeight
                                imgRatio = maxHeight / actualHeight;
                                actualWidth = imgRatio * actualWidth;
                                actualHeight = maxHeight;
                            } else if (imgRatio > maxRatio) {
                                //adjust height according to maxWidth
                                imgRatio = maxWidth / actualWidth;
                                actualHeight = imgRatio * actualHeight;
                                actualWidth = maxWidth;
                            } else {
                                actualHeight = maxHeight;
                                actualWidth = maxWidth;
                            }
                        }
                        finalBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) actualWidth, (int) actualHeight, true);
                      /*  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArrayFromGallery = stream.toByteArray();

                        imageString = Base64.encodeToString(byteArrayFromGallery, Base64.DEFAULT);*/
                        sendUploadProfileImageRequest(finalBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;
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
        ArticleDraftList draftObject = (ArticleDraftList) getIntent().getSerializableExtra("draftItem");
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticlePublishAPI articlePublishAPI = retrofit.create(ArticlePublishAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<ParentingDetailResponse> call = articlePublishAPI.publishArticle("" + userModel.getUser().getId(),
                draftObject.getTitle().trim(),
                draftObject.getBody(),
                articleId,
                draftObject.getId(),
                url,
                "" + 2,
                draftObject.getModeration_status() + "",
                draftObject.getNode_id() + "");
        Log.e("Publish Request", draftObject.getBody());

        //asynchronous call
        call.enqueue(new Callback<ParentingDetailResponse>() {
                         @Override
                         public void onResponse(Call<ParentingDetailResponse> call, retrofit2.Response<ParentingDetailResponse> response) {
                             int statusCode = response.code();

                             ParentingDetailResponse responseModel = (ParentingDetailResponse) response.body();

                             removeProgressDialog();
                             if (null == responseModel || responseModel.getResponseCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                                     //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                                     Log.i("Retrofit Publish Message", responseModel.getResult().getMessage());
                                 }
                                 if (responseModel.getResponse().toString().equals("success")) {


                                     // draftId=responseModel.getResult().getData().getId()+"";

                                     //setProfileImage(originalImage);
                                     alertDialog(responseModel.getResult().getMessage());
                                     Handler handler = new Handler();
                                     /*handler.postDelayed(new Runnable() {
                                         @Override
                                         public void run() {
                                             Intent intent = new Intent(ArticleImageTagUploadActivity.this, BloggerDashboardActivity.class);
                                             startActivity(intent);
                                             finish();
                                         }
                                     }, 2000);*/
                                 } else {
                                     showToast(responseModel.getResult().getMessage().toString());
                                 }
                             }

                         }


                         @Override
                         public void onFailure(Call<ParentingDetailResponse> call, Throwable t) {
                             Crashlytics.logException(t);
                             Log.d("Exception", Log.getStackTraceString(t));
                         }
                     }
        );
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

        Call<BlogDataResponse> call = getBlogPageAPI.getBlogPage("" + userModel.getUser().getId(),
                "" + 2);
        call.enqueue(new Callback<BlogDataResponse>() {
            @Override
            public void onResponse(Call<BlogDataResponse> call, retrofit2.Response<BlogDataResponse> response) {
                BlogDataResponse responseModel = (BlogDataResponse) response
                        .body();
                removeProgressDialog();
                if (responseModel.getResponseCode() != 200) {
                    showToast(getString(R.string.toast_response_error));
                    return;
                } else {
                    if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                        //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                        Log.i("BlogResponse message", responseModel.getResult().getMessage());

                    }
                    if (responseModel.getResponse().equals("success")) {
                        showProgressDialog(getResources().getString(R.string.please_wait));
                        pref = getApplicationContext().getSharedPreferences(COMMON_PREF_FILE, MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("blogSetup", true);
                        Log.e("blog setup in update ui", true + "");
                        editor.commit();
                        publishArticleRequest();

                    } else {
                        Intent intent = new Intent(ArticleImageTagUploadActivity.this, SetupBlogPageActivity.class);
                        startActivity(intent);
                    }
                    // removeProgressDialog();
                }
                //  removeProgressDialog();
            }

            @Override
            public void onFailure(Call<BlogDataResponse> call, Throwable t) {

            }
        });

       /* ArticleDraftRequest requestData = new ArticleDraftRequest();
        requestData.setUser_id("" + userModel.getUser().getId());
        requestData.setSourceId(""+2);
        ArticleDraftController controller = new ArticleDraftController(this, this);
        controller.getData(AppConstants.BLOG_DATA_REQUEST, requestData);*/
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
                        startActivity(intent);
                        finish();
                    }
                })
                .show();

    }

    public void sendUploadProfileImageRequest(Bitmap originalImage) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, imageString);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "" + userModel.getUser().getId());
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "jpg");
        // prepare call in Retrofit 2.0
        ImageUploadAPI imageUploadAPI = retrofit.create(ImageUploadAPI.class);

        Call<CommonResponse> call = imageUploadAPI.uploadImage(userId,
                imageType,
                requestBodyFile);
        //asynchronous call
        call.enqueue(new Callback<CommonResponse>() {
                         @Override
                         public void onResponse(Call<CommonResponse> call, retrofit2.Response<CommonResponse> response) {
                             int statusCode = response.code();
                             CommonResponse responseModel = response.body();

                             removeProgressDialog();
                             if (responseModel.getResponseCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getResult().getMessage());
                                 }
                                 setProfileImage(responseModel.getResult().getMessage());
                                 Picasso.with(ArticleImageTagUploadActivity.this).load(responseModel.getResult().getMessage()).error(R.drawable.default_article).into(articleImage);
                                 showToast("Image successfully uploaded!");
                                 // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                             }
                         }

                         @Override
                         public void onFailure(Call<CommonResponse> call, Throwable t) {

                         }
                     }
        );

    }

    public void setProfileImage(String url1) {
        if (!StringUtils.isNullOrEmpty(url1)) {
            url = url1;
            String[] seperated = url.split("/");
            if (seperated.length != 0) {
                url = seperated[seperated.length - 1];
                Log.e("url", url);
            }
        }
    }
}
