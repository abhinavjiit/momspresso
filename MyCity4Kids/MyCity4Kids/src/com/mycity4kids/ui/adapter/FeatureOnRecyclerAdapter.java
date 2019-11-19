package com.mycity4kids.ui.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.CollectionsModels.UserCollectiosModel;
import com.mycity4kids.ui.activity.FeaturedOnActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class FeatureOnRecyclerAdapter extends RecyclerView.Adapter<FeatureOnRecyclerAdapter.FeatureOnViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<UserCollectiosModel> featuredList;
    private RecyclerViewClickListener mListener;
    private String userId;
    RecyclerView recyclerView;
    private FeatureOnViewHolder mHolder;

    public FeatureOnRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mListener = listener;
    }

    public void setData(ArrayList<UserCollectiosModel> featuredList) {
        this.featuredList = featuredList;
    }

    public void setListUpdate(int updatePos, ArrayList<UserCollectiosModel> featuredList) {
        this.featuredList = featuredList;
        notifyItemChanged(updatePos, mHolder.follow_text);
    }

    @Override
    public FeatureOnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FeatureOnViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.featured_on_item_cell, parent, false);
//        viewHolder = new FeatureOnViewHolder(v0, mListener);
        return new FeatureOnViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(final FeatureOnViewHolder holder, final int position) {
        mHolder = (FeatureOnViewHolder) holder;
        Picasso.with(mContext).load(featuredList.get(position).getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                .fit().into(holder.img);
        holder.featured_name.setText(featuredList.get(position).getName());
        holder.author_name.setText(featuredList.get(position).getName());
        if (featuredList.get(position).isFollowing()) {
            holder.follow_text.setText("Following");
        } else {
            holder.follow_text.setText(mContext.getResources().getString(R.string.ad_follow_author));
        }
        holder.follow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FeaturedOnActivity) mContext).followAPICall(featuredList.get(position).getUserId(), featuredList.get(position).getUserCollectionId(), featuredList.get(position).getSortOrder(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return featuredList == null ? 0 : featuredList.size();
    }

    public class FeatureOnViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView featured_name, author_name, follow_text;

        public FeatureOnViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            featured_name = itemView.findViewById(R.id.featured_name);
            author_name = itemView.findViewById(R.id.author_name);
            follow_text = itemView.findViewById(R.id.follow);
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