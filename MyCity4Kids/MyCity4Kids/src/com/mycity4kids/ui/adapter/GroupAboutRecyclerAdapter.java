package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.GroupResult;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupAboutRecyclerAdapter extends RecyclerView.Adapter<GroupAboutRecyclerAdapter.GroupAboutHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private GroupResult groupData;
    private RecyclerViewClickListener mListener;

    public GroupAboutRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setData(GroupResult groupData) {
        this.groupData = groupData;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public GroupAboutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.group_about_item, parent, false);
        return new GroupAboutHolder(v0);
    }

    @Override
    public void onBindViewHolder(final GroupAboutHolder holder, final int position) {
        holder.groupDescTextView.setText(groupData.getDescription());
        holder.createdTimeTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(groupData.getCreatedAt()));
        if (AppConstants.GROUP_TYPE_OPEN_KEY.equals(groupData.getType())) {
            holder.groupTypeTextView.setText(mContext.getString(R.string.groups_anyone_join));
        } else if (AppConstants.GROUP_TYPE_CLOSED_KEY.equals(groupData.getType())) {
            holder.groupTypeTextView.setText(mContext.getString(R.string.groups_closed_gp));
        } else {
            holder.groupTypeTextView.setText(mContext.getString(R.string.groups_invitation_only_gp));
        }
//        holder.groupTypeTextView.setText("Closed Group");
        if (groupData.getAdminMembers().getData() != null && !groupData.getAdminMembers().getData().isEmpty()) {
            holder.groupAdminTextView.setText(groupData.getAdminMembers().getData().get(0).getUserInfo().getFirstName() + " "
                    + groupData.getAdminMembers().getData().get(0).getUserInfo().getLastName());
        } else {
            holder.groupAdminTextView.setText("");
        }

    }

    public class GroupAboutHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupDescTextView;
        TextView createdTimeTextView;
        TextView groupTypeTextView;
        TextView groupAdminLabelTextView;
        TextView groupAdminTextView;

        GroupAboutHolder(View view) {
            super(view);
            groupDescTextView = (TextView) view.findViewById(R.id.groupDescTextView);
            createdTimeTextView = (TextView) view.findViewById(R.id.createdTimeTextView);
            groupTypeTextView = (TextView) view.findViewById(R.id.groupTypeTextView);
            groupAdminLabelTextView = (TextView) view.findViewById(R.id.groupAdminLabelTextView);
            groupAdminTextView = (TextView) view.findViewById(R.id.groupAdminTextView);

            groupDescTextView.setOnClickListener(this);
            groupAdminTextView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

}
