package com.mycity4kids.constants;

import android.net.Uri;

import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * @author Deepanker Chaudhary
 */
public class AppConstants {


    public static final String DEV_URL = "http://54.169.17.138/";
    public static final String STAGING_URL = "http://52.77.116.39:8080/";
    public static final String LIVE_URL = "http://webserve.mycity4kids.com/";
//    public static final String LIVE_URL = "http://54.169.17.138/";
//    public static final String LIVE_URL = "http://10.42.0.1/";
//    public static final String LIVE_URL = "http://192.168.1.12/";

    public static final String PHOENIX_ARTICLE_STAGING_URL = "http://52.77.116.39:8082/";

    //Enable For Testing
    //public static final String LIVE_URL = SharedPrefUtils.getBaseURL(BaseApplication.getAppContext());

    public static final String BASE_URL = LIVE_URL;

    //    public static final String STAGING_INTERNAL_SERVER_URL = "http://59.162.46.199:8585/mycity4kids/";
    public static final String STAGING_INTERNAL_SERVER_URL = LIVE_URL;
    public static final String STAGING_CLIENT_SERVER_URL = LIVE_URL;

    public static final String STAGING_USERS_KEY = "apiusers";

    //public static final String LOGIN_URL				        = BASE_URL+"apiusers/login";
    public static final String REGISTRATION_URL = BASE_URL + "apiusers/registration";
    public static final String CATEGORY_URL = BASE_URL + "apiservices/category";
    public static final String LOCALITY_URL = BASE_URL + "apiservices/localities";
    public static final String GET_CITY_URL = BASE_URL + "apiservices/cities";
    // public static final String LOGOUT_URL = BASE_URL + "apiusers/logout";
    //public static final String FORGOT_URL     			        = BASE_URL+"apiusers/forgotpassword";
    //	public static final String WEBSERVICE_VERSION_URL	        = "apiservices/apiversion";
    public static final String CONFIGURATION_URL = BASE_URL + "apiservices/config?";
    public static final String BUSINESS_LISTING_URL = BASE_URL + "apilistings/lists?";
    public static final String AUTO_SUGGEST_URL = BASE_URL + "apisearches/suggest?";
    public static final String BUSINESS_SEARCH_URL = BASE_URL + "apisearches/search?";
    public static final String BUSINESS_SEARCH_URL_NEW = STAGING_CLIENT_SERVER_URL + "apisearches/search?";
    public static final String PARENTING_STOP_ARTICLE_URL = STAGING_CLIENT_SERVER_URL + "apiparentingstop/articles?";

    public static final String NEW_PARENTING_BLOG_URL = STAGING_CLIENT_SERVER_URL + "apiparentingstop/bloggers";
    public static final String NEW_PARENTING_BLOG_ARTICLE_LISTING_URL = STAGING_CLIENT_SERVER_URL + "apiparentingstop/";
    public static final String SHARE_BLOG_ARTICLE_URL = STAGING_CLIENT_SERVER_URL + "apiparentingstop/share_blog";


    public static final String MISSING_PLACE_URL = STAGING_CLIENT_SERVER_URL + "apilistings/";

    public static final String PARENTING_STOP_BLOGGER_URL = BASE_URL + "apiparentingstop/blogs?";
    public static final String BUSINESS_AND_EVENT_DETAILS_URL = BASE_URL + "apiservices/detail?";
    public static final String WRITE_A_REVIEW_AUTO_SUGGEST_URL = BASE_URL + "apireviews/autosuggest?";
    public static final String WRITE_A_REVIEW_URL = BASE_URL + "apireviews/writereview";
    public static final String FAVORITE_URL = BASE_URL + "apiusers/favourite";
    public static final String BEEN_THERE_URL = BASE_URL + "apiusers/beenthere";
    // public static final String IMAGE_UPLOAD_URL    			    = BASE_URL+"apiusers/imageupload";
    public static final String RECENTLY_VIEWED_URL = BASE_URL + "apiservices/recently_viewed?";
    public static final String UPLOAD_BUSINESS_IMAGE_URL = BASE_URL + "apireviews/uploadbusinessimages";
    public static final String PARENTING_STOP_TOP_PICKS_URL = BASE_URL + "apiparentingstop/top_pics?";
    public static final String ADD_A_LISTING_URL = BASE_URL + "apilistings/create?";
    public static final String REPORT_AN_ERROR_URL = BASE_URL + "apiservices/report?";
    public static final String GET_GOOGLE_COMMON_URL = "http://maps.googleapis.com/maps/api/directions/json";
    public static final String GET_GOOGLE_ADDRESS_URL = "http://maps.googleapis.com/maps/api/geocode/json";
    public static final String PARENTING_STOP_BLOGS_URL = BASE_URL + "apiparentingstop/blogs?";
    public static final String ARTICLES_BLOGS_DETAILS_URL = BASE_URL + "apiparentingstop/detail_article?";
    public static final String ARTICLES_BLOGS_DETAILS_URL_V1 = BASE_URL + "apiparentingstop/get_blog_detail?";
    public static final String ARTICLES_BLOGS_COMMENT_URL = BASE_URL + "apiparentingstop/get_comment?";

