package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.GroupReportedContentResult;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.mycity4kids.widget.IndefinitePagerIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupsReportedContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int POST_TYPE_TEXT = 0;
    public static final int POST_TYPE_MEDIA = 1;
    public static final int POST_TYPE_TEXT_POLL = 2;
    public static final int POST_TYPE_IMAGE_POLL = 3;

    //    public static final int HEADER = -1;
    public static final int COMMENT_LEVEL_ROOT = 4;

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupReportedContentResult> postCommentsList;
    private RecyclerViewClickListener mListener;
    private int currentPagerPos = 0;

    public GroupsReportedContentRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setData(ArrayList<GroupReportedContentResult> postCommentsList) {
        this.postCommentsList = postCommentsList;
    }

    @Override
    public int getItemCount() {
        return postCommentsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (postCommentsList.get(position).getResponseId() == 0) {
            if (postCommentsList.get(position).getContent().getType().equals("0")) {
                return POST_TYPE_TEXT;
            } else if (postCommentsList.get(position).getContent().getType().equals("1")) {
                return POST_TYPE_MEDIA;
            } else {
                if (postCommentsList.get(position).getContent().getPollType().equals("1")) {
                    return POST_TYPE_IMAGE_POLL;
                } else {
                    return POST_TYPE_TEXT_POLL;
                }
            }
        } else {
            return COMMENT_LEVEL_ROOT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (POST_TYPE_TEXT == viewType) {
            View v0 = mInflator.inflate(R.layout.report_text_post_item, parent, false);
            return new TextPostViewHolder(v0);
        } else if (POST_TYPE_MEDIA == viewType) {
            View v0 = mInflator.inflate(R.layout.report_media_post_item, parent, false);
            return new MediaPostViewHolder(v0);
        } else if (POST_TYPE_TEXT_POLL == viewType) {
            View v0 = mInflator.inflate(R.layout.report_text_poll_post_item, parent, false);
            return new TextPollPostViewHolder(v0);
        } else if (POST_TYPE_IMAGE_POLL == viewType) {
            View v0 = mInflator.inflate(R.layout.report_image_poll_post_item, parent, false);
            return new ImagePollPostViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.report_post_comment_cell, parent, false);
            return new RootCommentViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TextPostViewHolder) {
            TextPostViewHolder textPostViewHolder = (TextPostViewHolder) holder;
            textPostViewHolder.postDataTextView.setText(postCommentsList.get(position).getContent().getContent());
            textPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postCommentsList.get(position).getContent().getCreatedAt()));
            if (postCommentsList.get(position).getItsASpamCount() > 0) {
                textPostViewHolder.reportedSpamTextView.setVisibility(View.VISIBLE);
                textPostViewHolder.reportedSpamTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_spam) + " - " + postCommentsList.get(position).getItsASpamCount());
            } else {
                textPostViewHolder.reportedSpamTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getAbusiveContentCount() > 0) {
                textPostViewHolder.reportedAbuseTextView.setVisibility(View.VISIBLE);
                textPostViewHolder.reportedAbuseTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_abuse) + " - " + postCommentsList.get(position).getAbusiveContentCount());
            } else {
                textPostViewHolder.reportedAbuseTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getNotInterestingCount() > 0) {
                textPostViewHolder.reportedUninterestingTextView.setVisibility(View.VISIBLE);
                textPostViewHolder.reportedUninterestingTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_uninteresting) + " - " + postCommentsList.get(position).getNotInterestingCount());
            } else {
                textPostViewHolder.reportedUninterestingTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getItHurtsReligiousSentimentCount() > 0) {
                textPostViewHolder.reportedReligiousTextView.setVisibility(View.VISIBLE);
                textPostViewHolder.reportedReligiousTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_religious) + " - " + postCommentsList.get(position).getItHurtsReligiousSentimentCount());
            } else {
                textPostViewHolder.reportedReligiousTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getOtherCount() > 0) {
                textPostViewHolder.reportedOtherTextView.setVisibility(View.VISIBLE);
                textPostViewHolder.reportedOtherTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_other) + " - " + postCommentsList.get(position).getOtherCount());
            } else {
                textPostViewHolder.reportedOtherTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getContent().getIsAnnon() == 1) {
                textPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                textPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                textPostViewHolder.usernameTextView.setText(postCommentsList.get(position).getContent().getUserInfo().getFirstName() + " " + postCommentsList.get(position).getContent().getUserInfo().getLastName());
                try {
                    Picasso.get().load(postCommentsList.get(position).getContent().getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(textPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
        } else if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder mediaPostViewHolder = (MediaPostViewHolder) holder;
            mediaPostViewHolder.postDataTextView.setText(postCommentsList.get(position).getContent().getContent());
            mediaPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postCommentsList.get(position).getContent().getCreatedAt()));
            if (postCommentsList.get(position).getItsASpamCount() > 0) {
                mediaPostViewHolder.reportedSpamTextView.setVisibility(View.VISIBLE);
                mediaPostViewHolder.reportedSpamTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_spam) + " - " + postCommentsList.get(position).getItsASpamCount());
            } else {
                mediaPostViewHolder.reportedSpamTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getAbusiveContentCount() > 0) {
                mediaPostViewHolder.reportedAbuseTextView.setVisibility(View.VISIBLE);
                mediaPostViewHolder.reportedAbuseTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_abuse) + " - " + +postCommentsList.get(position).getAbusiveContentCount());
            } else {
                mediaPostViewHolder.reportedAbuseTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getNotInterestingCount() > 0) {
                mediaPostViewHolder.reportedUninterestingTextView.setVisibility(View.VISIBLE);
                mediaPostViewHolder.reportedUninterestingTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_uninteresting) + " - " + +postCommentsList.get(position).getNotInterestingCount());
            } else {
                mediaPostViewHolder.reportedUninterestingTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getItHurtsReligiousSentimentCount() > 0) {
                mediaPostViewHolder.reportedReligiousTextView.setVisibility(View.VISIBLE);
                mediaPostViewHolder.reportedReligiousTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_religious) + " - " + postCommentsList.get(position).getItHurtsReligiousSentimentCount());
            } else {
                mediaPostViewHolder.reportedReligiousTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getOtherCount() > 0) {
                mediaPostViewHolder.reportedOtherTextView.setVisibility(View.VISIBLE);
                mediaPostViewHolder.reportedOtherTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_other) + " - " + postCommentsList.get(position).getOtherCount());
            } else {
                mediaPostViewHolder.reportedOtherTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getContent().getIsAnnon() == 1) {
                mediaPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                mediaPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                mediaPostViewHolder.usernameTextView.setText(postCommentsList.get(position).getContent().getUserInfo().getFirstName() + " " + postCommentsList.get(position).getContent().getUserInfo().getLastName());
                try {
                    Picasso.get().load(postCommentsList.get(position).getContent().getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(mediaPostViewHolder.userImageView);
                } catch (Exception e) {
                    mediaPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            initializeViews((MediaPostViewHolder) holder, position);
        } else if (holder instanceof TextPollPostViewHolder) {
            TextPollPostViewHolder textPollPostViewHolder = (TextPollPostViewHolder) holder;
            textPollPostViewHolder.pollQuestionTextView.setText(postCommentsList.get(position).getContent().getContent());
            textPollPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postCommentsList.get(position).getContent().getCreatedAt()));
            if (postCommentsList.get(position).getItsASpamCount() > 0) {
                textPollPostViewHolder.reportedSpamTextView.setVisibility(View.VISIBLE);
                textPollPostViewHolder.reportedSpamTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_spam) + " - " + postCommentsList.get(position).getItsASpamCount());
            } else {
                textPollPostViewHolder.reportedSpamTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getAbusiveContentCount() > 0) {
                textPollPostViewHolder.reportedAbuseTextView.setVisibility(View.VISIBLE);
                textPollPostViewHolder.reportedAbuseTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_abuse) + " - " + postCommentsList.get(position).getAbusiveContentCount());
            } else {
                textPollPostViewHolder.reportedAbuseTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getNotInterestingCount() > 0) {
                textPollPostViewHolder.reportedUninterestingTextView.setVisibility(View.VISIBLE);
                textPollPostViewHolder.reportedUninterestingTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_uninteresting) + " - " + postCommentsList.get(position).getNotInterestingCount());
            } else {
                textPollPostViewHolder.reportedUninterestingTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getItHurtsReligiousSentimentCount() > 0) {
                textPollPostViewHolder.reportedReligiousTextView.setVisibility(View.VISIBLE);
                textPollPostViewHolder.reportedReligiousTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_religious) + " - " + postCommentsList.get(position).getItHurtsReligiousSentimentCount());
            } else {
                textPollPostViewHolder.reportedReligiousTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getOtherCount() > 0) {
                textPollPostViewHolder.reportedOtherTextView.setVisibility(View.VISIBLE);
                textPollPostViewHolder.reportedOtherTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_other) + " - " + postCommentsList.get(position).getOtherCount());
            } else {
                textPollPostViewHolder.reportedOtherTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getContent().getIsAnnon() == 1) {
                textPollPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                textPollPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                textPollPostViewHolder.usernameTextView.setText(postCommentsList.get(position).getContent().getUserInfo().getFirstName() + " " + postCommentsList.get(position).getContent().getUserInfo().getLastName());
                try {
                    Picasso.get().load(postCommentsList.get(position).getContent().getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(textPollPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPollPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            textPollPostViewHolder.option3Container.setVisibility(View.GONE);
            textPollPostViewHolder.option4Container.setVisibility(View.GONE);
            Map<String, String> optionsMap = (Map<String, String>) postCommentsList.get(position).getContent().getPollOptions();
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
            imageHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postCommentsList.get(position).getContent().getCreatedAt()));
            imageHolder.usernameTextView.setText(postCommentsList.get(position).getContent().getUserId());
            imageHolder.pollQuestionTextView.setText(postCommentsList.get(position).getContent().getContent());
            if (postCommentsList.get(position).getItsASpamCount() > 0) {
                imageHolder.reportedSpamTextView.setVisibility(View.VISIBLE);
                imageHolder.reportedSpamTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_spam) + " - " + postCommentsList.get(position).getItsASpamCount());
            } else {
                imageHolder.reportedSpamTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getAbusiveContentCount() > 0) {
                imageHolder.reportedAbuseTextView.setVisibility(View.VISIBLE);
                imageHolder.reportedAbuseTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_abuse) + " - " + postCommentsList.get(position).getAbusiveContentCount());
            } else {
                imageHolder.reportedAbuseTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getNotInterestingCount() > 0) {
                imageHolder.reportedUninterestingTextView.setVisibility(View.VISIBLE);
                imageHolder.reportedUninterestingTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_uninteresting) + " - " + postCommentsList.get(position).getNotInterestingCount());
            } else {
                imageHolder.reportedUninterestingTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getItHurtsReligiousSentimentCount() > 0) {
                imageHolder.reportedReligiousTextView.setVisibility(View.VISIBLE);
                imageHolder.reportedReligiousTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_religious) + " - " + postCommentsList.get(position).getItHurtsReligiousSentimentCount());
            } else {
                imageHolder.reportedReligiousTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getOtherCount() > 0) {
                imageHolder.reportedOtherTextView.setVisibility(View.VISIBLE);
                imageHolder.reportedOtherTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_other) + " - " + postCommentsList.get(position).getOtherCount());
            } else {
                imageHolder.reportedOtherTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getContent().getIsAnnon() == 1) {
                imageHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                imageHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                imageHolder.usernameTextView.setText(postCommentsList.get(position).getContent().getUserInfo().getFirstName() + " " + postCommentsList.get(position).getContent().getUserInfo().getLastName());
                try {
                    Picasso.get().load(postCommentsList.get(position).getContent().getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.userImageView);
                } catch (Exception e) {
                    imageHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            imageHolder.lastOptionsContainer.setVisibility(View.GONE);
            imageHolder.option3Container.setVisibility(View.GONE);
            imageHolder.option4Container.setVisibility(View.GONE);
            Map<String, String> imageMap = (Map<String, String>) postCommentsList.get(position).getContent().getPollOptions();
            for (Map.Entry<String, String> entry : imageMap.entrySet()) {
                switch (entry.getKey()) {
                    case "option1":
                        Picasso.get().load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option1ImageView);
                        break;
                    case "option2":
                        Picasso.get().load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option2ImageView);
                        break;
                    case "option3":
                        imageHolder.lastOptionsContainer.setVisibility(View.VISIBLE);
                        imageHolder.option3Container.setVisibility(View.VISIBLE);
                        Picasso.get().load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option3ImageView);
                        break;
                    case "option4":
                        imageHolder.lastOptionsContainer.setVisibility(View.VISIBLE);
                        imageHolder.option4Container.setVisibility(View.VISIBLE);
                        Picasso.get().load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option4ImageView);
                        break;
                }
            }
        } else if (holder instanceof RootCommentViewHolder) {
            RootCommentViewHolder rootCommentViewHolder = (RootCommentViewHolder) holder;
            rootCommentViewHolder.commentorUsernameTextView.setText(postCommentsList.get(position).getContent().getUserInfo().getFirstName()
                    + " " + postCommentsList.get(position).getContent().getUserInfo().getLastName());
            rootCommentViewHolder.commentDataTextView.setText(postCommentsList.get(position).getContent().getContent());
            rootCommentViewHolder.commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postCommentsList.get(position).getCreatedAt()));
            if (postCommentsList.get(position).getItsASpamCount() > 0) {
                rootCommentViewHolder.reportedSpamTextView.setVisibility(View.VISIBLE);
                rootCommentViewHolder.reportedSpamTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_spam) + " - " + postCommentsList.get(position).getItsASpamCount());
            } else {
                rootCommentViewHolder.reportedSpamTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getAbusiveContentCount() > 0) {
                rootCommentViewHolder.reportedAbuseTextView.setVisibility(View.VISIBLE);
                rootCommentViewHolder.reportedAbuseTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_abuse) + " - " + postCommentsList.get(position).getAbusiveContentCount());
            } else {
                rootCommentViewHolder.reportedAbuseTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getNotInterestingCount() > 0) {
                rootCommentViewHolder.reportedUninterestingTextView.setVisibility(View.VISIBLE);
                rootCommentViewHolder.reportedUninterestingTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_uninteresting) + " - " + postCommentsList.get(position).getNotInterestingCount());
            } else {
                rootCommentViewHolder.reportedUninterestingTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getItHurtsReligiousSentimentCount() > 0) {
                rootCommentViewHolder.reportedReligiousTextView.setVisibility(View.VISIBLE);
                rootCommentViewHolder.reportedReligiousTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_religious) + " - " + postCommentsList.get(position).getItHurtsReligiousSentimentCount());
            } else {
                rootCommentViewHolder.reportedReligiousTextView.setVisibility(View.GONE);
            }
            if (postCommentsList.get(position).getOtherCount() > 0) {
                rootCommentViewHolder.reportedOtherTextView.setVisibility(View.VISIBLE);
                rootCommentViewHolder.reportedOtherTextView.setText(BaseApplication.getAppContext().getString(R.string.reported_other) + " - " + postCommentsList.get(position).getOtherCount());
            } else {
                rootCommentViewHolder.reportedOtherTextView.setVisibility(View.GONE);
            }
            try {
                Picasso.get().load(postCommentsList.get(position).getContent().getUserInfo().getProfilePicUrl().getClientApp())
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
        Map<String, String> map = (Map<String, String>) postCommentsList.get(position).getContent().getMediaUrls();
        for (String entry : map.values()) {
            mediaList.add(entry);
        }
        holder.mViewPagerAdapter.setDataList(mediaList);
        holder.postDataViewPager.setAdapter(holder.mViewPagerAdapter);
        holder.dotIndicatorView.attachToViewPager(holder.postDataViewPager);
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
        TextView reportedSpamTextView, reportedAbuseTextView, reportedUninterestingTextView, reportedReligiousTextView, reportedOtherTextView;
        ImageView postSettingImageView;

        TextPostViewHolder(View view) {
            super(view);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            reportedSpamTextView = (TextView) view.findViewById(R.id.reportedSpamTextView);
            reportedAbuseTextView = (TextView) view.findViewById(R.id.reportedAbuseTextView);
            reportedUninterestingTextView = (TextView) view.findViewById(R.id.reportedUninterestingTextView);
            reportedReligiousTextView = (TextView) view.findViewById(R.id.reportedReligiousTextView);
            reportedOtherTextView = (TextView) view.findViewById(R.id.reportedOtherTextView);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
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
        TextView reportedSpamTextView, reportedAbuseTextView, reportedUninterestingTextView, reportedReligiousTextView, reportedOtherTextView;
        ImageView postSettingImageView;
        private IndefinitePagerIndicator dotIndicatorView;
        private GroupPostMediaViewPager postDataViewPager;
        private TextView indexTextView;
        private GroupMediaPostViewPagerAdapter mViewPagerAdapter;

        MediaPostViewHolder(View view) {
            super(view);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            dotIndicatorView = (IndefinitePagerIndicator) view.findViewById(R.id.dotIndicatorView);
            postDataViewPager = (GroupPostMediaViewPager) view.findViewById(R.id.postDataViewPager);
            indexTextView = (TextView) view.findViewById(R.id.indexTextView);
            reportedSpamTextView = (TextView) view.findViewById(R.id.reportedSpamTextView);
            reportedAbuseTextView = (TextView) view.findViewById(R.id.reportedAbuseTextView);
            reportedUninterestingTextView = (TextView) view.findViewById(R.id.reportedUninterestingTextView);
            reportedReligiousTextView = (TextView) view.findViewById(R.id.reportedReligiousTextView);
            reportedOtherTextView = (TextView) view.findViewById(R.id.reportedOtherTextView);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);

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
        TextView reportedSpamTextView, reportedAbuseTextView, reportedUninterestingTextView, reportedReligiousTextView, reportedOtherTextView;
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
            reportedSpamTextView = (TextView) view.findViewById(R.id.reportedSpamTextView);
            reportedAbuseTextView = (TextView) view.findViewById(R.id.reportedAbuseTextView);
            reportedUninterestingTextView = (TextView) view.findViewById(R.id.reportedUninterestingTextView);
            reportedReligiousTextView = (TextView) view.findViewById(R.id.reportedReligiousTextView);
            reportedOtherTextView = (TextView) view.findViewById(R.id.reportedOtherTextView);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);

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
        TextView reportedSpamTextView, reportedAbuseTextView, reportedUninterestingTextView, reportedReligiousTextView, reportedOtherTextView;
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
            reportedSpamTextView = (TextView) view.findViewById(R.id.reportedSpamTextView);
            reportedAbuseTextView = (TextView) view.findViewById(R.id.reportedAbuseTextView);
            reportedUninterestingTextView = (TextView) view.findViewById(R.id.reportedUninterestingTextView);
            reportedReligiousTextView = (TextView) view.findViewById(R.id.reportedReligiousTextView);
            reportedOtherTextView = (TextView) view.findViewById(R.id.reportedOtherTextView);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
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
        TextView commentDateTextView;
        TextView reportedSpamTextView, reportedAbuseTextView, reportedUninterestingTextView, reportedReligiousTextView, reportedOtherTextView;
        View underlineView;

        RootCommentViewHolder(View view) {
            super(view);
            commentorImageView = (ImageView) view.findViewById(R.id.commentorImageView);
            commentorUsernameTextView = (TextView) view.findViewById(R.id.commentorUsernameTextView);
            commentDataTextView = (TextView) view.findViewById(R.id.commentDataTextView);
            commentDateTextView = (TextView) view.findViewById(R.id.commentDateTextView);
            reportedSpamTextView = (TextView) view.findViewById(R.id.reportedSpamTextView);
            reportedAbuseTextView = (TextView) view.findViewById(R.id.reportedAbuseTextView);
            reportedUninterestingTextView = (TextView) view.findViewById(R.id.reportedUninterestingTextView);
            reportedReligiousTextView = (TextView) view.findViewById(R.id.reportedReligiousTextView);
            reportedOtherTextView = (TextView) view.findViewById(R.id.reportedOtherTextView);

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

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

}
