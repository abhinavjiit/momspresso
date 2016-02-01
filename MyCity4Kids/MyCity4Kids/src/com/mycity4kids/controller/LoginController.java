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
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.login.LoginResponse;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginController extends BaseController {

    private final Activity activity;

    public LoginController(Activity activity, IScreen screen) {
        super(activity, screen);
        this.activity = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //	serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(activity);
        UserRequest _requestModel = (UserRequest) requestData;
        serviceRequest.setPostData(setRequestParameters(requestType, _requestModel));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        //	serviceRequest.setUrl("http://54.251.100.249/webservices/apiusers/login?emailId=saur1234234123233@gmail.com&password=123456");
        //	serviceRequest.setUrl(AppConstants.LOGIN_URL+getAppendUrl(requestType,_requestModel));
        serviceRequest.setUrl(AppConstants.LOGIN_URL);

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.LOGIN_REQUEST:
            case AppConstants.NEW_LOGIN_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                /*String[] data=responseData.split("-->");
                String finalData=data[1].trim();*/
                    Log.i("Login Response", responseData);
                    UserResponse _loginResponse = new Gson().fromJson(responseData, UserResponse.class);
                    response.setResponseObject(_loginResponse);
                    /**
                     * if response code is 200 then user is logged in and we save login details In shared pref
                     * & send to response to login screen
                     */
                    if (_loginResponse.getResponseCode() == 200) {
                        _loginResponse.setLoggedIn(true);
                        saveUserDetails(getActivity(), _loginResponse, (UserResponse) response.getResponseObject());
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

    /**
     * This was for Get Login.
     *
     * @param requestType
     * @param _userRequest
     * @return
     */
    private String getAppendUrl(int requestType, UserRequest _userRequest) {
        StringBuilder builder = new StringBuilder();

        if (_userRequest.getNetworkName().equalsIgnoreCase("google")) {
            builder.append("?emailId=").append(_userRequest.getEmailId()).append("&network=").
                    append(_userRequest.getNetworkName()).append("&profileId=").append(_userRequest.getProfileId());
            if (!StringUtils.isNullOrEmpty(_userRequest.getFirstName())) {
                builder.append("&firstname=").append(_userRequest.getFirstName());
            }
            if (!StringUtils.isNullOrEmpty(_userRequest.getLastName())) {
                builder.append("&lastname=").append(_userRequest.getLastName());
            }
        } else if (_userRequest.getNetworkName().equalsIgnoreCase("facebook")) {
            builder.append("?emailId=").append(_userRequest.getEmailId()).append("&network=").
                    append(_userRequest.getNetworkName()).append("&profileId=").append(_userRequest.getProfileId());
            if (!StringUtils.isNullOrEmpty(_userRequest.getFirstName())) {
                builder.append("&firstname=").append(_userRequest.getFirstName());
            }
            if (!StringUtils.isNullOrEmpty(_userRequest.getLastName())) {
                builder.append("&lastname=").append(_userRequest.getLastName());
            }
        } else {
            builder.append("?emailId=").append(_userRequest.getEmailId()).append("&password=").append(_userRequest.getPassword());
        }

        return builder.toString().replace(" ", "%20");

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
    private List<NameValuePair> setRequestParameters(int requestType, UserRequest requestData) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {

            if (requestType == AppConstants.LOGIN_REQUEST) {
                if (requestData.getNetworkName().equalsIgnoreCase("google") || requestData.getNetworkName().equalsIgnoreCase("facebook")) {
                    if (!StringUtils.isNullOrEmpty(requestData.getEmailId())) {
                        nameValuePairs.add(new BasicNameValuePair("emailId", requestData.getEmailId()));
                    }
                    if (!StringUtils.isNullOrEmpty(requestData.getNetworkName())) {
                        nameValuePairs.add(new BasicNameValuePair("network", requestData.getNetworkName()));
                    }
                    if (!StringUtils.isNullOrEmpty(requestData.getProfileId())) {
                        nameValuePairs.add(new BasicNameValuePair("profileId", requestData.getProfileId()));
                    }
                    if (!StringUtils.isNullOrEmpty(requestData.getFirstName())) {
                        nameValuePairs.add(new BasicNameValuePair("firstname", requestData.getFirstName()));
                    }
                    if (!StringUtils.isNullOrEmpty(requestData.getLastName())) {
                        nameValuePairs.add(new BasicNameValuePair("lastname", requestData.getLastName()));
                    }
                } else {
                    if (!StringUtils.isNullOrEmpty(requestData.getEmailId())) {
                        nameValuePairs.add(new BasicNameValuePair("emailId", requestData.getEmailId()));
                    }
                    if (!StringUtils.isNullOrEmpty(requestData.getPassword())) {
                        nameValuePairs.add(new BasicNameValuePair("password", requestData.getPassword()));
                    }
                }
                nameValuePairs.add(new BasicNameValuePair("deviceId", DataUtils.getDeviceId(activity)));
                if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getDeviceToken(activity))) {
                    SharedPrefUtils.setPushTokenUpdateToServer(activity, true);
                }
                nameValuePairs.add(new BasicNameValuePair("push_token", SharedPrefUtils.getDeviceToken(activity)));
                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                Log.i("Login request ", nameValuePairs.toString());
            } else if (requestType == AppConstants.NEW_LOGIN_REQUEST) {
                if (!StringUtils.isNullOrEmpty(requestData.getEmailId())) {
                    nameValuePairs.add(new BasicNameValuePair("emailId", requestData.getEmailId()));
                }

                if (!StringUtils.isNullOrEmpty(requestData.getNetworkName()) && requestData.getNetworkName().equalsIgnoreCase("google")) {
                    nameValuePairs.add(new BasicNameValuePair("requestMedium", "gp"));
                } else if (!StringUtils.isNullOrEmpty(requestData.getNetworkName()) && requestData.getNetworkName().equalsIgnoreCase("facebook")) {
                    nameValuePairs.add(new BasicNameValuePair("requestMedium", "fb"));
                } else {
                    nameValuePairs.add(new BasicNameValuePair("requestMedium", "custom"));
                }

                if (!StringUtils.isNullOrEmpty(requestData.getProfileId())) {
                    nameValuePairs.add(new BasicNameValuePair("profileId", requestData.getProfileId()));
                }
                if (!StringUtils.isNullOrEmpty(requestData.getFirstName())) {
                    nameValuePairs.add(new BasicNameValuePair("firstname", requestData.getFirstName()));
                }
                if (!StringUtils.isNullOrEmpty(requestData.getLastName())) {
                    nameValuePairs.add(new BasicNameValuePair("lastname", requestData.getLastName()));
                }
                if (!StringUtils.isNullOrEmpty(requestData.getPassword())) {
                    nameValuePairs.add(new BasicNameValuePair("password", requestData.getPassword()));
                }
                nameValuePairs.add(new BasicNameValuePair("deviceId", DataUtils.getDeviceId(activity)));
                if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getDeviceToken(activity))) {
                    SharedPrefUtils.setPushTokenUpdateToServer(activity, true);
                }
                nameValuePairs.add(new BasicNameValuePair("push_token", SharedPrefUtils.getDeviceToken(activity)));
                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                Log.i("Login request ", nameValuePairs.toString());
            }

            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return nameValuePairs;
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
            userTable.deleteAll();
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