    public static final String ARTICLE_BLOG_SEARCH_FILTER_URL = BASE_URL + "apiparentingstop/filters_data?";
    public static final String PARENTING_STOP_SEARCH_URL = BASE_URL + "apiparentingstop/search?";
    public static final String VIEW_PROFILE_URL = BASE_URL + "apiusers/viewprofile";
    public static final String SAVE_PROFILE_URL = BASE_URL + "apiusers/profile";
    public static final String COMMENT_REPLY_URL = BASE_URL + "apiparentingstop/comment";
    public static final String ARTICLE_BLOG_FOLLOW_URL = BASE_URL + "apiusers/follow";
    public static final String BOOKMARK_BLOG_URL = BASE_URL + "apiparentingstop/add_or_remove_bookmark";
    public static final String BOOKMARK_RESOURCE_URL = BASE_URL + "apilistings/add_or_remove_bookmark";
    public static final String FETCH_BOOKMARK_URL = BASE_URL + "apiparentingstop/fav_blogs?";
    public static final String FETCH_RESOURCE_BOOKMARK_URL = BASE_URL + "apilistings/resourceBookmark?";

    public static final String PARENTING_FILTER_DATA_URL = STAGING_CLIENT_SERVER_URL + "apiparentingstop/filters_data?";
    public static final String PARENTING_LIST_DATA_FILTERED_URL = STAGING_CLIENT_SERVER_URL + "apiparentingstop/filters_data?";

    public static final String LOGIN_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/login/";
    public static final String SIGN_UP_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/registration/";
    public static final String CITY_BY_PINCODE_URL = STAGING_INTERNAL_SERVER_URL + "apiservices/get_city_id?";

    //public static final String LOGIN_URL = "http://192.168.13.55/mycity4kids/users/login";
    //public static final String SIGN_UP_URL = "http://192.168.13.55/mycity4kids/users/registration";

    public static final String FORGOT_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/forgotpassword";
    public static final String IMAGE_UPLOAD_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/familyimageupload";
    public static final String IMAGE_EDITOR_UPLOAD_URL = STAGING_INTERNAL_SERVER_URL + "apiblogs" + "/uploadImage";
    public static final String ARTICLE_DRAFT_URL = STAGING_INTERNAL_SERVER_URL + "apiblogs" + "/createUpdateDraft";
    public static final String ARTICLE_DRAFT_LIST_URL = STAGING_INTERNAL_SERVER_URL + "apiblogs" + "/getDraftLists";
    public static final String ARTICLE_PUBLISH_URL = STAGING_INTERNAL_SERVER_URL + "apiblogs" + "/createUpdateArticle";
    public static final String BLOG_SETUP_URL = STAGING_INTERNAL_SERVER_URL + "apiblogs" + "/createUpdateBlogPage";
    public static final String BLOG_DATA_URL = STAGING_INTERNAL_SERVER_URL + "apiblogs" + "/getBlogPage";
    public static final String CREATE_APPOINTMENT_URL = STAGING_INTERNAL_SERVER_URL + "apiappointments/add/";
    public static final String EDIT_APPOINTMENT_URL = STAGING_INTERNAL_SERVER_URL + "apiappointments/add/";
    public static final String DELETE_APPOINTMENT_URL = STAGING_INTERNAL_SERVER_URL + "apiappointments/delete/";


