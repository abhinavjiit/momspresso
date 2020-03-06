package com.mycity4kids.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupsActionVoteResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.mycity4kids.widget.IndefinitePagerIndicator;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupPostDetailsAndCommentsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        MediaPlayer.OnCompletionListener, Handler.Callback {

    private static final int HEADER = -1;
    private static final int COMMENT_LEVEL_ROOT = 0;
    private static final int COMMENT_AUDIO = 1;
    private final String localizedComment;

    private final Context context;
    private final LayoutInflater layoutInflater;
    private ArrayList<GroupPostCommentResult> postCommentsList;
    private RecyclerViewClickListener recyclerViewClickListener;
    private String postType;
    private GroupPostResult groupPostResult;
    private Handler handler;
    private ProgressDialog progressDialog;
    private static final int MSG_UPDATE_SEEK_BAR = 1845;
    private static final int MSG_UPDATE_TIME = 1846;
    private static final int MSG_UPDATE_SEEK_BAR_HEADER = 1847;
    private static final int MSG_UPDATE_TIME_HEADER = 1848;
    private int playingPosition;
    private AudioCommentViewHolder playingHolder;
    private AudioCommentViewHeaderHolder playingHeaderHolder;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerHeader;
    private boolean isPostPlaying = false;
    private boolean isCommentPlaying = false;

    public GroupPostDetailsAndCommentsRecyclerAdapter(Context context, RecyclerViewClickListener listener,
            String postType) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recyclerViewClickListener = listener;
        this.postType = postType;
        handler = new Handler(this);
        localizedComment = this.context.getString(R.string.ad_comments_title);
        this.playingPosition = -1;
    }

    public void setData(GroupPostResult groupPostResult, ArrayList<GroupPostCommentResult> postCommentsList) {
        this.groupPostResult = groupPostResult;
        this.postCommentsList = postCommentsList;
    }

    @Override
    public int getItemCount() {
        return postCommentsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else if (postCommentsList.get(position).getCommentType() == AppConstants.COMMENT_TYPE_AUDIO) {
            return COMMENT_AUDIO;
        } else {
            return COMMENT_LEVEL_ROOT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            if (AppConstants.POST_TYPE_TEXT.equals(postType)) {
                View v0 = layoutInflater.inflate(R.layout.groups_text_post_item, parent, false);
                return new TextPostViewHolder(v0);
            } else if (AppConstants.POST_TYPE_MEDIA.equals(postType)) {
                View v0 = layoutInflater.inflate(R.layout.groups_media_post_item, parent, false);
                return new MediaPostViewHolder(v0);
            } else if (AppConstants.POST_TYPE_AUDIO.equals(postType)) {
                View v0 = layoutInflater.inflate(R.layout.groups_post_audio_item, parent, false);
                return new AudioCommentViewHeaderHolder(v0);
            } else if (AppConstants.POST_TYPE_TEXT_POLL.equals(postType)) {
                View v0 = layoutInflater.inflate(R.layout.groups_text_poll_post_item, parent, false);
                return new TextPollPostViewHolder(v0);
            } else if (AppConstants.POST_TYPE_ASK_AN_EXPERT.equals(postType)) {
                View v0 = layoutInflater.inflate(R.layout.groups_media_post_item, parent, false);
                return new MediaPostViewHolder(v0);
            } else {
                View v0 = layoutInflater.inflate(R.layout.groups_image_poll_post_item, parent, false);
                return new ImagePollPostViewHolder(v0);
            }
        } else {
            if (COMMENT_AUDIO == viewType) {
                View v0 = layoutInflater.inflate(R.layout.group_post_audio_comment_cell, parent, false);
                return new AudioCommentViewHolder(v0);
            } else {
                View v0 = layoutInflater.inflate(R.layout.group_post_comment_cell_test, parent, false);
                return new RootCommentViewHolder(v0);
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TextPostViewHolder) {
            TextPostViewHolder textPostViewHolder = (TextPostViewHolder) holder;
            textPostViewHolder.postDataTextView.setText(groupPostResult.getContent());
            Linkify.addLinks(textPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            textPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPostViewHolder.postDataTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(textPostViewHolder.postDataTextView);

            textPostViewHolder.postDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            if (groupPostResult.getHelpfullCount() < 1) {
                textPostViewHolder.upvoteCountTextView.setText("");
            } else {
                textPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + "");
            }
            textPostViewHolder.postCommentsTextView
                    .setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                textPostViewHolder.usernameTextView.setText(context.getString(R.string.groups_anonymous));
                textPostViewHolder.userImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
            } else {
                textPostViewHolder.usernameTextView.setText(
                        groupPostResult.getUserInfo().getFirstName() + " " + groupPostResult.getUserInfo()
                                .getLastName());
                try {
                    Picasso.get().load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(textPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            textPostViewHolder.userImageView.setOnClickListener(view -> {
                if (groupPostResult.getIsAnnon() == 0) {
                    launchProfile(groupPostResult.getUserId());
                }
            });
            if (groupPostResult.getIsAnnon() == 1) {
                textPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (groupPostResult != null && groupPostResult.getUserInfo() != null
                        && groupPostResult.getUserInfo().getUserTag() != null) {
                    if (groupPostResult.getUserInfo().getUserTag().size() != 0) {
                        textPostViewHolder.userTag.setText(groupPostResult.getUserInfo().getUserTag().get(0));
                    } else {
                        textPostViewHolder.userTag.setVisibility(View.GONE);
                    }
                }
            }
            textPostViewHolder.usernameTextView.setOnClickListener(view -> {
                if (groupPostResult.getIsAnnon() == 0) {
                    launchProfile(groupPostResult.getUserId());
                }
            });

            if (groupPostResult.getMarkedHelpful() == 1) {
                textPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                textPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }
        } else if (holder instanceof AudioCommentViewHeaderHolder) {
            AudioCommentViewHeaderHolder audioCommentViewHolder = (AudioCommentViewHeaderHolder) holder;

            if (groupPostResult.getIsAnnon() == 1) {
                audioCommentViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (groupPostResult != null && groupPostResult.getUserInfo() != null
                        && groupPostResult.getUserInfo().getUserTag() != null) {
                    if (groupPostResult.getUserInfo().getUserTag().size() != 0) {
                        audioCommentViewHolder.userTag.setText(groupPostResult.getUserInfo().getUserTag().get(0));
                    } else {
                        audioCommentViewHolder.userTag.setVisibility(View.GONE);
                    }
                }
            }

            if (groupPostResult.getIsAnnon() == 1) {
                audioCommentViewHolder.commentorUsernameTextView.setText(context.getString(R.string.groups_anonymous));
                audioCommentViewHolder.commentorImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) postCommentsList.get(position).getMediaUrls();
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
                audioCommentViewHolder.commentorImageView.setOnClickListener(view -> {
                    if (groupPostResult.getIsAnnon() == 0) {
                        launchProfile(groupPostResult.getUserId());
                    }
                });
                try {
                    Picasso.get().load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                            .into(audioCommentViewHolder.profileImageView);
                } catch (Exception e) {
                    audioCommentViewHolder.profileImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                audioCommentViewHolder.commentorUsernameTextView.setOnClickListener(view -> {

                    if (groupPostResult.getIsAnnon() == 0) {

                        launchProfile(groupPostResult.getUserId());
                    }
                });
                audioCommentViewHolder.commentorUsernameTextView.setText(groupPostResult.getUserInfo().getFirstName()
                        + " " + groupPostResult.getUserInfo().getLastName());
                try {
                    Picasso.get().load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                            .into(audioCommentViewHolder.commentorImageView);
                } catch (Exception e) {
                    audioCommentViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) groupPostResult.getMediaUrls();
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
            if (!StringUtils.isNullOrEmpty(groupPostResult.getContent())) {
                audioCommentViewHolder.commentDataTextView.setVisibility(View.VISIBLE);
                audioCommentViewHolder.commentDataTextView.setText(groupPostResult.getContent());
                Linkify.addLinks(audioCommentViewHolder.commentDataTextView, Linkify.WEB_URLS);
                audioCommentViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
                audioCommentViewHolder.commentDataTextView
                        .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
                addLinkHandler(audioCommentViewHolder.commentDataTextView);
            } else {
                audioCommentViewHolder.commentDataTextView.setVisibility(View.GONE);
            }

            if (position == playingPosition) {
                playingHeaderHolder = audioCommentViewHolder;
                updatePlayingHeaderView();
            } else {
                updateNonPlayingHeaderView(audioCommentViewHolder);
            }

            audioCommentViewHolder.commentDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            audioCommentViewHolder.postCommentsTextView
                    .setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getHelpfullCount() < 1) {
                audioCommentViewHolder.upvoteCommentCountTextView.setText("");
            } else {
                audioCommentViewHolder.upvoteCommentCountTextView.setText(groupPostResult.getHelpfullCount() + "");
            }
            if (groupPostResult.getMarkedHelpful() == 1) {
                audioCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                audioCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);
            }

        } else if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder mediaPostViewHolder = (MediaPostViewHolder) holder;
            if (groupPostResult.getIsAnnon() == 1) {
                mediaPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (groupPostResult != null && groupPostResult.getUserInfo() != null
                        && groupPostResult.getUserInfo().getUserTag() != null) {
                    if (groupPostResult.getUserInfo().getUserTag().size() != 0) {
                        mediaPostViewHolder.userTag.setText(groupPostResult.getUserInfo().getUserTag().get(0));
                    } else {
                        mediaPostViewHolder.userTag.setVisibility(View.GONE);
                    }
                }
            }

            mediaPostViewHolder.postDataTextView.setText(groupPostResult.getContent());
            Linkify.addLinks(mediaPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            mediaPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mediaPostViewHolder.postDataTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(mediaPostViewHolder.postDataTextView);

            mediaPostViewHolder.postDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            if (groupPostResult.getHelpfullCount() < 1) {
                mediaPostViewHolder.upvoteCountTextView.setText("");
            } else {
                mediaPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + "");
            }
            mediaPostViewHolder.postCommentsTextView
                    .setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                mediaPostViewHolder.usernameTextView.setText(context.getString(R.string.groups_anonymous));
                mediaPostViewHolder.userImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
            } else {
                mediaPostViewHolder.usernameTextView.setText(
                        groupPostResult.getUserInfo().getFirstName() + " " + groupPostResult.getUserInfo()
                                .getLastName());
                try {
                    Picasso.get().load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(mediaPostViewHolder.userImageView);
                } catch (Exception e) {
                    mediaPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }

            mediaPostViewHolder.userImageView.setOnClickListener(view -> {
                if (groupPostResult.getIsAnnon() == 0) {
                    launchProfile(groupPostResult.getUserId());
                }
            });

            mediaPostViewHolder.usernameTextView.setOnClickListener(view -> {
                if (groupPostResult.getIsAnnon() == 0) {
                    launchProfile(groupPostResult.getUserId());
                }
            });
            initializeViews((MediaPostViewHolder) holder, position);

            if (groupPostResult.getMarkedHelpful() == 1) {
                mediaPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                mediaPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);
            }
        } else if (holder instanceof TextPollPostViewHolder) {
            TextPollPostViewHolder textPollPostViewHolder = (TextPollPostViewHolder) holder;

            if (groupPostResult.getIsAnnon() == 1) {
                textPollPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (groupPostResult != null && groupPostResult.getUserInfo() != null
                        && groupPostResult.getUserInfo().getUserTag() != null) {
                    if (groupPostResult.getUserInfo().getUserTag().size() != 0) {
                        textPollPostViewHolder.userTag.setText(groupPostResult.getUserInfo().getUserTag().get(0));
                    } else {
                        textPollPostViewHolder.userTag.setVisibility(View.GONE);
                    }
                }
            }

            textPollPostViewHolder.pollQuestionTextView.setText(groupPostResult.getContent());
            Linkify.addLinks(textPollPostViewHolder.pollQuestionTextView, Linkify.WEB_URLS);
            textPollPostViewHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPollPostViewHolder.pollQuestionTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(textPollPostViewHolder.pollQuestionTextView);

            textPollPostViewHolder.postDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            if (groupPostResult.getHelpfullCount() < 1) {
                textPollPostViewHolder.upvoteCountTextView.setText("");
            } else {
                textPollPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + "");
            }
            textPollPostViewHolder.postCommentsTextView
                    .setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                textPollPostViewHolder.usernameTextView.setText(context.getString(R.string.groups_anonymous));
                textPollPostViewHolder.userImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
            } else {
                textPollPostViewHolder.usernameTextView.setText(
                        groupPostResult.getUserInfo().getFirstName() + " " + groupPostResult.getUserInfo()
                                .getLastName());
                try {
                    Picasso.get().load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(textPollPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPollPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            textPollPostViewHolder.userImageView.setOnClickListener(view -> {
                if (groupPostResult.getIsAnnon() == 0) {
                    launchProfile(groupPostResult.getUserId());
                }
            });
            textPollPostViewHolder.usernameTextView.setOnClickListener(view -> {
                if (groupPostResult.getIsAnnon() == 0) {
                    launchProfile(groupPostResult.getUserId());
                }
            });
            textPollPostViewHolder.option3Container.setVisibility(View.GONE);
            textPollPostViewHolder.option4Container.setVisibility(View.GONE);
            Map<String, String> optionsMap = (Map<String, String>) groupPostResult.getPollOptions();
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
                    default:
                        break;
                }
            }
            textPollPostViewHolder.pollOption1ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption2ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption3ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption4ProgressBar.setProgress(0f);
            textPollPostViewHolder.totalVoteCountTextView
                    .setText(context.getString(R.string.groups_total_votes, groupPostResult.getTotalVotesCount()));
            if (groupPostResult.isVoted()) {
                showVotingData(textPollPostViewHolder, groupPostResult);
            } else {
                hideVotingData(textPollPostViewHolder);
            }

            if (groupPostResult.getMarkedHelpful() == 1) {
                textPollPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                textPollPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }
        } else if (holder instanceof ImagePollPostViewHolder) {
            ImagePollPostViewHolder imageHolder = (ImagePollPostViewHolder) holder;

            if (groupPostResult.getIsAnnon() == 1) {
                imageHolder.userTag.setVisibility(View.GONE);
            } else {
                if (groupPostResult != null && groupPostResult.getUserInfo() != null
                        && groupPostResult.getUserInfo().getUserTag() != null) {
                    if (groupPostResult.getUserInfo().getUserTag().size() != 0) {
                        imageHolder.userTag.setText(groupPostResult.getUserInfo().getUserTag().get(0));
                    } else {
                        imageHolder.userTag.setVisibility(View.GONE);
                    }
                }
            }

            imageHolder.pollQuestionTextView.setText(groupPostResult.getContent());
            Linkify.addLinks(imageHolder.pollQuestionTextView, Linkify.WEB_URLS);
            imageHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            imageHolder.pollQuestionTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(imageHolder.pollQuestionTextView);

            imageHolder.postDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            imageHolder.usernameTextView.setText(groupPostResult.getUserId());
            if (groupPostResult.getHelpfullCount() < 1) {
                imageHolder.upvoteCountTextView.setText("");
            } else {
                imageHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + "");
            }
            imageHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                imageHolder.usernameTextView.setText(context.getString(R.string.groups_anonymous));
                imageHolder.userImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
            } else {
                imageHolder.usernameTextView.setText(
                        groupPostResult.getUserInfo().getFirstName() + " " + groupPostResult.getUserInfo()
                                .getLastName());
                try {
                    Picasso.get().load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(imageHolder.userImageView);
                } catch (Exception e) {
                    imageHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            imageHolder.userImageView.setOnClickListener(view -> {

                if (groupPostResult.getIsAnnon() == 0) {

                    launchProfile(groupPostResult.getUserId());
                }
            });
            imageHolder.usernameTextView.setOnClickListener(view -> {

                if (groupPostResult.getIsAnnon() == 0) {

                    launchProfile(groupPostResult.getUserId());
                }
            });
            imageHolder.lastOptionsContainer.setVisibility(View.GONE);
            imageHolder.option3Container.setVisibility(View.GONE);
            imageHolder.option4Container.setVisibility(View.GONE);
            Map<String, String> imageMap = (Map<String, String>) groupPostResult.getPollOptions();
            for (Map.Entry<String, String> entry : imageMap.entrySet()) {
                switch (entry.getKey()) {
                    case "option1":
                        Picasso.get().load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                .into(imageHolder.option1ImageView);
                        break;
                    case "option2":
                        Picasso.get().load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                .into(imageHolder.option2ImageView);
                        break;
                    case "option3":
                        imageHolder.lastOptionsContainer.setVisibility(View.VISIBLE);
                        imageHolder.option3Container.setVisibility(View.VISIBLE);
                        Picasso.get().load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                .into(imageHolder.option3ImageView);
                        break;
                    case "option4":
                        imageHolder.lastOptionsContainer.setVisibility(View.VISIBLE);
                        imageHolder.option4Container.setVisibility(View.VISIBLE);
                        Picasso.get().load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                .into(imageHolder.option4ImageView);
                        break;
                    default:
                        break;
                }
            }
            imageHolder.pollOption1ProgressBar.setProgress(0f);
            imageHolder.pollOption2ProgressBar.setProgress(0f);
            imageHolder.pollOption3ProgressBar.setProgress(0f);
            imageHolder.pollOption4ProgressBar.setProgress(0f);
            imageHolder.totalVoteCountTextView
                    .setText(context.getString(R.string.groups_total_votes, groupPostResult.getTotalVotesCount()));
            if (groupPostResult.isVoted()) {
                showImagePollVotingData(imageHolder);
            } else {
                hideImagePollVotingData(imageHolder);
            }

            if (groupPostResult.getMarkedHelpful() == 1) {
                imageHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                imageHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }
        } else if (holder instanceof AudioCommentViewHolder) {
            AudioCommentViewHolder audioCommentViewHolder = (AudioCommentViewHolder) holder;

            if (postCommentsList.get(position).getIsAnnon() == 1) {
                audioCommentViewHolder.commentorUsernameTextView.setText(context.getString(R.string.groups_anonymous));
                audioCommentViewHolder.commentorImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) postCommentsList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    audioCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.replyCommentTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                } else {
                    audioCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.replyCommentTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                }
            } else {
                audioCommentViewHolder.commentorImageView.setOnClickListener(view -> {
                    if (postCommentsList.get(position).getIsAnnon() == 0) {
                        launchProfile(postCommentsList.get(position).getUserId());
                    }
                });
                audioCommentViewHolder.commentorUsernameTextView.setOnClickListener(view -> {
                    if (postCommentsList.get(position).getIsAnnon() == 0) {
                        launchProfile(postCommentsList.get(position).getUserId());
                    }
                });
                audioCommentViewHolder.commentorUsernameTextView
                        .setText(postCommentsList.get(position).getUserInfo().getFirstName()
                                + " " + postCommentsList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.get().load(postCommentsList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                            .into(audioCommentViewHolder.commentorImageView);
                } catch (Exception e) {
                    audioCommentViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) postCommentsList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    audioCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.replyCommentTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                } else {
                    audioCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.replyCommentTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    audioCommentViewHolder.media.setVisibility(View.GONE);
                }
            }
            if (!StringUtils.isNullOrEmpty(postCommentsList.get(position).getContent())) {
                audioCommentViewHolder.commentDataTextView.setVisibility(View.VISIBLE);
                audioCommentViewHolder.commentDataTextView.setText(postCommentsList.get(position).getContent());
                Linkify.addLinks(audioCommentViewHolder.commentDataTextView, Linkify.WEB_URLS);
                audioCommentViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
                audioCommentViewHolder.commentDataTextView
                        .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
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

            audioCommentViewHolder.commentDateTextView
                    .setText(DateTimeUtils.getFormattedDateGroups(postCommentsList.get(position).getCreatedAt()));
            if (postCommentsList.get(position).getChildData() == null || postCommentsList.get(position).getChildData()
                    .isEmpty()) {
                audioCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
            } else {
                audioCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                audioCommentViewHolder.replyCountTextView.setText(
                        context.getString(R.string.view_group) + "(" + postCommentsList.get(position).getChildCount()
                                + ")" + context.getString(R.string.replies));
            }
            if (postCommentsList.get(position).getHelpfullCount() < 1) {
                audioCommentViewHolder.upvoteCommentCountTextView.setText("");
            } else {
                audioCommentViewHolder.upvoteCommentCountTextView
                        .setText(postCommentsList.get(position).getHelpfullCount() + "");
            }
            if (postCommentsList.get(position).getMarkedHelpful() == 1) {
                audioCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                audioCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);

            }
        } else {
            RootCommentViewHolder rootCommentViewHolder = (RootCommentViewHolder) holder;
            if (postCommentsList.get(position).getIsAnnon() == 1) {
                rootCommentViewHolder.commentorUsernameTextView.setText(context.getString(R.string.groups_anonymous));
                rootCommentViewHolder.commentorImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) postCommentsList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    rootCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                    rootCommentViewHolder.replyCommentTextView.setVisibility(View.GONE);
                    rootCommentViewHolder.commentDateTextView.setVisibility(View.GONE);
                    rootCommentViewHolder.media.setVisibility(View.VISIBLE);
                    Picasso.get().load(mediaList.get(0)).error(R.drawable.default_article)
                            .into(rootCommentViewHolder.media);
                } else {
                    rootCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.replyCommentTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.media.setVisibility(View.GONE);
                }
            } else {
                rootCommentViewHolder.commentorImageView.setOnClickListener(view -> {
                    if (postCommentsList.get(position).getIsAnnon() == 0) {
                        launchProfile(postCommentsList.get(position).getUserId());
                    }
                });
                rootCommentViewHolder.commentorUsernameTextView.setOnClickListener(view -> {
                    if (postCommentsList.get(position).getIsAnnon() == 0) {
                        launchProfile(postCommentsList.get(position).getUserId());
                    }
                });
                rootCommentViewHolder.commentorUsernameTextView
                        .setText(postCommentsList.get(position).getUserInfo().getFirstName()
                                + " " + postCommentsList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.get().load(postCommentsList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                            .into(rootCommentViewHolder.commentorImageView);
                } catch (Exception e) {
                    rootCommentViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                ArrayList<String> mediaList = new ArrayList<>();
                Map<String, String> map = (Map<String, String>) postCommentsList.get(position).getMediaUrls();
                if (map != null && !map.isEmpty()) {
                    for (String entry : map.values()) {
                        mediaList.add(entry);
                    }
                    rootCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                    rootCommentViewHolder.replyCommentTextView.setVisibility(View.GONE);
                    rootCommentViewHolder.commentDateTextView.setVisibility(View.GONE);
                    rootCommentViewHolder.media.setVisibility(View.VISIBLE);
                    Picasso.get().load(mediaList.get(0)).error(R.drawable.default_article)
                            .into(rootCommentViewHolder.media);
                } else {
                    rootCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.replyCommentTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.media.setVisibility(View.GONE);
                }
            }
            rootCommentViewHolder.commentDataTextView.setText(postCommentsList.get(position).getContent());
            Linkify.addLinks(rootCommentViewHolder.commentDataTextView, Linkify.WEB_URLS);
            rootCommentViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            rootCommentViewHolder.commentDataTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(rootCommentViewHolder.commentDataTextView);

            rootCommentViewHolder.commentDateTextView
                    .setText(DateTimeUtils.getFormattedDateGroups(postCommentsList.get(position).getCreatedAt()));
            if (postCommentsList.get(position).getChildData() == null || postCommentsList.get(position).getChildData()
                    .isEmpty()) {
                rootCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
            } else {
                rootCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                rootCommentViewHolder.replyCountTextView.setText(
                        context.getString(R.string.view_group) + "(" + postCommentsList.get(position).getChildCount()
                                + ")" + context.getString(R.string.replies));
            }
            if (postCommentsList.get(position).getHelpfullCount() < 1) {
                rootCommentViewHolder.upvoteCommentCountTextView.setText("");
            } else {
                rootCommentViewHolder.upvoteCommentCountTextView
                        .setText(postCommentsList.get(position).getHelpfullCount() + "");
            }
            if (postCommentsList.get(position).getMarkedHelpful() == 1) {
                rootCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                rootCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);
            }
        }
    }

    private void launchProfile(String userId) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (playingPosition == holder.getAdapterPosition() && playingHolder != null) {
            updateNonPlayingView(playingHolder);
            playingHolder = null;
        }
    }

    private void updateNonPlayingView(AudioCommentViewHolder holder) {

        if (holder == playingHolder) {
            handler.removeMessages(MSG_UPDATE_SEEK_BAR);
            handler.removeMessages(MSG_UPDATE_TIME);
        }
        if (holder instanceof AudioCommentViewHolder) {
            holder.audioSeekBar.setEnabled(false);
            holder.audioSeekBar.setProgress(0);
            holder.playAudioImageView.setImageResource(R.drawable.play);
            holder.audioTimeElapsed.setVisibility(View.GONE);
        }
    }

    private void updateNonPlayingHeaderView(AudioCommentViewHeaderHolder holder) {

        if (holder == playingHeaderHolder) {
            handler.removeMessages(MSG_UPDATE_SEEK_BAR);
            handler.removeMessages(MSG_UPDATE_TIME);
        }
        if (holder instanceof AudioCommentViewHeaderHolder) {
            holder.headerAudioSeekBar.setEnabled(false);
            holder.headerAudioSeekBar.setProgress(0);
            holder.playHeaderAudioImageView.setImageResource(R.drawable.play);
            holder.headerAudioTimeElapsed.setVisibility(View.GONE);
        }
    }

    private void updatePlayingView() {
        playingHolder.audioSeekBar.setMax(mediaPlayer.getDuration());
        playingHolder.audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
        playingHolder.audioSeekBar.setEnabled(true);
        if (mediaPlayer.isPlaying()) {
            playingHolder.audioTimeElapsed.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 1000);
            handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
            playingHolder.playAudioImageView.setImageResource(R.drawable.pause);
        } else {
            handler.removeMessages(MSG_UPDATE_SEEK_BAR);
            handler.removeMessages(MSG_UPDATE_TIME);
            playingHolder.playAudioImageView.setImageResource(R.drawable.play);
            playingHolder.audioTimeElapsed.setVisibility(View.GONE);
        }
    }

    private void updatePlayingHeaderView() {
        playingHeaderHolder.headerAudioSeekBar.setMax(mediaPlayerHeader.getDuration());
        playingHeaderHolder.headerAudioSeekBar.setProgress(mediaPlayerHeader.getCurrentPosition());
        playingHeaderHolder.headerAudioSeekBar.setEnabled(true);
        if (mediaPlayerHeader.isPlaying()) {
            playingHeaderHolder.headerAudioTimeElapsed.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR_HEADER, 1000);
            handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME_HEADER, 1000);
            playingHeaderHolder.playHeaderAudioImageView.setImageResource(R.drawable.pause);
        } else {
            handler.removeMessages(MSG_UPDATE_SEEK_BAR_HEADER);
            handler.removeMessages(MSG_UPDATE_TIME_HEADER);
            playingHeaderHolder.playHeaderAudioImageView.setImageResource(R.drawable.play);
            playingHeaderHolder.headerAudioTimeElapsed.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        long totalDuration;
        long currentDuration;
        switch (msg.what) {
            case MSG_UPDATE_SEEK_BAR: {
                if (mediaPlayer != null) {
                    playingHolder.audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 1000);
                    return true;
                }
            }
            case MSG_UPDATE_TIME: {
                if (mediaPlayer != null) {
                    totalDuration = mediaPlayer.getDuration();
                    currentDuration = mediaPlayer.getCurrentPosition();
                    playingHolder.audioTimeElapsed
                            .setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
                    return true;
                }
            }
            case MSG_UPDATE_SEEK_BAR_HEADER: {
                if (mediaPlayerHeader != null) {
                    playingHeaderHolder.headerAudioSeekBar.setProgress(mediaPlayerHeader.getCurrentPosition());
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR_HEADER, 1000);
                    return true;
                }
            }
            case MSG_UPDATE_TIME_HEADER: {
                if (mediaPlayerHeader != null) {
                    totalDuration = mediaPlayerHeader.getDuration();
                    currentDuration = mediaPlayerHeader.getCurrentPosition();
                    playingHeaderHolder.headerAudioTimeElapsed
                            .setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME_HEADER, 1000);
                    return true;
                }
            }
            break;
            default:
                break;
        }
        return false;
    }

    private void startMediaPlayer(int position) {
        if (mediaPlayerHeader != null && isPostPlaying) {
            mediaPlayerHeader.release();
            mediaPlayerHeader = null;
            isPostPlaying = false;
            if (playingHeaderHolder != null) {
                playingHeaderHolder.playHeaderAudioImageView.setImageResource(R.drawable.play);
                playingHeaderHolder.headerAudioSeekBar.setProgress(0);
                playingHeaderHolder.headerAudioTimeElapsed.setVisibility(View.GONE);
            }
        }
        mediaPlayer = new MediaPlayer();
        Map<String, String> map = (Map<String, String>) postCommentsList.get(position).getMediaUrls();
        for (String entry : map.values()) {
            fetchAudioUrlFromFirebase(entry);
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


    private void startHeaderMediaPlayer() {
        if (mediaPlayer != null && isCommentPlaying) {
            mediaPlayer.release();
            mediaPlayer = null;
            isCommentPlaying = false;
            if (playingHolder != null) {
                playingHolder.playAudioImageView.setImageResource(R.drawable.play);
                playingHolder.audioSeekBar.setProgress(0);
                playingHolder.audioTimeElapsed.setVisibility(View.GONE);
            }
        }
        mediaPlayerHeader = new MediaPlayer();
        Map<String, String> map = (Map<String, String>) groupPostResult.getMediaUrls();
        for (String entry : map.values()) {
            fetchHeaderAudioUrlFromFirebase(entry);
        }
    }

    private void releaseHeaderMediaPlayer() {
        if (null != playingHeaderHolder) {
            updateNonPlayingHeaderView(playingHeaderHolder);
        }
        mediaPlayerHeader.stop();
        mediaPlayerHeader.release();
        mediaPlayerHeader = null;
        playingPosition = -1;
    }


    private void showVotingData(TextPollPostViewHolder textPollPostViewHolder, GroupPostResult postResult) {
        textPollPostViewHolder.pollOption1ProgressBar
                .setProgress((100f * postResult.getOption1VoteCount()) / postResult.getTotalVotesCount());
        textPollPostViewHolder.pollOption2ProgressBar
                .setProgress((100f * postResult.getOption2VoteCount()) / postResult.getTotalVotesCount());
        textPollPostViewHolder.pollOption3ProgressBar
                .setProgress((100f * postResult.getOption3VoteCount()) / postResult.getTotalVotesCount());
        textPollPostViewHolder.pollOption4ProgressBar
                .setProgress((100f * postResult.getOption4VoteCount()) / postResult.getTotalVotesCount());
        textPollPostViewHolder.pollOption1ProgressTextView.setText(
                AppUtils.round((100f * postResult.getOption1VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        textPollPostViewHolder.pollOption2ProgressTextView.setText(
                AppUtils.round((100f * postResult.getOption2VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        textPollPostViewHolder.pollOption3ProgressTextView.setText(
                AppUtils.round((100f * postResult.getOption3VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        textPollPostViewHolder.pollOption4ProgressTextView.setText(
                AppUtils.round((100f * postResult.getOption4VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
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

    private void showImagePollVotingData(ImagePollPostViewHolder imageHolder) {
        imageHolder.pollOption1ProgressBar
                .setProgress((100f * groupPostResult.getOption1VoteCount()) / groupPostResult.getTotalVotesCount());
        imageHolder.pollOption2ProgressBar
                .setProgress((100f * groupPostResult.getOption2VoteCount()) / groupPostResult.getTotalVotesCount());
        imageHolder.pollOption3ProgressBar
                .setProgress((100f * groupPostResult.getOption3VoteCount()) / groupPostResult.getTotalVotesCount());
        imageHolder.pollOption4ProgressBar
                .setProgress((100f * groupPostResult.getOption4VoteCount()) / groupPostResult.getTotalVotesCount());
        imageHolder.pollOption1TextView.setText(
                AppUtils.round((100f * groupPostResult.getOption1VoteCount()) / groupPostResult.getTotalVotesCount(), 2)
                        + "%");
        imageHolder.pollOption2TextView.setText(
                AppUtils.round((100f * groupPostResult.getOption2VoteCount()) / groupPostResult.getTotalVotesCount(), 2)
                        + "%");
        imageHolder.pollOption3TextView.setText(
                AppUtils.round((100f * groupPostResult.getOption3VoteCount()) / groupPostResult.getTotalVotesCount(), 2)
                        + "%");
        imageHolder.pollOption4TextView.setText(
                AppUtils.round((100f * groupPostResult.getOption4VoteCount()) / groupPostResult.getTotalVotesCount(), 2)
                        + "%");
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
        Map<String, String> map = (Map<String, String>) groupPostResult.getMediaUrls();
        for (String entry : map.values()) {
            mediaList.add(entry);
        }
        holder.viewPagerAdapter.setDataList(mediaList);
        holder.postDataViewPager.setAdapter(holder.viewPagerAdapter);
        holder.dotIndicatorView.attachToViewPager(holder.postDataViewPager);
        if (mediaList.size() == 1) {
            holder.indexTextView.setVisibility(View.GONE);
            holder.dotIndicatorView.setVisibility(View.GONE);
        } else {
            holder.indexTextView.setVisibility(View.VISIBLE);
            holder.dotIndicatorView.setVisibility(View.VISIBLE);
        }
        int currentPagerPos = 0;
        holder.postDataViewPager.setCurrentItem(currentPagerPos);
        holder.indexTextView.setText((holder.postDataViewPager.getCurrentItem() + 1) + "/" + mediaList.size());
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        ImageView upvoteImageVIew;
        ImageView whatsappShare;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        LinearLayout upvoteContainer;
        LinearLayout downvoteContainer;
        TextView postCommentsTextView;
        TextView userTag;
        ImageView postSettingImageView;
        ImageView shareTextView;

        TextPostViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            userTag = (TextView) view.findViewById(R.id.userTag);

            whatsappShare.setOnClickListener(this);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class MediaPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        ImageView upvoteImageVIew;
        ImageView whatsappShare;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        LinearLayout upvoteContainer;
        LinearLayout downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        ImageView shareTextView;
        private IndefinitePagerIndicator dotIndicatorView;
        private GroupPostMediaViewPager postDataViewPager;
        private TextView indexTextView;
        TextView userTag;
        private GroupMediaPostViewPagerAdapter viewPagerAdapter;

        MediaPostViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            dotIndicatorView = (IndefinitePagerIndicator) view.findViewById(R.id.dotIndicatorView);
            postDataViewPager = (GroupPostMediaViewPager) view.findViewById(R.id.postDataViewPager);
            indexTextView = (TextView) view.findViewById(R.id.indexTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            userTag = (TextView) view.findViewById(R.id.userTag);
            whatsappShare.setOnClickListener(this);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);

            viewPagerAdapter = new GroupMediaPostViewPagerAdapter(context);
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

        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class TextPollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        ImageView upvoteImageVIew;
        ImageView whatsappShare;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        LinearLayout upvoteContainer;
        LinearLayout downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        ImageView shareTextView;
        TextView pollQuestionTextView;
        RoundCornerProgressBar pollOption1ProgressBar;
        RoundCornerProgressBar pollOption2ProgressBar;
        RoundCornerProgressBar pollOption3ProgressBar;
        RoundCornerProgressBar pollOption4ProgressBar;
        TextView pollOption1TextView;
        TextView pollOption2TextView;
        TextView pollOption3TextView;
        TextView pollOption4TextView;
        TextView pollResult1TextView;
        TextView pollResult2TextView;
        TextView pollResult3TextView;
        TextView pollResult4TextView;
        TextView pollOption1ProgressTextView;
        TextView pollOption2ProgressTextView;
        TextView pollOption3ProgressTextView;
        TextView pollOption4ProgressTextView;
        TextView totalVoteCountTextView;
        TextView userTag;
        RelativeLayout option3Container;
        RelativeLayout option4Container;

        TextPollPostViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
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
            option3Container = (RelativeLayout) view.findViewById(R.id.option3Container);
            option4Container = (RelativeLayout) view.findViewById(R.id.option4Container);
            totalVoteCountTextView = (TextView) view.findViewById(R.id.totalVoteCountTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            userTag = (TextView) view.findViewById(R.id.userTag);
            whatsappShare.setOnClickListener(this);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);

            pollOption1ProgressBar.setOnClickListener(v -> addVote(getAdapterPosition(), "option1"));
            pollOption2ProgressBar.setOnClickListener(v -> addVote(getAdapterPosition(), "option2"));
            pollOption3ProgressBar.setOnClickListener(v -> addVote(getAdapterPosition(), "option3"));
            pollOption4ProgressBar.setOnClickListener(v -> addVote(getAdapterPosition(), "option4"));
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class ImagePollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        ImageView shareTextView;
        ImageView upvoteImageVIew;
        ImageView whatsappShare;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        LinearLayout upvoteContainer;
        LinearLayout downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        TextView pollQuestionTextView;
        TextView userTag;
        ImageView option1ImageView;
        ImageView option2ImageView;
        ImageView option3ImageView;
        ImageView option4ImageView;
        RoundCornerProgressBar pollOption1ProgressBar;
        RoundCornerProgressBar pollOption2ProgressBar;
        RoundCornerProgressBar pollOption3ProgressBar;
        RoundCornerProgressBar pollOption4ProgressBar;
        TextView pollOption1TextView;
        TextView pollOption2TextView;
        TextView pollOption3TextView;
        TextView pollOption4TextView;
        TextView totalVoteCountTextView;
        RelativeLayout option1Container;
        RelativeLayout option2Container;
        RelativeLayout option3Container;
        RelativeLayout option4Container;
        LinearLayout lastOptionsContainer;

        ImagePollPostViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
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
            pollQuestionTextView = (TextView) view.findViewById(R.id.pollQuestionTextView);
            lastOptionsContainer = (LinearLayout) view.findViewById(R.id.lastOptionsContainer);
            option1Container = (RelativeLayout) view.findViewById(R.id.option1Container);
            option2Container = (RelativeLayout) view.findViewById(R.id.option2Container);
            option3Container = (RelativeLayout) view.findViewById(R.id.option3Container);
            option4Container = (RelativeLayout) view.findViewById(R.id.option4Container);
            totalVoteCountTextView = (TextView) view.findViewById(R.id.totalVoteCountTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            userTag = (TextView) view.findViewById(R.id.userTag);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);

            option1Container.setOnClickListener(v -> addVote(getAdapterPosition(), "option1"));
            option2Container.setOnClickListener(v -> addVote(getAdapterPosition(), "option2"));
            option3Container.setOnClickListener(v -> addVote(getAdapterPosition(), "option3"));
            option4Container.setOnClickListener(v -> addVote(getAdapterPosition(), "option4"));
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }


    public class AudioCommentViewHeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, SeekBar.OnSeekBarChangeListener {

        ImageView commentorImageView;
        ImageView playHeaderAudioImageView;
        ImageView pauseHeaderAudioImageView;
        ImageView profileImageView;
        ImageView whatsappShare;
        ImageView media;
        ImageView upvoteImageVIew;
        TextView commentorUsernameTextView;
        TextView headerAudioTimeElapsed;
        TextView commentDataTextView;
        TextView commentDateTextView;
        TextView userTag;
        View underlineView;
        SeekBar headerAudioSeekBar;
        TextView upvoteCommentCountTextView;
        TextView downvoteCommentCountTextView;
        LinearLayout upvoteCommentContainer;
        LinearLayout downvoteCommentContainer;
        RelativeLayout audiotRootView;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        ImageView shareTextView;

        public AudioCommentViewHeaderHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            media = (ImageView) view.findViewById(R.id.media);
            profileImageView = (ImageView) view.findViewById(R.id.profileImageView);
            audiotRootView = view.findViewById(R.id.commentRootView);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            upvoteCommentCountTextView = (TextView) view.findViewById(R.id.upvoteCommentTextView);
            downvoteCommentCountTextView = (TextView) view.findViewById(R.id.downvoteCommentTextView);
            upvoteCommentContainer = (LinearLayout) view.findViewById(R.id.upvoteCommentContainer);
            downvoteCommentContainer = (LinearLayout) view.findViewById(R.id.downvoteCommentContainer);
            playHeaderAudioImageView = (ImageView) view.findViewById(R.id.playAudioImageView);
            pauseHeaderAudioImageView = (ImageView) view.findViewById(R.id.pauseAudioImageView);
            headerAudioSeekBar = (SeekBar) view.findViewById(R.id.audioSeekBar);
            headerAudioTimeElapsed = (TextView) view.findViewById(R.id.audioTimeElapsed);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            userTag = (TextView) view.findViewById(R.id.userTag);

            commentDataTextView.setOnLongClickListener(this);
            view.setOnLongClickListener(this);
            downvoteCommentContainer.setOnClickListener(this);
            upvoteCommentContainer.setOnClickListener(this);
            playHeaderAudioImageView.setOnClickListener(this);
            headerAudioSeekBar.setOnSeekBarChangeListener(this);
            postSettingImageView.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            profileImageView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);

            underlineView = view.findViewById(R.id.underlineView);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.playAudioImageView) {
                isPostPlaying = true;
                if (getAdapterPosition() == playingPosition) {
                    // toggle between play/pause of audio
                    if (mediaPlayerHeader.isPlaying()) {
                        mediaPlayerHeader.pause();
                        updateNonPlayingHeaderView(playingHeaderHolder);
                    } else {
                        mediaPlayerHeader.start();
                        playingHeaderHolder = this;
                        updatePlayingHeaderView();
                    }
                } else {
                    // start another audio playback
                    playingPosition = getAdapterPosition();
                    if (mediaPlayerHeader != null) {
                        if (null != playingHeaderHolder) {
                            updateNonPlayingHeaderView(playingHeaderHolder);
                        }
                        mediaPlayerHeader.release();
                    }
                    playingHeaderHolder = this;
                    startHeaderMediaPlayer();
                    showProgressDialog(context.getString(R.string.please_wait));
                }
            } else {
                recyclerViewClickListener.onRecyclerItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            recyclerViewClickListener.onRecyclerItemClick(view, getAdapterPosition());
            return true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                mediaPlayerHeader.seekTo(i);
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

    public class AudioCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, SeekBar.OnSeekBarChangeListener {

        ImageView commentorImageView;
        ImageView playAudioImageView;
        ImageView pauseAudioImageView;
        ImageView media;
        ImageView upvoteImageVIew;
        TextView commentorUsernameTextView;
        TextView audioTimeElapsed;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;
        View underlineView;
        SeekBar audioSeekBar;
        TextView upvoteCommentCountTextView;
        TextView downvoteCommentCountTextView;
        LinearLayout upvoteCommentContainer;
        LinearLayout downvoteCommentContainer;

        public AudioCommentViewHolder(View view) {
            super(view);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
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
                isCommentPlaying = true;
                if (getAdapterPosition() == playingPosition) {
                    // toggle between play/pause of audio
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        updateNonPlayingView(playingHolder);
                    } else {
                        mediaPlayer.start();
                        playingHolder = this;
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
                    showProgressDialog(context.getString(R.string.please_wait));
                }
            } else {
                recyclerViewClickListener.onRecyclerItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            recyclerViewClickListener.onRecyclerItemClick(view, getAdapterPosition());
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

    public class RootCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        ImageView commentorImageView;
        ImageView media;
        ImageView upvoteImageVIew;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;
        View underlineView;
        TextView upvoteCommentCountTextView;
        TextView downvoteCommentCountTextView;
        LinearLayout upvoteCommentContainer;
        LinearLayout downvoteCommentContainer;

        RootCommentViewHolder(View view) {
            super(view);
            media = (ImageView) view.findViewById(R.id.media);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
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

            commentDataTextView.setOnLongClickListener(this);
            view.setOnLongClickListener(this);
            replyCommentTextView.setOnClickListener(this);
            replyCountTextView.setOnClickListener(this);
            downvoteCommentContainer.setOnClickListener(this);
            upvoteCommentContainer.setOnClickListener(this);

            underlineView = view.findViewById(R.id.underlineView);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            recyclerViewClickListener.onRecyclerItemClick(view, getAdapterPosition());
            return true;
        }
    }

    private void addVote(int position, String option) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(groupPostResult.getGroupId());
        groupActionsRequest.setPostId(groupPostResult.getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(context).getDynamoId());
        groupActionsRequest.setVoteOption(option);
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupsActionVoteResponse> call = groupsApi.addActionVote(groupActionsRequest);
        call.enqueue(groupActionVoteResponseCallback);
    }

    private Callback<GroupsActionVoteResponse> groupActionVoteResponseCallback =
            new Callback<GroupsActionVoteResponse>() {
                @Override
                public void onResponse(Call<GroupsActionVoteResponse> call,
                        Response<GroupsActionVoteResponse> response) {
                    if (response.body() == null) {
                        if (response.code() == 400) {
                            try {
                                String errorBody = new String(response.errorBody().bytes());
                                JSONObject jsonObject = new JSONObject(errorBody);
                                String reason = jsonObject.getString("reason");
                                if (!StringUtils.isNullOrEmpty(reason) && "already voted".equals(reason)) {
                                    groupPostResult.setVoted(true);
                                    groupPostResult.setTotalVotesCount(0);
                                    for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                                        groupPostResult.setTotalVotesCount(groupPostResult.getTotalVotesCount()
                                                + Integer.parseInt(
                                                jsonObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                        switch (jsonObject.getJSONArray("data").getJSONObject(i).getString("name")) {
                                            case "option1":
                                                groupPostResult.setOption1VoteCount(Integer.parseInt(
                                                        jsonObject.getJSONArray("data").getJSONObject(i)
                                                                .getString("count")));
                                                break;
                                            case "option2":
                                                groupPostResult.setOption2VoteCount(Integer.parseInt(
                                                        jsonObject.getJSONArray("data").getJSONObject(i)
                                                                .getString("count")));
                                                break;
                                            case "option3":
                                                groupPostResult.setOption3VoteCount(Integer.parseInt(
                                                        jsonObject.getJSONArray("data").getJSONObject(i)
                                                                .getString("count")));
                                                break;
                                            case "option4":
                                                groupPostResult.setOption4VoteCount(Integer.parseInt(
                                                        jsonObject.getJSONArray("data").getJSONObject(i)
                                                                .getString("count")));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    notifyDataSetChanged();
                                }
                            } catch (IOException | JSONException e) {
                                Crashlytics.logException(e);
                                Log.d("MC4kException", Log.getStackTraceString(e));
                            }
                        }
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            GroupsActionVoteResponse groupsActionResponse = response.body();
                            groupPostResult.setVoted(true);
                            switch (groupsActionResponse.getData().getResult().get(0).getVoteOption()) {
                                case "option1":
                                    groupPostResult.setOption1VoteCount(groupPostResult.getOption1VoteCount() + 1);
                                    groupPostResult.setTotalVotesCount(groupPostResult.getTotalVotesCount() + 1);
                                    break;
                                case "option2":
                                    groupPostResult.setOption2VoteCount(groupPostResult.getOption2VoteCount() + 1);
                                    groupPostResult.setTotalVotesCount(groupPostResult.getTotalVotesCount() + 1);
                                    break;
                                case "option3":
                                    groupPostResult.setOption3VoteCount(groupPostResult.getOption3VoteCount() + 1);
                                    groupPostResult.setTotalVotesCount(groupPostResult.getTotalVotesCount() + 1);
                                    break;
                                case "option4":
                                    groupPostResult.setOption4VoteCount(groupPostResult.getOption4VoteCount() + 1);
                                    groupPostResult.setTotalVotesCount(groupPostResult.getTotalVotesCount() + 1);
                                    break;
                                default:
                                    break;
                            }
                            notifyDataSetChanged();
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

        private String url;

        CustomerTextClick(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(context, NewsLetterWebviewActivity.class);
            intent.putExtra(Constants.URL, url);
            context.startActivity(intent);
        }
    }

    private void fetchAudioUrlFromFirebase(String url) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        StorageReference storageRef = storage.getReferenceFromUrl(url);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                // Download url of file
                final String url1 = uri.toString();
                mediaPlayer.setDataSource(url1);
                // wait for media player to get prepare
                mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    removeProgressDialog();
                    mediaPlayer.start();
                    updatePlayingView();
                });
                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    handler.removeMessages(MSG_UPDATE_SEEK_BAR);
                    handler.removeMessages(MSG_UPDATE_TIME);
                    releaseMediaPlayer();
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> Log.i("TAG", e.getMessage()));
    }

    private void fetchHeaderAudioUrlFromFirebase(String url) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app

        mediaPlayerHeader.setAudioStreamType(AudioManager.STREAM_MUSIC);
        StorageReference storageRef = storage.getReferenceFromUrl(url);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    // Download url of file
                    final String url = uri.toString();
                    mediaPlayerHeader.setDataSource(url);
                    // wait for media player to get prepare
                    mediaPlayerHeader.setOnPreparedListener(mediaPlayer -> {
                        removeProgressDialog();
                        mediaPlayer.start();
                        updatePlayingHeaderView();
                    });
                    mediaPlayerHeader.setOnCompletionListener(mediaPlayer -> {
                        handler.removeMessages(MSG_UPDATE_SEEK_BAR);
                        handler.removeMessages(MSG_UPDATE_TIME);
                        releaseHeaderMediaPlayer();
                    });
                    mediaPlayerHeader.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(e -> Log.i("TAG", e.getMessage()));
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

    }

    private String milliSecondsToTimer(long milliseconds) {
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
            updateNonPlayingView(playingHolder);
            mediaPlayer.release();
            mediaPlayer = null;
            playingPosition = -1;
        } else if (mediaPlayerHeader != null) {
            updateNonPlayingHeaderView(playingHeaderHolder);
            mediaPlayerHeader.release();
            mediaPlayerHeader = null;
            playingPosition = -1;
        }
    }

    public void showProgressDialog(String bodyText) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(bodyText);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
