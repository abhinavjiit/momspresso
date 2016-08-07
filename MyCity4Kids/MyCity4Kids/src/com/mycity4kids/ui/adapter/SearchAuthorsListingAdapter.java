package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.SearchAuthorResult;
import com.mycity4kids.ui.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 19/4/16.
 */
public class SearchAuthorsListingAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<SearchAuthorResult> mDatalist;
    Boolean newChanges = false;

    public SearchAuthorsListingAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setNewListData(ArrayList<SearchAuthorResult> mDatalist) {
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
            convertView = mLayoutInflater.inflate(R.layout.search_author_list_item, null);
            viewholder = new Viewholder();
            viewholder.authorImageView = (ImageView) convertView.findViewById(R.id.authorImageView);
            viewholder.authorNameTextView = (TextView) convertView.findViewById(R.id.authorNameTextView);
            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }

        if (null != mDatalist.get(position).getProfile_image() && !StringUtils.isNullOrEmpty(mDatalist.get(position).getProfile_image().getClientApp())) {
            try {
                Picasso.with(mContext).load(mDatalist.get(position).getProfile_image().getClientApp()).placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                        .transform(new CircleTransformation()).into(viewholder.authorImageView);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(mContext).load(R.drawable.default_commentor_img).transform(new CircleTransformation()).into(viewholder.authorImageView);
            }
        } else {
            Picasso.with(mContext).load(R.drawable.default_commentor_img).transform(new CircleTransformation()).into(viewholder.authorImageView);
        }
        viewholder.authorNameTextView.setText(Html.fromHtml(mDatalist.get(position).getFirst_name() + " " + mDatalist.get(position).getLast_name()));
        return convertView;
    }
}
