package com.mycity4kids.models.recentlyviewed;

import java.util.List;

import com.mycity4kids.models.CommonMessage;

public class RecentlyResult extends CommonMessage{
private List<RecentlyViewedModel> data;


public List<RecentlyViewedModel> getData() {
	return data;
}

public void setData(List<RecentlyViewedModel> data) {
	this.data = data;
}
}
