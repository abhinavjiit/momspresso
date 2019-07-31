package com.mycity4kids.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.internal.LinkedTreeMap;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsActionVoteResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.shuhart.bubblepagerindicator.BubblePageIndicator;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/12/17.
 */

public class MyFeedPollGenericRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Handler.Callback {

    private static final int POST_TYPE_TEXT = 0;
    private static final int POST_TYPE_MEDIA = 1;
    private static final int POST_TYPE_TEXT_POLL = 2;
    private static final int POST_TYPE_IMAGE_POLL = 3;
    private static final int POST_TYPE_AUDIO = 4;

    private final Context mContext;
    private final LayoutInflater mInflator;
    private final GroupResult selectedGroup;
    private final String localizedNotHelpful, localizedHelpful, localizedComment;
    private ArrayList<GroupPostResult> postList;
    private HashMap<Integer, Integer> mViewPageStates = new HashMap<>();
    private RecyclerViewClickListener mListener;
    private int selectedPosition;
    private int pollPosition;
    private String memberType;
    private int playingPosition;
    private ProgressDialog mProgressDialog;
    private AudioCommentViewHolder playingHolder;
    private MediaPlayer mediaPlayer;
    private static final int MSG_UPDATE_SEEK_BAR = 1845;
    private static final int MSG_UPDATE_TIME = 1846;
    private Handler mHandler;
    private long totalDuration, currentDuration;
    private boolean isComment = false;

