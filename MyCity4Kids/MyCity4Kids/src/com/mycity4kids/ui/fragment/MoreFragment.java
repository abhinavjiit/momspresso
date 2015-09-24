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
import com.mycity4kids.dbtable.FilterTable;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.models.category.Filters;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.MoreFilterAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MoreFragment extends Fragment implements OnClickListener {

	private ListView mMoreListView;
	int categoryId,businessOrEvent;
	private IFilter ifilter;
	private ArrayList<Filters> mFilterList = null ;
	private Fragment parentFragment;
	MoreFilterAdapter _adapter;
	Context mcontext;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more_group , container, false);
		categoryId=getArguments().getInt(Constants.CATEGORY_KEY);
		businessOrEvent=getArguments().getInt(Constants.PAGE_TYPE);
		FilterTable _table = new FilterTable((BaseApplication) getActivity().getApplication());
		mFilterList = _table.getAllFilters(categoryId);
		if(mFilterList != null && mFilterList.size() != 0 ) {
			mMoreListView = (ListView) view.findViewById(R.id.moreListView);
			 _adapter=new MoreFilterAdapter(getActivity(), parentFragment, mFilterList );
			mMoreListView.setAdapter(_adapter);
			((TextView) view.findViewById(R.id.list_apply)).setOnClickListener(this);
			((TextView) view.findViewById(R.id.list_reset)).setOnClickListener(this);
			((TextView) view.findViewById(R.id.cancel)).setOnClickListener(this);
		} else {
			((RelativeLayout) view.findViewById(R.id.layout_age_non_blank)).setVisibility(View.GONE);
			((TextView) view.findViewById(R.id.txt_no_data)).setVisibility(View.VISIBLE);
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
			HashMap<MapTypeFilter, String> filterMap;
			if (parentFragment == null) {
				// kids resources flow
				filterMap = ((BusinessListActivityKidsResources) getActivity()).mFilterMap;
			} else {
				filterMap = ((FragmentBusinesslistEvents) parentFragment).mFilterMap;
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
			Log.i("more", e.getMessage());
		}
	
		//ifilter.doNewFilter(businessOrEvent);
		/*HashMap<String, String>   filterMap  =	((BusinessListActivity) getActivity()).mFilterMap;
		ifilter.doFilter(FilterType.More, filterMap,businessOrEvent);*/
		/*ArrayList<Filters> selected_filters = new ArrayList<Filters>() ; 
		for(int i = 0 ; i < mFilterList.size() ; i++) {
			Filters moreFiler = mFilterList.get(i) ; 
			if(moreFiler.isSelected()) {
				selected_filters.add(moreFiler) ; 
			}
		}
		if((selected_filters!=null) && !(selected_filters.isEmpty()))
		{
			if(selected_filters != null ) {
				String moreFltr = "" ; 
				for(int i = 0 ; i < selected_filters.size() ; i++) {
					moreFltr += selected_filters.get(i).getValue() + "," ; 
				}
				String moreFiler=moreFltr.substring(0 , moreFltr.length() - 1 ) ;
				filterMap.put("More", "&more="+moreFiler);
			}
			ifilter.doFilter(FilterType.More, filterMap,businessOrEvent);
			//ifilter.doFilter(FilterType.More, selected_filters,businessOrEvent);
		}else{
			Toast.makeText(getActivity(), getResources().getString(R.string.please_select), Toast.LENGTH_SHORT).show();
		}*/
		
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.list_apply:
			manageFilter() ; 
			break;
		case R.id.list_reset:
			ifilter.reject(businessOrEvent) ; 
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
		categoryId = args.getInt(Constants.CATEGORY_KEY) ; 
		businessOrEvent=args.getInt(Constants.PAGE_TYPE);
	}
}
