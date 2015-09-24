package com.kelltontech.controller;

import android.app.Activity;

import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;

/**
 * This interface will be used as a base interface for all controllers
 */
public interface IController {

	IScreen getScreen();

	Activity getActivity();

	ServiceRequest getData(int dataType, Object requestData);

	ServiceRequest getData(Object requestData);

	void handleResponse(Response response);

	void parseResponse(Response response);
	
	void sendResponseToScreen(Response response);

}
