package com.mycity4kids.ui.videochallengenewui.activity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mycity4kids.R;
import com.mycity4kids.base.BaseActivity;

public class ExoplayerVideoChallengePlayViewActivity extends BaseActivity {

    private static final String STATE_RESUME_WINDOW = "resumeWindow";
    private static final String STATE_RESUME_POSITION = "resumePosition";
    private static final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private PlayerView exoPlayerView;
    private MediaSource videoSource;
    private boolean exoPlayerFullscreen = false;
    private FrameLayout fullScreenButton;
    private ImageView fullScreenIcon;
    private Dialog fullScreenDialog;

    private int resumeWindow;
    private long resumePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exoplayer_challenge_player_view_activity);

        final String url = getIntent().getStringExtra("StreamUrl");
        if (savedInstanceState != null) {
            resumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            resumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            exoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }
        if (exoPlayerView == null) {
            exoPlayerView = (PlayerView) findViewById(R.id.exoPlayerView);
            initFullscreenDialog();
            initFullscreenButton();

            String userAgent = Util.getUserAgent(this, getApplicationContext().getApplicationInfo().packageName);
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, null,
                    httpDataSourceFactory);
            Uri daUri = Uri.parse(url);

            videoSource = new HlsMediaSource(daUri, dataSourceFactory, 1, null, null);
        }
        initExoPlayer();
        openFullscreenDialog();

        if (exoPlayerFullscreen) {
            ((ViewGroup) exoPlayerView.getParent()).removeView(exoPlayerView);
            fullScreenDialog.addContentView(exoPlayerView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_skrink));
            fullScreenDialog.show();
        }
    }

    private void initExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory
                .newSimpleInstance(this, new DefaultRenderersFactory(this), trackSelector, loadControl);
        exoPlayerView.setPlayer(player);

        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            exoPlayerView.getPlayer().seekTo(resumeWindow, resumePosition);
        }

        player.prepare(videoSource);
        exoPlayerView.getPlayer().setPlayWhenReady(true);
    }

    private void initFullscreenDialog() {
        fullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (exoPlayerFullscreen) {
                    finish();
                }
                super.onBackPressed();
            }
        };
    }

    private void initFullscreenButton() {
        PlaybackControlView controlView = exoPlayerView.findViewById(R.id.exo_controller);
        fullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        fullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        fullScreenButton.setOnClickListener(v -> {
            if (!exoPlayerFullscreen) {
                openFullscreenDialog();
            }
        });
    }

    private void openFullscreenDialog() {
        ((ViewGroup) exoPlayerView.getParent()).removeView(exoPlayerView);
        fullScreenDialog.addContentView(exoPlayerView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_skrink));
        exoPlayerFullscreen = true;
        fullScreenDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayerView.getPlayer().setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayerView.getPlayer().release();
    }
}
