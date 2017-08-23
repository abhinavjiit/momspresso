package com.kelltontech.network;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DataUtils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.SerializableCookie;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * @author sachin.gupta
 */
public class HttpClientConnection extends Thread {
    private final String LOG_TAG = "HttpClientConnection";

    private static HttpClientConnection instance;
//    public static List<Cookie> cookies;

    private HttpClientConnection() {
        defaultStatusCodeChecker = new StatusCodeChecker() {
            @Override
            public boolean isSuccess(int statusCode) {
                return statusCode == 200;
            }
        };
    }

    public static HttpClientConnection getInstance() {
        if (instance == null) {
            instance = new HttpClientConnection();
            instance.execute();
        }
        return instance;
    }

    private StatusCodeChecker defaultStatusCodeChecker;
    private int defaultRequestTimeOut = 60000; // 60 sec timeout

    /**
     * @param defaultStatusCodeChecker the defaultStatusCodeChecker to set
     */
    public void setDefaultStatusCodeChecker(StatusCodeChecker defaultStatusCodeChecker) {
        this.defaultStatusCodeChecker = defaultStatusCodeChecker;
    }

    /**
     * @param defaultRequestTimeOut the defaultRequestTimeOut in miliseconds to set
     */
    public void setDefaultRequestTimeOut(int defaultRequestTimeOut) {
        this.defaultRequestTimeOut = defaultRequestTimeOut;
    }

    private boolean isRunning;
    private Vector<ServiceRequest> highPriorityQueue;
    private Vector<ServiceRequest> lowPriorityQueue;

    public void execute() {
        highPriorityQueue = new Vector<ServiceRequest>();
        lowPriorityQueue = new Vector<ServiceRequest>();
        isRunning = true;
        start();
    }

    private ServiceRequest currentRequest;

    /**
     * {@link ServiceRequest} with {@link PRIORITY#HIGH} are executed before {@link ServiceRequest} with {@link PRIORITY#LOW}
     */
    public interface PRIORITY {
        /**
         * When-ever a new {@link ServiceRequest} with {@link PRIORITY#LOW} is added,
         * it gets lower priority than previous requests with same priority.
         */
        byte LOW = 0;
        /**
         * When-ever a new {@link ServiceRequest} with {@link PRIORITY#HIGH} is added,
         * it gets higher priority than previous requests with same priority.
         */
        byte HIGH = 1;
    }

    public interface HTTP_METHOD {
        byte GET = 0;
        byte POST = 1;
        byte PUT = 2;
        byte DELETE = 3;
    }

    /**
     * Specific instance of StatusCodeChecker can be set in ServiceRequest
     */
    public interface StatusCodeChecker {
        boolean isSuccess(int statusCode);
    }

    @Override
    public void run() {
        while (isRunning) {
            if (nextRequest()) {
                executeRequest();
            } else {
                try {
                    Thread.sleep(10 * 60 * 1000);// 10 min sleep
                } catch (InterruptedException e) {
//                    Log.i(LOG_TAG, "" + e);
                }
            }
        }
    }

    private boolean nextRequest() {
        if (highPriorityQueue.size() > 0) {
            currentRequest = highPriorityQueue.remove(0);
        } else if (lowPriorityQueue.size() > 0) {
            currentRequest = lowPriorityQueue.remove(0);
        } else {
            currentRequest = null;
        }

        return currentRequest != null;
    }

    public void executeRequest() {

		/*if(highPriorityQueue.size()>1 || lowPriorityQueue.size()>1)
        {*/
        if (currentRequest.isCancelled()) {
            return;
        }
        //}
        /**
         * Check if device is connected.
         */
        Activity activity = currentRequest.getResponseController().getActivity();
        if (activity != null && !ConnectivityUtils.isNetworkEnabled(activity)) {
            notifyError("Device is out of network coverage.", null);
            return;
        }

//        HttpClient httpClient = getHttpclient();
        HttpClient httpClient = new DefaultHttpClient();
//        HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
        //	httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        int requestTimeOut = currentRequest.getRequestTimeOut();
        if (requestTimeOut <= 0) {
            requestTimeOut = defaultRequestTimeOut;
        }
        if (requestTimeOut > 0) {
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, requestTimeOut);
            HttpConnectionParams.setSoTimeout(params, requestTimeOut);
        }

        HttpResponse httpResponse = null;

