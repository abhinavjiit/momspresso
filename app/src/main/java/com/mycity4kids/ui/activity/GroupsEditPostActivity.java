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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.request.UpdatePostContentRequest;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.fragment.ProcessBitmapTaskFragment;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.StringUtils;
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

/**
 * Created by hemant on 14/8/18.
 */

public class GroupsEditPostActivity extends BaseActivity implements View.OnClickListener,
        ProcessBitmapTaskFragment.TaskCallbacks {

    public static final int REQUEST_INIT_PERMISSION = 1;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    public static final int ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE = 1112;

    private EditText postContentEditText;
    private ImageView closeEditorImageView;
    private GroupPostResult postData;
    private TextView publishTextView;
    private RelativeLayout root;
    private LinearLayout mediaContainer;
    private LinkedHashMap<ImageView, String> imageUrlHashMap = new LinkedHashMap<>();
    private ArrayList<String> images = new ArrayList<>();
    private ProcessBitmapTaskFragment processBitmapTaskFragment;
    private GroupResult selectedGroup;
    private Uri imageUri;
    private File photoFile;
    private String currentPhotoPath;
    private String absoluteImagePath;
    private View mainLayout;
    private ImageView addMediaImageView;
    private ImageView anonymousImageView;
    private TextView imageCameraTextView;
    private TextView imageGalleryTextView;
    private TextView cancelTextView;
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
        mediaContainer = findViewById(R.id.mediaContainer);
        postContentEditText = findViewById(R.id.postContentEditText);
        closeEditorImageView = findViewById(R.id.closeEditorImageView);
        publishTextView = findViewById(R.id.publishTextView);
        mainLayout = findViewById(R.id.rootLayout);
        anonymousImageView = findViewById(R.id.anonymousImageView);
        anonymousTextView = findViewById(R.id.anonymousTextView);
        anonymousCheckbox = findViewById(R.id.anonymousCheckbox);
        addMediaImageView = findViewById(R.id.addMediaImageView);
        addMediaTextView = findViewById(R.id.addMediaTextView);
        chooseMediaTypeContainer = findViewById(R.id.chooseMediaTypeContainer);
        imageCameraTextView = findViewById(R.id.imageCameraTextView);
        imageGalleryTextView = findViewById(R.id.imageGalleryTextView);
        cancelTextView = findViewById(R.id.cancelTextView);

        selectedGroup = getIntent().getParcelableExtra("groupItem");

        closeEditorImageView.setOnClickListener(this);
        publishTextView.setOnClickListener(this);

        postData = getIntent().getParcelableExtra("postData");
        postContentEditText.setText(postData.getContent());
        if (postData.getGroupInfo() != null && postData.getGroupInfo().getAnnonAllowed() == 0) {
            SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
            anonymousCheckbox.setChecked(false);
            anonymousCheckbox.setVisibility(View.INVISIBLE);
            anonymousImageView.setVisibility(View.INVISIBLE);
            anonymousTextView.setVisibility(View.INVISIBLE);
        } else {
            if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
                anonymousCheckbox.setChecked(true);
            } else {
                anonymousCheckbox.setChecked(false);
            }
        }

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
            if (!entry.getKey().equalsIgnoreCase("audio")) {
                images.add(entry.getValue());
            }
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
    }

    private void addImageToContainer(String url) {
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.image_post_upload_item, null);
        ImageView uploadedIV = rl.findViewById(R.id.addImageOptionImageView);
        final ImageView removeIV = rl.findViewById(R.id.removeItemImageView);
        mediaContainer.addView(rl);
        imageUrlHashMap.put(removeIV, url);
        Picasso.get().load(url).error(R.drawable.default_article).into(uploadedIV);
        removeIV.setOnClickListener(v -> {
            imageUrlHashMap.remove(removeIV);
            mediaContainer.removeView((View) removeIV.getParent());
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeEditorImageView:
                onBackPressed();
                break;
            case R.id.publishTextView:
                UpdatePostContentRequest updatePostRequest = new UpdatePostContentRequest();
                updatePostRequest.setContent(postContentEditText.getText().toString());
                if ("1".equals(postData.getType())) {
                    updatePostRequest.setType("0");
                } else {
                    updatePostRequest.setType(postData.getType());
                }
                LinkedHashMap<String, String> mediaMap = new LinkedHashMap<>();
                int i = 1;
                if (!imageUrlHashMap.isEmpty()) {
                    for (Map.Entry<ImageView, String> entry : imageUrlHashMap.entrySet()) {
                        mediaMap.put("image" + i, entry.getValue());
                        i++;
                    }
                    updatePostRequest.setType("1");
                }
                updatePostRequest.setMediaUrls(mediaMap);
                Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
                Call<GroupPostResponse> call = groupsApi.updatePostContent(postData.getId(), updatePostRequest);
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
            case R.id.anonymousImageView:
            case R.id.anonymousTextView:
            case R.id.anonymousCheckbox:
                if (anonymousCheckbox.isChecked()) {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
                } else {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
                }
                break;
            default:
                break;
        }
    }


    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mainLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions())
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions())
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
                Snackbar.make(mainLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                openMediaChooserDialog();
            } else {
                Snackbar.make(mainLayout, R.string.permissions_not_granted,
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
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("TAG", "IOException");
            }
            if (photoFile != null) {
                try {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider
                            .getUriForFile(this, getApplicationContext().getPackageName() + ".my.package.name.provider",
                                    createImageFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivityForResult(cameraIntent, ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(imageFileName, ".jpg", dir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();
        absoluteImagePath = image.getAbsolutePath();
        return image;
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
                        // If the Fragment is non-null, then it is currently being
                        // retained across a configuration change.
                        FragmentManager fm = getFragmentManager();
                        processBitmapTaskFragment = (ProcessBitmapTaskFragment) fm
                                .findFragmentByTag(TAG_TASK_FRAGMENT);
                        if (processBitmapTaskFragment == null) {
                            processBitmapTaskFragment = new ProcessBitmapTaskFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("uri", imageUri);
                            processBitmapTaskFragment.setArguments(bundle);
                            fm.beginTransaction().add(processBitmapTaskFragment, TAG_TASK_FRAGMENT).commit();
                        } else {
                            processBitmapTaskFragment.launchNewTask(imageUri);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.parse(currentPhotoPath));
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
                        FileOutputStream fos = new FileOutputStream(photoFile);
                        fos.write(bitmapData);
                        fos.flush();
                        fos.close();
                        imageUri = Uri.fromFile(photoFile);
                        File file2 = FileUtils.getFile(this, imageUri);
                        sendUploadProfileImageRequest(file2);
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
            default:
                break;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private Callback<GroupPostResponse> updatePostContentResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    postData = groupPostResponse.getData().get(0).getResult().get(0);
                    Intent intent = getIntent();
                    intent.putExtra("editedPost", postData);
                    intent.putExtra("updatedContent",
                            groupPostResponse.getData().get(0).getResult().get(0).getContent());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType mediaType = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(mediaType, file);
        Log.e("requestBodyFile", new Gson().toJson(requestBodyFile.toString()));
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "2");
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ImageUploadAPI imageUploadApi = retro.create(ImageUploadAPI.class);
        Call<ImageUploadResponse> call = imageUploadApi.uploadImage(imageType, requestBodyFile);
        call.enqueue(
                new Callback<ImageUploadResponse>() {
                    @Override
                    public void onResponse(Call<ImageUploadResponse> call,
                            retrofit2.Response<ImageUploadResponse> response) {
                        removeProgressDialog();
                        if (response.body() == null) {
                            showToast(getString(R.string.server_went_wrong));
                            return;
                        }

                        file.delete();

                        ImageUploadResponse responseModel = response.body();
                        if (responseModel.getCode() != 200) {
                            showToast(getString(R.string.toast_response_error));
                        } else {
                            if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                            }

                            addImageToContainer(responseModel.getData().getResult().getUrl());
                            showToast(getString(R.string.image_upload_success));
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                        FirebaseCrashlytics.getInstance().recordException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                        showToast(getString(R.string.went_wrong));
                    }
                }
        );
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != postContentEditText.getText() && !StringUtils
                .isNullOrEmpty(postContentEditText.getText().toString()) && selectedGroup != null) {
            SharedPrefUtils.setSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId(),
                    postContentEditText.getText().toString());
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
        String path = MediaStore.Images.Media.insertImage(GroupsEditPostActivity.this.getContentResolver(), image,
                "Title" + System.currentTimeMillis(), null);
        Uri imageUriTemp = Uri.parse(path);
        File file2 = FileUtils.getFile(this, imageUriTemp);
        sendUploadProfileImageRequest(file2);
        processBitmapTaskFragment = null;
    }

}
