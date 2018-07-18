package com.mycity4kids.ui.adapter;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
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
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.BloggerProfileActivity;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.shuhart.bubblepagerindicator.BubblePageIndicator;
import com.squareup.picasso.Picasso;

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
    public static final int COMMENT_LEVEL_REPLY = 1;
    private final String localizedNotHelpful, localizedHelpful, localizedComment;

    public static final int COMMENT_LEVEL_REPLY_REPLY = 2;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupPostCommentResult> postCommentsList;
    private RecyclerViewClickListener mListener;
    private int selectedPosition;
    private String postType;
    private int currentPagerPos = 0;
    private GroupPostResult groupPostResult;

    public GroupPostDetailsAndCommentsRecyclerAdapter(Context pContext, RecyclerViewClickListener listener, String postType) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        this.postType = postType;
        localizedComment = mContext.getString(R.string.groups_post_comment);
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
        } else if (viewType == COMMENT_LEVEL_ROOT) {
            View v0 = mInflator.inflate(R.layout.group_post_comment_cell_test, parent, false);
            return new RootCommentViewHolder(v0);
        } else if (viewType == COMMENT_LEVEL_REPLY) {
            View v0 = mInflator.inflate(R.layout.group_post_comment_reply_cell, parent, false);
            return new CommentReplyViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.group_post_reply_reply_cell, parent, false);
            return new ReplyReplyViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TextPostViewHolder) {
            TextPostViewHolder textPostViewHolder = (TextPostViewHolder) holder;
            textPostViewHolder.postDataTextView.setText(groupPostResult.getContent());
            textPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            textPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + " " + localizedHelpful);
            textPostViewHolder.downvoteCountTextView.setText(groupPostResult.getNotHelpfullCount() + " " + localizedNotHelpful);
            textPostViewHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                textPostViewHolder.usernameTextView.setText("Anonymous");
                textPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_followers));
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
            mediaPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            mediaPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + " " + localizedHelpful);
            mediaPostViewHolder.downvoteCountTextView.setText(groupPostResult.getNotHelpfullCount() + " " + localizedNotHelpful);
            mediaPostViewHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                mediaPostViewHolder.usernameTextView.setText("Anonymous");
                mediaPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_followers));
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
            textPollPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            textPollPostViewHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + " " + localizedHelpful);
            textPollPostViewHolder.downvoteCountTextView.setText(groupPostResult.getNotHelpfullCount() + " " + localizedNotHelpful);
            textPollPostViewHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                textPollPostViewHolder.usernameTextView.setText("Anonymous");
                textPollPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_followers));
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
        } else if (holder instanceof ImagePollPostViewHolder) {
            ImagePollPostViewHolder imageHolder = (ImagePollPostViewHolder) holder;
            imageHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupPostResult.getCreatedAt()));
            imageHolder.usernameTextView.setText(groupPostResult.getUserId());
            imageHolder.pollQuestionTextView.setText(groupPostResult.getContent());
            imageHolder.upvoteCountTextView.setText(groupPostResult.getHelpfullCount() + " " + localizedHelpful);
            imageHolder.downvoteCountTextView.setText(groupPostResult.getNotHelpfullCount() + " " + localizedNotHelpful);
            imageHolder.postCommentsTextView.setText(groupPostResult.getResponseCount() + " " + localizedComment);
            if (groupPostResult.getIsAnnon() == 1) {
                imageHolder.usernameTextView.setText("Anonymous");
                imageHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_followers));
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
        } else if (holder instanceof RootCommentViewHolder) {
            RootCommentViewHolder rootCommentViewHolder = (RootCommentViewHolder) holder;
            rootCommentViewHolder.commentorUsernameTextView.setText(postCommentsList.get(position).getUserInfo().getFirstName()
                    + " " + postCommentsList.get(position).getUserInfo().getLastName());
            rootCommentViewHolder.commentDataTextView.setText(postCommentsList.get(position).getContent());
            rootCommentViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postCommentsList.get(position).getCreatedAt()));
            if (postCommentsList.get(position).getChildData() == null || postCommentsList.get(position).getChildData().isEmpty()) {
                rootCommentViewHolder.replyCountTextView.setVisibility(View.GONE);
            } else {
                rootCommentViewHolder.replyCountTextView.setVisibility(View.VISIBLE);
                rootCommentViewHolder.replyCountTextView.setText("View (" + postCommentsList.get(position).getChildCount() + ") replies");
            }
            try {
                Picasso.with(mContext).load(postCommentsList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                        .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).transform(new RoundedTransformation()).into(rootCommentViewHolder.commentorImageView);
            } catch (Exception e) {
                rootCommentViewHolder.commentorImageView.setBackgroundResource(R.drawable.default_commentor_img);
            }

        } else if (holder instanceof CommentReplyViewHolder) {

        } else {


        }
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
        ImageView postSettingImageView;

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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
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
        ImageView postSettingImageView;
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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);

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
        ImageView postSettingImageView;
        TextView pollQuestionTextView;
        RoundCornerProgressBar pollOption1ProgressBar, pollOption2ProgressBar, pollOption3ProgressBar, pollOption4ProgressBar;
        TextView pollOption1TextView, pollOption2TextView, pollOption3TextView, pollOption4TextView;
        TextView pollResult1TextView, pollResult2TextView, pollResult3TextView, pollResult4TextView;
        TextView pollOption1ProgressTextView, pollOption2ProgressTextView, pollOption3ProgressTextView, pollOption4ProgressTextView;
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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);

            pollOption1ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option1");

