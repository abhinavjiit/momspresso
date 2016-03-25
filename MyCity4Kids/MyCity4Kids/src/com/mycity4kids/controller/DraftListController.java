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
import com.mycity4kids.models.editor.ArticleDraftListResponse;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 3/15/16.
 */
public class DraftListController extends BaseController {
    private static final String LOG_TAG = "DraftListController";
    private Activity context;
    public DraftListController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }
    @Override
    public ServiceRequest getData(int requestType, Object requestData)
    {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setDataType(requestType);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(context);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        switch (requestType) {
            case AppConstants.ARTICLE_DRAFT_LIST_REQUEST: {
                serviceRequest.setPostData(setRequestParameters(requestData));
                serviceRequest.setUrl(AppConstants.ARTICLE_DRAFT_LIST_URL);
                break;
            }

        }
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.setDefaultRequestTimeOut(0);
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }
    private List<NameValuePair> setRequestParameters(Object pRequestModel) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            ArticleDraftRequest articleDraftRequest = (ArticleDraftRequest) pRequestModel;
            //nameValuePairs.add(new BasicNameValuePair("user_id", imgUploadRq.getUser_id() ));
            //nameValuePairs.add(new BasicNameValuePair("sessionId", imgUploadRq.getSessionId() ));
            //nameValuePairs.add(new BasicNameValuePair("profileId", imgUploadRq.getProfileId() ));
            nameValuePairs.add(new BasicNameValuePair("userId", articleDraftRequest.getUser_id()));
            // nameValuePairs.add(new BasicNameValuePair("type", imgUploadRq.getType()));
          /*  nameValuePairs.add(new BasicNameValuePair("title", articleDraftRequest.getTitle()));
            nameValuePairs.add(new BasicNameValuePair("body", articleDraftRequest.getBody()));
            nameValuePairs.add(new BasicNameValuePair("id", articleDraftRequest.getId()));*/

            Log.i("DraftRequest", nameValuePairs.toString());
//            encodedEntity = new UrlEncodedFormEntity(nameValuePairs);
        } catch (Exception e) {
            Log.e(LOG_TAG, "setRequestParameters", e);
        }
        return nameValuePairs;
    }
    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.ARTICLE_DRAFT_REQUEST:
                ParentingDetailResponse _forgotResponse;
                try {
                if (response != null) {
                    String responseDatta = new String(response.getResponseData());
                    System.out.println("draft response " + responseDatta);

                    _forgotResponse = new Gson().fromJson(responseDatta, ParentingDetailResponse.class);
                    response.setResponseObject(_forgotResponse);
                    sendResponseToScreen(response);
                } else {
                    sendResponseToScreen(null);
                }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseToScreen(null);
        }
                break;
            case AppConstants.ARTICLE_DRAFT_LIST_REQUEST:
                ArticleDraftListResponse draftListResponse;
                try {
                if (response != null) {
                    String responseDatta = new String(response.getResponseData());
                    System.out.println("draft response " + responseDatta);
                    JSONObject jsonObject = new JSONObject(responseDatta);
                    JSONArray dataObj = jsonObject.getJSONObject("result").optJSONArray("data");

                    if (null == dataObj) {
                        jsonObject.getJSONObject("result").remove("data");
                        jsonObject.getJSONObject("result").put("data", new JSONArray());
                        responseDatta = jsonObject.toString();
                    }

                    draftListResponse = new Gson().fromJson(responseDatta, ArticleDraftListResponse.class);
                    response.setResponseObject(draftListResponse);
                    sendResponseToScreen(response);
                } else {
                    sendResponseToScreen(null);
                }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseToScreen(null);
        }
                break;

        }
    }

    @Override
    public void parseResponse(Response response) {

    }
}
