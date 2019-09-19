package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.newmodels.AppointmentResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.Calendar;

public class GetAppointmentController extends BaseController {
    private Activity context;

    public GetAppointmentController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }


    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setContext(context);
        serviceRequest.setDataType(requestType);
        serviceRequest.setHttpHeaders(new String[]{"Content-Type"}, new String[]{"application/json"});
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.GET_APPOITMENT_URL + getAppendUrl(requestType));
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.GET_ALL_APPOINTMNET_REQ:

                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("getappotmnetResponse", responseData);

                    AppointmentResponse _loginResponse = new Gson().fromJson(responseData, AppointmentResponse.class);
                    response.setResponseObject(_loginResponse);

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

    private String getAppendUrl(int requestType) {
        StringBuilder builder = new StringBuilder();
        Calendar c = Calendar.getInstance();
        long timestamp = SharedPrefUtils.getAppointmentTimeSatmp(BaseApplication.getAppContext());
        if (timestamp > 0) {

            builder.append("sessionId:").append(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getSessionId()).append("/user_id:").append(SharedPrefUtils.getUserDetailModel(context).getId()).append("/family_id:")
                    .append(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getFamily_id()).append("/timestamp:")
                    .append(timestamp);

            SharedPrefUtils.setAppointmentTimeSatmp(BaseApplication.getAppContext(), c.getTimeInMillis());
        } else {
            builder.append("user_id:").append(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getId()).append("/family_id:")
                    .append(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getFamily_id()).append("/sessionId:")
                    .append(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getSessionId());


            SharedPrefUtils.setAppointmentTimeSatmp(BaseApplication.getAppContext(), c.getTimeInMillis());
        }


        Log.i("get appoitment ", builder.toString());

        return builder.toString();

    }


}
