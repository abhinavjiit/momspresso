package com.mycity4kids.utils;

import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.preference.SharedPrefUtils;

import org.json.JSONObject;

/**
 * Created by hemant on 14/11/18.
 */

public class MixPanelUtils {

    public static void pushMomVlogClickEvent(MixpanelAPI mixpanel, int index, String screen) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            jsonObject.put("index", index);
            jsonObject.put("screen", screen);
            mixpanel.track("MomVlogClick", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pushAddMomVlogClickEvent(MixpanelAPI mixpanel, String screen) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            jsonObject.put("screen", screen);
            mixpanel.track("AddMomVlogClick", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
