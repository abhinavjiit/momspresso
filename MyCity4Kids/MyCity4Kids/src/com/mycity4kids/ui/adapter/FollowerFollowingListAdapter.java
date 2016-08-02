package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowerFollowingListAdapter extends BaseAdapter {

    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<FollowersFollowingResult> mDataList;
    private final float density;

    public FollowerFollowingListAdapter(Context mContext) {
        density = mContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = mContext;
    }

    public void setData(ArrayList<FollowersFollowingResult> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView authorImageView;
        TextView authorNameTextView;
        TextView followTextView;
        TextView followingTextView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.follower_following_list_item, null);
            holder = new ViewHolder();
            holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
            holder.authorNameTextView = (TextView) view.findViewById(R.id.authorNameTextView);
            holder.followTextView = (TextView) view.findViewById(R.id.followTextView);
            holder.followingTextView = (TextView) view.findViewById(R.id.followingTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.authorNameTextView.setText(mDataList.get(position).getFirstName() + mDataList.get(position).getLastName());

        if (!StringUtils.isNullOrEmpty(mDataList.get(position).getProfilePicUrl())) {
            try {
                Picasso.with(mContext).load(mDataList.get(position).getProfilePicUrl()).into(holder.authorImageView);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.with(mContext).load(R.drawable.default_commentor_img).into(holder.authorImageView);
            }
        } else {
            Picasso.with(mContext).load(R.drawable.default_commentor_img).into(holder.authorImageView);
        }
        if (mDataList.get(position).getIsFollowed() == 0) {
            holder.followingTextView.setVisibility(View.VISIBLE);
            holder.followTextView.setVisibility(View.INVISIBLE);
        } else {
            holder.followingTextView.setVisibility(View.INVISIBLE);
            holder.followTextView.setVisibility(View.VISIBLE);
        }

        holder.followingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Unfollow", "Unfollow");
                holder.followingTextView.setVisibility(View.INVISIBLE);
                holder.followTextView.setVisibility(View.VISIBLE);
            }
        });

        holder.followTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Follow", "Follow");
                holder.followingTextView.setVisibility(View.VISIBLE);
                holder.followTextView.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }
}
