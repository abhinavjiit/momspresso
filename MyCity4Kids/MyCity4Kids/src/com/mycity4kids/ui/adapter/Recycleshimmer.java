package com.mycity4kids.ui.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycity4kids.R;

public class Recycleshimmer extends RecyclerView.Adapter<Recycleshimmer.Shimmer> {
    Context c;
    //  ArrayList<DraftListResult> articleDataModelsNew;

    public Recycleshimmer(Context c) {
        this.c = c;
    }

    @Override
    public Shimmer onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //   assert inflater != null;
        View row = inflater.inflate(R.layout.shimmer_layout, parent, false);
        return new Shimmer(row);
    }

    @Override
    public void onBindViewHolder(Shimmer holder, int position) {
    }

    /*    if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle())) {
            holder.view.setText(articleDataModelsNew.get(position).getTitle());
        } else {
            holder.view.setText(c.getString(R.string.user_article_draft_untitled_draft));
        }
        if (articleDataModelsNew.get(position).getArticleType() == null) {

            holder.view.setTextColor(Color.BLACK);
        }


    }

    public void setListData(ArrayList<DraftListResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }*/

    @Override
    public int getItemCount() {
        return 2;
        //articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class Shimmer extends RecyclerView.ViewHolder {
        TextView view;

        public Shimmer(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.articletitle);
        }
    }
}
