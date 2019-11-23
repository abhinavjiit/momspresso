package com.mycity4kids.constants;

import android.net.Uri;

import com.mycity4kids.BuildConfig;

/**
 * @author Hemant Parmar
 */
public class AppConstants {

    public static final String DEV_URL = "http://54.169.17.138/";
    public static final String STAGING_URL = "http://52.220.87.141/";

    //            public static final String LIVE_URL = "http://52.220.87.141/";
    public static final String LIVE_URL = "https://api.momspresso.com/";
    public static final String DEV_REWARDS_URL = "https://testingapi.momspresso.com/";

    public static final String PHOENIX_STAGING_URL = "http://52.77.116.39";

    public static final String GROUPS_LIVE_URL = BuildConfig.GROUPS_LIVE_URL;
    public static final String VIDEOS_URL = "http://35.200.233.99:3040/";
    public static final String READ_ARTICLES_URL = "35.200.142.199";
    public static final String BRANCH_MOMVLOGS = "momvlog_challenge";
    public static final String BRANCH_PERSONALINFO = "personal_info";
    public static final String BRANCH__CAMPAIGN_LISTING = "campaign_listing";
    public static final String BRANCH_CAMPAIGN_DETAIL = "campaign_detail";


    //Enable For Testing
    //public static final String LIVE_URL = SharedPrefUtils.getBaseURL(BaseApplication.getAppContext());

    public static final String BASE_URL = LIVE_URL;

    //public static final String STAGING_INTERNAL_SERVER_URL = "http://59.162.46.199:8585/mycity4kids/";
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

    public static final String GROUPS_TEST_STAGING_URL = "http://35.200.233.99:3030/";
    public static final String GROUPS_TEST_LIVE_URL = "https://groups.momspresso.com/";
    public static final String AZURE_LIVE_URL = "https://api.cognitive.microsoft.com/";

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

    public static final Uri APP_BASE_URI = Uri.parse("android-app://com.mycity4kids/https/www.momspresso.com/");
    public static final Uri WEB_BASE_URL = Uri.parse("https://www.momspresso.com/");

    public static final String GROUPS_BASE_SHARE_URL = "https://www.momspresso.com/";
    public static final String ARTICLE_SHARE_URL = "https://www.momspresso.com/" + "parenting/";
    public static final String ARTICLE_WEBVIEW_URL = "http://article.momspresso.com/parenting/";
    public static final String VIDEO_ARTICLE_SHARE_URL = "https://www.momspresso.com/" + "parenting/";
    public static final String VIDEO_UPLOAD_URL = LIVE_URL + "v1/media/videos/";
    public static final String BLOG_SHARE_BASE_URL = "https://www.momspresso.com";
//    public static final String ARTICLE_SHARE_URL = BASE_URL + "newparenting/";

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
    public static final int FOLLOW_REQUEST = 7001;

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
    public static final int REQUEST_VIDEO_TRIMMER = 1019;
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
    public static final String DEEP_LINK_AUTHOR_DETAIL = "author_detail";
    public static final String DEEP_LINK_ARTICLE_LISTING = "article_listing";
    public static final String DEEP_LINK_TOPIC_LISTING = "category_listing";
    public static final String DEEP_LINK_VLOG_DETAIL = "video";
    public static final String DEEP_LINK_STORY_DETAILS = "story_detail";
    public static final String APP_SETTINGS_DEEPLINK = "app_settings";
    public static final String CONTAINER_ID = "GTM-MS864S";

    //Author Types
    public static final String AUTHOR_TYPE_BLOGGER = "Blogger";
    public static final String AUTHOR_TYPE_EDITOR = "Editor";
    public static final String AUTHOR_TYPE_EDITORIAL = "Editorial";
    public static final String AUTHOR_TYPE_EXPERT = "Expert";
    public static final String AUTHOR_TYPE_FEATURED = "Featured Author";
    public static final String AUTHOR_TYPE_USER = "User";
    public static final String AUTHOR_TYPE_COLLABORATION = "VIDEO COLLABORATOR";
  /*  public static final String USER_TYPE_EDITOR = "0";
    public static final String USER_TYPE_EXPERT = "1";
    public static final String USER_TYPE_BLOGGER = "2";
    public static final String USER_TYPE_EDITORIAL = "3";*/

    public static final String Source_Id = "" + 2;

