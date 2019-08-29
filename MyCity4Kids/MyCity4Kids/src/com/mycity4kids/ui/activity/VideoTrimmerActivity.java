package com.mycity4kids.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.videotrimmer.K4LVideoTrimmer;
import com.mycity4kids.videotrimmer.interfaces.OnTrimVideoListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class VideoTrimmerActivity extends BaseActivity implements OnTrimVideoListener {

    private K4LVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    Handler mHandler;
    static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    //prevent multiple instances
    boolean isActivityLaunched = false;
    private String categoryId;
    private String duration;
    private String thumbnailTime;
    private String challengeId, challengeName, comingFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "VideoTrimmerActivity", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        Intent extraIntent = getIntent();
        String path = "";
        mHandler = new Handler();
        if (extraIntent != null) {
            path = extraIntent.getStringExtra(EXTRA_VIDEO_PATH);
            categoryId = extraIntent.getStringExtra("categoryId");
            duration = extraIntent.getStringExtra("duration");
            comingFrom = extraIntent.getStringExtra("comingFrom");
            if (comingFrom.equals("Challenge")) {
                challengeId = extraIntent.getStringExtra("ChallengeId");
                challengeName = extraIntent.getStringExtra("ChallengeName");
            }
        }
        if (path != null && path.contains(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/")) {
            Log.d("TRIM Video", "Video Picked from Mycity folder");
        } else {
            AppUtils.deleteDirectoryContent("MyCity4Kids/videos");
        }

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        ((BaseApplication) getApplication()).setView(mVideoTrimmer);

        if (mVideoTrimmer != null) {
            mVideoTrimmer.setMaxDuration(Integer.parseInt(duration));
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
    protected void updateUi(Response response) {

    }

    @Override
    public void onTrimStarted() {

    }

    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();
        if (!isActivityLaunched) {
//           mVideoTrimmer.getTimeStampForIFrame();
            isActivityLaunched = true;
            Intent intent = new Intent(VideoTrimmerActivity.this, AddVideoDetailsActivity.class);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("duration", duration);
            if (comingFrom.equals("Challenge")) {
                intent.putExtra("ChallengeId", challengeId);
                intent.putExtra("ChallengeName", challengeName);
                intent.putExtra("comingFrom", "Challenge");
            } else {
                intent.putExtra("comingFrom", "notFromChallenge");
            }
            intent.putExtra("thumbnailTime", "" + mVideoTrimmer.getTimeStampForIFrame());
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

    @Override
    public void onError(String message) {
        Toast.makeText(this, getString(R.string.upload_larger_video), Toast.LENGTH_SHORT).show();
    }

}
