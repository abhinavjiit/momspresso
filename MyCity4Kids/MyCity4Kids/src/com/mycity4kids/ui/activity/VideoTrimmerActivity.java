package com.mycity4kids.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class VideoTrimmerActivity extends AppCompatActivity implements OnTrimVideoListener {

    private K4LVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    Handler mHandler;
    static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    //prevent multiple instances
    boolean isActivityLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);
        Utils.pushOpenScreenEvent(this, "VideoTrimmerActivity", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        Intent extraIntent = getIntent();
        String path = "";
        mHandler = new Handler();
        if (extraIntent != null) {
            path = extraIntent.getStringExtra(EXTRA_VIDEO_PATH);
        }
        AppUtils.deleteDirectoryContent("MyCity4Kids");
        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
            mVideoTrimmer.setMaxDuration(60);
            mVideoTrimmer.setOnTrimVideoListener(this);
//            mVideoTrimmer.setOnK4LVideoListener(this);
            AppUtils.createDirIfNotExists("MyCity4Kids");
            mVideoTrimmer.setDestinationPath(Environment.getExternalStorageDirectory() + "/MyCity4Kids/");
            try {
                mVideoTrimmer.setVideoURI(Uri.parse(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
//            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

//    public void onTrimStarted() {
//        mProgressDialog.show();
//    }

    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(VideoTrimmerActivity.this, getString(R.string.video_saved_at, uri.getPath()), Toast.LENGTH_SHORT).show();
//            }
//        });
        if (!isActivityLaunched) {
            isActivityLaunched = true;
            Intent intent = new Intent(VideoTrimmerActivity.this, AddVideoDetailsActivity.class);
            intent.putExtra("uriPath", uri.getPath());
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

//    public void onError(final String message) {
//        mProgressDialog.cancel();
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(VideoTrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void onVideoPrepared() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(VideoTrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

}