        try {
            HttpRequestBase getOrPost = null;

            switch (currentRequest.getHttpMethod()) {
                case HTTP_METHOD.POST: {
                    getOrPost = new HttpPost();
                    if (currentRequest.getPostData() != null) {
                        List<NameValuePair> nvPair = currentRequest.getPostData();
                        if (null != SharedPrefUtils.getUserDetailModel(currentRequest.getContext())) {
                            nvPair.add(new BasicNameValuePair("user_id",
                                    "" + SharedPrefUtils.getUserDetailModel(currentRequest.getContext()).getId()));
                        } else {
                            nvPair.add(new BasicNameValuePair("user_id", ""));
                        }
                        HttpEntity httpEntity = new UrlEncodedFormEntity(currentRequest.getPostData(), "UTF-8");
                        ((HttpPost) getOrPost).setEntity(httpEntity);
                    }
                    break;
                }
                case HTTP_METHOD.GET: {
                    getOrPost = new HttpGet();
                    break;
                }
                case HTTP_METHOD.PUT: {
                    getOrPost = new HttpPut();
                    if (currentRequest.getPostData() != null) {
                        List<NameValuePair> nvPair = currentRequest.getPostData();
                        if (null != SharedPrefUtils.getUserDetailModel(currentRequest.getContext())) {
                            nvPair.add(new BasicNameValuePair("user_id",
                                    "" + SharedPrefUtils.getUserDetailModel(currentRequest.getContext()).getId()));
                        } else {
                            nvPair.add(new BasicNameValuePair("user_id", ""));
                        }
                        HttpEntity httpEntity = new UrlEncodedFormEntity(currentRequest.getPostData(), "UTF-8");
                        ((HttpPut) getOrPost).setEntity(httpEntity);

                    }
                    break;
                }
                case HTTP_METHOD.DELETE: {
                    getOrPost = new HttpDelete();
                    break;
                }

            }
            if (HTTP_METHOD.GET == currentRequest.getHttpMethod()) {
                URI uri;
                if (null != SharedPrefUtils.getUserDetailModel(currentRequest.getContext())) {
                    uri = new URI(currentRequest.getUrl() + "&user_id=" + SharedPrefUtils.getUserDetailModel(currentRequest.getContext()).getId());
                } else {
                    uri = new URI(currentRequest.getUrl() + "&user_id=" + "");
                }
                getOrPost.setURI(uri);
                Log.i(LOG_TAG, "Request URL: " + uri.toURL());
            } else {
                getOrPost.setURI(new URI(currentRequest.getUrl()));
                Log.i(LOG_TAG, "Request URL: " + getOrPost.getURI().toString());
            }

            String[] headerNames = currentRequest.getHeaderNames();
            if (headerNames != null) {
                String[] headerValues = currentRequest.getHeaderValues();
                for (int i = 0; i < headerNames.length; i++) {
                    getOrPost.addHeader(headerNames[i], headerValues[i]);
//                    Log.i(LOG_TAG, "Header: " + headerNames[i] + " = " + headerValues[i]);
                }
            }
            PackageInfo pInfo = currentRequest.getContext().getPackageManager().getPackageInfo(currentRequest.getContext().getPackageName(), 0);
            String version = pInfo.versionName;
            getOrPost.addHeader("appVersion", version);

            List<Cookie> cookies = loadSharedPreferencesCookie();
            if (cookies != null) {
                CookieStore cookieStore = new BasicCookieStore();
                for (int i = 0; i < cookies.size(); i++) {
                    cookieStore.addCookie(cookies.get(i));
                }
                ((DefaultHttpClient) httpClient).setCookieStore(cookieStore);
            }

            httpResponse = httpClient.execute(getOrPost);
            cookies = ((DefaultHttpClient) httpClient).getCookieStore().getCookies();
            saveSharedPreferencesCookies(cookies);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            Log.i(LOG_TAG, "Response Received : " + statusCode);

            if (currentRequest.isCancelled()) {
                return;
            }

            Response response = new Response();
            response.setDataType(currentRequest.getDataType());
            response.setRequestData(currentRequest.getRequestData());
            response.setHttpHeaders(httpResponse.getAllHeaders());
            response.setHttpResponseCode(statusCode);

            StatusCodeChecker statusCodeChecker = currentRequest.getStatusCodeChecker();
            if (statusCodeChecker == null) {
                statusCodeChecker = defaultStatusCodeChecker;
            }
            if (statusCodeChecker != null) {
                response.setSuccess(statusCodeChecker.isSuccess(statusCode));
            }

            InputStream responseContentStream = httpResponse.getEntity() == null ? null : httpResponse.getEntity().getContent();

            if (responseContentStream != null) {
                if (currentRequest.getIsCompressed()) {
                    responseContentStream = new GZIPInputStream(responseContentStream);
                }
                if (currentRequest.isCancelled()) {
                    return;
                }
                response.setResponseData(DataUtils.convertStreamToBytes(responseContentStream));
            }

            if (currentRequest.isCancelled()) {
                return;
            }
            currentRequest.getResponseController().handleResponse(response);
        } catch (URISyntaxException e1) {
            notifyError("Invalid URL.", e1);
        } catch (UnknownHostException e) {
            notifyError("Server not found.", e);
        } catch (SocketException esoc) {
            notifyError("Time out.", esoc);
        } catch (IOException e) {
            notifyError("There are IO error.", e);
        } catch (Exception e) {
            notifyError("There are some problem.", e);
        } finally {
            //httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * @param errorMessage
     * @param exception
     */
    private void notifyError(String errorMessage, Exception exception) {
        if (exception == null) {
            Log.e(LOG_TAG, "Error Response: " + errorMessage);
        } else {
            Log.e(LOG_TAG, "Error Response: " + errorMessage, exception);
        }
        Response response = new Response();
        response.setRequestData(currentRequest.getRequestData());
        response.setDataType(currentRequest.getDataType());
        response.setErrorMessage(errorMessage);
        response.setSuccess(false);
        response.setException(exception);
        if (currentRequest.isCancelled()) {
            return;
        }
        currentRequest.getResponseController().handleResponse(response);
    }

    public void addRequest(ServiceRequest request) {
        try {
            if (request.getPriority() == PRIORITY.HIGH) {
                highPriorityQueue.add(0, request);
            } else {
                lowPriorityQueue.addElement(request);
            }
            interrupt();
        } catch (Exception ex) {
//            Log.e(LOG_TAG, "addRequest()", ex);
        }
    }

    /**
     * @return the currentRequest
     */
    public ServiceRequest getCurrentRequest() {
        return currentRequest;
    }

    /**
     * @return the nextRequest
     */
    public ServiceRequest getNextRequest() {
        if (highPriorityQueue.size() > 0) {
            return highPriorityQueue.get(0);
        } else if (lowPriorityQueue.size() > 0) {
            return lowPriorityQueue.get(0);
        } else {
            return null;
        }
    }

    /**
     * @return true if pRequest is found and removed from high/low queue.
     */
    public boolean removeRequest(ServiceRequest pRequest, Comparator<ServiceRequest> pComparator) {
        ServiceRequest tempRq = null;
        Vector<ServiceRequest> targetQueue = lowPriorityQueue;
        if (pRequest.getPriority() == PRIORITY.HIGH) {
            targetQueue = highPriorityQueue;
        }
        for (int i = 0; i < targetQueue.size(); i++) {
            try {
                tempRq = targetQueue.get(i);
            } catch (Exception e) {
                return false;
            }
            if (tempRq != null && pComparator.compare(tempRq, pRequest) == 0) {
                return targetQueue.removeElement(tempRq);
            }
        }
        return false;
    }

    private void saveSharedPreferencesCookies(List<Cookie> cookies) {
        SerializableCookie[] serializableCookies = new SerializableCookie[cookies.size()];
        for (int i = 0; i < cookies.size(); i++) {
            SerializableCookie serializableCookie = new SerializableCookie(cookies.get(i));
            serializableCookies[i] = serializableCookie;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(currentRequest.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        ObjectOutputStream objectOutput;
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            objectOutput = new ObjectOutputStream(arrayOutputStream);


            objectOutput.writeObject(serializableCookies);
            byte[] data = arrayOutputStream.toByteArray();
            objectOutput.close();
            arrayOutputStream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
            b64.write(data);
            b64.close();
            out.close();

            editor.putString("cookies", new String(out.toByteArray()));
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Cookie> loadSharedPreferencesCookie() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(currentRequest.getContext());
        byte[] bytes = preferences.getString("cookies", "{}").getBytes();
        if (bytes.length == 0 || bytes.length == 2)
            return null;
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
        ObjectInputStream in;
        List<Cookie> cookies = new ArrayList<Cookie>();
        SerializableCookie[] serializableCookies;
        try {
            in = new ObjectInputStream(base64InputStream);
            serializableCookies = (SerializableCookie[]) in.readObject();
            for (int i = 0; i < serializableCookies.length; i++) {
                Cookie cookie = serializableCookies[i].getCookie();
                cookies.add(cookie);
            }
            return cookies;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
