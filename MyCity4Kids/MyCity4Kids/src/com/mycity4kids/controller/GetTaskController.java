package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;

public class GetTaskController extends BaseController {
    private Activity context;

    public GetTaskController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }


    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        //VersionApiModel _versionAPiModel = (VersionApiModel) requestData;

        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        //serviceRequest.setRequestData(requestData);
        //serviceRequest.setPostData(setRequestParameters((LoginRequest)requestData));
        serviceRequest.setDataType(requestType);
        serviceRequest.setContext(context);
        serviceRequest.setHttpHeaders(new String[]{"Content-Type"}, new String[]{"application/json"});
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
//        serviceRequest.setUrl(AppConstants.GET_APPOITMENT_URL + getAppendUrl(requestType));
        serviceRequest.setUrl(AppConstants.GET_TASK_URL + getAppendUrl(requestType));
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.GET_ALL_TASK_REQ:

                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("getTaskResponse", responseData);

                    TaskResponse _loginResponse = new Gson().fromJson(responseData, TaskResponse.class);
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
        long timestamp = SharedPrefUtils.getTaskTimeSatmp(context);
//        if (timestamp > 0) {

            builder.append("sessionId:").append(SharedPrefUtils.getUserDetailModel(context).getSessionId()).append("/user_id:").append(SharedPrefUtils.getUserDetailModel(context).getId()).append("/family_id:")
                    .append(SharedPrefUtils.getUserDetailModel(context).getFamily_id()).append("/timestamp:")
                    .append(timestamp);

//        } else {
//            builder.append("user_id:").append(SharedPrefUtils.getUserDetailModel(context).getId()).append("/family_id:")
//                    .append(SharedPrefUtils.getUserDetailModel(context).getFamily_id()).append("/sessionId:")
//                    .append(SharedPrefUtils.getUserDetailModel(context).getSessionId());
//
//
//        }


        Log.i("get task ", builder.toString());

        return builder.toString();

    }


}
