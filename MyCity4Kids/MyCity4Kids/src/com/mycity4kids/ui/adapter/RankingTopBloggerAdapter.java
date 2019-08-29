package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.ContributorListResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class RankingTopBloggerAdapter extends RecyclerView.Adapter<RankingTopBloggerAdapter.TopBloggerViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<ContributorListResult> contributorListResults;
    private final float density;
    private RecyclerViewClickListener mListener;

    public RankingTopBloggerAdapter(Context pContext, ArrayList<ContributorListResult> contributorListResults, RecyclerViewClickListener listener) {
        this.contributorListResults = contributorListResults;
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    @Override
    public TopBloggerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TopBloggerViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.top_blogger_recyler_item, parent, false);
        viewHolder = new TopBloggerViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TopBloggerViewHolder holder, int position) {

        try {
            Picasso.with(mContext).load(contributorListResults.get(position).getProfilePic().getClientApp()).fit()
                    .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(holder.rank1BloggerImageView);
        } catch (Exception e) {
            Picasso.with(mContext).load(R.drawable.default_commentor_img).into(holder.rank1BloggerImageView);
        }

        holder.rank1BloggerNameTV.setText(contributorListResults.get(position).getFirstName() + " " + contributorListResults.get(position).getLastName());
        holder.rank1FollowersCount.setText(contributorListResults.get(position).getFollowersCount() + " " + mContext.getString(R.string.ranking_followers_label));
        switch (position) {
            case 0:
                holder.rankTextView.setText("1st");
                break;
            case 1:
                holder.rankTextView.setText("2nd");
                break;
            case 2:
                holder.rankTextView.setText("3rd");
                break;
            case 3:
                holder.rankTextView.setText("4th");
                break;
            case 4:
                holder.rankTextView.setText("5th");
                break;
        }

    }

    @Override
    public int getItemCount() {
        return contributorListResults == null ? 0 : contributorListResults.size();
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class TopBloggerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView rank1BloggerImageView;
        TextView rank1BloggerNameTV;
        TextView rankTextView;
        TextView rank1FollowersCount;

        public TopBloggerViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            rank1BloggerImageView = (ImageView) itemView.findViewById(R.id.rank1BloggerImageView);
            rank1BloggerNameTV = (TextView) itemView.findViewById(R.id.rank1BloggerNameTV);
            rankTextView = (TextView) itemView.findViewById(R.id.rankTextView);
            rank1FollowersCount = (TextView) itemView.findViewById(R.id.rank1FollowersCount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}