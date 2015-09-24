package com.mycity4kids.sync;

public interface UpdateListener {
	void updateView(String jsonString, int requestType);
}