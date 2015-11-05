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
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class DeleteImagesController extends BaseController {

    private Activity context;

    public DeleteImagesController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setPostData(setRequestParameters(requestData, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setContext(context);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);

        if (requestType == AppConstants.DELETE_APPOINTMENT_IMAGE_REQUEST)
            serviceRequest.setUrl(AppConstants.DELETE_APPOINTMENT_IMAGE_URL);

        else if (requestType == AppConstants.DELETE_TASKS_IMAGE_REQUEST)
            serviceRequest.setUrl(AppConstants.DELETE_TASKS_IMAGE_URL);


        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.DELETE_APPOINTMENT_IMAGE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("delete  Response", responseData);
                    CommonResponse _loginResponse = new Gson().fromJson(responseData, CommonResponse.class);
                    response.setResponseObject(_loginResponse);


                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }

                break;

            case AppConstants.DELETE_TASKS_IMAGE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("delete  Response", responseData);

                    CommonResponse _loginResponse = new Gson().fromJson(responseData, CommonResponse.class);
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

    /**
     * this method creates login request
     *
     * @param requestData
     * @return
     */
    private List<NameValuePair> setRequestParameters(Object requestData, int reqtype) {

        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {

            String json = new Gson().toJson(requestData);


            if (reqtype == AppConstants.DELETE_APPOINTMENT_IMAGE_REQUEST) {


                nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
                // nameValuePairs.add(new BasicNameValuePair("appointment_id", ""+_requestModel.getAppointment_id()));
                //nameValuePairs.add(new BasicNameValuePair("file_id", "" + ""+_requestModel.getId()));
                nameValuePairs.add(new BasicNameValuePair("files", json));

                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            } else if (reqtype == AppConstants.DELETE_TASKS_IMAGE_REQUEST) {


                nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
                //nameValuePairs.add(new BasicNameValuePair("task_id", "" + ""+_requestModel.getTask_id()));
                // nameValuePairs.add(new BasicNameValuePair("file_id", "" + ""+_requestModel.getId()));
                nameValuePairs.add(new BasicNameValuePair("files", json));
                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            }


            System.out.println("image  data " + nameValuePairs.toString());


        } catch (Exception e) {
            // TODO: handle exception
        }
        return nameValuePairs;
    }


}
