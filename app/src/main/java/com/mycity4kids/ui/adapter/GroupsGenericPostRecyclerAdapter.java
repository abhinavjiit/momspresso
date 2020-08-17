package com.mycity4kids.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.internal.LinkedTreeMap;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsActionVoteResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.fragment.AddGpPostCommentReplyDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.mycity4kids.widget.IndefinitePagerIndicator;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupsGenericPostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        Handler.Callback {

    private static final int POST_TYPE_TEXT = 0;
    private static final int POST_TYPE_MEDIA = 1;
    private static final int POST_TYPE_TEXT_POLL = 2;
    private static final int POST_TYPE_IMAGE_POLL = 3;
    private static final int POST_TYPE_AUDIO = 4;

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final GroupResult selectedGroup;
    private final String localizedComment;
    private ArrayList<GroupPostResult> postList;
    private HashMap<Integer, Integer> viewPageStates = new HashMap<>();
    private RecyclerViewClickListener recyclerViewClickListener;
    private int pollPosition;
    private String memberType;
    private int playingPosition;
    private ProgressDialog progressDialog;
    private AudioCommentViewHolder playingHolder;
    private MediaPlayer mediaPlayer;
    private static final int MSG_UPDATE_SEEK_BAR = 1845;
    private static final int MSG_UPDATE_TIME = 1846;
    private Handler handler;
    private long currentDuration;

    public GroupsGenericPostRecyclerAdapter(Context context, RecyclerViewClickListener listener,
            GroupResult selectedGroup, String memberType) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recyclerViewClickListener = listener;
        this.selectedGroup = selectedGroup;
        localizedComment = this.context.getString(R.string.ad_comments_title);
        this.memberType = memberType;
        handler = new Handler(this);
        this.playingPosition = -1;
        setHasStableIds(true);
    }

    public void setData(ArrayList<GroupPostResult> postList) {
        this.postList = postList;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        switch (postList.get(position).getType()) {
            case "0":
            case "5":
                return POST_TYPE_TEXT;
            case "1":
            case "4":
                return POST_TYPE_MEDIA;
            case "2":
                if (postList.get(position).getPollType().equals("1")) {
                    return POST_TYPE_IMAGE_POLL;
                } else {
                    return POST_TYPE_TEXT_POLL;
                }
            case "3":
                return POST_TYPE_AUDIO;
            default:
                break;
        }
        return POST_TYPE_TEXT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == POST_TYPE_TEXT) {
            View v0 = layoutInflater.inflate(R.layout.groups_text_post_item, parent, false);
            return new TextPostViewHolder(v0);
        } else if (viewType == POST_TYPE_MEDIA) {
            View v0 = layoutInflater.inflate(R.layout.groups_media_post_item, parent, false);
            return new MediaPostViewHolder(v0);
        } else if (viewType == POST_TYPE_AUDIO) {
            View v0 = layoutInflater.inflate(R.layout.groups_post_audio_item, parent, false);
            return new AudioCommentViewHolder(v0);
        } else if (viewType == POST_TYPE_TEXT_POLL) {
            View v0 = layoutInflater.inflate(R.layout.groups_text_poll_post_item, parent, false);
            return new TextPollPostViewHolder(v0);
        } else {
            View v0 = layoutInflater.inflate(R.layout.groups_image_poll_post_item, parent, false);
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
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null
                            && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() > 1) {
                            textPostViewHolder.userTag
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            textPostViewHolder.userTag.setVisibility(View.VISIBLE);
                            textPostViewHolder.userTag1
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(1));
                            textPostViewHolder.userTag1.setVisibility(View.VISIBLE);
                        } else if (postList.get(position).getUserInfo().getUserTag().size() == 1) {
                            textPostViewHolder.userTag
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            textPostViewHolder.userTag.setVisibility(View.VISIBLE);
                            textPostViewHolder.userTag1.setVisibility(View.GONE);
                        } else {
                            textPostViewHolder.userTag.setVisibility(View.GONE);
                            textPostViewHolder.userTag1.setVisibility(View.GONE);
                        }
                    } else {
                        textPostViewHolder.userTag.setVisibility(View.GONE);
                        textPostViewHolder.userTag1.setVisibility(View.GONE);
                    }
                } else {
                    textPostViewHolder.userTag.setVisibility(View.GONE);
                    textPostViewHolder.userTag1.setVisibility(View.GONE);
                }
            }
            textPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
            Linkify.addLinks(textPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            textPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPostViewHolder.postDataTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(textPostViewHolder.postDataTextView);
            if (postList.get(position).getHelpfullCount() < 1) {
                textPostViewHolder.upvoteCountTextView.setText("");
            } else {
                textPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            }
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                textPostViewHolder.commentLayout.setVisibility(View.GONE);
                textPostViewHolder.postCommentsTextView
                        .setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                textPostViewHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                textPostViewHolder.commentLayout.setVisibility(View.VISIBLE);
                textPostViewHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                textPostViewHolder.postCommentsTextView
                        .setText(context.getResources().getString(R.string.group_add_comment_text));
            }
            textPostViewHolder.postDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            if (postList.get(position).getIsAnnon() == 1) {
                textPostViewHolder.usernameTextView.setText(context.getString(R.string.groups_anonymous));
                textPostViewHolder.userImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
            } else {
                textPostViewHolder.usernameTextView.setText(
                        postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo()
                                .getLastName());
                try {
                    Picasso.get().load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(textPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
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
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null
                            && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() > 1) {
                            audioCommentViewHolder.userTag
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            audioCommentViewHolder.userTag.setVisibility(View.VISIBLE);
                            audioCommentViewHolder.userTag1
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(1));
                            audioCommentViewHolder.userTag1.setVisibility(View.VISIBLE);
                        } else if (postList.get(position).getUserInfo().getUserTag().size() == 1) {
                            audioCommentViewHolder.userTag
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            audioCommentViewHolder.userTag.setVisibility(View.VISIBLE);
                            audioCommentViewHolder.userTag1.setVisibility(View.GONE);
                        } else {
                            audioCommentViewHolder.userTag.setVisibility(View.GONE);
                            audioCommentViewHolder.userTag1.setVisibility(View.GONE);
                        }
                    } else {
                        audioCommentViewHolder.userTag.setVisibility(View.GONE);
                        audioCommentViewHolder.userTag1.setVisibility(View.GONE);
                    }
                } else {
                    audioCommentViewHolder.userTag.setVisibility(View.GONE);
                    audioCommentViewHolder.userTag1.setVisibility(View.GONE);
                }
            }
            if (postList.get(position).getIsAnnon() == 1) {
                audioCommentViewHolder.commentorUsernameTextView.setText(context.getString(R.string.groups_anonymous));
                audioCommentViewHolder.commentorImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
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
                audioCommentViewHolder.commentorUsernameTextView
                        .setText(postList.get(position).getUserInfo().getFirstName()
                                + " " + postList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.get().load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                            .into(audioCommentViewHolder.commentorImageView);
                } catch (Exception e) {
                    audioCommentViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
                }
                try {
                    Picasso.get().load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                            .into(audioCommentViewHolder.profileImageView);
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
                    .setText(DateTimeUtils.getFormattedDateGroups(postList.get(position).getCreatedAt()));
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                audioCommentViewHolder.commentLayout.setVisibility(View.GONE);
                audioCommentViewHolder.postCommentsTextView
                        .setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                audioCommentViewHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                audioCommentViewHolder.commentLayout.setVisibility(View.VISIBLE);
                audioCommentViewHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                audioCommentViewHolder.postCommentsTextView
                        .setText(context.getResources().getString(R.string.group_add_comment_text));
            }
            if (postList.get(position).getMarkedHelpful() == 1) {
                audioCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                audioCommentViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);
            }
            if (postList.get(position).getHelpfullCount() < 1) {
                audioCommentViewHolder.upvoteCommentCountTextView.setText("");
            } else {
                audioCommentViewHolder.upvoteCommentCountTextView
                        .setText(postList.get(position).getHelpfullCount() + "");
            }
        } else if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder mediaPostViewHolder = (MediaPostViewHolder) holder;
            if (postList.get(position).getIsAnnon() == 1) {
                mediaPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postList != null && postList.size() != 0) {
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null
                            && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() > 1) {
                            mediaPostViewHolder.userTag
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            mediaPostViewHolder.userTag.setVisibility(View.VISIBLE);
                            mediaPostViewHolder.userTag1
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(1));
                            mediaPostViewHolder.userTag1.setVisibility(View.VISIBLE);
                        } else if (postList.get(position).getUserInfo().getUserTag().size() == 1) {
                            mediaPostViewHolder.userTag
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            mediaPostViewHolder.userTag.setVisibility(View.VISIBLE);
                            mediaPostViewHolder.userTag1.setVisibility(View.GONE);
                        } else {
                            mediaPostViewHolder.userTag.setVisibility(View.GONE);
                            mediaPostViewHolder.userTag1.setVisibility(View.GONE);
                        }
                    } else {
                        mediaPostViewHolder.userTag.setVisibility(View.GONE);
                        mediaPostViewHolder.userTag1.setVisibility(View.GONE);
                    }
                } else {
                    mediaPostViewHolder.userTag.setVisibility(View.GONE);
                    mediaPostViewHolder.userTag1.setVisibility(View.GONE);
                }
            }
            if (postList.get(position).getMarkedHelpful() == 1) {

                mediaPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                mediaPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);
            }
            mediaPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
            Linkify.addLinks(mediaPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            mediaPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mediaPostViewHolder.postDataTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(mediaPostViewHolder.postDataTextView);
            if (postList.get(position).getHelpfullCount() < 1) {
                mediaPostViewHolder.upvoteCountTextView.setText("");
            } else {
                mediaPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            }
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                mediaPostViewHolder.commentLayout.setVisibility(View.GONE);
                mediaPostViewHolder.postCommentsTextView
                        .setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                mediaPostViewHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                mediaPostViewHolder.commentLayout.setVisibility(View.VISIBLE);
                mediaPostViewHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                mediaPostViewHolder.postCommentsTextView
                        .setText(context.getResources().getString(R.string.group_add_comment_text));
            }
            mediaPostViewHolder.postDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            if (postList.get(position).getIsAnnon() == 1) {
                mediaPostViewHolder.usernameTextView.setText(context.getString(R.string.groups_anonymous));
                mediaPostViewHolder.userImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
            } else {
                mediaPostViewHolder.usernameTextView.setText(
                        postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo()
                                .getLastName());
                try {
                    Picasso.get().load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                            .into(mediaPostViewHolder.userImageView);
                } catch (Exception e) {
                    mediaPostViewHolder.userImageView
                            .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.default_commentor_img));
                }
            }
            initializeViews((MediaPostViewHolder) holder, position);
        } else if (holder instanceof TextPollPostViewHolder) {
            TextPollPostViewHolder textPollPostViewHolder = (TextPollPostViewHolder) holder;
            if (postList.get(position).getIsAnnon() == 1) {
                textPollPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postList != null && postList.size() != 0) {
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null
                            && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() > 1) {
                            textPollPostViewHolder.userTag
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            textPollPostViewHolder.userTag.setVisibility(View.VISIBLE);
                            textPollPostViewHolder.userTag1
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(1));
                            textPollPostViewHolder.userTag1.setVisibility(View.VISIBLE);
                        } else if (postList.get(position).getUserInfo().getUserTag().size() == 1) {
                            textPollPostViewHolder.userTag
                                    .setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            textPollPostViewHolder.userTag.setVisibility(View.VISIBLE);
                            textPollPostViewHolder.userTag1.setVisibility(View.GONE);
                        } else {
                            textPollPostViewHolder.userTag.setVisibility(View.GONE);
                            textPollPostViewHolder.userTag1.setVisibility(View.GONE);
                        }
                    } else {
                        textPollPostViewHolder.userTag.setVisibility(View.GONE);
                        textPollPostViewHolder.userTag1.setVisibility(View.GONE);
                    }
                } else {
                    textPollPostViewHolder.userTag.setVisibility(View.GONE);
                    textPollPostViewHolder.userTag1.setVisibility(View.GONE);
                }
            }
            if (postList.get(position).getMarkedHelpful() == 1) {

                textPollPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                textPollPostViewHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);
            }
            textPollPostViewHolder.pollQuestionTextView.setText(postList.get(position).getContent());
            Linkify.addLinks(textPollPostViewHolder.pollQuestionTextView, Linkify.WEB_URLS);
            textPollPostViewHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPollPostViewHolder.pollQuestionTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(textPollPostViewHolder.pollQuestionTextView);
            textPollPostViewHolder.postDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            if (postList.get(position).getHelpfullCount() < 1) {
                textPollPostViewHolder.upvoteCountTextView.setText("");
            } else {
                textPollPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            }
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                textPollPostViewHolder.commentLayout.setVisibility(View.GONE);
                textPollPostViewHolder.postCommentsTextView
                        .setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                textPollPostViewHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                textPollPostViewHolder.commentLayout.setVisibility(View.VISIBLE);
                textPollPostViewHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                textPollPostViewHolder.postCommentsTextView
                        .setText(context.getResources().getString(R.string.group_add_comment_text));
            }
            if (postList.get(position).getIsAnnon() == 1) {
                textPollPostViewHolder.usernameTextView.setText(context.getString(R.string.groups_anonymous));
                textPollPostViewHolder.userImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
            } else {
                textPollPostViewHolder.usernameTextView.setText(
                        postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo()
                                .getLastName());
                try {
                    Picasso.get().load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(textPollPostViewHolder.userImageView);
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
                    default:
                        break;
                }
            }
            textPollPostViewHolder.pollOption1ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption2ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption3ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption4ProgressBar.setProgress(0f);
            textPollPostViewHolder.totalVoteCountTextView.setText(
                    context.getString(R.string.groups_total_votes, postList.get(position).getTotalVotesCount()));
            if (postList.get(position).isVoted()) {
                showVotingData(textPollPostViewHolder, postList.get(position));
            } else {
                hideVotingData(textPollPostViewHolder);
            }
        } else {
            ImagePollPostViewHolder imageHolder = (ImagePollPostViewHolder) holder;
            if (postList.get(position).getIsAnnon() == 1) {
                imageHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postList != null && postList.size() != 0) {
                    if (postList.get(position) != null && postList.get(position).getUserInfo() != null
                            && postList.get(position).getUserInfo().getUserTag() != null) {
                        if (postList.get(position).getUserInfo().getUserTag().size() > 1) {
                            imageHolder.userTag.setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            imageHolder.userTag.setVisibility(View.VISIBLE);
                            imageHolder.userTag1.setText("" + postList.get(position).getUserInfo().getUserTag().get(1));
                            imageHolder.userTag1.setVisibility(View.VISIBLE);
                        } else if (postList.get(position).getUserInfo().getUserTag().size() == 1) {
                            imageHolder.userTag.setText("" + postList.get(position).getUserInfo().getUserTag().get(0));
                            imageHolder.userTag.setVisibility(View.VISIBLE);
                            imageHolder.userTag1.setVisibility(View.GONE);
                        } else {
                            imageHolder.userTag.setVisibility(View.GONE);
                            imageHolder.userTag1.setVisibility(View.GONE);
                        }
                    } else {
                        imageHolder.userTag.setVisibility(View.GONE);
                        imageHolder.userTag1.setVisibility(View.GONE);
                    }
                } else {
                    imageHolder.userTag.setVisibility(View.GONE);
                    imageHolder.userTag1.setVisibility(View.GONE);
                }
            }
            if (postList.get(position).getMarkedHelpful() == 1) {
                imageHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommended);
            } else {
                imageHolder.upvoteImageVIew.setImageResource(R.drawable.ic_recommend);
            }
            imageHolder.pollQuestionTextView.setText(postList.get(position).getContent());
            Linkify.addLinks(imageHolder.pollQuestionTextView, Linkify.WEB_URLS);
            imageHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            imageHolder.pollQuestionTextView
                    .setLinkTextColor(ContextCompat.getColor(context, R.color.groups_blue_color));
            addLinkHandler(imageHolder.pollQuestionTextView);

            imageHolder.postDateTextView
                    .setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            if (postList.get(position).getIsAnnon() == 1) {
                imageHolder.usernameTextView.setText(context.getString(R.string.groups_anonymous));
                imageHolder.userImageView
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incognito));
            } else {
                imageHolder.usernameTextView.setText(
                        postList.get(position).getUserInfo().getFirstName() + " " + postList.get(position).getUserInfo()
                                .getLastName());
                try {
                    Picasso.get().load(postList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(imageHolder.userImageView);
                } catch (Exception e) {
                    imageHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            if (postList.get(position).getHelpfullCount() < 1) {
                imageHolder.upvoteCountTextView.setText("");
            } else {
                imageHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + "");
            }
            if (postList != null && postList.size() != 0 && postList.get(position).getResponseCount() != 0) {
                imageHolder.commentLayout.setVisibility(View.GONE);
                imageHolder.postCommentsTextView
                        .setText(postList.get(position).getResponseCount() + " " + localizedComment);
            } else {
                imageHolder.beTheFirstOne.setVisibility(View.VISIBLE);
                imageHolder.commentLayout.setVisibility(View.VISIBLE);
                imageHolder.postCommentsTextView.setVisibility(View.VISIBLE);
                imageHolder.postCommentsTextView
                        .setText(context.getResources().getString(R.string.group_add_comment_text));
            }
            imageHolder.lastOptionsContainer.setVisibility(View.GONE);
            imageHolder.option3Container.setVisibility(View.GONE);
            imageHolder.option4Container.setVisibility(View.GONE);
            Map<String, String> imageMap = (Map<String, String>) postList.get(position).getPollOptions();
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
            imageHolder.totalVoteCountTextView.setText(
                    context.getString(R.string.groups_total_votes, postList.get(position).getTotalVotesCount()));
            if (postList.get(position).isVoted()) {
                showImagePollVotingData(imageHolder, postList.get(position));
            } else {
                hideImagePollVotingData(imageHolder);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
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
                    long totalDuration = mediaPlayer.getDuration();
                    currentDuration = mediaPlayer.getCurrentPosition();
                    playingHolder.audioTimeElapsed
                            .setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
                    return true;
                }
            }
            break;
            default:
                break;
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

    private void showImagePollVotingData(ImagePollPostViewHolder imageHolder, GroupPostResult postResult) {
        imageHolder.pollOption1ProgressBar
                .setProgress((100f * postResult.getOption1VoteCount()) / postResult.getTotalVotesCount());
        imageHolder.pollOption2ProgressBar
                .setProgress((100f * postResult.getOption2VoteCount()) / postResult.getTotalVotesCount());
        imageHolder.pollOption3ProgressBar
                .setProgress((100f * postResult.getOption3VoteCount()) / postResult.getTotalVotesCount());
        imageHolder.pollOption4ProgressBar
                .setProgress((100f * postResult.getOption4VoteCount()) / postResult.getTotalVotesCount());
        imageHolder.pollOption1TextView.setText(
                AppUtils.round((100f * postResult.getOption1VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption2TextView.setText(
                AppUtils.round((100f * postResult.getOption2VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption3TextView.setText(
                AppUtils.round((100f * postResult.getOption3VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption4TextView.setText(
                AppUtils.round((100f * postResult.getOption4VoteCount()) / postResult.getTotalVotesCount(), 2) + "%");
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
        if (viewPageStates.containsKey(position)) {
            holder.postDataViewPager.setCurrentItem(viewPageStates.get(position));
            holder.indexTextView.setText((viewPageStates.get(position) + 1) + "/" + mediaList.size());
        } else {
            holder.indexTextView.setText((holder.postDataViewPager.getCurrentItem() + 1) + "/" + mediaList.size());
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder viewHolder = (MediaPostViewHolder) holder;
            viewPageStates.put(holder.getAdapterPosition(), viewHolder.postDataViewPager.getCurrentItem());
            super.onViewRecycled(holder);
        }
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        ImageView whatsappShare;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView userTag;
        TextView userTag1;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        LinearLayout upvoteContainer;
        LinearLayout downvoteContainer;
        TextView postCommentsTextView;
        TextView typeHere;
        TextView beTheFirstOne;
        ImageView postSettingImageView;
        ImageView shareTextView;
        ImageView upvoteImageVIew;
        RelativeLayout commentLayout;

        TextPostViewHolder(View view) {
            super(view);
            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
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
            userTag1 = (TextView) view.findViewById(R.id.userTag1);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            commentLayout.setOnClickListener(v -> {
                launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                        postList.get(getAdapterPosition()).getId(),
                        postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
            });

            postCommentsTextView.setOnClickListener(v -> {
                if (postCommentsTextView.getText().toString()
                        .equals(context.getResources().getString(R.string.group_add_comment_text))) {
                    launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                            postList.get(getAdapterPosition()).getId(),
                            postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
                } else {
                    Intent intent = new Intent(context, GroupPostDetailActivity.class);
                    intent.putExtra("groupItem", selectedGroup);
                    intent.putExtra("postType", AppConstants.POST_TYPE_TEXT);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((GroupDetailsActivity) context).startActivityForResult(intent, 2222);
                }
            });
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class MediaPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        ImageView shareTextView;
        ImageView upvoteImageVIew;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        TextView userTag;
        TextView userTag1;
        LinearLayout upvoteContainer;
        LinearLayout downvoteContainer;
        TextView postCommentsTextView;
        TextView beTheFirstOne;
        ImageView postSettingImageView;
        ImageView whatsappShare;
        private IndefinitePagerIndicator dotIndicatorView;
        private GroupPostMediaViewPager postDataViewPager;
        private TextView indexTextView;
        private GroupMediaPostViewPagerAdapter viewPagerAdapter;
        RelativeLayout commentLayout;

        MediaPostViewHolder(View view) {
            super(view);
            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
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
            dotIndicatorView = (IndefinitePagerIndicator) view.findViewById(R.id.dotIndicatorView);
            postDataViewPager = (GroupPostMediaViewPager) view.findViewById(R.id.postDataViewPager);
            indexTextView = (TextView) view.findViewById(R.id.indexTextView);
            beTheFirstOne = (TextView) view.findViewById(R.id.beTheFirstOne);
            userTag = (TextView) view.findViewById(R.id.userTag);
            userTag1 = (TextView) view.findViewById(R.id.userTag1);

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
            whatsappShare.setOnClickListener(this);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            commentLayout.setOnClickListener(v -> {
                launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                        postList.get(getAdapterPosition()).getId(),
                        postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
            });

            postCommentsTextView.setOnClickListener(v -> {
                if (postCommentsTextView.getText().toString()
                        .equals(context.getResources().getString(R.string.group_add_comment_text))) {
                    launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                            postList.get(getAdapterPosition()).getId(),
                            postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
                } else {
                    Intent intent = new Intent(context, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_MEDIA);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList
                            .get(getAdapterPosition()).getMediaUrls();
                    intent.putExtra("mediaUrls", linkedTreeMap);
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((GroupDetailsActivity) context).startActivityForResult(intent, 2222);
                }
            });
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class AudioCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, SeekBar.OnSeekBarChangeListener {

        ImageView commentorImageView;
        ImageView playAudioImageView;
        ImageView pauseAudioImageView;
        ImageView whatsappShare;
        ImageView media;
        TextView commentorUsernameTextView;
        TextView audioTimeElapsed;
        TextView commentDataTextView;
        TextView commentDateTextView;
        View underlineView;
        SeekBar audioSeekBar;
        TextView upvoteCommentCountTextView;
        TextView downvoteCommentCountTextView;
        TextView userTag;
        TextView userTag1;
        LinearLayout upvoteCommentContainer;
        LinearLayout downvoteCommentContainer;
        RelativeLayout audiotRootView;
        TextView postCommentsTextView;
        TextView beTheFirstOne;
        ImageView postSettingImageView;
        ImageView upvoteImageVIew;
        ImageView shareTextView;
        ImageView profileImageView;
        RelativeLayout commentLayout;

        public AudioCommentViewHolder(View view) {
            super(view);
            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            media = (ImageView) view.findViewById(R.id.media);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
            audiotRootView = view.findViewById(R.id.commentRootView);
            profileImageView = view.findViewById(R.id.profileImageView);
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
            userTag1 = (TextView) view.findViewById(R.id.userTag1);
            profileImageView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);

            postCommentsTextView.setOnClickListener(v -> {
                if (postCommentsTextView.getText().toString()
                        .equals(context.getResources().getString(R.string.group_add_comment_text))) {
                    launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                            postList.get(getAdapterPosition()).getId(),
                            postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
                } else {
                    Intent intent = new Intent(context, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList
                            .get(getAdapterPosition()).getPollOptions();
                    intent.putExtra("pollOptions", linkedTreeMap);
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((GroupDetailsActivity) context).startActivityForResult(intent, 2222);
                }
            });
            commentLayout.setOnClickListener(v -> {
                launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                        postList.get(getAdapterPosition()).getId(),
                        postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
            });

            commentDataTextView.setOnLongClickListener(this);
            view.setOnLongClickListener(this);
            downvoteCommentContainer.setOnClickListener(this);
            upvoteCommentContainer.setOnClickListener(this);
            playAudioImageView.setOnClickListener(this);
            audioSeekBar.setOnSeekBarChangeListener(this);
            postSettingImageView.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
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
                recyclerViewClickListener.onGroupPostRecyclerItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            recyclerViewClickListener.onGroupPostRecyclerItemClick(view, getAdapterPosition());
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

        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        LinearLayout upvoteContainer;
        LinearLayout downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        ImageView upvoteImageVIew;
        TextView pollQuestionTextView;
        TextView beTheFirstOne;
        TextView userTag;
        TextView userTag1;
        ImageView shareTextView;
        ImageView whatsappShare;
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
        RelativeLayout option3Container;
        RelativeLayout option4Container;
        RelativeLayout commentLayout;

        TextPollPostViewHolder(View view) {
            super(view);
            commentLayout = (RelativeLayout) view.findViewById(R.id.commentLayout);
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
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
            userTag1 = (TextView) view.findViewById(R.id.userTag1);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);

            postCommentsTextView.setOnClickListener(v -> {
                if (postCommentsTextView.getText().toString()
                        .equals(context.getResources().getString(R.string.group_add_comment_text))) {
                    launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                            postList.get(getAdapterPosition()).getId(),
                            postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
                } else {
                    Intent intent = new Intent(context, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_TEXT_POLL);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList
                            .get(getAdapterPosition()).getPollOptions();
                    intent.putExtra("pollOptions", linkedTreeMap);
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((GroupDetailsActivity) context).startActivityForResult(intent, 2222);
                }
            });
            commentLayout.setOnClickListener(v -> {
                launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                        postList.get(getAdapterPosition()).getId(),
                        postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
            });
            pollOption1ProgressBar.setOnClickListener(v -> addVote(getAdapterPosition(), "option1"));
            pollOption2ProgressBar.setOnClickListener(v -> addVote(getAdapterPosition(), "option2"));
            pollOption3ProgressBar.setOnClickListener(v -> addVote(getAdapterPosition(), "option3"));
            pollOption4ProgressBar.setOnClickListener(v -> addVote(getAdapterPosition(), "option4"));
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class ImagePollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
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
        TextView userTag1;
        ImageView shareTextView;
        ImageView upvoteImageVIew;
        ImageView option1ImageView;
        ImageView option2ImageView;
        ImageView option3ImageView;
        ImageView option4ImageView;
        ImageView whatsappShare;
        RoundCornerProgressBar pollOption1ProgressBar;
        RoundCornerProgressBar pollOption2ProgressBar;
        RoundCornerProgressBar pollOption3ProgressBar;
        RoundCornerProgressBar pollOption4ProgressBar;
        TextView pollOption1TextView;
        TextView pollOption2TextView;
        TextView pollOption3TextView;
        TextView pollOption4TextView;
        TextView totalVoteCountTextView;
        TextView beTheFirstOne;
        LinearLayout lastOptionsContainer;
        RelativeLayout commentLayout;
        RelativeLayout option1Container;
        RelativeLayout option2Container;
        RelativeLayout option3Container;
        RelativeLayout option4Container;

        ImagePollPostViewHolder(View view) {
            super(view);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            upvoteImageVIew = (ImageView) view.findViewById(R.id.upvoteImageVIew);
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
            whatsappShare = (ImageView) view.findViewById(R.id.whatsappShare);
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
            userTag = (TextView) view.findViewById(R.id.userTag);
            userTag1 = (TextView) view.findViewById(R.id.userTag1);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsappShare.setOnClickListener(this);
            commentLayout.setOnClickListener(v -> {
                Utils.groupsEvent(context, "Groups_Discussion", "# comment ", "android",
                        SharedPrefUtils.getAppLocale(context),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Groups_Discussion", "",
                        String.valueOf(postList.get(getAdapterPosition()).getId()));

                launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                        postList.get(getAdapterPosition()).getId(),
                        postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
            });

            postCommentsTextView.setOnClickListener(v -> {
                if (postCommentsTextView.getText().toString()
                        .equals(context.getResources().getString(R.string.group_add_comment_text))) {
                    Utils.groupsEvent(context, "Groups_Discussion", "# comment ", "android",
                            SharedPrefUtils.getAppLocale(context),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "post page", "",
                            String.valueOf(postList.get(getAdapterPosition()).getId()));

                    launchAddCommentDialog(postList.get(getAdapterPosition()).getGroupId(),
                            postList.get(getAdapterPosition()).getId(),
                            postList.get(getAdapterPosition()).getGroupInfo().getAnnonAllowed());
                } else {
                    Intent intent = new Intent(context, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_IMAGE_POLL);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList
                            .get(getAdapterPosition()).getPollOptions();
                    intent.putExtra("pollOptions", linkedTreeMap);
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((GroupDetailsActivity) context).startActivityForResult(intent, 2222);
                }
            });
            option1Container.setOnClickListener(v -> addVote(getAdapterPosition(), "option1"));
            option2Container.setOnClickListener(v -> addVote(getAdapterPosition(), "option2"));
            option3Container.setOnClickListener(v -> addVote(getAdapterPosition(), "option3"));
            option4Container.setOnClickListener(v -> addVote(getAdapterPosition(), "option4"));
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }

    private void addVote(int position, String option) {
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(postList.get(position).getGroupId());
        groupActionsRequest.setPostId(postList.get(position).getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(context).getDynamoId());
        groupActionsRequest.setVoteOption(option);
        pollPosition = position;
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupsActionVoteResponse> call = groupsApi.addActionVote(groupActionsRequest);
        call.enqueue(groupActionResponseCallback);
    }

    private Callback<GroupsActionVoteResponse> groupActionResponseCallback = new Callback<GroupsActionVoteResponse>() {
        @Override
        public void onResponse(Call<GroupsActionVoteResponse> call, Response<GroupsActionVoteResponse> response) {
            if (response.body() == null) {
                if (response.code() == 400) {
                    try {
                        String errorBody = new String(response.errorBody().bytes());
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String reason = jsonObject.getString("reason");
                        if (!StringUtils.isNullOrEmpty(reason) && "already voted".equals(reason)) {

                            postList.get(pollPosition).setVoted(true);
                            postList.get(pollPosition).setTotalVotesCount(0);
                            for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                                postList.get(pollPosition)
                                        .setTotalVotesCount(postList.get(pollPosition).getTotalVotesCount()
                                                + Integer.parseInt(
                                                jsonObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                switch (jsonObject.getJSONArray("data").getJSONObject(i).getString("name")) {
                                    case "option1":
                                        postList.get(pollPosition).setOption1VoteCount(Integer.parseInt(
                                                jsonObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                        break;
                                    case "option2":
                                        postList.get(pollPosition).setOption2VoteCount(Integer.parseInt(
                                                jsonObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                        break;
                                    case "option3":
                                        postList.get(pollPosition).setOption3VoteCount(Integer.parseInt(
                                                jsonObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                        break;
                                    case "option4":
                                        postList.get(pollPosition).setOption4VoteCount(Integer.parseInt(
                                                jsonObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                        break;
                                    default:
                                        break;
                                }
                            }
                            notifyDataSetChanged();
                        }
                    } catch (IOException | JSONException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
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
                                default:
                                    break;
                            }
                            notifyDataSetChanged();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsActionVoteResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
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
            style.clearSpans();
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
        public void onClick(@NotNull View widget) {
            try {
                ((BaseActivity) context).handleDeeplinks(url);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }
    }

    private void startMediaPlayer(int position) {
        mediaPlayer = new MediaPlayer();
        Map<String, String> map = postList.get(position).getMediaUrls();
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

    private void updateNonPlayingView(AudioCommentViewHolder holder) {
        if (holder == playingHolder) {
            handler.removeMessages(MSG_UPDATE_SEEK_BAR);
            handler.removeMessages(MSG_UPDATE_TIME);
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
        }
    }

    private void launchAddCommentDialog(int groupId, int postId, int isAnonymousAllowed) {
        Bundle args = new Bundle();
        args.putInt("groupId", groupId);
        args.putInt("postId", postId);
        args.putInt("isAnonymousAllowed", isAnonymousAllowed);
        AddGpPostCommentReplyDialogFragment addGpPostCommentReplyDialogFragment
                = new AddGpPostCommentReplyDialogFragment();
        addGpPostCommentReplyDialogFragment.setArguments(args);
        addGpPostCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    public interface RecyclerViewClickListener {

        void onGroupPostRecyclerItemClick(View view, int position);
    }

}
