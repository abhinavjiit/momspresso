package com.mycity4kids.interfaces;

import com.mycity4kids.ui.activity.BusinessListActivity.FilterType;

public interface IFilter {
	public void doFilter(FilterType type , Object Content,int businessOrEvent) ;
	public void doNewFilter(int businessOrEvent) ;
	public void reject(int type) ;
	public void cancel(int type) ;
}
