package com.mycity4kids.models.category;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class MainFilters extends BaseDataModel{
private Filters Filter;

public Filters getFilter() {
	return Filter;
}

public void setFilter(Filters filter) {
	Filter = filter;
}
}
