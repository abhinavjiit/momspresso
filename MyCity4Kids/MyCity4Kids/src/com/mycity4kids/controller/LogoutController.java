package com.mycity4kids.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LogoutController extends BaseController {

    Activity mActivity;

    public LogoutController(Activity activity, IScreen screen) {
        super(activity, screen);
        mActivity = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(mActivity);
        //serviceRequest.setHttpHeaders(new String[]{HTTP.CONTENT_TYPE}, new String[]{"application/x-www-form-urlencoded"});
        serviceRequest.setPostData(setRequestParameters((String) requestData));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setUrl(AppConstants.NEW_LOGOUT_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.LOGOUT_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("Logout Response", responseData);
                    LogoutResponse _logoutResponse = new Gson().fromJson(responseData, LogoutResponse.class);
                    if (_logoutResponse.getResponseCode() == 200) {
                        response.setResponseObject(_logoutResponse);
                        deleteUserDetails(getActivity());
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


    public void deleteUserDetails(Context context) {

        try {
            SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(), "");
            SharedPrefUtils.setAppointmentTimeSatmp(BaseApplication.getAppContext(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<NameValuePair> setRequestParameters(String sessionId) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            nameValuePairs.add(new BasicNameValuePair("userId", "" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getId()));
            encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            System.out.println("logout  data " + nameValuePairs.toString());
        } catch (Exception e) {
            // TODO: handle exception
        }

        return nameValuePairs;

    }
}
