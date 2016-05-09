package com.mycity4kids.editor;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.BlogSetupController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BlogPageAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
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
public class SetupBlogPageActivity extends BaseActivity {
    Toolbar mToolbar;
    ImageView blogImage;
    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    Uri imageUri;
    String imageString;
    File file;
    String response;
    Bitmap finalBitmap;
    String url = "blogger_list_default.jpg";
    EditText blogTitle, bloggerBio;
    Button createBlog;
    private UserModel userModel;

    @Override
    protected void updateUi(Response response) {
        switch (response.getDataType()) {

            case AppConstants.BLOG_SETUP_REQUEST: {
              /*  if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    removeProgressDialog();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //   SharedPrefUtils.setProfileImgUrl(SetupBlogPageActivity.this, responseModel.getResult().getMessage());
                            Log.i("Blog Setup Response", responseModel.getResult().getMessage());
                            if (responseModel.getResponse().equals("failure")) {
                                // showToast(responseModel.getResult().getMessage());
                                alertDialog(responseModel.getResult().getMessage());
                            } else {
                                showToast(responseModel.getResult().getMessage());
                                finish();
                            }
                        }

                        //setProfileImage(originalImage);
                        //   showToast("You have successfully uploaded image.");
                    }
                    //  removeProgressDialog();
                }*/
                break;
            }
            case AppConstants.IMAGE_EDITOR_UPLOAD_REQUEST:
          /*      removeProgressDialog();
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
                        Picasso.with(this).load(responseModel.getResult().getMessage()).error(R.drawable.default_article).into(blogImage);
                        showToast("Image successfully uploaded!");
                        // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                    }
                }
                break;*/
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_page);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        bloggerBio = (EditText) findViewById(R.id.bloggerBio);
        blogTitle = (EditText) findViewById(R.id.blogTitle);
        createBlog = (Button) findViewById(R.id.createBlog);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Setup your Blog");
        blogImage = (ImageView) findViewById(R.id.blogImage);
        Utils.pushOpenScreenEvent(SetupBlogPageActivity.this, "Article Image Upload", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        userModel = userTable.getAllUserData();
        blogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            }
        });
        createBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.pushEvent(SetupBlogPageActivity.this, GTMEventType.CALENDAR_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(SetupBlogPageActivity.this).getId() + "", "");
                if (blogTitle.getText().toString().isEmpty()) {
                    // showToast("Please fill the required fields");
                    blogTitle.setFocusableInTouchMode(true);
                    blogTitle.setError("Please enter Blog Title");
                    blogTitle.requestFocus();
                } else if (bloggerBio.getText().toString().isEmpty()) {
                    bloggerBio.setFocusableInTouchMode(true);
                    bloggerBio.setError("Please enter your Bio");
                    bloggerBio.requestFocus();
                } else if (!blogTitle.getText().toString().matches("[a-zA-Z0-9 ]+")) {
                    blogTitle.setFocusableInTouchMode(true);
                    blogTitle.setError("Special characters are not allowed!");
                    blogTitle.requestFocus();
                } else {
                    showProgressDialog(getResources().getString(R.string.please_wait));
                    ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
                    /**
                     * this case will case in pagination case: for sorting
                     */
                    articleDraftRequest.setUser_id("" + userModel.getUser().getId());

                    articleDraftRequest.setImageName(url);
                    articleDraftRequest.setTitle(blogTitle.getText().toString());
                    articleDraftRequest.setBody(bloggerBio.getText().toString());
                    articleDraftRequest.setSourceId("" + 2);
/*
        articleDraftRequest.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);*/
                    BlogSetupController _controller = new BlogSetupController(SetupBlogPageActivity.this, SetupBlogPageActivity.this);

                    _controller.getData(AppConstants.BLOG_SETUP_REQUEST, articleDraftRequest);
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    // prepare call in Retrofit 2.0
                    BlogPageAPI blogSetupAPI = retrofit.create(BlogPageAPI.class);
                    if (!ConnectivityUtils.isNetworkEnabled(SetupBlogPageActivity.this)) {
                        showToast("");
                        return;
                    }
                    Call<CommonResponse> call = blogSetupAPI.createBlogPage("" + userModel.getUser().getId(),
                            blogTitle.getText().toString(),
                            bloggerBio.getText().toString(),
                            url,
                            AppConstants.Source_Id
                            );

                    //asynchronous call
                    call.enqueue(new Callback<CommonResponse>() {
                                     @Override
                                     public void onResponse(Call<CommonResponse> call, retrofit2.Response<CommonResponse> response) {
                                         int statusCode = response.code();

                                         CommonResponse responseModel = (CommonResponse) response.body();

                                         removeProgressDialog();
                                         if (responseModel.getResponseCode() != 200) {
                                             showToast(getString(R.string.toast_response_error));
                                             return;
                                         } else {
                                             if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                                                 //   SharedPrefUtils.setProfileImgUrl(SetupBlogPageActivity.this, responseModel.getResult().getMessage());
                                                 Log.i("Blog Setup Response", responseModel.getResult().getMessage());
                                                 if (responseModel.getResponse().equals("failure")) {
                                                     // showToast(responseModel.getResult().getMessage());
                                                     alertDialog(responseModel.getResult().getMessage());
                                                 } else {
                                                     showToast(responseModel.getResult().getMessage());
                                                     finish();
                                                 }
                                             }

                                             //setProfileImage(originalImage);
                                             //   showToast("You have successfully uploaded image.");
                                         }

                                     }


                                     @Override
                                     public void onFailure(Call<CommonResponse> call, Throwable t) {

                                     }
                                 }
                    );

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
                        int maxImageSize = BitmapUtils.getMaxSize(this);
                        maxImageSize = 512;
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(SetupBlogPageActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 243;
                        float maxWidth = 423;
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
                       /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        byte[] byteArrayFromGallery = stream.toByteArray();

                        imageString = Base64.encodeToString(byteArrayFromGallery, Base64.DEFAULT);*/
                        //    new FileUploadTask().execute();
                        sendUploadProfileImageRequest(finalBitmap);
                        // compressImage(filePath);
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


    private void alertDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("MyCity4Kids")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
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
        Retrofit retrofit  = new Retrofit.Builder()
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
                                 Picasso.with(SetupBlogPageActivity.this).load(responseModel.getResult().getMessage()).error(R.drawable.default_article).into(blogImage);
                                 showToast("Image successfully uploaded!");
                                 // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                             }
                         }

                         @Override
                         public void onFailure(Call<CommonResponse> call, Throwable t) {

                         }
                     }
        );
      /*  JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("extension", "image/png");
            jsonObject.put("size", ba.length);
            jsonObject.put("byteCode", imageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        UserModel userModel = userTable.getAllUserData();

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        ImageUploadRequest requestData = new ImageUploadRequest();
        //  requestData.setFile(jsonArray.toString());
        requestData.setFile(imageString);
        requestData.setUser_id("" + userModel.getUser().getId());
        // requestData.setSessionId("" + userModel.getUser().getSessionId());
        // requestData.setProfileId("" + userModel.getUser().getProfileId());
        requestData.setImageType("jpg");
        //  requestData.setType(AppConstants.IMAGE_TYPE_USER_PROFILE);

        ImageUploadController controller = new ImageUploadController(this, this);
        controller.getData(AppConstants.IMAGE_EDITOR_UPLOAD_REQUEST, requestData);*/
    }

    public void setProfileImage(String url1) {
        if (!StringUtils.isNullOrEmpty(url1)) {
            url = url1;
            String[] seperated = url.split("/");
            if (seperated.length != 0) {
                url = seperated[seperated.length - 1];
                Log.e("url", url);
            }
            // SharedPrefUtils.setProfileImgUrl(getActivity(), url);
            //((DashboardActivity) getActivity()).updateImageProfile();

        }
    }
}
