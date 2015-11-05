package com.mycity4kids.controller;

import org.apache.commons.lang3.StringEscapeUtils;

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
import com.mycity4kids.models.parentingfilter.ArticleBlogFilterResponse;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * @author deepanker.chaudhary
 */
public class ParentingFilterSearchController extends BaseController {

    private Activity context;

    public ParentingFilterSearchController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }


    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setDataType(requestType);
        serviceRequest.setContext(context);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.ARTICLE_BLOG_SEARCH_FILTER_URL + getAppendUrl(requestData));
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }


    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.ARTICLE_SEARCH_FILTER_REQUEST:
            case AppConstants.BLOG_SEARCH_FILTER_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("Article Blogs Filter Response", responseData);
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    ArticleBlogFilterResponse filterResponse = new Gson().fromJson(removeHtmlData, ArticleBlogFilterResponse.class);
                    response.setResponseObject(filterResponse);
                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }
                break;
        }
    }

    private String getAppendUrl(Object requestData) {
        String parentingType = (String) requestData;
        StringBuilder builder = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(parentingType)) {
            builder.append("type=").append(parentingType);
        }
        int cityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
        if (cityId != 0) {
            builder.append("&city_id=").append(cityId);
        }
        return builder.toString().replace(" ", "%20");

    }

    @Override
    public void parseResponse(Response response) {
        // TODO Auto-generated method stub

    }

}