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
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.parentingmodel.ArticleFilterListModel;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by manish.soni on 22-07-2015.
 */
public class ParentingFilterController extends BaseController {

    private Activity context;

    public ParentingFilterController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        if (requestType == AppConstants.PARENTING_FILTER_LIST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
            serviceRequest.setUrl(AppConstants.PARENTING_FILTER_DATA_URL + getAppendUrl(requestType, requestData));

            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        }

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {

            case AppConstants.PARENTING_FILTER_LIST:
                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("All filter List", removeHtmlData);
                    ArticleFilterListModel articleBlogResponse = new Gson().fromJson(removeHtmlData, ArticleFilterListModel.class);
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
        ParentingRequest parentingModel = (ParentingRequest) requestData;
        StringBuilder builder = new StringBuilder();
        String device_id = DataUtils.getDeviceId(getActivity());
        switch (requestType) {

            case AppConstants.PARENTING_FILTER_LIST:

                builder.append("type=articles");
                builder.append("&city_id=").append(parentingModel.getCity_id());
                break;

            default:
                break;
        }

        return builder.toString().replace(" ", "%20");

    }
}

