package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.AllDraftsResponse;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class UserAllDraftsRecyclerAdapter extends RecyclerView.Adapter<UserAllDraftsRecyclerAdapter.UserAllDraftViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<AllDraftsResponse.AllDraftsData.AllDraftsResult> allDraftsList;
    private final float density;
    private DraftRecyclerViewClickListener mListener;

    public UserAllDraftsRecyclerAdapter(Context pContext, DraftRecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<AllDraftsResponse.AllDraftsData.AllDraftsResult> mParentingLists) {
        allDraftsList = mParentingLists;
    }

    @Override
    public UserAllDraftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserAllDraftViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.user_all_draft_list_item, parent, false);
        viewHolder = new UserAllDraftViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserAllDraftViewHolder holder, int position) {

        if (!StringUtils.isNullOrEmpty(allDraftsList.get(position).getTitle())) {
            holder.titleTextView.setText(allDraftsList.get(position).getTitle());
        } else {
            holder.titleTextView.setText(mContext.getString(R.string.user_article_draft_untitled_draft));
        }

        holder.bodyTextView.setText(allDraftsList.get(position).getBody());

        try {
            holder.updatedOnTextView.setText(DateTimeUtils.getKidsDOBNanoMilliTimestamp(String.valueOf(allDraftsList.get(position).getUpdatedTime())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AppConstants.CONTENT_TYPE_SHORT_STORY.equals(allDraftsList.get(position).getContentType())) {
            holder.draftTypeTextView.setText(WordUtils.capitalize(mContext.getString(R.string.article_listing_type_short_story_label)));
            holder.draftTypeImageView.setImageResource(R.drawable.shortstory_grey);
        } else {
            holder.draftTypeTextView.setText(WordUtils.capitalize(mContext.getString(R.string.search_article_label)));
            holder.draftTypeImageView.setImageResource(R.drawable.draft_grey);
        }
    }

    @Override
    public int getItemCount() {
        return allDraftsList == null ? 0 : allDraftsList.size();
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class UserAllDraftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        TextView bodyTextView;
        TextView updatedOnTextView;
        TextView draftTypeTextView;
        ImageView draftTypeImageView;

        public UserAllDraftViewHolder(View itemView, DraftRecyclerViewClickListener listener) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            bodyTextView = (TextView) itemView.findViewById(R.id.bodyTextView);
            updatedOnTextView = (TextView) itemView.findViewById(R.id.updatedOnTextView);
            draftTypeTextView = (TextView) itemView.findViewById(R.id.draftTypeTextView);
            draftTypeImageView = (ImageView) itemView.findViewById(R.id.draftTypeImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onDraftItemClick(v, getAdapterPosition());
        }
    }

    public interface DraftRecyclerViewClickListener {
        void onDraftItemClick(View view, int position);
    }

}
