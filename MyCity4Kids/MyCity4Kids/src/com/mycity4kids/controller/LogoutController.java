package com.mycity4kids.controller;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
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
        serviceRequest.setUrl(AppConstants.LOGOUT_URL);
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
            SharedPrefUtils.setProfileImgUrl(context, "");
            SharedPrefUtils.setAppointmentTimeSatmp(context, 0);

            //UserTable userTable = new UserTable((BaseApplication)((Activity)context).getApplication()) ;
            //	userTable.deleteAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<NameValuePair> setRequestParameters(String sessionId) {
        UserTable _userTable = new UserTable((BaseApplication) getActivity().getApplication());
        String userId = "" + _userTable.getUserId();

        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            nameValuePairs.add(new BasicNameValuePair("sessionId", "" + SharedPrefUtils.getUserDetailModel(mActivity).getSessionId()));
            nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(mActivity).getId()));
            encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            //	String finalData=sessionId+"="+sessionId;T

            System.out.println("logout  data " + nameValuePairs.toString());
            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }

        return nameValuePairs;

    }
}
