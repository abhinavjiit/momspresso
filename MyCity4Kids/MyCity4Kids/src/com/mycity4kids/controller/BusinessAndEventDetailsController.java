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
import com.mycity4kids.models.businesseventdetails.DetailsRequest;
import com.mycity4kids.models.businesseventdetails.DetailsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.location.GPSTracker;


public class BusinessAndEventDetailsController extends BaseController {

    private Activity context;

    public BusinessAndEventDetailsController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        if (requestType == AppConstants.BUSINESS_AND_EVENT_DETAILS_REQUEST) {
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
            serviceRequest.setRequestData(requestData);
            serviceRequest.setDataType(requestType);
            serviceRequest.setContext(context);
            serviceRequest.setResponseController(this);
            serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);//http://54.251.100.249/webservices/apiservices/detail?id=988&type=business
            //serviceRequest.setUrl("http://54.251.100.249/webservices/apiservices/detail?id=26531&type=event");
            //	serviceRequest.setUrl("http://www.mycity4kids.com/webservices/apiservices/detail?id=55788&type=business");
            //http://www.mycity4kids.com/webservices/apiservices/detail?id=26531&type=event
            //	serviceRequest.setUrl("http://www.mycity4kids.com/webservices/apiservices/detail?id=49360&type=business");
            serviceRequest.setUrl(AppConstants.BUSINESS_AND_EVENT_DETAILS_URL + getAppendUrl(requestType, requestData));

            HttpClientConnection connection = HttpClientConnection.getInstance();
            connection.addRequest(serviceRequest);
        }/*else if(requestType==AppConstants.PARENTING_STOP_BLOGGER_REQUEST){
            serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
			serviceRequest.setRequestData(requestData);
			serviceRequest.setDataType(requestType);
			serviceRequest.setResponseController(this);
			serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
			//	serviceRequest.setUrl("http://54.251.100.249/webservices/apiusers/login?emailId=saur1234234123233@gmail.com&password=123456");
			serviceRequest.setUrl(AppConstants.PARENTING_STOP_BLOGGER_URL+getAppendUrl(requestType,requestData));

			HttpClientConnection connection = HttpClientConnection.getInstance();
			connection.addRequest(serviceRequest);
		}*/
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.BUSINESS_AND_EVENT_DETAILS_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.d("check", "detail res " + responseData);
                    DetailsResponse _detailsResponse = new Gson().fromJson(responseData, DetailsResponse.class);
                    response.setResponseObject(_detailsResponse);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }

                break;
			/*case AppConstants.PARENTING_STOP_BLOGGER_REQUEST:
			try {
				String responseData=new String(response.getResponseData());
				Log.i("Blogger Response", responseData);
				ParentingResponse _loginResponse=new Gson().fromJson(responseData, ParentingResponse.class);
				response.setResponseObject(_loginResponse);

				sendResponseToScreen(response);
			} catch (Exception e) {
				sendResponseToScreen(null);
			}
			break;*/
            default:
                break;
        }


    }

    @Override
    public void parseResponse(Response response) {
        // TODO Auto-generated method stub

    }

    private String getAppendUrl(int requestType, Object requestData) {
        GPSTracker getCurrentLocation = new GPSTracker(getActivity());
        double _latitude = getCurrentLocation.getLatitude();
        double _longitude = getCurrentLocation.getLongitude();
        String device_id = DataUtils.getDeviceId(getActivity());
        DetailsRequest detailsModel = (DetailsRequest) requestData;
        StringBuilder builder = new StringBuilder();
        if (requestType == AppConstants.BUSINESS_AND_EVENT_DETAILS_REQUEST) {
            if (!StringUtils.isNullOrEmpty(detailsModel.getBusinessOrEventId())) {
                builder.append("id=").append(detailsModel.getBusinessOrEventId());
            }
            //builder.append("id=").append("909");
            if (!StringUtils.isNullOrEmpty(detailsModel.getCategoryId())) {
                builder.append("&categoryId=").append(detailsModel.getCategoryId());
            }
            if (!StringUtils.isNullOrEmpty(detailsModel.getUser_id())) {
                builder.append("&user_id=").append(detailsModel.getUser_id());
            }
            if (!StringUtils.isNullOrEmpty(detailsModel.getType())) {
                builder.append("&type=").append(detailsModel.getType());
            }
            if (!StringUtils.isNullOrEmpty(device_id)) {
                builder.append("&imei_no=").append(device_id);
            }

            if (!StringUtils.isNullOrEmpty(String.valueOf(_latitude))) {
                builder.append("&latitude=").append(String.valueOf(_latitude));
            }
            if (!StringUtils.isNullOrEmpty(String.valueOf(_longitude))) {
                builder.append("&longitude=").append(String.valueOf(_longitude));
            }
            builder.append("&pincode=").append(SharedPrefUtils.getpinCode(BaseApplication.getAppContext()));
        }
        return builder.toString().replace(" ", "%20");

    }

}
