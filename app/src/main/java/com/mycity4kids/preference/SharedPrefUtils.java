package com.mycity4kids.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.version.RateVersion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;

public class SharedPrefUtils {

    private SharedPrefUtils() {
    }

    private static final String COMMON_PREF_FILE = "my_city_prefs";

    private static final String CONFIG_CATEGORY_VERSION = "configCategoryVersion";
    private static final String POPULAR_CONFIG_CATEGORY_VERSION = "popularConfigCategoryVersion";

    private static final String SELECTED_CITY_ID = "cityId";
    private static final String SELECTED_CITY_NAME = "city_name";
    private static final String SELECTED_NEW_CITY_ID = "newCityId";

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
    private static final String LOGIN_MODE = "loginMode";
    private static final String USER_HANDLE = "userHandle";
    private static final String REQUEST_MEDIUM = "requestMedium";
    private static final String EMAIL_VALIDATED = "emailValidated";

    private static final String DEVICE_TOKEN = "device_token";
    private static final String LOGOUT_FLAG = "logout_flag";

    private static final String COACHMARK_TOPICS = "coachmarkTopics";
    private static final String COACHMARK_TOPICS_ARTICLE = "coachmarkTopicsArticle";
    private static final String COACHMARK_ARTICLE_DETAILS = "coachmarkArticleDetails";
    private static final String COACHMARK_DRAWER = "coachmarkDrawer";
    private static final String COACHMARK_STORY = "coachmarkStory";
    private static final String COACH_MARK_CHOOSE_STORY_CHALLENGE = "chooseStoryOrChallenge";
    private static final String COACH_MARK_CHOOSE_ARTICLE_CHALLENGE = "articleChallengeSelectionScreenCoachMark";
    private static final String COACH_MARK_CHOOSE_VIDEO_CHALLENGE = "videoOrChallengeSelectionScreen";
    private static final String COACH_MARK_EDITOR_BOTTOM = "newEditor_bottom";
    private static final String COACHMARK_ARTICLE_PUBLISH = "articleEditorPublish";
    private static final String COACH_MARK_ADD_ARTICLE_TAG_IMAGE = "addArticleTagImageScreen";
    private static final String COACH_MARK_ADD_ARTICLE_TOPICS = "addArticleTopicScreen";
    private static final String COACH_MARK_CAMPAIGN_LIST = "campaignList";
    private static final String COACH_MARK_DASHBOARD_CONTENT_FILTER = "dashBoardContentFilterScreen";
    private static final String COACHMARK_VIDEOS_TRIMMER = "videoTrimmer";
    private static final String COACHMARK_VIDEOS_TITLE_TAGS = "videoTitleAndTags";
    private static final String COACHMARK_TAGGING = "coachmarkTagging";
    private static final String COACHMARK_TOPCOMMENT = "coachmarkTopComment";
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
    private static final String NOTIFICATION_CENTER_VISIT_TIMESTAMP = "notificationCenterVisitTimestamp";
    private static final String FOLLOW_TOPIC_APPROACH_FLAG = "followTopicApproachFlag";
    private static final String LAST_LOGIN_TIMESTAMP = "lastLoginTimestamp";
    private static final String USER_SKIPPED_FOLLOW_TOPIC_FLAG = "userSkippedFollowTopicFlag";
    private static final String HAS_TOPIC_SELECTION_CHANGED = "topicSelectionChangeFlag";
    private static final String FIREBASE_REMOTE_CONFIG_UPDATE_FLAG = "firebaseRemoteConfigUpdateFlag";
    private static final String DEMO_VIDEO_SEEN = "demovideoseen";

    private static final String DEFAULT_CAMPAIGN_SHOWN_FLAG = "defaultcampaignshownflag";

    private static final String HOME_AD_SLOT_URL = "homeAdSlotUrl";
    private static final String ADVERTISEMENT_ID = "advertisementId";
    private static final String IP_ADDRESS = "ipAddress";
    private static final String FOLLOWING_JSON = "followingJson";
    private static final String USER_JOURNEY_COMPLETED_FLAG = "userJourneyCompletedFlag";