    public static final String FROM_ACTIVITY = "fromActivity";
    public static final String ACTIVITY_LOGIN = "ActivityLogin";
    public static final String ACTIVITY_TUTORIAL = "TutorialActivity";
    public static final String SPLASH_ACTIVITY = "SplashActivity";

    public static final String SORT_TYPE_BOOKMARK = "bookmark";
    public static final String SERVICE_TYPE_ARTICLE = "v1/articles/";
    public static final String SERVICE_TYPE__EDITORS_PICKS = "v1/articles/topics/";
    public static final String SERVICE_TYPE_FOR_YOU = "v1/recommend/";
    public static final String SERVICE_TYPE_USER = "v1/users/";
    public static final String SEPARATOR_BACKSLASH = "/";
    public static final String SEPARATOR_QMARK = "?";

    public static final String CATEGORIES_JSON_FILE = "categories.json";
    public static final String FOLLOW_UNFOLLOW_TOPICS_JSON_FILE = "follow_unfollow_topics.json";
    public static final String PHOENIX_RELEASE_VERSION = "5.0";
    public static final String VENUS_RELEASE_VERSION = "5.0";
    public static final String LOCALIZATION_RELEASE_VERSION = "8.0.0";
    public static final String GROUPS_COACHMARK_VERSION = "9.1.3";
    public static final String FACEBOOK_CONNECT_RELEASE_VERSION = "5.5";
    public static final String UPLOAD_VIDEO_RELEASE_VERSION = "5.6.2";

    public static final String PAGINATION_END_VALUE = "NA";
    public static final String FOLLOW_LIST_TYPE = "followListType";
    public static final String FOLLOWER_LIST = "follower";
    public static final String FOLLOWING_LIST = "following";
    public static final String COLLECTION_FOLLOWING_LIST = "collectionFollowers";

    public static final String PUBLIC_PROFILE_FLAG = "publicProfileFlag";
    public static final String PUBLIC_PROFILE_USER_ID = "publicProfileUserId";
    public static final String USER_ID_FOR_FOLLOWING_FOLLOWERS = "userIdFollowingFollowers";
    public static final String STACK_CLEAR_REQUIRED = "stackClearRequired";

    public static final String USER_TYPE_USER = "0";
    public static final String USER_TYPE_ADMIN = "1";
    public static final String USER_TYPE_BUSINESS = "2";
    public static final String USER_TYPE_CITY_ADMIN = "3";
    public static final String USER_TYPE_REPORT_MANAGER = "4";
    public static final String USER_TYPE_EDITOR = "5";
    public static final String USER_TYPE_EXPERT = "6";
    public static final String USER_TYPE_BLOGGER = "7";
    public static final String USER_TYPE_EDITORIAL = "8";
    public static final String USER_TYPE_FEATURED = "9";
    public static final String USER_TYPE_COLLABORATION = "14";
    public static final String COMMENT_OR_REPLY_OR_NESTED_REPLY = "editType";
    public static final int EDIT_COMMENT = 0;
    public static final int EDIT_REPLY = 1;
    public static final int EDIT_NESTED_REPLY = 2;
    public static final String MOMSPRESSO_CATEGORY_ID = "momspressoCategoryId";
    public static final String MOMSPRESSO_SLUG = "momspresso-video";

    public static final String HOME_VIDEOS_CATEGORYID = "category-d4379f58f7b24846adcefc82dc22a86b";
    public static final String MOMSPRESSO_CATEGORYID = "category-ae65da4bf8bb48e4b80f13444be1e9a5";
    public static final String HINDI_CATEGORYID = "category-1515d3c9adb249988a136bdbfc8f0017";
    public static final String BANGLA_CATEGORYID = "category-5ad20fb85f7c4a84b66b8d6993dbad9f";
    public static final String MARATHI_CATEGORYID = "category-bd1d0a1ab9c0436d97f78d90a115ee71";
    public static final String TAMIL_CATEGORYID = "category-093a16d571584b67a5de83f793581bbe";
    public static final String TELUGU_CATEGORYID = "category-6063f21fed494fdea6e9f57069d3e9db";
    public static final String KANNADA_CATEGORYID = "category-725eea1950f449d7ac9b2a76fa2dc061";
    public static final String MALAYALAM_CATEGORYID = "category-71a035cdfafb455ab231b40df7ed06b9";
    public static final String GUJRATI_CATEGORYID = "category-10295b47d84743a68f4870134a68017d";
    public static final String PUNJABI_CATEGORYID = "category-b2a14f747d8647a7ae4049e755e161fd";

