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
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.parentingmodel.ArticleModelNew;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.commons.lang3.StringEscapeUtils;

public class ParentingStopController extends BaseController {

    private Activity context;

    public ParentingStopController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        if (requestType == AppConstants.PARENTING_STOP_ARTICLES_REQUEST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.PARENTING_STOP_ARTICLE_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.PARENTING_STOP_BLOGS_REQUEST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.PARENTING_STOP_BLOGS_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.TOP_PICKS_REQUEST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.PARENTING_NEW_TOP_PICKS_URL + getAppendUrl(requestType, requestData));

            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.NEW_ALL_ARTICLES_REQUEST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.NEW_ALL_ARTICLE_URL + getAppendUrl(requestType, requestData));

            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.ARTICLES_TODAY_REQUEST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.NEW_ALL_ARTICLE_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.BOOKMARKED_ARTICLE_LIST_REQUEST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.FETCH_BOOKMARK_URL + getAppendUrl(requestType, requestData));

            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        }

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.PARENTING_STOP_ARTICLES_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Article Response", removeHtmlData);
                    CommonParentingResponse articleBlogResponse = new Gson().fromJson(removeHtmlData, CommonParentingResponse.class);
                    response.setResponseObject(articleBlogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }

                break;
            case AppConstants.TOP_PICKS_REQUEST:

                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("TopPics Response Article", removeHtmlData);
                    CommonParentingResponse articleBlogResponse = new Gson().fromJson(removeHtmlData, CommonParentingResponse.class);
                    response.setResponseObject(articleBlogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;

            case AppConstants.PARENTING_STOP_BLOGS_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Article OR Blogs Or TopPics Response", removeHtmlData);
                    CommonParentingResponse articleBlogResponse = new Gson().fromJson(removeHtmlData, CommonParentingResponse.class);
                    response.setResponseObject(articleBlogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;

            case AppConstants.NEW_ALL_ARTICLES_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("All article Response", removeHtmlData);
                    ArticleModelNew articleBlogResponse = new Gson().fromJson(removeHtmlData, ArticleModelNew.class);
                    response.setResponseObject(articleBlogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }
                break;

            case AppConstants.ARTICLES_TODAY_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    //String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("today article Response", responseData);
                    CommonParentingResponse articleBlogResponse = new Gson().fromJson(responseData, CommonParentingResponse.class);
                    response.setResponseObject(articleBlogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }
                break;
            case AppConstants.BOOKMARKED_ARTICLE_LIST_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    //String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("today article Response", responseData);
                    CommonParentingResponse articleBlogResponse = new Gson().fromJson(responseData, CommonParentingResponse.class);
                    response.setResponseObject(articleBlogResponse);

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

    private String getAppendUrl(int requestType, Object requestData) {
        String pincode = SharedPrefUtils.getpinCode(BaseApplication.getAppContext());
        ParentingRequest parentingModel = (ParentingRequest) requestData;
        StringBuilder builder = new StringBuilder();
        String device_id = DataUtils.getDeviceId(getActivity());
        switch (requestType) {
            case AppConstants.PARENTING_STOP_ARTICLES_REQUEST:

                builder.append("city_id=").append(parentingModel.getCity_id());
                if (!StringUtils.isNullOrEmpty(parentingModel.getPage())) {
                    builder.append("&page=").append(parentingModel.getPage());
                }
                if (!StringUtils.isNullOrEmpty(pincode)) {
                    builder.append("&pincode=").append(pincode);
                }
                if (!StringUtils.isNullOrEmpty(parentingModel.getSoty_by())) {
                    builder.append("&sort=").append(parentingModel.getSoty_by());
                }

//                if (!StringUtils.isNullOrEmpty(device_id)) {
//                    builder.append("&imei_no=").append(device_id);
//                }
                break;

            case AppConstants.PARENTING_STOP_BLOGS_REQUEST:
            case AppConstants.TOP_PICKS_REQUEST:

                builder.append("q=").append(parentingModel.getSearchName());
                builder.append("&type=articles");
                builder.append("&filter=topics");

                builder.append("&cityId=").append(parentingModel.getCity_id());

                if (!StringUtils.isNullOrEmpty(parentingModel.getSoty_by())) {
                    builder.append("&sort=").append(parentingModel.getSoty_by());
                }

                if (!StringUtils.isNullOrEmpty(parentingModel.getPage())) {
                    builder.append("&page=").append(parentingModel.getPage());
                }
                if (!StringUtils.isNullOrEmpty(device_id)) {
                    builder.append("&imei_no=").append(device_id);
                }
                break;
            case AppConstants.PARENTING_STOP_BLOGGER_REQUEST:
                builder.append("city_id=").append(parentingModel.getCity_id());

                if (!StringUtils.isNullOrEmpty(parentingModel.getAuthorId())) {
                    builder.append("author_id").append(parentingModel.getPage());
                }
                if (!StringUtils.isNullOrEmpty(parentingModel.getPage())) {
                    builder.append("&page=").append(parentingModel.getPage());
                }
                if (!StringUtils.isNullOrEmpty(parentingModel.getSoty_by())) {
                    builder.append("&sort=").append(parentingModel.getSoty_by());
                }

                if (!StringUtils.isNullOrEmpty(device_id)) {
                    builder.append("&imei_no=").append(device_id);
                }
                break;

            case AppConstants.NEW_ALL_ARTICLES_REQUEST:
                builder.append("city_id=").append(parentingModel.getCity_id());
                if (!StringUtils.isNullOrEmpty(parentingModel.getPage())) {
                    builder.append("&page=").append(parentingModel.getPage());
                }
                if (!StringUtils.isNullOrEmpty(pincode)) {
                    builder.append("&pincode=").append(pincode);
                }
                builder.append("&sort=").append("all");
                break;
            case AppConstants.ARTICLES_TODAY_REQUEST:
                builder.append("city_id=").append(parentingModel.getCity_id());

                if (!StringUtils.isNullOrEmpty(parentingModel.getPage())) {
                    builder.append("&page=").append(parentingModel.getPage());
                }
                if (!StringUtils.isNullOrEmpty(pincode)) {
                    builder.append("&pincode=").append(pincode);
                }
                builder.append("&sort=").append("trending_today");

                break;
            case AppConstants.BOOKMARKED_ARTICLE_LIST_REQUEST:
                builder.append("user_id=").append(SharedPrefUtils.getUserDetailModel(context).getId());

                if (!StringUtils.isNullOrEmpty(parentingModel.getPage())) {
                    builder.append("&page=").append(parentingModel.getPage());
                }

                break;
            default:
                break;
        }

        return builder.toString().replace(" ", "%20");

    }


}
