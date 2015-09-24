package com.mycity4kids.models.autosuggest;

import com.mycity4kids.models.CommonMessage;


public class AutoSuggestResult extends CommonMessage{
private AutoSuggestData data;

public AutoSuggestData getData() {
	return data;
}

public void setData(AutoSuggestData data) {
	this.data = data;
}


}
