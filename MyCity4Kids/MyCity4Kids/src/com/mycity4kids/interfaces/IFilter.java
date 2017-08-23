package com.mycity4kids.interfaces;

import com.mycity4kids.ui.activity.BusinessListActivity.FilterType;

public interface IFilter {
	void doFilter(FilterType type, Object Content, int businessOrEvent) ;
	void doNewFilter(int businessOrEvent) ;
	void reject(int type) ;
	void cancel(int type) ;
}
