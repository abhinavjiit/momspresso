/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycity4kids.ui.activity;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.youtube.DeveloperKey;
import com.mycity4kids.youtube.YouTubeFailureRecoveryActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * displays a YouTube Video in a {@link YouTubePlayerView}.
 * <p/>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend {@link YouTubeBaseActivity}.
 */
public class YouTubeVideoActivity extends YouTubeFailureRecoveryActivity {
    String youTubeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerview_demo);
        Utils.pushOpenScreenEvent(YouTubeVideoActivity.this, "Youtube Video", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String youTubeUrl = bundle.getString("youTubeUrl");
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(youTubeUrl);

            if (matcher.find()) {
                youTubeId = matcher.group();
            }
        }

        try {
            YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
            youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            if (youTubeId != null) {
                player.cueVideo(youTubeId);
            } else
                player.cueVideo("wKJ9KzGQq0w");
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}
