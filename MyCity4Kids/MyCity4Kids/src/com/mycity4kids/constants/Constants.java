package com.mycity4kids.constants;

/**
 * @author deepanker.Chaudhary
 */
public class Constants {

    /**
     * Values to used in intents or bundles
     */
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String BUSINESS_ID = "business_id";
    public static final String CATEGOTY_NAME = "categoryName";
    public static final String BUSINESS_OR_EVENT_ID = "business_or_event_id";
    public static final String EXTRA_BUSINESS_DATA_MODEL = "extra_business_data_model";
    public static final String EXTRA_TOP_PICKS_ID = "extra_top_picks_id";
    public static final String EXTRA_TOP_PICKS_DATA_MODEL = "extra_top_picks_data_model";
    public static final String ARTICLES_DETAILS_DATA = "article_details_data";
    public static final String ARTICLE_ID = "article_id";
    public static final String PARENT_ID = "parent_id";
    public static final String ARTICLE_BLOG_CONTENT = "articleContentId";
    public static final String PARENTING_TYPE = "parenting_type";
    public static final String PARENTING_SEARCH_LIST_TYPE = "searchListType";
    public static final String PARENTING_SEARCH_QUERY = "parentingSearchQuery";
    public static final String IS_PARENTING_COMMING_FROM_SEARCH = "parenting_type";
    public static final String PARENTING_FILER_DATA = "parentingFilterData";
    public static final String IS_COMMENT = "isComment";
    public static final String IS_FIRST_RUN = "if_first_run";

    public static final String IS_COMMING_FROM_LISTING = "is_comming_from_listing";
    public static final String FILTER_TYPE = "filter_type";
    public static final String BLOG_NAME = "blog_name";


    public static final String ARTICLE_NAME = "article_name";

    public static final String SIGNUP_DATA = "signup_data";
    public static final String SIGNUP_FLAG = "signup_flag";


    public static final String URL = "newsletter_url";

    public static final String LOCAL_BROADCAST_GCM = "gcm_broadcast";

    public static final String PUSH_MODEL = "pushmodel";

    public static boolean IS_RESET = false;


    public static final String EVENT_SYNC_TIMESTAMP = "eventSyncTimestamp";


    /**
     * Response code constants for http request
     */
    public static final int HTTP_RESPONSE_SUCCESS = 200;

    /**
     * Sort by categories to be used in business listing api
     */
    public static final String SORT_BY_RATING = "rating";
    public static final String SORT_BY_FAVOURITES = "favourites";
    public static final String SORT_BY_REVIEW = "review";
    public static final String SORT_BY_VIDEO_REVIEW = "video_review";
    public static final String SORT_BY_BOOK_ONLINE = "book_online";


    /**
     * I am using same listview with two different adapter
     * so i tag this string with listview & check onItemClickListner;- HomeCategoryActivity
     */
    public static final String SEARCH_LIST_TAG = "searchListAdapter";
    public static final String LOCALITY_LIST_TAG = "localityListAdapter";
    public static final String ARTICLE_COVER_IMAGE = "article_cover_image";
    public static final String IS_COMMING_FROM_SETTING = "is_comming_from_setting";
    public static final String LOAD_EXTERNAL_EVENTS = "Please wait while loading events...";
    public static final String WEB_VIEW_URL = "web_view_url";

    public static boolean IS_PAGE_AVAILABLE = true;
    /**
     * sending a type in gallery adapter :-
     */
    public static final String FIRST_GALLERY = "firstGridGallery";
    public static final String SECOND_GALLERY = "secondGridGallery";

    public static final String ALBUM_TYPE = "albumType";
    public static final String GALLERY_TYPE = "galleryType";


    public static final int BUSINESS_PAGE_TYPE = 0;
    public static final int EVENT_PAGE_TYPE = 1;
    public static final String PAGE_TYPE = "pageEventOrBusiness";
    public static final String DISTANCE = "distance";

    public static final int OPEN_GALLERY = 0;
    public static final int TAKE_PICTURE = 1;
    public static final int OPEN_DOCUMENTS = 2;
    public static final int CREATE_TASK = 3;
    public static final int FILTER_ARTICLE = 4;
    public static final int FILTER_BLOG = 5;
    public static final int BLOG_FOLLOW_STATUS = 6;


    public final static String CATEGORY_KEY = "category";

    /**
     * this flag i am using for search listing in BusinessListActivity. Because there
     * are there cases. 1.BusinessListing2.EventListing3.Search Listing. SO For recognize that activity coming
     * from search or not i use this flag.
     */
    public static boolean IS_SEARCH_LISTING = false;
    public static boolean IS_COMING_FROM_SLIDER = false;
    public static final String IS_SLIDER = "isSlider";

    public static final String WEB_VIEW_ECOMMERECE = "webViewEcommerceUrl";

    /**
     * request codes for startActivityForResult
     */
    public static final int SHOW_TOP_PICKS_DETAIL = 0;

    /**
     * Basically these value we will use from add a photo,write a review,add to favorite.
     */
    public static boolean IS_COMING_FROM_INSIDE = false;
    public final static String LOGIN_REQUIRED = "loginRequired";

    // Twitter
    // public static final String TWITTER_OAUTH_KEY = "Mq6qXldDBORnU9k3UlMmHo74X";
    //    public static final String TWITTER_OAUTH_SECRET = "SmPoqcZiDbk8rJjPoCOYejqqr9AUHwf2Zf2nnV0jT6vVJzQFO7";

    public static final String TWITTER_OAUTH_KEY = "pRB1ECM3BwSTY4eirEYaeUHnE";
    public static final String TWITTER_OAUTH_SECRET = "1BiFlZA4Md4RhtfJvMpPrDINfcIN61wLZ5w1sDTrzSQfe683HM";
    public static final String CALLBACK_URL = "https://api.twitter.com/oauth/callback";


