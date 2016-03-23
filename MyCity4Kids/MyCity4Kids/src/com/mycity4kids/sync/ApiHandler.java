package com.mycity4kids.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.sync.connection.HTTPPoster;

import java.util.HashMap;


public class ApiHandler extends AsyncTask<String, Void, String> {

    private Context context;
    private UpdateListener updateListener;
    private int requestType;

    public ApiHandler(Context context, UpdateListener updateListener, int requestType) {
        this.context = context;
        this.updateListener = updateListener;
        this.requestType = requestType;
    }

    @Override
    protected String doInBackground(String... params) {

        String jsonString = "";

        switch (requestType) {
            case AppConstants.DELETE_TASK_REQUEST:

                try {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Content-Type", "application/json");

                    HashMap<String, String> keyvalue = new HashMap<String, String>();
                    keyvalue.put("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId());
                    keyvalue.put("tasks", params[1]);

                    jsonString = HTTPPoster.doPost(params[0],keyvalue,map, context);
                    Log.e(this.getClass().getName(), "jsonString: " + jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


            case AppConstants.TASKS_COMPLETE_REQUEST:

                try {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Content-Type", "application/json");

                    HashMap<String, String> keyvalue = new HashMap<String, String>();
                    keyvalue.put("task_date_excluded", params[1]);

                    jsonString = HTTPPoster.doPost(params[0],keyvalue,map, context);
                    Log.e(this.getClass().getName(), "jsonString: " + jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


            case AppConstants.GET_ALL_APPOINTMNET_REQ:

                try {
                    //System.out.println("RequestUrl : " + params[0]);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Content-Type", "application/json");
                    jsonString = HTTPPoster.doGet(params[0], map,context);
                    Log.e(this.getClass().getName(), "jsonString: " + jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case AppConstants.GET_ALL_TASK_REQ:

//                try {
//                    //System.out.println("RequestUrl : " + params[0]);
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put("Content-Type", "application/json");
//                    jsonString = HTTPPoster.doGet(params[0], map,context);
//                    Log.e(this.getClass().getName(), "jsonString: " + jsonString);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;

            case AppConstants.SYNC_USER_INFO_REQUEST:

                try {
                    //System.out.println("RequestUrl : " + params[0]);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Content-Type", "application/json");
                    jsonString = HTTPPoster.doGet(params[0], map,context);
                    Log.e(this.getClass().getName(), "jsonString: " + jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case AppConstants.PUSH_TOKEN_REQUEST:

                try {
                    //System.out.println("RequestUrl : " + params[0]);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Content-Type", "application/json");
                    jsonString = HTTPPoster.doGet(params[0], map,context);
                    Log.e(this.getClass().getName(), "jsonString: " + jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case AppConstants.EMAIL_NOTIFICATION_REQUEST:

                try {
                    //System.out.println("RequestUrl : " + params[0]);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Content-Type", "application/json");
                    jsonString = HTTPPoster.doGet(params[0], map,context);
                    Log.e(this.getClass().getName(), "jsonString: " + jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }

        return jsonString;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        updateListener.updateView(jsonString, requestType);
        super.onPostExecute(jsonString);
    }

}
