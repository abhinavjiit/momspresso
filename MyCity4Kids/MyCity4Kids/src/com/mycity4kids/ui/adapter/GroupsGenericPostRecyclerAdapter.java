package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.google.gson.internal.LinkedTreeMap;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsActionVoteResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.shuhart.bubblepagerindicator.BubblePageIndicator;
import com.squareup.picasso.Picasso;

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

public class GroupsGenericPostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int POST_TYPE_TEXT = 0;
    public static final int POST_TYPE_MEDIA = 1;
    public static final int POST_TYPE_TEXT_POLL = 2;
    public static final int POST_TYPE_IMAGE_POLL = 3;

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

    public GroupsGenericPostRecyclerAdapter(Context pContext, RecyclerViewClickListener listener, GroupResult selectedGroup, String memberType) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        this.selectedGroup = selectedGroup;
        localizedComment = mContext.getString(R.string.ad_comments_title);
        localizedHelpful = mContext.getString(R.string.groups_post_helpful);
        localizedNotHelpful = mContext.getString(R.string.groups_post_nothelpful);
        this.memberType = memberType;
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
            textPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
            textPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + " " + localizedHelpful);
            textPostViewHolder.downvoteCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            textPostViewHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
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
        } else if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder mediaPostViewHolder = (MediaPostViewHolder) holder;
            mediaPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + " " + localizedHelpful);
            mediaPostViewHolder.downvoteCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            mediaPostViewHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
            mediaPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
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
        } else if (holder instanceof TextPollPostViewHolder) {
            TextPollPostViewHolder textPollPostViewHolder = (TextPollPostViewHolder) holder;
            textPollPostViewHolder.pollQuestionTextView.setText(postList.get(position).getContent());
            textPollPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postList.get(position).getCreatedAt()));
            textPollPostViewHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + " " + localizedHelpful);
            textPollPostViewHolder.downvoteCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            textPollPostViewHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
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
            if (postList.get(position).isVoted()) {
                showVotingData(textPollPostViewHolder, postList.get(position));
            } else {
                hideVotingData(textPollPostViewHolder);
            }
        } else {
            ImagePollPostViewHolder imageHolder = (ImagePollPostViewHolder) holder;
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
            imageHolder.upvoteCountTextView.setText(postList.get(position).getHelpfullCount() + " " + localizedHelpful);
            imageHolder.downvoteCountTextView.setText(postList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            imageHolder.postCommentsTextView.setText(postList.get(position).getResponseCount() + " " + localizedComment);
            imageHolder.pollQuestionTextView.setText(postList.get(position).getContent());
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
            if (postList.get(position).isVoted()) {
                showImagePollVotingData(imageHolder, postList.get(position));
            } else {
                hideImagePollVotingData(imageHolder);
            }

        }
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
        Map<String, String> map = (Map<String, String>) postList.get(position).getMediaUrls();
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

        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        ImageView shareTextView;

        TextPostViewHolder(View view) {
            super(view);
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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);

            postCommentsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("groupItem", selectedGroup);
                    intent.putExtra("postType", AppConstants.POST_TYPE_TEXT);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((GroupDetailsActivity) mContext).startActivityForResult(intent, 1111);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("groupItem", selectedGroup);
                    intent.putExtra("postType", AppConstants.POST_TYPE_TEXT);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    intent.putExtra("postId", postList.get(getAdapterPosition()).getId());
                    intent.putExtra("groupId", postList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, memberType);
                    ((GroupDetailsActivity) mContext).startActivityForResult(intent, 1111);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class MediaPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        ImageView shareTextView;
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
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            upvoteContainer = (LinearLayout) view.findViewById(R.id.upvoteContainer);
            downvoteContainer = (LinearLayout) view.findViewById(R.id.downvoteContainer);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            dotIndicatorView = (BubblePageIndicator) view.findViewById(R.id.dotIndicatorView);
            postDataViewPager = (GroupPostMediaViewPager) view.findViewById(R.id.postDataViewPager);
            indexTextView = (TextView) view.findViewById(R.id.indexTextView);

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

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);

            postCommentsTextView.setOnClickListener(new View.OnClickListener() {
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
                    ((GroupDetailsActivity) mContext).startActivityForResult(intent, 1111);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
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
                    ((GroupDetailsActivity) mContext).startActivityForResult(intent, 1111);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onGroupPostRecyclerItemClick(v, getAdapterPosition());
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
        ImageView shareTextView;
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
            option3Container = (RelativeLayout) view.findViewById(R.id.option3Container);
            option4Container = (RelativeLayout) view.findViewById(R.id.option4Container);

            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postSettingImageView.setOnClickListener(this);
            upvoteContainer.setOnClickListener(this);
            downvoteContainer.setOnClickListener(this);
            shareTextView.setOnClickListener(this);

            postCommentsTextView.setOnClickListener(new View.OnClickListener() {
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
                    ((GroupDetailsActivity) mContext).startActivityForResult(intent, 1111);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
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
                    ((GroupDetailsActivity) mContext).startActivityForResult(intent, 1111);
                }
            });

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
        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        TextView pollQuestionTextView;
        ImageView shareTextView;
        ImageView option1ImageView, option2ImageView, option3ImageView, option4ImageView;
        RoundCornerProgressBar pollOption1ProgressBar, pollOption2ProgressBar, pollOption3ProgressBar, pollOption4ProgressBar;
        TextView pollOption1TextView, pollOption2TextView, pollOption3TextView, pollOption4TextView;
        LinearLayout lastOptionsContainer;
        RelativeLayout option1Container, option2Container, option3Container, option4Container;

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
            shareTextView.setOnClickListener(this);

            postCommentsTextView.setOnClickListener(new View.OnClickListener() {
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
                    ((GroupDetailsActivity) mContext).startActivityForResult(intent, 1111);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
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
                    ((GroupDetailsActivity) mContext).startActivityForResult(intent, 1111);
                }
            });

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
                                    postList.get(i).setTotalVotesCount(postList.get(i).getTotalVotesCount() + postList.get(i).getOption1VoteCount());
                                    break;
                                case "option2":
                                    postList.get(i).setOption2VoteCount(postList.get(i).getOption2VoteCount() + 1);
                                    postList.get(i).setTotalVotesCount(postList.get(i).getTotalVotesCount() + postList.get(i).getOption2VoteCount());
                                    break;
                                case "option3":
                                    postList.get(i).setOption3VoteCount(postList.get(i).getOption3VoteCount() + 1);
                                    postList.get(i).setTotalVotesCount(postList.get(i).getTotalVotesCount() + postList.get(i).getOption3VoteCount());
                                    break;
                                case "option4":
                                    postList.get(i).setOption4VoteCount(postList.get(i).getOption4VoteCount() + 1);
                                    postList.get(i).setTotalVotesCount(postList.get(i).getTotalVotesCount() + postList.get(i).getOption4VoteCount());
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

    public interface RecyclerViewClickListener {
        void onGroupPostRecyclerItemClick(View view, int position);
    }

}
