package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.BadgeListResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BadgeListGridAdapter extends BaseAdapter {

    private ArrayList<BadgeListResponse.BadgeListData.BadgeListResult> badgeList=new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflator;
    private final float density;
    private BadgeSelect badgeSelect;

    public BadgeListGridAdapter(Context pContext) {
        mContext = pContext;
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        badgeSelect = (BadgeSelect) pContext;
    }

    @Override
    public int getCount() {
        return badgeList == null ? 0 : badgeList.size();
    }

    @Override
    public Object getItem(int position) {
        return badgeList.get(position);
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
            view = mInflator.inflate(R.layout.badge_item_cell, null);
            holder.badgeLayout = view.findViewById(R.id.badge_layout);
            holder.badgeImageView = view.findViewById(R.id.badgeImageView);
            holder.badgeName = view.findViewById(R.id.badgeName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        try {
            if (!StringUtils.isNullOrEmpty(badgeList.get(position).getBadge_image_url())) {
                Picasso.with(mContext).load(badgeList.get(position).getBadge_image_url()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                        .fit().into(holder.badgeImageView);
            } else {
                holder.badgeImageView.setBackgroundResource(R.drawable.article_default);
            }
        } catch (Exception e) {
            holder.badgeImageView.setBackgroundResource(R.drawable.article_default);
        }

        holder.badgeName.setText("" + badgeList.get(position).getBadge_name());

        holder.badgeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                badgeSelect.onBadgeSelected(badgeList.get(position).getBadge_image_url(), badgeList.get(position).getBadge_sharing_url(), position);
            }
        });

        return view;
    }

    public void setDatalist(ArrayList<BadgeListResponse.BadgeListData.BadgeListResult> datalist) {
       badgeList = datalist;
    }

    class ViewHolder {
        RelativeLayout badgeLayout;
        ImageView badgeImageView;
        TextView badgeName;
    }

    public interface BadgeSelect {
        void onBadgeSelected(String image_url, String share_url, int position);
    }

}
