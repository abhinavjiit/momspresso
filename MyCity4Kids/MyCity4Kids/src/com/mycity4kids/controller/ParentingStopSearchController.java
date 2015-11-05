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
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.parentingfilter.ParentingSearchRequest;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;

public class ParentingStopSearchController extends BaseController {

    private Activity context;

    public ParentingStopSearchController(Activity activity, IScreen screen) {
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
        serviceRequest.setUrl(AppConstants.PARENTING_STOP_SEARCH_URL + getAppendUrl(requestData));
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.PARENTING_STOP_SEARCH_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    String removeHtmlData = StringEscapeUtils.unescapeHtml4(responseData);
                    Log.i("Article or Blogs search response", removeHtmlData);
                    CommonParentingResponse articleBlogResponse = new Gson().fromJson(removeHtmlData, CommonParentingResponse.class);
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

    private String getAppendUrl(Object requestData) {
        ParentingSearchRequest PaentingSearchData = (ParentingSearchRequest) requestData;
        StringBuilder builder = new StringBuilder();

        if (!StringUtils.isNullOrEmpty(PaentingSearchData.getQuery())) {
            builder.append("q=").append(PaentingSearchData.getQuery());
        }
        if (!StringUtils.isNullOrEmpty(PaentingSearchData.getParentingType())) {
            builder.append("&type=").append(PaentingSearchData.getParentingType());
        }
        if (!StringUtils.isNullOrEmpty(PaentingSearchData.getFilerType())) {
            builder.append("&filter=").append(PaentingSearchData.getFilerType());
        }
        if (PaentingSearchData.getCityId() != 0) {
            builder.append("&cityId=").append(PaentingSearchData.getCityId());
        }
        if (!StringUtils.isNullOrEmpty(PaentingSearchData.getSortBy())) {
            builder.append("&sort=").append(PaentingSearchData.getSortBy());
        }
        if (!StringUtils.isNullOrEmpty(PaentingSearchData.getPage())) {
            builder.append("&page=").append(PaentingSearchData.getPage());
        }
        String device_id = DataUtils.getDeviceId(getActivity());

        if (!StringUtils.isNullOrEmpty(device_id)) {
            builder.append("&imei_no=").append(device_id);
        }
        return builder.toString().replace(" ", "%20");

    }


}
