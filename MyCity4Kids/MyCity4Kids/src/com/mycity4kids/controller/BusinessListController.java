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
import com.mycity4kids.models.businesslist.BusinessListRequest;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.location.GPSTracker;

/**
 * Controller for business Listing Api used in SearchResultActivity
 *
 * @author kapil.vij
 */
public class BusinessListController extends BaseController {
    boolean isfilter = false;

    public BusinessListController(Activity activity, IScreen screen) {
        super(activity, screen);
    }

    public void isFilter(boolean isfilter) {
        this.isfilter = isfilter;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        Log.d("check", " requestType" + requestType);
        try {
            if (requestType == AppConstants.BUSINESS_LIST_REQUEST) {

                serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
                serviceRequest.setDataType(requestType);
                serviceRequest.setResponseController(this);
                serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
                //Log.d("check", "request url " + AppConstants.BUSINESS_LISTING_URLNEW + getQueryString(requestData));

                //serviceRequest.setUrl(AppConstants.BUSINESS_LISTING_URLNEW + getQueryString(requestData));
                //serviceRequest.setUrl("http://54.169.17.138/apilistings/lists?"+ getQueryString(requestData));requestData
                Log.d("check", " kidsresurl" + AppConstants.BUSINESSLISTINGURLTMP + getQueryString(requestData));
                serviceRequest.setUrl(AppConstants.BUSINESSLISTINGURLTMP + getQueryString(requestData));
                HttpClientConnection connection = HttpClientConnection.getInstance();
                connection.addRequest(serviceRequest);
            } else if (requestType == AppConstants.BUSINESS_SEARCH_LISTING_REQUEST) {

                serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
                serviceRequest.setDataType(requestType);
                serviceRequest.setResponseController(this);
                serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);

                serviceRequest.setUrl(AppConstants.BUSINESS_SEARCH_URL + getQuerySearchUrl(requestData));

                HttpClientConnection connection = HttpClientConnection.getInstance();
                connection.addRequest(serviceRequest);
            } else if (requestType == AppConstants.BUSINESS_SEARCH_LISTING_REQUESTNEW) {

                serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
                serviceRequest.setDataType(requestType);
                serviceRequest.setResponseController(this);
                serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);

                serviceRequest.setUrl(AppConstants.BUSINESS_SEARCH_URL + getQueryBusinessSearchUrl(requestData));

                HttpClientConnection connection = HttpClientConnection.getInstance();
                connection.addRequest(serviceRequest);
            } else if (requestType == AppConstants.BUSINESS_SEARCH_LISTING_REQUESTEVENTNEW) {

                serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
                serviceRequest.setDataType(requestType);
                serviceRequest.setResponseController(this);
                serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);

                serviceRequest.setUrl(AppConstants.BUSINESS_SEARCH_URL_NEW + getQueryEventSearchUrl(requestData));

                HttpClientConnection connection = HttpClientConnection.getInstance();
                connection.addRequest(serviceRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.BUSINESS_LIST_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    BusinessListResponse businessList = new Gson().fromJson(responseData, BusinessListResponse.class);
                    response.setResponseObject(businessList);
                /*if(businessList.getResponseCode() == AppConstants.HTTP_RESPONSE_SUCCESS){
                    sendResponseToScreen(response);
				}*/

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();

                    sendResponseToScreen(null);
                }
                break;
            case AppConstants.BUSINESS_SEARCH_LISTING_REQUEST:
                try {
                    String responseData = new String(response.getResponseData());
                    //Log.i("BusinessSearchList Response", responseData);
                    BusinessListResponse businessList = new Gson().fromJson(responseData, BusinessListResponse.class);
                    response.setResponseObject(businessList);
                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;
            case AppConstants.BUSINESS_SEARCH_LISTING_REQUESTNEW:
                try {
                    String responseData = new String(response.getResponseData());
                    //Log.i("BusinessSearchList Response", responseData);
                    BusinessListResponse businessList = new Gson().fromJson(responseData, BusinessListResponse.class);
                    response.setResponseObject(businessList);
                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;
            case AppConstants.BUSINESS_SEARCH_LISTING_REQUESTEVENTNEW:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.d("check", "request url " + responseData);
                    //Log.i("BusinessSearchList Response", responseData);
                    BusinessListResponse businessList = new Gson().fromJson(responseData, BusinessListResponse.class);
                    response.setResponseObject(businessList);
                    sendResponseToScreen(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponseToScreen(null);
                }
                break;
        }

    }

    @Override
    public void parseResponse(Response response) {
        // TODO Auto-generated method stub

    }


