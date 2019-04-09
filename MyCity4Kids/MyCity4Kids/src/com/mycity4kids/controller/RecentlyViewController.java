package com.mycity4kids.controller;

import android.app.Activity;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.newmodels.ExternalEventModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.RecentlyViewedActivity;

public class RecentlyViewController extends BaseController {

    private Activity context;
    private boolean fromEvents;

    public RecentlyViewController(Activity activity, IScreen screen) {
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
        if (getAppendUrl(requestData).equals("") || getAppendUrl(requestData) == null) {
            RecentlyViewedActivity _activity = (RecentlyViewedActivity) getActivity();
            _activity.removeProgressDialog();
            return null;
        }
        serviceRequest.setUrl(AppConstants.RECENTLY_VIEWED_URL + getAppendUrl(requestData));

        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.RECENTLY_VIEWED_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());

                    BusinessListResponse recentlyViewedData = new Gson().fromJson(responseData, BusinessListResponse.class);
                    response.setResponseObject(recentlyViewedData);
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

    private String getAppendUrl(Object pRequestData) {
        ExternalEventModel _requestModel = (ExternalEventModel) pRequestData;
        fromEvents = _requestModel.isfromEvents();

        StringBuilder builder = new StringBuilder();
        builder.append("&user_id=").append(SharedPrefUtils.getUserDetailModel(context).getId());
        builder.append("&type=").append(_requestModel.isfromEvents() ? "events" : "business");
        return builder.toString().replace(" ", "%20");

    }

}
