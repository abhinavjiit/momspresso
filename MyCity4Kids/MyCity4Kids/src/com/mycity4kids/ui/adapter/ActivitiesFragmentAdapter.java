package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.models.category.Activities;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivitiesFragmentAdapter extends BaseAdapter implements Filterable {
        private ArrayList<Activities> mActivitiesList;
        private LayoutInflater mInflator;
        private HashMap<MapTypeFilter, String> mFilterMap;
    ArrayList<Integer> checkboxlist;
    boolean[] checkBoxState;
    private ItemFilter mFilter = new ItemFilter();
    private ArrayList<Activities> filteredData;

    public ActivitiesFragmentAdapter(Context pContext, Fragment fragment, ArrayList<Activities> pActivitiesList) {
        mInflator = LayoutInflater.from(pContext);
        mActivitiesList = pActivitiesList;
        filteredData = pActivitiesList;
        if (fragment == null) {
            mFilterMap = ((BusinessListActivityKidsResources) pContext).mFilterMap;
        } else {
            mFilterMap = FragmentBusinesslistEvents.mFilterMap;
        }
        checkboxlist = new ArrayList<>();
        checkBoxState = new boolean[mActivitiesList.size()];
    }

    public void setList(ArrayList<Activities> pActivitiesList) {
        mActivitiesList = pActivitiesList;

    }

    /*@Override
    public int getCount() {
        return mActivitiesList == null ? 0 : mActivitiesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mActivitiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }*/
    public int getCount() {
        return filteredData == null ? 0 : filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.list_item_with_check_box, null);
            holder = new ViewHolder();
            holder.lnrRoot = (LinearLayout) view.findViewById(R.id.lnrRoot);
            holder.mActivityName = (TextView) view.findViewById(R.id.txvAgeGroupName);
            holder.mCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
            //Log.d("check", "isChecked before " + holder.mCheckBox.isChecked());
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //Log.d("check", "checkBoxState[position]  " + checkBoxState[position]);
        //holder.mCheckBox.setChecked(checkBoxState[position]);
        //holder.mCheckBox.setChecked(true);

        //Log.d("check", "isChecked " + holder.mCheckBox.isChecked());
		/*if(checkboxlist.size()>0)
		{
			for(int i=0;i<checkboxlist.size();i++) {
				Log.d("check","checkbox list item "+checkboxlist.get(i));
				Log.d("check","checkbox list position "+position);
				if (position == checkboxlist.get(i))
					holder.mCheckBox.setChecked(true);
			}
		}*/
        holder.mActivityName.setText(filteredData.get(position).getName());
        holder.mCheckBox.setChecked(filteredData.get(position).isSelected());
        //holder.mCheckBox.setTag(mActivitiesList.get(position));

        holder.mCheckBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                CheckBox chkBok = (CheckBox) v;
                filteredData.get(position).setSelected(chkBok.isChecked());
                for (Activities activities : mActivitiesList) {
                    if (activities.equals(filteredData.get(position))) {
                        activities.setSelected(filteredData.get(position).isSelected());
                        break;
                    }
                }
                //notifyDataSetChanged();
			/*	Activities activity=(Activities)chkBok.getTag();
				activity.setSelected(chkBok.isChecked());
				notifyDataSetChanged();*/

                addOrRemoveFilters(mActivitiesList);
            }
        });


        return view;
    }


    class ViewHolder {
        LinearLayout lnrRoot;
        TextView mActivityName;
        CheckBox mCheckBox;


    }

    private void addOrRemoveFilters(ArrayList<Activities> mActivitiesList) {
        mFilterMap.remove(MapTypeFilter.Activities);
        ArrayList<Activities> selected_filters = new ArrayList<Activities>();
        for (int i = 0; i < mActivitiesList.size(); i++) {
            Activities activity = mActivitiesList.get(i);
            if (activity.isSelected()) {
                selected_filters.add(activity);
            }
        }
        if ((selected_filters != null) && !(selected_filters.isEmpty())) {

            if (selected_filters != null) {
                String activityStr = "";
                for (int i = 0; i < selected_filters.size(); i++) {
                    activityStr += selected_filters.get(i).getId() + ",";
                }
                String finalString = activityStr.substring(0, activityStr.length() - 1);

                mFilterMap.put(MapTypeFilter.Activities, "&activities=" + finalString);

                // add in list
                // remove from business list
                try {
                    (FragmentBusinesslistEvents.mFilterMap).remove(MapTypeFilter.Activities);
                    (FragmentBusinesslistEvents.mFilterMap).put(MapTypeFilter.Activities, "&activities=" + finalString);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            //String filterString = constraint.toString().toLowerCase();
            String filterString = constraint.toString();
            FilterResults results = new FilterResults();

            final List<Activities> list = mActivitiesList;

            int count = list.size();
            final ArrayList<Activities> nlist = new ArrayList<Activities>(count);

            if (!StringUtils.isNullOrEmpty(filterString)) {
                for (Activities activities : mActivitiesList) {
                    if (activities.getName().toLowerCase().startsWith(filterString.toLowerCase())) {
                        nlist.add(activities);
                    }
                }
            } else {
                nlist.addAll(mActivitiesList);
            }

//			String filterableString ;
//         Log.d("check","filterString "+filterString);
//			for (int i = 0; i < count; i++) {
//				filterableString = list.get(i).getName();
//				if (filterString.equalsIgnoreCase((String)filterableString.subSequence(0,filterString.length()))){
//					Activities activityModel=new Activities();
//					activityModel.setId(list.get(i).getId());
//					activityModel.setName(list.get(i).getName());
//					nlist.add(activityModel);
//				}
//			}

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Activities>) results.values;
            Log.d("check", "constraint " + constraint);
            Log.d("check", "results.values " + results.values);
            notifyDataSetChanged();
        }

    }
}

