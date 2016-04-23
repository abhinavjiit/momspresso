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
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleListResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogDetailWithArticleModel;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.NewArticleListingResponse;
import com.mycity4kids.newmodels.bloggermodel.ParentingBlogResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class NewParentingBlogController extends BaseController {

    private Activity context;

    public NewParentingBlogController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        if (requestType == AppConstants.PARRENTING_BLOG_DATA) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.NEW_PARENTING_BLOG_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.PARRENTING_BLOG_SORT_DATA) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.NEW_PARENTING_BLOG_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.PARRENTING_BLOG_ARTICLE_LISTING) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.NEW_PARENTING_BLOG_ARTICLE_LISTING_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.PARRENTING_BLOG_ARTICLE_LISTING_PAGINATION) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.NEW_PARENTING_BLOG_ARTICLE_LISTING_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.PARRENTING_BLOG_ALL_DATA) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.NEW_PARENTING_BLOG_ARTICLE_LISTING_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        } else if (requestType == AppConstants.SEARCH_AUTHORS_REQUEST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.SEARCH_AUTHORS_URL + getAppendUrl(requestType, requestData));
            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        }
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {

        switch (response.getDataType()) {
            case AppConstants.PARRENTING_BLOG_DATA:
                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Blogger Response", removeHtmlData);
                    ParentingBlogResponse blogResponse = new Gson().fromJson(removeHtmlData, ParentingBlogResponse.class);
                    response.setResponseObject(blogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }

                break;
            case AppConstants.PARRENTING_BLOG_SORT_DATA:

                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Blogger Sort response", removeHtmlData);
                    ParentingBlogResponse blogResponse = new Gson().fromJson(removeHtmlData, ParentingBlogResponse.class);
                    response.setResponseObject(blogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;

            case AppConstants.PARRENTING_BLOG_ARTICLE_LISTING:

                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Blogger Article Listing response", removeHtmlData);
                    BlogArticleListResponse blogResponse = new Gson().fromJson(removeHtmlData, BlogArticleListResponse.class);
                    response.setResponseObject(blogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;
            case AppConstants.PARRENTING_BLOG_ARTICLE_LISTING_PAGINATION:

                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Blogger Article Listing new response", removeHtmlData);
                    NewArticleListingResponse blogResponse = new Gson().fromJson(removeHtmlData, NewArticleListingResponse.class);
                    response.setResponseObject(blogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;
            case AppConstants.PARRENTING_BLOG_ALL_DATA:

                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Blogger response with article listing", removeHtmlData);
                    BlogDetailWithArticleModel blogResponse = new Gson().fromJson(removeHtmlData, BlogDetailWithArticleModel.class);
                    response.setResponseObject(blogResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;
            case AppConstants.SEARCH_AUTHORS_REQUEST:

                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Blogger Search Result ", removeHtmlData);
                    ParentingBlogResponse blogResponse = new Gson().fromJson(removeHtmlData, ParentingBlogResponse.class);
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

    private String getAppendUrl(int requestType, Object requestData) {
        ParentingRequest parentingModel = (ParentingRequest) requestData;
        StringBuilder builder = new StringBuilder();
        String device_id = DataUtils.getDeviceId(getActivity());
        switch (requestType) {

            case AppConstants.PARRENTING_BLOG_DATA:
                boolean flag = false;
                if (!StringUtils.isNullOrEmpty(parentingModel.getPage())) {
                    flag = true;
                    builder.append("?page=").append(parentingModel.getPage());
                }
                if (flag)
                    builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
                else
                    builder.append("?user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
                break;

            case AppConstants.PARRENTING_BLOG_SORT_DATA:
                if (parentingModel.getPage().equalsIgnoreCase("0")) {
                    builder.append("?sort=").append(parentingModel.getSoty_by());
                    builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
                } else {
                    builder.append("?sort=").append(parentingModel.getSoty_by());
                    builder.append("&page=").append(parentingModel.getPage());
                    builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
                }
                break;

            case AppConstants.PARRENTING_BLOG_ARTICLE_LISTING:
                builder.append("search?q=").append(parentingModel.getSearchName());
                builder.append("&type=blogs&filter=blogs");
                builder.append("&cityId=").append(parentingModel.getCity_id());
                builder.append("&sort=all");
                builder.append("&page=1");
                builder.append("&imei_no=" + device_id);
                break;

            case AppConstants.PARRENTING_BLOG_ARTICLE_LISTING_PAGINATION:
                builder.append("search?q=").append(parentingModel.getSearchName());
                builder.append("&type=blogs&filter=blogs");
                builder.append("&cityId=").append(parentingModel.getCity_id());
                builder.append("&sort=").append(parentingModel.getSoty_by());
                builder.append("&page=").append(parentingModel.getPage());
                builder.append("&imei_no=" + device_id);
                break;

            case AppConstants.PARRENTING_BLOG_ALL_DATA:
                builder.append("search?q=").append(parentingModel.getSearchName());
                builder.append("&type=blogs");
                builder.append("&filter=").append(parentingModel.getSoty_by());
                builder.append("&cityId=").append(parentingModel.getCity_id());
                builder.append("&sort=all");
                builder.append("&page=1");
                builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
                builder.append("&imei_no=" + device_id);
                break;

            case AppConstants.SEARCH_AUTHORS_REQUEST:
                builder.append("q=").append(parentingModel.getSearchName());
                builder.append("&page=").append(parentingModel.getPage());
                break;

            default:
                break;
        }

        return builder.toString().replace(" ", "%20");

    }

}