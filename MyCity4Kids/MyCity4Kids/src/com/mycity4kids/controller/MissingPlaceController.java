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
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.TaskResponse;

/**
 * Created by manish.soni on 03-08-2015.
 */
public class MissingPlaceController extends BaseController {
    private Activity context;

    public MissingPlaceController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }


    @Override
    public ServiceRequest getData(int requestType, Object requestData) {

        ServiceRequest serviceRequest = new ServiceRequest();

        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(context);
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.MISSING_PLACE_URL + getAppendUrl(requestType, requestData));

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.MISSING_PLACE_REQUEST:

                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("missing place response", responseData);

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

    private String getAppendUrl(int requestType, Object requestData) {
        ParentingRequest parentingModel = (ParentingRequest) requestData;
        StringBuilder builder = new StringBuilder();

        builder.append("create?name=").append(parentingModel.getEvent_name());
        builder.append("&contact_no=").append(parentingModel.getContact_no());

        return builder.toString().replace(" ", "%20");
    }
}