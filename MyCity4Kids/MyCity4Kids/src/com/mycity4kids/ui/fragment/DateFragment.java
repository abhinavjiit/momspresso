package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.DateTable;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.models.category.DateValue;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.DateValueAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class DateFragment extends Fragment implements OnClickListener {

    private int categoryId, businessOrEvent;
    private IFilter ifilter;
    private ArrayList<DateValue> mDateValue = null;
    private String dateFromPicker = null;
    private Fragment parentFragment;
    DateValueAdapter _adapter;
    Context mcontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_group, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Listing Date Filter", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        categoryId = getArguments().getInt(Constants.CATEGORY_KEY, 0);
        businessOrEvent = getArguments().getInt(Constants.PAGE_TYPE, 0);
        DateTable _table = new DateTable((BaseApplication) getActivity().getApplication());
        mDateValue = _table.getAllDateValues(categoryId);
        if (mDateValue != null && mDateValue.size() != 0) {
            ListView mDateValueListView = (ListView) view.findViewById(R.id.subAgeGroupListView);
            _adapter = new DateValueAdapter(getActivity(), parentFragment, mDateValue);
            mDateValueListView.setAdapter(_adapter);
            view.findViewById(R.id.list_apply).setOnClickListener(this);
            view.findViewById(R.id.list_reset).setOnClickListener(this);
            view.findViewById(R.id.cancel).setOnClickListener(this);
        } else {
            view.findViewById(R.id.layout_age_non_blank).setVisibility(View.GONE);
            view.findViewById(R.id.txt_no_data).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.txt_no_data)).setText(getString(R.string.no_age_group));
        }
        return view;
    }

    public void setAction(Fragment parentFragment) {
        ifilter = (IFilter) parentFragment;
        this.parentFragment = parentFragment;
    }

    public void setAction(Context mcontext) {
        ifilter = (IFilter) mcontext;
        this.mcontext = mcontext;
    }

    private void manageFilter() {
        try {
            /**
             * final CR-deepanker.chaudhary
             */
            HashMap<MapTypeFilter, String> filterMap;
            if (parentFragment == null) {
                // kids resources flow
                filterMap = ((BusinessListActivityKidsResources) getActivity()).mFilterMap;
            } else {
                filterMap = FragmentBusinesslistEvents.mFilterMap;
                System.out.println("filter map " + filterMap);
            }
            if (filterMap != null && !filterMap.isEmpty() && ifilter != null) {
                ifilter.doNewFilter(businessOrEvent);
            } else {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).showToast(getActivity().getResources().getString(R.string.please_select));
                } else {
                    ((BusinessListActivityKidsResources) getActivity()).showToast(getActivity().getResources().getString(R.string.please_select));
                }
            }
        } catch (Exception e) {
            Log.i("dateValue", e.getMessage());
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_apply:
                manageFilter();
                break;
            case R.id.list_reset:
                ifilter.reject(businessOrEvent);
                break;
            case R.id.cancel:
                ifilter.cancel(businessOrEvent);
                break;
            default:
                break;
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        categoryId = args.getInt(Constants.CATEGORY_KEY);
        businessOrEvent = args.getInt(Constants.PAGE_TYPE);
    }
    /*@Override
    public void getDateValue(String date) {
		dateFromPicker=date;
		ifilter.doFilter(FilterType.DateValue, dateFromPicker,businessOrEvent);
	}*/

}
