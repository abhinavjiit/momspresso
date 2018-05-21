package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.google.gson.internal.LinkedTreeMap;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.widget.GroupPostMediaViewPager;
import com.shuhart.bubblepagerindicator.BubblePageIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<GroupPostResult> postList;
    private HashMap<Integer, Integer> mViewPageStates = new HashMap<>();
    private RecyclerViewClickListener mListener;
    private int selectedPosition;

    public GroupsGenericPostRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
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
            if (postList.get(position).getMediaUrls() instanceof String) {
                return POST_TYPE_TEXT_POLL;
            } else {
                return POST_TYPE_IMAGE_POLL;
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
            textPostViewHolder.postDateTextView.setText(postList.get(position).getCreatedAt());
            textPostViewHolder.usernameTextView.setText(postList.get(position).getUserId());
            textPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
        } else if (holder instanceof MediaPostViewHolder) {
            MediaPostViewHolder mediaPostViewHolder = (MediaPostViewHolder) holder;
            mediaPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
            mediaPostViewHolder.postDateTextView.setText(postList.get(position).getCreatedAt());
            mediaPostViewHolder.usernameTextView.setText(postList.get(position).getUserId());
            mediaPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
            initializeViews((MediaPostViewHolder) holder, position);
        } else if (holder instanceof TextPollPostViewHolder) {
            TextPollPostViewHolder textPollPostViewHolder = (TextPollPostViewHolder) holder;
            textPollPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
            textPollPostViewHolder.postDateTextView.setText(postList.get(position).getCreatedAt());
            textPollPostViewHolder.usernameTextView.setText(postList.get(position).getUserId());
            textPollPostViewHolder.postDataTextView.setText(postList.get(position).getContent());
        } else {
            ImagePollPostViewHolder imageHolder = (ImagePollPostViewHolder) holder;
            imageHolder.postDataTextView.setText(postList.get(position).getContent());
            imageHolder.postDateTextView.setText(postList.get(position).getCreatedAt());
            imageHolder.usernameTextView.setText(postList.get(position).getUserId());
            imageHolder.postDataTextView.setText(postList.get(position).getContent());
            imageHolder.pollQuestionTextView.setText(postList.get(position).getContent());
            Map<String, String> imageMap = (Map<String, String>) postList.get(position).getMediaUrls();
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

        }
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
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        TextView postCommentsTextView;

        TextPostViewHolder(View view) {
            super(view);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
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
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        TextView postCommentsTextView;
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
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GroupPostDetailActivity.class);
                    intent.putExtra("postType", AppConstants.POST_TYPE_MEDIA);
                    intent.putExtra("postData", postList.get(getAdapterPosition()));
                    LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) postList.get(getAdapterPosition()).getMediaUrls();
                    intent.putExtra("mediaUrls", linkedTreeMap);
                    mContext.startActivity(intent);
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
        TextView postDataTextView;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        TextView postCommentsTextView;
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
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
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
        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        TextView postDataTextView;
        TextView upvoteCountTextView;
        TextView downvoteCountTextView;
        TextView postCommentsTextView;
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
        RelativeLayout option1Container;
        RelativeLayout option2Container;
        RelativeLayout option3Container;
        RelativeLayout option4Container;

        ImagePollPostViewHolder(View view) {
            super(view);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            upvoteCountTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteCountTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
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

            option1Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pollOption1ProgressBar.setProgress(60.0f);
                    pollOption2ProgressBar.setProgress(17.0f);
                    pollOption3ProgressBar.setProgress(13.0f);
                    pollOption4ProgressBar.setProgress(10.0f);
                    pollOption1TextView.setText("60%");
                    pollOption2TextView.setText("17%");
                    pollOption3TextView.setText("13%");
                    pollOption4TextView.setText("10%");
                    pollOption1ProgressBar.setVisibility(View.VISIBLE);
                    pollOption2ProgressBar.setVisibility(View.VISIBLE);
                    pollOption3ProgressBar.setVisibility(View.VISIBLE);
                    pollOption4ProgressBar.setVisibility(View.VISIBLE);
                    pollOption1TextView.setVisibility(View.VISIBLE);
                    pollOption2TextView.setVisibility(View.VISIBLE);
                    pollOption3TextView.setVisibility(View.VISIBLE);
                    pollOption4TextView.setVisibility(View.VISIBLE);
                }
            });

            option2Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pollOption1ProgressBar.setProgress(60.0f);
                    pollOption2ProgressBar.setProgress(17.0f);
                    pollOption3ProgressBar.setProgress(13.0f);
                    pollOption4ProgressBar.setProgress(10.0f);
                    pollOption1TextView.setText("60%");
                    pollOption2TextView.setText("17%");
                    pollOption3TextView.setText("13%");
                    pollOption4TextView.setText("10%");
                    pollOption1ProgressBar.setVisibility(View.VISIBLE);
                    pollOption2ProgressBar.setVisibility(View.VISIBLE);
                    pollOption3ProgressBar.setVisibility(View.VISIBLE);
                    pollOption4ProgressBar.setVisibility(View.VISIBLE);
                    pollOption1TextView.setVisibility(View.VISIBLE);
                    pollOption2TextView.setVisibility(View.VISIBLE);
                    pollOption3TextView.setVisibility(View.VISIBLE);
                    pollOption4TextView.setVisibility(View.VISIBLE);
                }
            });

            option3Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pollOption1ProgressBar.setProgress(60.0f);
                    pollOption2ProgressBar.setProgress(17.0f);
                    pollOption3ProgressBar.setProgress(13.0f);
                    pollOption4ProgressBar.setProgress(10.0f);
                    pollOption1TextView.setText("60%");
                    pollOption2TextView.setText("17%");
                    pollOption3TextView.setText("13%");
                    pollOption4TextView.setText("10%");
                    pollOption1ProgressBar.setVisibility(View.VISIBLE);
                    pollOption2ProgressBar.setVisibility(View.VISIBLE);
                    pollOption3ProgressBar.setVisibility(View.VISIBLE);
                    pollOption4ProgressBar.setVisibility(View.VISIBLE);
                    pollOption1TextView.setVisibility(View.VISIBLE);
                    pollOption2TextView.setVisibility(View.VISIBLE);
                    pollOption3TextView.setVisibility(View.VISIBLE);
                    pollOption4TextView.setVisibility(View.VISIBLE);
                }
            });

            option4Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pollOption1ProgressBar.setProgress(60.0f);
                    pollOption2ProgressBar.setProgress(17.0f);
                    pollOption3ProgressBar.setProgress(13.0f);
                    pollOption4ProgressBar.setProgress(10.0f);
                    pollOption1TextView.setText("60%");
                    pollOption2TextView.setText("17%");
                    pollOption3TextView.setText("13%");
                    pollOption4TextView.setText("10%");
                    pollOption1ProgressBar.setVisibility(View.VISIBLE);
                    pollOption2ProgressBar.setVisibility(View.VISIBLE);
                    pollOption3ProgressBar.setVisibility(View.VISIBLE);
                    pollOption4ProgressBar.setVisibility(View.VISIBLE);
                    pollOption1TextView.setVisibility(View.VISIBLE);
                    pollOption2TextView.setVisibility(View.VISIBLE);
                    pollOption3TextView.setVisibility(View.VISIBLE);
                    pollOption4TextView.setVisibility(View.VISIBLE);
                }
            });
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
