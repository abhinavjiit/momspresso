package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.UserInviteResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 8/2/16.
 */
public class UpdateMobileController extends BaseController {
    private Activity activity;

    public UpdateMobileController(Activity activity, IScreen screen) {
        super(activity, screen);
        this.activity = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //	serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(activity);
        UserRequest _requestModel = (UserRequest) requestData;
        serviceRequest.setPostData(setRequestParameters(requestType, _requestModel));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        //	serviceRequest.setUrl("http://54.251.100.249/webservices/apiusers/login?emailId=saur1234234123233@gmail.com&password=123456");
        //	serviceRequest.setUrl(AppConstants.LOGIN_URL+getAppendUrl(requestType,_requestModel));
        serviceRequest.setUrl(AppConstants.UPDATE_MOBILE_FOR_EXISTING_USERS_URL);

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.UPDATE_MOBILE_FOR_EXISTING_USER_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                /*String[] data=responseData.split("-->");
                String finalData=data[1].trim();*/
                    Log.i("Update Mobile Response", responseData);
                    UserResponse _loginResponse = new Gson().fromJson(responseData, UserResponse.class);
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

    /**
     * this method creates login request
     *
     * @param requestData
     * @return
     */
    private List<NameValuePair> setRequestParameters(int requestType, UserRequest requestData) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {

            if (requestType == AppConstants.UPDATE_MOBILE_FOR_EXISTING_USER_REQUEST) {
                nameValuePairs.add(new BasicNameValuePair("userId", requestData.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("mobile", requestData.getMobileNumber()));
                Log.i("Mobile Update request ", nameValuePairs.toString());
            }
            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return nameValuePairs;
    }


    @Override
    public void parseResponse(Response response) {

    }
}
