package com.mycity4kids.googlemap.controller;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.googlemap.maputils.MapUtils;
import com.mycity4kids.models.businesseventdetails.DetailMap;

public class GetDirectionController extends BaseController {

    private final Activity activity;

    public GetDirectionController(Activity activity, IScreen screen) {
        super(activity, screen);
        this.activity=activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setContext(activity);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        DetailMap _mapDetails = (DetailMap) requestData;
        String url = getAppendUrl(_mapDetails, requestType);
        url = url != null ? url : "";
        if (requestType == AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST||
                requestType == AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST_DEST) {
            serviceRequest.setUrl(AppConstants.GET_GOOGLE_ADDRESS_URL + url);
        } else {
            serviceRequest.setUrl(AppConstants.GET_GOOGLE_COMMON_URL + url);
        }
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.GET_GOOGLE_MAP_DIRECTIONS:
            case AppConstants.GET_GOOGLE_MAP_TRANSIT:
                sendResponseToScreen(response);
                break;
            case AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST:
                sendResponseToScreen(response);
                break;
            case AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST_DEST:
                sendResponseToScreen(response);
                break;
            default:
                break;
        }


    }

    @Override
    public void parseResponse(Response response) {


    }

    /**
     * Basically this method create a url for getting get directions from google map : Deepanker
     *
     * @param _mapDetails
     * @param sourcelat
     * @param sourcelog
     * @param destlat
     * @param destlog     this method at this time on hold
     */
    private String getAppendUrl(DetailMap mapDetails, int requestTpye) {
        try {
            long timestamp = System.currentTimeMillis() / 1000;
            LatLng latLng = MapUtils.getLocation(getActivity());
            StringBuilder builder = new StringBuilder();
            if (requestTpye == AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST||
                    requestTpye == AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST_DEST) {
                builder.append("?sensor=").append(false);
                builder.append("&latlng=").append(mapDetails.getLatitude());
                builder.append(",").append(mapDetails.getLongitude());
                return builder.toString().replaceAll(" ", "%20");
            }
            builder.append("?origin=").append(latLng.latitude);
            builder.append(",").append(latLng.longitude);
            if (!StringUtils.isNullOrEmpty(mapDetails.getLatitude())) {
                builder.append("&destination=").append(mapDetails.getLatitude());
            }

            if (!StringUtils.isNullOrEmpty(mapDetails.getLongitude())) {
                builder.append(",").append(mapDetails.getLongitude());
            }
            if (AppConstants.GET_GOOGLE_MAP_DIRECTIONS == requestTpye) {
                builder.append("&sensor=false&units=metric&mode=driving");
            } else if (AppConstants.GET_GOOGLE_MAP_TRANSIT == requestTpye) {
                builder.append("&sensor=false&departure_time=" + timestamp + "&mode=transit");
            }
            return builder.toString().replaceAll(" ", "%20");
        } catch (Exception e) {
            return null;
        }

    }


}

