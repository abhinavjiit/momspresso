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
import android.os.Environment;
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

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.AudioPostRecordView;
import com.mycity4kids.utils.PermissionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
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
    static int count;
    private boolean isRequestRunning = false;
    private View mLayout;
    private ImageView anonymousImageView;
    private TextView publishTextView;
    private LinearLayout mediaContainer;
    private ImageView closeEditorImageView;
    private TextView anonymousTextView;
    private CheckBox anonymousCheckbox;
    private MediaPlayer mMediaplayer;
    private Uri downloadUri;
    private AudioPostRecordView audioRecordView;
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
    private TextView audioTimeElapsed, audioTimeElapsedComment;
    private MediaRecorder mRecorder;
    private String mFileName;
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

        mLayout = findViewById(R.id.rootLayout);
        mHandler = new Handler(this);
        closeEditorImageView = (ImageView) findViewById(R.id.closeEditorImageView);
        anonymousImageView = (ImageView) findViewById(R.id.anonymousImageView);
        anonymousTextView = (TextView) findViewById(R.id.anonymousTextView);
        anonymousCheckbox = (CheckBox) findViewById(R.id.anonymousCheckbox);
        publishTextView = (TextView) findViewById(R.id.publishTextView);
        mediaContainer = (LinearLayout) findViewById(R.id.mediaContainer);

        audioRecordView = (AudioPostRecordView) findViewById(R.id.recordingView);
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

        AppUtils.createDirIfNotExists("MyCity4Kids/videos");
        mFileName = Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/";
        mFileName += "/audiorecordtest.m4a";

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
        ((BaseApplication) getApplication()).setView(mLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeEditorImageView:
                Utils.groupsEvent(AddAudioGroupPostActivity.this, "Create Audio", "Cancel X sign", "android", SharedPrefUtils.getAppLocale(AddAudioGroupPostActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "click", "", "");
                onBackPressed();
                break;
            case R.id.anonymousImageView:
            case R.id.anonymousTextView:
            case R.id.anonymousCheckbox:
                Utils.groupsEvent(AddAudioGroupPostActivity.this, "Create Audio", "Cancel X sign", "android", SharedPrefUtils.getAppLocale(AddAudioGroupPostActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "click", "", "");
                if (anonymousCheckbox.isChecked()) {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
                } else {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
                }



                break;
            case R.id.publishTextView:
                Utils.groupsEvent(AddAudioGroupPostActivity.this, "Create Audio", "Anonymous", "android", SharedPrefUtils.getAppLocale(AddAudioGroupPostActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "click", "", "");
                if (!isRequestRunning && validateParams()) {
                    isRequestRunning = true;
                    publishPost();
                }
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
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
        addGroupPostRequest.setContent("");
        addGroupPostRequest.setType("0");
        addGroupPostRequest.setGroupId(selectedGroup.getId());
        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            addGroupPostRequest.setAnnon(1);
        }
        addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

        LinkedHashMap<String, String> mediaMap = new LinkedHashMap<>();
        if (!audioUrlHashMap.isEmpty()) {
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
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    SharedPrefUtils.clearSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId());
                    AddGroupPostResponse responseModel = response.body();
                    setResult(RESULT_OK);
                    onBackPressed();
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

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION_FOR_AUDIO) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                AppUtils.createDirIfNotExists("MyCity4Kids/videos");
                mFileName = Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/";
                mFileName += "/audiorecordtest.m4a";
            } else {
                Snackbar.make(mLayout, R.string.permissions_not_granted,
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
        Utils.groupsEvent(AddAudioGroupPostActivity.this, "Create Audio", "Audio Button", "android", SharedPrefUtils.getAppLocale(AddAudioGroupPostActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "click", "", "");
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
        Log.d("RecordView", "onFinish");
        isLocked = false;
        int recordTime = (int) ((System.currentTimeMillis() / (1000)) - time);
        if (recordTime < 1) {
            resetIcons();
            mRecorder.release();
            mRecorder = null;
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
            mRecorder.release();
            mRecorder = null;
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
        if (mRecorder != null) {
            mRecorder.release();
        }
        ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        audioRecordView.setLayoutParams(params);
        mRecorder = new MediaRecorder();
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
                            Log.d("VideoUpload", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadAudioToFirebase(contentURI);
                        } else {
                            Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                            Toast.makeText(AddAudioGroupPostActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
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
        final StorageReference riversRef = storageRef.child("user/" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                + "/audio/" + file2.getLastPathSegment() + "_" + suffixName + ".m4a");
        UploadTask uploadTask = riversRef.putFile(file2, metadata);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUri = uri;
                        removeProgressDialog();
                        audioRecordView.setVisibility(View.GONE);
                        addAudioToContainer(contentURI.toString());
                    }
                });
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("audio uploaded", "Bytes uploaded: " + taskSnapshot.getBytesTransferred());
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
                audioRecordView.setVisibility(View.VISIBLE);
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
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                audioSeekBarUpdate.setProgress(progress);
                mHandler.postDelayed(this, 100);
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
            Snackbar.make(mLayout, R.string.permission_audio_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissionsForAudio();
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissionsForAudio();
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissionsForAudio();
        }
    }

    private void requestUngrantedPermissionsForAudio() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT_FOR_AUDIO.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_INIT_FOR_AUDIO[i]) != PackageManager.PERMISSION_GRANTED) {
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
