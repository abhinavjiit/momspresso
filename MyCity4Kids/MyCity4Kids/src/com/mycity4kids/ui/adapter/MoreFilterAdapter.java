package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.models.category.Filters;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;

import java.util.ArrayList;
import java.util.HashMap;

public class MoreFilterAdapter extends BaseAdapter {
    private ArrayList<Filters> mMoreFilterList;
    private LayoutInflater mInflator;
    private HashMap<MapTypeFilter, String> mFilterMap;

    public MoreFilterAdapter(Context pContext, Fragment fragment, ArrayList<Filters> mFilterList) {
        mInflator = LayoutInflater.from(pContext);
        mMoreFilterList = mFilterList;
        if (fragment == null) {
            mFilterMap = ((BusinessListActivityKidsResources)pContext).mFilterMap;
        } else {
            mFilterMap=((FragmentBusinesslistEvents)fragment).mFilterMap;
        }
    }
    @Override
    public int getCount() {
        return mMoreFilterList == null ? 0 : mMoreFilterList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMoreFilterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.list_item_with_check_box, null);
            holder = new ViewHolder();
            holder.mFilterName = (TextView) view.findViewById(R.id.txvAgeGroupName);
            holder.mCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.mFilterName.setText(mMoreFilterList.get(position).getValue());
        holder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMoreFilterList.get(position).setSelected(isChecked);
                addOrRemoveFilters(mMoreFilterList);

            }
        });
        return view;
    }


    class ViewHolder {
        TextView mFilterName;
        CheckBox mCheckBox;


    }

    private void addOrRemoveFilters(ArrayList<Filters> mMoreFilterList) {
        mFilterMap.remove(MapTypeFilter.More);
        ArrayList<Filters> selected_filters = new ArrayList<Filters>();
        for (int i = 0; i < mMoreFilterList.size(); i++) {
            Filters moreFiler = mMoreFilterList.get(i);
            if (moreFiler.isSelected()) {
                selected_filters.add(moreFiler);
            }
        }
        if ((selected_filters != null) && !(selected_filters.isEmpty())) {
            if (selected_filters != null) {
                String moreFltr = "";
                for (int i = 0; i < selected_filters.size(); i++) {
                    moreFltr += selected_filters.get(i).getKey() + ",";
                }
                String moreFiler = moreFltr.substring(0, moreFltr.length() - 1);
                mFilterMap.put(MapTypeFilter.More, "&more=" + moreFiler);
            }
        }
    }
}
