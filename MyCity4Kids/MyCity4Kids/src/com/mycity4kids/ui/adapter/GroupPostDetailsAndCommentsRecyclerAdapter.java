package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.shuhart.bubblepagerindicator.BubblePageIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupPostDetailsAndCommentsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = -1;
    public static final int COMMENT_LEVEL_ROOT = 0;
    public static final int COMMENT_LEVEL_REPLY = 1;
    public static final int COMMENT_LEVEL_REPLY_REPLY = 2;

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<CommentsData> postCommentsList;
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
    }

    public void setData(GroupPostResult groupPostResult, ArrayList<CommentsData> postCommentsList) {
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
            return postCommentsList.get(position-1).getCommentLevel();
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
        } else if (holder instanceof MediaPostViewHolder) {
            initializeViews((MediaPostViewHolder) holder, position);
        } else if (holder instanceof TextPollPostViewHolder) {
        } else if (holder instanceof ImagePollPostViewHolder) {
            GroupsGenericPostRecyclerAdapter.ImagePollPostViewHolder imageHolder = (GroupsGenericPostRecyclerAdapter.ImagePollPostViewHolder) holder;
            imageHolder.pollQuestionTextView.setText(groupPostResult.getContent());
            Map<String, String> imageMap = (Map<String, String>) groupPostResult.getMediaUrls();
            for (Map.Entry<String, String> entry : imageMap.entrySet()) {
                switch (entry.getKey()) {
                    case "image1":
                        Picasso.with(mContext).load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option1ImageView);
                        break;
                    case "image2":
                        Picasso.with(mContext).load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option2ImageView);
                        break;
                    case "image3":
                        imageHolder.lastOptionsContainer.setVisibility(View.VISIBLE);
                        imageHolder.option3Container.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option3ImageView);
                        break;
                    case "image4":
                        imageHolder.lastOptionsContainer.setVisibility(View.VISIBLE);
                        imageHolder.option4Container.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(entry.getValue())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageHolder.option4ImageView);
                        break;
                }
            }

        } else if (holder instanceof RootCommentViewHolder) {
            if (postCommentsList.get(position).getIsLastConversation() == 1) {
                ((RootCommentViewHolder) holder).underlineView.setVisibility(View.VISIBLE);
            } else {
                ((RootCommentViewHolder) holder).underlineView.setVisibility(View.GONE);
            }
        } else if (holder instanceof CommentReplyViewHolder) {
            if (postCommentsList.get(position).getIsLastConversation() == 1) {
                ((CommentReplyViewHolder) holder).underlineView.setVisibility(View.VISIBLE);
            } else {
                ((CommentReplyViewHolder) holder).underlineView.setVisibility(View.GONE);
            }
        } else {
            if (postCommentsList.get(position).getIsLastConversation() == 1) {
                ((ReplyReplyViewHolder) holder).underlineView.setVisibility(View.VISIBLE);
            } else {
                ((ReplyReplyViewHolder) holder).underlineView.setVisibility(View.GONE);
            }

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
//        if (mViewPageStates.containsKey(position)) {
//            holder.postDataViewPager.setCurrentItem(mViewPageStates.get(position));
//            holder.indexTextView.setText((mViewPageStates.get(position) + 1) + "/" + mediaList.size());
//        } else {
//            holder.indexTextView.setText((holder.postDataViewPager.getCurrentItem() + 1) + "/" + mediaList.size());
//        }
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView groupDescTextView;
        TextView groupAdminTextView;

        TextPostViewHolder(View view) {
            super(view);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class MediaPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private BubblePageIndicator dotIndicatorView;
        private GroupPostMediaViewPager postDataViewPager;
        private TextView indexTextView;
        private GroupMediaPostViewPagerAdapter mViewPagerAdapter;

        MediaPostViewHolder(View view) {
            super(view);
            dotIndicatorView = (BubblePageIndicator) view.findViewById(R.id.dotIndicatorView);
            postDataViewPager = (GroupPostMediaViewPager) view.findViewById(R.id.postDataViewPager);
            indexTextView = (TextView) view.findViewById(R.id.indexTextView);

            mViewPagerAdapter = new GroupMediaPostViewPagerAdapter(mContext);
//            postDataViewPager.setAdapter(mViewPagerAdapter);
//            postDataViewPager.setTag(0);
            postDataViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    postDataViewPager.setTag(position);
                    currentPagerPos = position;
                    indexTextView.setText((position + 1) + "/" + postDataViewPager.getAdapter().getCount());
//                    dotIndicatorView.onPageChange(position);
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
        RoundCornerProgressBar pollOption1ProgressBar;
        RoundCornerProgressBar pollOption2ProgressBar;
        TextView pollOption1TextView;
        TextView pollOption2TextView;
        TextView pollResult1TextView;
        TextView pollResult2TextView;
        TextView pollOption1ProgressTextView;
        TextView pollOption2ProgressTextView;

        TextPollPostViewHolder(View view) {
            super(view);
            pollOption1ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption1ProgressBar);
            pollOption2ProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.pollOption2ProgressBar);
            pollOption1TextView = (TextView) view.findViewById(R.id.pollOption1TextView);
            pollOption2TextView = (TextView) view.findViewById(R.id.pollOption2TextView);
            pollResult1TextView = (TextView) view.findViewById(R.id.pollResult1TextView);
            pollResult2TextView = (TextView) view.findViewById(R.id.pollResult2TextView);
            pollOption1ProgressTextView = (TextView) view.findViewById(R.id.pollOption1ProgressTextView);
            pollOption2ProgressTextView = (TextView) view.findViewById(R.id.pollOption2ProgressTextView);
            pollOption1ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pollOption1ProgressBar.setProgress(80.0f);
                    pollOption2ProgressBar.setProgress(20.0f);
                    pollOption1ProgressTextView.setText("80%");
                    pollOption2ProgressTextView.setText("20%");
                    pollOption1ProgressTextView.setVisibility(View.VISIBLE);
                    pollOption2ProgressTextView.setVisibility(View.VISIBLE);
                    pollResult1TextView.setVisibility(View.VISIBLE);
                    pollResult2TextView.setVisibility(View.VISIBLE);
                    pollOption1TextView.setVisibility(View.GONE);
                    pollOption2TextView.setVisibility(View.GONE);
                }
            });

            pollOption2ProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pollOption1ProgressBar.setProgress(40.0f);
                    pollOption2ProgressBar.setProgress(60.0f);
                    pollOption1ProgressTextView.setText("40%");
                    pollOption2ProgressTextView.setText("60%");
                    pollOption1ProgressTextView.setVisibility(View.VISIBLE);
                    pollOption2ProgressTextView.setVisibility(View.VISIBLE);
                    pollResult1TextView.setVisibility(View.VISIBLE);
                    pollResult2TextView.setVisibility(View.VISIBLE);
                    pollOption1TextView.setVisibility(View.GONE);
                    pollOption2TextView.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class ImagePollPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView pollQuestionTextView;
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
        LinearLayout lastOptionsContainer;
        RelativeLayout option3Container;
        RelativeLayout option4Container;

        ImagePollPostViewHolder(View view) {
            super(view);
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
            option3Container = (RelativeLayout) view.findViewById(R.id.option3Container);
            option4Container = (RelativeLayout) view.findViewById(R.id.option4Container);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class RootCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View underlineView;

        RootCommentViewHolder(View view) {
            super(view);
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

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

}
