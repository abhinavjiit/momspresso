package com.mycity4kids.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.version.RateVersion;

import java.util.Locale;

/**
 * To save the Preference for My City App
 *
 * @author deepanker.chaudhary
 */
public class SharedPrefUtils {
    private SharedPrefUtils() {
    }

    /**
     * Preference Name
     */
    private static final String COMMON_PREF_FILE = "my_city_prefs";

    private static final String CONFIG_CATEGORY_VERSION = "configCategoryVersion";
    private static final String POPULAR_CONFIG_CATEGORY_VERSION = "popularConfigCategoryVersion";
    /**
     * Selected City related prefs constants
     */
    private static final String SELECTED_CITY_ID = "cityId";
    private static final String SELECTED_CITY_NAME = "city_name";
    private static final String SELECTED_NEW_CITY_ID = "newCityId";

    /**
     * Selected Profile Image URL prefs constants
     */
    private static final String PROFILE_IMAGE_URL = "imgURL";


    private static final String APP_RATE = "appRate";
    private static final String IS_APP_RATE_COMPLETE = "isAppRateComplete";
    private static final String IS_APP_WANT_UPGRADE = "isAppWantUpgrade";
    private static final String APP_UPGRADE_MESSAGE = "appUpgradeMessage";

    private static final String IS_REWARDS_ADDED = "isRewardsAdded";

    // user detail model
    public static final String USER_ID = "userid";
    private static final String DYNAMO_USER_ID = "dynamoUserid";
    private static final String FAMILY_ID = "familyid";
    private static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    private static final String COLOR_CODE = "colorcode";
    private static final String USER_NAME = "username";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String SESSIONID = "sessionid";
    private static final String MC4KTOKEN = "mc4kToken";
    private static final String IS_USER_VALIDATED = "isValidated";
    private static final String USER_LANG_SELECTION = "userLangSelection";
    private static final String SUBSCRIPTION_EMAIL = "subscriptionEmail";
    private static final String USER_TYPE = "userType";
    private static final String GENDER = "gender";
    private static final String NEW_USER_FLAG = "newUserFlag";

    private static final String DEVICE_TOKEN = "device_token";
    private static final String LOGOUT_FLAG = "logout_flag";

    private static final String COACHMARK_HOME = "coachmarkHome";
    private static final String COACHMARK_TOPICS = "coachmarkTopics";
    private static final String COACHMARK_TOPICS_ARTICLE = "coachmarkTopicsArticle";
    private static final String COACHMARK_ARTICLE_DETAILS = "coachmarkArticleDetails";
    private static final String COACHMARK_GROUP = "coachmarkGroups";
    private static final String COACHMARK_DRAWER = "coachmarkDrawer";
    private static final String COACHMARK_HOME_SCREEN = "coachmarkHomeScreen";
    private static final String COACHMARK_PROFILE = "coachmarkProfile";
    private static final String COACHMARK_MOMVLOG = "coachmarkMomVlog";
    private static final String COACHMARK_STORY = "coachmarkStory";

    private static final String CHANGE_CITY_FLAG = "changeCityFlag";

    private static final String LOCATION_LATITUDE = "latitude";
    private static final String LOCATION_LONGITUDE = "longitude";

    private static final String IS_FB_CONNECTED = "isFBConnected";
    private static final String USER_CITY_ID = "userCityId";
    private static final String LANGUAGE_FILTER = "languageFilter";

    private static final String FOLLOWED_TOPIC_COUNT = "followedTopicCount";

    private static final String BECOME_BLOGGER_FLAG = "becomeBloggerFlag";

    private static final String LOCALE_LANGUAGE_KEY = "language_key";
    private static final String ANONYMOUS_FLAG = "anonymousFlag";

    private static final String NOTIFICATION_CENTER_FLAG = "notificationCenterFlag";
    private static final String FOLLOW_TOPIC_APPROACH_FLAG = "followTopicApproachFlag";
    private static final String LAST_LOGIN_TIMESTAMP = "lastLoginTimestamp";
    private static final String USER_SKIPPED_FOLLOW_TOPIC_FLAG = "userSkippedFollowTopicFlag";
    private static final String HAS_TOPIC_SELECTION_CHANGED = "topicSelectionChangeFlag";
    private static final String FIREBASE_REMOTE_CONFIG_UPDATE_FLAG = "firebaseRemoteConfigUpdateFlag";
    private static final String DEMO_VIDEO_SEEN = "demovideoseen";

    private static final String DEFAULT_CAMPAIGN_SHOWN_FLAG = "defaultcampaignshownflag";

