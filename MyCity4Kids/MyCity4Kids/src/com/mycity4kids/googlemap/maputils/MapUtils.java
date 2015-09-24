package com.mycity4kids.googlemap.maputils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mycity4kids.utils.location.GPSTracker;

/**
 * To show map and to add markers on it.
 * @author kapil.vij
 */
public class MapUtils {


	/**
	 * To load Map With current Location of a user.
	 * @param context
	 * @param googleMap 
	 */
	public static void initilizeMapWithCurrentLocation(Context context, GoogleMap googleMap) {
		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(context, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		} else {
			addMarkerWithCameraZooming(googleMap, getLocation(context).latitude, getLocation(context).longitude, "Current Location");
		}
	}

	/**
	 * To get current location of the device based on GPS tracking
	 * @param context
	 * @return
	 */
	public static LatLng getLocation(Context context){
		GPSTracker gps = new GPSTracker(context);
		LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
		return latLng;
	}

	/**
	 * Adding Marker to given long, lat with animate camera zooming.
	 * @param googleMap
	 * @param latitude
	 * @param longitude
	 * @param title
	 */
	private static void addMarkerWithCameraZooming(GoogleMap googleMap, double latitude, double longitude, String title) {
		googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title));
		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	/**
	 * To create map with user defined Lat Long value
	 * @param context
	 * @param googleMap
	 * @param latitude
	 * @param longitude
	 * @param location 
	 */
	public static void initilizeMapWithSingleMarker(Context context, GoogleMap googleMap, double latitude, double longitude, String location) {
		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(context, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		} else {
			addMarkerWithCameraZooming(googleMap, latitude, longitude, location);
		}
	}

	/**
	 * Adding multiple markers on the map view
	 * @param context
	 * @param googleMap
	 * @param latitudeList
	 * @param longitudeList
	 * @param locationList
	 */
	public static void initilizeMapWithMultipleMarkers(Context context, GoogleMap googleMap, ArrayList<String> latitudeList, ArrayList<String> longitudeList, ArrayList<String> locationList) {
		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(context, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		} else {
			addMultipleMarkersWithCameraZooming(googleMap, latitudeList, longitudeList, locationList);
		}
	}

	/**
	 * Adding multiple markers on map with camera zoomed to first location in list of LatLng
	 * @param googleMap
	 * @param latitudeList
	 * @param longitudeList
	 * @param locationList
	 */
	private static void addMultipleMarkersWithCameraZooming(
			GoogleMap googleMap, ArrayList<String> latitudeList,
			ArrayList<String> longitudeList, ArrayList<String> locationList) {
		for (int i=0; i< latitudeList.size(); i++) {
			googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitudeList.get(i)), Double.parseDouble(longitudeList.get(i)))).title(locationList.get(i)));
		}
		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(latitudeList.get(0)), Double.parseDouble(longitudeList.get(0)))).zoom(12).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

	}
	
	/**
	 * To draw air path between source and destination LatLng
	 * @param context
	 * @param googleMap
	 * @param latLngs
	 */
	private static void drawPath(Context context, GoogleMap googleMap, LatLng[] latLngs){
		googleMap.addPolyline(new PolylineOptions()
		.add(latLngs)
		.color(0xffff0000).width(5)
		.geodesic(true));
	}

	
	/**
	 * To draw path on map view with source and destination markers, and camera zooming.
	 * @param context
	 * @param googleMap
	 * @param endLatList
	 * @param endLngList
	 * @param startLatList
	 * @param startLngList
	 * @param location
	 */
	public static void drawWalkingPathOnMap(Context context,
			GoogleMap googleMap, ArrayList<String> endLatList,
			ArrayList<String> endLngList, ArrayList<String> startLatList,
			ArrayList<String> startLngList, String location) {
		// TODO Auto-generated method stub
		
		googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(endLatList.get(endLatList.size() - 1)), Double.parseDouble(endLngList.get(endLngList.size() - 1)))).title(location));
		googleMap.addMarker(new MarkerOptions().position(getLocation(context)).title(getLocality(getLocation(context), context)));
		/*CircleOptions circleOptions = new CircleOptions()
		.center(getLocation(context)).strokeColor(0xffff0000).strokeWidth(5)
		.radius(10000); // In meters
		googleMap.addCircle(circleOptions);*/

		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(endLatList.get(endLatList.size() - 1)), Double.parseDouble(endLngList.get(endLngList.size() - 1)))).zoom(10).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		drawPath(context, googleMap, getLatLngsArray(context, endLatList, endLngList, startLatList, startLngList));

		
	}
	
	/**
	 * To generate LatLng array from obtained list of lat, lng coordinates
	 * @param context
	 * @param endLatList
	 * @param endLngList
	 * @param startLatList
	 * @param startLngList
	 * @return
	 */
	private static LatLng[] getLatLngsArray(Context context, ArrayList<String> endLatList, ArrayList<String> endLngList, ArrayList<String> startLatList, ArrayList<String> startLngList) {
		LatLng[] latLngs = new LatLng[endLatList.size() + 1];
		
		
		for(int i=0; i< endLatList.size(); i++){
			
			LatLng endLatLng = new LatLng(Double.parseDouble(startLatList.get(i)), Double.parseDouble(startLngList.get(i)));
			
			latLngs[i] = endLatLng;
			
		}
		latLngs[endLatList.size()] = new LatLng(Double.parseDouble(endLatList.get(endLatList.size() -1)), Double.parseDouble(endLngList.get(endLngList.size() -1)));
		/*latLngs[0] = getLocation(context);
		
		for(int i=0; i< endLatList.size(); i++){
			
			LatLng endLatLng = new LatLng(Double.parseDouble(endLatList.get(i)), Double.parseDouble(endLngList.get(i)));
			
			
			latLngs[i+1] = endLatLng;
//			latLngs[i + 1] = startLatLng;
			
		}*/
		
		return latLngs;
	}


	/**
	 * To know the location of the current marker, if searched from LatLng
	 * @param location
	 * @param context
	 * @return
	 */
	public static String getLocality(LatLng location, Context context) {
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
			return addresses.get(0).getLocality();
		} catch (IOException e) {
			e.printStackTrace();
			return "Current Loaction";
		}
	}

}
