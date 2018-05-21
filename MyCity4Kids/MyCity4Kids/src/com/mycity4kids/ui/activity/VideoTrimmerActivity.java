package com.mycity4kids.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

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
        if (path.contains(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/")) {
            Log.d("TRIM Video", "Video Picked from Mycity folder");
        } else {
            AppUtils.deleteDirectoryContent("MyCity4Kids/videos");
        }

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));

        if (mVideoTrimmer != null) {
            mVideoTrimmer.setMaxDuration(58);
            mVideoTrimmer.setOnTrimVideoListener(this);
//            mVideoTrimmer.setOnK4LVideoListener(this);
            AppUtils.createDirIfNotExists("MyCity4Kids/videos");
            mVideoTrimmer.setDestinationPath(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/");
            try {
                mVideoTrimmer.setVideoURI(Uri.parse(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();
        if (!isActivityLaunched) {
            isActivityLaunched = true;
            Intent intent = new Intent(VideoTrimmerActivity.this, AddVideoDetailsActivity.class);
            if (uri.getPath().contains("/MyCity4Kids/videos/")) {
                intent.putExtra("uriPath", uri.getPath());
            } else {
                File source = new File(uri.getPath());
                File destination = new File(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/videos.mp4");
                try {
                    FileUtils.copyFile(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra("uriPath", Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/videos.mp4");
            }

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

}
