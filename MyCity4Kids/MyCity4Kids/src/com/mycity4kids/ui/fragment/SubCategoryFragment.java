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
import com.mycity4kids.dbtable.SubCategoryTable;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.models.category.SubCategory;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.SubCategoryAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SubCategoryFragment extends Fragment implements OnClickListener{

	private ListView mSubCategoryListView;
	private int categoryId = 0 ;
	private int businessOrEvent = 0 ;
	private IFilter ifilter ; 
	private ArrayList<SubCategory> mSubCategoryList ;
	private Fragment parentFragment;
	SubCategoryAdapter _adapter;
	Context mcontext;
	public SubCategoryFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sub_category,container, false);
		
		SubCategoryTable _table=new SubCategoryTable((BaseApplication)getActivity().getApplication());
		categoryId=getArguments().getInt(Constants.CATEGORY_KEY);
		businessOrEvent=getArguments().getInt(Constants.PAGE_TYPE);
		mSubCategoryList =_table.getAllSubCategory(categoryId);
		
		if(mSubCategoryList != null && mSubCategoryList.size() != 0 ) {
			mSubCategoryListView = (ListView) view.findViewById(R.id.subCategoryListView);
			 _adapter=new SubCategoryAdapter(getActivity(), parentFragment, mSubCategoryList );
			mSubCategoryListView.setAdapter(_adapter);
			((TextView) view.findViewById(R.id.list_apply)).setOnClickListener(this);
			((TextView) view.findViewById(R.id.list_reset)).setOnClickListener(this);
			((TextView) view.findViewById(R.id.cancel)).setOnClickListener(this);
		} else {
			((RelativeLayout) view.findViewById(R.id.layout_age_non_blank)).setVisibility(View.GONE);
			((TextView) view.findViewById(R.id.txt_no_data)).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.txt_no_data)).setText(getString(R.string.no_category_group));
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
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.list_apply:
			manageFilter() ; 
			break;
		case R.id.list_reset:
			//Log.d("check","ifilter "+ifilter+" businessOrEvent "+businessOrEvent);
			ifilter.reject(businessOrEvent) ; 
			break;
			case R.id.cancel:
				ifilter.cancel(businessOrEvent);
				break;
		default:
			break;
		}
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
		}  catch (Exception e) {
			Log.i("subCategory", e.getMessage());
				//e.printStackTrace();
		}
	//	ifilter.doNewFilter(businessOrEvent);
		/*HashMap<String, String>   filterMap  =	((BusinessListActivity) getActivity()).mFilterMap;
		ifilter.doFilter(FilterType.SubCategory, filterMap,businessOrEvent);*/
		/*ArrayList<SubCategory> selected_category = new ArrayList<SubCategory>() ; 
		for(int i = 0 ; i < mSubCategoryList.size() ; i++) {
			SubCategory category = mSubCategoryList.get(i) ; 
			if(category.isSelected()) {
				selected_category.add(category) ; 
			}
		}
		if(selected_category!=null && !(selected_category.isEmpty()))
		{
			if(selected_category != null ) {
				String subCategory = "" ; 
				for(int i = 0 ; i < selected_category.size() ; i++) {
					subCategory += selected_category.get(i).getId() + "," ; 
				}
				String subCategoryFilter=subCategory.substring(0 , subCategory.length() - 1 ) ; 
				filterMap.put("subCategory", ""+subCategoryFilter);
			}
			
			ifilter.doFilter(FilterType.SubCategory, filterMap,businessOrEvent);
			//ifilter.doFilter(FilterType.SubCategory, selected_category,businessOrEvent);
		}else{
			Toast.makeText(getActivity(), getResources().getString(R.string.please_select), Toast.LENGTH_SHORT).show();
		}*/
		
	}
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		categoryId = args.getInt(Constants.CATEGORY_KEY) ; 
		businessOrEvent=args.getInt(Constants.PAGE_TYPE);
	}
	public void setArrayList(ArrayList<SubCategory> arrayList){
		mSubCategoryList=arrayList;
	}
}