    /**
     * this is flag for disable & enable for google analytics.
     */

    public static boolean IS_GOOGLE_ANALYTICS_ENABLED = true;

    public static final String USER_NAME = "username";
    public static final String USER_ID = "userid";
    public static final String USER_EMAIL = "useremail";
    public static final String ACCESS_TOKEN = "accesstoken";
    public static final String MODE = "mode";
    public static final String PROFILE_IMAGE = "profileimage";
    public static final String TEMP_PHOTO_FILE_NAME = ".temp_photo.jpg";


    public static final String ENTER_DETAIL = "Please fill all values";

    public static final String ENTER_SPOUSENAME = "Please enter name";
    public static final String ENTER_CONFIRM_PSWD = "Please enter confirm password";
    public static final String ENTER_EMAIL = "Please enter email";
    public static final String ENTER_FAMILY_PSWD = "Please enter family password";
    public static final String ENTER_PINCODE = "Please enter pincode";
    public static final String ENTER_KIDNAME = "Please enter name";
    public static final String ENTER_KIDBDY = "Please enter birthday";


    public static final String PASSWORD_MISMATCH = "Password should be same";
    public static final String PASSWORD_LENGTH = "Password must be of 5 characters";
    public static final String VALID_EMAIL = "Please enter valid email id";
    public static final String VALID_APPOINTMENT_NAME = "Please enter appointment name";
    public static final String VALID_ATTENDEE_WHO = "Please select attendees";
    public static final String VALID_ATTENDEE_WHO_N = "Please select atleast one person";
    public static final String VALID_USERS_WHO = "Please select users";
    public static final String DUPLICATE_EMAIL = "Please remove duplicate emailids";

    public static final String ENTER_NAME = "Please enter name";
    public static final String VALID_DATE = "You cannot create appointment for passed-off date & time";
    public static final String TASK_VALID_DATE = "You cannot create task for passed-off date & time";

    public static final String FROM_EVENTS = "FromEvents";


    /**
     * Constnats to be used in bundles or intents
     */
    public static final String EXTRA_ALARM_TYPE = "extra_alarm_type";
    public static final String EXTRA_ALARM_DESC = "extra_alarm_desc";
    public static final String EXTRA_ALARM_REMIND_BEFORE = "extra_alarm_remind_before";
    public static final String EXTRA_ALARM_RECURRING = "extra_alarm_recurring";
    public static final String EXTRA_ALARM_START_MILLIS = "extra_alarm_start_millis";
    public static String EXTRA_ALARM_REPEAT = "extra_alarm_repeat";
    public static String EXTRA_ALARM_REPEAT_FREQ = "extra_alarm_repeat_freq";
    public static String EXTRA_ALARM_REPEAT_NUM = "extra_alarm_repeat_num";
    public static String EXTRA_ALARM_REPEAT_UNTILL = "extra_alarm_repeat_untill";
    public static String EXTRA_ALARM_ID = "extra_alarm_id";

    /**
     * Reminder type constants
     */
    public static int REMINDER_TYPE_APPOINTMENT = 1;
    public static int REMINDER_TYPE_TASKS = 2;

    public static final int CROP_IMAGE = 20;

    public static String ARTICLES_LIST = "acticle_data";
    public static String BLOG_ARTICLES_LIST = "blog_acticle_data";
    public static String TAB_POSITION = "tab_position";
    public static String SORT_KEY = "article_sort_key";
    public static String KEY_RECENT = "recent";
    public static String KEY_POPULAR = "popular";
    public static String KEY_TRENDING = "trending";
    public static String SORT_TYPE = "sorttype";
    public static String BLOG_TITLE = "blog_title";
    public static String IS_SEARCH_ACTIVE = "is_Search_Active";
    public static String SEARCH_TOPIC = "search_topic";

    public static String TOTAL_ARTICAL_COUNT = "total_article_count";
    public static String FILTER_NAME = "filter_name";

    public static String RESET_FILTER = "reset_filter";


    public static String IS_FIRST_INTERNAL_SEARCH = "isfirstinternalSearch";

    public static boolean IS_REQUEST_RUNNING = false;

    public static String IS_MEET_CONTRIBUTORS_SELECTED = "is_contributors_selected";
    public static String FILTER_BLOG_SORT_TYPE = "filter_blog_sort";
    public static String BLOG_DETAILS = "blog_details";
    public static String BLOG_LIST_POSITION = "position";

    public static String BLOG_STATUS = "blogstatus";


    public static String BLOG_ISFOLLOWING = "blog_isfollowing";

    public static String EVENT_NAME = "event_name";
    public static String EVENT_DES = "event_decsription";
    public static String EVENT_LOCATION = "event_location";
    public static String EVENT_START_DATE = "event_startdate";
    public static String EVENT_END_DATE = "event_enddate";

    public static String REMAINDER_TYPE = "remainder_email_type";
    public static String REMAINDER_ID = "remainder_email_id";

    public static String IS_FROM_DEEPLINK = "is_from_deeplink";
    public static String CITY_ID_DEEPLINK = "city_id_deeplink";
    public static String DEEPLINK_URL = "deeplink_url";
//	public static int BACK_PRESS_CONST = 0 ;

    /*Fragments Mapping Constants*/
    public static String LOAD_FRAGMENT = "load_fragment";
    public static String BUSINESS_EVENTLIST_FRAGMENT = "fragment_eventlist";
    public static String TODOLIST_FRAGMENT = "fragment_todo";
    public static String CALENDARLIST_FRAGMENT = "fragment_calendar";
}
