package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;

import java.util.ArrayList;

/**
 * Created by hemant on 19/7/17.
 */
public class EventSubcategoriesRecyclerAdapter extends RecyclerView.Adapter<EventSubcategoriesRecyclerAdapter.SubcategoriesViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    private RecyclerViewClickListener mListener;
    private int lastSelectedPosition = 0;

    public EventSubcategoriesRecyclerAdapter(Context pContext) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
//        this.mListener = listener;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
    }


    @Override
    public SubcategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.events_subcategories_recycler_item_layout, parent, false);
        return new SubcategoriesViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(SubcategoriesViewHolder holder, int position) {
//        if (position == 0) {
//            holder.eventSubcategoriesTextView.setTextColor(ContextCompat.getColor(mContext, R.color.explore_events_selected_subcategory));
//        } else {
//            holder.eventSubcategoriesTextView.setTextColor(ContextCompat.getColor(mContext, R.color.explore_events_categories_text));
//        }
        holder.eventSubcategoriesTextView.setSelected(lastSelectedPosition == position);

    }

    @Override
    public int getItemCount() {
        return 10;
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class SubcategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eventSubcategoriesTextView;

        SubcategoriesViewHolder(View adView) {
            super(adView);
            eventSubcategoriesTextView = (TextView) itemView.findViewById(R.id.eventSubcategoriesTextView);
            adView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            lastSelectedPosition = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}