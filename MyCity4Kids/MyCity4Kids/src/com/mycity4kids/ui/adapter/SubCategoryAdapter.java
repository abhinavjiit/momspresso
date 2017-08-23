package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.models.category.SubCategory;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;

import java.util.ArrayList;
import java.util.HashMap;

public class SubCategoryAdapter extends BaseAdapter{
	private ArrayList<SubCategory> mSubCategoriesList;
	private LayoutInflater mInflator;
	private RadioButton mSelectedRB;
	private int mSelectedPosition = -1;
	private HashMap<MapTypeFilter, String> mFilterMap;

	public	SubCategoryAdapter(Context pContext, Fragment fragment, ArrayList<SubCategory> pSubCategoryList){
		mInflator=LayoutInflater.from(pContext);
		mSubCategoriesList=pSubCategoryList;
		if (fragment == null) {
			mFilterMap = ((BusinessListActivityKidsResources)pContext).mFilterMap;
		} else {
			mFilterMap= FragmentBusinesslistEvents.mFilterMap;
		}
	}
	@Override
	public int getCount() {
		return mSubCategoriesList == null ? 0 : mSubCategoriesList.size();
	}

	@Override
	public Object getItem(int position) {
		return mSubCategoriesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			view = mInflator.inflate(R.layout.list_item_sub_category, null);
			holder = new ViewHolder();
			holder.subCategoryName = (TextView) view.findViewById(R.id.txvSubCategoryName);
			holder.radioBtn=(RadioButton)view.findViewById(R.id.radioBtnCategory);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.subCategoryName.setText(mSubCategoriesList.get(position).getName());

		holder.radioBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(position!=mSelectedPosition && mSelectedRB!=null){
					mSelectedRB.setChecked(false);
					mSubCategoriesList.get(mSelectedPosition).setSelected(false) ; 
				}
				mSelectedPosition=position;
				mSelectedRB=(RadioButton)v;
				if(mSelectedRB.isChecked()){
					mSubCategoriesList.get(mSelectedPosition).setSelected(true) ; 
				}
				addOrRemoveFilters(mSubCategoriesList);

			}
		});
		if(mSelectedPosition!=position){
			holder.radioBtn.setChecked(false);
		}else{
			holder.radioBtn.setChecked(true);
			if(mSelectedRB!=null && holder.radioBtn!=mSelectedRB){
				mSelectedRB=holder.radioBtn;
			}
		}

		return view;
	}

	class ViewHolder{
		TextView subCategoryName;
		RadioButton radioBtn;
	}
	private void addOrRemoveFilters(ArrayList<SubCategory> mSubCategoriesList){
		mFilterMap.remove(MapTypeFilter.SubCategory);
		ArrayList<SubCategory> selected_category = new ArrayList<SubCategory>() ; 
		for(int i = 0 ; i < mSubCategoriesList.size() ; i++) {
			SubCategory category = mSubCategoriesList.get(i) ; 
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
				mFilterMap.put(MapTypeFilter.SubCategory, "&sub_category_id="+subCategoryFilter);
			}
		}
	}
}
