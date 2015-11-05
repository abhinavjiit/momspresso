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
import com.mycity4kids.models.profile.KidsInformation;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class EditProfileController extends BaseController {

    private Activity context;

    public EditProfileController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
//        serviceRequest.setRequestData(requestData);
       // AppoitmentDataModel _requestModel = (AppoitmentDataModel) requestData;

        serviceRequest.setPostData(setRequestParameters(requestData, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setContext(context);
        if (requestType == AppConstants.EDIT_ADULTPROFILE_REQUEST)
            serviceRequest.setUrl(AppConstants.EDIT_ADULTPROFILE_URL);

        else if (requestType == AppConstants.EDIT_FAMILY_REQUEST)
            serviceRequest.setUrl(AppConstants.EDIT_FAMILYPROFILE_URL);

        else if (requestType == AppConstants.EDIT_KIDPROFILE_REQUEST)
            serviceRequest.setUrl(AppConstants.EDIT_KIDPROFILE_URL);

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.EDIT_ADULTPROFILE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("edit profile Response", responseData);

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

            case AppConstants.EDIT_FAMILY_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("edit profile Response", responseData);

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
            case AppConstants.EDIT_KIDPROFILE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("edit profile Response", responseData);

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
    private List<NameValuePair> setRequestParameters(Object requestData, int reqtype) {

        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {


            if (reqtype == AppConstants.EDIT_ADULTPROFILE_REQUEST) {

                SignUpModel.User _requestModel=( SignUpModel.User)requestData;

                nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + _requestModel.getId()));
                nameValuePairs.add(new BasicNameValuePair("family_id", "" + SharedPrefUtils.getUserDetailModel(context).getFamily_id()));
                nameValuePairs.add(new BasicNameValuePair("username", _requestModel.getUsername()));
                nameValuePairs.add(new BasicNameValuePair("emailId", "" + _requestModel.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("color_code", "" + _requestModel.getColor_code()));
                nameValuePairs.add(new BasicNameValuePair("pincode",  _requestModel.getPincode()));
                nameValuePairs.add(new BasicNameValuePair("id",""+SharedPrefUtils.getUserDetailModel(context).getId()));

                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            } else if (reqtype == AppConstants.EDIT_FAMILY_REQUEST) {

                SignUpModel.Family _requestModel=( SignUpModel.Family)requestData;

                nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
                nameValuePairs.add(new BasicNameValuePair("family_id", "" + SharedPrefUtils.getUserDetailModel(context).getFamily_id()));
                nameValuePairs.add(new BasicNameValuePair("family_name", _requestModel.getFamily_name()));
                nameValuePairs.add(new BasicNameValuePair("family_pic", "" + _requestModel.getFamily_image()));
                nameValuePairs.add(new BasicNameValuePair("newpassword", "" + _requestModel.getFamily_password()));
                nameValuePairs.add(new BasicNameValuePair("confirmpassword", _requestModel.getFamily_password()));

                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            }
            else if (reqtype == AppConstants.EDIT_KIDPROFILE_REQUEST) {

                KidsInformation _requestModel=(KidsInformation)requestData;

                nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
                nameValuePairs.add(new BasicNameValuePair("family_id", "" + SharedPrefUtils.getUserDetailModel(context).getFamily_id()));
                nameValuePairs.add(new BasicNameValuePair("kid_id", _requestModel.getKidid()));
                nameValuePairs.add(new BasicNameValuePair("kid_name", "" + _requestModel.getName()));
                nameValuePairs.add(new BasicNameValuePair("color_code", "" + _requestModel.getColor_code()));
                nameValuePairs.add(new BasicNameValuePair("date_of_birth", _requestModel.getDob()));

                encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            }


            System.out.println("edit  data " + nameValuePairs.toString());

            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return nameValuePairs;
    }


}
