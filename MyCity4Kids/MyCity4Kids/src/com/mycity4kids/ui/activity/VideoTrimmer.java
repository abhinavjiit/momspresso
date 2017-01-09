package com.mycity4kids.ui.activity;

import android.net.Uri;
import android.os.Bundle;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;

/**
 * Created by hemant on 6/1/17.
 */
public class VideoTrimmer extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_trimmer);
        K4LVideoTrimmer videoTrimmer = (K4LVideoTrimmer) findViewById(R.id.timeLine);

        if (videoTrimmer != null) {
//            videoTrimmer.setVideoURI(Uri.parse(path));
        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
