package com.mycity4kids.googlemap;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.googlemap.models.Steps;

import java.util.ArrayList;

/**
 * this class would be remove when we implement map according to ui flow & needed method will be take
 * from here
 *
 * @author "Deepanker Chadhary"
 */
public class MapActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);


    }


    @Override
    protected void updateUi(Response response) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (getIntent().getBooleanExtra("extraShowPath", false)) {
            ArrayList<Steps> stepsList = getIntent().getParcelableArrayListExtra("parcelabeStepsList");
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
            String location = getIntent().getStringExtra("extraLocation");
            com.mycity4kids.googlemap.maputils.MapUtils.drawWalkingPathOnMap(MapActivity.this, googleMap, endLatList, endLngList, startLatList, startLngList, location);
        }
    }
}
