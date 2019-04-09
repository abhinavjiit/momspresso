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
import com.mycity4kids.newmodels.UserInviteResponse;
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
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(activity);
        UserRequest _requestModel = (UserRequest) requestData;
        serviceRequest.setPostData(setRequestParameters(requestType, _requestModel));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.NEW_LOGIN_URL);
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
                    } else if (_loginResponse.getResponseCode() == 201) {
                        _loginResponse.setLoggedIn(true);
                        UserInviteResponse _signUpData = new Gson().fromJson(responseData, UserInviteResponse.class);
                        response.setResponseObject(_signUpData);
                        response.setDataType(AppConstants.ACCEPT_OR_REJECT_INVITE_REQUEST);
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
        // TODO Auto-generated method stub

    }

    /**
     * this method creates login request
     *
     * @param requestData
     * @return
     */
    private List<NameValuePair> setRequestParameters(int requestType, UserRequest requestData) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {

            if (requestType == AppConstants.NEW_LOGIN_REQUEST) {

                if (!StringUtils.isNullOrEmpty(requestData.getNetworkName()) && requestData.getNetworkName().equalsIgnoreCase("google")) {
                    nameValuePairs.add(new BasicNameValuePair("requestMedium", "gp"));
                    nameValuePairs.add(new BasicNameValuePair("socialToken", requestData.getAccessToken()));
                } else if (!StringUtils.isNullOrEmpty(requestData.getNetworkName()) && requestData.getNetworkName().equalsIgnoreCase("facebook")) {
                    nameValuePairs.add(new BasicNameValuePair("requestMedium", "fb"));
                    nameValuePairs.add(new BasicNameValuePair("socialToken", requestData.getAccessToken()));
                } else {
                    nameValuePairs.add(new BasicNameValuePair("requestMedium", "custom"));
                    nameValuePairs.add(new BasicNameValuePair("socialToken", ""));
                    if (StringUtils.isValidEmail(requestData.getEmailId())) {
                        nameValuePairs.add(new BasicNameValuePair("email", requestData.getEmailId()));
                    }
                    if (!StringUtils.isNullOrEmpty(requestData.getPassword())) {
                        nameValuePairs.add(new BasicNameValuePair("password", requestData.getPassword()));
                    }
                }
                nameValuePairs.add(new BasicNameValuePair("cityId", "" + SharedPrefUtils.getCurrentCityModel(activity).getId()));
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
