package com.mycity4kids.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.AddTaskNoteResponse;
import com.mycity4kids.newmodels.NotesModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class AddTaskNotesController extends BaseController {
    private Activity context;


    public AddTaskNotesController(Activity activity, IScreen screen) {
        super(activity, screen);
        context = activity;
        // TODO Auto-generated constructor stub
    }

    @Override
    public ServiceRequest getData(int requestType, Object requestData) {
        ServiceRequest serviceRequest = new ServiceRequest();
        //serviceRequest.setHttpHeaders(header, header);
        serviceRequest.setHttpMethod(HttpClientConnection.HTTP_METHOD.POST);
        serviceRequest.setRequestData(requestData);
        //String data = new Gson().toJson(requestData);
        //System.out.println("JSON " + data);
        serviceRequest.setContext(context);
        serviceRequest.setPostData(setRequestParameters(requestData));
        serviceRequest.setDataType(requestType);
        serviceRequest.setResponseController(this);
        serviceRequest.setPriority(HttpClientConnection.PRIORITY.HIGH);
        serviceRequest.setUrl(AppConstants.ADD_TASK_NOTES_URL);
        HttpClientConnection connection = HttpClientConnection.getInstance();
        connection.addRequest(serviceRequest);

        return serviceRequest;
    }

    @Override
    public void handleResponse(Response response) {
        switch (response.getDataType()) {
            case AppConstants.ADD_TASK_NOTES_REQ:
                try {
                    String responseData = new String(response.getResponseData());
                    Log.i("ADD Response", responseData);

                    AddTaskNoteResponse _signUpData = new Gson().fromJson(responseData, AddTaskNoteResponse.class);
                    response.setResponseObject(_signUpData);

                    sendResponseToScreen(response);
                } catch (Exception e) {
                    sendResponseToScreen(null);
                }
                break;

            default:
                break;
        }

    }

    public void saveUserDetails(Context context, UserResponse pUserDetails, UserResponse requestdata) {

        try {
            UserTable userTable = new UserTable((BaseApplication) ((Activity) context).getApplication());
            userTable.deleteAll();
            userTable.insertData(requestdata);

        } catch (Exception e) {

        }
    }

    @Override
    public void parseResponse(Response response) {


    }

    /**
     * these method was for GET Registration:
     *
     * @param _userRequest
     * @return
     */


    /**
     * this method creates Registration request
     * // * @param requestData
     *
     * @return
     */
    private List<NameValuePair> setRequestParameters(Object requestData) {

        NotesModel _requestModel = (NotesModel) requestData;
        UrlEncodedFormEntity encodedEntity = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {

            nameValuePairs.add(new BasicNameValuePair("sessionId", SharedPrefUtils.getUserDetailModel(context).getSessionId()));
            nameValuePairs.add(new BasicNameValuePair("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId()));
            nameValuePairs.add(new BasicNameValuePair("task_id", "" + _requestModel.getAppointmentid()));
            nameValuePairs.add(new BasicNameValuePair("note", _requestModel.getMsg()));

            encodedEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

            Log.i("notes req ", nameValuePairs.toString());
            // encodedEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
            // "application/x-www-form-urlencoded"));
        } catch (Exception e) {
            // TODO: handle exception
        }

        return nameValuePairs;

    }


}
