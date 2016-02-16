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
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.NewSignUpModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ControllerSignUp extends BaseController {

    private final Activity activity;

    public ControllerSignUp(Activity activity, IScreen screen) {
        super(activity, screen);
        this.activity = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(activity);
        serviceRequest.setPostData(setRequestParameters(requestData, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        //	serviceRequest.setUrl("http://54.251.100.249/webservices/apiusers/registration?emailId=deepanker.chaudhary.1990@gmail.com&password=123456&FirstName=deep&LastName=chaudhary&MobileNumber=1235678990&cityId=1");
        //	serviceRequest.setUrl(AppConstants.REGISTRATION_URL+getAppendUrl((UserRequest)requestData));
        serviceRequest.setUrl(AppConstants.NEW_SIGN_UP_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.NEW_SIGNUP_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("SIGNUP Response", responseData);

                    UserResponse _signUpData = new Gson().fromJson(responseData, UserResponse.class);
                    response.setResponseObject(_signUpData);

                    if (_signUpData.getResponseCode() == 200) {
                        //saveUserDetails(getActivity(), _signUpData, (UserResponse) response.getResponseObject());
                        //clearPreference(getActivity());
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

    /**
     * this method creates Registration request
     * // * @param requestData
     *
     * @return
     */
    private List<NameValuePair> setRequestParameters(Object requestData, int requestType) {
        UrlEncodedFormEntity encodedEntity = null;
        NewSignUpModel model = (NewSignUpModel) requestData;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        try {
            nameValuePairs.add(new BasicNameValuePair("name", model.getUsername()));
            nameValuePairs.add(new BasicNameValuePair("mobile", "" + model.getMobileNumber()));
            nameValuePairs.add(new BasicNameValuePair("email", "" + model.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("colorCode", "" + model.getColor_code()));
            nameValuePairs.add(new BasicNameValuePair("password", model.getPassword()));
            nameValuePairs.add(new BasicNameValuePair("cityId", "" + SharedPrefUtils.getCurrentCityModel(activity).getId()));
            nameValuePairs.add(new BasicNameValuePair("requestMedium", "" + model.getSocialMode()));
            nameValuePairs.add(new BasicNameValuePair("socialToken", "" + model.getSocialToken()));
            nameValuePairs.add(new BasicNameValuePair("profileImg", "" + model.getProfileImgUrl()));
            System.out.println("JSON " + nameValuePairs);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return nameValuePairs;

    }


    public static void clearPreference(Context context) {
        SharedPrefUtils.setProfileImgUrl(context, "");
    }
}
