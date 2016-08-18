package com.mycity4kids.editor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.activity.AddArticleTopicsActivity;
import com.mycity4kids.ui.activity.EditSelectedTopicsActivity;

import org.wordpress.android.editor.EditorFragmentAbstract;
import org.wordpress.android.editor.EditorMediaUploadListener;
import org.wordpress.android.editor.ImageSettingsDialogFragment;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.helpers.MediaFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anshul on 2/29/16.
 */
public class EditorPostActivity extends BaseActivity implements EditorFragmentAbstract.EditorFragmentListener {

    private UserModel userModel;
    Uri imageUri;
    private String articleId;
    private String thumbnailUrl;
    private String moderation_status, node_id, path;

    File file;
    MediaFile mediaFile;
    String mediaId;
    String response;
    Boolean fromBackpress = false;
    String draftId = "";
    public static final String EDITOR_PARAM = "EDITOR_PARAM";
    public static final String TITLE_PARAM = "TITLE_PARAM";
    public static final String CONTENT_PARAM = "CONTENT_PARAM";
    public static final String DRAFT_PARAM = "DRAFT_PARAM";
    public static final String TITLE_PLACEHOLDER_PARAM = "TITLE_PLACEHOLDER_PARAM";
    public static final String CONTENT_PLACEHOLDER_PARAM = "CONTENT_PLACEHOLDER_PARAM";
    public static final int USE_NEW_EDITOR = 1;
    public static final int USE_LEGACY_EDITOR = 2;

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE = 1112;
    public static final int ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113;
    public static final String MEDIA_REMOTE_ID_SAMPLE = "123";

    private static final int SELECT_IMAGE_MENU_POSITION = 0;
    private static final int SELECT_IMAGE_FAIL_MENU_POSITION = 1;
    private static final int SELECT_VIDEO_MENU_POSITION = 2;
    private static final int SELECT_VIDEO_FAIL_MENU_POSITION = 3;
    private static final int SELECT_IMAGE_CAMERA_MENU_POSITION = 4;

    private EditorFragmentAbstract mEditorFragment;

    private Map<String, String> mFailedUploads;
    String title;
    String content;
    private String tag, cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getIntExtra(EDITOR_PARAM, USE_NEW_EDITOR) == USE_NEW_EDITOR) {
            // ToastUtils.showToast(this, R.string.starting_new_editor);
            setContentView(R.layout.activity_new_editor);
            UserTable userTable = new UserTable((BaseApplication) this.getApplication());
            userModel = userTable.getAllUserData();
            Utils.pushOpenScreenEvent(EditorPostActivity.this, "Text Editor", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        }
        mFailedUploads = new HashMap<>();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof EditorFragmentAbstract) {
            mEditorFragment = (EditorFragmentAbstract) fragment;
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("title", mEditorFragment.getTitle().toString());
        Fragment fragment = getFragmentManager()
                .findFragmentByTag(ImageSettingsDialogFragment.IMAGE_SETTINGS_DIALOG_TAG);
        if (fragment != null && fragment.isVisible()) {
            ((ImageSettingsDialogFragment) fragment).dismissFragment();
        } else {
            //Toast.makeText(this,"Draft Saved",Toast.LENGTH_LONG).show();
            if ((mEditorFragment.getTitle().toString().isEmpty() && (mEditorFragment.getContent().toString().isEmpty())) || (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList"))) {
                finish();
                super.onBackPressed();

            } else if (mEditorFragment.imageUploading == 0) {
                Log.e("imageuploading", mEditorFragment.imageUploading + "");
                showToast("Please wait while image is being uploaded");
            } else {
                fromBackpress = true;
                saveDraftRequest(titleFormatting(mEditorFragment.getTitle().toString().trim()), mEditorFragment.getContent().toString(), draftId);

            }
            // super.onBackPressed();

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, SELECT_IMAGE_MENU_POSITION, 0, getString(R.string.select_image));
      /*  menu.add(0, SELECT_IMAGE_FAIL_MENU_POSITION, 0, getString(R.string.select_image_fail));
        menu.add(0, SELECT_VIDEO_MENU_POSITION, 0, getString(R.string.select_video));
        menu.add(0, SELECT_VIDEO_FAIL_MENU_POSITION, 0, getString(R.string.select_video_fail));*/
        menu.add(0, SELECT_IMAGE_CAMERA_MENU_POSITION, 0, getString(R.string.camera_pick));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_PICK);

        switch (item.getItemId()) {
            case SELECT_IMAGE_MENU_POSITION:
                intent.setType("image/*");
              /*  intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_image));*/

                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);

                return true;
            case SELECT_IMAGE_FAIL_MENU_POSITION:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_image_fail));

                startActivityForResult(intent, ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE);
                return true;
            case SELECT_VIDEO_MENU_POSITION:
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_video));

                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                return true;
            case SELECT_VIDEO_FAIL_MENU_POSITION:
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_video_fail));

                startActivityForResult(intent, ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE);
                return true;
            case SELECT_IMAGE_CAMERA_MENU_POSITION:
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               /* String filename = "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                file = new File(Environment.getExternalStorageDirectory(),
                        filename);
                imageUri = Uri.fromFile(file);*/
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

                File output = new File(dir, "CameraContentDemo.jpeg");
                imageUri = Uri.fromFile(output);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

