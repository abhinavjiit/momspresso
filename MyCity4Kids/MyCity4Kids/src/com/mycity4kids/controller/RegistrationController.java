package com.mycity4kids.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;

public class RegistrationController extends BaseController{

	public RegistrationController(Activity activity, IScreen screen) {
		super(activity, screen);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ServiceRequest getData(int requestType, Object requestData) {
		ServiceRequest serviceRequest=new ServiceRequest();
		//serviceRequest.setHttpHeaders(header, header);
		serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
		serviceRequest.setRequestData(requestData);
		serviceRequest.setPostData(setRequestParameters((UserRequest)requestData));
		serviceRequest.setDataType(requestType);
		serviceRequest.setResponseController(this);
		serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
		//	serviceRequest.setUrl("http://54.251.100.249/webservices/apiusers/registration?emailId=deepanker.chaudhary.1990@gmail.com&password=123456&FirstName=deep&LastName=chaudhary&MobileNumber=1235678990&cityId=1");
	//	serviceRequest.setUrl(AppConstants.REGISTRATION_URL+getAppendUrl((UserRequest)requestData));
		serviceRequest.setUrl(AppConstants.REGISTRATION_URL);
		HttpClientConnection connection = HttpClientConnection.getInstance();
		connection.addRequest(serviceRequest);

		return serviceRequest;
	}

	@Override
	public void handleResponse(Response response) {
		switch (response.getDataType()) {
		case AppConstants.REGISTRATION_REQUEST:
			try {
				String responseData=new String(response.getResponseData());
                Log.i("Registration Response", responseData);
				UserResponse _registrationData=new Gson().fromJson(responseData, UserResponse.class);
				response.setResponseObject(_registrationData);
				if(_registrationData.getResponseCode()==200){
					saveUserDetails(getActivity(), _registrationData,(UserResponse)response.getResponseObject());
					clearPreference(getActivity());
				}
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


	}

	/**
	 * these method was for GET Registration:
	 * @param _userRequest
	 * @return
	 */

	private String getAppendUrl(UserRequest _userRequest){
		StringBuilder builder=new StringBuilder();

		if (! StringUtils.isNullOrEmpty(_userRequest.getEmailId())) {
			builder.append("?emailId=").append(_userRequest.getEmailId());
		}if (! StringUtils.isNullOrEmpty(_userRequest.getPassword())) {
			builder.append("&password=").append(_userRequest.getPassword());
		}
		if (! StringUtils.isNullOrEmpty(_userRequest.getPassword())) {
			builder.append("&cpassword=").append(_userRequest.getPassword());
		}
		if (! StringUtils.isNullOrEmpty(_userRequest.getFirstName())) {
			builder.append("&FirstName=").append(_userRequest.getFirstName());
		}
		
		builder.append("&cityId=").append(_userRequest.getCityId());

		return builder.toString().replace(" ", "%20");

	}


	/**
	 * this method creates Registration request
	 * @param requestData
	 * @return
	 */
	private HttpEntity setRequestParameters(UserRequest requestData){
		UrlEncodedFormEntity encodedEntity = null;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if (! StringUtils.isNullOrEmpty(requestData.getEmailId())) {
				nameValuePairs.add(new BasicNameValuePair("emailId", requestData.getEmailId()));
			}
			if (! StringUtils.isNullOrEmpty(requestData.getPassword())) {
				nameValuePairs.add(new BasicNameValuePair("password", requestData.getPassword()));
			}
			if (! StringUtils.isNullOrEmpty(requestData.getPassword())) {
				nameValuePairs.add(new BasicNameValuePair("cpassword", requestData.getPassword()));
			}
			if (! StringUtils.isNullOrEmpty(requestData.getFirstName())) {
				nameValuePairs.add(new BasicNameValuePair("FirstName", requestData.getFirstName()));
			}
			nameValuePairs.add(new BasicNameValuePair("cityId", ""+requestData.getCityId()));
			encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

			// encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
			// "application/x-www-form-urlencoded"));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return encodedEntity;

	}
	public void saveUserDetails(Context context, UserResponse pUserDetails, UserResponse requestdata)
	{

		try {
			UserTable userTable = new UserTable((BaseApplication)((Activity)context).getApplication()) ;
			userTable.insertData(requestdata) ; 

		} catch (Exception e) {

		}
	}
	
	public static void clearPreference(Context context)
	{
		SharedPrefUtils.setProfileImgUrl(context, "");
	}
}
