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
import com.mycity4kids.newmodels.CreateFamilyModel;
import com.mycity4kids.newmodels.NewSignUpModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 5/2/16.
 */
public class CreateFamilyController extends BaseController {

    private Activity activity;

    public CreateFamilyController(Activity activity, IScreen screen) {
        super(activity, screen);
        this.activity = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        CreateFamilyModel createFamilyModel = (CreateFamilyModel) requestData;
        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(activity);
        serviceRequest.setPostData(setRequestParameters(createFamilyModel, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        //	serviceRequest.setUrl("http://54.251.100.249/webservices/apiusers/registration?emailId=deepanker.chaudhary.1990@gmail.com&password=123456&FirstName=deep&LastName=chaudhary&MobileNumber=1235678990&cityId=1");
        //	serviceRequest.setUrl(AppConstants.REGISTRATION_URL+getAppendUrl((UserRequest)requestData));
        serviceRequest.setUrl(AppConstants.CREATE_FAMILY_URL);
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
    private List<NameValuePair> setRequestParameters(CreateFamilyModel requestData, int requestType) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String kidsJson = new Gson().toJson(requestData.getKidsInformationArrayList());
        String adultsJson = new Gson().toJson(requestData.getInviteUserList());

        try {
            nameValuePairs.add(new BasicNameValuePair("userId", requestData.getUserId()));
//            nameValuePairs.add(new BasicNameValuePair("profileImageUrl", "" + requestData.getProfileImageUrl()));
//            nameValuePairs.add(new BasicNameValuePair("userColorCode", "" + requestData.getUserColorCode()));
//            nameValuePairs.add(new BasicNameValuePair("cityId", "" + SharedPrefUtils.getCurrentCityModel(activity).getId()));
            nameValuePairs.add(new BasicNameValuePair("familyName", "" + requestData.getFamilyName()));
//            nameValuePairs.add(new BasicNameValuePair("familyName", "" + SharedPrefUtils.getUserDetailModel(activity).getPincode()));
            nameValuePairs.add(new BasicNameValuePair("kidsInformation", "" + kidsJson));
            nameValuePairs.add(new BasicNameValuePair("spouseInformation", "" + adultsJson));

            System.out.println("JSON " + nameValuePairs);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return nameValuePairs;

    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.CREATE_FAMILY_REQUEST:
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

    public void saveUserDetails(Context context, UserResponse pUserDetails, UserResponse requestdata) {

        try {
            UserTable userTable = new UserTable((BaseApplication) ((Activity) context).getApplication());
            userTable.deleteAll();
            userTable.insertData(requestdata);

        } catch (Exception e) {

        }
    }

    @Override
    public void parseResponse(Response response) {

    }
}
