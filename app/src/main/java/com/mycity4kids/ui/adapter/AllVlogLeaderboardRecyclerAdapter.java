package com.mycity4kids.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.AllLeaderboardDataResponse.AllLeaderboardData.AllLeaderboardRankHolder;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.VlogLeaderboardRecyclerAdapter.RecyclerViewClickListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by hemant on 30/5/18.
 */

public class AllVlogLeaderboardRecyclerAdapter extends
        RecyclerView.Adapter<AllVlogLeaderboardRecyclerAdapter.LeaderboardViewHolder> {

    private ArrayList<AllLeaderboardRankHolder> articleDataModelsNew;
    private RecyclerViewClickListener recyclerViewClickListener;

    public AllVlogLeaderboardRecyclerAdapter(RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setListData(ArrayList<AllLeaderboardRankHolder> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LeaderboardViewHolder viewHolder;
        View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_listing_item, parent, false);
        viewHolder = new LeaderboardViewHolder(v0);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LeaderboardViewHolder holder, int position) {
        try {
            Picasso.get().load(articleDataModelsNew.get(position).getProfilePic().getClientAppMin()).
                    placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher)
                    .into(holder.profilePic);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        holder.userName.setText(articleDataModelsNew.get(position).getName());
        holder.userHandle.setText(articleDataModelsNew.get(position).getUser_handle());
        holder.userRank.setText("" + articleDataModelsNew.get(position).getRank());
        holder.viewCount.setText("" + (articleDataModelsNew.get(position).getScore() / 1000) + "K");
        if (articleDataModelsNew.get(position).getUser_id()
                .equals(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {
            holder.rl.setBackgroundColor(ContextCompat.getColor(holder.rl.getContext(), R.color.color_FFF7F8));
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        TextView userName, userHandle, userRank, viewCount;
        ImageView profilePic;
        RelativeLayout rl;

        LeaderboardViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePicImageView);
            userName = itemView.findViewById(R.id.user_name);
            userHandle = itemView.findViewById(R.id.user_handle);
            userRank = itemView.findViewById(R.id.user_rank);
            viewCount = itemView.findViewById(R.id.view_count);
            rl = itemView.findViewById(R.id.rl);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListener.onRecyclerViewItemClick(view, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {

        void onRecyclerViewItemClick(View view, int position);
    }
}