    public static final String SPONSORED_CATEGORYID = "category-51a2ea215c634753ac6a9aa377deae0b";
    public static final String SHORT_STORY_CATEGORYID = "category-ce8bdcadbe0548a9982eec4e425a0851";
    public static final String DEEPLINK_EDITOR_URL = "mc4k://www.mycity4kids.com/editor";
    public static final String DEEPLINK_ADD_SHORT_STORY_URL = "mc4k://www.momspresso.com/addashortstory/";
    public static final String DEEPLINK_EDIT_SHORT_DRAFT_URL = "mc4k://www.momspresso.com/editshortdraft/";
    public static final String DEEPLINK_EDIT_SHORT_STORY_URL = "mc4k://www.momspresso.com/editashortstory/";
    public static final String DEEPLINK_MOMSPRESSO_EDITOR_URL = "mc4k://www.momspresso.com/editor/";
    public static final String DEEPLINK_PROFILE_URL = "mc4k://www.mycity4kids.com/profile";
    public static final String DEEPLINK_MOMSPRESSO_PROFILE_URL = "mc4k://www.momspresso.com/profile";
    public static final String DEEPLINK_SUGGESTED_TOPIC_URL = "mc4k://www.mycity4kids.com/suggested_topics";
    public static final String DEEPLINK_MOMSPRESSO_SUGGESTED_TOPIC_URL = "mc4k://www.momspresso.com/suggested_topics";
    public static final String DEEPLINK_ADD_FUNNY_VIDEO_URL = "mc4k://www.mycity4kids.com/addfunnyvideo";
    public static final String DEEPLINK_MOMSPRESSO_ADD_FUNNY_VIDEO_URL = "mc4k://www.momspresso.com/addfunnyvideo";
    public static final String DEEPLINK_MOMSPRESSO_CAMPAIGN = "https://www.momspresso.com/mymoney/";
    public static final String DEEPLINK_MOMSPRESSO_REWARD_MYMONEY = "https://www.momspresso.com/mymoney?";
    public static final String DEEPLINK_MOMSPRESSO_REWARD_PAGE = "https://www.momspresso.com/usersprofile/rewardsform";
    public static final String DEEPLINK_MOMSPRESSO_REFERRAL = "https://www.momspresso.com/usersprofile/rewardsform";
    public static final String DEEPLINK_UPCOMING_EVENTS = "/Events_el";
    public static final String DEEPLINK_SETUP_BLOG = "setupablog";
    public static final String DEEPLINK_GROUPS = "momspresso.com/groups/";
    public static final String BRANCH_DEEPLINK = "https://mycity4kids.app.link";//https://www.momspresso.com://
    public static final String BRANCH_DEEPLINK_URL = "https://www.momspresso.com://open?link_click_id=";


    public static final String IGNORE_TAG = "ignore";
    public static final String EDITOR_PICKS_CATEGORY_ID = "category-66b6883fd0434683b053f18aa4d52b59";
    public static final int MINIMUM_TOPICS_FOLLOW_REQUIREMENT = 5;

    public static final int EDITOR_PICKS_MIN_ARTICLES = 10;
    public static final int EDITOR_PICKS_ARTICLE_COUNT = 200;
    public static final String IS_ADD_MORE_TOPIC = "isAddMoreTopic";
    public static final int WORDS_PER_MINUTE = 240;
    public static final int MAX_ARTICLE_BODY_IMAGE_READ_TIME = 12;
    public static final int MIN_ARTICLE_BODY_IMAGE_READ_TIME = 3;
    public static final long MIN_PERCENT_FOR_TIMESPENT = 10L;
    public static final String ANALYTICS_INFO_TYPE = "analyticsInfoType";
    public static final String ANALYTICS_INFO_IMPROVE_PAGE_VIEWS = "infoImprovePageViews";
    public static final String ANALYTICS_INFO_RANK_CALCULATION = "infoRankCalculation";
    public static final String ANALYTICS_INFO_IMPROVE_RANK = "infoImproveRank";
    public static final String ANALYTICS_INFO_IMPROVE_SOCIAL_SHARE = "infoImproveSocialShare";
    public static final String ANALYTICS_INFO_INCREASE_FOLLOWERS = "infoIncreaseFollowers";
    public static final int ANALYTICS_TOP_BLOGGERS_COUNT = 5;
    public static final int CONTRIBUTOR_SORT_TYPE_RANK = 2;