    private String getQueryString(Object requestData) {
        GPSTracker getCurrentLocation = new GPSTracker(getActivity());
        double _latitude = getCurrentLocation.getLatitude();
        double _longitude = getCurrentLocation.getLongitude();
        BusinessListRequest businessModel = (BusinessListRequest) requestData;
        businessModel.setLatitude(String.valueOf(_latitude));
        businessModel.setLongitude(String.valueOf(_longitude));
        StringBuilder builder = new StringBuilder();
        //		http://54.251.100.249/webservices/apilistings/business?city_id=1&category_id=248&sub_category_id=585&page=3&zone_id=4&locality_id=852
        //			http://54.251.100.249/webservices/apilistings/business?city_id=1&category_id=248&sort_by=rating

        builder.append("city_id=").append(businessModel.getCity_id());

        if (Integer.parseInt(businessModel.getCategory_id()) != 0) {
            builder.append("&category_id=").append(businessModel.getCategory_id());
        }

//        String pincode = SharedPrefUtils.getpinCode(getActivity());
//        if (!StringUtils.isNullOrEmpty(pincode)) {
//            builder.append("&pincode=").append(pincode);
//        }

		/*if (! StringUtils.isNullOrEmpty(businessModel.getSub_category_id())) {
            builder.append("&sub_category_id=").append(businessModel.getSub_category_id());
		}*/
        if (!StringUtils.isNullOrEmpty(businessModel.getPage())) {
            builder.append("&page=").append(businessModel.getPage());
        }
        /**
         * now this value contains all sorting List Values; no need extra field-subcategory,zone id
         * age group,activities,dateby,etc.
         */
        if (!StringUtils.isNullOrEmpty(businessModel.getTotalFilterValues())) {
            builder.append(businessModel.getTotalFilterValues());
        }
        /*if (! StringUtils.isNullOrEmpty(businessModel.getZone_id())) {
            builder.append("&zone_id=").append(businessModel.getZone_id());
		}*/
        /*if (! StringUtils.isNullOrEmpty(businessModel.getAge_group())) {
            builder.append("&age_group=").append(businessModel.getAge_group());
		}
		if (! StringUtils.isNullOrEmpty(businessModel.getDate_by())) {
			builder.append("&date_by=").append(businessModel.getDate_by());
		}
		if (! StringUtils.isNullOrEmpty(businessModel.getActivities())) {
			builder.append("&activities=").append(businessModel.getActivities());
		}*/

		/*if(! StringUtils.isNullOrEmpty(businessModel.getQuerySearch())){
            builder.append("q=").append(businessModel.getQuerySearch());
		}
		if(! StringUtils.isNullOrEmpty(businessModel.getLocalitySearch())){
			builder.append("&locality=").append(businessModel.getLocalitySearch());
		}*/

        if (!StringUtils.isNullOrEmpty(businessModel.getDate_by())) {
            builder.append("&date_by=").append(businessModel.getDate_by());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getAge_group())) {
            builder.append(businessModel.getAge_group());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getLocality_id())) {
            builder.append("&locality_id=").append(businessModel.getLocality_id());
        }
        /*	if (! StringUtils.isNullOrEmpty(businessModel.getMore())) {
			builder.append("&more=").append(businessModel.getMore());
		}*/
        if (!StringUtils.isNullOrEmpty(businessModel.getSort_by())) {
            builder.append("&sort=").append(businessModel.getSort_by());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getLatitude())) {
            builder.append("&latitude=").append(businessModel.getLatitude());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getLongitude())) {
            builder.append("&longitude=").append(businessModel.getLongitude());
        }
