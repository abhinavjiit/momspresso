package com.mycity4kids.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

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
import com.mycity4kids.models.profile.SaveProfileRequest;
import com.mycity4kids.models.profile.ViewProfileRequest;
import com.mycity4kids.models.profile.ViewProfileResponse;

public class ProfileController extends BaseController {

    private static final String LOG_TAG = "ProfileController";

    private Activity context;

    public ProfileController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setDataType(requestType);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setContext(context);
        switch (requestType) {
            case AppConstants.VIEW_PROFILE_REQUEST:
                serviceRequest.setPostData(setRequestParameters(requestData));
                serviceRequest.setUrl(AppConstants.VIEW_PROFILE_URL);
                break;
            case AppConstants.SAVE_PROFILE_REQUEST:
                serviceRequest.setPostData(setSaveProfileReqParams(requestData));
                serviceRequest.setUrl(AppConstants.SAVE_PROFILE_URL);
                break;
            default:
                break;
        }

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    /**
     * @param pRequestModel
     * @return
     */
    private List<NameValuePair> setRequestParameters(Object pRequestModel) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            ViewProfileRequest _viewProfileRequest = (ViewProfileRequest) pRequestModel;
            nameValuePairs.add(new BasicNameValuePair("userId", _viewProfileRequest.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("sessionId", _viewProfileRequest.getSessionId()));
            Log.i("View Profile", nameValuePairs.toString());
            encodedEntity = new UrlEncodedFormEntity(nameValuePairs);
        } catch (Exception e) {
            Log.e(LOG_TAG, "setRequestParameters", e);
        }
        return nameValuePairs;
    }

    /**
     * @param pRequestModel
     * @return
     */
    private List<NameValuePair> setSaveProfileReqParams(Object pRequestModel) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            SaveProfileRequest _saveProfileRequest = (SaveProfileRequest) pRequestModel;
            nameValuePairs.add(new BasicNameValuePair("profileId", _saveProfileRequest.getProfileId()));
            nameValuePairs.add(new BasicNameValuePair("userId", _saveProfileRequest.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("sessionId", _saveProfileRequest.getSessionId()));
            nameValuePairs.add(new BasicNameValuePair("name", _saveProfileRequest.getName()));
            nameValuePairs.add(new BasicNameValuePair("emailId", _saveProfileRequest.getEmailId()));
            nameValuePairs.add(new BasicNameValuePair("MobileNumber", _saveProfileRequest.getMobileNumber()));
//			nameValuePairs.add(new BasicNameValuePair("parentType", _saveProfileRequest.getParentType()));
            nameValuePairs.add(new BasicNameValuePair("cityId", _saveProfileRequest.getCityId()));
            nameValuePairs.add(new BasicNameValuePair("localityId", _saveProfileRequest.getLocalityId()));

            ArrayList<KidsInformation> kidsInformationList = _saveProfileRequest.getKidsInformation();
            JSONArray kidsInformation = new JSONArray();
            for (KidsInformation kidsInfo : kidsInformationList) {
                JSONObject kidData = new JSONObject();
                kidData.put("name", kidsInfo.getName());
                kidData.put("gender", kidsInfo.getGender());
                kidData.put("dob", kidsInfo.getDob());
                kidData.put("id", kidsInfo.getId());
                kidsInformation.put(kidData);

            }
            if (kidsInformationList.size() > 0) {
                nameValuePairs.add(new BasicNameValuePair("KidsInfo", kidsInformation.toString()));
            }

            Log.i("Save Profile: Json", new Gson().toJson(nameValuePairs));

        } catch (Exception e) {
            Log.e(LOG_TAG, "setSaveProfileReqParams", e);
        }
        return nameValuePairs;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.VIEW_PROFILE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("View Profile Response", responseData);
                    ViewProfileResponse _viewProfileResponse = new Gson().fromJson(responseData, ViewProfileResponse.class);
                    response.setResponseObject(_viewProfileResponse);
                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }
                break;
            case AppConstants.SAVE_PROFILE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("Save Profile Response", responseData);
                    CommonResponse _forgotResponse = new Gson().fromJson(responseData, CommonResponse.class);
                    response.setResponseObject(_forgotResponse);
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