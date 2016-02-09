package com.mycity4kids.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.DataUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.FamilyInvites;
import com.mycity4kids.newmodels.UserInviteModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ListFamilyInvitesActivity;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 2/2/16.
 */
public class FamilyInvitationController extends BaseController {

    private final Activity activity;

    public FamilyInvitationController(Activity activity, IScreen screen) {
        super(activity, screen);
        this.activity = activity;
    }

    public ServiceRequest getData(int requestType, Object requestData, UserInviteModel userInfo) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(activity);
        serviceRequest.setPostData(setRequestParameters(userInfo, requestData, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.ACCEPT_OR_REJECT_INVITE_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    /**
     * this method creates Registration request
     * // * @param requestData
     *
     * @return
     */
    private List<NameValuePair> setRequestParameters(UserInviteModel userInfo, Object requestData, int requestType) {
        UrlEncodedFormEntity encodedEntity = null;
        FamilyInvites model = (FamilyInvites) requestData;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        try {
            nameValuePairs.add(new BasicNameValuePair("userId", "" + userInfo.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("email", userInfo.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("mobile", userInfo.getMobile()));
            nameValuePairs.add(new BasicNameValuePair("invitationId", "" + model.getInvitationId()));
            nameValuePairs.add(new BasicNameValuePair("familyId", "" + model.getFamilyId()));
            nameValuePairs.add(new BasicNameValuePair("colorCode", "" + model.getColorCode()));
            nameValuePairs.add(new BasicNameValuePair("pushToken", "" + SharedPrefUtils.getDeviceToken(activity)));
            nameValuePairs.add(new BasicNameValuePair("deviceId", "" + DataUtils.getDeviceId(activity)));
            nameValuePairs.add(new BasicNameValuePair("pictureUrl", "" + model.getProfileImage()));
            System.out.println("JSON " + nameValuePairs);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return nameValuePairs;

    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.ACCEPT_OR_REJECT_INVITE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                /*String[] data=responseData.split("-->");
                String finalData=data[1].trim();*/
                    Log.i("Login Response", responseData);
                    UserResponse _loginResponse = new Gson().fromJson(responseData, UserResponse.class);
                    response.setResponseObject(_loginResponse);
                    /**
                     * if response code is 200 then user is logged in and we save login details In shared pref
                     * & send to response to login screen
                     */
                    if (_loginResponse.getResponseCode() == 200) {
                        _loginResponse.setLoggedIn(true);
                        saveUserDetails(getActivity(), _loginResponse, (UserResponse) response.getResponseObject());
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

    public void saveUserDetails(Context context, UserResponse pUserDetails, UserResponse requestdata) {

        try {
            UserTable userTable = new UserTable((BaseApplication) ((Activity) context).getApplication());
            userTable.deleteAll();
            userTable.insertData(requestdata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parseResponse(Response response) {

    }
}