    public static final String CREATE_TASK_URL = STAGING_INTERNAL_SERVER_URL + "apitasks/add/";
    public static final String EDIT_TASK_URL = STAGING_INTERNAL_SERVER_URL + "apitasks/add/";
    public static final String DELETE_TASK_URL = STAGING_INTERNAL_SERVER_URL + "apitasks/delete/";
    public static final String DELETE_TASK_LIST_URL = STAGING_INTERNAL_SERVER_URL + "apitasks/delete_list";
//    public static final String DELETE_SELECTED_TASK_URL = LOCAL_URL_2 + "apitasks/delete/";
//

    public static final String CREATE_TASKLIST_URL = STAGING_INTERNAL_SERVER_URL + "apitasks/create_list/";

    public static final String LOGOUT_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/logout/";
    public static final String EDIT_FAMILYPROFILE_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/edit_family/";
    public static final String EDIT_KIDPROFILE_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/edit_kidsinformation/";
    public static final String EDIT_ADULTPROFILE_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/edit_user/";
    public static final String GET_APPOITMENT_URL = STAGING_INTERNAL_SERVER_URL + "apiappointments/index/";
    public static final String GET_TASK_URL = STAGING_INTERNAL_SERVER_URL + "apitasks/index/";
    public static final String ADD_ADDTIONAL_KIDUSER_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/save_aditional_user_and_kids/";
    public static final String ADD_NOTES_URL = STAGING_INTERNAL_SERVER_URL + "apiappointments/add_notes/";
    public static final String FILE_UPLOAD_URL = STAGING_INTERNAL_SERVER_URL + "apiappointments/appointment_files";
    public static final String FILE_UPLOAD_URL_TASK = STAGING_INTERNAL_SERVER_URL + "apitasks/task_files/";
    public static final String ADD_TASK_NOTES_URL = STAGING_INTERNAL_SERVER_URL + "apitasks/add_notes/";

    public static final String NEW_ALL_ARTICLE_URL = STAGING_CLIENT_SERVER_URL + "apiparentingstop/articles?";

    public static final String PARENTING_NEW_TOP_PICKS_URL = STAGING_CLIENT_SERVER_URL + "apiparentingstop/search?";
    public static final String BUSINESSLISTINGURLTMP = STAGING_CLIENT_SERVER_URL + "apilistings/lists?";

    public static final String TASK_COMPLETE_URL = STAGING_INTERNAL_SERVER_URL + "apitasks/tasks_exclude_dates";

    public static final String TODAY_EVENTS_URL = STAGING_CLIENT_SERVER_URL + "apilistings/lists?";

    public static final String GET_SYNC_USER_INFO_URL = STAGING_CLIENT_SERVER_URL + "apiusers/get_users_details_refresh/";


    public static final String GET_EMAIL_APPOINTMENT_NOTIFICATION_URL = STAGING_CLIENT_SERVER_URL + "apiappointments/send_appointment_email_from_device_to_cron/appointment_id:";
    public static final String GET_EMAIL_TASK_NOTIFICATION_URL = STAGING_CLIENT_SERVER_URL + "apitasks/send_task_email_from_device_to_cron/task_id:";

    public static final String NOTIFICATION_URL = STAGING_INTERNAL_SERVER_URL + "apiusers/update_notifications/";
    public static final String DELETE_APPOINTMENT_IMAGE_URL = STAGING_CLIENT_SERVER_URL + "apiappointments/delete_file/";

    public static final String DELETE_TASKS_IMAGE_URL = STAGING_CLIENT_SERVER_URL + "apitasks/delete_file/";

    public static final String PUSH_TOKEN_URL = STAGING_CLIENT_SERVER_URL + "apiservices/push_token?";

    public static final String DEEP_LINKING_URL = BASE_URL + "apicommon/get_url_data?";

    public static final Uri APP_BASE_URI = Uri.parse("android-app://com.mycity4kids/http/www.mycity4kids.com/");
    public static final Uri WEB_BASE_URL = Uri.parse("http://www.mycity4kids.com/");


