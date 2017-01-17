package com.mycity4kids.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.version.RateVersion;
import com.mycity4kids.sync.CategorySyncService;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.activity.SplashActivity;

/**
 * To save the Preference for My City App
 *
 * @author deepanker.chaudhary
 */
public class SharedPrefUtils {
    /**
     * Preference Name
     */
    public static final String COMMON_PREF_FILE = "my_city_prefs";

    /**
     * Version related prefs constants
     */
    public static final String CITI_VERSION = "cityVersion";
    public static final String LOCALITY_VERSION = "localityVersion";
    public static final String CATEGORY_VERSION = "categoryVersion";
    public static final String CONFIG_CATEGORY_VERSION = "configCategoryVersion";
    public static final String POPULAR_CONFIG_CATEGORY_VERSION = "popularConfigCategoryVersion";
    public static final String USER_TYPE_VERSION = "userTypeVersion";
    /**
     * Selected City related prefs constants
     */
    public static final String SELECTED_CITY_ID = "cityId";
    public static final String SELECTED_CITY_NAME = "city_name";

    public static final String SELECTED_TASKLIST_ID = "tasklist_id";

    /**
     * Selected Profile Image URL prefs constants
     */
    public static final String PROFILE_IMAGE_URL = "imgURL";


    public static final String APP_RATE = "appRate";
    public static final String IS_APP_RATE_COMPLETE = "isAppRateComplete";
    public static final String IS_APP_WANT_UPGRADE = "isAppWantUpgrade";
    public static final String APP_UPGRADE_MESSAGE = "appUpgradeMessage";

    public static final String PUSH_TOKEN_UPGRADE = "isTokenUpdate";


    // user detail model

    public static final String USER_ID = "userid";
    public static final String DYNAMO_USER_ID = "dynamoUserid";
    public static final String FAMILY_ID = "familyid";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String COLOR_CODE = "colorcode";
    public static final String USER_NAME = "username";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String SESSIONID = "sessionid";
    public static final String MC4KTOKEN = "mc4kToken";
    public static final String IS_USER_VALIDATED = "isValidated";


    public static final String APPOINTMENT_TIMESTAMP = "appointment_timestamp";
    public static final String TASK_TIMESTAMP = "task_timestamp";
    private static final String DEVICE_TOKEN = "device_token";
    private static final String APP_VERSION_GCM = "app_version_gcm";
    private static final String ARTICLE_FILTERS = "article_filters";
    private static final String NOTIFICATION_APPOINTMENT = "notification_appointment";
    private static final String NOTIFICATION_TASK = "notification_task";
    public static final String SOCIAL_EVENTS_TIMESTAMP = "socialevents_timestamp";
    private static final String PINCODE = "pincode";
    private static final String IS_CITY_FETCHED = "is_city_fetched";
    private static final String EVENT_ID = "event_id";
    private static final String IS_HOME_FLAG = "homeflag";
    private static final String LOGOUT_FLAG = "logout_flag";

    private static final String IS_FIRST_TYM_CHECK = "firsttym_check";

    private static final String SIGNUP_FLAG = "signup_flag";

    private static final String RATE_NOW_FIRST_CHECK = "ratenow_Chk";

    private static final String PHOENIX_FIRST_LAUNCH_FLAG = "phoenixFirstLaunchFlag";
    private static final String FB_CONNECT_FIRST_LAUNCH_FLAG = "fbConnectFirstLaunchFlag";
    private static final String UPLOAD_VIDEO_FIRST_LAUNCH_FLAG = "uploadVideoFirstLaunchFlag";

    private static final String CHANGE_CITY_FLAG = "changeCityFlag";


    public static final String MOMSPRESSO_CATEGORY_ID = "momspressoCategoryId";
    public static final String MOMSPRESSO_DISPLAY_NAME = "momspressoDisplayName";

    public static final String LOCATION_LATITUDE = "latitude";
    public static final String LOCATION_LONGITUDE = "longitude";

