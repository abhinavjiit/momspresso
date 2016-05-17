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

import java.util.Map;

/**
 * Created by hemant on 4/10/15.
 */
public class HttpVolleyRequest {

    static StringRequest stringRequest;

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

        stringRequest = new StringRequest(requestMethod, url,
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        baseResponse.setResponseBody(response);
                        Log.d("responseHeader:", "" + baseResponse.getResponseHeader());
                        Log.d("responseCode:", "" + baseResponse.getResponseCode());
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
                baseResponse.setResponseCode(response.statusCode);
                String rData = new String(response.data);
                return Response.success(rData, parseIgnoreCacheHeaders(response));
            }

            public Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
                Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                if (cacheEntry == null) {
                    cacheEntry = new Cache.Entry();
                }
                final long cacheHitButRefreshed = 20 * 1000; // in 5 minutes cache will be hit, but also refreshed on background
                final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                long now = System.currentTimeMillis();
                final long softExpire = now + cacheHitButRefreshed;
                final long ttl = now + cacheExpired;
                cacheEntry.data = response.data;
                cacheEntry.softTtl = softExpire;
                cacheEntry.ttl = ttl;
                String headerValue;
                headerValue = response.headers.get("Date");
                if (headerValue != null) {
                    cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                headerValue = response.headers.get("Last-Modified");
                if (headerValue != null) {
                    cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                cacheEntry.responseHeaders = response.headers;

                return cacheEntry;
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
