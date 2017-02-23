package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.Topics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 9/2/17.
 */
public class SubSubTopicsListAdapter extends ArrayAdapter<Topics> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Topics> subcategoryList;
    private List<String> selectedTopics = new ArrayList<>();

    public SubSubTopicsListAdapter(Context context, int resource, List<Topics> subcategoryList) {
        super(context, resource, subcategoryList);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.subcategoryList = subcategoryList;
        mContext = context;
    }

    static class ViewHolder {
        TextView itemNameTextView;
        CheckBox subSubTopicCheckBox;
    }

    public void setSubSubTopicsData(List<Topics> subcategoryList) {
        this.subcategoryList = subcategoryList;
    }

    @Override
    public int getCount() {
        return subcategoryList.size();
    }

    ViewHolder holder;

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.sub_sub_topics_filter_item, null);
            holder.itemNameTextView = (TextView) view.findViewById(R.id.itemNameTextView);
            holder.subSubTopicCheckBox = (CheckBox) view.findViewById(R.id.subSubTopicsCheckBox);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.subSubTopicCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                subcategoryList.get(position).setIsSelected(isChecked);
            }
        });

        holder.itemNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subcategoryList.get(position).setIsSelected(!subcategoryList.get(position).isSelected());
                notifyDataSetChanged();
            }
        });

        holder.subSubTopicCheckBox.setChecked(subcategoryList.get(position).isSelected());
        holder.itemNameTextView.setText(subcategoryList.get(position).getDisplay_name());
        return view;
    }

    public List<String> getSelectedTopics() {
        for (int i = 0; i < subcategoryList.size(); i++) {
            if (subcategoryList.get(i).isSelected())
                selectedTopics.add(subcategoryList.get(i).getId());
        }
        return selectedTopics;
    }
}