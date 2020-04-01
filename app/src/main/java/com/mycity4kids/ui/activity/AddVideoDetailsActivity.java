package com.mycity4kids.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.coremedia.iso.boxes.Container;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.ui.NotificationWorker;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/1/17.
 */
public class AddVideoDetailsActivity extends BaseActivity implements View.OnClickListener, EasyVideoCallback {

    public static final String COMMON_PREF_FILE = "my_city_prefs";
    private static final int MAX_VOLUME = 100;

    private EditText videoTitleEditText;
    private SwitchCompat muteSwitch;
    private Toolbar toolbar;
    private TextView saveUploadTextView;

    private Uri originalUri;
    private String vrotation;

    private String originalPath;
    private Uri contentUri;
    private Uri mutedUri;
    private EasyVideoPlayer player;
    private String categoryId;
    private String duration;
    private String thumbnailTime;
    private SharedPreferences pref;
    private String comingFrom;
    private String challengeId;
    private RelativeLayout root;
    private FirebaseAuth auth;
    private WorkManager workManager;
    private Boolean signIn = false;
    private OneTimeWorkRequest request;
    private RelativeLayout popup;
    private TextView okay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_video_details_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "CreateVideoScreen",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        popup = (RelativeLayout) findViewById(R.id.popup);
        okay = (TextView) findViewById(R.id.okay);
        videoTitleEditText = (EditText) findViewById(R.id.videoTitleEditText);
        muteSwitch = (SwitchCompat) findViewById(R.id.muteVideoSwitch);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        player = (EasyVideoPlayer) findViewById(R.id.player);
        saveUploadTextView = (TextView) findViewById(R.id.saveUploadTextView);
        auth = FirebaseAuth.getInstance();
        workManager = WorkManager.getInstance(this);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/oswald_regular.ttf");
        muteSwitch.setTypeface(font);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Video");

        categoryId = getIntent().getStringExtra("categoryId");
        duration = getIntent().getStringExtra("duration");
        thumbnailTime = getIntent().getStringExtra("thumbnailTime");
        comingFrom = getIntent().getStringExtra("comingFrom");
        if ("Challenge".equals(comingFrom)) {
            challengeId = getIntent().getStringExtra("ChallengeId");
        }

        muteSwitch.setOnClickListener(this);
        if (getIntent().hasExtra("uriPath")) {
            originalPath = getIntent().getStringExtra("uriPath");
        } else if (getIntent().hasExtra("originalPath")) {
            originalPath = getIntent().getStringExtra("originalPath");
        }

        saveUploadTextView.setOnClickListener(this);

        ColorStateList thumbStates = new ColorStateList(
                new int[][] {
                        new int[] {android.R.attr.state_checked},
                        new int[] {}
                },
                new int[] {

                        getResources().getColor(R.color.app_red),
                        getResources().getColor(R.color.add_video_details_mute_label)
                }
        );
        muteSwitch.setThumbTintList(thumbStates);
        if (Build.VERSION.SDK_INT >= 24) {
            ColorStateList trackStates = new ColorStateList(
                    new int[][] {
                            new int[] {android.R.attr.state_checked},
                            new int[] {}
                    },
                    new int[] {

                            getColor(R.color.app_red_50_opacity),
                            getColor(R.color.add_video_details_mute_label_50_percent_opacity)
                    }
            );
            muteSwitch.setTrackTintList(trackStates);

        }

