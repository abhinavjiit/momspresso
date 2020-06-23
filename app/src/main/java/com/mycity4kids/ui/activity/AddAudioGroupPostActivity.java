package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import com.google.firebase.storage.UploadTask;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.utils.AudioPostRecordView;
import com.mycity4kids.utils.PermissionUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/4/18.
 */

public class AddAudioGroupPostActivity extends BaseActivity implements View.OnClickListener, Handler.Callback,
        AudioPostRecordView.RecordingListener, SeekBar.OnSeekBarChangeListener {

    private static String[] PERMISSIONS_INIT_FOR_AUDIO = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_INIT_PERMISSION_FOR_AUDIO = 2;

    private GroupResult selectedGroup;
    private boolean isRequestRunning = false;
    private View rootLayout;
    private ImageView anonymousImageView;
    private TextView publishTextView;
    private LinearLayout mediaContainer;
    private ImageView closeEditorImageView;
    private TextView anonymousTextView;
    private CheckBox anonymousCheckbox;
    private MediaPlayer mediaPlayer;
    private Uri downloadUri;
    private AudioPostRecordView audioRecordView;
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
        setContentView(R.layout.add_audio_group_post_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        rootLayout = findViewById(R.id.rootLayout);
        handler = new Handler(this);
        closeEditorImageView = findViewById(R.id.closeEditorImageView);
        anonymousImageView = findViewById(R.id.anonymousImageView);
        anonymousTextView = findViewById(R.id.anonymousTextView);
        anonymousCheckbox = findViewById(R.id.anonymousCheckbox);
        publishTextView = findViewById(R.id.publishTextView);
        mediaContainer = findViewById(R.id.mediaContainer);
        audioRecordView = findViewById(R.id.recordingView);
        slideDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down_from_top);
        playAudioImageView = findViewById(R.id.playAudioImageView);
        pauseAudioImageView = findViewById(R.id.pauseAudioImageView);
        audioSeekBar = findViewById(R.id.audioSeekBar);
        audioTimeElapsedComment = findViewById(R.id.audioTimeElapsed);

        selectedGroup = getIntent().getParcelableExtra("groupItem");

        if (selectedGroup != null && selectedGroup.getAnnonAllowed() == 0) {
            anonymousCheckbox.setChecked(false);
            anonymousCheckbox.setVisibility(View.GONE);
            anonymousImageView.setVisibility(View.GONE);
            anonymousTextView.setVisibility(View.GONE);
        }
        firebaseAuth = FirebaseAuth.getInstance();

        fileName = BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator;
        fileName += "audiorecordtest.m4a";

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        audioRecordView.setRecordingListener(this);
        anonymousImageView.setOnClickListener(this);
        anonymousTextView.setOnClickListener(this);
        anonymousCheckbox.setOnClickListener(this);
        publishTextView.setOnClickListener(this);
        closeEditorImageView.setOnClickListener(this);

        if (SharedPrefUtils.isUserAnonymous(this)) {
            anonymousCheckbox.setChecked(true);
        } else {
            anonymousCheckbox.setChecked(false);
        }
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
                Utils.groupsEvent(AddAudioGroupPostActivity.this, "Create Audio", "Cancel X sign", "android",
                        SharedPrefUtils.getAppLocale(AddAudioGroupPostActivity.this),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", "");
                onBackPressed();
                break;
            case R.id.anonymousImageView:
            case R.id.anonymousTextView:
            case R.id.anonymousCheckbox:
                Utils.groupsEvent(AddAudioGroupPostActivity.this, "Create Audio", "Cancel X sign", "android",
                        SharedPrefUtils.getAppLocale(AddAudioGroupPostActivity.this),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", "");
                if (anonymousCheckbox.isChecked()) {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
                } else {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
                }

                break;
            case R.id.publishTextView:
                Utils.groupsEvent(AddAudioGroupPostActivity.this, "Create Audio", "Anonymous", "android",
                        SharedPrefUtils.getAppLocale(AddAudioGroupPostActivity.this),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", "");
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
        if (audioUrlHashMap.isEmpty()) {
            showToast("Please enter some content to continue");
            return false;
        }
        return true;
    }

    private void publishPost() {
        AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
        addGroupPostRequest.setContent("");
        addGroupPostRequest.setType("0");
        addGroupPostRequest.setGroupId(selectedGroup.getId());
        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            addGroupPostRequest.setAnnon(1);
        }
        addGroupPostRequest
                .setUserId(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

        LinkedHashMap<String, String> mediaMap = new LinkedHashMap<>();
        if (!audioUrlHashMap.isEmpty()) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION_FOR_AUDIO) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                fileName = BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator;
                fileName += "audiorecordtest.m4a";
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
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);

        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);
    }

    @Override
    public void onRecordingStarted() {
        Utils.groupsEvent(AddAudioGroupPostActivity.this, "Create Audio", "Audio Button", "android",
                SharedPrefUtils.getAppLocale(AddAudioGroupPostActivity.this),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                String.valueOf(System.currentTimeMillis()), "click", "", "");
        if (mediaPlayer != null && isCommentPlay) {
            mediaPlayer.release();
            mediaPlayer = null;
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
            Log.d("RecordTime", "" + recordTime);
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
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        audioRecordView.setLayoutParams(params);
        mediaRecorder = new MediaRecorder();
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            } finally {
                mediaRecorder.release();
                showProgressDialog(getString(R.string.please_wait));
                mediaRecorder = null;
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
                        Log.d("VideoUpload", "signInAnonymously:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        uploadAudioToFirebase(contentUri);
                    } else {
                        Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                        Toast.makeText(AddAudioGroupPostActivity.this, "Authentication failed.",
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
        UploadTask uploadTask = riversRef.putFile(file2, metadata);

        uploadTask.addOnFailureListener(exception -> {

        }).addOnSuccessListener(taskSnapshot -> riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadUri = uri;
            removeProgressDialog();
            audioRecordView.setVisibility(View.GONE);
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
            if (mediaPlayer != null && isCommentPlay) {
                mediaPlayer.release();
                mediaPlayer = null;
                playAudioImageView.setVisibility(View.VISIBLE);
                pauseAudioImageView.setVisibility(View.GONE);
                audioSeekBar.setProgress(0);
                audioTimeElapsedComment.setVisibility(View.GONE);
            }
            pauseAudio.setVisibility(View.VISIBLE);
            playAudio.setVisibility(View.GONE);
            audioTimeElapsed.setVisibility(View.VISIBLE);
            isCommentPlay = false;
            if (mediaPlayer != null && isPaused) {
                mediaPlayer.start();
                updateProgressBar();
                micImg.setVisibility(View.VISIBLE);
                isPlayed = true;
                isPaused = false;
            } else {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioSeekBarUpdate.setProgress(0);
                audioSeekBarUpdate.setMax(100);
                try {
                    mediaPlayer.setDataSource(fileName);
                    mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                        AddAudioGroupPostActivity.this.mediaPlayer.start();
                        updateProgressBar();
                        micImg.setVisibility(View.VISIBLE);
                        isPlayed = true;
                    });
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        if (isPlayed) {
                            isPlayed = false;
                            playAudio.setVisibility(View.VISIBLE);
                            pauseAudio.setVisibility(View.GONE);
                            AddAudioGroupPostActivity.this.mediaPlayer.stop();
                            AddAudioGroupPostActivity.this.mediaPlayer = null;
                        }
                    });
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        pauseAudio.setOnClickListener(view -> {
            playAudio.setVisibility(View.VISIBLE);
            pauseAudio.setVisibility(View.GONE);
            mediaPlayer.pause();
            isPaused = true;
            isCommentPlay = false;
            audioSeekBarUpdate.setProgress(0);
        });
        removeIV.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                isCommentPlay = false;
                mediaPlayer = null;
            }
            audioUrlHashMap.remove(removeIV);
            mediaContainer.removeView((View) removeIV.getParent());
            audioRecordView.setVisibility(View.VISIBLE);
        });

    }

    public void updateProgressBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            if (mediaPlayer != null && !isCommentPlay) {
                totalDuration = mediaPlayer.getDuration();
                currentDuration = mediaPlayer.getCurrentPosition();
                audioTimeElapsed
                        .setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));
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
        return currentDuration * 1000;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage = (((double) currentSeconds) / totalSeconds) * 100;
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
    public boolean handleMessage(Message message) {
        return false;
    }
}
