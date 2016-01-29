package com.chatPlatform.Controllers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.chatPlatform.models.GroupInfo;
import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 1/6/16.
 */
public class ControllerCreateGroup extends BaseController{
    Activity activity;
    public ControllerCreateGroup(Activity activity, IScreen screen) {
        super(activity, screen);
        this.activity=activity;
    }
    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(activity);
        GroupInfo data = (GroupInfo)requestData;
//        String data = new Gson().toJson(requestData);

//        try {
//            StringEntity entity = new StringEntity(data);
//            serviceRequest.setPostData(entity);
//        } catch (Exception ex) {
//
//        }
        serviceRequest.setPostData(setRequestParameters(data));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        //	serviceRequest.setUrl("http://54.251.100.249/webservices/apiusers/registration?emailId=deepanker.chaudhary.1990@gmail.com&password=123456&FirstName=deep&LastName=chaudhary&MobileNumber=1235678990&cityId=1");
        //	serviceRequest.setUrl(AppConstants.REGISTRATION_URL+getAppendUrl((UserRequest)requestData));
        serviceRequest.setUrl("http://10.42.0.1/apiusers/group_invites");
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        String responseData1 = new String(response.getResponseData());
        Log.e("responsePrint", responseData1);
        switch (response.getDataType()) {
            case AppConstants.SIGNUP_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("SIGNUP Response", responseData);

                    UserResponse _signUpData = new Gson().fromJson(responseData, UserResponse.class);
                    response.setResponseObject(_signUpData);

                    if (_signUpData.getResponseCode() == 200) {
                        saveUserDetails(getActivity(), _signUpData, (UserResponse) response.getResponseObject());
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
     * these method was for GET Registration:
     *
     * @param _userRequest
     * @return
     */


    /**
     * this method creates Registration request
     * // * @param requestData
     *
     * @return
     */
    private List<NameValuePair> setRequestParameters(GroupInfo requestData) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            String data = new Gson().toJson(requestData.getInviteList());


                nameValuePairs.add(new BasicNameValuePair("groupId", requestData.getGroupId()));
                nameValuePairs.add(new BasicNameValuePair("userId",""+ requestData.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("inviteList",data));


            encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

            System.out.println("JSON " + nameValuePairs);

            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
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

    public static void clearPreference(Context context) {
        SharedPrefUtils.setProfileImgUrl(context, "");
    }



}
