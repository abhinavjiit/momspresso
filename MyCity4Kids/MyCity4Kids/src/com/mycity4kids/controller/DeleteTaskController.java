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
import com.mycity4kids.newmodels.DeleteTaskModel;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class DeleteTaskController extends BaseController {

    private Activity context;

    public DeleteTaskController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        DeleteTaskModel _requestModel = (DeleteTaskModel) requestData;
        serviceRequest.setPostData(setRequestParameters(_requestModel));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.DELETE_TASK_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }
//
    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.DELETE_TASK_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("Appointment delete", responseData);

                    TaskResponse _loginResponse = new Gson().fromJson(responseData, TaskResponse.class);
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
    private HttpEntity setRequestParameters(DeleteTaskModel requestData) {

        String data = new Gson().toJson(requestData);

        UrlEncodedFormEntity encodedEntity = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
            nameValuePairs.add(new BasicNameValuePair("tasks", "" + data));

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
}
