package com.mycity4kids.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mycity4kids.R;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.adapter.VideoRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.List;

public class ExoPlayerRecyclerView extends RecyclerView {

    private static final String TAG = "ExoPlayerRecyclerView";

    private List<VlogsListingAndDetailResult> videoInfoList = new ArrayList<>();
    private List<VlogsListingAndDetailResult> videoInfoListHeader = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    public SimpleExoPlayer player;
    public PlayerView videoSurfaceView;
    private ImageView coverImage;
    public RelativeLayout videoCell;
    public RelativeLayout frameLayout;

    private ProgressBar progressBar;
    private Context appContext;
    private Context context;
    private String uriString;
    private MediaSource videoSource;


    /**
     * the position of playing video.
     */
    private int playPosition = -1;

    private boolean addedVideo = false;
    private View rowParent;
    private RecyclerView recyclerView;

    public ExoPlayerRecyclerView(Context context) {
        super(context);
        initialize(context);
    }

    public ExoPlayerRecyclerView(Context context,
            AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ExoPlayerRecyclerView(Context context,
            AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setVideoInfoList(Context context, ArrayList<VlogsListingAndDetailResult> videoInfoList) {
        this.context = context;
        this.videoInfoList = videoInfoList;

    }

    //remove the player from the row
    private void removeVideoView(PlayerView videoView) {

        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            addedVideo = false;
        }

    }

    //play the video in the row
    public void playVideo(boolean autoScroll) {
        int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

        if (endPosition - startPosition > 1) {
            endPosition = startPosition + 1;
        }

        if (startPosition < 0 || endPosition < 0) {
            return;
        }

        int targetPosition;
        if (startPosition != endPosition) {
            int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
            int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);
            targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
        } else {
            targetPosition = startPosition;
        }

        if ((targetPosition < 0 || targetPosition == playPosition) && !autoScroll) {
            return;
        }
        playPosition = targetPosition;
        if (videoSurfaceView == null) {
            return;
        }
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        videoSurfaceView.setVisibility(INVISIBLE);
        removeVideoView(videoSurfaceView);

        // get target View targetPosition in RecyclerView
        int at;
        if (autoScroll) {
            at = (targetPosition - 1) - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else {
            at = targetPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        }
        View child = getChildAt(at);
        if (child == null) {
            return;
        }

        VideoRecyclerViewAdapter.ViewHolder holder = (VideoRecyclerViewAdapter.ViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }
        videoCell = holder.videoCell;
        coverImage = holder.coverImageView;
        progressBar = holder.progressBar;
        frameLayout = holder.itemView.findViewById(R.id.video_layout);
        Glide.with(appContext)
                .asBitmap()
                .load(videoInfoList.get(targetPosition).getThumbnail())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap,
                            Transition<? super Bitmap> transition) {
                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight();
                        Log.e("width and height", w + " * " + h);
                        float ratio = ((float) h / (float) w);
                        frameLayout.getLayoutParams().height = Math
                                .round(ratio * appContext.getResources().getDisplayMetrics().widthPixels);
                        frameLayout.getLayoutParams().width = Math
                                .round(appContext.getResources().getDisplayMetrics().widthPixels);

                        Log.e("from ratio",
                                w + "   " + h + "   " + frameLayout.getLayoutParams().height + " * " + frameLayout
                                        .getLayoutParams().width);
                    }
                });

        frameLayout.addView(videoSurfaceView);

        addedVideo = true;
        rowParent = holder.itemView;
        videoSurfaceView.requestFocus();
        // Bind the player to the view.
        videoSurfaceView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        uriString = videoInfoList.get(targetPosition).getUrl();
        if (uriString != null) {
            String userAgent = Util.getUserAgent(appContext, appContext.getApplicationInfo().packageName);
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(appContext, null,
                    httpDataSourceFactory);
            Uri daUri = Uri.parse(uriString);
            videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(daUri);
            // Prepare the player with the source.
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
            ((ParallelFeedActivity) context).hitUpdateViewCountApi(videoInfoList.get(targetPosition).getId());
            ((ParallelFeedActivity) context).pushMomVlogViewEvent();
        }
    }

    public void restart(boolean haveResumePosition, int resumeWindow, long resumePosition) {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(appContext, trackSelector, loadControl);

        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        videoSurfaceView.setUseController(true);
        videoSurfaceView.setPlayer(player);
        scrollScreen();

        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
    }

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location01 = new int[2];
        child.getLocationInWindow(location01);

        if (location01[1] < 0) {
            return location01[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location01[1];
        }
    }


    private void initialize(Context context) {

        appContext = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;

        screenDefaultHeight = point.y;
        videoSurfaceView = new PlayerView(appContext);
        videoSurfaceView.setFastForwardIncrementMs(5000);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(appContext, trackSelector, loadControl);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        // Bind the player to the view.
        videoSurfaceView.setUseController(true);
        videoSurfaceView.setPlayer(player);
        scrollScreen();
    }

    public void scrollScreen() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (playPosition < videoInfoList.size() - 1) {
                        playVideo(false);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (addedVideo && rowParent != null && rowParent.equals(view)) {
                    removeVideoView(videoSurfaceView);
                    playPosition = -1;
                    videoSurfaceView.setVisibility(INVISIBLE);
                }

            }
        });

        player.addListener(new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case Player.STATE_BUFFERING:
                        videoSurfaceView.setAlpha(0.5f);
                        System.out.println("playingposition-----" + playPosition);
                        Log.e(TAG, "onPlayerStateChanged: Buffering ");
                        if (progressBar != null) {
                            progressBar.setVisibility(VISIBLE);
                            videoCell.setBackgroundColor(getResources().getColor(R.color.black_color));
                        }

                        break;
                    case Player.STATE_ENDED:
                        player.seekTo(0);
                        if (playPosition < videoInfoList.size() - 1) {
                            recyclerView.smoothScrollToPosition(playPosition + 1);
                            playVideo(true);
                        }
                        if (((ParallelFeedActivity) context).exoPlayerFullscreen) {
                            ((ParallelFeedActivity) context).closeFullscreenDialog();
                        }
                        break;
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready ");
                        System.out.println("playingposition-----" + playPosition);
                        if (progressBar != null) {
                            progressBar.setVisibility(GONE);
                        }
                        videoCell.setBackgroundColor(getResources().getColor(R.color.black_color));
                        videoSurfaceView.setVisibility(VISIBLE);
                        videoSurfaceView.setAlpha(1);
                        coverImage.setVisibility(GONE);

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    public PlayerView getSimpleExo() {
        return videoSurfaceView;
    }

}

