package com.mycity4kids.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.login.LoginResponse;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.AppointmentResponse;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDeleteController extends BaseController {

    private Activity context;

    public AppointmentDeleteController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        AppoitmentDataModel.AppointmentDetail _requestModel = ( AppoitmentDataModel.AppointmentDetail) requestData;
        serviceRequest.setPostData(setRequestParameters(_requestModel));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.DELETE_APPOINTMENT_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.DELETE_APPOINTEMT_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("Appointment delete", responseData);

                    AppointmentResponse _loginResponse = new Gson().fromJson(responseData, AppointmentResponse.class);
                    response.setResponseObject(_loginResponse);
                    /**
                     * if response code is 200 then user is logged in and we save login details In shared pref
                     * & send to response to login screen
                     */


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

    /**
     * this method creates login request
     *
     * @param requestData
     * @return
     */
    private HttpEntity setRequestParameters( AppoitmentDataModel.AppointmentDetail requestData) {

        String data = new Gson().toJson(requestData);


        UrlEncodedFormEntity encodedEntity = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


            nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
            nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
            nameValuePairs.add(new BasicNameValuePair("appointment_id", "" + requestData.getId()));

            encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

            System.out.println("Appointment JSON " + nameValuePairs.toString());
            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return encodedEntity;
    }

    /**
     * stored user details in sharedpref:-
     */

    public static final String SAVED_USER_DETAILS = "com.mycity4.saved_user_details";


    public void saveUserDetails(Context context, UserResponse pUserDetails, UserResponse requestdata) {

//		SharedPreferences prefs = context.getSharedPreferences(SAVED_USER_DETAILS, Context.MODE_PRIVATE);
//		Editor editor = prefs.edit();
//
//		try 
//		{ 
//			String value;
//			editor.putString( "emailId", requestdata.getEmailId());
//
//			value=requestdata.getPassword();
//			editor.putString("password", value);
//
//
//			value = pUserDetails.isLoggedIn()+ "";
//			editor.putString("isLoggedIn", value);
//
//			/*	value = pUserDetails.getResult().getUserId();
//			editor.putString("userId", value);*/
//
//
//
//			editor.commit();
//		}
//		catch (Exception e) {}

        try {
            UserTable userTable = new UserTable((BaseApplication) ((Activity) context).getApplication());
            userTable.insertData(requestdata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LoginResponse getSavedUser(Context context) {

        LoginResponse mSavedUser = new LoginResponse();


        SharedPreferences _prefs = context.getSharedPreferences(SAVED_USER_DETAILS, Context.MODE_PRIVATE);
        try {
            String value;
            value = _prefs.getString("isLoggedIn", "false");

            mSavedUser.setLoggedIn(Boolean.parseBoolean(value));

            //value = _prefs.getString("emailId", "");
            //value = _prefs.getString("password", "");


            value = _prefs.getString("userId", "");

            mSavedUser.getResult().setUserId(value);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return mSavedUser;
    }


    public static void clearLoginStatus(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SAVED_USER_DETAILS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();

        editor.putString("emailId", "");
        editor.putString("password", "");
        editor.putString("isLoggedIn", "");
        editor.putString("userId", "");
        editor.commit();
    }
}
