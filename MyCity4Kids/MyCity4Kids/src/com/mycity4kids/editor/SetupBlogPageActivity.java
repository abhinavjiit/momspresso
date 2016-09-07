package com.mycity4kids.editor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.SetupBlogResponse;
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
        Utils.pushOpenScreenEvent(SetupBlogPageActivity.this, "Setup Blog", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        userModel = userTable.getAllUserData();
        if (getIntent().getStringExtra("userBio") != null && !getIntent().getStringExtra("userBio").isEmpty()) {
            bloggerBio.setText(getIntent().getStringExtra("userBio"));
        }

        createBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.pushEvent(SetupBlogPageActivity.this, GTMEventType.SETUP_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(SetupBlogPageActivity.this).getDynamoId() + "", "Set Up Blog");
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

                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    // prepare call in Retrofit 2.0
                    BlogPageAPI blogSetupAPI = retrofit.create(BlogPageAPI.class);
                    if (!ConnectivityUtils.isNetworkEnabled(SetupBlogPageActivity.this)) {
                        removeProgressDialog();
                        showToast(getString(R.string.error_network));
                        return;
                    }
                    Call<SetupBlogResponse> call = blogSetupAPI.createBlogPage(
                            blogTitle.getText().toString(),
                            bloggerBio.getText().toString()
                    );

                    //asynchronous call
                    call.enqueue(new Callback<SetupBlogResponse>() {
                                     @Override
                                     public void onResponse(Call<SetupBlogResponse> call, retrofit2.Response<SetupBlogResponse> response) {
                                         removeProgressDialog();
                                         if (response == null || response.body() == null) {
                                             showToast(getString(R.string.went_wrong));
                                             return;
                                         }

                                         SetupBlogResponse responseData = (SetupBlogResponse) response.body();
                                         if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                             showToast(responseData.getData().getMsg());
                                             finish();
                                         } else {
                                             showToast(responseData.getReason());
                                         }
                                     }

                                     @Override
                                     public void onFailure(Call<SetupBlogResponse> call, Throwable t) {
                                         Crashlytics.logException(t);
                                         removeProgressDialog();
                                         Log.d("MC4KException", Log.getStackTraceString(t));
                                         showToast(getString(R.string.went_wrong));
                                     }
                                 }
                    );

                }
            }
        });
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

}