    public static final String NOTIFICATION_TYPE_WEBVIEW = "webView";
    public static final String NOTIFICATION_TYPE_UPCOMING_EVENTS = "upcoming_event_list";
    public static final String NOTIFICATION_TYPE_PROFILE = "profile";
    public static final String NOTIFICATION_TYPE_APP_SETTINGS = "app_settings";
    public static final String NOTIFICATION_TYPE_ARTICLE_DETAILS = "article_details";
    public static final String NOTIFICATION_TYPE_VIDEO_DETAILS = "video_details";
    public static final String NOTIFICATION_TYPE_EDITOR = "editor";
    public static final String NOTIFICATION_TYPE_SUGGESTED_TOPICS = "suggested_topics";
    public static final String NOTIFICATION_TYPE_TODAYS_BEST = "todays_best";
    public static final String NOTIFICATION_TYPE_SHORT_STORY_LIST = "short_story_list";
    public static final String NOTIFICATION_TYPE_SHORT_STORY_DETAILS = "short_story_details";
    public static final String NOTIFICATION_TYPE_GROUP_DETAILS = "group_details";
    public static final String NOTIFICATION_TYPE_POST_DETAILS = "post_details";
    public static final String NOTIFICATION_TYPE_GROUP_LISTING = "group_listing";
    public static final String NOTIFICATION_TYPE_MOMSIGHT_REWARD_LISTING = "momsights_screen";
    public static final String NOTIFICATION_TYPE_CAMPAIGN_LISING = "campaign_listing";
    public static final String NOTIFICATION_TYPE_CAMPAIGN_DETAIL = "campaign_detail";
    public static final String NOTIFICATION_TYPE_CAMPAIGN_SUBMIT_PROOF = "campaign_submit_proof";
    public static final String NOTIFICATION_TYPE_CAMPAIGN_PANCARD = "mymoney_pancard";
    public static final String NOTIFICATION_TYPE_CAMPAIGN_CATEGORY_LISTING = "category_listing";
    public static final String NOTIFICATION_TYPE_CAMPAIGN_BANKDETAILS = "mymoney_bankdetails";
    public static final String NOTIFICATION_NOTIFY_TYPE_MOMSIGHT_REWARDS = "20";
    public static final String NOTIFICATION_NOTIFY_TYPE_TOPIC = "21";
    public static final String NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_LISTING = "22";
    public static final String NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_DETAIL = "23";
    public static final String NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_SUBMIT_PROOF = "24";
    public static final String NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_PANCARD = "25";
    public static final String NOTIFICATION_NOTIFY_TYPE_CAMPAIGN_BANKDETAIL = "26";



    public static final String FOLLOWING_STATUS_ACTIVITY_RESULT = "followingStatusActivityResult";
    public static final String VIDEO_STATUS_DRAFT = "0";
    public static final String VIDEO_STATUS_APPROVAL_PENDING = "1";
    public static final String VIDEO_STATUS_APPROVAL_CANCELLED = "2";
    public static final String VIDEO_STATUS_PUBLISHED = "3";
    public static final String VIDEO_STATUS_UNPUBLISHED = "4";

    public static final String VIDEO_TYPE_DRAFT = "DRAFT";
    public static final String VIDEO_TYPE_APPROVAL_PENDING = "PENDING FOR APPROVAL";
    public static final String VIDEO_TYPE_APPROVAL_CANCELLED = "APPROVAL CANCELLED";
    public static final String VIDEO_TYPE_PUBLISHED = "";
    public static final String VIDEO_TYPE_UNPUBLISHED = "UNPUBLISHED";

    public static final String NOTIFICATION_STATUS_READ = "1";
    public static final String NOTIFICATION_STATUS_UNREAD = "0";

    public static final int OTHERS_CITY_ID = 11;
    public static final String OTHERS_NEW_CITY_ID = "city-11";
    public static final String OTHERS_CITY_NAME = "Others";

