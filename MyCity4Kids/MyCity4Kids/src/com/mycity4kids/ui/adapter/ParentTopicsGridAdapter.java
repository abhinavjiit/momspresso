package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Hemant.Parmar
 */
public class ParentTopicsGridAdapter extends BaseAdapter {

    private ArrayList<ExploreTopicsModel> topicsList;
    private ArrayList<ExploreTopicsModel> arraylist;
    private Context mContext;
    private LayoutInflater mInflator;
    private final float density;

    public ParentTopicsGridAdapter(Context pContext) {
        mContext = pContext;
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return topicsList == null ? 0 : topicsList.size();
    }

    @Override
    public Object getItem(int position) {
        return topicsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflator.inflate(R.layout.explore_topics_grid_item, null);
            holder.tagsImageView = (ImageView) view.findViewById(R.id.tagImageView);
            holder.topicsNameTextView = (TextView) view.findViewById(R.id.topicsNameTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.topicsNameTextView.setText(topicsList.get(position).getDisplay_name());

        //useless backend can't do shit. tired of checking null, empty, json object or json array.
        try {
            Picasso.with(mContext).load(topicsList.get(position).getExtraData().get(0).getCategoryBackImage().getApp()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(holder.tagsImageView);
        } catch (Exception e) {
            holder.tagsImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
        }
//        if (!StringUtils.isNullOrEmpty(topicsList.get(position).getExtraData().get(0).getCategoryBackImage().getApp())) {
//
//        } else {
//
//        }

//        if (topicsList.get(position).isSelected()) {
//            holder.selectedLayerLayout.setVisibility(View.VISIBLE);
//        } else {
//            holder.selectedLayerLayout.setVisibility(View.INVISIBLE);
//        }

//        holder.tagsImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                for (int i = 0; i < topicsList.size(); i++) {
//                    topicsList.get(i).setSelected(false);
//                }
//                topicsList.get(position).setSelected(true);
//                iTagImageSelect.onTagImageSelected(topicsList.get(position).getImageUrl().getClientApp());
//                notifyDataSetChanged();
//            }
//        });

        return view;
    }

    public void setDatalist(ArrayList<ExploreTopicsModel> datalist) {
        this.topicsList = datalist;
        this.arraylist = new ArrayList<ExploreTopicsModel>();
        this.arraylist.addAll(datalist);
    }

    class ViewHolder {
        ImageView tagsImageView;
        TextView topicsNameTextView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase();
        topicsList.clear();
        if (charText.length() == 0) {
            topicsList.addAll(arraylist);
        } else {
            for (ExploreTopicsModel wp : arraylist) {
                if (wp.getDisplay_name().toLowerCase()
                        .contains(charText)) {
                    topicsList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
