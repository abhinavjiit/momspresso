package com.mycity4kids.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.fragmentdialog.StartDatePicker;
import com.mycity4kids.interfaces.IGetDate;
import com.mycity4kids.models.category.DateValue;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author deepanker.chaudhary
 */
public class DateValueAdapter extends BaseAdapter implements IGetDate {
    private ArrayList<DateValue> mDateList;
    private LayoutInflater mInflator;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;
    private FragmentActivity mContext;
    //private IGetDate iGetDate;
    private HashMap<MapTypeFilter, String> mFilterMap;

    public DateValueAdapter(FragmentActivity pContext, Fragment fragment, ArrayList<DateValue> mDateValue) {
        mInflator = LayoutInflater.from(pContext);
        mDateList = mDateValue;
        mContext = pContext;
        //	this.iGetDate=iGetDate;
        if (fragment == null) {
            mFilterMap = ((BusinessListActivityKidsResources)pContext).mFilterMap;
        } else {
            mFilterMap=((FragmentBusinesslistEvents)fragment).mFilterMap;
        }
    }
    @Override
    public int getCount() {
        return mDateList == null ? 0 : mDateList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDateList.get(position);
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
            view = mInflator.inflate(R.layout.list_item_sub_category, null);
            holder = new ViewHolder();
            holder.mDateTxv = (TextView) view.findViewById(R.id.txvSubCategoryName);
            holder.radioBtn = (RadioButton) view.findViewById(R.id.radioBtnCategory);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.mDateTxv.setText(mDateList.get(position).getValue());
        holder.radioBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (position != mSelectedPosition && mSelectedRB != null) {
                    mSelectedRB.setChecked(false);
                    mDateList.get(mSelectedPosition).setSelected(false);
                }
                mSelectedPosition = position;
                mSelectedRB = (RadioButton) v;
                if (mSelectedRB.isChecked()) {
                    mDateList.get(mSelectedPosition).setSelected(true);
                    String customDate = mDateList.get(mSelectedPosition).getValue();

                    if (!StringUtils.isNullOrEmpty(customDate)) {
                        if (customDate.equalsIgnoreCase("Custom Date")) {
                            StartDatePicker _Picker = new StartDatePicker();
                            _Picker.setDateAction(DateValueAdapter.this);
                            _Picker.show(mContext.getSupportFragmentManager(), "");
                        }
                    }
                }
                addOrRemoveFilters(mDateList);

            }
        });
        if (mSelectedPosition != position) {
            holder.radioBtn.setChecked(false);
        } else {
            holder.radioBtn.setChecked(true);
            if (mSelectedRB != null && holder.radioBtn != mSelectedRB) {
                mSelectedRB = holder.radioBtn;
            }
        }
        return view;
    }


    class ViewHolder {
        TextView mDateTxv;
        RadioButton radioBtn;

    }

    private void addOrRemoveFilters(ArrayList<DateValue> mDateList) {
        mFilterMap.remove(MapTypeFilter.DateValue);
        ArrayList<DateValue> selected_filters = new ArrayList<DateValue>();
        for (int i = 0; i < mDateList.size(); i++) {
            DateValue category = mDateList.get(i);
            if (category.isSelected()) {
                selected_filters.add(category);
            }
        }


        if ((selected_filters != null) && !(selected_filters.isEmpty())) {

            if (selected_filters != null) {
                String dateValue = "";
                for (int i = 0; i < selected_filters.size(); i++) {
                    dateValue += selected_filters.get(i).getKey() + ",";
                }
                String dateFilter = dateValue.substring(0, dateValue.length() - 1);
                mFilterMap.put(MapTypeFilter.DateValue, "&date_by=" + dateFilter);

                // remove from business list
                try
                {
                    (FragmentBusinesslistEvents.mFilterMap).remove(MapTypeFilter.DateValue);
                    (FragmentBusinesslistEvents.mFilterMap).put(MapTypeFilter.DateValue, "&date_by=" + dateFilter);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }




            }
        }
    }

    @Override
    public void getDateValue(String date) {
        mFilterMap.remove("Date");
        if (date != null) {
            mFilterMap.put(MapTypeFilter.DateValue, "&date_by=" + date);
        } else {
            mFilterMap.put(MapTypeFilter.DateValue, "&date_by=" + "");
        }

    }

}