        player.setCallback(this);
        player.setAutoPlay(true);
        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.fromFile(new File(originalPath)));
        //specify the location of media file
        originalUri = Uri.parse(originalPath);
        // Starts or resumes playback.
        player.start();
        okay.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.muteVideoSwitch:
                if (muteSwitch.isChecked()) {

                    muteSwitch.setTextColor(getResources().getColor(R.color.app_red));
                    final float volume = (float) (1 - (Math.log(MAX_VOLUME - 0) / Math.log(MAX_VOLUME)));
                    player.setVolume(volume, volume);
                } else {

                    muteSwitch.setTextColor(getResources().getColor(R.color.mute_text_color));
                    final float volume = (float) (1 - (Math.log(MAX_VOLUME - 99) / Math.log(MAX_VOLUME)));
                    player.setVolume(volume, volume);
                }
                break;

            case R.id.saveUploadTextView:
                if (StringUtils.isNullOrEmpty(videoTitleEditText.getText().toString())) {
                    videoTitleEditText.setFocusableInTouchMode(true);
                    videoTitleEditText.setError(getString(R.string.add_video_details_error_empty_title));
                    videoTitleEditText.requestFocus();
                } else if (videoTitleEditText.getText().toString().length() > 150) {
                    videoTitleEditText.setFocusableInTouchMode(true);
                    videoTitleEditText.setError(getString(R.string.add_video_details_title_length_error));
                    videoTitleEditText.requestFocus();
                } else {
                    uploadVideo();
                }
                break;
            case R.id.okay:
                popup.setVisibility(View.GONE);
                Intent intent = new Intent(AddVideoDetailsActivity.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            default:
                break;

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void muteVideo() {
        String fname = AppUtils.getFileNameFromUri(this, originalUri);
        String outputFile = "";
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/MyCity4Kids/" + "mute_" + fname);
            file.createNewFile();
            outputFile = file.getAbsolutePath();

            MediaExtractor videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(originalUri.getPath());

            MediaMuxer muxer = new MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            videoExtractor.selectTrack(0);
            MediaFormat videoFormat = videoExtractor.getTrackFormat(0);
            int videoTrack = muxer.addTrack(videoFormat);
            Log.d("TAG", "Video Format " + videoFormat.toString());
            int frameCount = 0;
            int offset = 100;
            int sampleSize = 256 * 1024;
            ByteBuffer videoBuf = ByteBuffer.allocate(sampleSize);
            ByteBuffer audioBuf = ByteBuffer.allocate(sampleSize);
            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            muxer.setOrientationHint(Integer.parseInt(vrotation));
            muxer.start();
            boolean sawEos = false;
            while (!sawEos) {
                videoBufferInfo.offset = offset;
                videoBufferInfo.size = videoExtractor.readSampleData(videoBuf, offset);
                if (videoBufferInfo.size < 0) {
                    Log.d("TAG", "saw input EOS.");
                    sawEos = true;
                    videoBufferInfo.size = 0;
                } else {
                    videoBufferInfo.presentationTimeUs = videoExtractor.getSampleTime();
                    videoBufferInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                    muxer.writeSampleData(videoTrack, videoBuf, videoBufferInfo);
                    videoExtractor.advance();
                    frameCount++;
                    Log.d("TAG",
                            "Frame (" + frameCount + ") Video PresentationTimeUs:" + videoBufferInfo.presentationTimeUs
                                    + " Flags:" + videoBufferInfo.flags + " Size(KB) " + videoBufferInfo.size / 1024);
                }
            }

            muxer.stop();
            muxer.release();
            mutedUri = Uri.parse(outputFile);
        } catch (IOException e) {
            Log.d("TAG", "Mixer Error 1 " + e.getMessage());
        } catch (Exception e) {
            Log.d("TAG", "Mixer Error 2 " + e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void uploadVideo() {
        showProgressDialog("Please wait ...");
        if (muteSwitch.isChecked()) {
            MediaMetadataRetriever m = new MediaMetadataRetriever();
            m.setDataSource(originalUri.getPath());
            if (Build.VERSION.SDK_INT >= 17) {
                vrotation = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                Log.e("Before Upload Rotation", "" + vrotation);
            }

            muteVideo();
            if (null == mutedUri) {
                showToast(getString(R.string.video_upload_fail));
            } else {
                contentUri = AppUtils.exportToGallery(mutedUri.getPath(), getContentResolver(), this);
                contentUri = AppUtils.getVideoUriFromMediaProvider(mutedUri.getPath(), getContentResolver());
            }
        } else {
            contentUri = AppUtils.exportToGallery(originalUri.getPath(), getContentResolver(), this);
            contentUri = AppUtils.getVideoUriFromMediaProvider(originalUri.getPath(), getContentResolver());
        }
        removeProgressDialog();
        resumeUpload();
    }


    public void resumeUpload() {
        getBlogPage();
    }

    private void launchUploadActivity() {
        if (contentUri != null && signIn) {
            Data uriData = new Data.Builder()
                    .putString("ContentUrl", contentUri.toString())
                    .putString("categoryId", categoryId)
                    .putString("duration", duration)
                    .putString("thumbnailTime", thumbnailTime)
                    .putString("title", videoTitleEditText.getText().toString())
                    .putString("comingFrom", comingFrom)
                    .putString("challengeId", challengeId)
                    .putString("originalPath", originalPath)
                    // .putString("workRequestId", mRequest.getId().toString())
                    .build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(
                    NetworkType.CONNECTED).build();

            request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setConstraints(constraints)
                    .addTag("VideoUploading")
                    .setInputData(uriData)
                    .build();

            workManager.enqueue(request);
            removeProgressDialog();
            popup.setVisibility(View.VISIBLE);

        }
    }

    private void getBlogPage() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationApi = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationApi
                .getUserDetails(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(onLoginResponseReceivedListener);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

    }

    Callback<UserDetailResponse> onLoginResponseReceivedListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            removeProgressDialog();
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (responseData.getData().get(0).getResult().getBlogTitleSlug() == null || responseData.getData()
                        .get(0).getResult().getBlogTitleSlug().isEmpty()) {
                    Intent intent = new Intent(AddVideoDetailsActivity.this, BlogSetupActivity.class);
                    intent.putExtra("BlogTitle", responseData.getData().get(0).getResult().getBlogTitle());
                    intent.putExtra("email", responseData.getData().get(0).getResult().getEmail());
                    intent.putExtra("comingFrom", "Videos");
                    startActivity(intent);
                } else if (responseData.getData().get(0).getResult().getBlogTitleSlug() != null || !responseData
                        .getData().get(0).getResult().getBlogTitleSlug().isEmpty()) {
                    showProgressDialog(getResources().getString(R.string.please_wait));
                    pref = getApplicationContext().getSharedPreferences(COMMON_PREF_FILE, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("blogSetup", true);
                    Log.e("blog setup in update ui", true + "");
                    editor.commit();
                    launchUploadActivity();
                }
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));

        }
    };

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }

    private void mp4mux(String audioFileName) {
        try {
            String fname = AppUtils.getFileNameFromUri(this, originalUri);
            Movie movie = new Movie();
            Movie m = MovieCreator.build(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/" + fname);
            Track vtrack = null;
            for (Track track : m.getTracks()) {
                if ("soun".equals(track.getHandler())) {
                    System.err.println("Adding audio track to new movie");
                } else if ("vide".equals(track.getHandler())) {
                    System.err.println("Adding video track to new movie");
                    vtrack = track;
                    movie.addTrack(track);
                } else {
                    System.err.println("Adding " + track.getHandler() + " track to new movie");
                }
            }

            AACTrackImpl audioTrack = new AACTrackImpl(new FileDataSourceImpl(
                    Environment.getExternalStorageDirectory() + "/MyCity4Kids/" + audioFileName));
            int audioDuration = (int) Math.ceil(trackDuration(audioTrack));
            int videoDuration = (int) Math.ceil(trackDuration(vtrack));
            System.out.println("videoduration:" + videoDuration);
            System.out.println("audioDuration:" + audioDuration);
            System.out.println("video Samples:" + vtrack.getSamples().size());
            System.out.println("audio Samples:" + audioTrack.getSamples().size());
            if (audioDuration > videoDuration) {
                int factor = audioDuration / videoDuration;
                CroppedTrack croppedTrack = new CroppedTrack(audioTrack, 0, audioTrack.getSamples().size() / factor);
                movie.addTrack(croppedTrack);
            } else {
                movie.addTrack(audioTrack);
            }

            String filePath = Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/modifiedVideo.mp4";
            File fi = new File(filePath);
            if (fi.exists()) {
                fi.delete();
            }

            Container mp4file = new DefaultMp4Builder().build(movie);
            FileChannel fc = new FileOutputStream(
                    new File(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/modifiedVideo.mp4"))
                    .getChannel();
            mp4file.writeContainer(fc);
            fc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double trackDuration(Track track) {
        return (double) track.getDuration() / track.getTrackMetaData().getTimescale();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("VideoUpload", "signInAnonymously:success");
                            signIn = true;

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                            Toast.makeText(AddVideoDetailsActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
