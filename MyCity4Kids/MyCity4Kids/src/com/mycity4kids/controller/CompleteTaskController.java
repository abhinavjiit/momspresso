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
import com.mycity4kids.newmodels.CompleteTaskRequestModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CompleteTaskController extends BaseController {

    private Activity context;

    public CompleteTaskController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        CompleteTaskRequestModel _requestModel = (CompleteTaskRequestModel) requestData;
        serviceRequest.setPostData(setRequestParameters(_requestModel));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.TASK_COMPLETE_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }
//
    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.TASKS_COMPLETE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("tasks compltee ", responseData);

                    CommonResponse _loginResponse = new Gson().fromJson(responseData, CommonResponse.class);
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
    private HttpEntity setRequestParameters(CompleteTaskRequestModel requestData) {

        String data = new Gson().toJson(requestData);

        UrlEncodedFormEntity encodedEntity = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("task_date_excluded", "" + data));
            nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
            nameValuePairs.add(new BasicNameValuePair("family_id", "" + SharedPrefUtils.getUserDetailModel(context).getFamily_id()));

            encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

            System.out.println("tasks complete JSON " + nameValuePairs.toString());
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
