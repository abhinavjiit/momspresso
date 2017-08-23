package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.googlemap.controller.GetDirectionController;
import com.mycity4kids.googlemap.maputils.MapUtils;
import com.mycity4kids.googlemap.models.Directions;
import com.mycity4kids.googlemap.models.Steps;
import com.mycity4kids.googlemap.models.TransitModel;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.businesseventdetails.DetailMap;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.location.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MapFragment extends BaseFragment implements OnClickListener, OnMapReadyCallback {
    private GoogleMap googleMap;
    private DetailMap mapInfo;
    private LinearLayout mTransitLayout;
    private RelativeLayout mRelativeLout;
    private TextView transitBtn;
    public ArrayList<TransitModel> transitList = new ArrayList<>();

    String sourceAddressJson;
    String destAddressJson;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null, false);
        Utils.pushOpenScreenEvent(getActivity(), "Map for resource/event", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        try {
//			if(googleMap!=null){
//				googleMap.clear();
//			}
//			googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


            mTransitLayout = (LinearLayout) view.findViewById(R.id.transit_lout);
            mRelativeLout = (RelativeLayout) view.findViewById(R.id.parentLout);
            transitBtn = (TextView) view.findViewById(R.id.transitBtn);
            //  MapUtils.initilizeMapWithCurrentLocation(getActivity(), googleMap);

            view.findViewById(R.id.transitBtn).setOnClickListener(this);
            view.findViewById(R.id.directionBtn).setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    private void mapMarkersInitialization() {
        View view = getView();
        if (view == null) {
            return;
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            mapInfo = bundle.getParcelable("mapDetails");
            String address = bundle.getString("addressData");
            if (!StringUtils.isNullOrEmpty(address)) {
                TextView textview = (TextView) view.findViewById(R.id.address_txt);
                textview.setText(address);
            } else {
                view.findViewById(R.id.address_lout).setVisibility(View.GONE);
            }

            ArrayList<String> latitudeList = new ArrayList<String>();
            ArrayList<String> longitudeList = new ArrayList<String>();
            ArrayList<String> locations = new ArrayList<String>();
            String destinationLat = null;
            String destinationLong = null;
            if (mapInfo != null) {
                destinationLat = mapInfo.getLatitude();
                destinationLong = mapInfo.getLongitude();
            }
            if (destinationLat == null || destinationLong == null) {
                MapUtils.initilizeMapWithCurrentLocation(getActivity(), googleMap);

                //Toast.makeText(getActivity(), "We are getting wrong lattitude & longitude from server side!", Toast.LENGTH_SHORT).show();
            } else {
                GPSTracker getCurrentLocation = new GPSTracker(getActivity());
                double _latitude = getCurrentLocation.getLatitude();
                double _longitude = getCurrentLocation.getLongitude();


                latitudeList.add(destinationLat == null ? "" + 0.0 : destinationLat);
                latitudeList.add(String.valueOf(_latitude) == null ? "" + 0.0 : String.valueOf(_latitude));
                longitudeList.add(destinationLong == null ? "" + 0.0 : destinationLong);
                longitudeList.add(String.valueOf(_longitude) == null ? "" + 0.0 : String.valueOf(_longitude));
                locations.add("Destination Location");
                locations.add("Current Location");

                MapUtils.initilizeMapWithMultipleMarkers(getActivity(), googleMap, latitudeList, longitudeList, locations);
            }

            GetDirectionController _controller = new GetDirectionController(getActivity(), this);
            _controller.getData(AppConstants.GET_GOOGLE_MAP_TRANSIT, mapInfo);


        }
    }

    public void TransitPopUpClick(DetailMap model) {
        GetDirectionController _controller = new GetDirectionController(getActivity(), this);
        showProgressDialog(getActivity().getResources().getString(R.string.please_wait));
        googleMap.clear();
        _controller.getData(AppConstants.GET_GOOGLE_MAP_DIRECTIONS, model);

    }

    public void transitBtnClick() {
        // show popup
        if (transitList.isEmpty()) {

            ToastUtils.showToast(getActivity(), "Map is not initialise.Please wait");

        } else {
            TransitMapFragment dialog = new TransitMapFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("transitlist", transitList);
            dialog.setArguments(args);
            dialog.show(getActivity().getFragmentManager(), dialog.getClass().getName());
        }

    }

    public void directionBtnClick() {
        googleMap.clear();
        if (isEnableNetwork()) {

            GetDirectionController getDirectionController = new GetDirectionController(getActivity(), this);
            DetailMap detailMap = new DetailMap();
            detailMap.setLatitude("" + MapUtils.getLocation(getActivity()).latitude);
            detailMap.setLongitude("" + MapUtils.getLocation(getActivity()).longitude);
            getDirectionController.getData(AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST, detailMap);


//            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%s,%s", _latitude,_longitude, mapInfo.getLatitude(),mapInfo.getLongitude());
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//            startActivity(intent);
         /*   String sourceAddressJson = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&latlng=%f,%f", _latitude, _longitude);
            String destAddressJson = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&latlng=%s,%s", mapInfo.getLatitude(), mapInfo.getLongitude());

            String sourceAddressString, destAddressString;
            JSONObject js = null;
            try {
                js = new JSONObject(sourceAddressJson);
                sourceAddressString = ((JSONObject) js.getJSONArray("results").get(0)).getJSONObject("formatted_address").toString();
                js = new JSONObject(destAddressJson);
                destAddressString = ((JSONObject) js.getJSONArray("results").get(0)).getJSONObject("formatted_address").toString();

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s", sourceAddressString, destAddressString);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            /*showProgressDialog(getActivity().getResources().getString(R.string.please_wait));
            GetDirectionController _controller = new GetDirectionController(getActivity(), this);
            _controller.getData(AppConstants.GET_GOOGLE_MAP_DIRECTIONS, mapInfo);*/
        } else {
            Toast.makeText(getActivity(), "It seems that location services are disabled on your device.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_frame, mapFragment).commit();

        mapFragment.getMapAsync(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!getActivity().isFinishing()) {
            Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
            if (getActivity() != null && fragment != null) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        String responseData = new String(response.getResponseData());
        switch (response.getDataType()) {

            case AppConstants.GET_GOOGLE_MAP_DIRECTIONS:


                Directions directions = new Gson().fromJson(responseData, Directions.class);
                if (directions.getStatus().equals("OK")) {
                    ArrayList<Steps> stepsList = directions.getRoutes().get(0).getLegs().get(0).getSteps();
                    ArrayList<String> endLatList = new ArrayList<String>();
                    ArrayList<String> endLngList = new ArrayList<String>();
                    ArrayList<String> startLatList = new ArrayList<String>();
                    ArrayList<String> startLngList = new ArrayList<String>();

                    for (Steps steps : stepsList) {
                        endLatList.add(steps.getEnd_location().getLat() + "");
                        endLngList.add(steps.getEnd_location().getLng() + "");
                        startLatList.add(steps.getStart_location().getLat() + "");
                        startLngList.add(steps.getStart_location().getLng() + "");
                    }
                    String extraLocation = directions.getRoutes().get(0).getLegs().get(0).getEnd_address();
                    MapUtils.drawWalkingPathOnMap(getActivity(), googleMap, endLatList, endLngList, startLatList, startLngList, extraLocation);

                } else {
                    Toast.makeText(getActivity(), "Please enter address or pin code.", Toast.LENGTH_LONG).show();
                }
                break;
            case AppConstants.GET_GOOGLE_MAP_TRANSIT:
                Directions transitDirection = new Gson().fromJson(responseData, Directions.class);
                if (transitDirection.getStatus().equals("OK")) {
                    ArrayList<Steps> stepsList = transitDirection.getRoutes().get(0).getLegs().get(0).getSteps();
                    transitList = new ArrayList<TransitModel>();
                    for (Steps steps : stepsList) {
                        TransitModel _transitModel = new TransitModel();
                        _transitModel.setStartLatitude("" + steps.getStart_location().getLat());
                        _transitModel.setEndLatitude("" + steps.getEnd_location().getLat());
                        _transitModel.setEndLongitude("" + steps.getEnd_location().getLng());
                        _transitModel.setStartLongitude("" + steps.getStart_location().getLng());
                        _transitModel.setInstructions("" + steps.getHtml_instructions());
                        _transitModel.setTravelMode("" + steps.getTravel_mode());
                        transitList.add(_transitModel);
                    }

				/*if(transitDirection.getRoutes()==null || transitDirection.getRoutes().size()<=0){
                    transitBtn.setVisibility(View.GONE);
				}else{
					transitBtn.setVisibility(View.VISIBLE);
				}*/

                    LayoutInflater inflaterLout = LayoutInflater.from(getActivity());

                    for (TransitModel transitModel : transitList) {
                        TextView textView = (TextView) inflaterLout.inflate(R.layout.custom_transit_cell, null);
                        textView.setTag(transitModel);
                        textView.setText(transitModel.getInstructions());
                        textView.setOnClickListener(this);
                        mTransitLayout.addView(textView);
                    }

                }
                break;
            case AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST:
                sourceAddressJson=responseData;
                GetDirectionController getDirectionController = new GetDirectionController(getActivity(), this);
                DetailMap detailMap = new DetailMap();
                detailMap.setLatitude(mapInfo.getLatitude());
                detailMap.setLongitude(mapInfo.getLongitude());
                getDirectionController.getData(AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST_DEST, detailMap);
                break;
            case AppConstants.GET_ADDRESS_FROM_LATLONG_REQUEST_DEST:
                destAddressJson=responseData;

                String sourceAddressString, destAddressString;
                JSONObject js = null;
                try {
                    js = new JSONObject(sourceAddressJson);
                    sourceAddressString = ((JSONObject) js.getJSONArray("results").get(0)).getString("formatted_address");
                    js = new JSONObject(destAddressJson);
                    destAddressString = ((JSONObject) js.getJSONArray("results").get(0)).getString("formatted_address");

                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s", sourceAddressString, destAddressString);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void collepseView() {
        if (mTransitLayout.getVisibility() == View.GONE) {
            //	Animation topToBottom = AnimationUtils.loadAnimation(getActivity(),R.anim.bottom_up);
            //	mTransitLayout.startAnimation(topToBottom);
            mTransitLayout.setVisibility(View.VISIBLE);
        } else {
            mTransitLayout.setVisibility(View.GONE);
            //	Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),	R.anim.bottom_down);
            //	bottomUp.setAnimationListener(makeTopGone);
            //	mTransitLayout.startAnimation(bottomUp);
        }
    }

    final AnimationListener makeTopGone = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d("View Pager", "onAnimationEnd - makeTopGone");
            mTransitLayout.setVisibility(View.GONE);

        }
    };


    @Override
    public void onClick(View v) {
        GetDirectionController _controller = new GetDirectionController(getActivity(), this);
        switch (v.getId()) {
            case R.id.transitBtn:
                if (isEnableNetwork()) {
                    collepseView();
                } else {
                    Toast.makeText(getActivity(), "It seems that location services are disabled on your device.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.directionBtn:
                if (isEnableNetwork()) {
                   /* googleMap.clear();
                    showProgressDialog(getActivity().getResources().getString(R.string.please_wait));
                   /* _controller.getData(AppConstants.GET_GOOGLE_MAP_DIRECTIONS, mapInfo);*//*
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", transitList.get(0).getStartLatitude(), transitList.get(0).getStartLongitude(), transitList.get(0).getEndLatitude(), transitList.get(0).getEndLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);*/
                    GPSTracker getCurrentLocation = new GPSTracker(getActivity());

                    double _latitude = getCurrentLocation.getLatitude();
                    double _longitude = getCurrentLocation.getLongitude();
                    String sourceAddressJson = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&latlng=%f,%f", _latitude, _longitude);
                    String destAddressJson = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&latlng=%f,%f", mapInfo.getLatitude(), mapInfo.getLongitude());

                    String sourceAddressString, destAddressString;
                    JSONObject js = null;
                    try {
                        js = new JSONObject(sourceAddressJson);
                        sourceAddressString = ((JSONObject) js.getJSONArray("results").get(0)).getJSONObject("formatted_address").toString();
                        js = new JSONObject(destAddressJson);
                        destAddressString = ((JSONObject) js.getJSONArray("results").get(0)).getJSONObject("formatted_address").toString();

                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s", sourceAddressString, destAddressString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), "It seems that location services are disabled on your device.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.transit_btn:
                showProgressDialog(getActivity().getResources().getString(R.string.please_wait));
                googleMap.clear();
                if (v instanceof TextView) {
                    TextView textView = (TextView) v;

                    TransitModel model = (TransitModel) textView.getTag();
                    //	textView.setText(model.getInstructions());
                    DetailMap mapInfo = new DetailMap();
                    mapInfo.setLatitude(model.getEndLatitude());
                    mapInfo.setLongitude(model.getEndLongitude());

                    _controller.getData(AppConstants.GET_GOOGLE_MAP_DIRECTIONS, mapInfo);
                }

                break;

            default:
                break;
        }

    }

    private boolean isEnableNetwork() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return !(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapMarkersInitialization();
        googleMap.setMyLocationEnabled(true);

    }
}
