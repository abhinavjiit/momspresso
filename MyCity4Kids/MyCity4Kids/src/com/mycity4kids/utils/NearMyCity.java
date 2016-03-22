package com.mycity4kids.utils;

import java.util.ArrayList;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;

import com.mycity4kids.models.city.City;

/**
 * this class will give the current Region name , City Id (City Model) according to current Location
 * @author Deepanker Chaudhary
 *
 */


public class NearMyCity {
	
	private Context mContext;
	private double mLattitude; 
	private double mLongitude ; 
	private FetchCity mFetchCity ; 
	
	public interface FetchCity {
		public void nearCity(City message) ; 
	}
	public NearMyCity(Context context , double lat , double longitude , FetchCity _fetch ) {
		mLattitude = lat ; 
		mLongitude = longitude ;
		mFetchCity = _fetch ;
		mContext = context ; 
		if(mContext.checkCallingOrSelfPermission(permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			throw new RuntimeException("Permission ACCESS_FINE_LOCATION is required") ;
		} else if(mContext.checkCallingOrSelfPermission(permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			throw new RuntimeException("Permission INTERNET is required") ;
		} else {
			if(mFetchCity == null ) {
				throw new RuntimeException("Call back interface can't be null ") ; 
			} else {
				fetchingLocation() ;	
			}	
		}
	}

//	private void fetchingLocation() {
//		try {
//			Geocoder geocoder = new Geocoder(mContext) ;
//			List<Address> addresses = geocoder.getFromLocation(mLattitude, mLongitude, 10) ;
//			if(addresses == null || addresses.size() == 0 ) {
//				mFetchCity.nearCity("City can't be determined now");
//			} else {
//				boolean isFetched = false ;
//				for(int i = 0 ; i < addresses.size() ; i++ ) {
//					Address address = addresses.get(i) ;
//					if(address.getLatitude() != 0.0 && address.getLongitude() != 0.0 ) {
//						isFetched = true ;
//						mFetchCity.nearCity(getNearMe(address));
//						break ;
//					} else {
//						continue ;
//					}
//				}
//				if( isFetched == false ) {
//					mFetchCity.nearCity("City can't be determined now");
//				}
//			}
//		} catch (Exception e) {
//			mFetchCity.nearCity("City can't be determined now");
//		}
//	}
	private void fetchingLocation() {
		mFetchCity.nearCity(getNearMe(mLattitude , mLongitude));
	}

//	private String getNearMe(Address address) {

	/**
	 * this method get current region according to current Location lat & long
	 * @param lat
	 * @param longi
	 * @return
	 */
	private City getNearMe(double lat , double longi) {
		City nearMe = null ;
		ArrayList<City> addresses = new ArrayList<City>() ;
		
		addresses.add(new City("Delhi-Ncr", 28.6100 , 77.2300 ,1 )) ;
		addresses.add(new City("Bangalore", 12.9667 , 77.5667,2 )) ; 
		addresses.add(new City("Mumbai", 18.9750 , 72.8258 ,3)) ; 
		addresses.add(new City("Pune", 18.5203 , 73.8567 ,4)) ; 
		addresses.add(new City("Hyderabad", 17.3660 , 78.4760,5 )) ; 
		addresses.add(new City("Chennai", 13.0474097 ,79.9288085,6 )) ;
		addresses.add(new City("Kolkata",22.5667,88.3667,7));
		addresses.add(new City("Jaipur",26.9000,75.8000,8));
		addresses.add(new City("Ahmedabad",23.0300,72.5800,9));
		
		for(int i = 0 ; i < addresses.size() ; i++) {
			
			if(Math.abs(distance( lat, longi , addresses.get(i).getLatitude(), addresses.get(i).getLongitude() , 'K')) <= 100 ) {
				nearMe = addresses.get(i) ;
				break ;
			}
		}
		if(nearMe == null ) {
			/**
			 * by default city id curresponds to city name others will be 1 right now means by default it 
			 * will Dehli-Ncr
			 */
			nearMe = new City("Delhi-Ncr", 0.0, 0.0, 1) ;
		}
		return nearMe ; 
	}
	/**
	 * this Methods calculate distance from your current location to 100 kilomether :)
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @param unit
	 * @return
	 */
	private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
      
	  double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;
      if (unit == 'K') {
        dist = dist * 1.609344;
      } else if (unit == 'N') {
        dist = dist * 0.8684;
        }
      return (dist/0.621);
    }
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }
    

}