    public static final int ALL_CITY_ID = 11;
    public static final String ALL_CITY_NEW_ID = "city-10";

    public static final String TOPIC_LEVEL_MAIN_CATEGORY = "1";
    public static final String TOPIC_LEVEL_SUB_CATEGORY = "2";
    public static final String TOPIC_LEVEL_SUB_SUB_CATEGORY = "3";
    public static final String LANGUAGES_JSON_FILE = "languagesJsonFile.json";

    public static final String LANG_KEY_ENGLISH = "0";
    public static final String LANG_KEY_HINDI = "1";
    public static final String AUTHOR_NAME = "authorName";

    public static final String LAUNCH_FRAGMENT = "launchFragment";
    public static final String FRAGMENT_SIGNIN = "signinFragment";
    public static final String FRAGMENT_SIGNUP = "signupFragment";

    public static final String SEARCH_ITEM_TYPE_ARTICLE_HEADER = "articleHeader";
    public static final String SEARCH_ITEM_TYPE_TOPIC_HEADER = "topicHeader";
    public static final String SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE = "articleShowMore";
    public static final String SEARCH_ITEM_TYPE_TOPIC_SHOW_MORE = "topicShowMore";
    public static final String SEARCH_ITEM_TYPE_ARTICLE = "article";
    public static final String SEARCH_ITEM_TYPE_TOPIC = "topic";
    public static final String SEARCH_ITEM_TYPE_VIDEO = "video";
    public static final int MAX_ALLOWED_CHILD_COUNT = 8;

    public static final String DEBUGGING_USER_ID = "6f57d7cb01fa46c89bf85e3d2ade7de3";

    public static final String FB_AD_PLACEMENT_ARTICLE_LISTING = "206155642763202_1712332575478827";
    public static final String FB_AD_PLACEMENT_USER_ARTICLE = "206155642763202_1712333168812101";
    public static final String FB_AD_PLACEMENT_ARTICLE_DETAILS = "206155642763202_1699568463421905";
    public static final String FB_AD_PLACEMENT_ARTICLE_DETAILS_TOP = "206155642763202_2172163799495700";
    public static final long MYCITY_TO_MOMSPRESSO_SWITCH_TIME = 1515936600;

    public static final String LOCALE_ENGLISH = "en";
    public static final String LOCALE_HINDI = "hi";
    public static final String LOCALE_MARATHI = "mr";
    public static final String LOCALE_BENGALI = "bn";
    public static final String LOCALE_TAMIL = "ta";
    public static final String LOCALE_TELUGU = "te";
    public static final String LOCALE_KANNADA = "kn";
    public static final String LOCALE_MALAYALAM = "ml";
    public static final String LOCAL_GUJARATI = "gu";
    public static final String LOCAL_PUNJABI = "pa";

    public static final String GROUP_SECTION_ABOUT = "ABOUT";
    public static final String GROUP_SECTION_DISCUSSION = "DISCUSSION";
    public static final String GROUP_SECTION_BLOGS = "BLOGS";
    public static final String GROUP_SECTION_PHOTOS = "PHOTOS";
    public static final String GROUP_SECTION_VIDEOS = "VIDEOS";
    public static final String GROUP_SECTION_TOP_POSTS = "TOP POSTS";
    public static final String GROUP_SECTION_POLLS = "POLLS";
    public static final String GROUP_SECTION_ASK_AN_EXPERT = "ASK AN EXPERT";

    public static final String POST_TYPE_TEXT = "textPost";
    public static final String POST_TYPE_MEDIA = "mediaPost";
    public static final String POST_TYPE_TEXT_POLL = "textPoll";
    public static final String POST_TYPE_IMAGE_POLL = "imagePoll";
    public static final String POST_TYPE_AUDIO = "audioPost";
    public static final String POST_TYPE_ASK_AN_EXPERT = "askAnExpert";

    public static final String POST_TYPE_TEXT_KEY = "0";
    public static final String POST_TYPE_MEDIA_KEY = "1";
    public static final String POST_TYPE_POLL_KEY = "2";
    public static final String ASK_AN_EXPERT_KEY = "4";

    public static final String GROUP_TYPE_OPEN_KEY = "0";
    public static final String GROUP_TYPE_CLOSED_KEY = "1";
    public static final String GROUP_TYPE_INVITE_ONLY_KEY = "2";

