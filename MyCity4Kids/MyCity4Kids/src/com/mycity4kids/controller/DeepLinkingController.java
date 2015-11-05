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
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.deeplinking.DeepLinkApiModel;

/**
 * Created by arsh.vardhan on 15-09-2015.
 */

public class DeepLinkingController extends BaseController {

    private Activity context;

    public DeepLinkingController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        String _deeplinkURI = (String) requestData;

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setDataType(requestType);
        serviceRequest.setContext(context);
        serviceRequest.setHttpHeaders(new String[]{"Content-Type"}, new String[]{"application/json"});
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.DEEP_LINKING_URL + getAppendUrl(requestType, _deeplinkURI));
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.DEEP_LINK_RESOLVER_REQUEST:

                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("ConfigurationResponse", responseData);
                    DeepLinkApiModel _deepLinkResponse = new Gson().fromJson(responseData, DeepLinkApiModel.class);
                    response.setResponseObject(_deepLinkResponse);
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

    private String getAppendUrl(int requestType, String deepLinkURL) {
        StringBuilder builder = new StringBuilder();
        /**
         * This will call from SplashActivity.class: for resolving deep link and fetching
         * target screen data from server
         */

        if (AppConstants.DEEP_LINK_RESOLVER_REQUEST == requestType) {
            builder.append("url=").append(deepLinkURL);
        }

        return builder.toString();
    }

}
