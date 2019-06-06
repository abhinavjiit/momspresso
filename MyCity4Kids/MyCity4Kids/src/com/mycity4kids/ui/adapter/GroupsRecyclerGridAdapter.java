package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.lid.lib.LabelImageView;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.ui.fragment.GroupsFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsRecyclerGridAdapter extends RecyclerView.Adapter<GroupsRecyclerGridAdapter.GroupsViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupResult> groupsDataList;
    private GroupsRecyclerGridAdapter.RecyclerViewClickListener mListener;
    private int selectedPosition;
    private boolean isMember, isFullList;

    public GroupsRecyclerGridAdapter(Context pContext, GroupsRecyclerGridAdapter.RecyclerViewClickListener listener, boolean isMember, boolean isFullList) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        this.isMember = isMember;
        this.isFullList = isFullList;
    }

    public void setNewListData(ArrayList<GroupResult> groupsDataList) {
        this.groupsDataList = groupsDataList;
    }

    @Override
    public int getItemCount() {
        if (isFullList) {
            return groupsDataList.size();
        }
        if (groupsDataList.size() >= 4) {
            return 4;
        } else {
            return groupsDataList.size();
        }
    }

    @Override
    public GroupsRecyclerGridAdapter.GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.groups_grid_item_layout, parent, false);
        return new GroupsRecyclerGridAdapter.GroupsViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        if (groupsDataList.get(position).getHighlight() != 0) {
            holder.badge.setVisibility(View.VISIBLE);
            if (groupsDataList.get(position).getHighlight() > 30) {
                holder.badge.setText("30+");
            } else {
                holder.badge.setText(String.valueOf(groupsDataList.get(position).getHighlight()));
            }
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        try {
            Picasso.with(mContext).load(groupsDataList.get(position).getHeaderImage())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.groupImageView);
        } catch (Exception e) {
            holder.groupImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
        }
        holder.groupsNameTextView.setText(groupsDataList.get(position).getTitle());
        if (System.currentTimeMillis() - groupsDataList.get(position).getCreatedAt() * 1000 < AppConstants.DAYS_10_TIMESTAMP) {
//            holder.groupImageView.setLabelBackgroundAlpha(0);
            holder.groupNewLabelImageView.setLabelVisual(true);
        } else {
            holder.groupNewLabelImageView.setLabelVisual(false);
//            holder.groupImageView.setLabelBackgroundAlpha(1);
        }
//        holder.groupsNameTextView.setSelected(position == selectedPosition);
    }

    public class GroupsViewHolder extends RecyclerView.ViewHolder {
        TextView groupsNameTextView, badge;
        ImageView groupImageView, hightLightImageView;
        LabelImageView groupNewLabelImageView;

        GroupsViewHolder(View view) {
            super(view);
            groupImageView = (ImageView) view.findViewById(R.id.groupImageView);
            groupsNameTextView = (TextView) view.findViewById(R.id.groupNameTextView);
            groupNewLabelImageView = (LabelImageView) view.findViewById(R.id.groupNewLabelImageView);
            //  hightLightImageView = (ImageView) view.findViewById(R.id.highlights);
            badge = (TextView) view.findViewById(R.id.badge);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRecyclerItemClick(v, getAdapterPosition(), isMember);
                    // ((SetHighLights) this).setHighLight(getAdapterPosition());
                    //((GroupsFragment) this).setHightlight(getAdapterPosition());
                    groupsDataList.get(getAdapterPosition()).setHighlight(0);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position, boolean isMember);
    }


}