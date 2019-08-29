package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.DraftListResult;

import java.util.ArrayList;

public class Recyclenormal extends RecyclerView.Adapter<Recyclenormal.Shimmer> {
    Context c;
    ArrayList<DraftListResult> articleDataModelsNew;

    public Recyclenormal(Context c) {
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
        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle())) {
            holder.article.setText(articleDataModelsNew.get(position).getTitle());
        } else {
            holder.article.setText(c.getString(R.string.user_article_draft_untitled_draft));
        }
        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getBody())) {
            holder.body.setText(articleDataModelsNew.get(position).getBody());
        } else {
            holder.body.setText(c.getString(R.string.user_article_draft_untitled_draft));
        }


    }

    public void setListData(ArrayList<DraftListResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class Shimmer extends RecyclerView.ViewHolder {
        TextView article;
        TextView body;

        public Shimmer(View itemView) {
            super(itemView);
            article = (TextView) itemView.findViewById(R.id.articletitle);
            body = (TextView) itemView.findViewById(R.id.articlebody);
        }
    }
}
