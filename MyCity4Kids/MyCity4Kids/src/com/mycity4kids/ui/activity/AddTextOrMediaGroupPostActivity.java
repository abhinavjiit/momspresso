package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.fragment.TaskFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.AudioRecordView;
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
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

/**
 * Created by hemant on 24/4/18.
 */

public class AddTextOrMediaGroupPostActivity extends BaseActivity implements View.OnClickListener, TaskFragment.TaskCallbacks, Handler.Callback, AudioRecordView.RecordingListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

//    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private static final int REQUEST_INIT_PERMISSION = 1;

    public static final int ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE = 1112;

    private TaskFragment mTaskFragment;
    private GroupResult selectedGroup;
    private HashMap<ImageView, String> imageUrlHashMap = new HashMap<>();
    private Uri imageUri;
    private File photoFile;
    private String mCurrentPhotoPath, absoluteImagePath;
    private boolean isRequestRunning = false;

    private View mLayout;
    private EditText postContentEditText;
    private ImageView addMediaImageView, anonymousImageView;
    private ImageView postImageView;
    private TextView publishTextView;
    private TextView imageCameraTextView, imageGalleryTextView, cancelTextView;
    private RelativeLayout chooseMediaTypeContainer;
    private LinearLayout mediaContainer;
    private ImageView closeEditorImageView;
    private TextView anonymousTextView;
    private CheckBox anonymousCheckbox;
    private TextView addMediaTextView;

    private MediaPlayer mMediaplayer;
    private Uri downloadUri;
    private AudioRecordView audioRecordView;
    private long time;
    private Handler mHandler;
    private SeekBar audioSeekBarUpdate, audioSeekBar;
    private long totalDuration, currentDuration;
    private ImageView playAudio, pauseAudio;
    private int pos;
    private boolean isPlayed = false;
    private boolean isPaused = false;
    private boolean isCommentPlay = false;
    private LinearLayout playAudioLayout, timerLayout, dateContainermedia;
    private Uri originalUri;
    private Uri contentURI;
    private long suffixName;
    private FirebaseAuth mAuth;
    private ImageView playAudioImageView, pauseAudioImageView, micImg;
    private ArrayList<String> audioCommentList;
    private TextView audioTimeElapsed, audioTimeElapsedComment;
    private MediaRecorder mRecorder;
    private String mFileName;
    private Animation slideDownAnim;
    private HashMap<ImageView, String> audioUrlHashMap = new HashMap<>();
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean isLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_text_group_post_activity);

        mLayout = findViewById(R.id.rootLayout);
        mHandler = new Handler(this);
        postContentEditText = (EditText) findViewById(R.id.postContentEditText);
        closeEditorImageView = (ImageView) findViewById(R.id.closeEditorImageView);
        anonymousImageView = (ImageView) findViewById(R.id.anonymousImageView);
        anonymousTextView = (TextView) findViewById(R.id.anonymousTextView);
        anonymousCheckbox = (CheckBox) findViewById(R.id.anonymousCheckbox);
        addMediaImageView = (ImageView) findViewById(R.id.addMediaImageView);
        addMediaTextView = (TextView) findViewById(R.id.addMediaTextView);
        postImageView = (ImageView) findViewById(R.id.postImageView);
        publishTextView = (TextView) findViewById(R.id.publishTextView);
        chooseMediaTypeContainer = (RelativeLayout) findViewById(R.id.chooseMediaTypeContainer);
        mediaContainer = (LinearLayout) findViewById(R.id.mediaContainer);
        imageCameraTextView = (TextView) findViewById(R.id.imageCameraTextView);
        imageGalleryTextView = (TextView) findViewById(R.id.imageGalleryTextView);
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);

        audioRecordView = (AudioRecordView) findViewById(R.id.recordingView);
        slideDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down_from_top);
        playAudioLayout = (LinearLayout) findViewById(R.id.playAudioLayout);
        timerLayout = (LinearLayout) findViewById(R.id.timerLayout);
        playAudioImageView = (ImageView) findViewById(R.id.playAudioImageView);
        pauseAudioImageView = (ImageView) findViewById(R.id.pauseAudioImageView);
        audioSeekBar = (SeekBar) findViewById(R.id.audioSeekBar);
        dateContainermedia = (LinearLayout) findViewById(R.id.dateContainermedia);
        audioTimeElapsedComment = (TextView) findViewById(R.id.audioTimeElapsed);

        selectedGroup = (GroupResult) getIntent().getParcelableExtra("groupItem");
        mAuth = FirebaseAuth.getInstance();

        mFileName = Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/";
        mFileName += "/audiorecordtest.m4a";

        audioRecordView.setRecordingListener(this);
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

        postContentEditText.setText(SharedPrefUtils.getSavedPostData(this, selectedGroup.getId()));

        if (SharedPrefUtils.isUserAnonymous(this)) {
            anonymousCheckbox.setChecked(true);
        } else {
            anonymousCheckbox.setChecked(false);
        }
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
                break;
            case R.id.publishTextView:
                if (!isRequestRunning && validateParams()) {
                    isRequestRunning = true;
                    publishPost();
                }
                break;
        }
    }

    private boolean validateParams() {
        if (audioUrlHashMap.isEmpty() && ((postContentEditText.getText() == null || StringUtils.isNullOrEmpty(postContentEditText.getText().toString())) &&
                imageUrlHashMap.isEmpty())) {
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
        //   String str=postContentEditText.getText().toString();
        addGroupPostRequest.setType("0");
        addGroupPostRequest.setGroupId(selectedGroup.getId());
        if (SharedPrefUtils.isUserAnonymous(this)) {
            addGroupPostRequest.setAnnon(1);
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
        }  else if (!audioUrlHashMap.isEmpty()) {
            mediaMap.put("audio", downloadUri.toString());
            addGroupPostRequest.setType("3");
            addGroupPostRequest.setMediaUrls(mediaMap);
        }

        Call<AddGroupPostResponse> call = groupsAPI.createPost(addGroupPostRequest);
        call.enqueue(postAdditionResponseCallback);
    }

    private Callback<AddGroupPostResponse> postAdditionResponseCallback = new Callback<AddGroupPostResponse>() {
        @Override
        public void onResponse(Call<AddGroupPostResponse> call, retrofit2.Response<AddGroupPostResponse> response) {
            isRequestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    SharedPrefUtils.clearSavedPostData(AddTextOrMediaGroupPostActivity.this, selectedGroup.getId());
                    AddGroupPostResponse responseModel = response.body();
                    setResult(RESULT_OK);
                    postContentEditText.setText("");
                    onBackPressed();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<AddGroupPostResponse> call, Throwable t) {
            isRequestRunning = false;
            showToast(getString(R.string.went_wrong));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != postContentEditText.getText() && !StringUtils.isNullOrEmpty(postContentEditText.getText().toString())) {
            SharedPrefUtils.setSavedPostData(AddTextOrMediaGroupPostActivity.this, selectedGroup.getId(), postContentEditText.getText().toString());
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
        String path = MediaStore.Images.Media.insertImage(AddTextOrMediaGroupPostActivity.this.getContentResolver(), image, "Title", null);
        Uri imageUriTemp = Uri.parse(path);
        File file2 = FileUtils.getFile(this, imageUriTemp);
        sendUploadProfileImageRequest(file2);
        mTaskFragment = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);

        int totalDuration = mMediaplayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mMediaplayer.seekTo(currentPosition);
    }

    @Override
    public void onRecordingStarted() {
        if (mMediaplayer != null && isCommentPlay) {
            mMediaplayer.release();
            mMediaplayer = null;
            playAudioImageView.setVisibility(View.VISIBLE);
            pauseAudioImageView.setVisibility(View.GONE);
            audioSeekBar.setProgress(0);
            audioTimeElapsedComment.setVisibility(View.GONE);
        }
        startRecording();
    }

    @Override
    public void onRecordingLocked() {
        isLocked = true;
    }

    @Override
    public void onRecordingCompleted() {
//Stop Recording..
//                String time = getHumanTimeText(recordTime);
        Log.d("RecordView", "onFinish");
        isLocked = false;
        int recordTime = (int) ((System.currentTimeMillis() / (1000)) - time);
        if (recordTime < 1) {
            resetIcons();
            Toast.makeText(this, R.string.hold_to_release, Toast.LENGTH_SHORT).show();
        } else if (recordTime >= 4) {
            stopRecording();
            originalUri = Uri.parse(mFileName);
            contentURI = AppUtils.exportAudioToGallery(originalUri.getPath(), BaseApplication.getAppContext().getContentResolver(), this);
            contentURI = AppUtils.getAudioUriFromMediaProvider(originalUri.getPath(), BaseApplication.getAppContext().getContentResolver());
            uploadAudio(contentURI);
            Log.d("RecordTime", "" + recordTime);
        } else {
            audioRecordView.disableClick(false);
            resetIcons();
            Toast.makeText(this, R.string.please_hold_for_3_seconds, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecordingCanceled() {
        addMediaImageView.setEnabled(false);
        addMediaTextView.setEnabled(false);
    }


    @Override
    public void onReset() {
        resetIcons();
    }

    @Override
    public void setPermission() {
        requestAudioPermissions();
    }

    private void resetIcons() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postContentEditText.setVisibility(View.VISIBLE);
                addMediaImageView.setVisibility(View.VISIBLE);
                anonymousCheckbox.setVisibility(View.VISIBLE);
                anonymousImageView.setVisibility(View.VISIBLE);
                anonymousTextView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                audioRecordView.setLayoutParams(params);
                audioRecordView.disableClick(true);
            }
        }, 500);
    }

    private void startRecording() {
        if (mRecorder != null) {
            mRecorder.release();
        }
        ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        audioRecordView.setLayoutParams(params);
        mRecorder = new MediaRecorder();
        postContentEditText.setVisibility(View.GONE);
        addMediaImageView.setVisibility(View.GONE);
        anonymousCheckbox.setVisibility(View.GONE);
        anonymousImageView.setVisibility(View.GONE);
        anonymousTextView.setVisibility(View.GONE);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(mFileName);
        time = System.currentTimeMillis() / (1000);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (RuntimeException e) {

            } finally {
                mRecorder.release();
                showProgressDialog(getString(R.string.please_wait));
                mRecorder = null;
                addMediaImageView.setEnabled(false);
                addMediaTextView.setEnabled(false);
                postContentEditText.setVisibility(View.VISIBLE);
                addMediaImageView.setVisibility(View.VISIBLE);
                anonymousCheckbox.setVisibility(View.VISIBLE);
                anonymousImageView.setVisibility(View.VISIBLE);
                anonymousTextView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                audioRecordView.setLayoutParams(params);
                slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addMediaImageView.setEnabled(false);
                                addMediaTextView.setEnabled(false);
                                postContentEditText.setVisibility(View.VISIBLE);
                                addMediaImageView.setVisibility(View.VISIBLE);
                                anonymousCheckbox.setVisibility(View.VISIBLE);
                                anonymousImageView.setVisibility(View.VISIBLE);
                                anonymousTextView.setVisibility(View.VISIBLE);
                                ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
                                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                audioRecordView.setLayoutParams(params);
                            }
                        }, 0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

    public void uploadAudio(Uri file) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("VideoUpload", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadAudioToFirebase(contentURI);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                            Toast.makeText(AddTextOrMediaGroupPostActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void uploadAudioToFirebase(Uri file2) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://api-project-3577377239.appspot.com");

        final StorageReference storageRef = storage.getReference();

        suffixName = System.currentTimeMillis();
//        Uri file = Uri.fromFile(file2);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/m4a")
                .build();
        final StorageReference riversRef = storageRef.child("user/" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                + "/audio/" + file2.getLastPathSegment() + "_" + suffixName + ".m4a");
        com.google.firebase.storage.UploadTask uploadTask = riversRef.putFile(file2, metadata);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
//                MixPanelUtils.pushVideoUploadFailureEvent(mixpanel, title);
//                createRowForFailedAttempt(exception.getMessage());

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
//                MixPanelUtils.pushVideoUploadSuccessEvent(mixpanel, title);
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUri = uri;
                        removeProgressDialog();
                        audioRecordView.setVisibility(View.GONE);
                        addMediaImageView.setVisibility(View.GONE);
                        postContentEditText.setVisibility(View.GONE);
                        addAudioToContainer(contentURI.toString());
                    }
                });
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("Tuts+", "Bytes uploaded: " + taskSnapshot.getBytesTransferred());
            }
        });
    }

    private void addAudioToContainer(String url) {
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.audio_post_upload_item, null);
        final ImageView removeIV = (ImageView) rl.findViewById(R.id.removeItemImageView);
        playAudio = rl.findViewById(R.id.playAudioImageView);
        pauseAudio = rl.findViewById(R.id.pauseAudioImageView);
        micImg = rl.findViewById(R.id.mic_img);
        audioSeekBarUpdate = rl.findViewById(R.id.audioSeekBar);
        audioTimeElapsed = rl.findViewById(R.id.audioTimeElapsed);
        mediaContainer.addView(rl);
        audioUrlHashMap.put(removeIV, url);

        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaplayer != null && isCommentPlay) {
                    mMediaplayer.release();
                    mMediaplayer = null;
                    playAudioImageView.setVisibility(View.VISIBLE);
                    pauseAudioImageView.setVisibility(View.GONE);
                    audioSeekBar.setProgress(0);
                    audioTimeElapsedComment.setVisibility(View.GONE);
                }
                pauseAudio.setVisibility(View.VISIBLE);
                playAudio.setVisibility(View.GONE);
                audioTimeElapsed.setVisibility(View.VISIBLE);
                isCommentPlay = false;
                if (mMediaplayer != null && isPaused) {
                    mMediaplayer.start();
                    updateProgressBar();
                    micImg.setVisibility(View.VISIBLE);
                    isPlayed = true;
                    isPaused = false;
                } else {
                    mMediaplayer = new MediaPlayer();
                    mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    audioSeekBarUpdate.setProgress(0);
                    audioSeekBarUpdate.setMax(100);
                    try {
                        mMediaplayer.setDataSource(mFileName);

                        // wait for media player to get prepare
                        mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mMediaplayer.start();
                                updateProgressBar();
                                micImg.setVisibility(View.VISIBLE);
                                isPlayed = true;
                            }
                        });
                        mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                if (isPlayed) {
                                    isPlayed = false;
                                    playAudio.setVisibility(View.VISIBLE);
                                    pauseAudio.setVisibility(View.GONE);
                                    mMediaplayer.stop();
                                    mMediaplayer = null;
//                                    audioSeekBarUpdate.setProgress(0);
                                }
                            }
                        });
                        mMediaplayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        pauseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio.setVisibility(View.VISIBLE);
                pauseAudio.setVisibility(View.GONE);
                mMediaplayer.pause();
                isPaused = true;
                isCommentPlay = false;
                audioSeekBarUpdate.setProgress(0);
            }
        });
        removeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaplayer != null) {
                    mMediaplayer.stop();
                    mMediaplayer.release();
                    isCommentPlay = false;
                    mMediaplayer = null;
                }
                audioUrlHashMap.remove(removeIV);
                mediaContainer.removeView((View) removeIV.getParent());
                postContentEditText.setVisibility(View.VISIBLE);
                addMediaImageView.setEnabled(true);
                addMediaTextView.setEnabled(true);
                audioRecordView.setVisibility(View.VISIBLE);
                addMediaImageView.setVisibility(View.VISIBLE);
            }
        });

    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mMediaplayer != null && !isCommentPlay) {
                totalDuration = mMediaplayer.getDuration();
                currentDuration = mMediaplayer.getCurrentPosition();

                audioTimeElapsed.setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));

                // Updating progress bar
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                audioSeekBarUpdate.setProgress(progress);


                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
            }
        }
    };


    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, R.string.audio_permission, Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }
       /* //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            startRecording();
        }*/
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }
}
