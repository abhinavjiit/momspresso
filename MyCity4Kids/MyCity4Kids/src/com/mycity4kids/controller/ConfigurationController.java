package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.configuration.ConfigurationApiModel;

public class ConfigurationController extends BaseController {

	public ConfigurationController(Activity activity, IScreen screen) {
		super(activity, screen);
		// TODO Auto-generated constructor stub
	}



	@Override
	public ServiceRequest getData(int requestType, Object requestData) {
		VersionApiModel _versionAPiModel=(VersionApiModel)requestData;

		ServiceRequest serviceRequest=new ServiceRequest();
		//serviceRequest.setHttpHeaders(header, header);
		serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
		serviceRequest.setRequestData(requestData);
		//serviceRequest.setPostData(setRequestParameters((LoginRequest)requestData));
		serviceRequest.setDataType(requestType);
		serviceRequest.setHttpHeaders(new String[]{"Content-Type"}, new String[]{"application/json"});
		serviceRequest.setResponseController(this);
		serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
		serviceRequest.setUrl(AppConstants.CONFIGURATION_URL+getAppendUrl(requestType,_versionAPiModel));
		HttpClientConnection connection = HttpClientConnection.getInstance();
		connection.addRequest(serviceRequest);

		return serviceRequest;
	}

	@Override
	public void handleResponse(Response response) {
		switch (response.getDataType()) {
		case AppConstants.CONFIGURATION_REQUEST:

			try {
				String responseData=new String(response.getResponseData());
				Log.i("ConfigurationResponse", responseData);
				ConfigurationApiModel _configurationResponse=new Gson().fromJson(responseData, ConfigurationApiModel.class);
				response.setResponseObject(_configurationResponse);

				sendResponseToScreen(response);
			} catch (Exception e) {
				sendResponseToScreen(null);
			}

			break;


		case AppConstants.LOCATION_SEARCH_REQUEST:

			try {
				String responseData=new String(response.getResponseData());
				Log.i("LocationSearchResponse", responseData);
				ConfigurationApiModel _configurationResponse=new Gson().fromJson(responseData, ConfigurationApiModel.class);
				response.setResponseObject(_configurationResponse);

				sendResponseToScreen(response);
			} catch (Exception e) {
				sendResponseToScreen(null);
			}

			break;
			
		case AppConstants.LOCATION_MY_PROFILE_REQUEST:

			try {
				String responseData=new String(response.getResponseData());
				//Log.i("MyProfileLocationSearchResponse", responseData);
				ConfigurationApiModel _configurationResponse=new Gson().fromJson(responseData, ConfigurationApiModel.class);
				response.setResponseObject(_configurationResponse);

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

	private String getAppendUrl(int requestType,VersionApiModel pVersionAPiModel){
		StringBuilder builder=new StringBuilder();
		/**
		 * This will call from SplashActivity.class: for first time configuratiojn
		 */
		if(AppConstants.CONFIGURATION_REQUEST==requestType){
			builder.append("city_id=").append(pVersionAPiModel.getCityId()).append("&city_api_version=")
			.append(pVersionAPiModel.getCityVersion()).append("&locality_api_version=")
			.append(pVersionAPiModel.getLocalityVersion()).append("&category_api_version=")
			.append(pVersionAPiModel.getCategoryVersion());
			if(!StringUtils.isNullOrEmpty(pVersionAPiModel.getAppUpdateVersion())){
				builder.append("&app_version=").append(pVersionAPiModel.getAppUpdateVersion());
			}
		}
		/**
		 * this api will call from SelectLocation Screen:
		 * & all data will be update into data base SelectLocationActivity.class
		 */
		else if(AppConstants.LOCATION_SEARCH_REQUEST==requestType){
			builder.append("city_id=").append(pVersionAPiModel.getCityId()).append("&city_api_version=")
			.append("0.0").append("&locality_api_version=")
			.append("0.0").append("&category_api_version=")
			.append("0.0");
			
			if(!StringUtils.isNullOrEmpty(pVersionAPiModel.getAppUpdateVersion())){
				builder.append("&app_version=").append(pVersionAPiModel.getAppUpdateVersion());
			}
		}
		/*
		 * this case will call for calling location only from ProfileActivity
		 */
		
		else if(AppConstants.LOCATION_MY_PROFILE_REQUEST==requestType){
			builder.append("city_id=").append(pVersionAPiModel.getCityId()).append("&city_api_version=")
			.append(pVersionAPiModel.getCityVersion()).append("&locality_api_version=")
			.append("0.0").append("&category_api_version=")
			.append(pVersionAPiModel.getCategoryVersion());
		}
		return builder.toString();

	}




}
