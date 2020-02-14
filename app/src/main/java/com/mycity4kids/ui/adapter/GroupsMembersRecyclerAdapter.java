package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.GroupsMembershipResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsMembersRecyclerAdapter extends RecyclerView.Adapter<GroupsMembersRecyclerAdapter.MembersViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupsMembershipResult> membersDataList;
    private GroupsMembersRecyclerAdapter.RecyclerViewClickListener mListener;

    public GroupsMembersRecyclerAdapter(Context pContext, GroupsMembersRecyclerAdapter.RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setData(ArrayList<GroupsMembershipResult> membersDataList) {
        this.membersDataList = membersDataList;
    }


    @Override
    public GroupsMembersRecyclerAdapter.MembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.group_members_recycler_item, parent, false);
        return new GroupsMembersRecyclerAdapter.MembersViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(MembersViewHolder holder, int position) {
        holder.memberNameTextView.setText(membersDataList.get(position).getUserInfo().getFirstName());
        try {
            Picasso.get().load(membersDataList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.memberImageView);
        } catch (Exception e) {
            holder.memberImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_commentor_img));
        }

        if (membersDataList.get(position).getIsAdmin() == 1) {
            holder.memberOptionImageView.setVisibility(View.GONE);
        } else {
            holder.memberOptionImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return membersDataList.size();
    }

    public class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView memberImageView;
        TextView memberNameTextView;
        ImageView memberOptionImageView;

        MembersViewHolder(View view) {
            super(view);
            memberImageView = (ImageView) view.findViewById(R.id.memberImageView);
            memberOptionImageView = (ImageView) view.findViewById(R.id.memberOptionImageView);
            memberNameTextView = (TextView) view.findViewById(R.id.memberNameTextView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRecyclerItemClick(v, getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            memberOptionImageView.setOnClickListener(this);
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
