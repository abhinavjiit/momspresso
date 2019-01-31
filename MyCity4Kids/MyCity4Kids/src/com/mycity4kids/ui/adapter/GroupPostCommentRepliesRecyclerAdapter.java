package com.mycity4kids.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 23/5/18.
 */

public class GroupPostCommentRepliesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Handler.Callback {

    private static final int RESPONSE_TYPE_COMMENT = 0;
    private static final int RESPONSE_TYPE_REPLY = 1;
    private static final int RESPONSE_TYPE_AUDIO_COMMENT = 2;
    private static final int RESPONSE_TYPE_AUDIO_REPLY = 3;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupPostCommentResult> repliesList;
    private RecyclerViewClickListener mListener;
    private final String localizedNotHelpful, localizedHelpful;
    private int playingPosition;
    private ProgressDialog mProgressDialog;
    private AudioCommentViewHolder playingHolder;
    private MediaPlayer mediaPlayer;
    private static final int MSG_UPDATE_SEEK_BAR = 1845;
    private static final int MSG_UPDATE_TIME = 1846;
    private Handler mHandler;
    private long totalDuration, currentDuration;
    private boolean isComment = false;

    public GroupPostCommentRepliesRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        localizedHelpful = mContext.getString(R.string.groups_post_helpful);
        localizedNotHelpful = mContext.getString(R.string.groups_post_nothelpful);
        mHandler = new Handler(this);
        this.playingPosition = -1;
    }

    public void setData(ArrayList<GroupPostCommentResult> repliesList) {
        this.repliesList = repliesList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (repliesList.get(position).getCommentType() == AppConstants.COMMENT_TYPE_AUDIO)
                return RESPONSE_TYPE_AUDIO_COMMENT;
            else
                return RESPONSE_TYPE_COMMENT;
        } else {
            if (repliesList.get(position).getCommentType() == AppConstants.COMMENT_TYPE_AUDIO)
                return RESPONSE_TYPE_AUDIO_REPLY;
            else
                return RESPONSE_TYPE_REPLY;
        }
    }

    @Override
    public int getItemCount() {
        return repliesList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RESPONSE_TYPE_REPLY) {
            isComment = true;
            View v0 = mInflator.inflate(R.layout.group_post_replies_cell, parent, false);
            return new RepliesViewHolder(v0);
        } else if (RESPONSE_TYPE_AUDIO_COMMENT == viewType) {
            isComment = true;
            View v0 = mInflator.inflate(R.layout.group_post_audio_comment_cell, parent, false);
            return new AudioCommentViewHolder(v0);
        } else if (RESPONSE_TYPE_AUDIO_REPLY == viewType) {
            isComment = false;
            View v0 = mInflator.inflate(R.layout.group_post_audio_comment_cell, parent, false);
            return new AudioCommentViewHolder(v0);
        } else {
            isComment = true;
            View v0 = mInflator.inflate(R.layout.group_post_comment_cell_test, parent, false);
            return new CommentsViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CommentsViewHolder) {
            CommentsViewHolder commentsViewHolder = (CommentsViewHolder) holder;
            if (repliesList.get(position).getIsAnnon() == 1) {
                commentsViewHolder.commentorUsernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                commentsViewHolder.commentorImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    commentsViewHolder.commentDateTextView.setVisibility(View.GONE);
                    commentsViewHolder.media.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(commentsViewHolder.media);
                } else {
                    commentsViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    commentsViewHolder.media.setVisibility(View.GONE);
                }
            } else {
                commentsViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserInfo().getFirstName()
                        + " " + repliesList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(repliesList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(commentsViewHolder.commentorImageView);
                } catch (Exception e) {
                    commentsViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    commentsViewHolder.commentDateTextView.setVisibility(View.GONE);
                    commentsViewHolder.media.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(commentsViewHolder.media);
                } else {
                    commentsViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    commentsViewHolder.media.setVisibility(View.GONE);
                }
            }
            commentsViewHolder.upvoteCommentCountTextView.setText(repliesList.get(position).getHelpfullCount() + " " + localizedHelpful);
            commentsViewHolder.downvoteCommentCountTextView.setText(repliesList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            commentsViewHolder.commentDataTextView.setText(repliesList.get(position).getContent());
            Linkify.addLinks(commentsViewHolder.commentDataTextView, Linkify.WEB_URLS);
            commentsViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            commentsViewHolder.commentDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(commentsViewHolder.commentDataTextView);
            commentsViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(repliesList.get(position).getCreatedAt()));
        } else if (holder instanceof AudioCommentViewHolder) {
            AudioCommentViewHolder audioCommentViewHolder = (AudioCommentViewHolder) holder;

            if (!isComment) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(24, 0, 0, 0);
                audioCommentViewHolder.audiotRootView.setLayoutParams(params);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 0);
                audioCommentViewHolder.audiotRootView.setLayoutParams(params);
            }
            if (repliesList.get(position).getIsAnnon() == 1) {
                audioCommentViewHolder.commentorUsernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                audioCommentViewHolder.commentorImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    audioCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                    audioCommentViewHolder.replyCommentTextView.setVisibility(View.GONE);
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                } else {
                    audioCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                    audioCommentViewHolder.replyCommentTextView.setVisibility(View.GONE);
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                }
            } else {
                audioCommentViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserInfo().getFirstName()
                        + " " + repliesList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(repliesList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(audioCommentViewHolder.commentorImageView);
                } catch (Exception e) {
                    audioCommentViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    audioCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                    audioCommentViewHolder.replyCommentTextView.setVisibility(View.GONE);
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                } else {
                    audioCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                    audioCommentViewHolder.replyCommentTextView.setVisibility(View.GONE);
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                }
            }
            if (!StringUtils.isNullOrEmpty(repliesList.get(position).getContent())) {
                audioCommentViewHolder.commentDataTextView.setVisibility(View.VISIBLE);
                audioCommentViewHolder.commentDataTextView.setText(repliesList.get(position).getContent());
                Linkify.addLinks(audioCommentViewHolder.commentDataTextView, Linkify.WEB_URLS);
                audioCommentViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
                audioCommentViewHolder.commentDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
                addLinkHandler(audioCommentViewHolder.commentDataTextView);
            } else {
                audioCommentViewHolder.commentDataTextView.setVisibility(View.GONE);
            }

            if (position == playingPosition) {
                playingHolder = audioCommentViewHolder;
                updatePlayingView();
            } else {
                updateNonPlayingView(audioCommentViewHolder);
            }

            audioCommentViewHolder.commentDateTextView.setText(DateTimeUtils.getFormattedDateGroups(repliesList.get(position).getCreatedAt()));
            if (repliesList.get(position).getChildData() == null || repliesList.get(position).getChildData().isEmpty()) {
                audioCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
            } else {
                audioCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                audioCommentViewHolder.replyCountTextView.setText("View (" + repliesList.get(position).getChildCount() + ") replies");
            }
            audioCommentViewHolder.upvoteCommentCountTextView.setText(repliesList.get(position).getHelpfullCount() + " " + localizedHelpful);
            audioCommentViewHolder.downvoteCommentCountTextView.setText(repliesList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
        } else {
            RepliesViewHolder repliesViewHolder = (RepliesViewHolder) holder;
            if (repliesList.get(position).getIsAnnon() == 1) {
                repliesViewHolder.commentorUsernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                repliesViewHolder.commentorImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    repliesViewHolder.commentDateTextView.setVisibility(View.GONE);
                    repliesViewHolder.mediaview.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(repliesViewHolder.mediaview);
                } else {
                    repliesViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    repliesViewHolder.mediaview.setVisibility(View.GONE);
                }
            } else {
                repliesViewHolder.commentorUsernameTextView.setText(repliesList.get(position).getUserInfo().getFirstName()
                        + " " + repliesList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(repliesList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(repliesViewHolder.commentorImageView);
                } catch (Exception e) {
                    repliesViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    repliesViewHolder.commentDateTextView.setVisibility(View.GONE);
                    repliesViewHolder.mediaview.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(repliesViewHolder.mediaview);
                } else {
                    repliesViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    repliesViewHolder.mediaview.setVisibility(View.GONE);
                }
            }
            repliesViewHolder.upvoteReplyCountTextView.setText(repliesList.get(position).getHelpfullCount() + " " + localizedHelpful);
            repliesViewHolder.downvoteReplyCountTextView.setText(repliesList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            repliesViewHolder.commentDataTextView.setText(repliesList.get(position).getContent());
            Linkify.addLinks(repliesViewHolder.commentDataTextView, Linkify.WEB_URLS);
            repliesViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            repliesViewHolder.commentDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(repliesViewHolder.commentDataTextView);
            repliesViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(repliesList.get(position).getCreatedAt()));
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_SEEK_BAR: {
                if (mediaPlayer != null) {
                    playingHolder.audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 1000);
                    return true;
                }
            }
            case MSG_UPDATE_TIME: {
                if (mediaPlayer != null) {
                    totalDuration = mediaPlayer.getDuration();
                    currentDuration = mediaPlayer.getCurrentPosition();
                    playingHolder.audioTimeElapsed.setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
                    return true;
                }
            }
        }
        return false;
    }


    public class AudioCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, SeekBar.OnSeekBarChangeListener {

        ImageView commentorImageView, playAudioImageView, pauseAudioImageView;
        ImageView media;
        TextView commentorUsernameTextView, audioTimeElapsed;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;
        View underlineView;
        SeekBar audioSeekBar;
        TextView upvoteCommentCountTextView, downvoteCommentCountTextView;
        LinearLayout upvoteCommentContainer, downvoteCommentContainer;
        RelativeLayout audiotRootView;

        public AudioCommentViewHolder(View view) {
            super(view);
            media = (ImageView) view.findViewById(R.id.media);
            audiotRootView = view.findViewById(R.id.commentRootView);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            upvoteCommentCountTextView = (TextView) view.findViewById(R.id.upvoteCommentTextView);
            downvoteCommentCountTextView = (TextView) view.findViewById(R.id.downvoteCommentTextView);
            upvoteCommentContainer = (LinearLayout) view.findViewById(R.id.upvoteCommentContainer);
            downvoteCommentContainer = (LinearLayout) view.findViewById(R.id.downvoteCommentContainer);
            playAudioImageView = (ImageView) view.findViewById(R.id.playAudioImageView);
            pauseAudioImageView = (ImageView) view.findViewById(R.id.pauseAudioImageView);
            audioSeekBar = (SeekBar) view.findViewById(R.id.audioSeekBar);
            audioTimeElapsed = (TextView) view.findViewById(R.id.audioTimeElapsed);

            commentDataTextView.setOnLongClickListener(this);
            view.setOnLongClickListener(this);
            replyCommentTextView.setOnClickListener(this);
            replyCountTextView.setOnClickListener(this);
            downvoteCommentContainer.setOnClickListener(this);
            upvoteCommentContainer.setOnClickListener(this);
            playAudioImageView.setOnClickListener(this);
            audioSeekBar.setOnSeekBarChangeListener(this);

            underlineView = view.findViewById(R.id.underlineView);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.playAudioImageView) {
                if (getAdapterPosition() == playingPosition) {
                    // toggle between play/pause of audio
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        updateNonPlayingView(playingHolder);
                    } else {
                        mediaPlayer.start();
                        playingHolder = this;
//                        startMediaPlayer(playingPosition);
                        updatePlayingView();
                    }
                } else {
                    // start another audio playback
                    playingPosition = getAdapterPosition();
                    if (mediaPlayer != null) {
                        if (null != playingHolder) {
                            updateNonPlayingView(playingHolder);
                        }
                        mediaPlayer.release();
                    }
                    playingHolder = this;
                    startMediaPlayer(playingPosition);
                    showProgressDialog(mContext.getString(R.string.please_wait));
                }
//                updatePlayingView();
            } else {
                mListener.onRecyclerItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onRecyclerItemClick(view, getAdapterPosition());
            return true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                mediaPlayer.seekTo(i);
                notifyItemChanged(playingPosition);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;
        ImageView media;
        TextView upvoteCommentCountTextView, downvoteCommentCountTextView;
        LinearLayout upvoteCommentContainer, downvoteCommentContainer;

        CommentsViewHolder(View view) {
            super(view);
            media = (ImageView) view.findViewById(R.id.media);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);
            upvoteCommentCountTextView = (TextView) view.findViewById(R.id.upvoteCommentTextView);
            downvoteCommentCountTextView = (TextView) view.findViewById(R.id.downvoteCommentTextView);
            upvoteCommentContainer = (LinearLayout) view.findViewById(R.id.upvoteCommentContainer);
            downvoteCommentContainer = (LinearLayout) view.findViewById(R.id.downvoteCommentContainer);
            replyCommentTextView.setVisibility(View.GONE);

            downvoteCommentContainer.setOnClickListener(this);
            upvoteCommentContainer.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onRecyclerItemClick(view, getAdapterPosition());
            return true;
        }
    }

    public class RepliesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView commentDateTextView;
        ImageView mediaview;
        TextView upvoteReplyCountTextView, downvoteReplyCountTextView;
        LinearLayout upvoteReplyContainer, downvoteReplyContainer;

        RepliesViewHolder(View view) {
            super(view);
            mediaview = (ImageView) view.findViewById(R.id.media);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            upvoteReplyCountTextView = (TextView) view.findViewById(R.id.upvoteReplyTextView);
            downvoteReplyCountTextView = (TextView) view.findViewById(R.id.downvoteReplyTextView);
            upvoteReplyContainer = (LinearLayout) view.findViewById(R.id.upvoteReplyContainer);
            downvoteReplyContainer = (LinearLayout) view.findViewById(R.id.downvoteReplyContainer);

            downvoteReplyContainer.setOnClickListener(this);
            upvoteReplyContainer.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onRecyclerItemClick(view, getAdapterPosition());
            return true;
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

    private void addLinkHandler(TextView textView) {
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) textView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();//should clear old spans
            for (URLSpan url : urls) {
                CustomerTextClick click = new CustomerTextClick(url.getURL());
                style.setSpan(click, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(style);
        }
    }

    private class CustomerTextClick extends ClickableSpan {

        private String mUrl;

        CustomerTextClick(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(mContext, NewsLetterWebviewActivity.class);
            intent.putExtra(Constants.URL, mUrl);
            mContext.startActivity(intent);
        }
    }

    private void startMediaPlayer(int position) {
        mediaPlayer = new MediaPlayer();
        Map<String, String> map = (Map<String, String>) repliesList.get(position).getMediaUrls();
        for (String entry : map.values()) {
            fetchAudioUrlFromFirebase(entry, playingHolder.audioSeekBar);
        }
    }

    private void releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder);
        }
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        playingPosition = -1;
    }

    private void updateNonPlayingView(AudioCommentViewHolder holder) {
        if (holder == playingHolder) {
            mHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
            mHandler.removeMessages(MSG_UPDATE_TIME);
        }
        holder.audioSeekBar.setEnabled(false);
        holder.audioSeekBar.setProgress(0);
        holder.playAudioImageView.setImageResource(R.drawable.play);
        holder.audioTimeElapsed.setVisibility(View.GONE);
    }

    private void updatePlayingView() {
        playingHolder.audioSeekBar.setMax(mediaPlayer.getDuration());
        playingHolder.audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
        playingHolder.audioSeekBar.setEnabled(true);
        if (mediaPlayer.isPlaying()) {
            playingHolder.audioTimeElapsed.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 1000);
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
            playingHolder.playAudioImageView.setImageResource(R.drawable.pause);
        } else {
            mHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
            mHandler.removeMessages(MSG_UPDATE_TIME);
            playingHolder.playAudioImageView.setImageResource(R.drawable.play);
            playingHolder.audioTimeElapsed.setVisibility(View.GONE);
        }
    }

    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchAudioUrlFromFirebase(String url, SeekBar audioSeekBar) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        StorageReference storageRef = storage.getReferenceFromUrl(url);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    // Download url of file
                    final String url = uri.toString();
                    mediaPlayer.setDataSource(url);
                    // wait for media player to get prepare
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            removeProgressDialog();
                            mediaPlayer.start();
                            updatePlayingView();
//                            updateProgressBar();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
                            mHandler.removeMessages(MSG_UPDATE_TIME);
                            releaseMediaPlayer();
                        }
                    });
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });

    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public void releasePlayer() {
        if (mediaPlayer != null) {
//            mediaPlayer.stop();
            updateNonPlayingView(playingHolder);
            mediaPlayer.release();
            mediaPlayer = null;
            playingPosition = -1;
        }
    }

}