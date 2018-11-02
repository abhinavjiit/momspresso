package com.mycity4kids.ui.activity;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;

/**
 * Created by hemant on 18/10/18.
 */


public class ExoPlayerTestActivity extends BaseActivity {
    @Override
    protected void updateUi(Response response) {

    }
//    PlayerView playerView;
//    private boolean playWhenReady = true;
//    private int currentWindow = 0;
//    private long playbackPosition = 0;
//    private ExoPlayer player;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.exo_player_activity);
//
//        playerView = (PlayerView) findViewById(R.id.exoPlayerView);
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        //--------------------------------------
//        //Creating default track selector
//        //and init the player
//        TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
//        player = ExoPlayerFactory.newSimpleInstance(
//                new DefaultRenderersFactory(this),
//                new DefaultTrackSelector(adaptiveTrackSelection),
//                new DefaultLoadControl());
//
//        //init the player
//        playerView.setPlayer(player);
//
//        //-------------------------------------------------
//        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
//        // Produces DataSource instances through which media data is loaded.
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
//                Util.getUserAgent(this, "Exo2"), defaultBandwidthMeter);
//
//        //-----------------------------------------------
//        //Create media source
//        String hls_url = "https://www.momspresso.com/new-videos/v1/test/playlist.m3u8";
//        Uri uri = Uri.parse(hls_url);
//        Handler mainHandler = new Handler();
//        MediaSource mediaSource = new HlsMediaSource(uri,
//                dataSourceFactory, mainHandler, null);
//        player.prepare(mediaSource);
//
//
//        player.setPlayWhenReady(playWhenReady);
//        player.addListener(new ExoPlayer.EventListener() {
//            @Override
//            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
//
//            }
//
//            @Override
//            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
//            }
//
//            @Override
//            public void onLoadingChanged(boolean isLoading) {
//
//            }
//
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                switch (playbackState) {
//                    case ExoPlayer.STATE_READY:
////                        loading.setVisibility(View.GONE);
//                        break;
//                    case ExoPlayer.STATE_BUFFERING:
////                        loading.setVisibility(View.VISIBLE);
//                        break;
//                }
//            }
//
//            @Override
//            public void onRepeatModeChanged(int repeatMode) {
//
//            }
//
//            @Override
//            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//
//            }
//
//            @Override
//            public void onPlayerError(ExoPlaybackException error) {
//
//            }
//
//            @Override
//            public void onPositionDiscontinuity(int reason) {
//
//            }
//
//            @Override
//            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
//            }
//
//            @Override
//            public void onSeekProcessed() {
//
//            }
//        });
//        player.seekTo(currentWindow, playbackPosition);
//        player.prepare(mediaSource, true, false);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (Util.SDK_INT <= 23) {
//            releasePlayer();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        if (Util.SDK_INT > 23) {
//            releasePlayer();
//        }
//    }
//
//    private void releasePlayer() {
//        if (player != null) {
//            playbackPosition = player.getCurrentPosition();
//            currentWindow = player.getCurrentWindowIndex();
//            playWhenReady = player.getPlayWhenReady();
//            player.release();
//            player = null;
//        }
//    }
//
//    @Override
//    protected void updateUi(Response response) {
//
//    }
//

}