//                    pollOption1ProgressBar.setProgress(80.0f);
//                    pollOption2ProgressBar.setProgress(20.0f);
//                    pollOption1ProgressTextView.setText("80%");
//                    pollOption2ProgressTextView.setText("20%");
//                    pollOption1ProgressTextView.setVisibility(View.VISIBLE);
//                    pollOption2ProgressTextView.setVisibility(View.VISIBLE);
//                    pollResult1TextView.setVisibility(View.VISIBLE);
//                    pollResult2TextView.setVisibility(View.VISIBLE);
//                    pollOption1TextView.setVisibility(View.GONE);
//                    pollOption2TextView.setVisibility(View.GONE);
                }
            });

            pollOption2ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option2");
//                    pollOption1ProgressBar.setProgress(40.0f);
//                    pollOption2ProgressBar.setProgress(60.0f);
//                    pollOption1ProgressTextView.setText("40%");
//                    pollOption2ProgressTextView.setText("60%");
//                    pollOption1ProgressTextView.setVisibility(View.VISIBLE);
//                    pollOption2ProgressTextView.setVisibility(View.VISIBLE);
//                    pollResult1TextView.setVisibility(View.VISIBLE);
//                    pollResult2TextView.setVisibility(View.VISIBLE);
//                    pollOption1TextView.setVisibility(View.GONE);
//                    pollOption2TextView.setVisibility(View.GONE);
                }
            });

            pollOption3ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option3");
//                    pollOption1ProgressBar.setProgress(40.0f);
//                    pollOption2ProgressBar.setProgress(60.0f);
//                    pollOption1ProgressTextView.setText("40%");
//                    pollOption2ProgressTextView.setText("60%");
//                    pollOption1ProgressTextView.setVisibility(View.VISIBLE);
//                    pollOption2ProgressTextView.setVisibility(View.VISIBLE);
//                    pollResult1TextView.setVisibility(View.VISIBLE);
//                    pollResult2TextView.setVisibility(View.VISIBLE);
//                    pollOption1TextView.setVisibility(View.GONE);
//                    pollOption2TextView.setVisibility(View.GONE);
                }
            });

            pollOption4ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option4");
//                    pollOption1ProgressBar.setProgress(40.0f);
//                    pollOption2ProgressBar.setProgress(60.0f);
//                    pollOption1ProgressTextView.setText("40%");
//                    pollOption2ProgressTextView.setText("60%");
//                    pollOption1ProgressTextView.setVisibility(View.VISIBLE);
//                    pollOption2ProgressTextView.setVisibility(View.VISIBLE);
//                    pollResult1TextView.setVisibility(View.VISIBLE);
//                    pollResult2TextView.setVisibility(View.VISIBLE);
//                    pollOption1TextView.setVisibility(View.GONE);
//                    pollOption2TextView.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class ImagePollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView;
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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);

            upvoteCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markAsHelpfulOrUnhelpful(getAdapterPosition(), AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY);
                }
            });
            downvoteCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markAsHelpfulOrUnhelpful(getAdapterPosition(), AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY);
                }
            });
            option1Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option1");
//                    pollOption1ProgressBar.setProgress(60.0f);
//                    pollOption2ProgressBar.setProgress(17.0f);
//                    pollOption3ProgressBar.setProgress(13.0f);
//                    pollOption4ProgressBar.setProgress(10.0f);
//                    pollOption1TextView.setText("60%");
//                    pollOption2TextView.setText("17%");
//                    pollOption3TextView.setText("13%");
//                    pollOption4TextView.setText("10%");
//                    pollOption1ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption2ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption3ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption4ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption1TextView.setVisibility(View.VISIBLE);
//                    pollOption2TextView.setVisibility(View.VISIBLE);
//                    pollOption3TextView.setVisibility(View.VISIBLE);
//                    pollOption4TextView.setVisibility(View.VISIBLE);
                }
            });

            option2Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option2");
