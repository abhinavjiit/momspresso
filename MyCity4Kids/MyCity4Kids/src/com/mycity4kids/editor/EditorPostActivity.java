package com.mycity4kids.editor;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.request.SaveDraftRequest;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.activity.AddArticleTopicsActivityNew;
import com.mycity4kids.ui.fragment.SpellCheckDialogFragment;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.PermissionUtil;

import org.wordpress.android.editor.EditorFragmentAbstract;
import org.wordpress.android.editor.EditorMediaUploadListener;
import org.wordpress.android.editor.ImageSettingsDialogFragment;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.helpers.MediaFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 2/29/16.
 */
public class EditorPostActivity extends BaseActivity implements EditorFragmentAbstract.EditorFragmentListener, View.OnClickListener, SpellCheckDialogFragment.ISpellcheckResult {

    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static final int REQUEST_INIT_PERMISSION = 1;

    Uri imageUri;
    private String articleId;
    private String thumbnailUrl;
    private String moderation_status, node_id, path;
    String mCurrentPhotoPath, absoluteImagePath;
    File photoFile;

    DraftListResult draftObject;
    File file;
    MediaFile mediaFile;
    String mediaId;
    String response;
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
    private Toolbar mToolbar;
    private View mLayout;
    private String imageSelectorType;

