package com.kelltontech.controller;

import android.app.Activity;
import android.util.Log;

import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;

/**
 * This class will be used as a base class for all controllers
 */
public abstract class BaseController implements IController {

	private static String LOG_TAG	= "BaseController";
	
	private Activity activity;
	private IScreen	screen;


	
	public BaseController(Activity activity, IScreen screen) {
		this.activity = activity;
		this.screen = screen;
	}
	
	/**
	 * @return the activity
	 */
	@Override
	public Activity getActivity() {
		return  activity;
	}
	
	/**
	 * @return the screen
	 */
	@Override
	public IScreen getScreen() {
		return screen;
	}
	
	/**
	 * @return the screen
	 */
	@Override
	public final void sendResponseToScreen(final Response response) {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				try {
					getScreen().handleUiUpdate(response);
				} catch( Throwable tr ) {
					Log.e(LOG_TAG, "sendResponseToScreen()", tr);
				}
			}
		});
	}

	/**
	 * Must be overridden by subclass to support ServiceRequest without requestType
	 */
	@Override
	public ServiceRequest getData(Object requestData) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Must be overridden by subclass to support ServiceRequest with requestType
	 */
	@Override
	public ServiceRequest getData(int requestType, Object requestData) {
		throw new UnsupportedOperationException();
	}
	

	protected final void sendRequestErrorToScreen(int requestType, Object requestData) {
		Response response = new Response();
		response.setDataType(requestType);
		response.setRequestData(requestData);
		response.setResponseObject("Some error in Request Data.");
		response.setSuccess(false);
		sendResponseToScreen(response);
	}
}