package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.ExploreTopicsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Hemant.Parmar
 */
public class ParentTopicsGridAdapter extends BaseAdapter {

    private ArrayList<ExploreTopicsModel> topicsList;
    private ArrayList<ExploreTopicsModel> arraylist;
    private LayoutInflater mInflator;
    private String source;
    private boolean videoFlag;

    public ParentTopicsGridAdapter(String source) {
        mInflator = (LayoutInflater) BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.source = source;
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
            holder.durationImageView = (ImageView) view.findViewById(R.id.durationImageView);
            holder.durationTextView = (TextView) view.findViewById(R.id.durationTextView);
            holder.selectorView = view.findViewById(R.id.selectorView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.topicsNameTextView.setText(topicsList.get(position).getDisplay_name().toUpperCase());

        if ("exploreSectionId".equals(topicsList.get(position).getId())) {
            holder.tagsImageView.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.events_card_bg));
        } else {
            //useless backend can't do shit. tired of checking null, empty, json object or json array.
            try {
                Picasso.with(BaseApplication.getAppContext()).load(topicsList.get(position).getExtraData().get(0).getCategoryBackImage().getApp()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                        .fit().into(holder.tagsImageView);
            } catch (Exception e) {
                holder.tagsImageView.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.default_article));
            }
        }

        if (videoFlag) {
            holder.durationImageView.setVisibility(View.VISIBLE);
            holder.durationTextView.setVisibility(View.VISIBLE);
            if (topicsList.get(position).getExtraData() == null || topicsList.get(position).getExtraData().isEmpty()) {
                holder.durationTextView.setText("60");
            } else if (StringUtils.isNullOrEmpty(topicsList.get(position).getExtraData().get(0).getMax_duration())) {
                holder.durationTextView.setText("60");
            } else {
                holder.durationTextView.setText(topicsList.get(position).getExtraData().get(0).getMax_duration());
            }
        } else {
            holder.durationImageView.setVisibility(View.GONE);
            holder.durationTextView.setVisibility(View.GONE);
        }

        if ("search".equals(source)) {
            if (topicsList.get(position).isSelected()) {
                holder.selectorView.setVisibility(View.GONE);
            } else {
                holder.selectorView.setVisibility(View.VISIBLE);
            }
        } else {
            holder.selectorView.setVisibility(View.GONE);
        }

        return view;
    }

    public void setDatalist(ArrayList<ExploreTopicsModel> datalist) {
        this.topicsList = datalist;
        this.arraylist = new ArrayList<ExploreTopicsModel>();
        this.arraylist.addAll(datalist);
    }

    public void setVideoFlag() {
        videoFlag = true;
    }

    class ViewHolder {
        ImageView tagsImageView;
        TextView topicsNameTextView;
        View selectorView;
        ImageView durationImageView;
        TextView durationTextView;
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