    public static void clearPrefrence(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.clear().commit();
    }

    public static void setCurrentCityModel(Context context, MetroCity model) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putInt(SELECTED_CITY_ID, model.getId());
        editor.putString(SELECTED_CITY_NAME, model.getName());
        editor.putString(SELECTED_NEW_CITY_ID, model.getNewCityId());
        editor.commit();
    }

    public static MetroCity getCurrentCityModel(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        MetroCity city = new MetroCity();
        city.setId(sharedPref.getInt(SELECTED_CITY_ID, 11));
        city.setName(sharedPref.getString(SELECTED_CITY_NAME, "Others"));
        city.setNewCityId(sharedPref.getString(SELECTED_NEW_CITY_ID, "city-11"));
        return city;
    }

    public static void setConfigCategoryVersion(Context context, int id) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putInt(CONFIG_CATEGORY_VERSION, id);
        editor.commit();
    }

    public static int getConfigCategoryVersion(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        int id = 0;
        id = (sharedPref.getInt(CONFIG_CATEGORY_VERSION, 0));
        return id;
    }

    public static boolean getLogoutFlag(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (sharedPref.getBoolean(LOGOUT_FLAG, false));
        return flag;
    }

    public static void setLogoutFlag(Context context, boolean flag) {
        // true means today screen
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(LOGOUT_FLAG, flag);
        editor.commit();
    }

    // set userdeatil in prefrences model
    public static void setUserDetailModel(Context context, UserInfo model) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(USER_ID, model.getId());
        editor.putString(DYNAMO_USER_ID, model.getDynamoId());
        editor.putString(EMAIL, model.getEmail());
        editor.putString(MOBILE, model.getMobile_number());
        editor.putString(COLOR_CODE, model.getColor_code());
        editor.putInt(FAMILY_ID, model.getFamily_id());
        editor.putString(USER_NAME, model.getFirst_name() + model.getLast_name());
        editor.putString(FIRST_NAME, model.getFirst_name());
        editor.putString(LAST_NAME, model.getLast_name());
        editor.putString(SESSIONID, model.getSessionId());
        editor.putString(MC4KTOKEN, model.getMc4kToken());
        editor.putString(IS_USER_VALIDATED, model.getIsValidated());
        editor.putString(USER_CITY_ID, model.getCityId());
        editor.putString(USER_LANG_SELECTION, model.getIsLangSelection());
        editor.putString(SUBSCRIPTION_EMAIL, model.getSubscriptionEmail());
        editor.putString(USER_TYPE, model.getUserType());
        editor.putString(GENDER, model.getGender());
        editor.putString(NEW_USER_FLAG, model.getIsNewUser());
        editor.putString(LOGIN_MODE, model.getLoginMode());
        editor.putString(USER_HANDLE, model.getUserHandle());
        editor.putString(REQUEST_MEDIUM, model.getRequestMedium());
        editor.putString(EMAIL_VALIDATED, model.getEmailValidated());
        Gson gson = new Gson();
        String jsonLangs = gson.toJson(model.getVideoPreferredLanguages());
        editor.putString("languages", jsonLangs);

        editor.commit();
    }

    public static UserInfo getUserDetailModel(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        UserInfo user = new UserInfo();
        // Changed the userId from int to String when backend changed from sql to dyanoma.
        try {
            user.setId(sharedPref.getString(USER_ID, "0"));
        } catch (ClassCastException cce) {
            user.setId("" + (sharedPref.getInt(USER_ID, 0)));
        }
        user.setDynamoId(sharedPref.getString(DYNAMO_USER_ID, "0"));
        user.setFamily_id(sharedPref.getInt(FAMILY_ID, 0));
        user.setEmail(sharedPref.getString(EMAIL, ""));
        user.setMobile_number(sharedPref.getString(MOBILE, ""));
        user.setColor_code(sharedPref.getString(COLOR_CODE, ""));
        user.setFirst_name(sharedPref.getString(FIRST_NAME, ""));
        user.setLast_name(sharedPref.getString(LAST_NAME, ""));
        user.setSessionId(sharedPref.getString(SESSIONID, ""));
        user.setMc4kToken(sharedPref.getString(MC4KTOKEN, ""));
        user.setIsValidated(sharedPref.getString(IS_USER_VALIDATED, ""));
        user.setCityId(sharedPref.getString(USER_CITY_ID, "11"));
        user.setIsLangSelection(sharedPref.getString(USER_LANG_SELECTION, "0"));
        user.setSubscriptionEmail(sharedPref.getString(SUBSCRIPTION_EMAIL, sharedPref.getString(EMAIL, "")));
        user.setUserType(sharedPref.getString(USER_TYPE, "0"));
        user.setGender(sharedPref.getString(GENDER, "0"));
        user.setIsNewUser(sharedPref.getString(NEW_USER_FLAG, "0"));
        user.setLoginMode(sharedPref.getString(LOGIN_MODE, ""));
        user.setUserHandle(sharedPref.getString(USER_HANDLE, ""));
        user.setRequestMedium(sharedPref.getString(REQUEST_MEDIUM, ""));
        user.setEmailValidated(sharedPref.getString(EMAIL_VALIDATED, "0"));

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(sharedPref.getString("languages", ""));
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
            user.setVideoPreferredLanguages(list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static void setProfileImgUrl(Context context, String imgUrl) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(PROFILE_IMAGE_URL, imgUrl);
        editor.commit();
    }

    public static String getProfileImgUrl(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        String profileImgUrl = sharedPref.getString(PROFILE_IMAGE_URL, "");
        return profileImgUrl;
    }

    public static void setAppRateVersion(Context context, RateVersion rateVersion) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putInt(APP_RATE, rateVersion.getAppRateVersion());
        editor.putBoolean(IS_APP_RATE_COMPLETE, rateVersion.isAppRateComplete());
        editor.commit();
    }

    public static RateVersion getRateVersion(Context context) {
        RateVersion rateVersion = new RateVersion();
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        rateVersion.setAppRateVersion(sharedPref.getInt(APP_RATE, -7));
        rateVersion.setAppRateComplete(sharedPref.getBoolean(IS_APP_RATE_COMPLETE, false));
        return rateVersion;
    }

    /**
     * this flag comes from server in config api.if this flag is true then we will show dialog for App upgrade. This
     * flag i am using in HomeCategoryActivity
     */
    public static void setAppUgrade(Context context, boolean isAppWantsUpgrade) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(IS_APP_WANT_UPGRADE, isAppWantsUpgrade);
        editor.commit();
    }

    public static boolean getAppUpgrade(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(IS_APP_WANT_UPGRADE, false);
    }

    public static void setIsRewardsAdded(Context context, String isRewardAdded) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(IS_REWARDS_ADDED, isRewardAdded);
        editor.commit();
    }

    public static String getIsRewardsAdded(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(IS_REWARDS_ADDED, "");
    }

    public static String getAppUgradeMessage(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(APP_UPGRADE_MESSAGE, "Please update your app to continue");
    }

    public static void setAppUgradeMessage(Context context, String appUpgradeMessage) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(APP_UPGRADE_MESSAGE, appUpgradeMessage);
        editor.commit();
    }

    public static void setDeviceToken(Context context, String deviceToken) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(DEVICE_TOKEN, deviceToken);
        editor.commit();
    }

    public static String getDeviceToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(DEVICE_TOKEN, "");
    }

    public static void setBaseUrl(Context context, String baseurl) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString("BASE_URL", baseurl);
        editor.commit();
    }

    public static String getBaseUrl(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString("BASE_URL", AppConstants.LIVE_URL);
    }

    public static void setCoachmarksShownFlag(Context context, String screenName, boolean flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        if ("topics".equals(screenName)) {
            editor.putBoolean(COACHMARK_TOPICS, flag);
        } else if ("topics_article".equals(screenName)) {
            editor.putBoolean(COACHMARK_TOPICS_ARTICLE, flag);
        } else if ("article_details".equals(screenName)) {
            editor.putBoolean(COACHMARK_ARTICLE_DETAILS, flag);
        } else if ("Drawer".equals(screenName)) {
            editor.putBoolean(COACHMARK_DRAWER, flag);
        } else if ("storyCoachmark".equals(screenName)) {
            editor.putBoolean(COACHMARK_STORY, flag);
        } else if ("taggingCoachmark".equals(screenName)) {
            editor.putBoolean(COACHMARK_TAGGING, flag);
        } else if ("topCommentCoachMark".equals(screenName)) {
            editor.putBoolean(COACHMARK_TOPCOMMENT, flag);
        } else if ("chooseStoryOrChallenge".equals(screenName)) {
            editor.putBoolean(COACH_MARK_CHOOSE_STORY_CHALLENGE, flag);
        } else if ("articleChallengeSelectionScreenCoachMark".equals(screenName)) {
            editor.putBoolean(COACH_MARK_CHOOSE_ARTICLE_CHALLENGE, flag);
        } else if ("videoOrChallengeSelectionScreen".equals(screenName)) {
            editor.putBoolean(COACH_MARK_CHOOSE_VIDEO_CHALLENGE, flag);
        } else if ("newEditor_bottom".equals(screenName)) {
            editor.putBoolean(COACH_MARK_EDITOR_BOTTOM, flag);
        } else if ("articleEditorPublish".equals(screenName)) {
            editor.putBoolean(COACHMARK_ARTICLE_PUBLISH, flag);
        } else if ("addArticleTagImageScreen".equals(screenName)) {
            editor.putBoolean(COACH_MARK_ADD_ARTICLE_TAG_IMAGE, flag);
        } else if ("addArticleTopicScreen".equals(screenName)) {
            editor.putBoolean(COACH_MARK_ADD_ARTICLE_TOPICS, flag);
        } else if ("campaignList".equals(screenName)) {
            editor.putBoolean(COACH_MARK_CAMPAIGN_LIST, flag);
        } else if ("dashBoardContentFilterScreen".equals(screenName)) {
            editor.putBoolean(COACH_MARK_DASHBOARD_CONTENT_FILTER, flag);
        } else if ("videoTrimmer".equals(screenName)) {
            editor.putBoolean(COACHMARK_VIDEOS_TRIMMER, flag);
        } else if ("videoTitleAndTags".equals(screenName)) {
            editor.putBoolean(COACHMARK_VIDEOS_TITLE_TAGS, flag);
        }
        editor.commit();
    }

    public static boolean isCoachmarksShownFlag(Context context, String screenName) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        if ("topics".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_TOPICS, false);
        } else if ("topics_article".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_TOPICS_ARTICLE, false);
        } else if ("article_details".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_ARTICLE_DETAILS, false);
        } else if ("Drawer".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_DRAWER, false);
        } else if ("storyCoachmark".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_STORY, false);
        } else if ("taggingCoachmark".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_TAGGING, false);
        } else if ("topCommentCoachMark".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_TOPCOMMENT, false);
        } else if ("chooseStoryOrChallenge".equals(screenName)) {
            return sharedPref.getBoolean(COACH_MARK_CHOOSE_STORY_CHALLENGE, false);
        } else if ("articleChallengeSelectionScreenCoachMark".equals(screenName)) {
            return sharedPref.getBoolean(COACH_MARK_CHOOSE_ARTICLE_CHALLENGE, false);
        } else if ("videoOrChallengeSelectionScreen".equals(screenName)) {
            return sharedPref.getBoolean(COACH_MARK_CHOOSE_VIDEO_CHALLENGE, false);
        } else if ("newEditor_bottom".equals(screenName)) {
            return sharedPref.getBoolean(COACH_MARK_EDITOR_BOTTOM, false);
        } else if ("articleEditorPublish".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_ARTICLE_PUBLISH, false);
        } else if ("addArticleTagImageScreen".equals(screenName)) {
            return sharedPref.getBoolean(COACH_MARK_ADD_ARTICLE_TAG_IMAGE, false);
        } else if ("addArticleTopicScreen".equals(screenName)) {
            return sharedPref.getBoolean(COACH_MARK_ADD_ARTICLE_TOPICS, false);
        } else if ("campaignList".equals(screenName)) {
            return sharedPref.getBoolean(COACH_MARK_CAMPAIGN_LIST, false);
        } else if ("dashBoardContentFilterScreen".equals(screenName)) {
            return sharedPref.getBoolean(COACH_MARK_DASHBOARD_CONTENT_FILTER, false);
        } else if ("videoTrimmer".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_VIDEOS_TRIMMER, false);
        } else if ("videoTitleAndTags".equals(screenName)) {
            return sharedPref.getBoolean(COACHMARK_VIDEOS_TITLE_TAGS, false);
        }
        return true;
    }

    public static void setConfigPopularCategoryVersion(Context context, int id) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putInt(POPULAR_CONFIG_CATEGORY_VERSION, id);
        editor.commit();
    }

    public static int getConfigPopularCategoryVersion(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        int id = 0;
        id = (sharedPref.getInt(POPULAR_CONFIG_CATEGORY_VERSION, 0));
        return id;
    }

    public static void setUserLocationLatitude(Context context, double latitude) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(LOCATION_LATITUDE, "" + latitude);
        editor.commit();
    }

    public static String getUserLocationLatitude(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(LOCATION_LATITUDE, "");
    }

    public static void setUserLocationLongitude(Context context, double longitude) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(LOCATION_LONGITUDE, "" + longitude);
        editor.commit();
    }

    public static String getUserLocationLongitude(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(LOCATION_LONGITUDE, "");
    }

    public static void setNotificationConfig(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getNotificationConfig(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static void setFacebookConnectedFlag(Context context, String isExpired) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(IS_FB_CONNECTED, isExpired);
        editor.commit();
    }

    public static String getFacebookConnectedFlag(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(IS_FB_CONNECTED, "");
    }

    public static void setLanguageFilters(Context context, String languageFilters) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(LANGUAGE_FILTER, languageFilters);
        editor.commit();
    }

    public static String getLanguageFilters(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_FILTER, "0");
    }

    public static void setFollowedTopicsCount(Context context, int topicCount) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putInt(FOLLOWED_TOPIC_COUNT, topicCount);
        editor.commit();
    }

    public static int getFollowedTopicsCount(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getInt(FOLLOWED_TOPIC_COUNT, 0);
    }

    public static void setBecomeBloggerFlag(Context context, boolean flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(BECOME_BLOGGER_FLAG, flag);
        editor.commit();
    }

    public static boolean getBecomeBloggerFlag(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (sharedPref.getBoolean(BECOME_BLOGGER_FLAG, false));
        return flag;
    }

    public static void setDemoVideoSeen(Context context, boolean flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(DEMO_VIDEO_SEEN, flag);
        editor.commit();
    }

    public static boolean getDemoVideoSeen(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (sharedPref.getBoolean(DEMO_VIDEO_SEEN, false));
        return flag;
    }

    public static void setAppLocale(Context context, String language) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(LOCALE_LANGUAGE_KEY, language);
        editor.commit();
    }

    public static String getAppLocale(Context context) {
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
            String language = Locale.getDefault().getLanguage();
            return language;
        } catch (Exception e) {
            return "en";
        }
    }

    public static boolean isUserAnonymous(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (sharedPref.getBoolean(ANONYMOUS_FLAG, false));
        return flag;
    }

    public static void setUserAnonymous(Context context, boolean flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(ANONYMOUS_FLAG, flag);
        editor.commit();
    }

    public static void setLastNotificationIdForUnreadFlag(Context context, String flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(NOTIFICATION_CENTER_FLAG, flag);
        editor.commit();
    }

    public static String getLastNotificationIdForUnreadFlag(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        String language = (sharedPref.getString(NOTIFICATION_CENTER_FLAG, "0"));
        return language;
    }

    public static void setNotificationCenterVisitTimestamp(Context context, long timestamp) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putLong(NOTIFICATION_CENTER_VISIT_TIMESTAMP, timestamp);
        editor.commit();
    }

    public static long getNotificationCenterVisitTimestamp(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        long timestamp = (sharedPref.getLong(NOTIFICATION_CENTER_VISIT_TIMESTAMP, 0L));
        return timestamp;
    }

    public static boolean getFollowTopicApproachChangeFlag(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (sharedPref.getBoolean(FOLLOW_TOPIC_APPROACH_FLAG, false));
        return flag;
    }

    public static void setFollowTopicApproachChangeFlag(Context context, boolean flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(FOLLOW_TOPIC_APPROACH_FLAG, flag);
        editor.commit();
    }

    public static long getLastLoginTimestamp(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        long flag = (sharedPref.getLong(LAST_LOGIN_TIMESTAMP, 0));
        return flag;
    }

    public static void setLastLoginTimestamp(Context context, long timestamp) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putLong(LAST_LOGIN_TIMESTAMP, timestamp);
        editor.commit();
    }

    public static boolean getUserSkippedFollowTopicFlag(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (sharedPref.getBoolean(USER_SKIPPED_FOLLOW_TOPIC_FLAG, false));
        return flag;
    }

    public static void setUserSkippedFollowTopicFlag(Context context, boolean flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(USER_SKIPPED_FOLLOW_TOPIC_FLAG, flag);
        editor.commit();
    }

    public static boolean isTopicSelectionChanged(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        boolean flag = (sharedPref.getBoolean(HAS_TOPIC_SELECTION_CHANGED, false));
        return flag;
    }

    public static void setTopicSelectionChanged(Context context, boolean flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(HAS_TOPIC_SELECTION_CHANGED, flag);
        editor.commit();
    }

    public static String getSavedPostData(Context context, int groupId) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getString("groupId-" + groupId, ""));
    }

    public static void setSavedPostData(Context context, int groupId, String content) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString("groupId-" + groupId, content);
        editor.commit();
    }

    public static void clearSavedPostData(Context context, int groupId) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.remove("groupId-" + groupId);
        editor.commit();
    }

    public static String getSavedReplyData(Context context, int groupId, int postId, int parentId) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref
                .getString("groupId-" + groupId + "~" + "postId-" + postId + "~" + "commentId" + parentId, ""));
    }

    public static void setSavedReplyData(Context context, int groupId, int postId, int parentId, String content) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString("groupId-" + groupId + "~" + "postId-" + postId + "~" + "commentId" + parentId, content);
        editor.commit();
    }

    public static void clearSavedReplyData(Context context, int groupId, int postId, int parentId) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.remove("groupId-" + groupId + "~" + "postId-" + postId + "~" + "commentId" + parentId);
        editor.commit();
    }

    public static void myMoneyCoachMark(Context context, int count) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putInt("count", count);
        editor.apply();
    }

    public static int getMyMoney(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getInt("count", 0));
    }

    public static void setFrequencyForShowingAppUpdate(Context context, int versionCode) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putInt("count", versionCode);
        editor.apply();
    }

    public static int getFrequencyForShowingUpdateApp(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getInt("count", 0));
    }

    public static void setFirebaseRemoteConfigUpdateFlag(Context context, boolean b) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(FIREBASE_REMOTE_CONFIG_UPDATE_FLAG, b);
        editor.apply();
    }

    public static boolean getFirebaseRemoteConfigUpdateFlag(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(FIREBASE_REMOTE_CONFIG_UPDATE_FLAG, false);
    }

    public static boolean isDefaultCampaignShown(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getBoolean(DEFAULT_CAMPAIGN_SHOWN_FLAG, false));
    }

    public static void setDefaultCampaignShownFlag(Context context, boolean flag) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean(DEFAULT_CAMPAIGN_SHOWN_FLAG, flag);
        editor.commit();
    }

    public static String getHomeAdSlotUrl(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getString(HOME_AD_SLOT_URL, ""));
    }

    public static void setHomeAdSlotUrl(Context context, String adSlotUrl) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(HOME_AD_SLOT_URL, adSlotUrl);
        editor.commit();
    }

    public static String getAdvertisementId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getString(ADVERTISEMENT_ID, ""));
    }

    public static void setAdvertisementId(Context context, String advertisementId) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(ADVERTISEMENT_ID, advertisementId);
        editor.commit();
    }

    public static Boolean getFollowClickCountInMomVlog(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getBoolean("isFiveClickDone", false));
    }

    public static void setFollowClickCountInMomVlog(Context context, Boolean isFiveClickDone) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean("isFiveClickDone", isFiveClickDone);
        editor.commit();
    }

    public static Boolean getOriginalContentBlogClick(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getBoolean("isBlogClickDone", false));
    }

    public static void setOriginalContentBlogClick(Context context, Boolean isFiveClickDone) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean("isBlogClickDone", isFiveClickDone);
        editor.commit();
    }

    public static Boolean getOriginalContentChallengeClick(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getBoolean("isChallengeClickDone", false));
    }

    public static void setOriginalContentChallengeClick(Context context, Boolean isFiveClickDone) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean("isChallengeClickDone", isFiveClickDone);
        editor.commit();
    }

    public static Boolean getOriginalContentStoryClick(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getBoolean("isStoryClickDone", false));
    }

    public static void setOriginalContentStoryClick(Context context, Boolean isFiveClickDone) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean("isStoryClickDone", isFiveClickDone);
        editor.commit();
    }

    public static Boolean getOriginalContentVideoClick(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getBoolean("isVideoClickDone", false));
    }

    public static void setOriginalContentVideoClick(Context context, Boolean isFiveClickDone) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putBoolean("isVideoClickDone", isFiveClickDone);
        editor.commit();
    }

    public static void setPublicIpAddress(Context context, String ipAddress) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(IP_ADDRESS, ipAddress);
        editor.commit();
    }

    public static String getPublicIpAddress(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return (sharedPref.getString(IP_ADDRESS, ""));
    }

    public static void setFollowingJson(Context context, String followingJson) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        editor.putString(FOLLOWING_JSON, followingJson);
        editor.commit();
    }

    public static Map<String, String> getFollowingJson(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Map<String, String> retMap = new Gson().fromJson(
                sharedPref.getString(FOLLOWING_JSON, "{}"), new TypeToken<HashMap<String, String>>() {
                }.getType()
        );
        return retMap;
    }

    public static void setCommentSuggestionsVisibilityFlag(Context context, Boolean flag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("flg", flag);
        editor.commit();

    }

    public static Boolean getCommentSuggestionsVisibilityFlag(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("flag", true);

    }

    public static void setUserJourneyCompletedFlag(Context context, Boolean flag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_JOURNEY_COMPLETED_FLAG, flag);
        editor.commit();
    }

    public static boolean isUserJourneyCompleted(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(USER_JOURNEY_COMPLETED_FLAG, false);
    }

    public static void setVlogSelectedLanguages(Context context, Boolean flag, ArrayList<String> selectedLang) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        List<String> vlogSelectedList;
        vlogSelectedList = selectedLang;
        String jsonLangs = gson.toJson(vlogSelectedList);
        editor.putString("languages", jsonLangs);
        editor.putBoolean("langSelected", flag);
        editor.commit();
    }

    public static String getSelectedVlogsLangs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString("languages", "");
    }

    public static Boolean getSelectedVlogsLangsFlag(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("langSelected", false);
    }
}
