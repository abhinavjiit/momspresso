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
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.editor.BlogDataResponse;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.user.BusinessImageUploadRequest;
import com.mycity4kids.models.user.ImageUploadRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 3/14/16.
 */
public class ArticleDraftController extends BaseController {


    private static final String LOG_TAG = "ArticleDraftController";
    private Activity context;

    public ArticleDraftController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setDataType(requestType);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(context);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        switch (requestType) {
            case AppConstants.ARTICLE_DRAFT_REQUEST: {
                serviceRequest.setPostData(setRequestParameters(requestData));
                serviceRequest.setUrl(AppConstants.ARTICLE_DRAFT_URL);
                break;
            }
            case AppConstants.BLOG_DATA_REQUEST: {
                serviceRequest.setPostData(setRequestParameters(requestData));
                serviceRequest.setUrl(AppConstants.BLOG_DATA_URL);
                break;
            }
          /*  case AppConstants.IMAGE_EDITOR_UPLOAD_REQUEST: {
                serviceRequest.setPostData(setRequestParameters(requestData));
                serviceRequest.setUrl(AppConstants.IMAGE_EDITOR_UPLOAD_URL);
                break;
            }

            case AppConstants.FILE_UPLOAD_REQ: {
                serviceRequest.setPostData(setRequestParameters(requestData));
                serviceRequest.setUrl(AppConstants.FILE_UPLOAD_URL);
                break;
            }
            case AppConstants.UPLOAD_BUSINESS_IMAGE_REQUEST: {
                serviceRequest.setPostData(createUploadBusinessImageEntity(requestData));
                serviceRequest.setUrl(AppConstants.UPLOAD_BUSINESS_IMAGE_URL);
                break;
            }
            case AppConstants.FILE_UPLOAD_REQ_TASK: {
                serviceRequest.setPostData(
                        setRequestParameters(requestData));
                serviceRequest.setUrl(AppConstants.FILE_UPLOAD_URL_TASK);
                break;
            }*/
        }
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.setDefaultRequestTimeOut(0);
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
            ArticleDraftRequest articleDraftRequest = (ArticleDraftRequest) pRequestModel;
            //nameValuePairs.add(new BasicNameValuePair("user_id", imgUploadRq.getUser_id() ));
            //nameValuePairs.add(new BasicNameValuePair("sessionId", imgUploadRq.getSessionId() ));
            //nameValuePairs.add(new BasicNameValuePair("profileId", imgUploadRq.getProfileId() ));
            nameValuePairs.add(new BasicNameValuePair("userId", articleDraftRequest.getUser_id()));
            // nameValuePairs.add(new BasicNameValuePair("type", imgUploadRq.getType()));
            nameValuePairs.add(new BasicNameValuePair("title", articleDraftRequest.getTitle()));
            nameValuePairs.add(new BasicNameValuePair("body", articleDraftRequest.getBody()));
            nameValuePairs.add(new BasicNameValuePair("id", articleDraftRequest.getId()));
            nameValuePairs.add(new BasicNameValuePair("status", articleDraftRequest.getStatus()));
            nameValuePairs.add(new BasicNameValuePair("sourceId", articleDraftRequest.getSourceId()));

            Log.i("DraftReuest", nameValuePairs.toString());
//            encodedEntity = new UrlEncodedFormEntity(nameValuePairs);
        } catch (Exception e) {
            Log.e(LOG_TAG, "setRequestParameters", e);
        }
        return nameValuePairs;
    }

    /**
     * @param pRequestModel
     * @return
     */
    private List<NameValuePair> createUploadBusinessImageEntity(Object pRequestModel) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            BusinessImageUploadRequest imgUploadRq = (BusinessImageUploadRequest) pRequestModel;
            nameValuePairs.add(new BasicNameValuePair("userId", imgUploadRq.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("sessionId", imgUploadRq.getSessionId()));
            nameValuePairs.add(new BasicNameValuePair("businessId", imgUploadRq.getBusinessId()));
            nameValuePairs.add(new BasicNameValuePair("type", imgUploadRq.getType()));
            nameValuePairs.add(new BasicNameValuePair("image", imgUploadRq.getImage()));
            Log.i("imageUpload", nameValuePairs.toString());
            encodedEntity = new UrlEncodedFormEntity(nameValuePairs);
        } catch (Exception e) {
            Log.e(LOG_TAG, "createUploadBusinessImageEntity", e);
        }
        return nameValuePairs;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.ARTICLE_DRAFT_REQUEST:
                try {
                ParentingDetailResponse _forgotResponse;
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
                try {
                if (response != null) {
                    CommonResponse _forgotResponse1;
                    String responseDatta = new String(response.getResponseData());
                    System.out.println("img response " + responseDatta);

                    _forgotResponse1 = new Gson().fromJson(responseDatta, CommonResponse.class);
                    response.setResponseObject(_forgotResponse1);
                    sendResponseToScreen(response);
                } else {
                    sendResponseToScreen(null);
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }

                break;
            case AppConstants.BLOG_DATA_REQUEST:
                try {
                if (response != null) {
                    BlogDataResponse _forgotResponse1;
                    String responseDatta = new String(response.getResponseData());
                    System.out.println("img response " + responseDatta);

                    _forgotResponse1 = new Gson().fromJson(responseDatta, BlogDataResponse.class);
                    response.setResponseObject(_forgotResponse1);
                    sendResponseToScreen(response);
                } else {
                    sendResponseToScreen(null);
                }

                break;
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseToScreen(null);
        }

        }
    }

    @Override
    public void parseResponse(Response response) {
        // nothing to do here
    }
}
