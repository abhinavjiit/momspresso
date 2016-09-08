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
import com.mycity4kids.models.WriteReviewModel;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class WriteReviewController extends BaseController {

    private Activity context;

    public WriteReviewController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(context);
        serviceRequest.setPostData(setRequestParameters((WriteReviewModel) requestData));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.WRITE_A_REVIEW_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.WRITE_A_REVIEW_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("Response:", responseData);
                    CommonResponse _writeReviewResponse = new Gson().fromJson(responseData, CommonResponse.class);
                    response.setResponseObject(_writeReviewResponse);
                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }

                break;

            default:
                break;
        }

    }

    private List<NameValuePair> setRequestParameters(WriteReviewModel writeReviewModel) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {

            nameValuePairs.add(new BasicNameValuePair("title", writeReviewModel.getTitle()));
            nameValuePairs.add(new BasicNameValuePair("reviewType", writeReviewModel.getReviewType()));
            nameValuePairs.add(new BasicNameValuePair("rating", writeReviewModel.getRating()));
            nameValuePairs.add(new BasicNameValuePair("description", writeReviewModel.getDescription()));
            if (writeReviewModel.getImage() != null) {
                nameValuePairs.add(new BasicNameValuePair("image", writeReviewModel.getImage().toString()));
            }
            nameValuePairs.add(new BasicNameValuePair("userId", writeReviewModel.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("type", writeReviewModel.getType()));
            nameValuePairs.add(new BasicNameValuePair("businessId", writeReviewModel.getBusinessId()));
            nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameValuePairs;
    }

    @Override
    public void parseResponse(Response response) {
        // TODO Auto-generated method stub

    }

}
