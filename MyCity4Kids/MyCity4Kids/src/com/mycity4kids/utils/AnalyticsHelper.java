package com.mycity4kids.utils;

/*import java.util.HashMap;

import android.app.Activity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.mycity4kids.application.BaseApplication;

*//**
 * Description: AnalyticsHelper class includes the methods to integrate FlurryAgent analytics and Google Analytics tools in the FindIt Malaysia project
 *//*

public class AnalyticsHelper {
	
	*//*public static void onActivityCreate() {
		FlurryAgent.onPageView();
	}*//*
	
	*//**
	 * @param Methods to start Flurry and Google Analytics
	 * @Note To be called in the "onStart()" method of the MaxisMainActivity
	 *//*
	public static void onActivityStart(Activity activity) {
	//	FlurryAgent.onStartSession(activity, AppConstants.FLURRY_KEY); // For Flurry
		EasyTracker.getInstance(activity).activityStart(activity);// For Google Analytics
	}

	*//**
	 * @param Methods to start Flurry and Google Analytics
	 * @Note To be called in the "onStop()" method of the MaxisMainActivity
	 *//*
	public static void onActivityStop(Activity activity) {
		//FlurryAgent.onEndSession(activity);
		EasyTracker.getInstance(activity).activityStop(activity);
	}
	
	*//**
	 * @param Methods to  enable/disable Debug/DryRun mode
	 * @Note To be called in the "onStart()" method of the MaxisMainActivity
	 *//*
	public static void setLogEnabled(boolean state) {
	//	FlurryAgent.setLogEnabled(state); // For Flurry
		BaseApplication.getGaInstance().setDryRun(!state); // For Google Analytics
	}
	
	public static void logEvent(String event) {
	//	FlurryAgent.logEvent(event); // For Flurry
		BaseApplication.getGaTracker().send(MapBuilder.createEvent(Fields.EVENT_CATEGORY, event, null, null).set(Fields.SCREEN_NAME, event).build());  // For Google Analytics
	}
	
	public static void logEvent(String event, boolean trackSession) {
	//	FlurryAgent.logEvent(event, trackSession);  // For Flurry
		BaseApplication.getGaTracker().send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, event).build());  // For Google Analytics
	}
	
	public static void logEvent(String event, HashMap<String, String> map) {
	//	FlurryAgent.logEvent(event,map); // For Flurry
		BaseApplication.getGaTracker().send(map);  // For Google Analytics
	}
	
	*//*public static void logEvent(String event, HashMap<String, String> map, boolean trackSession) {
		FlurryAgent.logEvent(event,map,trackSession);
	}

	public static void endTimedEvent(String event) {
		FlurryAgent.endTimedEvent(event);
	}
	
	public static void endTimedEvent(String event, HashMap<String, String> map) {
		FlurryAgent.endTimedEvent(event,map);
	}*//*
	
	*//*public static void onError(String errorId, String message, Throwable exception) {
		FlurryAgent.onError(errorId, message, exception);
		exception.printStackTrace();
		Log.e(AppConstants.FINDIT_ERROR_TAG, message, exception);
	}

	//Deprecated method
	public static void onError(String errorId, String message, String errorClass) {
		FlurryAgent.onError(errorId, message, errorClass);
	}
	
	public static void setUserID(String userId) {
		FlurryAgent.setUserId(userId);
	}
	
	public static void getReleaseVersion() {
		int v = FlurryAgent.getAgentVersion();
		Log.i("FlurryAgent version", String.valueOf(v));
	}
	
	public static void setContinueSession(long seconds) {
		FlurryAgent.setContinueSessionMillis(seconds * 1000);
	}*//*
	
}*/
