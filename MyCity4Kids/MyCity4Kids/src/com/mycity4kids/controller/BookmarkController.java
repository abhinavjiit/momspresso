package com.mycity4kids.controller;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.bookmark.BookmarkModel;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 29/10/15.
 */
public class BookmarkController extends BaseController {

    private static final String LOG_TAG = "BookmarkController";
    private Activity context;

    public BookmarkController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
//        serviceRequest.setRequestData(requestData);
        BookmarkModel _requestModel = (BookmarkModel) requestData;
        serviceRequest.setContext(context);
        serviceRequest.setPostData(setRequestParameters(_requestModel, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        if (requestType == AppConstants.BOOKMARK_BLOG_REQUEST) {
            serviceRequest.setUrl(AppConstants.BOOKMARK_BLOG_URL);
        } else if (requestType == AppConstants.BOOKMARK_RESOURCE_REQUEST) {
            serviceRequest.setUrl(AppConstants.BOOKMARK_RESOURCE_URL);
        }
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);
        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        try {
            String responseData = new String(response.getResponseData());
            if (BuildConfig.DEBUG) {
                Log.e("BOOKMARK response", responseData);
            }
            CommonResponse _followResponse = new Gson().fromJson(responseData, CommonResponse.class);
            response.setResponseObject(_followResponse);
            sendResponseToScreen(response);
        } catch (Exception e) {
            sendResponseToScreen(null);
        }
    }

    @Override
    public void parseResponse(Response response) {

    }

    /**
     * this method creates login request
     *
     * @param requestData
     * @return
     */
    private List<NameValuePair> setRequestParameters(BookmarkModel requestData, int reqtype) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
            nameValuePairs.add(new BasicNameValuePair("ar", requestData.getAction()));
            if (reqtype == AppConstants.BOOKMARK_BLOG_REQUEST) {
                nameValuePairs.add(new BasicNameValuePair("blog_id", requestData.getId()));
            } else if (reqtype == AppConstants.BOOKMARK_RESOURCE_REQUEST) {
                nameValuePairs.add(new BasicNameValuePair("res_id", requestData.getId()));
            }
//                nameValuePairs.add(new BasicNameValuePair("category", requestData.getCategory()));
            Log.d(LOG_TAG, nameValuePairs.toString());

        } catch (Exception e) {
            // TODO: handle exception
        }
        return nameValuePairs;
    }
}
