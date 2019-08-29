package com.mycity4kids.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
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
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by hemant on 10/1/17.
 */
public class AddGroupVideoPostActivity extends BaseActivity implements View.OnClickListener, EasyVideoCallback {

    private final static int MAX_VOLUME = 100;

    private Switch muteSwitch;
    private Toolbar mToolbar;
    private TextView saveUploadTextView;

    private Uri originalUri;
    private String vRotation;

    private String originalPath;
    Uri contentURI;
    private Uri mutedUri;
    private EasyVideoPlayer player;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group_video_post_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "CreateVideoScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        muteSwitch = (Switch) findViewById(R.id.muteVideoSwitch);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        player = (EasyVideoPlayer) findViewById(R.id.player);
        saveUploadTextView = (TextView) findViewById(R.id.saveUploadTextView);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/oswald_regular.ttf");
        muteSwitch.setTypeface(font);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Video");

        muteSwitch.setOnClickListener(this);

        if (Build.VERSION.SDK_INT < 18) {
            muteSwitch.setVisibility(View.GONE);
        }

        originalPath = getIntent().getStringExtra("uriPath");

        saveUploadTextView.setOnClickListener(this);

        player.setCallback(this);
        player.setAutoPlay(true);
        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.fromFile(new File(originalPath)));
        //specify the location of media file
        originalUri = Uri.parse(originalPath);
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
//            case R.id.audioTextView:
//                openAudioFilePickerDialog();
//                break;
            case R.id.saveUploadTextView:
                uploadVideo();
                break;
//            case R.id.removeCustomAudioTextView:
//                restoreOriginalSound();
//                removeCustomAudioTextView.setVisibility(View.GONE);
//                audioTextView.setText(getString(R.string.add_video_details_add_music));
//                audioTextView.setTextColor(ContextCompat.getColor(this, R.color.add_video_details_add_music));
//                break;
        }
    }

//    private void restoreOriginalSound() {
//        player.stop();
//        player.setSource(Uri.fromFile(new File(originalPath)));
//        player.start();
//    }

//    private void openAudioFilePickerDialog() {
//        AudioPickerDialogFragment filterTopicsDialogFragment = new AudioPickerDialogFragment();
//        Bundle args = new Bundle();
//        filterTopicsDialogFragment.setArguments(args);
//        FragmentManager fm = getSupportFragmentManager();
//        filterTopicsDialogFragment.show(fm, "Audio Picker");
//    }

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
                vRotation = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                Log.e("Before Upload Rotation", "" + vRotation);
            }

            muteVideo();
            if (null == mutedUri) {
                showToast(getString(R.string.video_upload_fail));
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

    private void mp4mux(String audioFileName) {
        try {
            String fname = AppUtils.getFileNameFromUri(this, originalUri);
            Movie movie = new Movie();
            Movie m = MovieCreator.build(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/" + fname);
            Track vTrack = null;
            for (Track track : m.getTracks()) {
                if ("soun".equals(track.getHandler())) {
                    System.err.println("Adding audio track to new movie");
                } else if ("vide".equals(track.getHandler())) {
                    System.err.println("Adding video track to new movie");
                    vTrack = track;
                    movie.addTrack(track);
                } else {
                    System.err.println("Adding " + track.getHandler() + " track to new movie");
                }
            }

            AACTrackImpl audioTrack = new AACTrackImpl(new FileDataSourceImpl(Environment.getExternalStorageDirectory() + "/MyCity4Kids/" + audioFileName));
            int audioDuration = (int) Math.ceil(trackDuration(audioTrack));
            int videoDuration = (int) Math.ceil(trackDuration(vTrack));
            System.out.println("videoduration:" + videoDuration);
            System.out.println("audioDuration:" + audioDuration);
            System.out.println("video Samples:" + vTrack.getSamples().size());
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
            FileChannel fc = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/modifiedVideo.mp4")).getChannel();
            mp4file.writeContainer(fc);
            fc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double trackDuration(Track track) {
        return (double) track.getDuration() / track.getTrackMetaData().getTimescale();
    }
}
