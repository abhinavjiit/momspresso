package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingstop.ArticleBlogFollowRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ArticleBlogFollowController extends BaseController {

    private static final String LOG_TAG = "ArticleBlogFollowController";

    public ArticleBlogFollowController(Activity activity, IScreen screen) {
        super(activity, screen);
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        if (requestType == AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST) {
            serviceRequest.setDataType(requestType);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setResponseController(this);
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setPostData(setRequestParameters(requestData));
            serviceRequest.setUrl(AppConstants.ARTICLE_BLOG_FOLLOW_URL);
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        }
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    if (BuildConfig.DEBUG) {
                        Log.e("Follow response", responseData);
                    }
                    CommonResponse _followResponse = new Gson().fromJson(responseData, CommonResponse.class);
                    response.setResponseObject(_followResponse);
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
     * @param pRequestModel
     * @return
     */
    private HttpEntity setRequestParameters(Object pRequestModel) {
        UrlEncodedFormEntity encodedEntity = null;
        try {
            ArticleBlogFollowRequest _followRequest = (ArticleBlogFollowRequest) pRequestModel;
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("user_id", _followRequest.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("session_id", _followRequest.getSessionId()));
            nameValuePairs.add(new BasicNameValuePair("author_id", _followRequest.getAuthorId()));
            Log.i("Article Blog Follow Params: ", nameValuePairs.toString());
            encodedEntity = new UrlEncodedFormEntity(nameValuePairs);
        } catch (Exception e) {
            Log.e(LOG_TAG, "setRequestParameters", e);
        }
        return encodedEntity;
    }

	
/*	private String getAppendUrl(int requestType,Object requestData) {
        ArticleBlogFollowRequest _followRequest = (ArticleBlogFollowRequest) requestData;
		StringBuilder builder=new StringBuilder();

		switch (requestType) {
		case AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST:
			if (! StringUtils.isNullOrEmpty(_followRequest.getUser_id())) {
				builder.append("&user_id=").append(_followRequest.getUser_id());
			}
			if(! StringUtils.isNullOrEmpty(_followRequest.getAuthor_id())){
				builder.append("&author_id=").append(_followRequest.getAuthor_id());
			}
			if (! StringUtils.isNullOrEmpty(_followRequest.getUser_id())) {
				builder.append("&article_id=").append(_followRequest.getArticle_id());
			}
			if(! StringUtils.isNullOrEmpty(_followRequest.getAuthor_id())){
				builder.append("&session_id=").append(_followRequest.getSessionId());
			}
			break;
		default:
			break;
		}
		return builder.toString().replace(" ", "%20");

	}
*/
}