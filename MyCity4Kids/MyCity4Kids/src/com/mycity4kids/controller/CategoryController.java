package com.mycity4kids.controller;

import android.app.Activity;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.category.CategoryResponse;

public class CategoryController extends BaseController {

    private Activity context;

    public CategoryController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setRequestData(requestData);
        serviceRequest.setContext(context);
        //serviceRequest.setPostData(setRequestParameters((LoginRequest)requestData));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        //serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl("http://54.251.100.249/webservices/apiservices/category?city_id=2");
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.CATEGORY_REQUEST:

                try {
                    String responseData = new String(response.getResponseData());

                    CategoryResponse _categoryResponse = new Gson().fromJson(responseData, CategoryResponse.class);
                    response.setResponseObject(_categoryResponse);

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

}
