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
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.BlogShareSpouseModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thehi on 02-11-2015.
 */
public class BlogShareSpouseController extends BaseController {
    private Activity context;

    public BlogShareSpouseController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
//        serviceRequest.setRequestData(requestData);
        BlogShareSpouseModel _blogShareModel = (BlogShareSpouseModel) requestData;

        serviceRequest.setPostData(setRequestParameters(_blogShareModel, requestType));
        serviceRequest.setDataType(requestType);
        serviceRequest.setContext(context);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);

        if (requestType == AppConstants.SHARE_SPOUSE_BLOG)
            serviceRequest.setUrl(AppConstants.SHARE_BLOG_ARTICLE_URL);


        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {

    }

    @Override
    public void parseResponse(Response response) {

    }

    private List<NameValuePair> setRequestParameters(BlogShareSpouseModel requestData, int reqtype) {

        String data = new Gson().toJson(requestData.getSharedWithUserList());
        //System.out.println("Appointment JSON " + data);
        Log.d("Shared User data", data);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {

            if (reqtype == AppConstants.SHARE_SPOUSE_BLOG) {
                BlogShareSpouseModel _blogShareModel = requestData;
                nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
                nameValuePairs.add(new BasicNameValuePair("article_id", _blogShareModel.getArticleId()));
                nameValuePairs.add(new BasicNameValuePair("share_spouse_data", data));
            }

            System.out.println("Blog Share  data " + nameValuePairs.toString());

            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return nameValuePairs;
    }
}
