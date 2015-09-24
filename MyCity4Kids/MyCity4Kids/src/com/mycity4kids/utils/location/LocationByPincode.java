package com.mycity4kids.utils.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.newmodels.PincodeModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.NearMyCity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 19-06-2015.
 */
public class LocationByPincode {

    private Context context;
    private String mPincode;
    private static String URL = "http://maps.googleapis.com/maps/api/geocode/json?address=";
    private PincodeModel _pincodeResponse;
    private Double _latitude = null;
    private Double _longitude = null;
    public int cityId;
    private ICallBackForCity mCallBackForCity;

    public LocationByPincode(Context ctx, String pincode, ICallBackForCity pCallBackForCity) {
        // TODO Auto-generated constructor stub
        context = ctx;
        mPincode = pincode;
        mCallBackForCity = pCallBackForCity;
        new GetLocation().execute();
    }

    public interface ICallBackForCity {
        public void call();
    }


    public class GetLocation extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                // http client
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;

                URL = "http://maps.googleapis.com/maps/api/geocode/json?address=";

                URL = URL + mPincode + "&region=in";
                // Checking http request method type

                Log.i("pinode url ", URL);

                HttpGet httpGet = new HttpGet(URL);

                httpResponse = httpClient.execute(httpGet);

                httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);

                // do json parsing

                _pincodeResponse = new Gson().fromJson(response, PincodeModel.class);

                // Log.i("pinode response ", new Gson().toJson(_pincodeResponse));

                ArrayList<PincodeModel.ResultArray> data = _pincodeResponse.getResults();
                if (data.size() > 0) {
                    _latitude = data.get(0).getGeometry().getLocation().getLat();
                    _longitude = data.get(0).getGeometry().getLocation().getLng();

                    Log.i("pinode lat long ", _latitude + " :" + _longitude);
                }

                if (_latitude == null || _longitude == null) {


                } else {
                    new NearMyCity(context, _latitude, _longitude, new NearMyCity.FetchCity() {

                        @Override
                        public void nearCity(City cityModel) {

                            cityId = cityModel.getCityId();

                            System.out.println("city : " + cityId);

                            /**
                             * save current city id in shared preference
                             */
                            MetroCity model = new MetroCity();
                            model.setId(cityModel.getCityId());
                            model.setName(cityModel.getCityName());
                            /**
                             * this city model will be save only one time on splash:
                             */
                            SharedPrefUtils.setCurrentCityModel(context, model);


                        }
                    });

                }

                return "";

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            } catch (ClientProtocolException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }


            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mCallBackForCity != null)
                mCallBackForCity.call();

        }

    }


}


