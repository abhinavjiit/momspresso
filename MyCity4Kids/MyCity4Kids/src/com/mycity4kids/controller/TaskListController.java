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
import com.mycity4kids.newmodels.TaskListModel;
import com.mycity4kids.newmodels.TaskListResponse;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class TaskListController extends BaseController {

    private Activity context;

    public TaskListController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
//        serviceRequest.setRequestData(requestData);
        TaskListModel _requestModel = (TaskListModel) requestData;

        serviceRequest.setPostData(setRequestParameters(_requestModel, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);

        if (requestType == AppConstants.CREATE_TASKLIST_REQUEST)
            serviceRequest.setUrl(AppConstants.CREATE_TASKLIST_URL);

        else if (requestType == AppConstants.DELETE_LIST_REQUEST)
            serviceRequest.setUrl(AppConstants.DELETE_TASK_LIST_URL);

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.CREATE_TASKLIST_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("TASK LIST Response", responseData);

                    TaskListResponse _loginResponse = new Gson().fromJson(responseData, TaskListResponse.class);
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

            case AppConstants.DELETE_LIST_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("DeleteList Response", responseData);

                    TaskResponse taskResponse = new Gson().fromJson(responseData, TaskResponse.class);
                    response.setResponseObject(taskResponse);


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
    private HttpEntity setRequestParameters(TaskListModel requestData, int reqtype) {

        String data = new Gson().toJson(requestData);
        //System.out.println("Appointment JSON " + data);

        UrlEncodedFormEntity encodedEntity = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


            if (reqtype == AppConstants.CREATE_TASKLIST_REQUEST) {

//            nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
                nameValuePairs.add(new BasicNameValuePair("family_id", "" + SharedPrefUtils.getUserDetailModel(context).getFamily_id()));
                nameValuePairs.add(new BasicNameValuePair("list_for", ""));
                nameValuePairs.add(new BasicNameValuePair("list_name", requestData.getList_name()));

                if(requestData.getId()>0)
                    nameValuePairs.add(new BasicNameValuePair("id",""+requestData.getId()));

                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");


            }

            else if (reqtype == AppConstants.DELETE_LIST_REQUEST) {

//                nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("task_list_id", "" + requestData.getId()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
//                nameValuePairs.add(new BasicNameValuePair("family_id", "" + SharedPrefUtils.getUserDetailModel(context).getFamily_id()));

                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            }

            System.out.println("TASK LIST data " + nameValuePairs.toString());

            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return encodedEntity;
    }


}
