package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.GroupResult;

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
//        holder.groupImageView.setText(groupsDataList.get(position));
        holder.groupsNameTextView.setText(groupsDataList.get(position).getTitle());
//        holder.groupsNameTextView.setSelected(position == selectedPosition);
    }

    public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupsNameTextView;
        ImageView groupImageView;

        GroupsViewHolder(View view) {
            super(view);
            groupImageView = (ImageView) view.findViewById(R.id.groupImageView);
            groupsNameTextView = (TextView) view.findViewById(R.id.groupNameTextView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRecyclerItemClick(v, getAdapterPosition(), isMember);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition(), isMember);
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position, boolean isMember);
    }

}
