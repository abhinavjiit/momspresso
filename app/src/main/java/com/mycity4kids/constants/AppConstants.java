package com.mycity4kids.constants;

import android.net.Uri;

import com.mycity4kids.BuildConfig;

/**
 * @author Hemant Parmar
 */
public class AppConstants {

    public static final String DEV_URL = "http://54.169.17.138/";
    public static final String STAGING_URL = "http://52.220.87.141/";

    public static final String LIVE_URL = "https://api.momspresso.com/";
    public static final String DEV_REWARDS_URL = "https://testingapi.momspresso.com/";

    public static final String BRANCH_MOMVLOGS = "momvlog_challenge";
    public static final String BRANCH_PERSONALINFO = "personal_info";
    public static final String BRANCH__CAMPAIGN_LISTING = "campaign_listing";
    public static final String BRANCH_CAMPAIGN_DETAIL = "campaign_detail";

    public static final String BASE_URL = LIVE_URL;

    public static final String STAGING_INTERNAL_SERVER_URL = LIVE_URL;
    public static final String STAGING_CLIENT_SERVER_URL = LIVE_URL;

    public static final String STAGING_USERS_KEY = "apiusers";

    public static final String GROUPS_TEST_LIVE_URL = "https://groups.momspresso.com/";
    public static final String AZURE_LIVE_URL = "https://api.cognitive.microsoft.com/";

    public static final String GROUPS_BASE_SHARE_URL = "https://www.momspresso.com/";
    public static final String ARTICLE_SHARE_URL = "https://www.momspresso.com/" + "parenting/";
    public static final String ARTICLE_WEBVIEW_URL = "http://article.momspresso.com/parenting/";
    public static final String VIDEO_ARTICLE_SHARE_URL = "https://www.momspresso.com/" + "parenting/";
    public static final String BLOG_SHARE_BASE_URL = "https://www.momspresso.com";
    public static final String USER_PROFILE_SHARE_BASE_URL = "https://www.momspresso.com/parenting/user/";

    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_VIDEO_TRIMMER = 1019;
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    public static final String VALIDATED_USER = "1";

    public static final String SLIDER_POSITION = "slider_position";
    public static final String NOTIFICATION_ID = "notification_id";

    /**
     * Deep Linking Constants
     */
    public static final String DEEP_LINK_URL = "deep_link_url";
    public static final String DEEP_LINK_AUTHOR_LISTING = "author_listing";
    public static final String DEEP_LINK_BLOGGER_LISTING = "blogger_listing";
    public static final String DEEP_LINK_ARTICLE_DETAIL = "article_detail";
    public static final String DEEP_LINK_AUTHOR_DETAIL = "author_detail";
    public static final String DEEP_LINK_ARTICLE_LISTING = "article_listing";
    public static final String DEEP_LINK_TOPIC_LISTING = "category_listing";
    public static final String DEEP_LINK_VLOG_DETAIL = "video";
    public static final String DEEP_LINK_STORY_DETAILS = "story_detail";
    public static final String APP_SETTINGS_DEEPLINK = "app_settings";

    //Author Types
    public static final String AUTHOR_TYPE_BLOGGER = "Blogger";
    public static final String AUTHOR_TYPE_EDITOR = "Editor";
    public static final String AUTHOR_TYPE_EDITORIAL = "Editorial";
    public static final String AUTHOR_TYPE_EXPERT = "Expert";
    public static final String AUTHOR_TYPE_FEATURED = "Featured Author";
    public static final String AUTHOR_TYPE_USER = "User";
    public static final String AUTHOR_TYPE_COLLABORATION = "VIDEO COLLABORATOR";

    public static final String FROM_ACTIVITY = "fromActivity";
    public static final String ACTIVITY_LOGIN = "ActivityLogin";
    public static final String ACTIVITY_TUTORIAL = "TutorialActivity";

    public static final String SORT_TYPE_BOOKMARK = "bookmark";
    public static final String SERVICE_TYPE_ARTICLE = "v1/articles/";
    public static final String SEPARATOR_BACKSLASH = "/";
    public static final String SEPARATOR_QMARK = "?";

    public static final String CATEGORIES_JSON_FILE = "categories.json";
    public static final String FOLLOW_UNFOLLOW_TOPICS_JSON_FILE = "follow_unfollow_topics.json";

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
    public static final String DEEPLINK_SELF_PROFILE_URL_1 = ".momspresso.com/parenting/admin";
    public static final String DEEPLINK_SELF_PROFILE_URL_2 = ".momspresso.com/parenting/admin/";
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

