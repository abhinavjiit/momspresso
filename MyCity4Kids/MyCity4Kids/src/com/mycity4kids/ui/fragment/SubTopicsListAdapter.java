package com.mycity4kids.ui.fragment;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.Topics;

import java.util.List;

/**
 * Created by hemant on 9/2/17.
 */
public class SubTopicsListAdapter extends ArrayAdapter<Topics> {

    private LayoutInflater mInflater;
    private Context mContext;
    private static final int TYPE_REPLY_LEVEL_ONE = 0;
    private static final int TYPE_REPLY_LEVEL_TWO = 1;
    private static final int TYPE_MAX_COUNT = 2;
    private List<Topics> subcategoryList;
    private int selectedRow = 0;

    public SubTopicsListAdapter(Context context, int resource, List<Topics> subcategoryList) {
        super(context, resource, subcategoryList);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.subcategoryList = subcategoryList;
        mContext = context;
    }

    static class ViewHolder {
        TextView itemNameTextView;
    }

    ViewHolder holder;

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.sub_topics_filter_item, null);
            holder.itemNameTextView = (TextView) view.findViewById(R.id.itemNameTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.itemNameTextView.setText(subcategoryList.get(position).getDisplay_name());
        if (selectedRow == position) {
            holder.itemNameTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
        } else {
            holder.itemNameTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.home_bg));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        return view;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
        notifyDataSetChanged();
    }
}
