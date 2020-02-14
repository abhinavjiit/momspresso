package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.SearchTopicResult;
import com.mycity4kids.utils.AppUtils;

import java.util.ArrayList;

/**
 * Created by hemant on 21/7/16.
 */
public class SearchTopicsListingAdapter extends RecyclerView.Adapter<SearchTopicsListingAdapter.TopicViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<SearchTopicResult> articleDataModelsNew;
    private final float density;
    private RecyclerViewClickListener mListener;

    public SearchTopicsListingAdapter(Context pContext, RecyclerViewClickListener listener) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
    }

    public void setListData(ArrayList<SearchTopicResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TopicViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.search_all_topic_item, parent, false);
        viewHolder = new TopicViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        holder.searchTopicTextView.setText(AppUtils.fromHtml(articleDataModelsNew.get(position).getTitle()));
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView searchTopicTextView;

        public TopicViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            searchTopicTextView = (TextView) itemView.findViewById(R.id.searchTopicTextView);
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