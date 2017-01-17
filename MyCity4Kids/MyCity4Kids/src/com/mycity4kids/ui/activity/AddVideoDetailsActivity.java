package com.mycity4kids.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by hemant on 10/1/17.
 */
public class AddVideoDetailsActivity extends BaseActivity implements View.OnClickListener, EasyVideoCallback {

    private final static int MAX_VOLUME = 100;

    private EditText videoTitleEditText;
    private Switch muteSwitch;
    private Toolbar mToolbar;

    private Uri originalUri;
    private String vRotation;

    Uri contentURI;
    private Uri mutedUri;
    private EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_video_details_activity);
        Utils.pushOpenScreenEvent(this, "AddVideoDetailsActivity", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        videoTitleEditText = (EditText) findViewById(R.id.videoTitleEditText);
        muteSwitch = (Switch) findViewById(R.id.muteVideoSwitch);
//        videoView = (VideoView) findViewById(R.id.videoView1);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        player = (EasyVideoPlayer) findViewById(R.id.player);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Video");

        muteSwitch.setOnClickListener(this);

        if (Build.VERSION.SDK_INT < 18) {
            muteSwitch.setVisibility(View.GONE);
        }

        String uriPath = getIntent().getStringExtra("uriPath");

        player.setCallback(this);
        player.setAutoPlay(true);
        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.fromFile(new File(uriPath)));
        //specify the location of media file
        originalUri = Uri.parse(uriPath);
        // Starts or resumes playback.
        player.start();
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
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.muteVideoSwitch:
                if (muteSwitch.isChecked()) {
                    final float volume = (float) (1 - (Math.log(MAX_VOLUME - 0) / Math.log(MAX_VOLUME)));
                    player.setVolume(volume, volume);
                } else {
                    final float volume = (float) (1 - (Math.log(MAX_VOLUME - 99) / Math.log(MAX_VOLUME)));
                    player.setVolume(volume, volume);
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void muteVideo() {
        String fname = AppUtils.getFileNameFromUri(this, originalUri);
//        String filePath = Environment.getExternalStorageDirectory() + "/MyCity4Kids/" + "mute_" + fname;
//        File fi = new File(filePath);
//        if (fi.exists()) {
//            fi.delete();
//        }
        String outputFile = "";
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/MyCity4Kids/" + "mute_" + fname);
            file.createNewFile();
            outputFile = file.getAbsolutePath();

            MediaExtractor videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(originalUri.getPath());

//            MediaExtractor audioExtractor = new MediaExtractor();

            Log.d("TAG", "Video Extractor Track Count " + videoExtractor.getTrackCount());
//            Log.d("TAG", "Audio Extractor Track Count " + audioExtractor.getTrackCount());

            MediaMuxer muxer = new MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            videoExtractor.selectTrack(0);
            MediaFormat videoFormat = videoExtractor.getTrackFormat(0);
//            videoFormat.setInteger("rotation-degrees", 0);
            int videoTrack = muxer.addTrack(videoFormat);

            Log.d("TAG", "Video Format " + videoFormat.toString());

            boolean sawEOS = false;
            int frameCount = 0;
            int offset = 100;
            int sampleSize = 256 * 1024;
            ByteBuffer videoBuf = ByteBuffer.allocate(sampleSize);
            ByteBuffer audioBuf = ByteBuffer.allocate(sampleSize);
            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
//            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

            videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            muxer.setOrientationHint(Integer.parseInt(vRotation));
            muxer.start();

            while (!sawEOS) {
                videoBufferInfo.offset = offset;
                videoBufferInfo.size = videoExtractor.readSampleData(videoBuf, offset);
                if (videoBufferInfo.size < 0) {
                    Log.d("TAG", "saw input EOS.");
                    sawEOS = true;
                    videoBufferInfo.size = 0;
                } else {
                    videoBufferInfo.presentationTimeUs = videoExtractor.getSampleTime();
                    videoBufferInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                    muxer.writeSampleData(videoTrack, videoBuf, videoBufferInfo);
                    videoExtractor.advance();
                    frameCount++;
                    Log.d("TAG", "Frame (" + frameCount + ") Video PresentationTimeUs:" + videoBufferInfo.presentationTimeUs + " Flags:" + videoBufferInfo.flags + " Size(KB) " + videoBufferInfo.size / 1024);
                }
            }

//            Toast.makeText(getApplicationContext(), "frame:" + frameCount, Toast.LENGTH_SHORT).show();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.uploadButton:
                if (StringUtils.isNullOrEmpty(videoTitleEditText.getText().toString())) {
//                    showToast("Please enter the title to continue");
                    videoTitleEditText.setFocusableInTouchMode(true);
                    videoTitleEditText.setError("Please enter the title of the video");
                    videoTitleEditText.requestFocus();
                } else {
                    uploadVideo();
                }
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
                vRotation = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                Log.e("Before Upload Rotation", "" + vRotation);
            }

            muteVideo();
            if (null == mutedUri) {
                showToast("Unable to upload the video. Please try again later.");
            } else {
                contentURI = AppUtils.exportToGallery(mutedUri.getPath(), getContentResolver(), this);
                contentURI = AppUtils.getVideoUriFromMediaProvider(mutedUri.getPath(), getContentResolver());
            }
        } else {
            contentURI = AppUtils.exportToGallery(originalUri.getPath(), getContentResolver(), this);
            contentURI = AppUtils.getVideoUriFromMediaProvider(originalUri.getPath(), getContentResolver());
        }
        removeProgressDialog();
        resumeUpload();
    }

    public void resumeUpload() {
        Intent intt = new Intent(this, VideoUploadProgressActivity.class);
        intt.putExtra("uri", contentURI);
        intt.putExtra("title", videoTitleEditText.getText().toString());
        startActivity(intt);
    }

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
}
