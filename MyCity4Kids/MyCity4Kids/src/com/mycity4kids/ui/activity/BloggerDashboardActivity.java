package com.mycity4kids.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.BloggerDashboardAndPublishedArticlesController;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.request.UpdateUserDetail;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.newmodels.BloggerDashboardModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.ui.adapter.BloggerDashboardPagerAdapter;
import com.mycity4kids.utils.RoundedTransformation;
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
 * Created by hemant on 16/3/16.
 */
public class BloggerDashboardActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView bloggerNameTextView, rankingTextView, viewCountTextView, followersViewCount;
    private ImageView bloggerImageView;
    View vSeparator1, vSeparator2;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView addDraft;
    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    Uri imageUri;
    Bitmap finalBitmap;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogger_dashboard);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        Utils.pushOpenScreenEvent(BloggerDashboardActivity.this, "Blogger Dashboard", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        rankingTextView = (TextView) findViewById(R.id.rankingTextView);
        viewCountTextView = (TextView) findViewById(R.id.viewCountTextView);
        followersViewCount = (TextView) findViewById(R.id.followersViewCount);
        vSeparator1 = (View) findViewById(R.id.vSeparator1);
        vSeparator2 = (View) findViewById(R.id.vSeparator2);
        addDraft = (ImageView) findViewById(R.id.addDraft);

        bloggerNameTextView = (TextView) findViewById(R.id.bloggerNameTextView);
        bloggerNameTextView.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name());

        bloggerImageView = (ImageView) findViewById(R.id.bloggerImageView);
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
        }
        bloggerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Bookmarks (0)"));
        tabLayout.addTab(tabLayout.newTab().setText("Published (0)"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        addDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 15) {
                    Intent intent1 = new Intent(BloggerDashboardActivity.this, EditorPostActivity.class);
                    Bundle bundle5 = new Bundle();
                    bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                    bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                    bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_title_placeholder));
                    bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_content_placeholder));
                    bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                    bundle5.putString("from", "DraftListViewActivity");
                    intent1.putExtras(bundle5);
                    startActivity(intent1);
                } else {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("http://www.mycity4kids.com/parenting/admin/setupablog"));
                    startActivity(viewIntent);
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void getBloggerDashboardDetails() {

        showProgressDialog("please wait ...");
       /* BloggerDashboardAndPublishedArticlesController _controller = new BloggerDashboardAndPublishedArticlesController(this, this);
        _controller.getData(AppConstants.GET_BLOGGER_DASHBOARD_REQUEST, 0);*/
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<BloggerDashboardModel> call = bloggerDashboardAPI.getBloggerData("" + SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getId());


        //asynchronous call
        call.enqueue(new Callback<BloggerDashboardModel>() {
                         @Override
                         public void onResponse(Call<BloggerDashboardModel> call, retrofit2.Response<BloggerDashboardModel> response) {
                             int statusCode = response.code();

                             BloggerDashboardModel responseData = (BloggerDashboardModel) response.body();

                             removeProgressDialog();

                             try {
                                 if (responseData.getResponseCode() == 200) {
                                     removeProgressDialog();

                                     if (responseData.getResult().getData().getRank() == 0) {
                                         rankingTextView.setVisibility(View.INVISIBLE);
                                         viewCountTextView.setVisibility(View.INVISIBLE);
                                         followersViewCount.setVisibility(View.INVISIBLE);
                                         vSeparator1.setVisibility(View.INVISIBLE);
                                         vSeparator2.setVisibility(View.INVISIBLE);
                                     } else {
                                         rankingTextView.setVisibility(View.VISIBLE);
                                         viewCountTextView.setVisibility(View.VISIBLE);
                                         followersViewCount.setVisibility(View.VISIBLE);
                                         vSeparator1.setVisibility(View.VISIBLE);
                                         vSeparator2.setVisibility(View.VISIBLE);
                                         rankingTextView.setText("" + responseData.getResult().getData().getRank());
                                         viewCountTextView.setText("" + responseData.getResult().getData().getViews());
                                         followersViewCount.setText("" + responseData.getResult().getData().getFollowers());
                                     }

                                     tabLayout.getTabAt(0).setText("Bookmarks (" + responseData.getResult().getData().getBookmarkCount() + ")");
                                     tabLayout.getTabAt(1).setText("Published (" + responseData.getResult().getData().getArticleCount() + ")");

                                     final BloggerDashboardPagerAdapter adapter = new BloggerDashboardPagerAdapter
                                             (getSupportFragmentManager(), BloggerDashboardActivity.this, responseData.getResult().getData().getBookmarkCount(),
                                                     responseData.getResult().getData().getArticleCount());
                                     viewPager.setAdapter(adapter);
                                     viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                     tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                         @Override
                                         public void onTabSelected(TabLayout.Tab tab) {
                                             viewPager.setCurrentItem(tab.getPosition());
                                         }

                                         @Override
                                         public void onTabUnselected(TabLayout.Tab tab) {

                                         }

                                         @Override
                                         public void onTabReselected(TabLayout.Tab tab) {

                                         }
                                     });
                                 } else if (responseData.getResponseCode() == 400) {
                                     String message = responseData.getResult().getMessage();
                                     if (!StringUtils.isNullOrEmpty(message)) {
                                         showToast(message);
                                     } else {
                                         showToast(getString(R.string.went_wrong));
                                     }
                                 }
                             } catch (Exception e) {
                                 removeProgressDialog();
                                 Crashlytics.logException(e);
                                 Log.d("Exception", Log.getStackTraceString(e));
                                 showToast(getString(R.string.went_wrong));
                             }

                         }


                         @Override
                         public void onFailure(Call<BloggerDashboardModel> call, Throwable t) {

                         }
                     }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBloggerDashboardDetails();
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();

        BloggerDashboardModel responseData;

        if (response == null) {
            showToast("Something went wrong from server");
            removeProgressDialog();
//            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.GET_BLOGGER_DASHBOARD_REQUEST:
           /*     responseData = (BloggerDashboardModel) response.getResponseObject();
                try {
                    if (responseData.getResponseCode() == 200) {
                        removeProgressDialog();

                        if (responseData.getResult().getData().getRank() == 0) {
                            rankingTextView.setVisibility(View.INVISIBLE);
                            viewCountTextView.setVisibility(View.INVISIBLE);
                            followersViewCount.setVisibility(View.INVISIBLE);
                            vSeparator1.setVisibility(View.INVISIBLE);
                            vSeparator2.setVisibility(View.INVISIBLE);
                        } else {
                            rankingTextView.setVisibility(View.VISIBLE);
                            viewCountTextView.setVisibility(View.VISIBLE);
                            followersViewCount.setVisibility(View.VISIBLE);
                            vSeparator1.setVisibility(View.VISIBLE);
                            vSeparator2.setVisibility(View.VISIBLE);
                            rankingTextView.setText("" + responseData.getResult().getData().getRank());
                            viewCountTextView.setText("" + responseData.getResult().getData().getViews());
                            followersViewCount.setText("" + responseData.getResult().getData().getFollowers());
                        }

                        tabLayout.getTabAt(0).setText("Bookmarks (" + responseData.getResult().getData().getBookmarkCount() + ")");
                        tabLayout.getTabAt(1).setText("Published (" + responseData.getResult().getData().getArticleCount() + ")");
                        tabLayout.getTabAt(0).select();
                        final BloggerDashboardPagerAdapter adapter = new BloggerDashboardPagerAdapter
                                (getSupportFragmentManager(), this, responseData.getResult().getData().getBookmarkCount(),
                                        responseData.getResult().getData().getArticleCount());
                        viewPager.setAdapter(adapter);

                    } else if (responseData.getResponseCode() == 400) {
                        String message = responseData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
                        }
                    }
                } catch (Exception e) {
                    removeProgressDialog();
                    e.printStackTrace();
                    showToast(getString(R.string.went_wrong));
                }
                break;

            default:
                break;*/
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BloggerDashboardActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hidefloatingbutton(Boolean b) {
        if (b == true) {
            addDraft.setVisibility(View.INVISIBLE);
        } else {
            addDraft.setVisibility(View.VISIBLE);
        }
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

                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(BloggerDashboardActivity.this.getContentResolver(), imageUri);
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
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        //     byte[] byteArrayFromGallery = stream.toByteArray();

                        //   imageString = Base64.encodeToString(byteArrayFromGallery, Base64.DEFAULT);
                        String path = MediaStore.Images.Media.insertImage(BloggerDashboardActivity.this.getContentResolver(), finalBitmap, "Title", null);
                        Uri imageUriTemp=Uri.parse(path);
                        File file2= FileUtils.getFile(this,imageUriTemp);
                        sendUploadProfileImageRequest(file2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;
        }
    }
    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
       /* originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.STAGING_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
   //     RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "" + userModel.getUser().getId());
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "jpg");
        // prepare call in Retrofit 2.0
        ImageUploadAPI imageUploadAPI = retrofit.create(ImageUploadAPI.class);

        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,
                //  imageType,
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
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getUrl());
                                 }
                                 setProfileImage( responseModel.getData().getUrl());
                              //   setProfileImage(responseModel.getData().getUrl());
                                 Picasso.with(BloggerDashboardActivity.this).load(responseModel.getData().getUrl()).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
                                 showToast("Image successfully uploaded!");
                                 // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {

                         }
                     }
        );

    }
  public void   setProfileImage(String url)
  {
      UpdateUserDetail updateUserDetail=new UpdateUserDetail();
      updateUserDetail.setAttributeName("profilePicUrl");
      updateUserDetail.setAttributeValue(url);
      updateUserDetail.setAttributeType("S");
      Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
      UserAttributeUpdateAPI userAttributeUpdateAPI= retrofit.create(UserAttributeUpdateAPI.class);
      Call<UserDetailResponse> call=userAttributeUpdateAPI.updateProfilePic(updateUserDetail);
      call.enqueue(new Callback<UserDetailResponse>() {
          @Override
          public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
              if (!response.body().getStatus().equals("success"))
              {
                  showToast(getString(R.string.toast_response_error));
              }
          }

          @Override
          public void onFailure(Call<UserDetailResponse> call, Throwable t) {

          }
      });

  }

}
