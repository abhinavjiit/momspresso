package com.mycity4kids.controller;

import android.app.Activity;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.preference.SharedPrefUtils;

public class PushTokenController extends BaseController {

    private Activity context;
    private boolean fromEvents;

    public PushTokenController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.PUSH_TOKEN_URL + getAppendUrl());

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.PUSH_TOKEN_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    System.out.println("push token response "+responseData);

                    CommonResponse recentlyViewedData = new Gson().fromJson(responseData, CommonResponse.class);
                    response.setResponseObject(recentlyViewedData);
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

    private String getAppendUrl() {

        StringBuilder builder = new StringBuilder();
        builder.append("user_id=").append(SharedPrefUtils.getUserDetailModel(context).getId());
        builder.append("&push_token=").append(SharedPrefUtils.getDeviceToken(context));
        return builder.toString().replace(" ", "%20");

    }

}
