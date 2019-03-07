package com.mycity4kids.ui.videochallengenewui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

public class ExoplayerVideoChallengePlayViewActivity extends BaseActivity {
    private Toolbar mToolbar;
    private SimpleExoPlayerView simpleExoPlayerView;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exoplayer_challenge_player_view_activity);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoPlayerView);
    }

    @Override
    protected void updateUi(Response response) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