    public static final String IS_FB_CONNECTED = "isFBConnected";

    /**
     * this shared preference save current versions for control city,locality,category APIs .
     *
     * @param pContext
     * @param pVersionData
     */
    public static void setSharedPrefVesion(Context pContext, VersionApiModel pVersionData) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putFloat(CITI_VERSION, pVersionData.getCityVersion());
        _editor.putFloat(LOCALITY_VERSION, pVersionData.getLocalityVersion());
        _editor.putFloat(CATEGORY_VERSION, pVersionData.getCategoryVersion());
        _editor.commit();
    }

    /**
     * this function will give current API version for city , Locality , & for category;- deepanker :)
     *
     * @param pContext
     * @return
     */

    public static VersionApiModel getSharedPrefVersion(Context pContext) {
        VersionApiModel _model = new VersionApiModel();
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        float value;
        value = _sharedPref.getFloat(CITI_VERSION, 0.0f);
        _model.setCityVersion(value);

        value = _sharedPref.getFloat(LOCALITY_VERSION, 0.0f);
        _model.setLocalityVersion(value);

        value = _sharedPref.getFloat(CATEGORY_VERSION, 0.0f);
        _model.setCategoryVersion(value);

        return _model;

    }


    public static void clearPrefrence(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.clear().commit();
    }

    public static void setCurrentCityModel(Context pContext, MetroCity pModel) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(SELECTED_CITY_ID, pModel.getId());
        _editor.putString(SELECTED_CITY_NAME, pModel.getName());
        _editor.commit();
    }

    public static void setTaskListID(Context pContext, int id) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(SELECTED_TASKLIST_ID, id);
        _editor.commit();
    }

    public static void setConfigCategoryVersion(Context pContext, int id) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(CONFIG_CATEGORY_VERSION, id);
        _editor.commit();
    }

    public static int getConfigCategoryVersion(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        int id = 0;
        id = (_sharedPref.getInt(CONFIG_CATEGORY_VERSION, 0));
        return id;
    }

    public static int getSignupFlag(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        int id = 1;
        id = (_sharedPref.getInt(SIGNUP_FLAG, 0));
        return id;
    }


    public static void setSignupFlag(Context pContext, int id) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(SIGNUP_FLAG, id);
        _editor.commit();
    }

    public static int getTaskListID(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        int id = 0;
        id = (_sharedPref.getInt(SELECTED_TASKLIST_ID, 0));
        return id;
    }

    public static void setHomeCheckFlag(Context pContext, boolean flag) {
        // true means today screen
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(IS_HOME_FLAG, flag);
        _editor.commit();
    }

    public static boolean getLogoutFlag(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(LOGOUT_FLAG, false));
        return flag;
    }

    public static void setLogoutFlag(Context pContext, boolean flag) {
        // true means today screen
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(LOGOUT_FLAG, flag);
        _editor.commit();
    }

    public static boolean getRateNowCheck(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(RATE_NOW_FIRST_CHECK, false));
        return flag;
    }

    public static void setRateNowCheck(Context pContext, boolean flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(RATE_NOW_FIRST_CHECK, flag);
        _editor.commit();
    }

    public static boolean getHomeCheckFlag(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(IS_HOME_FLAG, false));
        return flag;
    }

    public static void setFirstTimeCheckFlag(Context pContext, boolean flag) {
        // true means today screen
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(IS_FIRST_TYM_CHECK, flag);
        _editor.commit();
    }

    public static boolean getFirstTimeCheckFlag(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(IS_FIRST_TYM_CHECK, false));
        return flag;
    }


    public static MetroCity getCurrentCityModel(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        MetroCity city = new MetroCity();
        city.setId(_sharedPref.getInt(SELECTED_CITY_ID, 1));
        city.setName(_sharedPref.getString(SELECTED_CITY_NAME, "Delhi-NCR"));
        return city;
    }

    // set userdeatil in prefrences model
    //added by khushboo

    public static void setUserDetailModel(Context pContext, UserInfo pModel) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(USER_ID, pModel.getId());
        _editor.putString(DYNAMO_USER_ID, pModel.getDynamoId());
        _editor.putString(EMAIL, pModel.getEmail());
        _editor.putString(MOBILE, pModel.getMobile_number());
        _editor.putString(COLOR_CODE, pModel.getColor_code());
        _editor.putInt(FAMILY_ID, pModel.getFamily_id());
        _editor.putString(USER_NAME, pModel.getFirst_name() + pModel.getLast_name());
        _editor.putString(FIRST_NAME, pModel.getFirst_name());
        _editor.putString(LAST_NAME, pModel.getLast_name());
        _editor.putString(SESSIONID, pModel.getSessionId());
        _editor.putString(MC4KTOKEN, pModel.getMc4kToken());
        _editor.putString(IS_USER_VALIDATED, pModel.getIsValidated());
        _editor.commit();
    }


    public static UserInfo getUserDetailModel(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        UserInfo user = new UserInfo();
        // Changed the userId from int to String when backend changed from sql to dyanoma.
        try {
            user.setId(_sharedPref.getString(USER_ID, "0"));
        } catch (ClassCastException cce) {
            user.setId("" + (_sharedPref.getInt(USER_ID, 0)));
        }
        user.setDynamoId(_sharedPref.getString(DYNAMO_USER_ID, "0"));
        user.setFamily_id(_sharedPref.getInt(FAMILY_ID, 0));
        user.setEmail(_sharedPref.getString(EMAIL, ""));
        user.setMobile_number(_sharedPref.getString(MOBILE, ""));
        user.setColor_code(_sharedPref.getString(COLOR_CODE, ""));
        user.setFirst_name(_sharedPref.getString(FIRST_NAME, ""));
        user.setLast_name(_sharedPref.getString(LAST_NAME, ""));
        user.setSessionId(_sharedPref.getString(SESSIONID, ""));
        user.setMc4kToken(_sharedPref.getString(MC4KTOKEN, ""));
        user.setIsValidated(_sharedPref.getString(IS_USER_VALIDATED, ""));
        return user;
    }


    public static long getAppointmentTimeSatmp(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        long TIME = _sharedPref.getLong(APPOINTMENT_TIMESTAMP, 0);
        return TIME;
    }

    public static void setAppointmentTimeSatmp(Context pContext, long imgUrl) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putLong(APPOINTMENT_TIMESTAMP, imgUrl);
        _editor.commit();
    }

    public static long getSocialEventsTimeSatmp(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        long TIME = _sharedPref.getLong(SOCIAL_EVENTS_TIMESTAMP, 0);
        return TIME;
    }

    public static void setSocialEventsTimeSatmp(Context pContext, long imgUrl) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putLong(SOCIAL_EVENTS_TIMESTAMP, imgUrl);
        _editor.commit();
    }

    public static long getTaskTimeSatmp(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        long TIME = _sharedPref.getLong(TASK_TIMESTAMP, 0);
        return TIME;
    }

    public static void setTaskTimeSatmp(Context pContext, long imgUrl) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putLong(TASK_TIMESTAMP, imgUrl);
        _editor.commit();
    }


    public static void setProfileImgUrl(Context pContext, String imgUrl) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(PROFILE_IMAGE_URL, imgUrl);
        _editor.commit();
    }


    public static String getProfileImgUrl(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        String profileImgUrl = _sharedPref.getString(PROFILE_IMAGE_URL, "");
        return profileImgUrl;
    }

    /**
     * these two methods are for App rating handling.
     * This Logic i am using in HomeCategoryActivity
     *
     * @param pContext
     * @param rateVersion
     */

    public static void setAppRateVersion(Context pContext, RateVersion rateVersion) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(APP_RATE, rateVersion.getAppRateVersion());
        _editor.putBoolean(IS_APP_RATE_COMPLETE, rateVersion.isAppRateComplete());
        _editor.commit();
    }


    public static RateVersion getRateVersion(Context pContext) {
        RateVersion rateVersion = new RateVersion();
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        rateVersion.setAppRateVersion(_sharedPref.getInt(APP_RATE, -7));
        rateVersion.setAppRateComplete(_sharedPref.getBoolean(IS_APP_RATE_COMPLETE, false));
        return rateVersion;
    }

    /**
     * this flag comes from server in
     * config api.if this flag is true then
     * we will show dialog for App upgrade.
     * This flag i am using in HomeCategoryActivity
     *
     * @param pContext
     * @param isAppWantsUpgrade
     */
    public static void setAppUgrade(Context pContext, boolean isAppWantsUpgrade) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(IS_APP_WANT_UPGRADE, isAppWantsUpgrade);
        _editor.commit();
    }

    public static boolean getAppUpgrade(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getBoolean(IS_APP_WANT_UPGRADE, false);
    }

    public static String getAppUgradeMessage(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(APP_UPGRADE_MESSAGE, "Please update your app to continue");
    }

    public static void setAppUgradeMessage(Context pContext, String appUpgradeMessage) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(APP_UPGRADE_MESSAGE, appUpgradeMessage);
        _editor.commit();
    }

    public static void setPushTokenUpdateToServer(Context pContext, boolean isAppWantsUpgrade) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(PUSH_TOKEN_UPGRADE, isAppWantsUpgrade);
        _editor.commit();
    }


    public static boolean getPushTokenUpdateToServer(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getBoolean(PUSH_TOKEN_UPGRADE, false);
    }

    public static void setDeviceToken(Context pContext, String deviceToken) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(DEVICE_TOKEN, deviceToken);
        _editor.commit();
    }

    public static String getDeviceToken(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(DEVICE_TOKEN, "");
    }

    public static void setAppVersion(Context pContext, int appVersion) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(APP_VERSION_GCM, appVersion);
        _editor.commit();
    }

    public static int getAppVersion(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getInt(APP_VERSION_GCM, Integer.MIN_VALUE);
    }

    public static void setArticleFiltersData(Context context, String filterData) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(ARTICLE_FILTERS, filterData);
        _editor.commit();
    }

    public static String getArticleFiltersData(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(ARTICLE_FILTERS, "");
    }

    public static String getNotificationPrefrence(Context context, boolean isAppointment) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(isAppointment ? NOTIFICATION_APPOINTMENT : NOTIFICATION_TASK, AppConstants.NOTIFICATION_PREF_BOTH);
    }

    public static void setNotificationPrefrence(Context context, String prefrenceValue, boolean isAppointment) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(isAppointment ? NOTIFICATION_APPOINTMENT : NOTIFICATION_TASK, prefrenceValue);
        _editor.commit();
    }

    public static void setpinCode(Context context, String pincode) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(PINCODE, pincode);
        _editor.commit();
    }

    public static String getpinCode(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(PINCODE, "");
    }


    public static void setCityFetched(Context context, boolean isCityIdFetched) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(IS_CITY_FETCHED, isCityIdFetched);
        _editor.commit();
    }

    public static boolean isCityFetched(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getBoolean(IS_CITY_FETCHED, true);
    }

    public static void setEventIdForCity(Context context, int eventId) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(EVENT_ID, eventId);
        _editor.commit();
    }

    public static int getEventIdForCity(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getInt(EVENT_ID, 6);
    }

    public static void setBaseURL(Context context, String baseURL) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString("BASE_URL", baseURL);
        _editor.commit();
    }

    public static String getBaseURL(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString("BASE_URL", AppConstants.LIVE_URL);
    }

    public static void setUserFamilyInvites(Context context, String userInviteModel) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString("userInviteModel", userInviteModel);
        _editor.commit();
    }

    public static String getUserFamilyInvites(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString("userInviteModel", "");
    }

    public static void setPhoenixFirstLaunch(Context context, boolean flag) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(PHOENIX_FIRST_LAUNCH_FLAG, flag);
        _editor.commit();
    }

    public static boolean isPhoenixFirstLaunch(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getBoolean(PHOENIX_FIRST_LAUNCH_FLAG, true);
    }

    public static void setFBConnectFirstLaunch(Context context, boolean flag) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(FB_CONNECT_FIRST_LAUNCH_FLAG, flag);
        _editor.commit();
    }

    public static boolean isFBConnectFirstLaunch(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getBoolean(FB_CONNECT_FIRST_LAUNCH_FLAG, true);
    }

    public static void setUploadVideoFirstLaunch(Context context, boolean flag) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(UPLOAD_VIDEO_FIRST_LAUNCH_FLAG, flag);
        _editor.commit();
    }

    public static boolean isUploadVideoFirstLaunch(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getBoolean(UPLOAD_VIDEO_FIRST_LAUNCH_FLAG, true);
    }

    public static void setChangeCityFlag(Context context, boolean flag) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(CHANGE_CITY_FLAG, flag);
        _editor.commit();
    }

    public static boolean isChangeCity(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getBoolean(CHANGE_CITY_FLAG, false);
    }

    public static void setMomspressoCategory(Context context, Topics category) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();

        _editor.putString(MOMSPRESSO_CATEGORY_ID, category.getId());
        _editor.putString(MOMSPRESSO_DISPLAY_NAME, category.getDisplay_name());
        _editor.commit();
    }

    public static Topics getMomspressoCategory(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Topics momspressoTopic = new Topics();
        momspressoTopic.setId(_sharedPref.getString(MOMSPRESSO_CATEGORY_ID, ""));
        momspressoTopic.setDisplay_name(_sharedPref.getString(MOMSPRESSO_DISPLAY_NAME, ""));
        return momspressoTopic;
    }

    public static void setConfigPopularCategoryVersion(Context pContext, int id) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(POPULAR_CONFIG_CATEGORY_VERSION, id);
        _editor.commit();
    }

    public static int getConfigPopularCategoryVersion(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        int id = 0;
        id = (_sharedPref.getInt(POPULAR_CONFIG_CATEGORY_VERSION, 0));
        return id;
    }

    public static void setUserLocationLatitude(Context pContext, double latitude) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(LOCATION_LATITUDE, "" + latitude);
        _editor.commit();
    }

    public static String getUserLocationLatitude(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(LOCATION_LATITUDE, "");
    }

    public static void setUserLocationLongitude(Context pContext, double longitude) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(LOCATION_LONGITUDE, "" + longitude);
        _editor.commit();
    }

    public static String getUserLocationLongitude(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(LOCATION_LONGITUDE, "");
    }

    public static void setNotificationConfig(Context pContext, String key, String value) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(key, value);
        _editor.commit();
    }

    public static String getNotificationConfig(Context context, String key) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(key, "");
    }

    public static void setFacebookConnectedFlag(Context pContext, String isExpired) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(IS_FB_CONNECTED, isExpired);
        _editor.commit();
    }

    public static String getFacebookConnectedFlag(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(IS_FB_CONNECTED, "");
    }

    public static void setNotificationType(Context pContext, String key, String value) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(key, value);
        _editor.commit();
    }

    public static String getNotificationType(Context context, String key) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(key, "");
    }

    public static void setUserTypeVersion(Context pContext, int id) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(USER_TYPE_VERSION, id);
        _editor.commit();
    }

    public static int getUserTypeVersion(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        int id = 0;
        id = (_sharedPref.getInt(USER_TYPE_VERSION, 0));
        return id;
    }

    public static void setConfigUserType(Context pContext, String key, String value) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(key, value);
        _editor.commit();
    }

    public static String getConfigUserType(Context context, String key) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(key, "");
    }
}