    /*New Login/Sign Up Constants*/
    public static final String SMS_ORIGIN = "MCKOTP";
    public static final int NEW_SIGNUP_REQUEST = 1005;
    public static final int VERIFY_OTP_REQUEST = 1006;
    public static final int NEW_LOGIN_REQUEST = 1007;
    public static final int ACCEPT_OR_REJECT_INVITE_REQUEST = 1008;
    public static final int CREATE_FAMILY_REQUEST = 1009;
    public static final int UPDATE_MOBILE_FOR_EXISTING_USER_REQUEST = 1010;
    public static final int CONFIRM_MOBILE_OTP_FOR_EXISTING_USERS_REQUEST = 1011;
    public static final int DELETE_INVITE = 1012;
    public static final String VERIFY_OTP_URL = BASE_URL + STAGING_USERS_KEY + "/checkOtpV1";
    public static final String NEW_SIGN_UP_URL = BASE_URL + STAGING_USERS_KEY + "/registrationV1/";
    public static final String NEW_LOGIN_URL = BASE_URL + STAGING_USERS_KEY + "/loginV2/";
    public static final String CREATE_FAMILY_URL = BASE_URL + STAGING_USERS_KEY + "/createFamilyV2/";
    public static final String ACCEPT_OR_REJECT_INVITE_URL = BASE_URL + STAGING_USERS_KEY + "/acceptInviteV1/";
    public static final String UPDATE_MOBILE_FOR_EXISTING_USERS_URL = BASE_URL + STAGING_USERS_KEY + "/updateMobileV1/";
    public static final String CONFIRM_MOBILE_OTP_FOR_EXISTING_USERS_URL = BASE_URL + STAGING_USERS_KEY + "/confirmUpdateMobileOtpV1/";
    public static final String NEW_LOGOUT_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/logoutV1/";
    public static final String UPDATE_PUSH_TOKEN_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/updatePushTokenV1?";
    public static final String ADD_ADULT_AND_KIDS_URL = STAGING_INTERNAL_SERVER_URL + STAGING_USERS_KEY + "/addAdultAndKidsV1/";

    //New Text Editor and Blogs Changes APIs
    public static final String GET_BLOGGER_DASHBOARD_URL = BASE_URL + "apiblogs/bloggerDashboardData?";
    public static final String GET_BLOGGER_PUBLISHED_ARTICLES_URL = BASE_URL + "apiblogs/publishedArticle?";

    //Editor's Pick API
    public static final String GET_EDITOR_ARTICLES_URL = BASE_URL + "apiparentingstop/editorPickArticle?";

    //Search bloggers/authors
    public static final String SEARCH_AUTHORS_URL = BASE_URL + "apiparentingstop/searchWithBlogger?";

    //Force_update
    public static final String FORCE_UPDATE_URL = BASE_URL + "apiservices/forceUpdate?";

    //Force_update
    public static final String EDITOR_PICKS_ARTICLES = BASE_URL + "apiparentingstop/editorPickArticle?";

    //Comments Lazy loading
    public static final int COMMENT_LIMIT = 10;
    public static final String COMMENT_TYPE_DB = "db";
    public static final String COMMENT_TYPE_FB_PLUGIN = "fb";
    public static final String COMMENT_TYPE_FB_PAGE = "fan";