//anupama
		/*String device_id=DataUtils.getDeviceId(getActivity());
		if (! StringUtils.isNullOrEmpty(device_id)) {
			builder.append("&imei_no=").append(device_id);
		}*/

        builder.append("&pincode=").append(SharedPrefUtils.getpinCode(getActivity()));

        return builder.toString().replace(" ", "%20");

    }

    private String getQuerySearchUrl(Object requestData) {
        GPSTracker getCurrentLocation = new GPSTracker(getActivity());
        double _latitude = getCurrentLocation.getLatitude();
        double _longitude = getCurrentLocation.getLongitude();
        BusinessListRequest businessModel = (BusinessListRequest) requestData;
        businessModel.setLatitude(String.valueOf(_latitude));
        businessModel.setLongitude(String.valueOf(_longitude));

        StringBuilder builder = new StringBuilder();

        builder.append("q=").append(businessModel.getQuerySearch());

        if (!StringUtils.isNullOrEmpty(businessModel.getLocalitySearch())) {
            builder.append("&locality=").append(businessModel.getLocalitySearch().trim());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getCity_id())) {
            builder.append("&cityId=").append(businessModel.getCity_id());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getSort_by())) {
            builder.append("&sort=").append(businessModel.getSort_by());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getTotalFilterValues())) {
            builder.append(businessModel.getTotalFilterValues());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getPage())) {
            builder.append("&page=").append(businessModel.getPage());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getLatitude())) {
            builder.append("&latitude=").append(businessModel.getLatitude());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getLongitude())) {
            builder.append("&longitude=").append(businessModel.getLongitude());
        }
        String device_id = DataUtils.getDeviceId(getActivity());
        if (!StringUtils.isNullOrEmpty(device_id)) {
            builder.append("&imei_no=").append(device_id);
        }

        builder.append("&pincode=").append(SharedPrefUtils.getpinCode(getActivity()));

        return builder.toString().replace(" ", "%20");

    }

    private String getQueryBusinessSearchUrl(Object requestData) {
        GPSTracker getCurrentLocation = new GPSTracker(getActivity());
        double _latitude = getCurrentLocation.getLatitude();
        double _longitude = getCurrentLocation.getLongitude();
        BusinessListRequest businessModel = (BusinessListRequest) requestData;
        businessModel.setLatitude(String.valueOf(_latitude));
        businessModel.setLongitude(String.valueOf(_longitude));

        StringBuilder builder = new StringBuilder();

        builder.append("q=").append(businessModel.getQuerySearch());

        if (!StringUtils.isNullOrEmpty(businessModel.getLocalitySearch())) {
            builder.append("&locality=").append(businessModel.getLocalitySearch().trim());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getCity_id())) {
            builder.append("&cityId=").append(businessModel.getCity_id());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getSort_by())) {
            builder.append("&sort=").append(businessModel.getSort_by());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getTotalFilterValues())) {
            builder.append(businessModel.getTotalFilterValues());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getPage())) {
            builder.append("&page=").append(businessModel.getPage());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getLatitude())) {
            builder.append("&latitude=").append(businessModel.getLatitude());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getLongitude())) {
            builder.append("&longitude=").append(businessModel.getLongitude());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getLongitude())) {
            builder.append("&type=business");
        }
        String device_id = DataUtils.getDeviceId(getActivity());
        if (!StringUtils.isNullOrEmpty(device_id)) {
            builder.append("&imei_no=").append(device_id);
        }

        builder.append("&pincode=").append(SharedPrefUtils.getpinCode(getActivity()));

        return builder.toString().replace(" ", "%20");

    }

    private String getQueryEventSearchUrl(Object requestData) {
        GPSTracker getCurrentLocation = new GPSTracker(getActivity());
        double _latitude = getCurrentLocation.getLatitude();
        double _longitude = getCurrentLocation.getLongitude();
        BusinessListRequest businessModel = (BusinessListRequest) requestData;
        businessModel.setLatitude(String.valueOf(_latitude));
        businessModel.setLongitude(String.valueOf(_longitude));

        StringBuilder builder = new StringBuilder();
        Log.d("check", "businessModel.getQuerySearch() " + businessModel.getQuerySearch());
        builder.append("q=").append(businessModel.getQuerySearch());

        if (!StringUtils.isNullOrEmpty(businessModel.getLocalitySearch())) {
            builder.append("&locality=").append(businessModel.getLocalitySearch().trim());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getCity_id())) {
            builder.append("&cityId=").append(businessModel.getCity_id());
        }

        if (!StringUtils.isNullOrEmpty(businessModel.getLatitude())) {
            builder.append("&latitude=").append(businessModel.getLatitude());
        }
        if (!StringUtils.isNullOrEmpty(businessModel.getLongitude())) {
            builder.append("&longitude=").append(businessModel.getLongitude());
        }

		/*if (! StringUtils.isNullOrEmpty(businessModel.getSort_by())) {
			builder.append("&sort=").append(businessModel.getSort_by());
		}*/

		/*if (! StringUtils.isNullOrEmpty(businessModel.getTotalFilterValues())) {
			builder.append(businessModel.getTotalFilterValues());
		}*/

		/*if (! StringUtils.isNullOrEmpty(businessModel.getPage())) {
			builder.append("&page=").append(businessModel.getPage());
		}*/
		/*if (! StringUtils.isNullOrEmpty(businessModel.getLatitude())) {
			builder.append("&latitude=").append(businessModel.getLatitude());
		}
		if (! StringUtils.isNullOrEmpty(businessModel.getLongitude())) {
			builder.append("&longitude=").append(businessModel.getLongitude());
		}*/
        if (!StringUtils.isNullOrEmpty(businessModel.getLongitude())) {
            builder.append("&type=event");
        }
		/*String device_id=DataUtils.getDeviceId(getActivity());
		if (! StringUtils.isNullOrEmpty(device_id)) {
			builder.append("&imei_no=").append(device_id);
		}*/

        builder.append("&pincode=").append(SharedPrefUtils.getpinCode(getActivity()));

        return builder.toString().replace(" ", "%20");

    }
}
