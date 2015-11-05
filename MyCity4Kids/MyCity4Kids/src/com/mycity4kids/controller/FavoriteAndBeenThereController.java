package com.mycity4kids.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.favorite.FavoriteRequest;
import com.mycity4kids.models.forgot.CommonResponse;

/**
 * @author sachin.gupta
 */
public class FavoriteAndBeenThereController extends BaseController{

	private static final String LOG_TAG = "FavoriteAndBeenThereController";
	private Activity context;
	/**
	 * @param activity
	 * @param screen
	 */
	public FavoriteAndBeenThereController(Activity activity, IScreen screen) {
		super(activity, screen);
		context = activity;
	}
	
	@Override
	public ServiceRequest getData(int requestType, Object requestData) {
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
	
		serviceRequest.setRequestData(requestData);
		serviceRequest.setDataType(requestType);
		serviceRequest.setResponseController(this);
		serviceRequest.setContext(context);
		switch (requestType) {
		case AppConstants.FAVORITE_REQUEST: {
			serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
			serviceRequest.setPostData(setRequestParameters(requestData));
			serviceRequest.setUrl(AppConstants.FAVORITE_URL);
			break;
		}
		case AppConstants.BEEN_THERE_REQUEST: {
			FavoriteRequest favoriteRequest = (FavoriteRequest)requestData; 
			if(StringUtils.isNullOrEmpty(favoriteRequest.getSessionId())){
				serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
				serviceRequest.setUrl(AppConstants.BEEN_THERE_URL+getAppendUrl(favoriteRequest));
			}else{
			serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
			serviceRequest.setPostData(setRequestParameters(requestData));
			serviceRequest.setUrl(AppConstants.BEEN_THERE_URL);
			}
			break;
		}
		}

		HttpClientConnection connection = HttpClientConnection.getInstance();
		connection.addRequest(serviceRequest);
		return serviceRequest;
	}


	@Override
	public void handleResponse(Response response) {
		switch (response.getDataType()) {
		case AppConstants.FAVORITE_REQUEST:
		case AppConstants.BEEN_THERE_REQUEST: {
			try {
				String responseData=new String(response.getResponseData());
				Log.i("Favorite And Been There Response", responseData);
				CommonResponse _forgotResponse=new Gson().fromJson(responseData, CommonResponse.class);
				response.setResponseObject(_forgotResponse);
			    sendResponseToScreen(response);
			} catch (Exception e) {
				 sendResponseToScreen(null);
			}
			break;
		}
		}
	}
	private List<NameValuePair> setRequestParameters(Object pRequestModel) {
		UrlEncodedFormEntity encodedEntity = null;
		String device_id=DataUtils.getDeviceId(getActivity());
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			FavoriteRequest favoriteRequest = (FavoriteRequest)pRequestModel;
			nameValuePairs.add(new BasicNameValuePair("user_id", favoriteRequest.getUser_id() ));
			nameValuePairs.add(new BasicNameValuePair("sessionId", favoriteRequest.getSessionId() ));
			nameValuePairs.add(new BasicNameValuePair("id", favoriteRequest.getId() ));
			nameValuePairs.add(new BasicNameValuePair("type", favoriteRequest.getType() ));
			nameValuePairs.add(new BasicNameValuePair("imei_no",device_id==null?"":device_id ));
			encodedEntity = new UrlEncodedFormEntity(nameValuePairs);
		} catch (Exception e) {
			Log.e(LOG_TAG, "setRequestParameters", e);
		}
		return nameValuePairs;
	}

	private String getAppendUrl(FavoriteRequest favoriteRequest) {
		StringBuilder builder=new StringBuilder();
		String device_id=DataUtils.getDeviceId(getActivity());
		if (! StringUtils.isNullOrEmpty(favoriteRequest.getUser_id())) {
			builder.append("?user_id=").append(favoriteRequest.getUser_id());
		}if (! StringUtils.isNullOrEmpty(favoriteRequest.getId())) {
			builder.append("&id=").append(favoriteRequest.getId());
		}
		if (! StringUtils.isNullOrEmpty(favoriteRequest.getType())) {
			builder.append("&type=").append(favoriteRequest.getType());
		}
		if (! StringUtils.isNullOrEmpty(device_id)) {
			builder.append("&imei_no=").append(device_id);
		}
		
		return builder.toString().replace(" ", "%20");
	}
	
	@Override
	public void parseResponse(Response response) {
		// TODO Auto-generated method stub
	}
}
