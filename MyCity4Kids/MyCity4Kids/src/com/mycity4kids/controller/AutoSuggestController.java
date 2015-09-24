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
import com.mycity4kids.models.autosuggest.AutoSuggestResponse;
import com.mycity4kids.models.autosuggest.AutoSuggestReviewResponse;
import com.mycity4kids.preference.SharedPrefUtils;

public class AutoSuggestController extends BaseController {

    private ServiceRequest _request;

    public AutoSuggestController(Activity activity, IScreen screen) {
        super(activity, screen);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);

        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        if (requestType == AppConstants.AUTO_SUGGEST_REQUEST) {
            serviceRequest.setUrl(AppConstants.AUTO_SUGGEST_URL + getAppendUrl((String) requestData,requestType));

        } else if (requestType == AppConstants.BUSINESS_AUTO_SUGGEST_REQUEST) {
            serviceRequest.setUrl(AppConstants.AUTO_SUGGEST_URL + getAppendUrl((String) requestData,requestType));
        } else if (requestType == AppConstants.WRITE_A_REVIEW_AUTO_SUGGEST_REQUEST) {
            serviceRequest.setUrl(AppConstants.WRITE_A_REVIEW_AUTO_SUGGEST_URL + getAppendUrlForReview((String) requestData));
        }

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.setDefaultRequestTimeOut(20000);

        connection.addRequest(serviceRequest);

        _request = serviceRequest;
        return serviceRequest;
    }


    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.AUTO_SUGGEST_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("AutoSuggest Response", responseData);
                    AutoSuggestResponse _loginResponse = new Gson().fromJson(responseData, AutoSuggestResponse.class);
                    response.setResponseObject(_loginResponse);


                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }

                break;

            case AppConstants.BUSINESS_AUTO_SUGGEST_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("AutoSuggest Response", responseData);
                    AutoSuggestResponse _loginResponse = new Gson().fromJson(responseData, AutoSuggestResponse.class);
                    response.setResponseObject(_loginResponse);


                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }

                break;

            case AppConstants.WRITE_A_REVIEW_AUTO_SUGGEST_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    Log.i("AutoSuggest Response", responseData);
                    AutoSuggestReviewResponse _autoSuggest = new Gson().fromJson(responseData, AutoSuggestReviewResponse.class);
                    response.setResponseObject(_autoSuggest);

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


    private String getAppendUrl(String queryString, int requestType) {
        int cityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
        StringBuilder builder = new StringBuilder();

        if (requestType == AppConstants.AUTO_SUGGEST_REQUEST) {
            builder.append("query=").append(queryString).append("&city=").append(cityId).append("&type=events");
        } else if (requestType == AppConstants.BUSINESS_AUTO_SUGGEST_REQUEST) {
            builder.append("query=").append(queryString).append("&city=").append(cityId).append("&type=business");
        }


        return builder.toString().replace(" ", "%20");

    }

    private String getAppendUrlForReview(String queryString) {
        String[] params = queryString.split(",");
        String query = params[0];
        String categoryId = params[1];

        int cityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
        StringBuilder builder = new StringBuilder();

        if (!StringUtils.isNullOrEmpty(query)) {
            builder.append("query=").append(query);
        }

        builder.append("&city=").append(cityId);
        if (!StringUtils.isNullOrEmpty(categoryId)) {
            builder.append("&categoryId=").append(categoryId);
        }

        return builder.toString().replace(" ", "%20");

    }

    public void setCanceled(Boolean iscancel) {
        if (_request != null)
            _request.setCancelled(iscancel);
    }

}
