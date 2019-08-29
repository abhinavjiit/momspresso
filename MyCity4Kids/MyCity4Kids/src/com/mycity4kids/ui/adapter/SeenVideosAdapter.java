package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SeenVideosAdapter extends RecyclerView.Adapter<SeenVideosAdapter.SeenVideoViewHolder> {
    private final static String VIDEO_PUBLISHED_STATUS = "3";
    private Context mContext;
    private LayoutInflater mInflator;
    RecyclerViewClickListener mListener;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;

    public SeenVideosAdapter(Context mContext, RecyclerViewClickListener mListener) {
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public void setNewListData(ArrayList<VlogsListingAndDetailResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    @NonNull
    @Override
    public SeenVideosAdapter.SeenVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        SeenVideoViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.users_funny_video_item, parent, false);
        viewHolder = new SeenVideoViewHolder(v0, mListener);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull SeenVideosAdapter.SeenVideoViewHolder holder, int position) {


        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle())) {
            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
        } else {
            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitleSlug());
        }

        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUrl())) {
            Picasso.with(mContext).load(R.drawable.default_article)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
        } else {
            Picasso.with(mContext).load(articleDataModelsNew.get(position).getThumbnail())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
        }


        holder.dateTextView.setText(mContext.getString(R.string.user_funny_video_published_on, DateTimeUtils.getDateFromTimestamp(Long.parseLong(articleDataModelsNew.get(position).getPublished_time()))));

        if (VIDEO_PUBLISHED_STATUS.equals(articleDataModelsNew.get(position).getPublication_status())) {
            holder.shareImageView.setVisibility(View.VISIBLE);
        } else {
            holder.shareImageView.setVisibility(View.GONE);
        }


        if (articleDataModelsNew.get(position).getComment_count() != null && !articleDataModelsNew.get(position).getComment_count().equals("0")) {
            holder.commentCountTextView.setText(articleDataModelsNew.get(position).getComment_count());
        } else {
            holder.commentCountTextView.setVisibility(View.GONE);
        }

        if (articleDataModelsNew.get(position).getComment_count() != null && !articleDataModelsNew.get(position).getView_count().equals("0")) {
            holder.viewCountTextView.setText(articleDataModelsNew.get(position).getView_count());
        } else {
            holder.viewCountTextView.setVisibility(View.GONE);
        }

        if (articleDataModelsNew.get(position).getComment_count() != null && !articleDataModelsNew.get(position).getLike_count().equals("0")) {
            holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLike_count());
        } else {
            holder.recommendCountTextView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew.size();
    }


    class SeenVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txvArticleTitle, viewCountTextView, commentCountTextView, recommendCountTextView;
        ImageView articleImageView;
        TextView dateTextView;
        ImageView shareImageView;
        RelativeLayout rootView;

        public SeenVideoViewHolder(View view, RecyclerViewClickListener mListner) {
            super(view);
            rootView = (RelativeLayout) view.findViewById(R.id.rootView);
            txvArticleTitle = (TextView) view.findViewById(R.id.articleTitleTextView);
            articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
            shareImageView = (ImageView) view.findViewById(R.id.shareImageView);
            dateTextView = (TextView) view.findViewById(R.id.dateTextView);
            viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
            shareImageView.setOnClickListener(this);
            txvArticleTitle.setOnClickListener(this);
            rootView.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}

