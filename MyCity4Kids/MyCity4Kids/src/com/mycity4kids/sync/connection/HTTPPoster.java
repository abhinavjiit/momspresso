package com.mycity4kids.sync.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;
import android.webkit.CookieManager;

import com.kelltontech.network.HttpClientFactory;
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.SerializableCookie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

/**
 * to execute the HTTP Connection.
 *
 * @author vivek.srivastava
 */
public class HTTPPoster {
    private static final int TIMEOUT_CONN = 120000;
    private static final int TIMEOUT_SO = 120000;
    private static final int MCC_TIMEOUT = 120000;
    private static CookieStore store = null;

    /**
     * execute the HTTP Connection and return the response.
     *
     * @param url     URL of the web service API.
     * @param kvPairs {@link HashMap} for key value.
     */
    public static String doPost(String url, Map<String, String> kvPairs, Map<String, String> header, Context context)
            throws ClientProtocolException, IOException, PackageManager.NameNotFoundException {
//		HttpClient httpClient = getNewHttpClient(new BasicHttpParams());
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        if (header != null && header.isEmpty() == false) {
            String k, v;
            Iterator<String> itKeys = header.keySet().iterator();

            while (itKeys.hasNext()) {
                k = itKeys.next();
                v = header.get(k);
                httppost.addHeader(k, v);
            }
        }
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        String version = pInfo.versionName;
        httppost.addHeader("appVersion", version);
        if (kvPairs != null && kvPairs.isEmpty() == false) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(kvPairs.size());
            String k, v;
            Iterator<String> itKeys = kvPairs.keySet().iterator();

            while (itKeys.hasNext()) {
                k = itKeys.next();
                v = kvPairs.get(k);
                nameValuePairs.add(new BasicNameValuePair(k, v));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        }
        List<Cookie> cookies = loadSharedPreferencesCookie(context);
        if (cookies != null) {
            CookieStore cookieStore = new BasicCookieStore();
            for (int i = 0; i < cookies.size(); i++) {
                cookieStore.addCookie(cookies.get(i));
//                Log.d("MycityCookie ", "Name = " + cookies.get(i).getName() + " Value = " + cookies.get(i).getValue());
            }
            ((DefaultHttpClient) httpClient).setCookieStore(cookieStore);
        }

        HttpResponse response = httpClient.execute(httppost);
        cookies = ((DefaultHttpClient) httpClient).getCookieStore().getCookies();
        saveSharedPreferencesCookies(cookies, context);
//        if (cookies != null) {
//            for (int i = 0; i < cookies.size(); i++) {
//                Log.d("MycityCookie Response", "Name = " + cookies.get(i).getName() + " Value = " + cookies.get(i).getValue());
//            }
//        }

        InputStream responseContentStream = response.getEntity() == null ? null : response.getEntity().getContent();
        String responseString = new String(DataUtils.convertStreamToBytes(responseContentStream));
//		String responseString = EntityUtils.toString(response.getEntity());

        httpClient.getConnectionManager().shutdown();
        return responseString;
    }

    /**
     * execute the HTTP Post Connection and return response.
     *
     * @param url
     * @param header
     * @param body
     * @return response
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> header, String body, Context context) throws ClientProtocolException,
            IOException {
        HttpClient httpclient = getNewHttpClient(new BasicHttpParams());

        HttpPost httppost = new HttpPost(url);

        if (header != null && header.isEmpty() == false) {
            String k, v;
            Iterator<String> itKeys = header.keySet().iterator();

            while (itKeys.hasNext()) {
                k = itKeys.next();
                v = header.get(k);
                httppost.setHeader(k, v);
            }

        }
        if (body != null && !body.trim().equals("")) {

            StringEntity entity = new StringEntity(body, "UTF-8");
            httppost.setEntity(entity);
        }

        HttpResponse response = httpclient.execute(httppost);
        String responseString = EntityUtils.toString(response.getEntity());
        return responseString;
    }

    /**
     * execute the HTTP Get Connection and return response.
     *
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> header, Context context) throws IOException, PackageManager.NameNotFoundException {

//        HttpClient httpclient = getNewHttpClient(new BasicHttpParams());
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httppost = new HttpGet(url);
        if (header != null && header.isEmpty() == false) {
            String k, v;
            Iterator<String> itKeys = header.keySet().iterator();

            while (itKeys.hasNext()) {
                k = itKeys.next();
                v = header.get(k);
                httppost.setHeader(k, v);
            }
        }
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        String version = pInfo.versionName;
        httppost.addHeader("appVersion", version);
        List<Cookie> cookies = loadSharedPreferencesCookie(context);
        if (cookies != null) {
            CookieStore cookieStore = new BasicCookieStore();
            for (int i = 0; i < cookies.size(); i++) {
                cookieStore.addCookie(cookies.get(i));
//                Log.d("MycityCookie ", "Name = " + cookies.get(i).getName() + " Value = " + cookies.get(i).getValue());
            }
            ((DefaultHttpClient) httpclient).setCookieStore(cookieStore);
        }
        HttpResponse response = httpclient.execute(httppost);
        cookies = ((DefaultHttpClient) httpclient).getCookieStore().getCookies();
        saveSharedPreferencesCookies(cookies, context);
//        if (cookies != null) {
//            for (int i = 0; i < cookies.size(); i++) {
//                Log.d("MycityCookie Response", "Name = " + cookies.get(i).getName() + " Value = " + cookies.get(i).getValue());
//            }
//        }
        String responseString = EntityUtils.toString(response.getEntity());

        return responseString;
    }

    /**
     * execute the HTTP Put Connection and return response.
     *
     * @param context
     * @param webserviceUrl
     * @param jsonData
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doPut(String webserviceUrl, String jsonData, Context context) throws ClientProtocolException, IOException {

        HttpClient httpclient = getNewHttpClient(new BasicHttpParams());
        HttpPut httpPut = new HttpPut(webserviceUrl);
        httpPut.setHeader("Content-type", "application/json");

        StringEntity se = new StringEntity(jsonData);
        httpPut.setEntity(se);

        HttpResponse response = httpclient.execute(httpPut);
        String responseString = EntityUtils.toString(response.getEntity());

        return responseString;
    }

    /**
     * @param params
     * @return HttpClient
     */
    private static HttpClient getNewHttpClient(HttpParams params) {
        try {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

            params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
            params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
            params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
            params.setLongParameter(ConnManagerPNames.TIMEOUT, MCC_TIMEOUT);
            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_CONN);
            HttpConnectionParams.setSoTimeout(params, TIMEOUT_SO);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

            ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
            return new DefaultHttpClient(cm, params);
        } catch (Exception e) {
            params.setLongParameter(ConnManagerPNames.TIMEOUT, MCC_TIMEOUT);
            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_CONN);
            HttpConnectionParams.setSoTimeout(params, TIMEOUT_SO);
            return new DefaultHttpClient(params);
        }
    }

    private static void saveSharedPreferencesCookies(List<Cookie> cookies, Context context) {
        SerializableCookie[] serializableCookies = new SerializableCookie[cookies.size()];
        for (int i = 0; i < cookies.size(); i++) {
            SerializableCookie serializableCookie = new SerializableCookie(cookies.get(i));
            serializableCookies[i] = serializableCookie;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
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

    private static List<Cookie> loadSharedPreferencesCookie(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