    public static final int LOGIN_REQUEST = 0;
    public static final int GOOGLE_REQUEST = 1;
    public static final int FACEBOOK_REQUEST = 2;
    public static final int REGISTRATION_REQUEST = 3;
    public static final int CATEGORY_REQUEST = 4;
    public static final int LOCALITES_REQUEST = 5;
    public static final int GET_CITY_REQUEST = 6;
    public static final int LOGOUT_REQUEST = 7;
    public static final int FORGOT_REQUEST = 8;
    public static final int WEBSERVICE_VERSION_REQUEST = 9;
    public static final int CONFIGURATION_REQUEST = 10;
    public static final int LOCATION_SEARCH_REQUEST = 11;
    public static final int BUSINESS_LIST_REQUEST = 12;
    public static final int AUTO_SUGGEST_REQUEST = 13;
    public static final int BUSINESS_AUTO_SUGGEST_REQUEST = 133;
    public static final int BUSINESS_SEARCH_LISTING_REQUEST = 14;
    public static final int BUSINESS_SEARCH_LISTING_REQUESTNEW = 100;
    public static final int BUSINESS_SEARCH_LISTING_REQUESTEVENTNEW = 105;
    public static final int PARENTING_STOP_ARTICLES_REQUEST = 15;
    public static final int PARENTING_STOP_BLOGGER_REQUEST = 16;
    public static final int BUSINESS_AND_EVENT_DETAILS_REQUEST = 17;
    public static final int WRITE_A_REVIEW_AUTO_SUGGEST_REQUEST = 18;
    public static final int FAVORITE_REQUEST = 19;
    public static final int BEEN_THERE_REQUEST = 20;
    public static final int WRITE_A_REVIEW_REQUEST = 21;
    public static final int IMAGE_UPLOAD_REQUEST = 22;
    public static final int IMAGE_EDITOR_UPLOAD_REQUEST = 222;
    public static final int RECENTLY_VIEWED_REQUEST = 23;
    public static final int UPLOAD_BUSINESS_IMAGE_REQUEST = 24;
    public static final int TOP_PICKS_REQUEST = 25;
    public static final int ADD_A_LISTING_REQUEST = 26;
    public static final int REPORT_AN_ERROR_REQUEST = 27;
    public static final int PARENTING_STOP_BLOGS_REQUEST = 28;
    public static final int ARTICLES_DETAILS_REQUEST = 29;
    public static final int BLOGS_DETAILS_REQUEST = 30;
    public static final int ARTICLE_SEARCH_FILTER_REQUEST = 31;
    public static final int BLOG_SEARCH_FILTER_REQUEST = 32;
    public static final int PARENTING_STOP_SEARCH_REQUEST = 33;
    public static final int VIEW_PROFILE_REQUEST = 34;
    public static final int SAVE_PROFILE_REQUEST = 35;
    public static final int LOCATION_MY_PROFILE_REQUEST = 36;
    public static final int COMMENT_REPLY_REQUEST = 37;
    public static final int ARTICLE_BLOG_FOLLOW_REQUEST = 38;

    public static final int SIGNUP_REQUEST = 39;

    public static final int CREATE_APPOINTEMT_REQUEST = 40;
    public static final int DELETE_APPOINTEMT_REQUEST = 41;
    public static final int EDIT_APPOINTEMT_REQUEST = 42;


    public static final int EDIT_KIDPROFILE_REQUEST = 43;
    public static final int EDIT_ADULTPROFILE_REQUEST = 44;
    public static final int EDIT_FAMILY_REQUEST = 45;
    public static final int GET_ALL_APPOINTMNET_REQ = 46;
    public static final int ADD_ADDITIONAL_USERKID_REQ = 47;
    public static final int ADD_NOTES_REQ = 48;
    public static final int FILE_UPLOAD_REQ = 49;

    public static final int CREATE_TASK_REQUEST = 50;
    public static final int DELETE_TASK_REQUEST = 51;
    public static final int EDIT_TASK_REQUEST = 52;


    public static final int CREATE_TASKLIST_REQUEST = 53;
    public static final int GET_ALL_TASK_REQ = 54;
    public static final int DELETE_LIST_REQUEST = 55;
    public static final int FILE_UPLOAD_REQ_TASK = 56;
    public static final int ADD_TASK_NOTES_REQ = 57;
    public static final int NEW_ALL_ARTICLES_REQUEST = 58;

    public static final int TASKS_COMPLETE_REQUEST = 59;


    public static final int PARENTING_FILTER_LIST = 59;

    public static final int PARENTING_LIST_DATA_FILTERED = 60;

    public static final int PARRENTING_BLOG_DATA = 61;
    public static final int PARRENTING_BLOG_SORT_DATA = 62;
    public static final int PARRENTING_BLOG_ARTICLE_LISTING = 63;
    public static final int PARRENTING_BLOG_ARTICLE_LISTING_PAGINATION = 64;

    public static final int TODAY_EVENTS_REQUEST = 65;

    public static final int MISSING_PLACE_REQUEST = 66;
    public static final int SYNC_USER_INFO_REQUEST = 67;
    public static final int NOTIFICATION_REQUEST = 68;
    public static final int EMAIL_NOTIFICATION_REQUEST = 69;

    public static final int DELETE_APPOINTMENT_IMAGE_REQUEST = 70;
    public static final int DELETE_TASKS_IMAGE_REQUEST = 71;
    public static final int CITY_BY_PINCODE_REQUEST = 72;
    public static final int PARRENTING_BLOG_ALL_DATA = 73;
    public static final int BOOKMARK_BLOG_REQUEST = 74;
    public static final int BOOKMARK_RESOURCE_REQUEST = 75;
    public static final int SHARE_SPOUSE_BLOG = 76;
    /**
     * this type related to google map get direction
     */
    public static final int GET_GOOGLE_MAP_DIRECTIONS = 100;
    public static final int GET_GOOGLE_MAP_TRANSIT = 101;