/*
                intent1.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                intent1.putExtra("return-data", true);*/
if(intent1.resolveActivity(getPackageManager()) != null)
{
                startActivityForResult(intent1, ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE);}
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }


        mediaFile = new MediaFile();
        mediaId = String.valueOf(System.currentTimeMillis());
        mediaFile.setMediaId(mediaId);
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
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(EditorPostActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 1300;
                        float maxWidth = 720;
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
                        Bitmap finalBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) actualWidth, (int) actualHeight, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        byte[] byteArrayFromGallery = stream.toByteArray();
                        //    byteArrayToSend = byteArrayFromGallery;
                        //    imageString = Base64.encodeToString(byteArrayToSend, Base64.DEFAULT);
                        String path = MediaStore.Images.Media.insertImage(EditorPostActivity.this.getContentResolver(), finalBitmap, "Title", null);
                        Uri imageUriTemp = Uri.parse(path);
                        mEditorFragment.imageUploading = 0;
                        //new FileUploadTask().execute();
                        File file2 = FileUtils.getFile(this, imageUriTemp);
                        sendUploadProfileImageRequest(file2);
                        // compressImage(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    mEditorFragment.appendMediaFile(mediaFile, imageUri.toString(), null);

                    if (mEditorFragment instanceof EditorMediaUploadListener) {
                        //  simulateFileUpload(mediaId, imageUri.toString());
                    }
                }
                break;
            case ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    try {

                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(EditorPostActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 1300;
                        float maxWidth = 700;
                        float imgRatio = actualWidth / actualHeight;
                        float maxRatio = maxWidth / maxHeight;
                        // float compressionQuality = 0.5;//50 percent compression

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

                        Bitmap finalBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) actualWidth, (int) actualHeight, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        byte[] byteArrayFromGallery = stream.toByteArray();
                        //    byteArrayToSend = byteArrayFromGallery;
                        //    imageString = Base64.encodeToString(byteArrayToSend, Base64.DEFAULT);
                        String path = MediaStore.Images.Media.insertImage(EditorPostActivity.this.getContentResolver(), finalBitmap, "Title", null);
                        Uri imageUriTemp = Uri.parse(path);
                        mEditorFragment.imageUploading = 0;
                        //new FileUploadTask().execute();
                        File file2 = FileUtils.getFile(this, imageUriTemp);
                        sendUploadProfileImageRequest(file2);
                        // compressImage(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  decodeFile(picturePath);

                    mEditorFragment.appendMediaFile(mediaFile, imageUri.toString(), null);

                    if (mEditorFragment instanceof EditorMediaUploadListener) {
                        //  simulateFileUpload(mediaId, imageUri.toString());
                    }
                }
                break;

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.draft: {
                if (mEditorFragment.getTitle().toString().isEmpty() && mEditorFragment.getContent().toString().isEmpty()) {
                    showToast("There is nothing to save in draft");
                } else if (mEditorFragment.imageUploading == 0) {
                    Log.e("imageuploading", mEditorFragment.imageUploading + "");
                    showToast("Please wait while image is being uploaded");
                } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList")) {
                    showToast("Published Articles are not allowed to be saved in drafts");
                } else {
                    Log.e("draftId", draftId + "");
                    saveDraftRequest(titleFormatting(mEditorFragment.getTitle().toString().trim()), mEditorFragment.getContent().toString(), draftId);
                    fromBackpress = false;
                }
            }
            break;
            case R.id.publish: {
                if (mEditorFragment.getTitle().toString().isEmpty()) {
                    showToast("Title can't be empty");
                } else if (mEditorFragment.getContent().toString().isEmpty()) {
                    showToast("Body can't be empty");
                } else if (mEditorFragment.getContent().toString().split(" ").length < 300) {
                    showToast("Please write atleast 300 words to publish");
                } else if (mEditorFragment.imageUploading == 0) {
                    Log.e("imageuploading", mEditorFragment.imageUploading + "");
                    showToast("Please wait while image is being uploaded");
                } else {

                    PublishDraftObject draftObject = new PublishDraftObject();

                    draftObject.setBody(contentFormatting(mEditorFragment.getContent().toString()));
                    draftObject.setTitle(titleFormatting(mEditorFragment.getTitle().toString()));
                    //  draftObject.setId(draftId);
//                    draftObject.setImageUrl(thumbnailUrl);
                    //  draftObject.setArticleType(moderation_status);
                    Log.d("draftId = ", draftId + "");

//                    Intent intent = new Intent(EditorPostActivity.this, ArticleImageTagUploadActivity.class);
//                    intent.putExtra("draftItem", draftObject);
                    if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList")) {
                        // coming from edit published articles
//
                        Intent intent_1 = new Intent(EditorPostActivity.this, EditSelectedTopicsActivity.class);
                        draftObject.setId(articleId);
                        intent_1.putExtra("draftItem", draftObject);
                        intent_1.putExtra("imageUrl", thumbnailUrl);
                        intent_1.putExtra("from", "publishedList");
                        intent_1.putExtra("articleId", articleId);
                        intent_1.putExtra("tag", tag);
                        intent_1.putExtra("cities", cities);
                        startActivity(intent_1);
//                        finish();
                    }
//                    else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("draftList")) {
//                        Intent intent_2 = new Intent(EditorPostActivity.this, ArticleImageTagUploadActivity.class);
//                        intent_2.putExtra("draftItem", draftObject);
//                        intent_2.putExtra("imageUrl", path);
//                        intent_2.putExtra("from", "draftList");
//                    }
                    else {
                        Intent intent_3 = new Intent(EditorPostActivity.this, AddArticleTopicsActivity.class);

                        //          draftObject.setId(draftId);

                        intent_3.putExtra("draftItem", draftObject);
                        intent_3.putExtra("from", "editor");
                        startActivity(intent_3);
                    }
//                    startActivity(intent);

//                    Intent intent = new Intent(EditorPostActivity.this, ArticleImageTagUploadActivity.class);
//                    intent.putExtra("draftItem", draftObject);
//                    if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList")) {
//                        intent.putExtra("imageUrl", thumbnailUrl);
//                        intent.putExtra("from", "publishedList");
//                        intent.putExtra("articleId", articleId);
//                    } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("draftList")) {
//                        intent.putExtra("imageUrl", path);
//                        intent.putExtra("from", "draftList");
//                    } else {
//                        intent.putExtra("from", "editor");
//                    }
//                    startActivity(intent);
                }
            }
            break;
        }
        return super.onOptionsItemSelected(item);

    }


    public void saveDraftRequest(String title, String body, String draftId1) {
        showProgressDialog(getResources().getString(R.string.please_wait));

/*
        ArticleDraftRequest requestData = new ArticleDraftRequest();
        title = title.trim();
        requestData.setUser_id("" + userModel.getUser().getId());
        requestData.setBody("" + body);
        requestData.setTitle("" + title);
        requestData.setId("" + draftId1);
        requestData.setSourceId("" + 2);

        Log.e("userId", userModel.getUser().getId() + "");
        ArticleDraftController controller = new ArticleDraftController(this, this);
        controller.getData(AppConstants.ARTICLE_DRAFT_REQUEST, requestData);*/
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI articleDraftAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        if (draftId1.isEmpty()) {
            Call<ArticleDraftResponse> call = articleDraftAPI.saveDraft(
                    title,
                    body,
                    "0"
            );


            //asynchronous call
            call.enqueue(new Callback<ArticleDraftResponse>() {
                             @Override
                             public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                                 int statusCode = response.code();

                                 ArticleDraftResponse responseModel = (ArticleDraftResponse) response.body();
                                 // Result<ArticleDraftResult> result=responseModel.getData().getResult();
                                 removeProgressDialog();

                                 if (responseModel.getCode() != 200) {
                                     showToast(getString(R.string.toast_response_error));
                                     return;
                                 } else {
                                     if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                         //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                                         Log.i("Draft message", responseModel.getData().getMsg());
                                     }
                                     draftId = responseModel.getData().getResult().getId() + "";

                                     //setProfileImage(originalImage);
                                     showToast("Draft Successfully saved");
                                     if (fromBackpress) {
                                         //onBackPressed();
                                         finish();
                                     }
                                     //  finish();
                                 }

                             }


                             @Override
                             public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {

                             }
                         }
            );
        } else {

            Call<ArticleDraftResponse> call = articleDraftAPI.updateDraft(
                    AppConstants.LIVE_URL + "v1/articles/" + draftId1,

                    title,
                    body,
                    "0"
            );
            call.enqueue(new Callback<ArticleDraftResponse>() {
                @Override
                public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                    int statusCode = response.code();

                    ArticleDraftResponse responseModel = (ArticleDraftResponse) response.body();
                    // Result<ArticleDraftResult> result=responseModel.getData().getResult();
                    removeProgressDialog();
                    if (response == null || response.body() == null) {
                        showToast(getString(R.string.went_wrong));
                        return;
                    }
                    if (responseModel.getCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                            //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Draft message", responseModel.getData().getMsg());
                        }
                        draftId = responseModel.getData().getResult().getId() + "";

                        //setProfileImage(originalImage);
                        showToast("Draft Successfully saved");
                        if (fromBackpress) {
                            //onBackPressed();
                            finish();
                        }
                        //  finish();
                    }
                }

                @Override
                public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {

                }
            });
        }

    }

    @Override
    public void onSettingsClicked() {
        // TODO
    }

    @Override
    public void onAddMediaClicked() {
        // TODO
    }

    @Override
    public void onMediaRetryClicked(String mediaId) {
        if (mFailedUploads.containsKey(mediaId)) {
            // simulateFileUpload(mediaId, mFailedUploads.get(mediaId));
        }
    }

    @Override
    public void onMediaUploadCancelClicked(String mediaId, boolean delete) {

    }

    @Override
    public void onFeaturedImageChanged(int mediaId) {

    }

    @Override
    public void onVideoPressInfoRequested(String videoId) {

    }

    @Override
    public String onAuthHeaderRequested(String url) {
        return "";
    }

    @Override
    public void onEditorFragmentInitialized() {
        // arbitrary setup
        mEditorFragment.setFeaturedImageSupported(true);
        mEditorFragment.setBlogSettingMaxImageWidth("600");
        mEditorFragment.setDebugModeEnabled(true);

        // get title and content and draft switch
        title = getIntent().getStringExtra(TITLE_PARAM);
        content = getIntent().getStringExtra(CONTENT_PARAM);
        boolean isLocalDraft = getIntent().getBooleanExtra(DRAFT_PARAM, true);
        if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("draftList")) {
            PublishDraftObject draftObject = (PublishDraftObject) getIntent().getSerializableExtra("draftItem");
            title = draftObject.getTitle();
            title = title.trim();
            content = draftObject.getBody();
            draftId = draftObject.getId();
            //  path = draftObject.getPath();
            //  moderation_status = draftObject.getModeration_status();
            if (null == moderation_status) {
                moderation_status = "0";
            }
            Log.e("moderation_status", "" + moderation_status);
            // node_id = draftObject.getNode_id();
            mEditorFragment.setTitle(title);
            mEditorFragment.setContent(content);
            if (null == moderation_status) {
            }
          /*  if (moderation_status.equals("3")) {
                mEditorFragment.toggleTitleView(true);
            }*/
        } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList")) {
            //  PublishedArticlesModel.PublishedArticleData draftObject=(PublishedArticlesModel.PublishedArticleData) getIntent().getSerializableExtra("publishedItem");
            title = getIntent().getStringExtra("title");
            title = title.trim();
            content = getIntent().getStringExtra("content");
            tag = getIntent().getStringExtra("tag");
            cities = getIntent().getStringExtra("cities");
            thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
            articleId = getIntent().getStringExtra("articleId");
            mEditorFragment.setTitle(title);
            mEditorFragment.setContent(content);
            //  mEditorFragment.toggleTitleView(true);
            //  mEditorFragment.setTitle(title);
            //    mEditorFragment.setContent(content);
        } else /*if (getIntent().getStringExtra("from").equals("dashboard"))*/ {
            title = title.trim();
            mEditorFragment.setTitle(title);
            mEditorFragment.setContent(content);
            Log.e("postContent", content);
            mEditorFragment.setTitlePlaceholder(getIntent().getStringExtra(TITLE_PLACEHOLDER_PARAM));
            mEditorFragment.setContentPlaceholder(getIntent().getStringExtra(CONTENT_PLACEHOLDER_PARAM));
            mEditorFragment.setLocalDraft(isLocalDraft);
        }
    }

    @Override
    public void saveMediaFile(MediaFile mediaFile) {
        // TODO
    }

    @Override
    public void onTrackableEvent(EditorFragmentAbstract.TrackableEvent event) {
        AppLog.d(AppLog.T.EDITOR, "Trackable event: " + event);
    }


    public String contentFormatting(String content) {

        String pTag = "<p>";
        String newString = pTag.concat(content);
        String formattedString = newString.replace("\n\n", "</p><p>");
        formattedString = formattedString.concat("</p>");
        return formattedString;

    }

    public String titleFormatting(String title) {
        return title.replace("&nbsp;", "");


    }

    //public void sendUploadProfileImageRequest(Bitmap originalImage) {
    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
       /* ByteArrayOutputStream bao = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);*/
  /*      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient
                .Builder()
                .cache(new Cache(getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .addInterceptor(logging)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.LIVE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();*/
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), //"" + userModel.getUser().getId());
                0 + "");
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "2");
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ImageUploadAPI imageUploadAPI = retro.create(ImageUploadAPI.class);

        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,
                imageType,
                requestBodyFile);
        //asynchronous call
        call.enqueue(new Callback<ImageUploadResponse>() {
                         @Override
                         public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
                             if (response == null || response.body() == null) {
                                 showToast(getString(R.string.went_wrong));
                                 return;
                             }
                             ImageUploadResponse responseModel = response.body();
                             Log.e("responseURL", responseModel.getData().getResult().getUrl());
                             removeProgressDialog();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 removeProgressDialog();
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     //      SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                                     Log.i("Uploaded Image URL", responseModel.getData().getResult().getUrl());
                                 }
                                 mediaFile.setFileURL(responseModel.getData().getResult().getUrl());

                                 ((EditorMediaUploadListener) mEditorFragment).onMediaUploadSucceeded(mediaId, mediaFile);
                                 removeProgressDialog();
                                 //setProfileImage(originalImage);
                                 //  showToast("You have successfully uploaded image.");
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             t.printStackTrace();
                             Log.e("infailure", "test");
                         }
                     }
        );


    }

    @Override
    protected void updateUi(Response response) {

    }
}