    public MyFeedPollGenericRecyclerAdapter(Context pContext, RecyclerViewClickListener listener, GroupResult selectedGroup, String memberType) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        this.selectedGroup = selectedGroup;
        localizedComment = mContext.getString(R.string.ad_comments_title);
        localizedHelpful = mContext.getString(R.string.groups_post_helpful);
        localizedNotHelpful = mContext.getString(R.string.groups_post_nothelpful);
        this.memberType = memberType;
        mHandler = new Handler(this);
        this.playingPosition = -1;
    }

    public void setData(ArrayList<GroupPostResult> postList) {
        this.postList = postList;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (postList.get(position).getType().equals("0")) {
            return POST_TYPE_TEXT;
        } else if (postList.get(position).getType().equals("1")) {
            return POST_TYPE_MEDIA;
        } else if (postList.get(position).getType().equals("2")) {
            if (postList.get(position).getPollType().equals("1")) {
                return POST_TYPE_IMAGE_POLL;
            } else {
                return POST_TYPE_TEXT_POLL;
            }
        } else if (postList.get(position).getType().equals("3")) {
            return POST_TYPE_AUDIO;
        }
        return POST_TYPE_TEXT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == POST_TYPE_TEXT) {
            View v0 = mInflator.inflate(R.layout.groups_text_post_item, parent, false);
            return new TextPostViewHolder(v0);
        } else if (viewType == POST_TYPE_MEDIA) {
            View v0 = mInflator.inflate(R.layout.groups_media_post_item, parent, false);
            return new MediaPostViewHolder(v0);
        } else if (viewType == POST_TYPE_AUDIO) {
            View v0 = mInflator.inflate(R.layout.groups_post_audio_item, parent, false);
            return new AudioCommentViewHolder(v0);
        } else if (viewType == POST_TYPE_TEXT_POLL) {
            View v0 = mInflator.inflate(R.layout.groups_text_poll_post_item, parent, false);
            return new TextPollPostViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.groups_image_poll_post_item, parent, false);
            return new ImagePollPostViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TextPostViewHolder) {
            TextPostViewHolder textPostViewHolder = (TextPostViewHolder) holder;
            if (postList.get(position).getIsAnnon() == 1) {
                textPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postList != null && postList.size() != 0) {
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() != 0) {
                            textPostViewHolder.userTag.setText(postList.get(position).getUserInfo().getUserTag().get(0));
                            textPostViewHolder.userTag.setVisibility(View.VISIBLE);
                        } else {
                            textPostViewHolder.userTag.setVisibility(View.GONE);

                        }
                    } else {
                        textPostViewHolder.userTag.setVisibility(View.GONE);
                    }

                } else {
                    textPostViewHolder.userTag.setVisibility(View.GONE);

                }
            }
            textPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
            Linkify.addLinks(textPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            textPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPostViewHolder.postDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(textPostViewHolder.postDataTextView);

            if (postList.get(position).getHelpfullCount() > 0)
                textPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            textPostViewHolder.downvoteCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                textPostViewHolder.commentLayout.setVisibility(View.GONE);
                textPostViewHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                textPostViewHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                textPostViewHolder.commentLayout.setVisibility(View.VISIBLE);
                textPostViewHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                textPostViewHolder.postCommentsTextView.setText(mContext.getResources().getString(R.string.group_add_comment_text));
            }
            textPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            if (postList.get(position).getIsAnnon() == 1) {
                textPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                textPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                textPostViewHolder.usernameTextView.setText(postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(textPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            textPostViewHolder.groupName.setText(postList.get(position).getGroupInfo().getName());
            if (postList.get(position).getGroupInfo().getColor() != null) {
                textPostViewHolder.groupName.setTextColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
                textPostViewHolder.groupNameView.setBackgroundColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
            }
            if (postList.get(position).getMarkedHelpful() == 1) {

                textPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                textPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }

        } else if (holder instanceof AudioCommentViewHolder) {
            AudioCommentViewHolder audioCommentViewHolder = (AudioCommentViewHolder) holder;

            if (postList.get(position).getIsAnnon() == 1) {
                audioCommentViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postList != null && postList.size() != 0) {
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() != 0 && postList.get(position).getUserInfo().getUserTag().get(0) != null) {
                            audioCommentViewHolder.userTag.setText(postList.get(position).getUserInfo().getUserTag().get(0));
                            audioCommentViewHolder.userTag.setVisibility(View.VISIBLE);

                        } else {
                            audioCommentViewHolder.userTag.setVisibility(View.GONE);

                        }
                    } else {
                        audioCommentViewHolder.userTag.setVisibility(View.GONE);
                    }

                } else {
                    audioCommentViewHolder.userTag.setVisibility(View.GONE);

                }
            }


            if (postList.get(position).getIsAnnon() == 1) {
                audioCommentViewHolder.commentorUsernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                audioCommentViewHolder.commentorImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) postList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                } else {
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                }
            } else {
                audioCommentViewHolder.commentorUsernameTextView.setText(postList.get(position).getUserInfo().getFirstName()
                        + " " + postList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(audioCommentViewHolder.commentorImageView);
                } catch (Exception e) {
                    audioCommentViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                try {
                    Picasso.with(mContext).load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(audioCommentViewHolder.profileImageView);
                } catch (Exception e) {
                    audioCommentViewHolder.profileImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }

                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) postList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                } else {
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                }
            }
            if (!StringUtils.isNullOrEmpty(postList.get(position).getContent())) {
                audioCommentViewHolder.commentDataTextView.setVisibility(View.VISIBLE);
                audioCommentViewHolder.commentDataTextView.setText(postList.get(position).getContent());
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

            audioCommentViewHolder.commentDateTextView.setText(DateTimeUtils.getFormattedDateGroups(postList.get(position).getCreatedAt()));
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                audioCommentViewHolder.commentLayout.setVisibility(View.GONE);
                audioCommentViewHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                audioCommentViewHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                audioCommentViewHolder.commentLayout.setVisibility(View.VISIBLE);
                audioCommentViewHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                audioCommentViewHolder.postCommentsTextView.setText(mContext.getResources().getString(R.string.group_add_comment_text));
            }
            if (postList.get(position).getHelpfullCount() > 0)
                audioCommentViewHolder.upvoteCommentCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            audioCommentViewHolder.downvoteCommentCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            audioCommentViewHolder.groupName.setText(postList.get(position).getGroupInfo().getName());
            if (postList.get(position).getGroupInfo().getColor() != null) {
                audioCommentViewHolder.groupName.setTextColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
                audioCommentViewHolder.groupNameView.setBackgroundColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
            }


            if (postList.get(position).getMarkedHelpful() == 1) {

                audioCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                audioCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }

        } else if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder mediaPostViewHolder = (MediaPostViewHolder) holder;

            if (postList.get(position).getIsAnnon() == 1) {
                mediaPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postList != null && postList.size() != 0) {
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() != 0 && postList.get(position).getUserInfo().getUserTag().get(0) != null) {
                            mediaPostViewHolder.userTag.setText(postList.get(position).getUserInfo().getUserTag().get(0));
                            mediaPostViewHolder.userTag.setVisibility(View.VISIBLE);

                        } else {
                            mediaPostViewHolder.userTag.setVisibility(View.GONE);

                        }
                    } else {
                        mediaPostViewHolder.userTag.setVisibility(View.GONE);
                    }

                } else {
                    mediaPostViewHolder.userTag.setVisibility(View.GONE);

                }
            }
            mediaPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
            Linkify.addLinks(mediaPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            mediaPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mediaPostViewHolder.postDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(mediaPostViewHolder.postDataTextView);
            if (postList.get(position).getHelpfullCount() > 0)
                mediaPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            mediaPostViewHolder.downvoteCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                mediaPostViewHolder.commentLayout.setVisibility(View.GONE);
                mediaPostViewHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                mediaPostViewHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                mediaPostViewHolder.commentLayout.setVisibility(View.VISIBLE);
                mediaPostViewHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                mediaPostViewHolder.postCommentsTextView.setText(mContext.getResources().getString(R.string.group_add_comment_text));

            }
            mediaPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            if (postList.get(position).getIsAnnon() == 1) {
                mediaPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                mediaPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                mediaPostViewHolder.usernameTextView.setText(postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(mediaPostViewHolder.userImageView);
                } catch (Exception e) {
                    mediaPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            initializeViews((MediaPostViewHolder) holder, position);
            mediaPostViewHolder.groupName.setText(postList.get(position).getGroupInfo().getName());
            if (postList.get(position).getGroupInfo().getColor() != null) {
                mediaPostViewHolder.groupName.setTextColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
                mediaPostViewHolder.groupNameView.setBackgroundColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
            }


            if (postList.get(position).getMarkedHelpful() == 1) {

                mediaPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                mediaPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }
        } else if (holder instanceof TextPollPostViewHolder) {
            TextPollPostViewHolder textPollPostViewHolder = (TextPollPostViewHolder) holder;

            if (postList.get(position).getIsAnnon() == 1) {
                textPollPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postList != null && postList.size() != 0) {
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() != 0 && postList.get(position).getUserInfo().getUserTag().get(0) != null) {
                            textPollPostViewHolder.userTag.setText(postList.get(position).getUserInfo().getUserTag().get(0));
                            textPollPostViewHolder.userTag.setVisibility(View.VISIBLE);

                        } else {
                            textPollPostViewHolder.userTag.setVisibility(View.GONE);

                        }
                    } else {
                        textPollPostViewHolder.userTag.setVisibility(View.GONE);
                    }

                } else {
                    textPollPostViewHolder.userTag.setVisibility(View.GONE);

                }
            }


            textPollPostViewHolder.pollQuestionTextView.setText(postList.get(position).getContent());
            Linkify.addLinks(textPollPostViewHolder.pollQuestionTextView, Linkify.WEB_URLS);
            textPollPostViewHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPollPostViewHolder.pollQuestionTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(textPollPostViewHolder.pollQuestionTextView);

            textPollPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            if (postList.get(position).getHelpfullCount() > 0)
                textPollPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            textPollPostViewHolder.downvoteCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                textPollPostViewHolder.commentLayout.setVisibility(View.GONE);
                textPollPostViewHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                textPollPostViewHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                textPollPostViewHolder.commentLayout.setVisibility(View.VISIBLE);
                textPollPostViewHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                textPollPostViewHolder.postCommentsTextView.setText(mContext.getResources().getString(R.string.group_add_comment_text));
            }
            if (postList.get(position).getIsAnnon() == 1) {
                textPollPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                textPollPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                textPollPostViewHolder.usernameTextView.setText(postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(textPollPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPollPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            textPollPostViewHolder.option3Container.setVisibility(View.GONE);
            textPollPostViewHolder.option4Container.setVisibility(View.GONE);
            Map<String, String> optionsMap = (Map<String, String>) postList.get(position).getPollOptions();
            for (Map.Entry<String, String> entry : optionsMap.entrySet()) {
                switch (entry.getKey()) {
                    case "option1":
                        textPollPostViewHolder.pollOption1TextView.setText(entry.getValue());
                        textPollPostViewHolder.pollResult1TextView.setText(entry.getValue());
                        break;
                    case "option2":
                        textPollPostViewHolder.pollOption2TextView.setText(entry.getValue());
                        textPollPostViewHolder.pollResult2TextView.setText(entry.getValue());
                        break;
                    case "option3":
                        textPollPostViewHolder.option3Container.setVisibility(View.VISIBLE);
                        textPollPostViewHolder.pollOption3TextView.setText(entry.getValue());
                        textPollPostViewHolder.pollResult3TextView.setText(entry.getValue());
                        break;
                    case "option4":
                        textPollPostViewHolder.option4Container.setVisibility(View.VISIBLE);
                        textPollPostViewHolder.pollOption4TextView.setText(entry.getValue());
                        textPollPostViewHolder.pollResult4TextView.setText(entry.getValue());
                        break;
                }
            }
            textPollPostViewHolder.pollOption1ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption2ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption3ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption4ProgressBar.setProgress(0f);
            textPollPostViewHolder.totalVoteCountTextView.setText(mContext.getString(R.string.groups_total_votes, postList.get(position).getTotalVotesCount()));
            if (postList.get(position).isVoted()) {
                showVotingData(textPollPostViewHolder, postList.get(position));
            } else {
                hideVotingData(textPollPostViewHolder);
            }
            textPollPostViewHolder.groupName.setText(postList.get(position).getGroupInfo().getName());
            if (postList.get(position).getGroupInfo().getColor() != null) {
                textPollPostViewHolder.groupName.setTextColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
                textPollPostViewHolder.groupNameView.setBackgroundColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
            }
            if (postList.get(position).getMarkedHelpful() == 1) {

                textPollPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                textPollPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }

        } else {
            ImagePollPostViewHolder imageHolder = (ImagePollPostViewHolder) holder;
            if (postList.get(position).getIsAnnon() == 1) {
                imageHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postList != null && postList.size() != 0) {
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() != 0 && postList.get(position).getUserInfo().getUserTag().get(0) != null) {
                            imageHolder.userTag.setText(postList.get(position).getUserInfo().getUserTag().get(0));
                            imageHolder.userTag.setVisibility(View.VISIBLE);

                        } else {
                            imageHolder.userTag.setVisibility(View.GONE);

                        }
                    } else {
                        imageHolder.userTag.setVisibility(View.GONE);
                    }

                } else {
                    imageHolder.userTag.setVisibility(View.GONE);

                }
            }
            imageHolder.pollQuestionTextView.setText(postList.get(position).getContent());
            Linkify.addLinks(imageHolder.pollQuestionTextView, Linkify.WEB_URLS);
            imageHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            imageHolder.pollQuestionTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(imageHolder.pollQuestionTextView);

            imageHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            if (postList.get(position).getIsAnnon() == 1) {
                imageHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                imageHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                imageHolder.usernameTextView.setText(postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.userImageView);
                } catch (Exception e) {
                    imageHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }

            if (postList.get(position).getHelpfullCount() > 0)
                imageHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            imageHolder.downvoteCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                imageHolder.commentLayout.setVisibility(View.GONE);
                imageHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                imageHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                imageHolder.commentLayout.setVisibility(View.VISIBLE);
                imageHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                imageHolder.postCommentsTextView.setText(mContext.getResources().getString(R.string.group_add_comment_text));
            }
            Map<String, String> imageMap = (Map<String, String>) postList.get(position).getPollOptions();
            imageHolder.lastOptionsContainer.setVisibility(View.GONE);
            imageHolder.option3Container.setVisibility(View.GONE);
            imageHolder.option4Container.setVisibility(View.GONE);
            for (Map.Entry<String, String> entry : imageMap.entrySet()) {
                switch (entry.getKey()) {
                    case "option1":
                        Picasso.with(mContext).load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option1ImageView);
                        break;
                    case "option2":
                        Picasso.with(mContext).load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option2ImageView);
                        break;
                    case "option3":
                        imageHolder.lastOptionsContainer.setVisibility(View.VISIBLE);
                        imageHolder.option3Container.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option3ImageView);
                        break;
                    case "option4":
                        imageHolder.lastOptionsContainer.setVisibility(View.VISIBLE);
                        imageHolder.option4Container.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option4ImageView);
                        break;
                }
            }
            imageHolder.pollOption1ProgressBar.setProgress(0f);
            imageHolder.pollOption2ProgressBar.setProgress(0f);
            imageHolder.pollOption3ProgressBar.setProgress(0f);
            imageHolder.pollOption4ProgressBar.setProgress(0f);
            imageHolder.totalVoteCountTextView.setText(mContext.getString(R.string.groups_total_votes, postList.get(position).getTotalVotesCount()));
            if (postList.get(position).isVoted()) {
                showImagePollVotingData(imageHolder, postList.get(position));
            } else {
                hideImagePollVotingData(imageHolder);
            }

            imageHolder.groupName.setText(postList.get(position).getGroupInfo().getName());
            if (postList.get(position).getGroupInfo().getColor() != null) {
                imageHolder.groupName.setTextColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
                imageHolder.groupNameView.setBackgroundColor(Color.parseColor(postList.get(position).getGroupInfo().getColor()));
            }
            if (postList.get(position).getMarkedHelpful() == 1) {
                imageHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                imageHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }

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

    private void hideVotingData(TextPollPostViewHolder textPollPostViewHolder) {
        textPollPostViewHolder.pollOption1ProgressTextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollOption2ProgressTextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollOption3ProgressTextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollOption4ProgressTextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollResult1TextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollResult2TextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollResult3TextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollResult4TextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollOption1TextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollOption2TextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollOption3TextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollOption4TextView.setVisibility(View.VISIBLE);
    }

    private void showVotingData(TextPollPostViewHolder textPollPostViewHolder, GroupPostResult postResult) {
        textPollPostViewHolder.pollOption1ProgressBar.setProgress((100f * postResult.getOption1VoteCount()) / postResult.getTotalVotesCount());
        textPollPostViewHolder.pollOption2ProgressBar.setProgress((100f * postResult.getOption2VoteCount()) / postResult.getTotalVotesCount());
        textPollPostViewHolder.pollOption3ProgressBar.setProgress((100f * postResult.getOption3VoteCount()) / postResult.getTotalVotesCount());
        textPollPostViewHolder.pollOption4ProgressBar.setProgress((100f * postResult.getOption4VoteCount()) / postResult.getTotalVotesCount());
        textPollPostViewHolder.pollOption1ProgressTextView.setText(AppUtils.round((100f * postResult.getOption1VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        textPollPostViewHolder.pollOption2ProgressTextView.setText(AppUtils.round((100f * postResult.getOption2VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        textPollPostViewHolder.pollOption3ProgressTextView.setText(AppUtils.round((100f * postResult.getOption3VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        textPollPostViewHolder.pollOption4ProgressTextView.setText(AppUtils.round((100f * postResult.getOption4VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        textPollPostViewHolder.pollOption1ProgressTextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollOption2ProgressTextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollOption3ProgressTextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollOption4ProgressTextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollResult1TextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollResult2TextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollResult3TextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollResult4TextView.setVisibility(View.VISIBLE);
        textPollPostViewHolder.pollOption1TextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollOption2TextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollOption3TextView.setVisibility(View.GONE);
        textPollPostViewHolder.pollOption4TextView.setVisibility(View.GONE);
    }

    private void showImagePollVotingData(ImagePollPostViewHolder imageHolder, GroupPostResult postResult) {
        imageHolder.pollOption1ProgressBar.setProgress((100f * postResult.getOption1VoteCount()) / postResult.getTotalVotesCount());
        imageHolder.pollOption2ProgressBar.setProgress((100f * postResult.getOption2VoteCount()) / postResult.getTotalVotesCount());
        imageHolder.pollOption3ProgressBar.setProgress((100f * postResult.getOption3VoteCount()) / postResult.getTotalVotesCount());
        imageHolder.pollOption4ProgressBar.setProgress((100f * postResult.getOption4VoteCount()) / postResult.getTotalVotesCount());
        imageHolder.pollOption1TextView.setText(AppUtils.round((100f * postResult.getOption1VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption2TextView.setText(AppUtils.round((100f * postResult.getOption2VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption3TextView.setText(AppUtils.round((100f * postResult.getOption3VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption4TextView.setText(AppUtils.round((100f * postResult.getOption4VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption1ProgressBar.setVisibility(View.VISIBLE);
        imageHolder.pollOption2ProgressBar.setVisibility(View.VISIBLE);
        imageHolder.pollOption3ProgressBar.setVisibility(View.VISIBLE);
        imageHolder.pollOption4ProgressBar.setVisibility(View.VISIBLE);
        imageHolder.pollOption1TextView.setVisibility(View.VISIBLE);
        imageHolder.pollOption2TextView.setVisibility(View.VISIBLE);
        imageHolder.pollOption3TextView.setVisibility(View.VISIBLE);
        imageHolder.pollOption4TextView.setVisibility(View.VISIBLE);
    }

    private void hideImagePollVotingData(ImagePollPostViewHolder imageHolder) {
        imageHolder.pollOption1ProgressBar.setVisibility(View.GONE);
        imageHolder.pollOption2ProgressBar.setVisibility(View.GONE);
        imageHolder.pollOption3ProgressBar.setVisibility(View.GONE);
        imageHolder.pollOption4ProgressBar.setVisibility(View.GONE);
        imageHolder.pollOption1TextView.setVisibility(View.GONE);
        imageHolder.pollOption2TextView.setVisibility(View.GONE);
        imageHolder.pollOption3TextView.setVisibility(View.GONE);
        imageHolder.pollOption4TextView.setVisibility(View.GONE);
    }

    private void initializeViews(MediaPostViewHolder holder, int position) {
        ArrayList<String> mediaList = new ArrayList<>();
        LinkedTreeMap<String, String> map = (LinkedTreeMap<String, String>) postList.get(position).getMediaUrls();
        for (String entry : map.values()) {
            mediaList.add(entry);
        }
        holder.mViewPagerAdapter.setDataList(mediaList);
        holder.postDataViewPager.setAdapter(holder.mViewPagerAdapter);
        holder.dotIndicatorView.setViewPager(holder.postDataViewPager);
        if (mediaList.size() == 1) {
            holder.indexTextView.setVisibility(View.GONE);
            holder.dotIndicatorView.setVisibility(View.GONE);
        } else {
            holder.indexTextView.setVisibility(View.VISIBLE);
            holder.dotIndicatorView.setVisibility(View.VISIBLE);
        }
        if (mViewPageStates.containsKey(position)) {
            holder.postDataViewPager.setCurrentItem(mViewPageStates.get(position));
            holder.indexTextView.setText((mViewPageStates.get(position) + 1) + "/" + mediaList.size());
        } else {
            holder.indexTextView.setText((holder.postDataViewPager.getCurrentItem() + 1) + "/" + mediaList.size());
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder viewHolder = (MediaPostViewHolder) holder;
            mViewPageStates.put(holder.getAdapterPosition(), viewHolder.postDataViewPager.getCurrentItem());
            super.onViewRecycled(holder);
        }
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView, upvoteImageVIew, whatsappShare;
        TextView usernameTextView, groupName;
        TextView postDateTextView;
        TextView postDataTextView, userTag;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView, typeHere, beTheFirstOne;
        ImageView postSettingImageView;
        ImageView shareTextView;
        RelativeLayout commentLayout, groupNameLayout;
        View groupNameView;

        TextPostViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            typeHere = (TextView) view.findViewById(R.id.typeHere);
            beTheFirstOne = (TextView) view.findViewById(R.id.beTheFirstOne);
            userTag = (TextView) view.findViewById(R.id.userTag);
            groupNameLayout = (RelativeLayout) view.findViewById(R.id.group_name_layout);
            groupNameView = (View) view.findViewById(R.id.groupname_view);
            groupNameLayout.setVisibility(View.VISIBLE);
            groupName = (TextView) view.findViewById(R.id.group_name);
            groupName.setOnClickListener(this);

            groupNameLayout.setOnClickListener(this);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);
            commentLayout.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
/*
            commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("groupItem", selectedGroup);
                    intent.putExtra("postType", AppConstants.POST_TYPE_TEXT);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                }
            });


            postCommentsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postCommentsTextView.getText().toString().equals(mContext.getResources().getString(R.string.group_add_comment_text))) {

                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("groupItem", selectedGroup);
                        intent.putExtra("postType", AppConstants.POST_TYPE_TEXT);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);

                    } else {
                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("groupItem", selectedGroup);
                        intent.putExtra("postType", AppConstants.POST_TYPE_TEXT);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                    }
                }
            });*/
        }

        @Override
        public void onClick(View v) {
            mListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class MediaPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView, whatsappShare;
        TextView usernameTextView, groupName;
        TextView postDateTextView;
        TextView postDataTextView;
        ImageView shareTextView;
        TextView upvoteCountTextView, downvoteCountTextView, userTag;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView, beTheFirstOne;
        ImageView postSettingImageView, upvoteImageVIew;
        private BubblePageIndicator dotIndicatorView;
        private GroupPostMediaViewPager postDataViewPager;
        private TextView indexTextView;
        private GroupMediaPostViewPagerAdapter mViewPagerAdapter;
        RelativeLayout commentLayout, groupNameLayout;
        View groupNameView;

        MediaPostViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            dotIndicatorView = (BubblePageIndicator) view.findViewById(R.id.dotIndicatorView);
            postDataViewPager = (GroupPostMediaViewPager) view.findViewById(R.id.postDataViewPager);
            indexTextView = (TextView) view.findViewById(R.id.indexTextView);
            beTheFirstOne = (TextView) view.findViewById(R.id.beTheFirstOne);
            userTag = (TextView) view.findViewById(R.id.userTag);
            groupNameLayout = (RelativeLayout) view.findViewById(R.id.group_name_layout);
            groupNameView = (View) view.findViewById(R.id.groupname_view);
            groupNameLayout.setVisibility(View.VISIBLE);
            groupName = (TextView) view.findViewById(R.id.group_name);
            groupName.setOnClickListener(this);


            mViewPagerAdapter = new GroupMediaPostViewPagerAdapter(mContext);
            postDataViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    postDataViewPager.setTag(position);
                    indexTextView.setText((position + 1) + "/" + postDataViewPager.getAdapter().getCount());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            whatsappShare.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
            groupNameLayout.setOnClickListener(this);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            commentLayout.setOnClickListener(this);
           /* commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_MEDIA);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getMediaUrls();
                    intent.putExtra("mediaUrls", linkedTreeMap);
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                }
            });

            postCommentsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (postCommentsTextView.getText().toString().equals(mContext.getResources().getString(R.string.group_add_comment_text))) {

                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("postType", AppConstants.POST_TYPE_MEDIA);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getMediaUrls();
                        intent.putExtra("mediaUrls", linkedTreeMap);
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                    } else {
                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("postType", AppConstants.POST_TYPE_MEDIA);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getMediaUrls();
                        intent.putExtra("mediaUrls", linkedTreeMap);
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                    }
                }
            });*/
        }

        @Override
        public void onClick(View v) {
            mListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }


    public class AudioCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, SeekBar.OnSeekBarChangeListener {

        ImageView commentorImageView, playAudioImageView, pauseAudioImageView, whatsappShare, profileImageView;
        ImageView media, upvoteImageVIew;
        TextView commentorUsernameTextView, audioTimeElapsed;
        TextView commentDataTextView;
        TextView commentDateTextView;
        View underlineView;
        SeekBar audioSeekBar;
        TextView upvoteCommentCountTextView, downvoteCommentCountTextView, userTag;
        LinearLayout upvoteCommentContainer, downvoteCommentContainer;
        RelativeLayout audiotRootView;
        TextView postCommentsTextView, beTheFirstOne, groupName;
        ImageView postSettingImageView;
        ImageView shareTextView;
        RelativeLayout commentLayout, groupNameLayout;
        View groupNameView;


        public AudioCommentViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            profileImageView = (ImageView) view.findViewById(R.id.profileImageView);
            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            media = (ImageView) view.findViewById(R.id.media);
            audiotRootView = view.findViewById(R.id.commentRootView);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            upvoteCommentCountTextView = (TextView) view.findViewById(R.id.upvoteCommentTextView);
            downvoteCommentCountTextView = (TextView) view.findViewById(R.id.downvoteCommentTextView);
            upvoteCommentContainer = (LinearLayout) view.findViewById(R.id.upvoteCommentContainer);
            downvoteCommentContainer = (LinearLayout) view.findViewById(R.id.downvoteCommentContainer);
            playAudioImageView = (ImageView) view.findViewById(R.id.playAudioImageView);
            pauseAudioImageView = (ImageView) view.findViewById(R.id.pauseAudioImageView);
            audioSeekBar = (SeekBar) view.findViewById(R.id.audioSeekBar);
            audioTimeElapsed = (TextView) view.findViewById(R.id.audioTimeElapsed);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            beTheFirstOne = (TextView) view.findViewById(R.id.beTheFirstOne);
            userTag = (TextView) view.findViewById(R.id.userTag);
            groupNameLayout = (RelativeLayout) view.findViewById(R.id.group_name_layout);
            groupNameView = (View) view.findViewById(R.id.groupname_view);
            groupNameLayout.setVisibility(View.VISIBLE);
            groupName = (TextView) view.findViewById(R.id.group_name);
            groupName.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
            commentLayout.setOnClickListener(this);

        /*    postCommentsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postCommentsTextView.getText().toString().equals(mContext.getResources().getString(R.string.group_add_comment_text))) {

                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                        intent.putExtra("pollOptions", linkedTreeMap);
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                    } else {

                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                        intent.putExtra("pollOptions", linkedTreeMap);
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                    }
                }
            });
            commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                    intent.putExtra("pollOptions", linkedTreeMap);
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                }
            });*/

            groupNameLayout.setOnClickListener(this);
            commentDataTextView.setOnLongClickListener(this);
            view.setOnLongClickListener(this);
            downvoteCommentContainer.setOnClickListener(this);
            upvoteCommentContainer.setOnClickListener(this);
            playAudioImageView.setOnClickListener(this);
            audioSeekBar.setOnSeekBarChangeListener(this);
            postSettingImageView.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);

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
                mListener.onGroupPostRecyclerItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onGroupPostRecyclerItemClick(view, getAdapterPosition());
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

    public class TextPollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView, whatsappShare;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView, upvoteImageVIew;
        TextView pollQuestionTextView, beTheFirstOne, userTag;
        ImageView shareTextView;
        RoundCornerProgressBar pollOption1ProgressBar, pollOption2ProgressBar, pollOption3ProgressBar, pollOption4ProgressBar;
        TextView pollOption1TextView, pollOption2TextView, pollOption3TextView, pollOption4TextView;
        TextView pollResult1TextView, pollResult2TextView, pollResult3TextView, pollResult4TextView;
        TextView pollOption1ProgressTextView, pollOption2ProgressTextView, pollOption3ProgressTextView, pollOption4ProgressTextView;
        TextView totalVoteCountTextView, groupName;
        RelativeLayout option3Container, option4Container;
        RelativeLayout commentLayout, groupNameLayout;
        View groupNameView;

        TextPollPostViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);

            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            pollQuestionTextView = (TextView) view.findViewById(R.id.pollQuestionTextView);
            pollOption1ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption1ProgressBar);
            pollOption2ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption2ProgressBar);
            pollOption3ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption3ProgressBar);
            pollOption4ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption4ProgressBar);
            pollOption1TextView = (TextView) view.findViewById(R.id.pollOption1TextView);
            pollOption2TextView = (TextView) view.findViewById(R.id.pollOption2TextView);
            pollOption3TextView = (TextView) view.findViewById(R.id.pollOption3TextView);
            pollOption4TextView = (TextView) view.findViewById(R.id.pollOption4TextView);
            pollResult1TextView = (TextView) view.findViewById(R.id.pollResult1TextView);
            pollResult2TextView = (TextView) view.findViewById(R.id.pollResult2TextView);
            pollResult3TextView = (TextView) view.findViewById(R.id.pollResult3TextView);
            pollResult4TextView = (TextView) view.findViewById(R.id.pollResult4TextView);
            pollOption1ProgressTextView = (TextView) view.findViewById(R.id.pollOption1ProgressTextView);
            pollOption2ProgressTextView = (TextView) view.findViewById(R.id.pollOption2ProgressTextView);
            pollOption3ProgressTextView = (TextView) view.findViewById(R.id.pollOption3ProgressTextView);
            pollOption4ProgressTextView = (TextView) view.findViewById(R.id.pollOption4ProgressTextView);
            totalVoteCountTextView = (TextView) view.findViewById(R.id.totalVoteCountTextView);
            option3Container = (RelativeLayout) view.findViewById(R.id.option3Container);
            option4Container = (RelativeLayout) view.findViewById(R.id.option4Container);
            beTheFirstOne = (TextView) view.findViewById(R.id.beTheFirstOne);
            userTag = (TextView) view.findViewById(R.id.userTag);
            groupNameLayout = (RelativeLayout) view.findViewById(R.id.group_name_layout);
            groupNameView = (View) view.findViewById(R.id.groupname_view);
            groupNameLayout.setVisibility(View.VISIBLE);
            groupName = (TextView) view.findViewById(R.id.group_name);
            groupName.setOnClickListener(this);

            groupNameLayout.setOnClickListener(this);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
            commentLayout.setOnClickListener(this);
/*
            postCommentsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (postCommentsTextView.getText().toString().equals(mContext.getResources().getString(R.string.group_add_comment_text))) {

                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                        intent.putExtra("pollOptions", linkedTreeMap);
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                    } else {


                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                        intent.putExtra("pollOptions", linkedTreeMap);
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                    }
                }
            });
            commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                    intent.putExtra("pollOptions", linkedTreeMap);
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                }
            });*/
            pollOption1ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option1");
                }
            });

            pollOption2ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option2");
                }
            });

            pollOption3ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option3");
                }
            });

            pollOption4ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option4");
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class ImagePollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView, whatsappShare;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        TextView pollQuestionTextView, userTag;
        ImageView shareTextView, upvoteImageVIew;
        ImageView option1ImageView, option2ImageView, option3ImageView, option4ImageView;
        RoundCornerProgressBar pollOption1ProgressBar, pollOption2ProgressBar, pollOption3ProgressBar, pollOption4ProgressBar;
        TextView pollOption1TextView, pollOption2TextView, pollOption3TextView, pollOption4TextView;
        TextView totalVoteCountTextView, beTheFirstOne, groupName;
        LinearLayout lastOptionsContainer;
        RelativeLayout commentLayout, groupNameLayout;
        RelativeLayout option1Container, option2Container, option3Container, option4Container;
        View groupNameView;

        ImagePollPostViewHolder(View view) {
            super(view);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);

            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            option1ImageView = (ImageView) view.findViewById(R.id.option1ImageView);
            option2ImageView = (ImageView) view.findViewById(R.id.option2ImageView);
            option3ImageView = (ImageView) view.findViewById(R.id.option3ImageView);
            option4ImageView = (ImageView) view.findViewById(R.id.option4ImageView);
            pollOption1ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption1ProgressBar);
            pollOption2ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption2ProgressBar);
            pollOption3ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption3ProgressBar);
            pollOption4ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption4ProgressBar);
            pollOption1TextView = (TextView) view.findViewById(R.id.pollOption1TextView);
            pollOption2TextView = (TextView) view.findViewById(R.id.pollOption2TextView);
            pollOption3TextView = (TextView) view.findViewById(R.id.pollOption3TextView);
            pollOption4TextView = (TextView) view.findViewById(R.id.pollOption4TextView);
            totalVoteCountTextView = (TextView) view.findViewById(R.id.totalVoteCountTextView);
            pollQuestionTextView = (TextView) view.findViewById(R.id.pollQuestionTextView);
            lastOptionsContainer = (LinearLayout) view.findViewById(R.id.lastOptionsContainer);
            option1Container = (RelativeLayout) view.findViewById(R.id.option1Container);
            option2Container = (RelativeLayout) view.findViewById(R.id.option2Container);
            option3Container = (RelativeLayout) view.findViewById(R.id.option3Container);
            option4Container = (RelativeLayout) view.findViewById(R.id.option4Container);
            beTheFirstOne = (TextView) view.findViewById(R.id.beTheFirstOne);
            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            groupNameLayout = (RelativeLayout) view.findViewById(R.id.group_name_layout);
            groupNameView = (View) view.findViewById(R.id.groupname_view);
            groupNameLayout.setVisibility(View.VISIBLE);
            groupName = (TextView) view.findViewById(R.id.group_name);
            groupName.setOnClickListener(this);

            userTag = (TextView) view.findViewById(R.id.userTag);
            groupNameLayout.setOnClickListener(this);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);
            commentLayout.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
            /*commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_IMAGE_POLL);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                    intent.putExtra("pollOptions", linkedTreeMap);
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                }
            });

            postCommentsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postCommentsTextView.getText().toString().equals(mContext.getResources().getString(R.string.group_add_comment_text))) {


                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("postType", AppConstants.POST_TYPE_IMAGE_POLL);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                        intent.putExtra("pollOptions", linkedTreeMap);
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);

                    } else {
                        Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                        intent.putExtra("postType", AppConstants.POST_TYPE_IMAGE_POLL);
                        intent.putExtra("postData", postList.get(getAdapterPosition()));
                        LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getPollOptions();
                        intent.putExtra("pollOptions", linkedTreeMap);
                        intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                        intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                        intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                        ((FragmentActivity) mContext).startActivityForResult(intent, 2222);
                    }
                }
            });
*/
            option1Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option1");
                }
            });

            option2Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option2");
                }
            });

            option3Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option3");
                }
            });

            option4Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option4");
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }

    private void addVote(int position, String option) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(postList.get(position).getGroupId());
        groupActionsRequest.setPostId(postList.get(position).getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(mContext).getDynamoId());
        groupActionsRequest.setVoteOption(option);
        pollPosition = position;
        Call<GroupsActionVoteResponse> call = groupsAPI.addActionVote(groupActionsRequest);
        call.enqueue(groupActionResponseCallback);
    }

    private Callback<GroupsActionVoteResponse> groupActionResponseCallback = new Callback<GroupsActionVoteResponse>() {
        @Override
        public void onResponse(Call<GroupsActionVoteResponse> call, Response<GroupsActionVoteResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    if (response.code() == 400) {
                        try {
                            String errorBody = new String(response.errorBody().bytes());
                            JSONObject jObject = new JSONObject(errorBody);
                            String reason = jObject.getString("reason");
                            if (!StringUtils.isNullOrEmpty(reason) && "already voted".equals(reason)) {

                                postList.get(pollPosition).setVoted(true);
                                postList.get(pollPosition).setTotalVotesCount(0);
                                for (int i = 0; i < jObject.getJSONArray("data").length(); i++) {
                                    postList.get(pollPosition).setTotalVotesCount(postList.get(pollPosition).getTotalVotesCount()
                                            + Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                    switch (jObject.getJSONArray("data").getJSONObject(i).getString("name")) {
                                        case "option1":
                                            postList.get(pollPosition).setOption1VoteCount(Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                            break;
                                        case "option2":
                                            postList.get(pollPosition).setOption2VoteCount(Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                            break;
                                        case "option3":
                                            postList.get(pollPosition).setOption3VoteCount(Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                            break;
                                        case "option4":
                                            postList.get(pollPosition).setOption4VoteCount(Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                            break;
                                    }
                                }
                                notifyDataSetChanged();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionVoteResponse groupsActionResponse = response.body();
                    for (int i = 0; i < postList.size(); i++) {
                        if (postList.get(i).getId() == groupsActionResponse.getData().getResult().get(0).getPostId()) {
                            postList.get(i).setVoted(true);
                            switch (groupsActionResponse.getData().getResult().get(0).getVoteOption()) {
                                case "option1":
                                    postList.get(i).setOption1VoteCount(postList.get(i).getOption1VoteCount() + 1);
                                    postList.get(i).setTotalVotesCount(postList.get(i).getTotalVotesCount() + 1);
                                    break;
                                case "option2":
                                    postList.get(i).setOption2VoteCount(postList.get(i).getOption2VoteCount() + 1);
                                    postList.get(i).setTotalVotesCount(postList.get(i).getTotalVotesCount() + 1);
                                    break;
                                case "option3":
                                    postList.get(i).setOption3VoteCount(postList.get(i).getOption3VoteCount() + 1);
                                    postList.get(i).setTotalVotesCount(postList.get(i).getTotalVotesCount() + 1);
                                    break;
                                case "option4":
                                    postList.get(i).setOption4VoteCount(postList.get(i).getOption4VoteCount() + 1);
                                    postList.get(i).setTotalVotesCount(postList.get(i).getTotalVotesCount() + 1);
                                    break;
                            }
                            notifyDataSetChanged();
                            break;
                        }
                    }

                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsActionVoteResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
        public void onClick(@NotNull View widget) {
            Intent intent = new Intent(mContext, NewsLetterWebviewActivity.class);
            intent.putExtra(Constants.URL, mUrl);
            mContext.startActivity(intent);
        }
    }

    private void startMediaPlayer(int position) {
        mediaPlayer = new MediaPlayer();
        Map<String, String> map = (Map<String, String>) postList.get(position).getMediaUrls();
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

    public interface RecyclerViewClickListener {
        void onGroupPostRecyclerItemClick(View view, int position);

    }

}
