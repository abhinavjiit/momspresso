package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;

import java.util.ArrayList;

/**
 * Created by hemant on 25/5/18.
 */

public class GroupMemberRecyclerAdapter extends RecyclerView.Adapter<GroupMemberRecyclerAdapter.GroupMembersHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public GroupMemberRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public GroupMembersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GroupMembersHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.group_member_recycler_item, parent, false);
        viewHolder = new GroupMembersHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GroupMembersHolder holder, int position) {
        holder.memberNameTextView.setText(articleDataModelsNew.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class GroupMembersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView memberNameTextView;
        ImageView memberImageView;
        ImageView memberSettingsImageView;

        public GroupMembersHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            memberImageView = (ImageView) itemView.findViewById(R.id.memberImageView);
            memberNameTextView = (TextView) itemView.findViewById(R.id.memberNameTextView);
            memberSettingsImageView = (ImageView) itemView.findViewById(R.id.memberSettingsImageView);
            memberSettingsImageView.setOnClickListener(this);
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