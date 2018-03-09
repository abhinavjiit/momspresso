package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;

import java.util.ArrayList;

/**
 * Created by hemant on 4/12/17.
 */

public class LanguageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ARTICLE = 1;
    public static final int HEADER = 0;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<String> articleDataModelsNew;
    private RecyclerViewClickListener mListener;
    private int selectedPosition;

    public LanguageRecyclerViewAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setNewListData(ArrayList<String> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ARTICLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View v0 = mInflator.inflate(R.layout.language_selection_header, parent, false);
            return new HeaderViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.language_selection_grid_item, parent, false);
            return new LanguageViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
//            addArticleItem((HeaderViewHolder) holder, position);
        } else {
//            addArticleItem((LanguageViewHolder) holder, position);
            ((LanguageViewHolder) holder).languageTextView.setText(articleDataModelsNew.get(position));
            ((LanguageViewHolder) holder).languageTextView.setSelected(position == selectedPosition);
        }
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView languageTextView;

        LanguageViewHolder(View view) {
            super(view);
            languageTextView = (TextView) view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();
                    if (selectedPosition == RecyclerView.NO_POSITION) return;
//                    recyclerViewOnItemClickListener.onItemSelect(itemView, getAdapterPosition()); //Custom listener - in turn calls your highlightButton method
                    mListener.onRecyclerItemClick(v, selectedPosition);
                    notifyDataSetChanged();
                    //call notifyDataSetChanged(); or notifyItemRangeChanged();
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        HeaderViewHolder(View view) {
            super(view);
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
