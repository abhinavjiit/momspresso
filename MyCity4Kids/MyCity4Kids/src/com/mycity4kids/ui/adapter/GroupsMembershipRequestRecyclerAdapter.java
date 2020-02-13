package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.GroupsMembershipResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsMembershipRequestRecyclerAdapter extends RecyclerView.Adapter<GroupsMembershipRequestRecyclerAdapter.MembersViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<GroupsMembershipResult> membersDataList;
    private GroupsMembershipRequestRecyclerAdapter.RecyclerViewClickListener mListener;
    private int selectedPosition;
    private boolean isMember, isFullList;

    public GroupsMembershipRequestRecyclerAdapter(Context pContext, GroupsMembershipRequestRecyclerAdapter.RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
        this.isMember = isMember;
        this.isFullList = isFullList;
    }

    public void setData(ArrayList<GroupsMembershipResult> membersDataList) {
        this.membersDataList = membersDataList;
    }


    @Override
    public GroupsMembershipRequestRecyclerAdapter.MembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.group_membership_request_recycler_item, parent, false);
        return new GroupsMembershipRequestRecyclerAdapter.MembersViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(MembersViewHolder holder, int position) {

//        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getThumbMax())) {
//            Picasso.get().load(articleDataModelsNew.get(position).getImageUrl().getThumbMax())
//                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
//        } else {
//            holder.articleImageView.setBackgroundResource(R.drawable.default_article);
//        }
        holder.memberNameTextView.setText(membersDataList.get(position).getUserInfo().getFirstName());
        holder.dateTextView.setText(DateTimeUtils.getDateTimeFromTimestamp(membersDataList.get(position).getCreatedAt()));
        try {
            Picasso.get().load(membersDataList.get(position).getUserInfo().getProfilePicUrl().getClientApp())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.memberImageView);
        } catch (Exception e) {
            holder.memberImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_commentor_img));
        }
    }

    @Override
    public int getItemCount() {
        return membersDataList.size();
    }

    public class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView memberImageView;
        TextView memberNameTextView;
        TextView dateTextView;
        TextView acceptTextView;
        TextView rejectTextView;

        MembersViewHolder(View view) {
            super(view);
            memberImageView = (ImageView) view.findViewById(R.id.memberImageView);
            memberNameTextView = (TextView) view.findViewById(R.id.memberNameTextView);
            dateTextView = (TextView) view.findViewById(R.id.dateTextView);
            acceptTextView = (TextView) view.findViewById(R.id.acceptTextView);
            rejectTextView = (TextView) view.findViewById(R.id.rejectTextView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRecyclerItemClick(v, getAdapterPosition(), isMember);
                    notifyDataSetChanged();
                }
            });

            acceptTextView.setOnClickListener(this);
            rejectTextView.setOnClickListener(this);
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
