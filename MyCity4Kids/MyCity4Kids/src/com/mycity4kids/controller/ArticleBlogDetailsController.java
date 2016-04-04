package com.mycity4kids.controller;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.mycity4kids.newmodels.GetCommentsRequestModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.commons.lang3.StringEscapeUtils;

public class ArticleBlogDetailsController extends BaseController {

    private Activity context;

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setContext(context);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        if (AppConstants.ARTICLES_DETAILS_REQUEST == requestType) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
//            serviceRequest.setUrl(AppConstants.ARTICLES_BLOGS_DETAILS_URL + getAppendUrl(requestData));
            serviceRequest.setUrl(AppConstants.ARTICLES_BLOGS_DETAILS_URL_V1 + getAppendUrl(requestType, requestData));
        } else if (AppConstants.GET_MORE_COMMENTS == requestType) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setUrl(AppConstants.ARTICLES_BLOGS_COMMENT_URL + getAppendUrl(requestType, requestData));
        }
        //serviceRequest.setUrl("http://54.251.100.249/webservices/apiparentingstop/detail_article?article_id=1655");
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }


    public ArticleBlogDetailsController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
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
            case AppConstants.GET_MORE_COMMENTS:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("COMMENTS Response", responseData);
                    response.setResponseObject(responseData);
                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }
                break;
        }
    }

    private String getAppendUrl(int requestType, Object requestData) {
        StringBuilder builder = new StringBuilder();
        if (requestType == AppConstants.GET_MORE_COMMENTS) {
            GetCommentsRequestModel getCommentsRequestModel = (GetCommentsRequestModel) requestData;
            builder.append("article_id=").append(getCommentsRequestModel.getArticleId());
            builder.append("&limit=").append(getCommentsRequestModel.getLimit());
            builder.append("&offset=").append(getCommentsRequestModel.getOffset());
            builder.append("&comment_type=").append(getCommentsRequestModel.getCommentType());
        } else {
            String authorId = (String) requestData;
            if (!StringUtils.isNullOrEmpty(authorId)) {
                builder.append("article_id=").append(authorId);
            }
            String device_id = DataUtils.getDeviceId(getActivity());
            if (!StringUtils.isNullOrEmpty(device_id)) {
                builder.append("&imei_no=").append(device_id);
            }
        }

        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = pInfo.versionName;
        builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
        builder.append("&app_version=").append(versionName);
        return builder.toString().replace(" ", "%20");

    }

    @Override
    public void parseResponse(Response response) {
        // TODO Auto-generated method stub

    }

}