    private ImageView closeEditorImageView;
    private TextView publishTextView;
    private TextView lastSavedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getIntExtra(EDITOR_PARAM, USE_NEW_EDITOR) == USE_NEW_EDITOR) {
            // ToastUtils.showToast(this, R.string.starting_new_editor);
            setContentView(R.layout.activity_new_editor);
            Utils.pushOpenScreenEvent(EditorPostActivity.this, "CreateArticleScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        closeEditorImageView = (ImageView) findViewById(R.id.closeEditorImageView);
        lastSavedTextView = (TextView) findViewById(R.id.lastSavedTextView);
        publishTextView = (TextView) findViewById(R.id.publishTextView);

        closeEditorImageView.setOnClickListener(this);
        publishTextView.setOnClickListener(this);

        if (null != draftObject) {
            try {
                Calendar calendar1 = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
                calendar1.setTimeInMillis(draftObject.getUpdatedTime() * 1000);
                Long diff = System.currentTimeMillis() - draftObject.getUpdatedTime() * 1000;
                if (diff / (1000 * 60 * 60) > 24 && !sdf.format(System.currentTimeMillis()).equals(sdf.format((draftObject.getUpdatedTime() * 1000)))) {
                    lastSavedTextView.setText(getString(R.string.editor_last_saved_on, DateTimeUtils.getDateFromTimestamp(draftObject.getUpdatedTime())));
                } else {
                    lastSavedTextView.setText(getString(R.string.editor_last_saved_at, sdf1.format(calendar1.getTime())));
                }
                lastSavedTextView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }
        mLayout = findViewById(R.id.rootLayout);
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
                super.onBackPressed();
                finish();
            } else if (EditorFragmentAbstract.imageUploading == 0) {
                Log.e("imageuploading", EditorFragmentAbstract.imageUploading + "");
                showToast(getString(R.string.image_upload_wait));
            } else {
                if (!ConnectivityUtils.isNetworkEnabled(this)) {
                    showToast(getString(R.string.error_network));
                    return;
                }
                saveDraftRequest(titleFormatting(mEditorFragment.getTitle().toString().trim()), mEditorFragment.getContent().toString(), draftId);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, SELECT_IMAGE_MENU_POSITION, 0, getString(R.string.select_image));
        menu.add(0, SELECT_IMAGE_CAMERA_MENU_POSITION, 0, getString(R.string.camera_pick));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_PICK);

        switch (item.getItemId()) {
            case SELECT_IMAGE_MENU_POSITION:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_image));
                imageSelectorType = "STORAGE";
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(EditorPostActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(EditorPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(EditorPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                    } else {
                        startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                    }
                } else {
                    startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                }
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
                imageSelectorType = "CAMERA";

                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(EditorPostActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(EditorPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(EditorPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                    } else {
                        loadImageFromCamera();
                    }
                } else {
                    loadImageFromCamera();
                }

                return true;
            default:
                return false;
        }
    }

    private void loadImageFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("TAG", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivityForResult(cameraIntent, ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private void requestPermissions() {
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
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Display a SnackBar with an explanation and a button to trigger the request.
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

    /**
     * Callback received when a permissions request has been completed.
     */
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
                if ("CAMERA".equals(imageSelectorType)) {
                    loadImageFromCamera();
                } else if ("STORAGE".equals(imageSelectorType)) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                dir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        absoluteImagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mediaFile = new MediaFile();
        mediaId = String.valueOf(System.currentTimeMillis());
        mediaFile.setMediaId(mediaId);

        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:

                if (data == null) {
                    return;
                }
                imageUri = data.getData();

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(EditorPostActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        if (actualWidth < 720) {
                            showToast(getString(R.string.upload_min_width));
                            return;
                        }
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
                        String path = MediaStore.Images.Media.insertImage(EditorPostActivity.this.getContentResolver(), finalBitmap, "Title", null);
                        Uri imageUriTemp = Uri.parse(path);
                        EditorFragmentAbstract.imageUploading = 0;
                        File file2 = FileUtils.getFile(this, imageUriTemp);
                        sendUploadProfileImageRequest(file2);
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
//                        imageUri = data.getData();
//                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(mCurrentPhotoPath));
                        ExifInterface ei = new ExifInterface(absoluteImagePath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                imageBitmap = rotateImage(imageBitmap, 90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                imageBitmap = rotateImage(imageBitmap, 180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                imageBitmap = rotateImage(imageBitmap, 270);
                                break;
                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                break;
                        }
//                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data"); //MediaStore.Images.Media.getBitmap(EditorPostActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 1300;
                        float maxWidth = 720;
                        float imgRatio = actualWidth / actualHeight;
                        float maxRatio = maxWidth / maxHeight;
                        // float compressionQuality = 0.5;//50 percent compression
                        if (actualWidth < 720) {
                            showToast(getString(R.string.upload_min_width));
                            return;
                        }
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

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
                        byte[] bitmapData = bytes.toByteArray();

                        //write the bytes in file
                        FileOutputStream fos = new FileOutputStream(photoFile);
                        fos.write(bitmapData);
                        fos.flush();
                        fos.close();
                        imageUri = Uri.fromFile(photoFile);
                        EditorFragmentAbstract.imageUploading = 0;
                        File file2 = FileUtils.getFile(this, imageUri);
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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    public void saveDraftRequest(String title, String body, String draftId1) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDraftAPI articleDraftAPI = retrofit.create(ArticleDraftAPI.class);
        if (StringUtils.isNullOrEmpty(body)) {
            //dynamoDB can't handle empty spaces
            body = " ";
        }
        if (draftId1.isEmpty()) {
            Call<ArticleDraftResponse> call = articleDraftAPI.saveDraft(title, body, "0", null);
            call.enqueue(new Callback<ArticleDraftResponse>() {
                @Override
                public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                    removeProgressDialog();
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
                            //onBackPressed();
                            finish();
                        } else {
                            if (StringUtils.isNullOrEmpty(responseModel.getReason())) {
                                showToast(getString(R.string.toast_response_error));
                            } else {
                                showToast(responseModel.getReason());
                            }
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        showToast(getString(R.string.went_wrong));
                    }
                }

                @Override
                public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
                    Crashlytics.logException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                    showToast(getString(R.string.went_wrong));
                }
            });
        } else {
            SaveDraftRequest saveDraftRequest = new SaveDraftRequest();
            saveDraftRequest.setTitle(title);
            saveDraftRequest.setBody(body);
            saveDraftRequest.setArticleType("0");
            saveDraftRequest.setUserAgent1(AppConstants.ANDROID_NEW_EDITOR);
            Call<ArticleDraftResponse> call = articleDraftAPI.updateDrafts(AppConstants.LIVE_URL + "v1/articles/" + draftId1, saveDraftRequest);
            call.enqueue(new Callback<ArticleDraftResponse>() {
                @Override
                public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                    removeProgressDialog();
                    if (response == null || response.body() == null) {
                        showToast(getString(R.string.went_wrong));
                        return;
                    }
                    try {
                        ArticleDraftResponse responseModel = response.body();
                        if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                            draftId = responseModel.getData().get(0).getResult().getId() + "";
                            showToast(getString(R.string.draft_save_success));
                            finish();
                        } else {
                            if (StringUtils.isNullOrEmpty(responseModel.getReason())) {
                                showToast(getString(R.string.toast_response_error));
                            } else {
                                showToast(responseModel.getReason());
                            }
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        showToast(getString(R.string.went_wrong));
                    }
                }

                @Override
                public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
                    Crashlytics.logException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                    showToast(getString(R.string.went_wrong));
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
            draftObject = (DraftListResult) getIntent().getSerializableExtra("draftItem");
            title = draftObject.getTitle();
            title = title.trim();
            content = draftObject.getBody();
            draftId = draftObject.getId();

            if (StringUtils.isNullOrEmpty(moderation_status)) {
                moderation_status = "0";
            }
            mEditorFragment.setTitle(title);
            mEditorFragment.setContent(content);

        } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList")) {
            title = getIntent().getStringExtra("title");
            title = title.trim();
            content = getIntent().getStringExtra("content");
            tag = getIntent().getStringExtra("tag");
            cities = getIntent().getStringExtra("cities");
            thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
            articleId = getIntent().getStringExtra("articleId");
            mEditorFragment.setTitle(title);
            mEditorFragment.setContent(content);
        } else {
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
        String htmlStrippedTitle = Html.fromHtml(title).toString();
        return htmlStrippedTitle;


    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
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
                             removeProgressDialog();
                             if (response == null || response.body() == null) {
                                 showToast(getString(R.string.went_wrong));
                                 return;
                             }
                             ImageUploadResponse responseModel = response.body();
                             Log.e("responseURL", responseModel.getData().getResult().getUrl());

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
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             removeProgressDialog();
                             Crashlytics.logException(t);
                             Toast.makeText(EditorPostActivity.this, "Error while uploading image", Toast.LENGTH_SHORT).show();
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );


    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeEditorImageView:
                onBackPressed();
                break;
            case R.id.publishTextView:
                if (mEditorFragment.getTitle().toString().isEmpty()) {
                    showToast(getString(R.string.editor_title_empty));
                } else if (mEditorFragment.getTitle().toString().length() > 150) {
                    showToast(getString(R.string.editor_title_char_limit));
                } else if (mEditorFragment.getContent().toString().isEmpty()) {
                    showToast(getString(R.string.editor_body_empty));
                } else if (mEditorFragment.getContent().toString().replace("&nbsp;", " ").split("\\s+").length < 299 && !BuildConfig.DEBUG) {
                    showToast(getString(R.string.editor_body_min_words));
                } else if (EditorFragmentAbstract.imageUploading == 0) {
                    Log.e("imageuploading", EditorFragmentAbstract.imageUploading + "");
                    showToast(getString(R.string.image_upload_wait));
                } else {
                    if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList")) {
                        launchSpellCheckDialog();
                    } else {
                        saveDraftBeforePublishRequest(titleFormatting(mEditorFragment.getTitle().toString().trim()), mEditorFragment.getContent().toString(), draftId);
                    }
                }
                break;
        }
    }

    private void saveDraftBeforePublishRequest(String title, String body, String draftId1) {
        showProgressDialog(getResources().getString(R.string.please_wait));

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI articleDraftAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        if (draftId1.isEmpty()) {
            Call<ArticleDraftResponse> call = articleDraftAPI.saveDraft(title, body, "0", null);
            call.enqueue(saveDraftBeforePublishResponseListener);
        } else {
            SaveDraftRequest saveDraftRequest = new SaveDraftRequest();
            saveDraftRequest.setTitle(title);
            saveDraftRequest.setBody(body);
            saveDraftRequest.setArticleType("0");
            saveDraftRequest.setUserAgent1(AppConstants.ANDROID_NEW_EDITOR);
            Call<ArticleDraftResponse> call = articleDraftAPI.updateDrafts(AppConstants.LIVE_URL + "v1/articles/" + draftId1, saveDraftRequest);
            call.enqueue(saveDraftBeforePublishResponseListener);
        }

    }

    private Callback<ArticleDraftResponse> saveDraftBeforePublishResponseListener = new Callback<ArticleDraftResponse>() {
        @Override
        public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                ArticleDraftResponse responseModel = response.body();
                if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                    draftId = responseModel.getData().get(0).getResult().getId() + "";
                    launchSpellCheckDialog();
                } else {
                    if (StringUtils.isNullOrEmpty(responseModel.getReason())) {
                        showToast(getString(R.string.toast_response_error));
                    } else {
                        showToast(responseModel.getReason());
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    public void launchSpellCheckDialog() {
        SpellCheckDialogFragment spellCheckDialogFragment = new SpellCheckDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("activity", "dashboard");
        spellCheckDialogFragment.setArguments(_args);
        spellCheckDialogFragment.setCancelable(true);
        spellCheckDialogFragment.show(fm, "Spell Check");
    }

    @Override
    public void onContinuePublish() {
        PublishDraftObject publishObject = new PublishDraftObject();

        publishObject.setBody(contentFormatting(mEditorFragment.getContent().toString()));
        publishObject.setTitle(titleFormatting(mEditorFragment.getTitle().toString().trim()));
        Log.d("draftId = ", draftId + "");
        if ((getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("publishedList")) || ("4".equals(moderation_status))) {
            // coming from edit published articles
            Intent intent_1 = new Intent(EditorPostActivity.this, AddArticleTopicsActivityNew.class);
            publishObject.setId(articleId);
            intent_1.putExtra("draftItem", publishObject);
            intent_1.putExtra("imageUrl", thumbnailUrl);
            intent_1.putExtra("from", "publishedList");
            intent_1.putExtra("articleId", articleId);
            intent_1.putExtra("tag", tag);
            intent_1.putExtra("cities", cities);
            startActivity(intent_1);
        } else {
            Intent intent_3 = new Intent(EditorPostActivity.this, AddArticleTopicsActivityNew.class);
            if (!StringUtils.isNullOrEmpty(draftId)) {
                publishObject.setId(draftId);
            }
            intent_3.putExtra("draftItem", publishObject);
            intent_3.putExtra("from", "editor");
            startActivity(intent_3);
        }
    }
}
