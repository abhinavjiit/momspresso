package com.mycity4kids.ui.adapter;

import android.os.Handler;
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
import com.mycity4kids.models.response.LeaderboardDataResponse.LeaderboardData.LeaderBoradRank;
import com.mycity4kids.preference.SharedPrefUtils;
import com.squareup.picasso.Picasso;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

/**
 * Created by hemant on 30/5/18.
 */

public class VlogLeaderboardRecyclerAdapter extends
        RecyclerView.Adapter<VlogLeaderboardRecyclerAdapter.LeaderboardViewHolder> {

    private LeaderBoradRank articleDataModelsNew;
    private SimpleTooltip simpleTooltip;
    private Handler handler;
    private RecyclerViewClickListener recyclerViewClickListener;

    public VlogLeaderboardRecyclerAdapter(RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setListData(LeaderBoradRank mParentingLists) {
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
            Picasso.get().load(articleDataModelsNew.ranks.get(position).getProfilePic().getClientAppMin()).
                    placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher)
                    .into(holder.profilePic);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        holder.userName.setText(articleDataModelsNew.ranks.get(position).getName());
        holder.userHandle.setText(articleDataModelsNew.ranks.get(position).getUser_handle());
        holder.userRank.setText("" + articleDataModelsNew.ranks.get(position).getRank());
        holder.viewCount.setText("" + (articleDataModelsNew.ranks.get(position).getScore() / 1000) + "K");
        if (articleDataModelsNew.ranks.get(position).getUser_id()
                .equals(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {
            holder.rl.setBackgroundColor(ContextCompat.getColor(holder.rl.getContext(), R.color.color_FFF7F8));
            /*int rankDiff =
                    articleDataModelsNew.ranks.get(position).getRank() - articleDataModelsNew.ranks.get(position)
                            .getYesterday_rank();
            if (articleDataModelsNew.ranks.get(position).getRank() < articleDataModelsNew.ranks.get(position)
                    .getYesterday_rank()) {
                tooltipForShare(holder.rl, mContext.getString(R.string.rank_up, rankDiff));
            } else if (articleDataModelsNew.ranks.get(position).getRank() > articleDataModelsNew.ranks.get(position)
                    .getYesterday_rank()) {
                tooltipForShare(holder.rl, mContext.getString(R.string.rank_down, rankDiff));
            } else {
                tooltipForShare(holder.rl, mContext.getString(R.string.rank_same));
            }*/
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.ranks.size();
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

    private void tooltipForShare(View view, String msg) {
//        simpleTooltip = new SimpleTooltip.Builder(mContext)
//                .anchorView(view)
//                .backgroundColor(mContext.getResources().getColor(R.color.app_blue))
//                .text(msg)
//                .textColor(mContext.getResources().getColor(R.color.white_color))
//                .arrowColor(mContext.getResources().getColor(R.color.app_blue))
//                .gravity(Gravity.END)
//                .arrowWidth(60)
//                .arrowHeight(20)
//                .animated(false)
//                .focusable(true)
//                .transparentOverlay(true)
//                .build();
//        simpleTooltip.show();
        /*handler = new Handler();
        handler.postDelayed(() -> {
            if (simpleTooltip.isShowing()) {
                simpleTooltip.dismiss();
            }
        }, 30000);*/
    }

    public interface RecyclerViewClickListener {

        void onRecyclerViewItemClick(View view, int position);
    }
}