package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.commons.lang3.StringEscapeUtils;

public class ArticleBlogDetailsController extends BaseController {


    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.ARTICLES_BLOGS_DETAILS_URL + getAppendUrl(requestData));
        //serviceRequest.setUrl("http://54.251.100.249/webservices/apiparentingstop/detail_article?article_id=1655");
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }


    public ArticleBlogDetailsController(Activity activity, IScreen screen) {
        super(activity, screen);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.ARTICLES_DETAILS_REQUEST:
            case AppConstants.BLOGS_DETAILS_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("Article Blogs Details Response", responseData);
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    ParentingDetailResponse detailsResponse = new Gson().fromJson(removeHtmlData, ParentingDetailResponse.class);
                    response.setResponseObject(detailsResponse);
                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }
                break;
        }
    }

    private String getAppendUrl(Object requestData) {
        String authorId = (String) requestData;
        StringBuilder builder = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(authorId)) {
            builder.append("article_id=").append(authorId);
        }
        String device_id = DataUtils.getDeviceId(getActivity());
        if (!StringUtils.isNullOrEmpty(device_id)) {
            builder.append("&imei_no=").append(device_id);
        }

        if (!StringUtils.isNullOrEmpty(authorId)) {
            builder.append("article_id=").append(authorId);
        }
        String versionName = BuildConfig.VERSION_NAME;
        builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
        builder.append("&app_version=").append(versionName);
        return builder.toString().replace(" ", "%20");

    }

    @Override
    public void parseResponse(Response response) {
        // TODO Auto-generated method stub

    }

}
