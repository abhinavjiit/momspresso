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
import com.mycity4kids.dbtable.AgeGroupTable;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.models.category.AgeGroup;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.AgeGroupAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class AgeGroupFragment extends Fragment implements OnClickListener {

	private int categoryId,businessOrEvent ;
	private IFilter ifilter;
	private ArrayList<AgeGroup> mAgeGroupList = null ;
	private Fragment parentFragment;
	AgeGroupAdapter _adapter;
	Context mcontext;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_age_group, container, false);
		businessOrEvent=getArguments().getInt(Constants.PAGE_TYPE);
		categoryId=getArguments().getInt(Constants.CATEGORY_KEY);
		boolean isComeFromSearch=getArguments().getBoolean("isComeFromSearch");
		ArrayList<AgeGroup> mAgeGroupListFromSearch=getArguments().getParcelableArrayList("AgeGroupArray");
		if((isComeFromSearch) && (mAgeGroupListFromSearch!=null) && !(mAgeGroupListFromSearch.isEmpty())){
			mAgeGroupList=new ArrayList<AgeGroup>();
			mAgeGroupList=mAgeGroupListFromSearch;
		}else{
		AgeGroupTable _table = new AgeGroupTable((BaseApplication) getActivity().getApplication());
		mAgeGroupList = _table.getAgeGroupData(categoryId);
			AgeGroup allage=new AgeGroup();
			allage.setSelected(false);
			allage.setKey("All Age Group");
			allage.setValue("All Age Group");
			mAgeGroupList.add(0,allage);
		}
		if(mAgeGroupList != null && mAgeGroupList.size() != 0 ) {
			ListView	mAgeGroupListView = (ListView) view.findViewById(R.id.subAgeGroupListView);
			 _adapter=new AgeGroupAdapter(getActivity(), parentFragment,  mAgeGroupList,isComeFromSearch );
			
			mAgeGroupListView.setAdapter(_adapter);
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
		FragmentBusinesslistEvents.isagefilter=true;
		try {

			HashMap<MapTypeFilter, String> filterMap;
			if (parentFragment == null) {
				// kids resources flow
				filterMap = ((BusinessListActivityKidsResources) getActivity()).mFilterMap;
			} else {
				filterMap = ((FragmentBusinesslistEvents.mFilterMap));
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
			Log.i("ageGroup", e.getMessage());
		}
		
	/*HashMap<String, String>   filterMap  =	((BusinessListActivity) getActivity()).mFilterMap;
	ifilter.doFilter(FilterType.AgeGroup, filterMap,businessOrEvent);*/
		/*ArrayList<AgeGroup> selected_filters = new ArrayList<AgeGroup>() ; 
		for(int i = 0 ; i < mAgeGroupList.size() ; i++) {
			AgeGroup category = mAgeGroupList.get(i) ; 
			if(category.isSelected()) {
				selected_filters.add(category) ; 
			}
		}
		if((selected_filters!=null) && !(selected_filters.isEmpty()))
		{
			
			if(selected_filters != null ) {
				String ageGr = "" ; 
				for(int i = 0 ; i < selected_filters.size() ; i++) {
					ageGr += selected_filters.get(i).getKey() + "," ; 
				}
				String finalString=ageGr.substring(0 , ageGr.length() - 1 ) ;
			
				filterMap.put("AgeGroup", "&age_group="+finalString);
			
			}
			
		//	ifilter.doFilter(FilterType.AgeGroup, selected_filters,businessOrEvent);
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