//                    pollOption1ProgressBar.setProgress(60.0f);
//                    pollOption2ProgressBar.setProgress(17.0f);
//                    pollOption3ProgressBar.setProgress(13.0f);
//                    pollOption4ProgressBar.setProgress(10.0f);
//                    pollOption1TextView.setText("60%");
//                    pollOption2TextView.setText("17%");
//                    pollOption3TextView.setText("13%");
//                    pollOption4TextView.setText("10%");
//                    pollOption1ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption2ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption3ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption4ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption1TextView.setVisibility(View.VISIBLE);
//                    pollOption2TextView.setVisibility(View.VISIBLE);
//                    pollOption3TextView.setVisibility(View.VISIBLE);
//                    pollOption4TextView.setVisibility(View.VISIBLE);
                }
            });

            option3Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option3");
//                    pollOption1ProgressBar.setProgress(60.0f);
//                    pollOption2ProgressBar.setProgress(17.0f);
//                    pollOption3ProgressBar.setProgress(13.0f);
//                    pollOption4ProgressBar.setProgress(10.0f);
//                    pollOption1TextView.setText("60%");
//                    pollOption2TextView.setText("17%");
//                    pollOption3TextView.setText("13%");
//                    pollOption4TextView.setText("10%");
//                    pollOption1ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption2ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption3ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption4ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption1TextView.setVisibility(View.VISIBLE);
//                    pollOption2TextView.setVisibility(View.VISIBLE);
//                    pollOption3TextView.setVisibility(View.VISIBLE);
//                    pollOption4TextView.setVisibility(View.VISIBLE);
                }
            });

            option4Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVote(getAdapterPosition(), "option4");
//                    pollOption1ProgressBar.setProgress(60.0f);
//                    pollOption2ProgressBar.setProgress(17.0f);
//                    pollOption3ProgressBar.setProgress(13.0f);
//                    pollOption4ProgressBar.setProgress(10.0f);
//                    pollOption1TextView.setText("60%");
//                    pollOption2TextView.setText("17%");
//                    pollOption3TextView.setText("13%");
//                    pollOption4TextView.setText("10%");
//                    pollOption1ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption2ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption3ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption4ProgressBar.setVisibility(View.VISIBLE);
//                    pollOption1TextView.setVisibility(View.VISIBLE);
//                    pollOption2TextView.setVisibility(View.VISIBLE);
//                    pollOption3TextView.setVisibility(View.VISIBLE);
//                    pollOption4TextView.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class RootCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView commentorImageView;
        TextView commentorUsernameTextView;
        TextView commentDataTextView;
        TextView replyCommentTextView;
        TextView commentDateTextView;
        TextView replyCountTextView;
        View underlineView;

        RootCommentViewHolder(View view) {
            super(view);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            replyCommentTextView = (TextView) view.findViewById(R.id.replyCommentTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            replyCountTextView = (TextView) view.findViewById(R.id.replyCountTextView);

            replyCommentTextView.setOnClickListener(this);
            replyCountTextView.setOnClickListener(this);

            underlineView = view.findViewById(R.id.underlineView);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class CommentReplyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View underlineView;

        CommentReplyViewHolder(View view) {
            super(view);
            underlineView = view.findViewById(R.id.underlineView);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class ReplyReplyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View underlineView;

        ReplyReplyViewHolder(View view) {
            super(view);
            underlineView = view.findViewById(R.id.underlineView);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    private void addVote(int position, String option) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(groupPostResult.getGroupId());
        groupActionsRequest.setPostId(groupPostResult.getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(mContext).getDynamoId());
        groupActionsRequest.setType(AppConstants.GROUP_ACTION_TYPE_VOTE_KEY);
        groupActionsRequest.setVoteOption(option);
        Call<GroupsActionResponse> call = groupsAPI.addAction(groupActionsRequest);
        call.enqueue(groupActionResponseCallback);
    }

    private Callback<GroupsActionResponse> groupActionResponseCallback = new Callback<GroupsActionResponse>() {
        @Override
        public void onResponse(Call<GroupsActionResponse> call, Response<GroupsActionResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionResponse groupsActionResponse = response.body();
//                    for (int i = 0; i < postList.size(); i++) {
//                        if (postList.get(i).getId() == groupsActionResponse.getData().getResult().getPostId()) {
                    groupPostResult.setVoted(true);
                    notifyDataSetChanged();
//                            break;
//                        }
//                    }
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsActionResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void markAsHelpfulOrUnhelpful(int position, String markType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(groupPostResult.getGroupId());
        groupActionsRequest.setPostId(groupPostResult.getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(mContext).getDynamoId());
        groupActionsRequest.setType(markType);//AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY
        Call<GroupsActionResponse> call = groupsAPI.addAction(groupActionsRequest);
        call.enqueue(groupActionResponseCallback);
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

}