    public static final String NOTIFICATION_CENTER_APP_SETTINGS = "-1";
    public static final String NOTIFICATION_CENTER_WEB_VIEW = "0";
    public static final String NOTIFICATION_CENTER_ARTICLE_DETAIL = "1";
    public static final String NOTIFICATION_CENTER_PROFILE = "2";
    public static final String NOTIFICATION_CENTER_UPCOMING_EVENTS = "3"; //Deprecated
    public static final String NOTIFICATION_CENTER_VIDEO_DETAIL = "4";
    public static final String NOTIFICATION_CENTER_LAUNCH_EDITOR = "5";
    public static final String NOTIFICATION_CENTER_SUGGESTED_TOPICS = "6";
    public static final String NOTIFICATION_CENTER_TODAYS_BEST = "7";
    public static final String NOTIFICATION_CENTER_SHORT_STORY_LIST = "8";
    public static final String NOTIFICATION_CENTER_SHORT_STORY_DETAILS = "9";
    public static final String NOTIFICATION_CENTER_GROUP_MEMBERSHIP = "10";
    public static final String NOTIFICATION_CENTER_GROUP_NEW_POST = "11";
    public static final String NOTIFICATION_CENTER_GROUP_NEW_RESPONSE = "12";
    public static final String NOTIFICATION_CENTER_GROUP_NEW_REPLY = "13";
    public static final String NOTIFICATION_CENTER_GROUP_ADMIN_MEMBERSHIP = "14";
    public static final String NOTIFICATION_CENTER_GROUP_ADMIN_REPORTED = "15";
    public static final String NOTIFICATION_CENTER_GROUP_ADMIN_EDIT_GROUP = "16";
    public static final String NOTIFICATION_CENTER_GROUP_ADMIN = "17";
    public static final String NOTIFICATION_CENTER_GROUP_LISTING = "18";
    public static final String NOTIFICATION_CENTER_CREATE_SECTION = "19";
    public static final String NOTIFICATION_CENTER_MY_MONEY_PERSONAL_INFO = "20";
    public static final String NOTIFICATION_CENTER_TOPICS_ARTICLE_LISTING = "21";
    public static final String NOTIFICATION_CENTER_CAMPAIGN_LISTING = "22";
    public static final String NOTIFICATION_CENTER_CAMPAIGN_DETAIL = "23";
    public static final String NOTIFICATION_CENTER_CAMPAIGN_SUBMIT_PROOF = "24";
    public static final String NOTIFICATION_CENTER_CAMPAIGN_PANCARD = "25";
    public static final String NOTIFICATION_CENTER_CAMPAIGN_BANKDETAIL = "26";
    public static final String NOTIFICATION_CENTER_VIDEO_CHALLENGE_DETAIL = "27";
    public static final String NOTIFICATION_CENTER_COLLECTION_DETAIL = "28";
    public static final String NOTIFICATION_CENTER_BADGE_LISTING = "29";
    public static final String NOTIFICATION_CENTER_STORY_PUBLISH_SUCCESS = "30";

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
    public static final String LOCALE_GUJARATI = "gu";
    public static final String LOCALE_PUNJABI = "pa";

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

    public static final String CONTENT_TYPE_MYMONEY = "4";
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
    public static final String MEDIUM_GENERIC = "generic";
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
    public static final String COLLECTION_EDIT_TYPE = "updateCollection";

    public static final String FOLLOWING = "1";
    public static final String FOLLOW = "0";
    public static final String CONTENT_ID = "contentId";

    public static final String OTP_REGEX = "(?<!\\d)\\d{6}(?!\\d)";
    public static final String COLLECTION_LIST_REGEX = ".+?(user\\/)[a-zA-Z0-9]+\\/(collections)$";
    public static final String COLLECTION_DETAIL_REGEX = ".+?(user\\/)[a-zA-Z0-9]+(\\/(collections\\/))[a-zA-Z0-9]+$";
    public static final String BADGES_LISTING_REGEX = ".+?(user\\/)[a-zA-Z0-9]+\\/(badges)$";
    public static final String BADGES_DETAIL_REGEX = ".+?(user\\/)[a-zA-Z0-9]+(\\/(badges\\/))[a-zA-Z0-9]+$";
    public static final String MILESTONE_DETAIL_REGEX = ".+?(user\\/)[a-zA-Z0-9]+(\\/(milestones\\/))[a-zA-Z0-9]+$";
    public static final String USER_PROFILE_REGEX = ".+?(user\\/)[a-zA-Z0-9]+$";
    public static final String USER_ANALYTICS_REGEX = ".+?(user\\/)[a-zA-Z0-9]+\\/(rank)$";
    public static final String BADGE_ID = "badgeId";
    public static final String MILESTONE_ID = "milestoneId";
    public static final String COLLECTION_ID = "collectionId";
    public static final String STORY_SHARE_IMAGE_NAME = "storyShareImage";

    public static final String STORY_CATEGORY_QUOTES = "category-8dcc26eb81de4042b225f82ec8e88cd3";
    public static final String STORY_CATEGORY_ROMANCE = "category-7b52fede7bd349e79bd26b24845287d8";
    public static final String STORY_CATEGORY_COMEDY = "category-6d2efd6eeb9e414bb62cab0a72f35b78";
    public static final String STORY_CATEGORY_THRILLER = "category-34df3cd58d4849818eda2f43f8527819";
    public static final String STORY_CATEGORY_INSPIRATIONAL = "category-f1a5dcea3d884bd8b75e0da8fb1763d3";
    public static final String STORY_CATEGORY_DARK = "category-c9fa6d31a7c44699a8df5814030399a2";

    enum SocialPlatformName {

    }
}