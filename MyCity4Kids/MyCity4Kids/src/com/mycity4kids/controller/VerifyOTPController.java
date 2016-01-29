package com.mycity4kids.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.TaskListModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 21/1/16.
 */
public class VerifyOTPController extends BaseController {
    private Activity context;

    public VerifyOTPController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    public ServiceRequest getData(int requestType, String mobileNumber, String email, String otp) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);

        serviceRequest.setPostData(setRequestParameters(requestType, mobileNumber, email, otp));
        serviceRequest.setDataType(requestType);
        serviceRequest.setContext(context);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.VERIFY_OTP_URL);

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    /**
     * this method creates login request
     *
     * @param reqtype
     * @return
     */
    private List<NameValuePair> setRequestParameters(int reqtype, String mobileNumber, String email, String otp) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {


            if (reqtype == AppConstants.VERIFY_OTP_REQUEST) {
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("mobile", mobileNumber));
                nameValuePairs.add(new BasicNameValuePair("otp", otp));
            }

            System.out.println("Verify OTP data " + nameValuePairs.toString());
        } catch (Exception e) {
            // TODO: handle exception
        }
        return nameValuePairs;
    }

    public void saveUserDetails(Context context, UserResponse pUserDetails, UserResponse requestdata) {

        try {
            UserTable userTable = new UserTable((BaseApplication) ((Activity) context).getApplication());
            userTable.deleteAll();
            userTable.insertData(requestdata);

        } catch (Exception e) {

        }
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.VERIFY_OTP_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("VERIFY_OTP_REQUEST Response", responseData);

                    UserResponse _signUpData = new Gson().fromJson(responseData, UserResponse.class);
                    response.setResponseObject(_signUpData);

                    if (_signUpData.getResponseCode() == 200) {
                        saveUserDetails(getActivity(), _signUpData, (UserResponse) response.getResponseObject());
                    }

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

    }
}
