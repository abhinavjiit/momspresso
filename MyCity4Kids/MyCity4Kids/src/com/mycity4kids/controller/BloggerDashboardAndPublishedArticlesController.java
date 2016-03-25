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
import com.mycity4kids.newmodels.BloggerDashboardModel;
import com.mycity4kids.newmodels.PublishedArticlesModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hemant on 16/3/16.
 */
public class BloggerDashboardAndPublishedArticlesController extends BaseController {

    private Activity activity;

    public BloggerDashboardAndPublishedArticlesController(Activity activity, IScreen screen) {
        super(activity, screen);
        this.activity = activity;
    }

    public ServiceRequest getData(int requestType, int page) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setRequestData(null);
        serviceRequest.setDataType(requestType);
        serviceRequest.setContext(activity);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        if (requestType == AppConstants.GET_BLOGGER_DASHBOARD_REQUEST) {
            serviceRequest.setUrl(AppConstants.GET_BLOGGER_DASHBOARD_URL + getAppendUrl(requestType, page));
        } else if (requestType == AppConstants.GET_BLOGGER_PUBLISHED_ARTICLES_REQUEST) {
            serviceRequest.setUrl(AppConstants.GET_BLOGGER_PUBLISHED_ARTICLES_URL + getAppendUrl(requestType, page));
        }
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    private String getAppendUrl(int requestType, int page) {
        StringBuilder builder = new StringBuilder();
        if (requestType == AppConstants.GET_BLOGGER_DASHBOARD_REQUEST) {
            builder.append("userId=").append(SharedPrefUtils.getUserDetailModel(activity).getId());
        } else if (requestType == AppConstants.GET_BLOGGER_PUBLISHED_ARTICLES_REQUEST) {
            builder.append("userId=").append(SharedPrefUtils.getUserDetailModel(activity).getId());
            builder.append("&page=").append(page);
        }
        return builder.toString().replace(" ", "%20");
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.GET_BLOGGER_DASHBOARD_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("GET_BLOGGER_DASHBOARD_REQUEST Response", responseData);
                    BloggerDashboardModel blogResponse = new Gson().fromJson(responseData, BloggerDashboardModel.class);
                    response.setResponseObject(blogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }

                break;
            case AppConstants.GET_BLOGGER_PUBLISHED_ARTICLES_REQUEST:

                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("GET_BLOGGER_PUBLISHED_ARTICLES_REQUEST response", responseData);
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray dataObj = jsonObject.getJSONObject("result").optJSONArray("data");

                    if (null == dataObj) {
                        jsonObject.getJSONObject("result").remove("data");
                        jsonObject.getJSONObject("result").put("data", new JSONArray());
                        responseData = jsonObject.toString();
                    }
                    PublishedArticlesModel blogResponse = new Gson().fromJson(responseData, PublishedArticlesModel.class);
                    response.setResponseObject(blogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
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
