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
import com.mycity4kids.models.forgot.AddAListingRequest;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.reportanerror.ErrorRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordController extends BaseController {

	public ForgotPasswordController(Activity activity, IScreen screen) {
		super(activity, screen);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ServiceRequest getData(int requestType, Object requestData) {
		ServiceRequest serviceRequest = new ServiceRequest();
		// serviceRequest.setHttpHeaders(header, header);
		if (requestType == AppConstants.FORGOT_REQUEST) {
			serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
			serviceRequest.setRequestData(requestData);
			serviceRequest.setPostData(setRequestParameters((String) requestData));
			serviceRequest.setDataType(requestType);
			serviceRequest.setResponseController(this);
			Log.i("Forgot", "requestData - " + requestData);
			// serviceRequest.setHttpHeaders(new String[]{HTTP.CONTENT_TYPE},
			// new String[]{"application/x-www-form-urlencoded"});
			serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
			// serviceRequest.setUrl("http://54.251.100.249/webservices/users/login?emailId=lifelover.w@gmail.com&password=01167c442ea5ab019f8c210b7b51c6ef9f6e205f");
			serviceRequest.setUrl(AppConstants.FORGOT_URL);
			HttpClientConnection connection = HttpClientConnection
					.getInstance();
			connection.addRequest(serviceRequest);

		} else if (requestType == AppConstants.ADD_A_LISTING_REQUEST) {
			serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
			serviceRequest.setRequestData(requestData);
			serviceRequest.setDataType(requestType);
			serviceRequest.setResponseController(this);
			serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
			serviceRequest.setUrl(AppConstants.ADD_A_LISTING_URL
					+ getAppendUrl(requestType, requestData));
			HttpClientConnection connection = HttpClientConnection
					.getInstance();
			connection.addRequest(serviceRequest);

		} else if (requestType == AppConstants.REPORT_AN_ERROR_REQUEST) {
			serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
			serviceRequest.setRequestData(requestData);
			serviceRequest.setDataType(requestType);
			serviceRequest.setResponseController(this);
			serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
			serviceRequest.setUrl(AppConstants.REPORT_AN_ERROR_URL
					+ getAppendUrl(requestType, requestData));
			HttpClientConnection connection = HttpClientConnection
					.getInstance();
			connection.addRequest(serviceRequest);
		}

		return serviceRequest;
	}

	@Override
	public void handleResponse(Response response) {
		switch (response.getDataType()) {
		case AppConstants.FORGOT_REQUEST:
			try {
				String responseData = new String(response.getResponseData());
				Log.i("Forgot Response", responseData);
				CommonResponse _forgotResponse = new Gson().fromJson(responseData, CommonResponse.class);
						
				response.setResponseObject(_forgotResponse);
				sendResponseToScreen(response);
			} catch (Exception e) {
				sendResponseToScreen(null);
			}

			break;

		case AppConstants.ADD_A_LISTING_REQUEST:
			try {
				String responseData = new String(response.getResponseData());
				Log.i("Add listing Response", responseData);
				CommonResponse _addAListingResponse = new Gson().fromJson(
						responseData, CommonResponse.class);
				response.setResponseObject(_addAListingResponse);
				sendResponseToScreen(response);
			} catch (Exception e) {
				sendResponseToScreen(null);
			}

			break;
			
		case AppConstants.REPORT_AN_ERROR_REQUEST:
			try {
				String responseData = new String(response.getResponseData());
				
				Log.i("ReportAnError Response", responseData);
				CommonResponse _reportAnErrorResponse = new Gson().fromJson(
						responseData, CommonResponse.class);
				response.setResponseObject(_reportAnErrorResponse);
				sendResponseToScreen(response);
			} catch (Exception e) {
				sendResponseToScreen(null);
			}

			break;
			
		default:
			break;
		}

	}

	private HttpEntity setRequestParameters(String emailId) {
		UrlEncodedFormEntity encodedEntity = null;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("emailId", emailId));
			encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

			// encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
			// "application/x-www-form-urlencoded"));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return encodedEntity;

	}

	@Override
	public void parseResponse(Response response) {
		// TODO Auto-generated method stub

	}

	private String getAppendUrl(int requestType, Object requestData) {
		StringBuilder builder = new StringBuilder();
		if (requestType == AppConstants.ADD_A_LISTING_REQUEST) {
			AddAListingRequest addAListingModel = (AddAListingRequest) requestData;
			builder.append("name=").append(addAListingModel.getBusinessName());
			if (!StringUtils.isNullOrEmpty(addAListingModel.getContactNumber())) {
				builder.append("&contact_no=").append(
						addAListingModel.getContactNumber());
			}
		} else if (requestType == AppConstants.REPORT_AN_ERROR_REQUEST) {
			ErrorRequest request = (ErrorRequest) requestData;
			if (!StringUtils.isNullOrEmpty(request.getListingId())) {
				builder.append("listing_id=").append(request.getListingId());
			}
			if (!StringUtils.isNullOrEmpty(request.getListingType())) {
				builder.append("&listing_type=").append(
						request.getListingType());
			}
			if (!StringUtils.isNullOrEmpty(request.getUserId())) {
				builder.append("&user_id=").append(request.getUserId());
			}
			if (!StringUtils.isNullOrEmpty(request.getReportType())) {
				builder.append("&report_type=").append(request.getReportType());
			}
			if (!StringUtils.isNullOrEmpty(request.getReportContent())) {
				builder.append("&report_content=").append(
						request.getReportContent());
			}
		}
		return builder.toString().replace(" ", "%20");
	}

}
