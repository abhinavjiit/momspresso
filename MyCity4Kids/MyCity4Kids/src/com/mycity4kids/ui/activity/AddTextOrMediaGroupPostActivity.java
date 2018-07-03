package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.fragment.ChooseAnonymousDialogFragment;
import com.mycity4kids.utils.PermissionUtil;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.wordpress.android.editor.EditorFragmentAbstract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/4/18.
 */

public class AddTextOrMediaGroupPostActivity extends BaseActivity implements View.OnClickListener {

    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

//    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private static final int REQUEST_INIT_PERMISSION = 1;

    public static final int ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE = 1112;

    private GroupResult selectedGroup;
    private HashMap<ImageView, String> imageUrlHashMap = new HashMap<>();
    private Uri imageUri;
    private File photoFile;
    private String mCurrentPhotoPath, absoluteImagePath;

    private View mLayout;
    private EditText postContentEditText;
    private ImageView addMediaImageView, anonymousImageView;
    private ImageView postImageView;
    private TextView publishTextView;
    private TextView imageCameraTextView, imageGalleryTextView, videoCameraTextView, videoGalleryTextView, cancelTextView;
    private RelativeLayout chooseMediaTypeContainer;
    private LinearLayout mediaContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_text_group_post_activity);
        mLayout = findViewById(R.id.rootLayout);
        postContentEditText = (EditText) findViewById(R.id.postContentEditText);
        anonymousImageView = (ImageView) findViewById(R.id.anonymousImageView);
        addMediaImageView = (ImageView) findViewById(R.id.addMediaImageView);
        postImageView = (ImageView) findViewById(R.id.postImageView);
        publishTextView = (TextView) findViewById(R.id.publishTextView);
        chooseMediaTypeContainer = (RelativeLayout) findViewById(R.id.chooseMediaTypeContainer);
        mediaContainer = (LinearLayout) findViewById(R.id.mediaContainer);
        imageCameraTextView = (TextView) findViewById(R.id.imageCameraTextView);
        imageGalleryTextView = (TextView) findViewById(R.id.imageGalleryTextView);
        videoCameraTextView = (TextView) findViewById(R.id.videoCameraTextView);
        videoGalleryTextView = (TextView) findViewById(R.id.videoGalleryTextView);
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);

        selectedGroup = (GroupResult) getIntent().getParcelableExtra("groupItem");

        chooseMediaTypeContainer.setOnClickListener(this);
        addMediaImageView.setOnClickListener(this);
        anonymousImageView.setOnClickListener(this);
        publishTextView.setOnClickListener(this);
        imageCameraTextView.setOnClickListener(this);
        imageGalleryTextView.setOnClickListener(this);
        videoCameraTextView.setOnClickListener(this);
        videoGalleryTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelTextView:
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.chooseMediaTypeContainer:
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
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
                ChooseAnonymousDialogFragment chooseAnonymousDialogFragment = new ChooseAnonymousDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                chooseAnonymousDialogFragment.setArguments(_args);
                chooseAnonymousDialogFragment.setCancelable(true);
                chooseAnonymousDialogFragment.show(fm, "Go Anonymous");
                break;
            case R.id.publishTextView:
                if (validateParams()) {
                    publishPost();
                }
                break;
        }
    }

    private boolean validateParams() {
        if ((postContentEditText.getText() == null || StringUtils.isNullOrEmpty(postContentEditText.getText().toString())) &&
                imageUrlHashMap.isEmpty()) {
            showToast("Please enter some content to continue");
            return false;
        }
        return true;
    }

    private void publishPost() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
        addGroupPostRequest.setContent(postContentEditText.getText().toString());
        addGroupPostRequest.setType("0");
        addGroupPostRequest.setGroupId(selectedGroup.getId());
        if (SharedPrefUtils.isUserAnonymous(this)) {
            addGroupPostRequest.setAnnon(true);
        }
        addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());

        Map<String, String> mediaMap = new HashMap<>();
        int i = 1;
        if (!imageUrlHashMap.isEmpty()) {
            for (Map.Entry<ImageView, String> entry : imageUrlHashMap.entrySet()) {
                mediaMap.put("image" + i, entry.getValue());
                i++;
            }
            addGroupPostRequest.setMediaUrls(mediaMap);
            addGroupPostRequest.setType("1");
        }

        Call<AddGroupPostResponse> call = groupsAPI.createPost(addGroupPostRequest);
        call.enqueue(postAdditionResponseCallback);
    }

    private Callback<AddGroupPostResponse> postAdditionResponseCallback = new Callback<AddGroupPostResponse>() {
        @Override
        public void onResponse(Call<AddGroupPostResponse> call, retrofit2.Response<AddGroupPostResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGroupPostResponse responseModel = response.body();
//                    processGroupListingResponse(responseModel);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<AddGroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
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

    private void getGroupListApi(int skip, int limit) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsListingResponse> call = groupsAPI.getGroupList(skip, limit);
//        call.enqueue(groupListResponseCallback);
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
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(AddTextOrMediaGroupPostActivity.this.getContentResolver(), imageUri);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        String path = MediaStore.Images.Media.insertImage(AddTextOrMediaGroupPostActivity.this.getContentResolver(), imageBitmap, "Title", null);
                        Uri imageUriTemp = Uri.parse(path);
                        EditorFragmentAbstract.imageUploading = 0;
                        File file2 = FileUtils.getFile(this, imageUriTemp);
                        sendUploadProfileImageRequest(file2);
                    } catch (Exception e) {
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
                        EditorFragmentAbstract.imageUploading = 0;
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

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        Log.e("requestBodyFile", requestBodyFile.toString());
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

    private void addImageToContainer(String url) {
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.image_post_upload_item, null);
        ImageView uploadedIV = (ImageView) rl.findViewById(R.id.addImageOptionImageView);
        final ImageView removeIV = (ImageView) rl.findViewById(R.id.removeItemImageView);
        mediaContainer.addView(rl);
        imageUrlHashMap.put(removeIV, url);
        Picasso.with(AddTextOrMediaGroupPostActivity.this).load(url).error(R.drawable.default_article).into(uploadedIV);
        removeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrlHashMap.remove(removeIV);
                mediaContainer.removeView((View) removeIV.getParent());
            }
        });
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
}