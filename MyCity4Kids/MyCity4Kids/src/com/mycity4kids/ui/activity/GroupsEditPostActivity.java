package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.FragmentManager;
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
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.request.UpdatePostContentRequest;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.fragment.TaskFragment;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.PermissionUtil;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.mycity4kids.ui.adapter.BusinessListingAdapterevent.REQUEST_INIT_PERMISSION;

/**
 * Created by hemant on 14/8/18.
 */

public class GroupsEditPostActivity extends BaseActivity implements View.OnClickListener, TaskFragment.TaskCallbacks {

    private EditText postContentEditText;
    private ImageView closeEditorImageView;
    private GroupPostResult postData;
    private TextView publishTextView;
    private RelativeLayout root;
    private LinearLayout mediaContainer;
    private LinkedHashMap<ImageView, String> imageUrlHashMap = new LinkedHashMap<>();
    private ArrayList<String> images = new ArrayList<>();
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    public static final int ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE = 1112;
    private TaskFragment mTaskFragment;
    private GroupResult selectedGroup;
    private Uri imageUri;
    private File photoFile;
    static int count;
    private String mCurrentPhotoPath, absoluteImagePath;
    private View mLayout;
    private ImageView addMediaImageView, anonymousImageView;
    private ImageView postImageView;
    private TextView imageCameraTextView, imageGalleryTextView, cancelTextView;
    private RelativeLayout chooseMediaTypeContainer;
    private TextView anonymousTextView;
    private CheckBox anonymousCheckbox;
    private TextView addMediaTextView;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_edit_post_activity);
        root = findViewById(R.id.rootLayout);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);
        mediaContainer = (LinearLayout) findViewById(R.id.mediaContainer);
        postContentEditText = (EditText) findViewById(R.id.postContentEditText);
        closeEditorImageView = (ImageView) findViewById(R.id.closeEditorImageView);
        publishTextView = (TextView) findViewById(R.id.publishTextView);
        mLayout = findViewById(R.id.rootLayout);
        anonymousImageView = (ImageView) findViewById(R.id.anonymousImageView);
        anonymousTextView = (TextView) findViewById(R.id.anonymousTextView);
        anonymousCheckbox = (CheckBox) findViewById(R.id.anonymousCheckbox);
        addMediaImageView = (ImageView) findViewById(R.id.addMediaImageView);
        addMediaTextView = (TextView) findViewById(R.id.addMediaTextView);
        postImageView = (ImageView) findViewById(R.id.postImageView);
        chooseMediaTypeContainer = (RelativeLayout) findViewById(R.id.chooseMediaTypeContainer);
        imageCameraTextView = (TextView) findViewById(R.id.imageCameraTextView);
        imageGalleryTextView = (TextView) findViewById(R.id.imageGalleryTextView);
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);

        selectedGroup = (GroupResult) getIntent().getParcelableExtra("groupItem");

        closeEditorImageView.setOnClickListener(this);
        publishTextView.setOnClickListener(this);

        postData = getIntent().getParcelableExtra("postData");

        postContentEditText.setText(postData.getContent());

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        chooseMediaTypeContainer.setOnClickListener(this);
        addMediaImageView.setOnClickListener(this);
        addMediaTextView.setOnClickListener(this);
        anonymousImageView.setOnClickListener(this);
        anonymousTextView.setOnClickListener(this);
        anonymousCheckbox.setOnClickListener(this);
        publishTextView.setOnClickListener(this);
        imageCameraTextView.setOnClickListener(this);
        imageGalleryTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        closeEditorImageView.setOnClickListener(this);

        for (Map.Entry<String, String> entry : postData.getMediaUrls().entrySet()) {
            images.add(entry.getValue());
        }

        for (int i = 0; i < images.size(); i++) {
            addImageToContainer(images.get(i));
        }

        postContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    publishTextView.setEnabled(false);
                } else {
                    publishTextView.setEnabled(true);
                }
            }
        });

        if (SharedPrefUtils.isUserAnonymous(this)) {
            anonymousCheckbox.setChecked(true);
        } else {
            anonymousCheckbox.setChecked(false);
        }
    }

    private void addImageToContainer(String url) {
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.image_post_upload_item, null);
        ImageView uploadedIV = (ImageView) rl.findViewById(R.id.addImageOptionImageView);
        final ImageView removeIV = (ImageView) rl.findViewById(R.id.removeItemImageView);
        mediaContainer.addView(rl);
        imageUrlHashMap.put(removeIV, url);
        Picasso.with(GroupsEditPostActivity.this).load(url).error(R.drawable.default_article).into(uploadedIV);
        removeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrlHashMap.remove(removeIV);
                mediaContainer.removeView((View) removeIV.getParent());
            }
        });
    }


    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeEditorImageView:
                onBackPressed();
                break;
            case R.id.publishTextView:
                Log.d("publish", "publish");

                Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                UpdatePostContentRequest updatePostRequest = new UpdatePostContentRequest();
                updatePostRequest.setContent(postContentEditText.getText().toString());
                if("1".equals(postData.getType())){
                    updatePostRequest.setType("0");
                }else{
                    updatePostRequest.setType(postData.getType());
                }

                LinkedHashMap<String, String> mediaMap = new LinkedHashMap<>();
                int i = 1;
                if (!imageUrlHashMap.isEmpty()) {
                    for (Map.Entry<ImageView, String> entry : imageUrlHashMap.entrySet()) {
                        mediaMap.put("image" + i, entry.getValue());
                        i++;
                    }
//                    updatePostRequest.setMediaUrls(mediaMap);
                    updatePostRequest.setType("1");
                }
                updatePostRequest.setMediaUrls(mediaMap);
                Call<GroupPostResponse> call = groupsAPI.updatePostContent(postData.getId(), updatePostRequest);
                call.enqueue(updatePostContentResponseCallback);
                break;
            case R.id.cancelTextView:
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.chooseMediaTypeContainer:
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.addMediaTextView:
            case R.id.addMediaImageView:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                    } else {
                        openMediaChooserDialog();
                    }
                } else {
                    openMediaChooserDialog();
                }
                break;
            case R.id.imageCameraTextView:
                loadImageFromCamera();
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.imageGalleryTextView:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE);
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.videoCameraTextView:
                Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.videoGalleryTextView:
                pickVideoFromGallery();
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.anonymousImageView:
            case R.id.anonymousTextView:
            case R.id.anonymousCheckbox:
                if (anonymousCheckbox.isChecked()) {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
                } else {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
                }

                SharedPrefUtils.toolTipChecking(this, 3);


                break;
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
                openMediaChooserDialog();
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

    private void openMediaChooserDialog() {
        chooseMediaTypeContainer.setVisibility(View.VISIBLE);
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
                startActivityForResult(cameraIntent, ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE);
            }
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

    private void pickVideoFromGallery() {
        try {
            Intent intent = new Intent();
            intent.setType("video/mp4");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                imageUri = data.getData();
                if (resultCode == Activity.RESULT_OK) {
                    try {
//                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(AddTextOrMediaGroupPostActivity.this.getContentResolver(), imageUri);
                        // If the Fragment is non-null, then it is currently being
                        // retained across a configuration change.
                        FragmentManager fm = getFragmentManager();
                        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
                        if (mTaskFragment == null) {
                            mTaskFragment = new TaskFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("uri", imageUri);
                            mTaskFragment.setArguments(bundle);
                            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
                        } else {
                            mTaskFragment.launchNewTask(imageUri);
                        }
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
//                        String path = MediaStore.Images.Media.insertImage(AddTextOrMediaGroupPostActivity.this.getContentResolver(), imageBitmap, "Title", null);
//                        Uri imageUriTemp = Uri.parse(path);
//                        File file2 = FileUtils.getFile(this, imageUriTemp);
//                        sendUploadProfileImageRequest(file2);
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
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

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
                        byte[] bitmapData = bytes.toByteArray();

                        //write the bytes in file
                        FileOutputStream fos = new FileOutputStream(photoFile);
                        fos.write(bitmapData);
                        fos.flush();
                        fos.close();
                        imageUri = Uri.fromFile(photoFile);
                        File file2 = FileUtils.getFile(this, imageUri);
                        sendUploadProfileImageRequest(file2);
                        // compressImage(filePath);
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

    //    private void startCropActivity(@NonNull Uri uri) {
//        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
//        Log.e("instartCropActivity", "test");
//        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
//        uCrop.withAspectRatio(16, 9);
//        uCrop.withMaxResultSize(720, 405);
//        uCrop.start(AddTextOrMediaGroupPostActivity.this);
//    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private Callback<GroupPostResponse> updatePostContentResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    postData = groupPostResponse.getData().get(0).getResult().get(0);
                    Intent intent = getIntent();
                    intent.putExtra("editedPost", postData);
                    intent.putExtra("updatedContent", groupPostResponse.getData().get(0).getResult().get(0).getContent());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        Log.e("requestBodyFile", new Gson().toJson(requestBodyFile.toString()));
        //   RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "" + userModel.getUser().getId());
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "2");
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

                             file.delete();

                             ImageUploadResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                                 }

                                 addImageToContainer(responseModel.getData().getResult().getUrl());
//                                 Picasso.with(AddTextOrMediaGroupPostActivity.this).load(responseModel.getData().getResult().getUrl()).error(R.drawable.default_article).into(postImageView);
//                                 postImageView.setVisibility(View.VISIBLE);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != postContentEditText.getText() && !StringUtils.isNullOrEmpty(postContentEditText.getText().toString()) && selectedGroup != null) {
            SharedPrefUtils.setSavedPostData(GroupsEditPostActivity.this, selectedGroup.getId(), postContentEditText.getText().toString());
        }
    }

    @Override
    public void onPreExecute() {
        showProgressDialog(getString(R.string.please_wait));
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(Bitmap image) {
        String path = MediaStore.Images.Media.insertImage(GroupsEditPostActivity.this.getContentResolver(), image, "Title", null);
        Uri imageUriTemp = Uri.parse(path);
        File file2 = FileUtils.getFile(this, imageUriTemp);
        sendUploadProfileImageRequest(file2);
        mTaskFragment = null;
    }

}
