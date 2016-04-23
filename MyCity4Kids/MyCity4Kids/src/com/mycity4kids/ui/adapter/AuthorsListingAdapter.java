package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 19/4/16.
 */
public class AuthorsListingAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<BlogItemModel> mDatalist;
    Boolean newChanges = false;

    public AuthorsListingAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setNewListData(ArrayList<BlogItemModel> mDatalist) {
        this.mDatalist = mDatalist;
    }

    static class Viewholder {
        TextView authorNameTextView;
        ImageView authorImageView;
    }

    @Override
    public int getCount() {
        return mDatalist == null ? 0 : mDatalist.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Viewholder viewholder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.author_list_item, null);
            viewholder = new Viewholder();
            viewholder.authorImageView = (ImageView) convertView.findViewById(R.id.authorImageView);
            viewholder.authorNameTextView = (TextView) convertView.findViewById(R.id.authorNameTextView);
            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }

        if (!StringUtils.isNullOrEmpty(mDatalist.get(position).getProfile_image())) {
            Picasso.with(mContext).load(mDatalist.get(position).getProfile_image()).placeholder(R.drawable.article_default).into(viewholder.authorImageView);
        } else {
            viewholder.authorImageView.setBackgroundResource(R.drawable.article_default);
        }
        viewholder.authorNameTextView.setText(mDatalist.get(position).getFirst_name() + " " + mDatalist.get(position).getLast_name());

        return convertView;
    }
}
