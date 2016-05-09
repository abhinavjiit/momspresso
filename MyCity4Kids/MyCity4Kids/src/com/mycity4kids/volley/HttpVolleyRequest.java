package com.mycity4kids.volley;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hemant on 4/10/15.
 */
public class HttpVolleyRequest {

    public static void getStringResponse(final Context context, String url, final Map<String, String> paramsMap,
                                         final OnWebServiceCompleteListener listener, int requestMethod, boolean isCacheEnabled) {
        final VolleyBaseResponse baseResponse = new VolleyBaseResponse();
        // Formulate the request and handle the response.
        if (Request.Method.GET == requestMethod) {
            if (null != SharedPrefUtils.getUserDetailModel(context)) {
                url = url + "&user_id=" + SharedPrefUtils.getUserDetailModel(context).getId();
            } else {
                url = url + "&user_id=" + "";
            }
        } else if (Request.Method.POST == requestMethod || Request.Method.PUT == requestMethod) {
            if (null != SharedPrefUtils.getUserDetailModel(context)) {
                paramsMap.put("user_id", "" + SharedPrefUtils.getUserDetailModel(context).getId());
            } else {
                paramsMap.put("user_id", "");

            }
        }

        StringRequest stringRequest = new StringRequest(requestMethod, url,
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        baseResponse.setResponseBody(response);
                        Log.d("response:", response.toString());
                        listener.onWebServiceComplete(baseResponse, false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
//                        Toast.makeText(context, "Error Response " + error, Toast.LENGTH_SHORT).show();
                        if (null != error && null != error.networkResponse) {
                            baseResponse.setResponseCode(error.networkResponse.statusCode);
                            baseResponse.setResponseBody(new String(error.networkResponse.data));
                        } else {
                            error.printStackTrace();
                            baseResponse.setResponseCode(999);
                            baseResponse.setResponseBody("Unknown Error occured");
                        }
                        listener.onWebServiceComplete(baseResponse, true);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                return paramsMap;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                Map<String, String> responseHeader = response.headers;
                baseResponse.setResponseHeader(responseHeader);

                String rData = new String(response.data);
                return Response.success(rData, parseIgnoreCacheHeaders(response));
            }

            public Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
                long now = System.currentTimeMillis();

                Map<String, String> headers = response.headers;
                long serverDate = 0;
                String serverEtag = null;
                String headerValue;

                headerValue = headers.get("Date");
                if (headerValue != null) {
                    serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }

                serverEtag = headers.get("ETag");

                final long cacheHitButRefreshed = 5 * 60 * 1000; // in 5 minutes cache will be hit, but also refreshed on background
                final long cacheExpired = 4 * 24 * 60 * 60 * 1000; // in 4 days this cache entry expires completely
                final long softExpire = now + cacheHitButRefreshed;
                final long ttl = now + cacheExpired;

                Cache.Entry entry = new Cache.Entry();
                entry.data = response.data;
                entry.etag = serverEtag;
                entry.softTtl = softExpire;
                entry.ttl = ttl;
                entry.serverDate = serverDate;
                entry.responseHeaders = headers;

                return entry;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mHeaders = new ArrayMap<String, String>();
                return mHeaders;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        // Get a RequestQueue
        RequestQueue queue = BaseApplication.getInstance().
                getRequestQueue();
        if (!isCacheEnabled) {
            stringRequest.setShouldCache(false);
        }
        stringRequest.setRetryPolicy(policy);
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        queue.add(stringRequest);
    }

}
