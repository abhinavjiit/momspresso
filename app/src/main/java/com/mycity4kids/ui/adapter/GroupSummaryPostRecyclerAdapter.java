package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.mycity4kids.widget.IndefinitePagerIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupSummaryPostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int POST_TYPE_TEXT = 1;
    public static final int POST_TYPE_MEDIA = 2;
    public static final int POST_TYPE_TEXT_POLL = 3;
    public static final int POST_TYPE_IMAGE_POLL = 4;

    private HashMap<Integer, Integer> mViewPageStates = new HashMap<>();

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupPostResult> postDataList;
    private RecyclerViewClickListener mListener;
    private GroupResult groupDetails;
    private final String localizedNotHelpful, localizedComment;
    private String modeList = "";

    public GroupSummaryPostRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        localizedComment = mContext.getString(R.string.ad_comments_title);
        localizedNotHelpful = mContext.getString(R.string.groups_post_nothelpful);
    }

    public void setHeaderData(GroupResult groupDetails) {
        this.groupDetails = groupDetails;
    }

    public void setData(ArrayList<GroupPostResult> postDataList) {
        this.postDataList = postDataList;
    }

    @Override
    public int getItemCount() {
        return postDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            if (postDataList.get(position).getType().equals("0")) {
                return POST_TYPE_TEXT;
            } else if (postDataList.get(position).getType().equals("1")) {
                return POST_TYPE_MEDIA;
            } else if (postDataList.get(position).getType().equals("2")) {
                if (postDataList.get(position).getPollType().equals("1")) {
                    return POST_TYPE_IMAGE_POLL;
                } else {
                    return POST_TYPE_TEXT_POLL;
                }
            } else if (postDataList.get(position).getType().equals("5")) {
                return POST_TYPE_TEXT;
            }
        }
        return POST_TYPE_TEXT_POLL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View v0 = mInflator.inflate(R.layout.groups_summary_header, parent, false);
            return new HeaderViewHolder(v0);
        } else if (viewType == POST_TYPE_TEXT) {
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
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).createdTimeTextView.setText(mContext.getString(R.string.groups_created) + " - " + DateTimeUtils.getDateFromNanoMilliTimestamp(groupDetails.getCreatedAt()));
            if (!groupDetails.getHeaderImage().trim().isEmpty()) {
                Picasso.get().load(groupDetails.getHeaderImage())
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(((HeaderViewHolder) holder).groupImageView);
            }

            if (AppConstants.GROUP_TYPE_OPEN_KEY.equals(groupDetails.getType())) {
                ((HeaderViewHolder) holder).groupTypeTextView.setText(mContext.getString(R.string.groups_anyone_join));
            } else if (AppConstants.GROUP_TYPE_CLOSED_KEY.equals(groupDetails.getType())) {
                ((HeaderViewHolder) holder).groupTypeTextView.setText(mContext.getString(R.string.groups_closed_gp));
            } else {
                ((HeaderViewHolder) holder).groupTypeTextView.setText(mContext.getString(R.string.groups_invitation_only_gp));
            }
            ((HeaderViewHolder) holder).groupNameTextView.setText(groupDetails.getTitle());
            ((HeaderViewHolder) holder).memberCountTextView.setText(groupDetails.getMemberCount() + " " + mContext.getString(R.string.groups_member_label));
            ((HeaderViewHolder) holder).groupDescTextView.setText(groupDetails.getDescription());
            if (groupDetails.getAdminMembers().getData() != null && !groupDetails.getAdminMembers().getData().isEmpty()) {
                modeList = "";
                for (int i = 0; i < groupDetails.getAdminMembers().getData().size(); i++) {
                    if (StringUtils.isNullOrEmpty(modeList)) {
                        if (modeList == null) {
                            modeList = "";
                        }
                        modeList = groupDetails.getAdminMembers().getData().get(i).getUserInfo().getFirstName() + " "
                                + groupDetails.getAdminMembers().getData().get(i).getUserInfo().getLastName();
                    } else {
                        modeList = modeList + ", " + groupDetails.getAdminMembers().getData().get(i).getUserInfo().getFirstName() + " "
                                + groupDetails.getAdminMembers().getData().get(i).getUserInfo().getLastName();
                    }
                }
                ((HeaderViewHolder) holder).groupAdminTextView.setText(modeList);
            }
        } else if (holder instanceof TextPostViewHolder) {
            TextPostViewHolder textPostViewHolder = (TextPostViewHolder) holder;
            textPostViewHolder.postDataTextView.setText(postDataList.get(position).getContent());
            Linkify.addLinks(textPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            textPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPostViewHolder.postDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(textPostViewHolder.postDataTextView);

            if (postDataList.get(position).getHelpfullCount() < 1) {
                textPostViewHolder.upvoteCountTextView.setText("");
            } else {
                textPostViewHolder.upvoteCountTextView.setText(postDataList.get(position).getHelpfullCount() + "");
            }
            textPostViewHolder.downvoteCountTextView.setText(postDataList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            textPostViewHolder.postCommentsTextView.setText(postDataList.get(position).getResponseCount() + " " + localizedComment);
            textPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postDataList.get(position).getCreatedAt()));
            if (postDataList.get(position).getIsAnnon() == 1) {
                textPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postDataList != null && postDataList.size() != 0) {
                    if (postDataList.get(position) != null && postDataList.get(position).getUserInfo() != null && postDataList.get(position).getUserInfo().getUserTag() != null) {
                        if (postDataList.get(position).getUserInfo().getUserTag().size() != 0) {
                            textPostViewHolder.userTag.setText(postDataList.get(position).getUserInfo().getUserTag().get(0));
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
            if (postDataList.get(position).getIsAnnon() == 1) {
                textPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                textPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                textPostViewHolder.usernameTextView.setText(postDataList.get(position).getUserInfo().getFirstName() + " " + postDataList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.get().load(postDataList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(textPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
        } else if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder mediaPostViewHolder = (MediaPostViewHolder) holder;

            mediaPostViewHolder.postDataTextView.setText(postDataList.get(position).getContent());
            Linkify.addLinks(mediaPostViewHolder.postDataTextView, Linkify.WEB_URLS);
            mediaPostViewHolder.postDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mediaPostViewHolder.postDataTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(mediaPostViewHolder.postDataTextView);

            if (postDataList.get(position).getHelpfullCount() < 1) {
                mediaPostViewHolder.upvoteCountTextView.setText("");
            } else {
                mediaPostViewHolder.upvoteCountTextView.setText(postDataList.get(position).getHelpfullCount() + "");
            }
            mediaPostViewHolder.downvoteCountTextView.setText(postDataList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            mediaPostViewHolder.postCommentsTextView.setText(postDataList.get(position).getResponseCount() + " " + localizedComment);
//            mediaPostViewHolder.postDataTextView.setText(postDataList.get(position).getContent().replace(" ","&nbsp;").replace("\n","<br />"));
            mediaPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postDataList.get(position).getCreatedAt()));
            if (postDataList.get(position).getIsAnnon() == 1) {
                mediaPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postDataList != null && postDataList.size() != 0) {
                    if (postDataList.get(position) != null && postDataList.get(position).getUserInfo() != null && postDataList.get(position).getUserInfo().getUserTag() != null) {
                        if (postDataList.get(position).getUserInfo().getUserTag().size() != 0) {
                            mediaPostViewHolder.userTag.setText(postDataList.get(position).getUserInfo().getUserTag().get(0));
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
            if (postDataList.get(position).getIsAnnon() == 1) {
                mediaPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                mediaPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                mediaPostViewHolder.usernameTextView.setText(postDataList.get(position).getUserInfo().getFirstName() + " " + postDataList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.get().load(postDataList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(mediaPostViewHolder.userImageView);
                } catch (Exception e) {
                    mediaPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            initializeViews((MediaPostViewHolder) holder, position);
        } else if (holder instanceof TextPollPostViewHolder) {
            TextPollPostViewHolder textPollPostViewHolder = (TextPollPostViewHolder) holder;
//            textPollPostViewHolder.pollQuestionTextView.setText(postDataList.get(position).getContent().replace(" ","&nbsp;").replace("\n","<br />"));
            textPollPostViewHolder.pollQuestionTextView.setText(postDataList.get(position).getContent());
            Linkify.addLinks(textPollPostViewHolder.pollQuestionTextView, Linkify.WEB_URLS);
            textPollPostViewHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            textPollPostViewHolder.pollQuestionTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(textPollPostViewHolder.pollQuestionTextView);
            textPollPostViewHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postDataList.get(position).getCreatedAt()));

            if (postDataList.get(position).getHelpfullCount() < 1) {
                textPollPostViewHolder.upvoteCountTextView.setText("");
            } else {
                textPollPostViewHolder.upvoteCountTextView.setText(postDataList.get(position).getHelpfullCount() + "");
            }
            textPollPostViewHolder.downvoteCountTextView.setText(postDataList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            textPollPostViewHolder.postCommentsTextView.setText(postDataList.get(position).getResponseCount() + " " + localizedComment);
            if (postDataList.get(position).getIsAnnon() == 1) {
                textPollPostViewHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postDataList != null && postDataList.size() != 0) {
                    if (postDataList.get(position) != null && postDataList.get(position).getUserInfo() != null && postDataList.get(position).getUserInfo().getUserTag() != null) {
                        if (postDataList.get(position).getUserInfo().getUserTag().size() != 0) {
                            textPollPostViewHolder.userTag.setText(postDataList.get(position).getUserInfo().getUserTag().get(0));
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
            if (postDataList.get(position).getIsAnnon() == 1) {
                textPollPostViewHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                textPollPostViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                textPollPostViewHolder.usernameTextView.setText(postDataList.get(position).getUserInfo().getFirstName() + " " + postDataList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.get().load(postDataList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(textPollPostViewHolder.userImageView);
                } catch (Exception e) {
                    textPollPostViewHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            textPollPostViewHolder.option3Container.setVisibility(View.GONE);
            textPollPostViewHolder.option4Container.setVisibility(View.GONE);
            Map<String, String> optionsMap = (Map<String, String>) postDataList.get(position).getPollOptions();
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
        } else {
            ImagePollPostViewHolder imageHolder = (ImagePollPostViewHolder) holder;

            imageHolder.pollQuestionTextView.setText(postDataList.get(position).getContent());
            Linkify.addLinks(imageHolder.pollQuestionTextView, Linkify.WEB_URLS);
            imageHolder.pollQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            imageHolder.pollQuestionTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.groups_blue_color));
            addLinkHandler(imageHolder.pollQuestionTextView);

            imageHolder.postDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(postDataList.get(position).getCreatedAt()));
            if (postDataList.get(position).getIsAnnon() == 1) {
                imageHolder.userTag.setVisibility(View.GONE);
            } else {
                if (postDataList != null && postDataList.size() != 0) {
                    if (postDataList.get(position) != null && postDataList.get(position).getUserInfo() != null && postDataList.get(position).getUserInfo().getUserTag() != null) {
                        if (postDataList.get(position).getUserInfo().getUserTag().size() != 0) {
                            imageHolder.userTag.setText(postDataList.get(position).getUserInfo().getUserTag().get(0));
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
            if (postDataList.get(position).getIsAnnon() == 1) {
                imageHolder.usernameTextView.setText(mContext.getString(R.string.groups_anonymous));
                imageHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_incognito));
            } else {
                imageHolder.usernameTextView.setText(postDataList.get(position).getUserInfo().getFirstName() + " " + postDataList.get(position).getUserInfo().getLastName());
                try {
                    Picasso.get().load(postDataList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.userImageView);
                } catch (Exception e) {
                    imageHolder.userImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
            if (postDataList.get(position).getHelpfullCount() < 1) {
                imageHolder.upvoteCountTextView.setText("");
            } else {
                imageHolder.upvoteCountTextView.setText(postDataList.get(position).getHelpfullCount() + "");
            }
            imageHolder.downvoteCountTextView.setText(postDataList.get(position).getNotHelpfullCount() + " " + localizedNotHelpful);
            imageHolder.postCommentsTextView.setText(postDataList.get(position).getResponseCount() + " " + localizedComment);
//            imageHolder.pollQuestionTextView.setText(postDataList.get(position).getContent().replace(" ","&nbsp;").replace("\n","<br />"));
            Map<String, String> imageMap = (Map<String, String>) postDataList.get(position).getPollOptions();
            imageHolder.lastOptionsContainer.setVisibility(View.GONE);
            imageHolder.option3Container.setVisibility(View.GONE);
            imageHolder.option4Container.setVisibility(View.GONE);
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
            imageHolder.pollOption1ProgressBar.setProgress(0f);
            imageHolder.pollOption2ProgressBar.setProgress(0f);
            imageHolder.pollOption3ProgressBar.setProgress(0f);
            imageHolder.pollOption4ProgressBar.setProgress(0f);
        }
    }

    private void initializeViews(MediaPostViewHolder holder, int position) {
        ArrayList<String> mediaList = new ArrayList<>();
        Map<String, String> map = (Map<String, String>) postDataList.get(position).getMediaUrls();
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

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView groupImageView, shareGroupImageView;
        TextView memberCountTextView;
        TextView groupNameTextView;
        TextView groupDescTextView;
        TextView groupAdminTextView;
        TextView createdTimeTextView;
        TextView groupTypeTextView;

        HeaderViewHolder(View view) {
            super(view);
            groupImageView = (ImageView) view.findViewById(R.id.groupImageView);
            groupDescTextView = (TextView) view.findViewById(R.id.groupDescTextView);
            groupAdminTextView = (TextView) view.findViewById(R.id.groupAdminTextView);
            memberCountTextView = (TextView) view.findViewById(R.id.memberCountTextView);
            groupNameTextView = (TextView) view.findViewById(R.id.groupNameTextView);
            createdTimeTextView = (TextView) view.findViewById(R.id.createdTimeTextView);
            groupTypeTextView = (TextView) view.findViewById(R.id.groupTypeTextView);
            shareGroupImageView = (ImageView) view.findViewById(R.id.shareGroupImageView);

            shareGroupImageView.setVisibility(View.INVISIBLE);

            groupDescTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("GpSumPstRecyclerAdapter", "groupDescTextView");
//                    getAdapterPosition()
                }
            });
            groupAdminTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("GpSumPstRecyclerAdapter", "groupAdminTextView");
                }
            });
            memberCountTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView, userTag;
        ImageView postSettingImageView;
        ImageView shareTextView, whatsAppShare;

        TextPostViewHolder(View view) {
            super(view);
            userTag = (TextView) view.findViewById(R.id.userTag);
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
            whatsAppShare = (ImageView) view.findViewById(R.id.whatsappShare);
            postSettingImageView.setVisibility(View.GONE);
            upvoteContainer.setOnClickListener(this);
            upvoteCountTextView.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsAppShare.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, mContext.getText(R.string.join_group), Toast.LENGTH_LONG).show();
        }
    }

    public class MediaPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView, userTag;
        ImageView postSettingImageView;
        private IndefinitePagerIndicator dotIndicatorView;
        private GroupPostMediaViewPager postDataViewPager;
        private TextView indexTextView;
        private GroupMediaPostViewPagerAdapter mViewPagerAdapter;
        ImageView shareTextView, whatsAppShare;

        MediaPostViewHolder(View view) {
            super(view);
            userTag = (TextView) view.findViewById(R.id.userTag);

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
            whatsAppShare = (ImageView) view.findViewById(R.id.whatsappShare);
            upvoteContainer.setOnClickListener(this);
            upvoteCountTextView.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsAppShare.setOnClickListener(this);

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
            postSettingImageView.setVisibility(View.GONE);

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, mContext.getText(R.string.join_group), Toast.LENGTH_LONG).show();
        }
    }

    public class TextPollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView upvoteCountTextView, downvoteCountTextView, userTag;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        TextView pollQuestionTextView;
        RoundCornerProgressBar pollOption1ProgressBar, pollOption2ProgressBar, pollOption3ProgressBar, pollOption4ProgressBar;
        TextView pollOption1TextView, pollOption2TextView, pollOption3TextView, pollOption4TextView;
        TextView pollResult1TextView, pollResult2TextView, pollResult3TextView, pollResult4TextView;
        TextView pollOption1ProgressTextView, pollOption2ProgressTextView, pollOption3ProgressTextView, pollOption4ProgressTextView;
        RelativeLayout option3Container, option4Container;
        ImageView shareTextView, whatsAppShare;

        TextPollPostViewHolder(View view) {
            super(view);
            userTag = (TextView) view.findViewById(R.id.userTag);

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
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            whatsAppShare = (ImageView) view.findViewById(R.id.whatsappShare);
            upvoteContainer.setOnClickListener(this);
            upvoteCountTextView.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsAppShare.setOnClickListener(this);

            postSettingImageView.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, mContext.getText(R.string.join_group), Toast.LENGTH_LONG).show();
        }
    }

    public class ImagePollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView;
        TextView usernameTextView, userTag;
        TextView postDateTextView;
        TextView upvoteCountTextView, downvoteCountTextView;
        LinearLayout upvoteContainer, downvoteContainer;
        TextView postCommentsTextView;
        ImageView postSettingImageView;
        TextView pollQuestionTextView;
        ImageView option1ImageView, option2ImageView, option3ImageView, option4ImageView;
        RoundCornerProgressBar pollOption1ProgressBar, pollOption2ProgressBar, pollOption3ProgressBar, pollOption4ProgressBar;
        TextView pollOption1TextView, pollOption2TextView, pollOption3TextView, pollOption4TextView;
        LinearLayout lastOptionsContainer;
        RelativeLayout option1Container, option2Container, option3Container, option4Container;
        ImageView shareTextView, whatsAppShare;

        ImagePollPostViewHolder(View view) {
            super(view);
            userTag = (TextView) view.findViewById(R.id.userTag);
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
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            whatsAppShare = (ImageView) view.findViewById(R.id.whatsappShare);
            upvoteContainer.setOnClickListener(this);
            upvoteCountTextView.setOnClickListener(this);
            postCommentsTextView.setOnClickListener(this);
            shareTextView.setOnClickListener(this);
            whatsAppShare.setOnClickListener(this);

            postSettingImageView.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, mContext.getText(R.string.join_group), Toast.LENGTH_LONG).show();
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
}
