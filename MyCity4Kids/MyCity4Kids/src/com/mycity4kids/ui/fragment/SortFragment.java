package com.mycity4kids.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.models.category.SortBy;

public class SortFragment extends Fragment{
	private int categoryId,businessOrEvent ;
	private IFilter ifilter;
	private ArrayList<SortBy> sortByArrayList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		businessOrEvent=getArguments().getInt(Constants.PAGE_TYPE);
		categoryId=getArguments().getInt(Constants.CATEGORY_KEY);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void setAction(IFilter filter) {
		ifilter = filter;
	}
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		categoryId = args.getInt(Constants.CATEGORY_KEY) ; 
		businessOrEvent=args.getInt(Constants.PAGE_TYPE);
	}
}
