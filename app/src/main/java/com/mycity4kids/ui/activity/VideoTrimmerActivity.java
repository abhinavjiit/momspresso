package com.mycity4kids.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.videotrimmer.K4LVideoTrimmer;
import com.mycity4kids.videotrimmer.interfaces.OnTrimVideoListener;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;


public class VideoTrimmerActivity extends BaseActivity implements OnTrimVideoListener {

    private K4LVideoTrimmer videoTrimmer;
    private ProgressDialog progressDialog;
    static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    //prevent multiple instances
    boolean isActivityLaunched = false;
    private String categoryId;
    private String duration;
    private String challengeId;
    private String challengeName;
    private String comingFrom;
    private Topics selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "VideoTrimmerActivity",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        Intent extraIntent = getIntent();
        String path = "";
        if (extraIntent != null) {
            path = extraIntent.getStringExtra(EXTRA_VIDEO_PATH);
            categoryId = extraIntent.getStringExtra("categoryId");
            selectedCategory = extraIntent.getParcelableExtra("selectedCategory");
            duration = extraIntent.getStringExtra("duration");
            comingFrom = extraIntent.getStringExtra("comingFrom");
            if ("Challenge".equals(comingFrom)) {
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.trimming_progress));

        videoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        ((BaseApplication) getApplication()).setView(videoTrimmer);

        if (videoTrimmer != null && !StringUtils.isNullOrEmpty(duration)) {
            videoTrimmer.setMaxDuration(Integer.parseInt(duration));
            videoTrimmer.setOnTrimVideoListener(this);
            AppUtils.createDirIfNotExists("MyCity4Kids/videos");
            videoTrimmer.setDestinationPath(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/");
            try {
                videoTrimmer.setVideoURI(Uri.parse(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTrimStarted() {

    }

    @Override
    public void getResult(final Uri uri) {
        progressDialog.cancel();
        if (!isActivityLaunched) {
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
                intent.putExtra("selectedCategory", selectedCategory);
            }
            intent.putExtra("thumbnailTime", "" + videoTrimmer.getTimeStampForIFrame());
            if (uri.getPath().contains("/MyCity4Kids/videos/")) {
                intent.putExtra("uriPath", uri.getPath());
            } else {
                File source = new File(uri.getPath());
                File destination = new File(
                        Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/videos.mp4");
                try {
                    FileUtils.copyFile(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra("uriPath",
                        Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/videos.mp4");
            }

            startActivity(intent);
            finish();
        }

    }

    @Override
    public void cancelAction() {
        progressDialog.cancel();
        videoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, getString(R.string.upload_larger_video), Toast.LENGTH_SHORT).show();
    }

}
