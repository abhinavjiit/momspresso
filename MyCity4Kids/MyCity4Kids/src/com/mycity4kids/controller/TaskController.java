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
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class TaskController extends BaseController {

    private Activity context;

    public TaskController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
//        serviceRequest.setRequestData(requestData);
        TaskDataModel _requestModel = (TaskDataModel) requestData;

        serviceRequest.setPostData(setRequestParameters(_requestModel, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setContext(context);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);

        if (requestType == AppConstants.CREATE_TASK_REQUEST)
            serviceRequest.setUrl(AppConstants.CREATE_TASK_URL);

        else if (requestType == AppConstants.EDIT_TASK_REQUEST)
            serviceRequest.setUrl(AppConstants.EDIT_TASK_URL);

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {

            case AppConstants.CREATE_TASK_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("Task Response", responseData);

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

            case AppConstants.EDIT_TASK_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("Edit Task Response", responseData);

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

//            case AppConstants.DELETE_TASK_REQUEST:
//                try {
//                    String responseData = new String(response.getResponseData());
//
//                    Log.i("Appointment Response", responseData);
//
//                    TaskResponse _loginResponse = new Gson().fromJson(responseData, TaskResponse.class);
//                    response.setResponseObject(_loginResponse);
//                    /**
//                     * if response code is 200 then user is logged in and we save login details In shared pref
//                     * & send to response to login screen
//                     */
//
//
//                    sendResponseToScreen(response);
//                } catch (Exception e) {
//                    sendResponseToScreen(null);
//                }
//
//                break;

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
    private List<NameValuePair> setRequestParameters(TaskDataModel requestData, int reqtype) {

        String data = new Gson().toJson(requestData);
        System.out.println("Task JSON " + data);

        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {


            if (reqtype == AppConstants.CREATE_TASK_REQUEST) {

                nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("family_id", "" + SharedPrefUtils.getUserDetailModel(context).getFamily_id()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
                nameValuePairs.add(new BasicNameValuePair("task", data));

                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            } else if (reqtype == AppConstants.EDIT_TASK_REQUEST) {

                nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("family_id", "" + SharedPrefUtils.getUserDetailModel(context).getFamily_id()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
                nameValuePairs.add(new BasicNameValuePair("task", data));

                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            }

            System.out.println("Task data " + nameValuePairs.toString());

            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return nameValuePairs;
    }


}
