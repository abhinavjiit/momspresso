package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.DraftListResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hemant on 19/7/17.
 */
public class UserDraftArticleAdapter extends RecyclerView.Adapter<UserDraftArticleAdapter.UserDraftArticleViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<DraftListResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public UserDraftArticleAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<DraftListResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public UserDraftArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserDraftArticleViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.user_draft_article_list_item, parent, false);
        viewHolder = new UserDraftArticleViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserDraftArticleViewHolder holder, int position) {

        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle())) {
            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
        } else {
            holder.txvArticleTitle.setText("Untitled Draft");
        }

        if (articleDataModelsNew.get(position).getArticleType() == null) {
            holder.txvStatus.setVisibility(View.INVISIBLE);
            holder.rootLayout.setBackgroundColor(Color.WHITE);
            holder.rootLayout.setClickable(false);
            holder.popupButton.setClickable(true);
            holder.txvArticleTitle.setTextColor(Color.BLACK);
        } else {
            switch (articleDataModelsNew.get(position).getArticleType()) {
                case "0": {
                    holder.txvStatus.setVisibility(View.INVISIBLE);
                    holder.rootLayout.setClickable(false);
                    holder.deleteDraftImageView.setVisibility(View.VISIBLE);
                    break;
                }
                case "1": {
                    holder.txvStatus.setVisibility(View.VISIBLE);
                    holder.txvStatus.setText(mContext.getString(R.string.user_article_draft_status_pending_approval));
                    holder.rootLayout.setClickable(true);
                    holder.deleteDraftImageView.setVisibility(View.INVISIBLE);
                    break;
                }
                case "2": {
                    holder.txvStatus.setVisibility(View.VISIBLE);
                    holder.txvStatus.setText(mContext.getString(R.string.user_article_draft_status_unapproved));
                    holder.rootLayout.setClickable(false);
                    holder.deleteDraftImageView.setVisibility(View.INVISIBLE);
                    break;
                }
                case "4": {
                    holder.txvStatus.setVisibility(View.VISIBLE);
                    holder.txvStatus.setText(mContext.getString(R.string.user_article_draft_status_unpublished));
                    holder.rootLayout.setClickable(false);
                    holder.deleteDraftImageView.setVisibility(View.VISIBLE);
                    break;
                }
                default:
                    holder.txvStatus.setVisibility(View.INVISIBLE);
                    holder.rootLayout.setClickable(false);
                    holder.deleteDraftImageView.setVisibility(View.VISIBLE);
                    break;
            }
        }

        try {
            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
            calendar1.setTimeInMillis(articleDataModelsNew.get(position).getUpdatedTime() * 1000);

            Long diff = System.currentTimeMillis() - articleDataModelsNew.get(position).getUpdatedTime() * 1000;
            if (diff / (1000 * 60 * 60) > 24 && !sdf.format(System.currentTimeMillis()).equals(sdf.format((articleDataModelsNew.get(position).getUpdatedTime() * 1000)))) {
                holder.txvPublishDate.setText(mContext.getString(R.string.user_article_draft_saved_on) + " " + DateTimeUtils.getDateFromTimestamp(articleDataModelsNew.get(position).getUpdatedTime()));
            } else {
                holder.txvPublishDate.setText(mContext.getString(R.string.user_article_draft_saved_on) + " " + sdf1.format(calendar1.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class UserDraftArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txvArticleTitle;
        TextView txvPublishDate;
        TextView txvStatus;
        TextView editDraftTextView;
        ImageView deleteDraftImageView;
        RelativeLayout rootLayout;
        View popupButton;

        public UserDraftArticleViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.txvArticleTitle);
            txvPublishDate = (TextView) itemView.findViewById(R.id.txvPublishDate);
            txvStatus = (TextView) itemView.findViewById(R.id.txvStatus);
            editDraftTextView = (TextView) itemView.findViewById(R.id.editDraftTextView);
            deleteDraftImageView = (ImageView) itemView.findViewById(R.id.deleteDraftImageView);
            rootLayout = (RelativeLayout) itemView.findViewById(R.id.rootLayout);
            deleteDraftImageView.setOnClickListener(this);
            editDraftTextView.setOnClickListener(this);
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