    private static final String HOME_AD_SLOT_URL = "homeAdSlotUrl";

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
        _editor.putString(SELECTED_NEW_CITY_ID, pModel.getNewCityId());
        _editor.commit();
    }

    public static MetroCity getCurrentCityModel(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        MetroCity city = new MetroCity();
        city.setId(_sharedPref.getInt(SELECTED_CITY_ID, 11));
        city.setName(_sharedPref.getString(SELECTED_CITY_NAME, "Others"));
        city.setNewCityId(_sharedPref.getString(SELECTED_NEW_CITY_ID, "city-11"));
        return city;
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
        _editor.putString(USER_CITY_ID, pModel.getCityId());
        _editor.putString(USER_LANG_SELECTION, pModel.getIsLangSelection());
        _editor.putString(SUBSCRIPTION_EMAIL, pModel.getSubscriptionEmail());
        _editor.putString(USER_TYPE, pModel.getUserType());
        _editor.putString(GENDER, pModel.getGender());
        _editor.putString(NEW_USER_FLAG, pModel.getIsNewUser());
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
        user.setCityId(_sharedPref.getString(USER_CITY_ID, "11"));
        user.setIsLangSelection(_sharedPref.getString(USER_LANG_SELECTION, "0"));
        user.setSubscriptionEmail(_sharedPref.getString(SUBSCRIPTION_EMAIL, _sharedPref.getString(EMAIL, "")));
        user.setUserType(_sharedPref.getString(USER_TYPE, "0"));
        user.setGender(_sharedPref.getString(GENDER, "0"));
        user.setIsNewUser(_sharedPref.getString(NEW_USER_FLAG, "0"));
        return user;
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

    public static void setIsRewardsAdded(Context pContext, String isRewardAdded) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(IS_REWARDS_ADDED, isRewardAdded);
        _editor.commit();
    }

    public static String getIsRewardsAdded(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(IS_REWARDS_ADDED, "");
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

    public static String getRewardsBaseURL(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString("BASE_URL", AppConstants.DEV_REWARDS_URL);
    }

    public static void setCoachmarksShownFlag(Context context, String screenName, boolean flag) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        if ("home".equals(screenName)) {
            _editor.putBoolean(COACHMARK_HOME, flag);
        } else if ("topics".equals(screenName)) {
            _editor.putBoolean(COACHMARK_TOPICS, flag);
        } else if ("topics_article".equals(screenName)) {
            _editor.putBoolean(COACHMARK_TOPICS_ARTICLE, flag);
        } else if ("article_details".equals(screenName)) {
            _editor.putBoolean(COACHMARK_ARTICLE_DETAILS, flag);
        } else if ("groups".equals(screenName)) {
            _editor.putBoolean(COACHMARK_GROUP, flag);
        } else if ("Drawer".equals(screenName)) {
            _editor.putBoolean(COACHMARK_DRAWER, flag);
        } else if ("HomeScreen".equals(screenName)) {
            _editor.putBoolean(COACHMARK_HOME_SCREEN, flag);
        } else if ("Profile".equals(screenName)) {
            _editor.putBoolean(COACHMARK_PROFILE, flag);
        } else if ("Mom_vlog".equals(screenName)) {
            _editor.putBoolean(COACHMARK_MOMVLOG, flag);

        } else if ("storyCoachmark".equals(screenName)) {
            _editor.putBoolean(COACHMARK_STORY, flag);
        }
        _editor.commit();
    }

    public static boolean isCoachmarksShownFlag(Context context, String screenName) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        if ("home".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_HOME, false);
        } else if ("topics".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_TOPICS, false);
        } else if ("topics_article".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_TOPICS_ARTICLE, false);
        } else if ("article_details".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_ARTICLE_DETAILS, false);
        } else if ("groups".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_GROUP, false);
        } else if ("Drawer".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_DRAWER, false);
        } else if ("HomeScreen".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_HOME_SCREEN, false);
        } else if ("Profile".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_PROFILE, false);
        } else if ("Mom_vlog".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_MOMVLOG, false);
        } else if ("storyCoachmark".equals(screenName)) {
            return _sharedPref.getBoolean(COACHMARK_STORY, false);
        }
        return true;
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

    public static void setLanguageFilters(Context pContext, String languageFilters) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(LANGUAGE_FILTER, languageFilters);
        _editor.commit();
    }

    public static String getLanguageFilters(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getString(LANGUAGE_FILTER, "");
    }

    public static void setFollowedTopicsCount(Context pContext, int topicCount) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putInt(FOLLOWED_TOPIC_COUNT, topicCount);
        _editor.commit();
    }

    public static int getFollowedTopicsCount(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getInt(FOLLOWED_TOPIC_COUNT, 0);
    }

    public static void setBecomeBloggerFlag(Context pContext, boolean flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(BECOME_BLOGGER_FLAG, flag);
        _editor.commit();
    }

    public static boolean getBecomeBloggerFlag(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(BECOME_BLOGGER_FLAG, false));
        return flag;
    }

    public static void setDemoVideoSeen(Context pContext, boolean flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(DEMO_VIDEO_SEEN, flag);
        _editor.commit();
    }

    public static boolean getDemoVideoSeen(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(DEMO_VIDEO_SEEN, false));
        return flag;
    }

    public static void setAppLocale(Context pContext, String language) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(LOCALE_LANGUAGE_KEY, language);
        _editor.commit();
    }

    public static String getAppLocale(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        String language = (_sharedPref.getString(LOCALE_LANGUAGE_KEY, Locale.getDefault().getLanguage()));
        return language;
    }

    public static boolean isUserAnonymous(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(ANONYMOUS_FLAG, false));
        return flag;
    }

    public static void setUserAnonymous(Context pContext, boolean flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(ANONYMOUS_FLAG, flag);
        _editor.commit();
    }

    public static void setLastNotificationIdForUnreadFlag(Context pContext, String flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(NOTIFICATION_CENTER_FLAG, flag);
        _editor.commit();
    }

    public static String getLastNotificationIdForUnreadFlag(Context context) {
        SharedPreferences _sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        String language = (_sharedPref.getString(NOTIFICATION_CENTER_FLAG, "0"));
        return language;
    }

    public static boolean getFollowTopicApproachChangeFlag(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(FOLLOW_TOPIC_APPROACH_FLAG, false));
        return flag;
    }

    public static void setFollowTopicApproachChangeFlag(Context pContext, boolean flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(FOLLOW_TOPIC_APPROACH_FLAG, flag);
        _editor.commit();
    }

    public static long getLastLoginTimestamp(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        long flag = (_sharedPref.getLong(LAST_LOGIN_TIMESTAMP, 0));
        return flag;
    }

    public static void setLastLoginTimestamp(Context pContext, long timestamp) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putLong(LAST_LOGIN_TIMESTAMP, timestamp);
        _editor.commit();
    }

    public static boolean getUserSkippedFollowTopicFlag(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(USER_SKIPPED_FOLLOW_TOPIC_FLAG, false));
        return flag;
    }

    public static void setUserSkippedFollowTopicFlag(Context pContext, boolean flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(USER_SKIPPED_FOLLOW_TOPIC_FLAG, flag);
        _editor.commit();
    }

    public static boolean isTopicSelectionChanged(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (_sharedPref.getBoolean(HAS_TOPIC_SELECTION_CHANGED, false));
        return flag;
    }

    public static void setTopicSelectionChanged(Context pContext, boolean flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(HAS_TOPIC_SELECTION_CHANGED, flag);
        _editor.commit();
    }

    public static String getSavedPostData(Context pContext, int groupId) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (_sharedPref.getString("groupId-" + groupId, ""));
    }

    public static void setSavedPostData(Context pContext, int groupId, String content) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString("groupId-" + groupId, content);
        _editor.commit();
    }

    public static void clearSavedPostData(Context pContext, int groupId) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.remove("groupId-" + groupId);
        _editor.commit();
    }

    public static String getSavedReplyData(Context pContext, int groupId, int postId, int parentId) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (_sharedPref.getString("groupId-" + groupId + "~" + "postId-" + postId + "~" + "commentId" + parentId, ""));
    }

    public static void setSavedReplyData(Context pContext, int groupId, int postId, int parentId, String content) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString("groupId-" + groupId + "~" + "postId-" + postId + "~" + "commentId" + parentId, content);
        _editor.commit();
    }

    public static void clearSavedReplyData(Context pContext, int groupId, int postId, int parentId) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.remove("groupId-" + groupId + "~" + "postId-" + postId + "~" + "commentId" + parentId);
        _editor.commit();
    }

    public static void myMoneyCoachMark(Context pContext, int count) {
        SharedPreferences _sharedPref1 = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref1.edit();
        _editor.putInt("count", count);
        _editor.apply();
    }

    public static int getMyMoney(Context pContext) {
        SharedPreferences _sharedPref1 = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (_sharedPref1.getInt("count", 0));
    }

    public static void setFrequencyForShowingAppUpdate(Context pContext, int versionCode) {
        SharedPreferences _sharedPref1 = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref1.edit();
        _editor.putInt("count", versionCode);
        _editor.apply();
    }

    public static int getFrequencyForShowingUpdateApp(Context pContext) {
        SharedPreferences _sharedPref1 = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (_sharedPref1.getInt("count", 0));
    }

    public static void setFirebaseRemoteConfigUpdateFlag(Context pContext, boolean b) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(FIREBASE_REMOTE_CONFIG_UPDATE_FLAG, b);
        _editor.apply();
    }

    public static boolean getFirebaseRemoteConfigUpdateFlag(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return _sharedPref.getBoolean(FIREBASE_REMOTE_CONFIG_UPDATE_FLAG, false);
    }

    public static boolean isDefaultCampaignShown(Context pContext) {
        SharedPreferences _sharedPref1 = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (_sharedPref1.getBoolean(DEFAULT_CAMPAIGN_SHOWN_FLAG, false));
    }

    public static void setDefaultCampaignShownFlag(Context pContext, boolean flag) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putBoolean(DEFAULT_CAMPAIGN_SHOWN_FLAG, flag);
        _editor.commit();
    }

    public static String getHomeAdSlotUrl(Context pContext) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (_sharedPref.getString(HOME_AD_SLOT_URL, ""));
    }

    public static void setHomeAdSlotUrl(Context pContext, String adSlotUrl) {
        SharedPreferences _sharedPref = pContext.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor _editor = _sharedPref.edit();
        _editor.putString(HOME_AD_SLOT_URL, adSlotUrl);
        _editor.commit();
    }
}
