package com.mycity4kids.gtmutils;

import android.content.Context;

import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

/**
 * Created by anshul on 2/11/16.
 */
public class Utils {
    private Utils() {
        // private constructor.
    }

    /**
     * Push an "openScreen" event with the given screen name. Tags that match that event will fire.
     */
    public static void pushOpenScreenEvent(Context context, String screenName,String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        //dataLayer.pushEvent("screenOpen", DataLayer.mapOf("screenName",screenName));
        dataLayer.push(DataLayer.mapOf("event", "openScreen",        // Event, Name of Open Screen Event.
                "screenName", screenName,GTMTags.USER_ID,user));  // Name of screen name field, Screen name value.
    }

    /**
     * Push a "closeScreen" event with the given screen name. Tags that match that event will fire.
     */
    public static void pushCloseScreenEvent(Context context, String screenName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("screenOpen", DataLayer.mapOf("screenName", screenName));
    }

    public static void pushEvent(Context context,GTMEventType event,String user,String eventValue) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        // dataLayer.push("user", "monitor1");
        dataLayer.push(DataLayer.mapOf("event", event , GTMTags.USER_ID,user,GTMTags.ScreenName,eventValue));

    }
}
