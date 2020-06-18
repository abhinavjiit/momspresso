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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.fragment.ProcessBitmapTaskFragment;
import com.mycity4kids.utils.AudioRecordView;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/4/18.
 */

public class AddTextOrMediaGroupPostActivity extends BaseActivity implements View.OnClickListener,
        ProcessBitmapTaskFragment.TaskCallbacks, Handler.Callback, AudioRecordView.RecordingListener,
        SeekBar.OnSeekBarChangeListener {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static String[] PERMISSIONS_INIT_FOR_AUDIO = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static final int REQUEST_INIT_PERMISSION_FOR_AUDIO = 2;

    public static final int ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE = 1112;
    private ProcessBitmapTaskFragment processBitmapTaskFragment;
    private GroupResult selectedGroup;
    private LinkedHashMap<ImageView, String> imageUrlHashMap = new LinkedHashMap<>();
    private Uri imageUri;
    private File photoFile;
    private String currentPhotoPath;
    private String absoluteImagePath;
    private boolean isRequestRunning = false;
    private View rootLayout;
    private EditText postContentEditText;
    private ImageView addMediaImageView;
    private ImageView anonymousImageView;
    private TextView publishTextView;
    private TextView imageCameraTextView;
    private TextView imageGalleryTextView;
    private TextView cancelTextView;
    private RelativeLayout chooseMediaTypeContainer;
    private LinearLayout mediaContainer;
    private ImageView closeEditorImageView;
    private TextView anonymousTextView;
    private CheckBox anonymousCheckbox;
    private TextView addMediaTextView;
    private MediaPlayer mediaplayer;
    private Uri downloadUri;
    private AudioRecordView audioRecordView;
    private long time;
    private Handler handler;
    private SeekBar audioSeekBarUpdate;
    private SeekBar audioSeekBar;
    private long totalDuration;
    private long currentDuration;
    private ImageView playAudio;
    private ImageView pauseAudio;
    private boolean isPlayed = false;
    private boolean isPaused = false;
    private boolean isCommentPlay = false;
    private Uri originalUri;
    private Uri contentUri;
    private long suffixName;
    private FirebaseAuth firebaseAuth;
    private ImageView playAudioImageView;
    private ImageView pauseAudioImageView;
    private ImageView micImg;
    private TextView audioTimeElapsed;
    private TextView audioTimeElapsedComment;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private Animation slideDownAnim;
    private HashMap<ImageView, String> audioUrlHashMap = new HashMap<>();
    private boolean isLocked = false;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_text_group_post_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        rootLayout = findViewById(R.id.rootLayout);
        handler = new Handler(this);
        postContentEditText = (EditText) findViewById(R.id.postContentEditText);
        closeEditorImageView = (ImageView) findViewById(R.id.closeEditorImageView);
        anonymousImageView = (ImageView) findViewById(R.id.anonymousImageView);
        anonymousTextView = (TextView) findViewById(R.id.anonymousTextView);
        anonymousCheckbox = (CheckBox) findViewById(R.id.anonymousCheckbox);
        addMediaImageView = (ImageView) findViewById(R.id.addMediaImageView);
        addMediaTextView = (TextView) findViewById(R.id.addMediaTextView);
        publishTextView = (TextView) findViewById(R.id.publishTextView);
        chooseMediaTypeContainer = (RelativeLayout) findViewById(R.id.chooseMediaTypeContainer);
        mediaContainer = (LinearLayout) findViewById(R.id.mediaContainer);
        imageCameraTextView = (TextView) findViewById(R.id.imageCameraTextView);
        imageGalleryTextView = (TextView) findViewById(R.id.imageGalleryTextView);
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);

        audioRecordView = (AudioRecordView) findViewById(R.id.recordingView);
        slideDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down_from_top);
        playAudioImageView = (ImageView) findViewById(R.id.playAudioImageView);
        pauseAudioImageView = (ImageView) findViewById(R.id.pauseAudioImageView);
        audioSeekBar = (SeekBar) findViewById(R.id.audioSeekBar);
        audioTimeElapsedComment = (TextView) findViewById(R.id.audioTimeElapsed);

        selectedGroup = (GroupResult) getIntent().getParcelableExtra("groupItem");
        firebaseAuth = FirebaseAuth.getInstance();

        fileName = BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator;
        fileName += "audiorecordtest.m4a";
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        if (SharedPrefUtils.getSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId()) != null) {
            postContentEditText
                    .setText(SharedPrefUtils.getSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId()));
        }

        postContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    audioRecordView.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addMediaImageView
                            .getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    addMediaImageView.setLayoutParams(params);
                } else {
                    audioRecordView.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addMediaImageView
                            .getLayoutParams();
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.LEFT_OF, R.id.recordingView);
                    addMediaImageView.setLayoutParams(params);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            anonymousCheckbox.setChecked(true);
        } else {
            anonymousCheckbox.setChecked(false);
        }

        new SimpleTooltip.Builder(this)
                .anchorView(audioRecordView)
                .backgroundColor(getResources().getColor(R.color.app_red))
                .text(getResources().getString(R.string.add_text_or_media_group_post_activity_tooltip_text))
                .textColor(getResources().getColor(R.color.white_color))
                .arrowColor(getResources().getColor(R.color.app_red))
                .gravity(Gravity.TOP)
                .arrowWidth(40)
                .animated(true)
                .transparentOverlay(true)
                .build()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(rootLayout);
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
            default:
                break;
        }
    }

    private boolean validateParams() {
        if (audioUrlHashMap.isEmpty() && ((postContentEditText.getText() == null || StringUtils
                .isNullOrEmpty(postContentEditText.getText().toString()))
                && imageUrlHashMap.isEmpty())) {
            showToast("Please enter some content to continue");
            return false;
        }
        return true;
    }

    private void publishPost() {
        AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
        addGroupPostRequest.setContent(postContentEditText.getText().toString());
        addGroupPostRequest.setType("0");
        addGroupPostRequest.setGroupId(selectedGroup.getId());
        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            addGroupPostRequest.setAnnon(1);
        }
        addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        LinkedHashMap<String, String> mediaMap = new LinkedHashMap<>();
        int i = 1;
        if (!imageUrlHashMap.isEmpty()) {
            for (Map.Entry<ImageView, String> entry : imageUrlHashMap.entrySet()) {
                mediaMap.put("image" + i, entry.getValue());
                i++;
            }
            addGroupPostRequest.setMediaUrls(mediaMap);
            addGroupPostRequest.setType("1");
        } else if (!audioUrlHashMap.isEmpty()) {
            mediaMap.put("audio", downloadUri.toString());
            addGroupPostRequest.setType("3");
            addGroupPostRequest.setMediaUrls(mediaMap);
        }
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<AddGroupPostResponse> call = groupsApi.createPost(addGroupPostRequest);
        call.enqueue(postAdditionResponseCallback);
    }

    private Callback<AddGroupPostResponse> postAdditionResponseCallback = new Callback<AddGroupPostResponse>() {
        @Override
        public void onResponse(Call<AddGroupPostResponse> call, retrofit2.Response<AddGroupPostResponse> response) {
            isRequestRunning = false;
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    SharedPrefUtils.clearSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId());
                    setResult(RESULT_OK);
                    postContentEditText.setText("");
                    onBackPressed();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<AddGroupPostResponse> call, Throwable t) {
            isRequestRunning = false;
            showToast(getString(R.string.went_wrong));
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                dir
        );
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

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType mediaType = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(mediaType, file);
        Log.e("requestBodyFile", new Gson().toJson(requestBodyFile.toString()));
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "2");
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ImageUploadAPI imageUploadApi = retro.create(ImageUploadAPI.class);
        Call<ImageUploadResponse> call = imageUploadApi.uploadImage(//userId,
                imageType,
                requestBodyFile);
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

    private void addImageToContainer(String url) {
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.image_post_upload_item, null);
        ImageView uploadedIV = (ImageView) rl.findViewById(R.id.addImageOptionImageView);
        final ImageView removeIV = (ImageView) rl.findViewById(R.id.removeItemImageView);
        mediaContainer.addView(rl);
        imageUrlHashMap.put(removeIV, url);
        Picasso.get().load(url).error(R.drawable.default_article).into(uploadedIV);
        removeIV.setOnClickListener(v -> {
            imageUrlHashMap.remove(removeIV);
            mediaContainer.removeView((View) removeIV.getParent());
        });
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions())
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(rootLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions())
                    .show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestPermissionsForAudio() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(rootLayout, R.string.permission_audio_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissionsForAudio())
                    .show();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissionsForAudio())
                    .show();
        } else {
            requestUngrantedPermissionsForAudio();
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

    private void requestUngrantedPermissionsForAudio() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT_FOR_AUDIO.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_INIT_FOR_AUDIO[i])
                    != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT_FOR_AUDIO[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_INIT_PERMISSION_FOR_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                openMediaChooserDialog();
            } else {
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == REQUEST_INIT_PERMISSION_FOR_AUDIO) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
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
        if (null != postContentEditText.getText() && !StringUtils
                .isNullOrEmpty(postContentEditText.getText().toString())) {
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
        String path = MediaStore.Images.Media
                .insertImage(AddTextOrMediaGroupPostActivity.this.getContentResolver(), image,
                        "Title" + System.currentTimeMillis(), null);
        if (path != null) {
            Uri imageUriTemp = Uri.parse(path);
            File file2 = FileUtils.getFile(this, imageUriTemp);
            sendUploadProfileImageRequest(file2);
            processBitmapTaskFragment = null;
        } else {
            removeProgressDialog();
            Toast.makeText(this, R.string.unsupported_image, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);

        int totalDuration = mediaplayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
        // forward or backward to certain seconds
        mediaplayer.seekTo(currentPosition);
    }

    @Override
    public void onRecordingStarted() {
        if (mediaplayer != null && isCommentPlay) {
            mediaplayer.release();
            mediaplayer = null;
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
        Log.d("RecordView", "onFinish");
        isLocked = false;
        int recordTime = (int) ((System.currentTimeMillis() / (1000)) - time);
        if (recordTime < 1) {
            resetIcons();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, R.string.hold_to_release, Toast.LENGTH_SHORT).show();
        } else if (recordTime >= 4) {
            stopRecording();
            contentUri = FileProvider.getUriForFile(this, "com.momspresso.fileprovider", new File(fileName));
            uploadAudio(contentUri);
        } else {
            audioRecordView.disableClick(false);
            resetIcons();
            mediaRecorder.release();
            mediaRecorder = null;
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
        handler.postDelayed(() -> {
            postContentEditText.setVisibility(View.VISIBLE);
            addMediaImageView.setVisibility(View.VISIBLE);
            anonymousCheckbox.setVisibility(View.VISIBLE);
            anonymousImageView.setVisibility(View.VISIBLE);
            anonymousTextView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            audioRecordView.setLayoutParams(params);
            audioRecordView.disableClick(true);
        }, 500);
    }

    private void startRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        audioRecordView.setLayoutParams(params);
        mediaRecorder = new MediaRecorder();
        postContentEditText.setVisibility(View.GONE);
        addMediaImageView.setVisibility(View.GONE);
        anonymousCheckbox.setVisibility(View.GONE);
        anonymousImageView.setVisibility(View.GONE);
        anonymousTextView.setVisibility(View.GONE);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(fileName);
        time = System.currentTimeMillis() / (1000);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (RuntimeException e) {
                Log.e("LOG_TAG", "prepare() failed");
            } finally {
                mediaRecorder.release();
                showProgressDialog(getString(R.string.please_wait));
                mediaRecorder = null;
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
                        new Handler().postDelayed(() -> {
                            addMediaImageView.setEnabled(false);
                            addMediaTextView.setEnabled(false);
                            postContentEditText.setVisibility(View.VISIBLE);
                            addMediaImageView.setVisibility(View.VISIBLE);
                            anonymousCheckbox.setVisibility(View.VISIBLE);
                            anonymousImageView.setVisibility(View.VISIBLE);
                            anonymousTextView.setVisibility(View.VISIBLE);
                            ViewGroup.LayoutParams params1 = audioRecordView.getLayoutParams();
                            params1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                            audioRecordView.setLayoutParams(params1);
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
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("VideoUpload", "signInAnonymously:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (contentUri != null) {
                            uploadAudioToFirebase(contentUri);
                        } else {
                            Toast.makeText(AddTextOrMediaGroupPostActivity.this, "Authentication failed...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                        Toast.makeText(AddTextOrMediaGroupPostActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAudioToFirebase(Uri file2) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://api-project-3577377239.appspot.com");

        final StorageReference storageRef = storage.getReference();

        suffixName = System.currentTimeMillis();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/m4a")
                .build();
        final StorageReference riversRef = storageRef
                .child("user/" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                        + "/audio/" + file2.getLastPathSegment() + "_" + suffixName + ".m4a");
        com.google.firebase.storage.UploadTask uploadTask = riversRef.putFile(file2, metadata);

        uploadTask.addOnFailureListener(exception -> {
        }).addOnSuccessListener(taskSnapshot -> riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadUri = uri;
            removeProgressDialog();
            audioRecordView.setVisibility(View.GONE);
            addMediaImageView.setVisibility(View.GONE);
            postContentEditText.setVisibility(View.GONE);
            addAudioToContainer(contentUri.toString());
        }));

        uploadTask.addOnProgressListener(
                taskSnapshot -> Log.e("audio uploaded", "Bytes uploaded: " + taskSnapshot.getBytesTransferred()));
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

        playAudio.setOnClickListener(view -> {
            if (mediaplayer != null && isCommentPlay) {
                mediaplayer.release();
                mediaplayer = null;
                playAudioImageView.setVisibility(View.VISIBLE);
                pauseAudioImageView.setVisibility(View.GONE);
                audioSeekBar.setProgress(0);
                audioTimeElapsedComment.setVisibility(View.GONE);
            }
            pauseAudio.setVisibility(View.VISIBLE);
            playAudio.setVisibility(View.GONE);
            audioTimeElapsed.setVisibility(View.VISIBLE);
            isCommentPlay = false;
            if (mediaplayer != null && isPaused) {
                mediaplayer.start();
                updateProgressBar();
                micImg.setVisibility(View.VISIBLE);
                isPlayed = true;
                isPaused = false;
            } else {
                mediaplayer = new MediaPlayer();
                mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioSeekBarUpdate.setProgress(0);
                audioSeekBarUpdate.setMax(100);
                try {
                    mediaplayer.setDataSource(fileName);

                    // wait for media player to get prepare
                    mediaplayer.setOnPreparedListener(mediaPlayer -> {
                        mediaplayer.start();
                        updateProgressBar();
                        micImg.setVisibility(View.VISIBLE);
                        isPlayed = true;
                    });
                    mediaplayer.setOnCompletionListener(mediaPlayer -> {
                        if (isPlayed) {
                            isPlayed = false;
                            playAudio.setVisibility(View.VISIBLE);
                            pauseAudio.setVisibility(View.GONE);
                            mediaplayer.stop();
                            mediaplayer = null;
                        }
                    });
                    mediaplayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        pauseAudio.setOnClickListener(view -> {
            playAudio.setVisibility(View.VISIBLE);
            pauseAudio.setVisibility(View.GONE);
            mediaplayer.pause();
            isPaused = true;
            isCommentPlay = false;
            audioSeekBarUpdate.setProgress(0);
        });
        removeIV.setOnClickListener(v -> {
            if (mediaplayer != null) {
                mediaplayer.stop();
                mediaplayer.release();
                isCommentPlay = false;
                mediaplayer = null;
            }
            audioUrlHashMap.remove(removeIV);
            mediaContainer.removeView((View) removeIV.getParent());
            postContentEditText.setVisibility(View.VISIBLE);
            addMediaImageView.setEnabled(true);
            addMediaTextView.setEnabled(true);
            audioRecordView.setVisibility(View.VISIBLE);
            addMediaImageView.setVisibility(View.VISIBLE);
        });

    }

    public void updateProgressBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            if (mediaplayer != null && !isCommentPlay) {
                totalDuration = mediaplayer.getDuration();
                currentDuration = mediaplayer.getCurrentPosition();

                audioTimeElapsed
                        .setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));

                // Updating progress bar
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                audioSeekBarUpdate.setProgress(progress);
                handler.postDelayed(this, 100);
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
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsForAudio();
            }
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }
}