    public static final int ARTICLES_TODAY_REQUEST = 102;
    public static final int BOOKMARKED_ARTICLE_LIST_REQUEST = 103;
    public static final int BOOKMARKED_RESOURCE_LIST_REQUEST = 104;

    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int PUSH_TOKEN_REQUEST = 1003;

    public static final int GET_ADDRESS_FROM_LATLONG_REQUEST = 1004;
    public static final int GET_ADDRESS_FROM_LATLONG_REQUEST_DEST = 1005;

    //New Text Editor and BLogs Changes
    public static final int GET_BLOGGER_DASHBOARD_REQUEST = 1013;
    public static final int GET_BLOGGER_PUBLISHED_ARTICLES_REQUEST = 1014;

    public static final int GET_MORE_COMMENTS = 1015;
    public static final int SEARCH_AUTHORS_REQUEST = 1016;

    public static final int DEEP_LINK_RESOLVER_REQUEST = 2001;
    public static final int ARTICLE_DRAFT_REQUEST = 3001;
    public static final int ARTICLE_DRAFT_LIST_REQUEST = 3002;
    public static final int BLOG_SETUP_REQUEST = 4001;
    public static final int ARTICLE_PUBLISH_REQUEST = 5001;
    public static final int BLOG_DATA_REQUEST = 6001;

    public static final String VALIDATED_USER = "1";

    public static final String PREF_ACCOUNT_NAME = "accountName";

    public static final String SLIDER_POSITION = "slider_position";

    /**
     * Images types to create separate folders on server
     */
    public static final String IMAGE_TYPE_USER_PROFILE = "userimages";
    public static final String IMAGE_TYPE_APPOINTMENT = "appointments";
    public static final String IMAGE_TYPE_TASK = "tasks";

    // key used in intents or bundles
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String IS_RECURRING = "is_recuuring";
    public static final String EXTRA_APPOINTMENT_ID = "appointment_id";
    public static final String EXTERNAL_APPOINTMENT_ID = "external_appointment_id";
    public static final String EXTRA_SHARE = "is_share";
    public static final String EXTRA_ID = "id";
    public static final String IS_APPOINTMENT = "isAppointment";
    public static final String SHARE_CONTENT = "share_content";
    public static final String SHARE_URL = "share_url";

    /**
     * Notification constants
     */
    public static final String NOTIFICATION_PREF_BOTH = "3";
    public static final String NOTIFICATION_PREF_PUSH = "2";
    public static final String NOTIFICATION_PREF_EMAIL = "1";
    public static final String NOTIFICATION_PREF_NONE = "0";

    /**
     * Deep Linking Constants
     */
    public static final String DEEP_LINK_URL = "deep_link_url";
    public static final String DEEP_LINK_BUSINESS_LISTING = "business_listing";
    public static final String DEEP_LINK_BUSINESS_DETAIL = "business_detail";
    public static final String DEEP_LINK_EVENT_LISTING = "events_listing";
    public static final String DEEP_LINK_EVENT_DETAIL = "event_detail";
    public static final String DEEP_LINK_AUTHOR_LISTING = "author_listing";
    public static final String DEEP_LINK_BLOGGER_LISTING = "blogger_listing";
    public static final String DEEP_LINK_ARTICLE_DETAIL = "article_detail";
    public static final String CONTAINER_ID = "GTM-MS864S";

    //Author Types
    public static final String AUTHOR_TYPE_BLOGGER = "Blogger";
    public static final String AUTHOR_TYPE_EDITOR = "Editor";
    public static final String AUTHOR_TYPE_EDITORIAL = "Editorial";
    public static final String AUTHOR_TYPE_EXPERT = "Expert";
    public static final String Source_Id = "" + 2;

    public static final String FROM_ACTIVITY = "fromActivity";
    public static final String ACTIVITY_LOGIN = "ActivityLogin";
    public static final String SPLASH_ACTIVITY = "SplashActivity";

    public static final String SORT_TYPE_BOOKMARK = "bookmark";
    public static final String SERVICE_TYPE_ARTICLE = "v1/articles/";
    public static final String SEPARATOR_BACKSLASH = "/";

    public static final String CATEGORIES_JSON_FILE = "categories.json";
}
