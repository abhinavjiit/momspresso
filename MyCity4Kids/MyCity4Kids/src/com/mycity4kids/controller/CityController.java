package com.mycity4kids.controller;

import android.app.Activity;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.city.CityResponse;

public class CityController extends BaseController{

	public CityController(Activity activity, IScreen screen) {
		super(activity, screen);
		// TODO Auto-generated constructor stub
	}
	@Override
	public ServiceRequest getData(int requestType, Object requestData) {
		ServiceRequest serviceRequest=new ServiceRequest();
		//serviceRequest.setHttpHeaders(header, header);
		serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
		serviceRequest.setRequestData(requestData);
		//serviceRequest.setPostData(setRequestParameters((LoginRequest)requestData));
		serviceRequest.setDataType(requestType);
		serviceRequest.setResponseController(this);
		//serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
		serviceRequest.setUrl("http://54.251.100.249/webservices/users/login?emailId=lifelover.w@gmail.com&password=01167c442ea5ab019f8c210b7b51c6ef9f6e205f");
		HttpClientConnection connection = HttpClientConnection.getInstance();
		connection.addRequest(serviceRequest);
		return serviceRequest;
	}

	@Override
	public void handleResponse(Response response) {
		switch (response.getDataType()) {
		case AppConstants.LOCALITES_REQUEST:
			try {
				String responseData=new String(response.getResponseData());
			
				CityResponse _cityResponse=new Gson().fromJson(responseData, CityResponse.class);
				response.setResponseObject(_cityResponse);
				
			    sendResponseToScreen(response);
			} catch (Exception e) {
				 sendResponseToScreen(null);
			}
			
			break;

		default:
			break;
		}
		
	}

	@Override
	public void parseResponse(Response response) {
		// TODO Auto-generated method stub
		
	}

}