    public static final String GROUP_ACTION_TYPE_HELPFUL_KEY = "1";
    public static final String GROUP_ACTION_TYPE_UNHELPFUL_KEY = "0";
    public static final String GROUP_ACTION_TYPE_SHARE_KEY = "2";
    public static final String GROUP_ACTION_TYPE_VOTE_KEY = "3";
    public static final String GROUP_ACTION_TYPE_TAG_KEY = "4";

    public static final String CONTENT_TYPE_VIDEO = "2";
    public static final String CONTENT_TYPE_SHORT_STORY = "1";
    public static final String CONTENT_TYPE_ARTICLE = "0";
    public static final String CONTENT_TYPE_CREATE_SECTION = "-1";

    public static final int REPORT_TYPE_STORY = 1;
    public static final int REPORT_TYPE_COMMENT = 2;
    public static final String GROUP_REPORT_TYPE_POST = "0";
    public static final String GROUP_REPORT_TYPE_COMMENT = "1";

    public static final String CONTENT_REPORT_ACTION_NONE = "0";
    public static final String CONTENT_REPORT_ACTION_HIDE_CONTENT = "1";
    public static final String CONTENT_REPORT_ACTION_BLOCK_USER = "2";

    public static final String GROUP_MEMBER_TYPE = "memberType";
    public static final String GROUP_MEMBER_TYPE_ADMIN = "2";
    public static final String GROUP_MEMBER_TYPE_MODERATOR = "1";
    public static final String GROUP_MEMBER_TYPE_USER = "0";

    public static final String GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION = "0";
    public static final String GROUP_MEMBERSHIP_STATUS_MEMBER = "1";
    public static final String GROUP_MEMBERSHIP_STATUS_LEFT = "2";
    public static final String GROUP_MEMBERSHIP_STATUS_BLOCKED = "3";
    public static final String GROUP_MEMBERSHIP_STATUS_REJECTED = "4";

    public static final String MIX_PANEL_TOKEN = "76ebc952badcc143b417b3a4cf89cadd";
    public static final long HOURS_24_TIMESTAMP = 86400000;
    public static final long DAYS_10_TIMESTAMP = 1209600000l;

    public static final String ANDROID_NEW_EDITOR = "android_new_editor";
    public static final String ANDROID_OLD_EDITOR = "android_old_editor";
    public static final int COMMENT_TYPE_AUDIO = 2;

    public static final String SHORT_STORY_CHALLENGE_ID = "category-743892a865774baf9c20cbcc5c01d35f";

    public static final String VIDEO_CHALLENGE_ID = "category-ee7ea82543bd4bc0a8dad288561f2beb";
    public static final String VICHAAR_SAGAR_CATEGORY_ID = "category-8dcc26eb81de4042b225f82ec8e88cd3";
    public static final String PUBLIC_VISIBILITY = "1";
    public static final String PUBLIC_INVISIBILITY = "0";
    public static final String MEDIUM_WHATSAPP = "whatsapp";
    public static final String MEDIUM_FACEBOOK = "facebook";
    public static final String MEDIUM_INSTAGRAM = "instagram";
    public static final String MEDIUM_TWITTER = "twitter";
    public static final String ARTICLE_CHALLENGES_ID = "category-ee7ea82543bd4bc0a8dad288561f2beb";
    public static final String ARTICLE_LIST_TYPE = "articleListType";
    public static final String VIDEO_UPLOAD_NOT_STARTED = "video_upload_not_started";
    public static final String VIDEO_UPLOAD_IN_PROGRESS = "video_upload_in_progress";
    public static final String VIDEO_UPLOAD_FAILED = "video_upload_failed";
    public static final String VIDEO_UPLOAD_SUCCESS = "video_upload_success";
    public static final String STATUS_FOLLOWING = "following";
    public static final String STATUS_NOT_FOLLOWING = "not_following";
    public static final String PLACES_API_KEY = "AIzaSyATjKBk6YHXTxF0nuN6zCumLfq9JY4aBKU";

    public static final String VIDEO_COLLECTION_TYPE = "2";
    public static final String SHORT_STORY_COLLECTION_TYPE = "1";
    public static final String ARTICLE_COLLECTION_TYPE = "0";

    public static final String FOLLOWING = "1";
    public static final String FOLLOW = "0";

    enum SocialPlatformName {

    }
}