package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.newmodels.parentingmodel.CityByPinCodeModel;

public class ControllerCityByPincode extends BaseController {

    public ControllerCityByPincode(Activity activity, IScreen screen) {
        super(activity, screen);
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
        serviceRequest.setRequestData(requestData);

//        serviceRequest.setPostData(setRequestParameters(data));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.CITY_BY_PINCODE_URL + "pincode=" + requestData);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.CITY_BY_PINCODE_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("CITY_PINCODE Response", responseData);

                    CityByPinCodeModel cityPincodeModel = new Gson().fromJson(responseData, CityByPinCodeModel.class);
                    response.setResponseObject(cityPincodeModel);
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


    }

}
