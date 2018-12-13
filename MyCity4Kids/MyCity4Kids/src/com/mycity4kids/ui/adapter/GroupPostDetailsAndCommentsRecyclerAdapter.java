package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupsActionVoteResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.shuhart.bubblepagerindicator.BubblePageIndicator;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupPostDetailsAndCommentsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = -1;
    public static final int COMMENT_LEVEL_ROOT = 0;
    private final String localizedNotHelpful, localizedHelpful, localizedComment;

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupPostCommentResult> postCommentsList;
    private RecyclerViewClickListener mListener;
    private String postType;
    private int currentPagerPos = 0;
    private GroupPostResult groupPostResult;

    public GroupPostDetailsAndCommentsRecyclerAdapter(Context pContext, RecyclerViewClickListener listener, String postType) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        this.postType = postType;
        localizedComment = mContext.getString(R.string.ad_comments_title);
        localizedHelpful = mContext.getString(R.string.groups_post_helpful);
        localizedNotHelpful = mContext.getString(R.string.groups_post_nothelpful);
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
        } else {
            return COMMENT_LEVEL_ROOT;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            if (AppConstants.POST_TYPE_TEXT.equals(postType)) {
                View v0 = mInflator.inflate(R.layout.groups_text_post_item, parent, false);
                return new TextPostViewHolder(v0);
            } else if (AppConstants.POST_TYPE_MEDIA.equals(postType)) {
                View v0 = mInflator.inflate(R.layout.groups_media_post_item, parent, false);
                return new MediaPostViewHolder(v0);
            } else if (AppConstants.POST_TYPE_TEXT_POLL.equals(postType)) {
                View v0 = mInflator.inflate(R.layout.groups_text_poll_post_item, parent, false);
                return new TextPollPostViewHolder(v0);
            } else {
                View v0 = mInflator.inflate(R.layout.groups_image_poll_post_item, parent, false);
                return new ImagePollPostViewHolder(v0);
            }
        } else {
            View v0 = mInflator.inflate(R.layout.group_post_comment_cell_test, parent, false);
            return new RootCommentViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TextPostViewHolder) {
            TextPostViewHolder textPostViewHolder = (TextPostViewHolder) holder;

            textPostViewHolder.postDataTextView.setText(groupPostResult.getContent());
            Linkify.addLinks(textPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            textPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPostViewHolder.postDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(textPostViewHolder.postDataTextView);

            textPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            textPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + " " + localizedHelpful);
            textPostViewHolder.downvoteCountTextView.setText(groupPostResult.getNotHelpfullCount() + " " + localizedNotHelpful);
            textPostViewHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                textPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                textPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                textPostViewHolder.usernameTextView.setText(groupPostResult.getUserInfo().getFirstName() + " " + groupPostResult.getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(textPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
        } else if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder mediaPostViewHolder = (MediaPostViewHolder) holder;

            mediaPostViewHolder.postDataTextView.setText(groupPostResult.getContent());
            Linkify.addLinks(mediaPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            mediaPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mediaPostViewHolder.postDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(mediaPostViewHolder.postDataTextView);

            mediaPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            mediaPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + " " + localizedHelpful);
            mediaPostViewHolder.downvoteCountTextView.setText(groupPostResult.getNotHelpfullCount() + " " + localizedNotHelpful);
            mediaPostViewHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                mediaPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                mediaPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                mediaPostViewHolder.usernameTextView.setText(groupPostResult.getUserInfo().getFirstName() + " " + groupPostResult.getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(mediaPostViewHolder.userImageView);
                } catch (Exception e) {
                    mediaPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            initializeViews((MediaPostViewHolder) holder, position);
        } else if (holder instanceof TextPollPostViewHolder) {
            TextPollPostViewHolder textPollPostViewHolder = (TextPollPostViewHolder) holder;

            textPollPostViewHolder.pollQuestionTextView.setText(groupPostResult.getContent());
            Linkify.addLinks(textPollPostViewHolder.pollQuestionTextView, Linkify.WEB_URLS);
            textPollPostViewHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPollPostViewHolder.pollQuestionTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(textPollPostViewHolder.pollQuestionTextView);

            textPollPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            textPollPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + " " + localizedHelpful);
            textPollPostViewHolder.downvoteCountTextView.setText(groupPostResult.getNotHelpfullCount() + " " + localizedNotHelpful);
            textPollPostViewHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                textPollPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                textPollPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                textPollPostViewHolder.usernameTextView.setText(groupPostResult.getUserInfo().getFirstName() + " " + groupPostResult.getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(textPollPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPollPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
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
                }
            }
            textPollPostViewHolder.pollOption1ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption2ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption3ProgressBar.setProgress(0f);
            textPollPostViewHolder.pollOption4ProgressBar.setProgress(0f);
            textPollPostViewHolder.totalVoteCountTextView.setText(mContext.getString(R.string.groups_total_votes, groupPostResult.getTotalVotesCount()));
            if (groupPostResult.isVoted()) {
                showVotingData(textPollPostViewHolder, groupPostResult);
            } else {
                hideVotingData(textPollPostViewHolder);
            }
        } else if (holder instanceof ImagePollPostViewHolder) {
            ImagePollPostViewHolder imageHolder = (ImagePollPostViewHolder) holder;

            imageHolder.pollQuestionTextView.setText(groupPostResult.getContent());
            Linkify.addLinks(imageHolder.pollQuestionTextView, Linkify.WEB_URLS);
            imageHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            imageHolder.pollQuestionTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(imageHolder.pollQuestionTextView);

            imageHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            imageHolder.usernameTextView.setText(groupPostResult.getUserId());
            imageHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + " " + localizedHelpful);
            imageHolder.downvoteCountTextView.setText(groupPostResult.getNotHelpfullCount() + " " + localizedNotHelpful);
            imageHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                imageHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                imageHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                imageHolder.usernameTextView.setText(groupPostResult.getUserInfo().getFirstName() + " " + groupPostResult.getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(groupPostResult.getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.userImageView);
                } catch (Exception e) {
                    imageHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            imageHolder.lastOptionsContainer.setVisibility(View.GONE);
            imageHolder.option3Container.setVisibility(View.GONE);
            imageHolder.option4Container.setVisibility(View.GONE);
            Map<String, String> imageMap = (Map<String, String>) groupPostResult.getPollOptions();
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
            imageHolder.totalVoteCountTextView.setText(mContext.getString(R.string.groups_total_votes, groupPostResult.getTotalVotesCount()));
            if (groupPostResult.isVoted()) {
                showImagePollVotingData(imageHolder);
            } else {
                hideImagePollVotingData(imageHolder);
            }
        } else {
            RootCommentViewHolder rootCommentViewHolder = (RootCommentViewHolder) holder;
            if (postCommentsList.get(position).getIsAnnon() == 1) {
                rootCommentViewHolder.commentorUsernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                rootCommentViewHolder.commentorImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
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
                    rootCommentViewHolder.replyCountTextViewmedia.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.replyCommentTextViewmedia.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.commentdatetextviewmedia.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(rootCommentViewHolder.media);
                } else {
                    rootCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.replyCommentTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.media.setVisibility(View.GONE);
                    rootCommentViewHolder.replyCountTextViewmedia.setVisibility(View.GONE);
                    rootCommentViewHolder.replyCommentTextViewmedia.setVisibility(View.GONE);
                    rootCommentViewHolder.commentdatetextviewmedia.setVisibility(View.GONE);
                }


            } else {
                rootCommentViewHolder.commentorUsernameTextView.setText(postCommentsList.get(position).getUserInfo().getFirstName()
                        + " " + postCommentsList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.with(mContext).load(postCommentsList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(rootCommentViewHolder.commentorImageView);
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
                    rootCommentViewHolder.replyCountTextViewmedia.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.replyCommentTextViewmedia.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.commentdatetextviewmedia.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mediaList.get(0)).error(R.drawable.default_article).into(rootCommentViewHolder.media);
                } else {
                    rootCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.replyCommentTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.commentDateTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.media.setVisibility(View.GONE);
                    rootCommentViewHolder.replyCountTextViewmedia.setVisibility(View.GONE);
                    rootCommentViewHolder.replyCommentTextViewmedia.setVisibility(View.GONE);
                    rootCommentViewHolder.commentdatetextviewmedia.setVisibility(View.GONE);
                }
            }
//            AppUtils.fromHtml(postCommentsList.get(position).getContent().replace(" ","&nbsp;").replace("\n","<br />"))
            rootCommentViewHolder.commentDataTextView.setText(postCommentsList.get(position).getContent());
            Linkify.addLinks(rootCommentViewHolder.commentDataTextView, Linkify.WEB_URLS);
            rootCommentViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            rootCommentViewHolder.commentDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(rootCommentViewHolder.commentDataTextView);

            rootCommentViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postCommentsList.get(position).getCreatedAt()));
            rootCommentViewHolder.commentdatetextviewmedia.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postCommentsList.get(position).getCreatedAt()));
            if (rootCommentViewHolder.media.getVisibility() == View.GONE) {
                if (postCommentsList.get(position).getChildData() == null || postCommentsList.get(position).getChildData().isEmpty()) {
                    rootCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
                } else {
                    rootCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.replyCountTextView.setText("View (" + postCommentsList.get(position).getChildCount() + ") replies");
                }
            } else {
                if (postCommentsList.get(position).getChildData() == null || postCommentsList.get(position).getChildData().isEmpty()) {
                    rootCommentViewHolder.replyCountTextViewmedia.setVisibility(View.GONE);
                } else {
                    rootCommentViewHolder.replyCountTextViewmedia.setVisibility(View.VISIBLE);
                    rootCommentViewHolder.replyCountTextViewmedia.setText("View (" + postCommentsList.get(position).getChildCount() + ") replies");
                }
            }
        }
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
        imageHolder.pollOption1ProgressBar.setProgress((100f * groupPostResult.getOption1VoteCount()) / groupPostResult.getTotalVotesCount());
        imageHolder.pollOption2ProgressBar.setProgress((100f * groupPostResult.getOption2VoteCount()) / groupPostResult.getTotalVotesCount());
        imageHolder.pollOption3ProgressBar.setProgress((100f * groupPostResult.getOption3VoteCount()) / groupPostResult.getTotalVotesCount());
        imageHolder.pollOption4ProgressBar.setProgress((100f * groupPostResult.getOption4VoteCount()) / groupPostResult.getTotalVotesCount());
        imageHolder.pollOption1TextView.setText(AppUtils.round((100f * groupPostResult.getOption1VoteCount()) / groupPostResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption2TextView.setText(AppUtils.round((100f * groupPostResult.getOption2VoteCount()) / groupPostResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption3TextView.setText(AppUtils.round((100f * groupPostResult.getOption3VoteCount()) / groupPostResult.getTotalVotesCount(), 2) + "%");
        imageHolder.pollOption4TextView.setText(AppUtils.round((100f * groupPostResult.getOption4VoteCount()) / groupPostResult.getTotalVotesCount(), 2) + "%");
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
        holder.postDataViewPager.setCurrentItem(currentPagerPos);
        holder.indexTextView.setText((holder.postDataViewPager.getCurrentItem() + 1) + "/" + mediaList.size());
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView, shareTextView;

        TextPostViewHolder(View view) {
            super(view);
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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class MediaPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView, shareTextView;
        private BubblePageIndicator dotIndicatorView;
        private GroupPostMediaViewPager postDataViewPager;
        private TextView indexTextView;
        private GroupMediaPostViewPagerAdapter mViewPagerAdapter;

        MediaPostViewHolder(View view) {
            super(view);
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
            dotIndicatorView = (BubblePageIndicator) view.findViewById(R.id.dotIndicatorView);
            postDataViewPager = (GroupPostMediaViewPager) view.findViewById(R.id.postDataViewPager);
            indexTextView = (TextView) view.findViewById(R.id.indexTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);

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

        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class TextPollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView, shareTextView;
        TextView pollQuestionTextView;
        RoundCornerProgressBar pollOption1ProgressBar, pollOption2ProgressBar, pollOption3ProgressBar, pollOption4ProgressBar;
        TextView pollOption1TextView, pollOption2TextView, pollOption3TextView, pollOption4TextView;
        TextView pollResult1TextView, pollResult2TextView, pollResult3TextView, pollResult4TextView;
        TextView pollOption1ProgressTextView, pollOption2ProgressTextView, pollOption3ProgressTextView, pollOption4ProgressTextView;
        TextView totalVoteCountTextView;
        RelativeLayout option3Container, option4Container;

        TextPollPostViewHolder(View view) {
            super(view);
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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);

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
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class ImagePollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView, shareTextView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        TextView pollQuestionTextView;
        ImageView option1ImageView, option2ImageView, option3ImageView, option4ImageView;
        RoundCornerProgressBar pollOption1ProgressBar, pollOption2ProgressBar, pollOption3ProgressBar, pollOption4ProgressBar;
        TextView pollOption1TextView, pollOption2TextView, pollOption3TextView, pollOption4TextView;
        TextView totalVoteCountTextView;
        RelativeLayout option1Container, option2Container, option3Container, option4Container;
        LinearLayout lastOptionsContainer;

        ImagePollPostViewHolder(View view) {
            super(view);
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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);

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
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class RootCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView commentorImageView;
        ImageView media;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView replyCommentTextViewmedia;
        TextView commentDateTextView;
        TextView replyCountTextView;
        TextView replyCountTextViewmedia;
        TextView commentdatetextviewmedia;
        View underlineView;

        RootCommentViewHolder(View view) {
            super(view);
            replyCountTextViewmedia = (TextView) view.findViewById(R.id.replyCountTextViewmedia);
            media = (ImageView) view.findViewById(R.id.media);
            replyCommentTextViewmedia = (TextView) view.findViewById(R.id.replyCommentTextViewmedia);
            commentdatetextviewmedia = (TextView) view.findViewById(R.id.commentDateTextViewmedia);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);

            commentDataTextView.setOnLongClickListener(this);
            view.setOnLongClickListener(this);
            replyCommentTextView.setOnClickListener(this);
            replyCountTextView.setOnClickListener(this);
            replyCountTextViewmedia.setOnClickListener(this);
            replyCommentTextViewmedia.setOnClickListener(this);

            underlineView = view.findViewById(R.id.underlineView);
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

    private void addVote(int position, String option) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(groupPostResult.getGroupId());
        groupActionsRequest.setPostId(groupPostResult.getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(mContext).getDynamoId());
        groupActionsRequest.setVoteOption(option);
        Call<GroupsActionVoteResponse> call = groupsAPI.addActionVote(groupActionsRequest);
        call.enqueue(groupActionVoteResponseCallback);
    }

    private Callback<GroupsActionVoteResponse> groupActionVoteResponseCallback = new Callback<GroupsActionVoteResponse>() {
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
                                groupPostResult.setVoted(true);
                                groupPostResult.setTotalVotesCount(0);
                                for (int i = 0; i < jObject.getJSONArray("data").length(); i++) {
                                    groupPostResult.setTotalVotesCount(groupPostResult.getTotalVotesCount()
                                            + Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                    switch (jObject.getJSONArray("data").getJSONObject(i).getString("name")) {
                                        case "option1":
                                            groupPostResult.setOption1VoteCount(Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                            break;
                                        case "option2":
                                            groupPostResult.setOption2VoteCount(Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                            break;
                                        case "option3":
                                            groupPostResult.setOption3VoteCount(Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
                                            break;
                                        case "option4":
                                            groupPostResult.setOption4VoteCount(Integer.parseInt(jObject.getJSONArray("data").getJSONObject(i).getString("count")));
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
                    }
                    notifyDataSetChanged();
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
}
