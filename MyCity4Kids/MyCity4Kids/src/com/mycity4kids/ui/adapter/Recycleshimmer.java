package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.DraftListResult;

import java.util.ArrayList;
import java.util.zip.Inflater;

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
