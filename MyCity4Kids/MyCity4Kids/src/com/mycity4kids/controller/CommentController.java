package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.CommentRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

public class CommentController extends BaseController {

    private Activity context;

    public CommentController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(context);
        serviceRequest.setHttpHeaders(new String[]{HTTP.CONTENT_TYPE}, new String[]{"application/x-www-form-urlencoded"});
        serviceRequest.setPostData(setRequestParameters((CommentRequest) requestData));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setUrl(AppConstants.COMMENT_REPLY_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.COMMENT_REPLY_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("add comment", responseData);
                    CommonResponse _commentResponse = new Gson().fromJson(responseData, CommonResponse.class);
                    if (_commentResponse.getResponseCode() == 200) {
                        response.setResponseObject(_commentResponse);
                    }
                    response.setResponseObject(_commentResponse);
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


    private List<NameValuePair> setRequestParameters(CommentRequest commentRequestData) {
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            nameValuePairs.add(new BasicNameValuePair("sessionId", commentRequestData.getSessionId()));
            if (!StringUtils.isNullOrEmpty(commentRequestData.getParentId())) {
                nameValuePairs.add(new BasicNameValuePair("parent_id", commentRequestData.getParentId()));
            }
            nameValuePairs.add(new BasicNameValuePair("user_id", commentRequestData.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("content", commentRequestData.getContent()));
            nameValuePairs.add(new BasicNameValuePair("article_id", commentRequestData.getArticleId()));
            System.out.println("Comment data " + nameValuePairs.toString());
            encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
        }

        return nameValuePairs;

    }
}
